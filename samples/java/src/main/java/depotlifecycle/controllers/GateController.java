package depotlifecycle.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import depotlifecycle.ErrorResponse;
import depotlifecycle.GateResponse;
import depotlifecycle.GateStatus;
import depotlifecycle.PendingResponse;
import depotlifecycle.domain.GateCreateRequest;
import depotlifecycle.domain.GateDeleteRequest;
import depotlifecycle.domain.GateUpdateRequest;
import depotlifecycle.domain.Party;
import depotlifecycle.repositories.GateCreateRequestRepository;
import depotlifecycle.repositories.GateDeleteRequestRepository;
import depotlifecycle.repositories.GateUpdateRequestRepository;
import depotlifecycle.repositories.PartyRepository;
import depotlifecycle.security.AuthenticationProviderUserPassword;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpResponseFactory;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.hateoas.JsonError;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

@Tag(name = "gate")
@Validated
@Secured("isAuthenticated()")
@Controller("/api/v2/gate")
@RequiredArgsConstructor
public class GateController {
    private static final Logger LOG = LoggerFactory.getLogger(GateController.class);
    private final PartyRepository partyRepository;
    private final GateCreateRequestRepository gateCreateRequestRepository;
    private final GateUpdateRequestRepository gateUpdateRequestRepository;
    private final GateDeleteRequestRepository gateDeleteRequestRepository;
    private final ConversionService<?> conversionService;
    private final SecurityService securityService;

    @Post(produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "create a gate record",
        description = "Creates either a gate-in or gate-out record for the given shipping container against the provided advice and depot data.",
        method = "POST",
        operationId = "saveGate",
        extensions = @Extension(properties = { @ExtensionProperty(name = "iicl-purpose", value = "activity", parseValue = true) })
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successfully created a gate in or gate out record for the shipping container", content = {@Content(schema = @Schema(implementation = GateResponse.class))}),
        @ApiResponse(responseCode = "202", description = "gate accepted for processing, but not created due to manual processing requirement", content = {@Content(schema = @Schema(implementation = PendingResponse.class))}),
        @ApiResponse(responseCode = "400", description = "an error occurred", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "create a gate record is disallowed by security configuration"),
        @ApiResponse(responseCode = "404", description = "the shipping container or depot could not be found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse<Object> create(@Body @RequestBody(description = "gate object to create a new gate in or gate out record", required = true, content = {@Content(schema = @Schema(implementation = GateCreateRequest.class))}) GateCreateRequest gateCreateRequest) {
        LOG.info("Received Gate Create");
        conversionService.convert(gateCreateRequest, JsonNode.class).ifPresent(jsonNode -> LOG.info(jsonNode.toString()));

        if (securityService.username().equals(AuthenticationProviderUserPassword.VALIDATE_USER_NAME) && gateCreateRequestRepository.existsByAdviceNumberAndUnitNumberAndType(gateCreateRequest.getAdviceNumber(), gateCreateRequest.getUnitNumber(), gateCreateRequest.getType())) {
            throw new IllegalArgumentException("Gate already exists; please update instead.");
        }

        if (gateCreateRequest.getDepot() != null) {
            gateCreateRequest.setDepot(partyRepository.save(gateCreateRequest.getDepot()));
        }

        gateCreateRequest = gateCreateRequestRepository.save(gateCreateRequest);

        //Generate an example gate for the purposes of this demo
        GateResponse gate = new GateResponse();
        gate.setRelatedId(gateCreateRequest.getId());
        gate.setAdviceNumber(gateCreateRequest.getAdviceNumber());
        gate.setCustomerReference("EXAMPLE01");
        gate.setTransactionReference(gateCreateRequest.getId().toString());
        //No insurance coverage in example
        gate.setCurrentExchangeRate(BigDecimal.ONE);
        gate.setComments(Arrays.asList("Example Comment #1", "Example Comment #2"));
        gate.setCurrentInspectionCriteria("IICL");

        LOG.info("Responding with example Gate Response");
        conversionService.convert(gate, JsonNode.class).ifPresent(jsonNode -> LOG.info(jsonNode.toString()));

        return HttpResponse.ok(gate);
    }

    @Get(uri = "/{unitNumber}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "fetch the current gate status",
        description = "For the given unit number, if the shipping container is currently gated in or gated out, fetch the current interchange information - status, the time of the gate, etc.  This is a reporting only function.  To correct a gate status, use the update a gate record endpoint.",
        method = "GET",
        operationId = "showGate",
        extensions = @Extension(properties = { @ExtensionProperty(name = "iicl-purpose", value = "reporting", parseValue = true) })
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successfully found current gate status", content = {@Content(schema = @Schema(implementation = GateStatus.class))}),
        @ApiResponse(responseCode = "400", description = "an error occurred", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "fetching a gate record is disallowed by security"),
        @ApiResponse(responseCode = "404", description = "the shipping container was not found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse<HttpStatus> get(@Parameter(name = "unitNumber", description = "the current unit number of the shipping container", in = ParameterIn.PATH, required = true, schema = @Schema(type = "string", pattern = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", example = "CONU1234561", maxLength = 11)) String unitNumber) {
        return HttpResponseFactory.INSTANCE.status(HttpStatus.NOT_IMPLEMENTED);
    }

