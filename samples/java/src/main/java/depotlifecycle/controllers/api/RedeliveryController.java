package depotlifecycle.controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import depotlifecycle.ErrorResponse;
import depotlifecycle.domain.Redelivery;
import depotlifecycle.domain.RedeliveryDetail;
import depotlifecycle.domain.RedeliveryUnit;
import depotlifecycle.repositories.ExternalPartyRepository;
import depotlifecycle.repositories.PartyRepository;
import depotlifecycle.repositories.RedeliveryRepository;
import depotlifecycle.security.AuthenticationProviderUserPassword;
import depotlifecycle.system.ApiErrorHandling;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpResponseFactory;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.AuthorizationException;
import io.micronaut.security.utils.SecurityService;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.stream.Stream;

@Tag(name = "redelivery")
@Validated
@Secured("isAuthenticated()")
@Controller("/api/v2/redelivery")
@RequiredArgsConstructor
public class RedeliveryController {
    private static final Logger LOG = LoggerFactory.getLogger(RedeliveryController.class);
    private final PartyRepository partyRepository;
    private final RedeliveryRepository redeliveryRepository;
    private final ConversionService conversionService;
    private final SecurityService securityService;
    private final ExternalPartyRepository externalPartyRepository;

    @Get(produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "search for a redelivery",
        description = "Finds Redeliveries for the given the criteria.",
        method = "GET",
        operationId = "indexRedelivery",
        extensions = @Extension(properties = {@ExtensionProperty(name = "iicl-purpose", value = "reporting", parseValue = true)})
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful search", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = Redelivery.class)))}),
        @ApiResponse(responseCode = "400", description = "an error occurred", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "security disallows access"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse<HttpStatus> index(@Nullable @QueryValue("redeliveryNumber") @Parameter(name = "redeliveryNumber", description = "the redelivery number to filter to", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", example = "AHAMG000000", maxLength = 16)) String redeliveryNumber,
                                          @Nullable @QueryValue("unitNumber") @Parameter(name = "unitNumber", description = "the unit number of the shipping container", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", example = "CONU1234561", pattern = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", maxLength = 11)) String unitNumber,
                                          @Nullable @QueryValue("gateCheck") @Parameter(name = "gateCheck", description = "flag to indicate this search is to check if the found advices are valid for gate in", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "boolean", example = "true", defaultValue = "true")) Boolean gateCheck) {
        LOG.info("Received Redelivery Search");
        Stream.of(Optional.of("Redelivery Number:"), Optional.ofNullable(redeliveryNumber)).filter(Optional::isPresent).map(Optional::get).reduce(String::concat).ifPresent(LOG::info);
        Stream.of(Optional.of("Unit Number:"), Optional.ofNullable(unitNumber)).filter(Optional::isPresent).map(Optional::get).reduce(String::concat).ifPresent(LOG::info);
        Stream.of(Optional.of("Gate Check:"), Optional.of(gateCheck == null || gateCheck).map(Object::toString)).map(Optional::get).reduce(String::concat).ifPresent(LOG::info);

        return HttpResponseFactory.INSTANCE.status(HttpStatus.NOT_IMPLEMENTED);
    }

    @Post(produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "create redelivery",
        description = "Creates a Redelivery for the given criteria.",
        method = "POST",
        operationId = "saveRedelivery",
        extensions = @Extension(properties = {@ExtensionProperty(name = "iicl-purpose", value = "activity", parseValue = true)})
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful create", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "400", description = "an error occurred", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "security disallows access"),
        @ApiResponse(responseCode = "404", description = "the redelivery depot was not found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse<HttpStatus> create(@Body @RequestBody(description = "Data to use to update the given Redelivery", required = true, content = {@Content(schema = @Schema(implementation = Redelivery.class))}) Redelivery redelivery, @Parameter(hidden = true) HttpHeaders headers) {
        LOG.info("Received Redelivery Create");
        conversionService.convert(redelivery, JsonNode.class).ifPresent(jsonNode -> LOG.info(jsonNode.toString()));
        Optional.of(headers.names().stream().collect(LinkedHashMap::new, (m, v) -> m.put(v, headers.get(v)), HashMap::putAll).toString()).ifPresent(LOG::info);

        if (securityService.username().equals(AuthenticationProviderUserPassword.VALIDATE_USER_NAME) && redeliveryRepository.existsByRedeliveryNumber(redelivery.getRedeliveryNumber())) {
            throw new IllegalArgumentException("Redelivery already exists; please update instead.");
        }

        saveParties(redelivery);

        redeliveryRepository.save(redelivery);
        return HttpResponse.ok();
    }

    @Put(uri = "/{redeliveryNumber}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "update redelivery",
        description = "Updates an existing Redelivery.",
        method = "PUT",
        operationId = "updateRedelivery",
        extensions = @Extension(properties = {@ExtensionProperty(name = "iicl-purpose", value = "activity", parseValue = true)})
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful update", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "400", description = "an error occurred", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "security disallows access"),
        @ApiResponse(responseCode = "404", description = "the redelivery was not found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse<HttpStatus> update(@Parameter(description = "the redelivery number that needs updated", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string", example = "AHAMG000000", maxLength = 16)) String redeliveryNumber,
                                           @Body @RequestBody(description = "Data to use to update the given Redelivery", required = true, content = {@Content(schema = @Schema(implementation = Redelivery.class))}) Redelivery redelivery, @Parameter(hidden = true) HttpHeaders headers) {
        LOG.info("Received Redelivery Update");
        conversionService.convert(redelivery, JsonNode.class).ifPresent(jsonNode -> LOG.info(jsonNode.toString()));
        Optional.of(headers.names().stream().collect(LinkedHashMap::new, (m, v) -> m.put(v, headers.get(v)), HashMap::putAll).toString()).ifPresent(LOG::info);

        if (!redeliveryRepository.existsByRedeliveryNumber(redeliveryNumber)) {
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
                try {
                    detail.setCustomer(externalPartyRepository.save(detail.getCustomer()));
                }
                catch(Exception e) {
                    throw new IllegalArgumentException(String.format("Customer - %s", e.getMessage()));
                }
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
            try {
                redelivery.setRecipient(externalPartyRepository.save(redelivery.getRecipient()));
            }
            catch(Exception e) {
                throw new IllegalArgumentException(String.format("Recipient - %s", e.getMessage()));
            }
        }
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
