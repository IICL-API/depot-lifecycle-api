package depotlifecycle.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import depotlifecycle.ErrorResponse;
import depotlifecycle.domain.RepairComplete;
import depotlifecycle.domain.WorkOrder;
import depotlifecycle.domain.WorkOrderUnit;
import depotlifecycle.repositories.WorkOrderRepository;
import depotlifecycle.repositories.WorkOrderUnitRepository;
import depotlifecycle.services.AuthenticationProviderUserPassword;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.utils.SecurityService;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Tag(name = "workOrder")
@Validated
@Secured("isAuthenticated()")
@Controller("/api/v2/workOrderUnit")
@RequiredArgsConstructor
public class WorkOrderUnitController {
    private static final Logger LOG = LoggerFactory.getLogger(WorkOrderController.class);
    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderUnitRepository workOrderUnitRepository;
    private final ConversionService<?> conversionService;
    private final SecurityService securityService;

    @Put(uri = "/{workOrderNumber}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "marks a shipping container repaired",
        description = "For the given work order, attempt to mark it repair complete.",
        method = "PUT",
        operationId = "updateWorkOrderUnit",
        extensions = @Extension(properties = { @ExtensionProperty(name = "iicl-purpose", value = "activity", parseValue = true) })
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successfully repair completed the workOrder"),
        @ApiResponse(responseCode = "400", description = "an error occurred trying to repair complete the work order", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "repair completion is not allowed by security"),
        @ApiResponse(responseCode = "404", description = "shipping container, depot, or work order could not be found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse<HttpStatus> update(@Parameter(name = "workOrderNumber", description = "the work order number", in = ParameterIn.PATH, required = true, schema = @Schema(type = "string", example = "WHAMG30001", maxLength = 16)) String workOrderNumber,
                                           @Body @RequestBody(description = "Necessary information to mark a shipping container repair complete", required = true, content = {@Content(schema = @Schema(implementation = RepairComplete.class))}) RepairComplete repairComplete) {
        LOG.info("Received Work Order Repair Complete for {}:", workOrderNumber);
        conversionService.convert(repairComplete, JsonNode.class).ifPresent(jsonNode -> LOG.info(jsonNode.toString()));

        Optional<WorkOrder> workOrder = workOrderRepository.findByWorkOrderNumber(workOrderNumber);

        Optional<WorkOrderUnit> unit = workOrder.flatMap(order -> order.getLineItems().stream().filter(p -> p.getUnitNumber().equals(repairComplete.getUnitNumber())).findAny());

        if (securityService.username().equals(AuthenticationProviderUserPassword.VALIDATE_USER_NAME)) {
            if(workOrder.isEmpty()) {
                throw new IllegalArgumentException("Work Order " + workOrderNumber + " was not found.");
            }

            if(unit.isEmpty()) {
                throw new IllegalArgumentException("Work Order " + workOrderNumber + " does not contain unit " + repairComplete.getUnitNumber());
            }

            if(unit.get().getStatus().equals("REMOVED")) {
                throw new IllegalArgumentException("Unit was removed from work order.");
            }

            if (unit.get().getStatus().equals("REPAIRED")) {
                throw new IllegalArgumentException("Unit already repaired.");
            }
        }

        if(unit.isPresent()) {
            unit.get().setStatus("REPAIRED");
            workOrderUnitRepository.save(unit.get());
        }


        return HttpResponse.ok();
    }

    @Error
    public HttpResponse<ErrorResponse> onSavedFailed(HttpRequest request, Throwable ex) {
        LOG.info("\tError - 400 - Bad Request", ex);
        ErrorResponse error = new ErrorResponse();
        error.setCode("ERR000");
        error.setMessage(ex.getMessage());

        return HttpResponse.<ErrorResponse>badRequest().body(error);
    }
}