    @Put(uri = "/{depot}/{adviceNumber}/{unitNumber}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "update a gate record",
        description = "Correct the initial damage indicator status or activity time from when the gate record was created.  Some depot operators refer to this as a `status` update.",
        method = "PUT",
        operationId = "updateGate",
        extensions = @Extension(properties = { @ExtensionProperty(name = "iicl-purpose", value = "activity", parseValue = true) })
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successfully update the gate record", content = {@Content(schema = @Schema(implementation = GateResponse.class))}),
        @ApiResponse(responseCode = "202", description = "gate update accepted for processing, but not created due to manual processing requirement", content = {@Content(schema = @Schema(implementation = PendingResponse.class))}),
        @ApiResponse(responseCode = "400", description = "an error occurred", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "update a gate record is disallowed by security"),
        @ApiResponse(responseCode = "404", description = "the shipping container or depot could not be found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse<Object> update(@Parameter(name = "adviceNumber", description = "the redelivery or release advice number for the gate record", in = ParameterIn.PATH, required = true, schema = @Schema(type = "string", example = "AHAMG000000", minLength = 1, maxLength = 16)) String adviceNumber,
                               @Parameter(name = "unitNumber", description = "the current unit number of the shipping container", in = ParameterIn.PATH, required = true, schema = @Schema(type = "string", example = "CONU1234561", pattern = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", maxLength = 11)) String unitNumber,
                               @Parameter(name = "depot", description = "the identifier of the depot", in = ParameterIn.PATH, required = true, schema = @Schema(type = "string", pattern = "^[A-Z0-9]{9}$", example = "DEHAMCMRA", maxLength = 9)) String depot,
                               @Body @RequestBody(description = "gate object to update an existing record", required = true, content = {@Content(schema = @Schema(implementation = GateUpdateRequest.class))}) GateUpdateRequest gateUpdateRequest) {
        LOG.info("Received Gate Update");
        conversionService.convert(gateUpdateRequest, JsonNode.class).ifPresent(jsonNode -> LOG.info(jsonNode.toString()));

        if(!gateCreateRequestRepository.existsByAdviceNumberAndUnitNumberAndType(adviceNumber, unitNumber, gateUpdateRequest.getType())) {
            if (securityService.username().equals(AuthenticationProviderUserPassword.VALIDATE_USER_NAME)) {
                throw new IllegalArgumentException("Gate does not exist.");
            }

            LOG.info("Gate DNE -> Writing to Gate Update");
        }

        gateUpdateRequest = gateUpdateRequestRepository.save(gateUpdateRequest);

        //Generate an example gate for the purposes of this demo
        GateResponse gate = new GateResponse();
        gate.setRelatedId(gateUpdateRequest.getId());
        gate.setAdviceNumber(adviceNumber);
        gate.setCustomerReference("EXAMPLE01");
        gate.setTransactionReference(gateUpdateRequest.getId().toString());
        //No insurance coverage in example
        gate.setCurrentExchangeRate(BigDecimal.ONE);
        gate.setComments(Arrays.asList("Example Comment #1", "Example Comment #2"));
        gate.setCurrentInspectionCriteria("IICL");

        LOG.info("Responding with example Gate Response");
        conversionService.convert(gate, JsonNode.class).ifPresent(jsonNode -> LOG.info(jsonNode.toString()));

        return HttpResponse.ok(gate);
    }

    @Delete(uri = "/{depot}/{adviceNumber}/{unitNumber}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "delete a gate record",
        description = "Delete a gate record.",
        method = "DELETE",
        operationId = "deleteGate",
        extensions = @Extension(properties = { @ExtensionProperty(name = "iicl-purpose", value = "activity", parseValue = true) })
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successfully delete the gate record"),
        @ApiResponse(responseCode = "400", description = "an error occurred", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "delete a gate record is disallowed by security"),
        @ApiResponse(responseCode = "404", description = "the shipping container or depot could not be found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse<HttpStatus> delete(@Parameter(name = "adviceNumber", description = "the redelivery or release advice number to identify which gate record to delete", in = ParameterIn.PATH, required = true, schema = @Schema(type = "string", example = "AHAMG000000", minLength = 1, maxLength = 16)) String adviceNumber,
                                       @Parameter(name = "unitNumber", description = "the current unit number of the shipping container", in = ParameterIn.PATH, required = true, schema = @Schema(type = "string", example = "CONU1234561", pattern = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", maxLength = 11)) String unitNumber,
                                       @Parameter(name = "depot", description = "the identifier of the depot", in = ParameterIn.PATH, required = true, schema = @Schema(type = "string", pattern = "^[A-Z0-9]{9}$", example = "DEHAMCMRA", maxLength = 9)) String depot) {
        LOG.info("Received Gate Delete for {}, {}, {}", depot, adviceNumber, unitNumber);

        Optional<Party> depotParty = partyRepository.findByCompanyId(depot);
        if (securityService.username().equals(AuthenticationProviderUserPassword.VALIDATE_USER_NAME)) {
            if(depotParty.isEmpty()) {
                return HttpResponse.notFound();
            }

            if(!gateCreateRequestRepository.existsByAdviceNumberAndUnitNumberAndType(adviceNumber, unitNumber, "IN") ||
               !gateCreateRequestRepository.existsByAdviceNumberAndUnitNumberAndType(adviceNumber, unitNumber, "OUT")) {
                return HttpResponse.notFound();
            }

            if(!gateDeleteRequestRepository.existsByDepotAndAdviceNumberAndUnitNumber(depotParty.get(), adviceNumber, unitNumber)) {
                return HttpResponse.notFound();
            }
        }
        else {
            depotParty = Optional.of(new Party());
            depotParty.get().setCompanyId(depot);
            depotParty = Optional.of(partyRepository.save(depotParty.get()));
        }

        GateDeleteRequest gateDeleteRequest = new GateDeleteRequest();
        gateDeleteRequest.setDepot(depotParty.get());
        gateDeleteRequest.setAdviceNumber(adviceNumber);
        gateDeleteRequest.setUnitNumber(unitNumber);
        gateDeleteRequestRepository.save(gateDeleteRequest);

        LOG.info("Gate Deleted, responding with OK");
        return HttpResponse.ok();
    }

    @Error(status = HttpStatus.NOT_FOUND)
    public HttpResponse<JsonError> notFound(HttpRequest request) {
        JsonError error = new JsonError("Not Found");

        return HttpResponse.<JsonError>notFound()
            .body(error);
    }

    @Error
    public HttpResponse onSavedFailed(HttpRequest request, Throwable ex) {
        LOG.info("\tError - 400 - Bad Request", ex);
        ErrorResponse error = new ErrorResponse();
        error.setCode("ERR000");
        error.setMessage(ex.getMessage());

        return HttpResponse.badRequest().body(error);
    }
}
