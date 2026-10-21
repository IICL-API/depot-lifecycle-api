package depotlifecycle.controllers.api.inventory;

import depotlifecycle.ErrorResponse;
import depotlifecycle.domain.Party;
import depotlifecycle.domain.inventory.InventorySnapshot;
import depotlifecycle.domain.inventory.InventoryStatus;
import depotlifecycle.domain.inventory.InventoryUnit;
import depotlifecycle.repositories.PartyRepository;
import depotlifecycle.repositories.inventory.InventoryUnitRepository;
import depotlifecycle.system.ApiErrorHandling;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.security.annotation.Secured;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Tag(name = "inventory")
@Validated
@Secured("isAuthenticated()")
@Controller("/api/v2/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryController.class);
    private final PartyRepository partyRepository;
    private final InventoryUnitRepository inventoryUnitRepository;

    @Get(uri = "/{depot}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "fetch current inventory for a depot",
        description = "*This API is only proposed at this time and is in an alpha state - it is not production approved and may change without notice.*\n\nLists the shipping containers currently on site at the given depot for the calling party, with the responding system's current lifecycle status for each.  This is a reporting only snapshot intended for near real time inventory status reconciliation; it does not replace gate records.  Either the depot or the owner may implement this API - the response reports that system's view of the depot's inventory and the caller compares it against its own.",
        method = "GET",
        operationId = "indexInventory",
        extensions = @Extension(properties = { @ExtensionProperty(name = "iicl-purpose", value = "reporting", parseValue = true) })
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successfully fetched the depot inventory", content = {@Content(schema = @Schema(implementation = InventorySnapshot.class))}),
        @ApiResponse(responseCode = "400", description = "an error occurred", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "fetching depot inventory is disallowed by security"),
        @ApiResponse(responseCode = "404", description = "the depot could not be found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse<InventorySnapshot> index(@Parameter(name = "depot", description = "the identifier of the depot", in = ParameterIn.PATH, required = true, schema = @Schema(type = "string", pattern = "^[A-Z0-9]{9}$", example = "DEHAMCMRA", maxLength = 9)) String depot,
                                                 @Nullable @QueryValue("unitNumber") @Parameter(name = "unitNumber", description = "the unit number of the shipping container to filter to", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", example = "CONU1234561", pattern = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", maxLength = 11, required = false, nullable = true)) String unitNumber,
                                                 @Nullable @QueryValue("status") @Parameter(name = "status", description = "the inventory status to filter to", in = ParameterIn.QUERY, required = false, schema = @Schema(implementation = InventoryStatus.class, required = false, nullable = true)) InventoryStatus status,
                                                 @Nullable @QueryValue("statusChangedAfter") @Parameter(name = "statusChangedAfter", description = "only include shipping containers whose status changed after this date and time; used for cheap delta pulls between full snapshots\n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", format = "date-time", required = false, nullable = true)) ZonedDateTime statusChangedAfter) {
        LOG.info("Received Inventory Fetch for {}", depot);

        Optional<Party> depotParty = partyRepository.findByCompanyId(depot);
        if (depotParty.isEmpty()) {
            return HttpResponse.notFound();
        }

        List<InventoryUnit> units = inventoryUnitRepository.findByDepot(depotParty.get()).stream()
            .filter(unit -> unitNumber == null || Objects.equals(unit.getUnitNumber(), unitNumber))
            .filter(unit -> status == null || unit.getStatus() == status)
            .filter(unit -> statusChangedAfter == null || (unit.getStatusDateTime() != null && unit.getStatusDateTime().isAfter(statusChangedAfter)))
            .toList();

        InventorySnapshot snapshot = new InventorySnapshot();
        //For the purposes of this example, this server reports the depot's view of its own inventory
        snapshot.setReportedBy(depotParty.get());
        snapshot.setDepot(depotParty.get());
        snapshot.setSnapshotDateTime(ZonedDateTime.now());
        snapshot.setUnits(units);

        LOG.info("Responding with {} inventory unit(s)", units.size());
        return HttpResponse.ok(snapshot);
    }

    @Error(status = HttpStatus.NOT_FOUND)
    public HttpResponse<JsonError> notFound(HttpRequest request) {
        LOG.info("\tError - 404 - Not Found");
        return ApiErrorHandling.notFound(request);
    }

    @Error
    public HttpResponse<ErrorResponse> onSavedFailed(HttpRequest request, Throwable ex) {
        LOG.info("\tError - 400 - Bad Request", ex);
        return ApiErrorHandling.onSavedFailed(request, ex);
    }
}
