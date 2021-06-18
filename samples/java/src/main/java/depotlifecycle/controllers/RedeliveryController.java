package depotlifecycle.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import depotlifecycle.ErrorResponse;
import depotlifecycle.domain.Redelivery;
import depotlifecycle.domain.RedeliveryDetail;
import depotlifecycle.domain.RedeliveryUnit;
import depotlifecycle.repositories.PartyRepository;
import depotlifecycle.repositories.RedeliveryRepository;
import depotlifecycle.services.AuthenticationProviderUserPassword;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.jackson.convert.ObjectToJsonNodeConverter;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.utils.SecurityService;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "redelivery")
@Validated
@Secured("isAuthenticated()")
@Controller("/api/v2/redelivery")
@RequiredArgsConstructor
public class RedeliveryController {
    private static final Logger LOG = LoggerFactory.getLogger(RedeliveryController.class);
    private final PartyRepository partyRepository;
    private final RedeliveryRepository redeliveryRepository;
    private final ObjectToJsonNodeConverter objectToJsonNodeConverter;
    private final SecurityService securityService;

    @Get(produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "search for a redelivery", description = "Finds Redeliveries for the given the criteria.", method = "GET", operationId = "indexRedelivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful search", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = Redelivery.class)))}),
        @ApiResponse(responseCode = "400", description = "an error occurred", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "security disallows access"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse index(@QueryValue("redeliveryNumber") @Parameter(name = "redeliveryNumber", description = "the redelivery number to filter to", in = ParameterIn.QUERY, required = false, schema = @Schema(example = "AHAMG000000", maxLength = 16)) String redeliveryNumber) {
        LOG.info("Received Redelivery Search");
        Optional.of(redeliveryNumber).ifPresent(LOG::info);

        List<Redelivery> redeliveries = new ArrayList<>();
        if (redeliveryNumber != null) {
            Optional<Redelivery> redelivery = redeliveryRepository.findByRedeliveryNumber(redeliveryNumber);
            redelivery.ifPresent(redeliveries::add);
        }
        else {
            for (Redelivery redelivery : redeliveryRepository.findAll()) {
                redeliveries.add(redelivery);
            }
        }

        if (redeliveries.isEmpty()) {
            LOG.info("\tRelease Search - 404 - Not Found");
            return HttpResponse.notFound();
        }
        else {
            LOG.info("\tRelease Search - 200 - Found Releases");
            return HttpResponse.ok(redeliveries);
        }
    }

    @Post(produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "create redelivery", description = "Creates a Redelivery for the given criteria.", method = "POST", operationId = "saveRedelivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful create"),
        @ApiResponse(responseCode = "400", description = "an error occurred", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "security disallows access"),
        @ApiResponse(responseCode = "404", description = "the redelivery depot was not found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse<HttpStatus> create(@Body @RequestBody(description = "Data to use to update the given Redelivery", required = true, content = {@Content(schema = @Schema(implementation = Redelivery.class))}) Redelivery redelivery, HttpHeaders headers) {
        LOG.info("Received Redelivery Create");
        objectToJsonNodeConverter.convert(redelivery, JsonNode.class).ifPresent(jsonNode -> LOG.info(jsonNode.toString()));
        Optional.of(headers.names().stream().collect(LinkedHashMap::new, (m, v)->m.put(v, headers.get(v)), HashMap::putAll).toString()).ifPresent(LOG::info);

        if (securityService.username().equals(AuthenticationProviderUserPassword.VALIDATE_USER_NAME) && redeliveryRepository.existsByRedeliveryNumber(redelivery.getRedeliveryNumber())) {
            throw new IllegalArgumentException("Redelivery already exists; please update instead.");
        }

        saveParties(redelivery);

        redeliveryRepository.save(redelivery);
        return HttpResponse.ok();
    }

    @Put(uri = "/{redeliveryNumber}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "update redelivery", description = "Updates an existing Redelivery.", method = "PUT", operationId = "updateRedelivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful update"),
        @ApiResponse(responseCode = "400", description = "an error occurred", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "security disallows access"),
        @ApiResponse(responseCode = "404", description = "the redelivery was not found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse<HttpStatus> update(@Parameter(description = "the redelivery number that needs updated", required = true, in = ParameterIn.PATH, schema = @Schema(example = "AHAMG000000", maxLength = 16)) String redeliveryNumber,
                       @Body @RequestBody(description = "Data to use to update the given Redelivery", required = true, content = {@Content(schema = @Schema(implementation = Redelivery.class))}) Redelivery redelivery, HttpHeaders headers) {
        LOG.info("Received Redelivery Update");
        objectToJsonNodeConverter.convert(redelivery, JsonNode.class).ifPresent(jsonNode -> LOG.info(jsonNode.toString()));
        Optional.of(headers.names().stream().collect(LinkedHashMap::new, (m, v)->m.put(v, headers.get(v)), HashMap::putAll).toString()).ifPresent(LOG::info);

        if(!redeliveryRepository.existsByRedeliveryNumber(redeliveryNumber)) {
            if (securityService.username().equals(AuthenticationProviderUserPassword.VALIDATE_USER_NAME)) {
                throw new IllegalArgumentException("Redelivery does not exist.");
            }

            LOG.info("Redelivery DNE -> Forcing Create Workflow");
            return create(redelivery, headers);
        }

        saveParties(redelivery);

        redeliveryRepository.update(redelivery);
        return HttpResponse.ok();
    }

    private void saveParties(Redelivery redelivery) {
        for (RedeliveryDetail detail : redelivery.getDetails()) {
            if (detail.getCustomer() != null) {
                detail.setCustomer(partyRepository.save(detail.getCustomer()));
            }

            for (RedeliveryUnit unit : detail.getUnits()) {
                if (unit.getLastOnHireLocation() != null) {
                    unit.setLastOnHireLocation(partyRepository.save(unit.getLastOnHireLocation()));
                }
                if (unit.getBillingParty() != null) {
                    unit.setBillingParty(partyRepository.save(unit.getBillingParty()));
                }
            }
        }

        if (redelivery.getOwner() != null) {
            redelivery.setOwner(partyRepository.save(redelivery.getOwner()));
        }

        if (redelivery.getDepot() != null) {
            redelivery.setDepot(partyRepository.save(redelivery.getDepot()));
        }

        if (redelivery.getRecipient() != null) {
            redelivery.setRecipient(partyRepository.save(redelivery.getRecipient()));
        }
    }

    @Error(status = HttpStatus.NOT_FOUND)
    public HttpResponse<JsonError> notFound(HttpRequest request) {
        LOG.info("\tError - 404 - Not Found");
        JsonError error = new JsonError("Not Found");

        return HttpResponse.<JsonError>notFound()
            .body(error);
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
