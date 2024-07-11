package depotlifecycle.controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import depotlifecycle.ErrorResponse;
import depotlifecycle.domain.Release;
import depotlifecycle.domain.ReleaseDetail;
import depotlifecycle.domain.ReleaseDetailCriteria;
import depotlifecycle.repositories.PartyRepository;
import depotlifecycle.repositories.ReleaseRepository;
import depotlifecycle.security.AuthenticationProviderUserPassword;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.convert.ConversionService;
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
import io.micronaut.security.annotation.Secured;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Tag(name = "release")
@Validated
@Secured("isAuthenticated()")
@Controller("/api/v2/release")
@RequiredArgsConstructor
public class ReleaseController {
    private static final Logger LOG = LoggerFactory.getLogger(ReleaseController.class);
    private final PartyRepository partyRepository;
    private final ReleaseRepository releaseRepository;
    private final ConversionService conversionService;
    private final SecurityService securityService;

    @Get(produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "search for a release",
        description = "Finds Releases for the given the criteria.",
        method = "GET",
        operationId = "indexRelease",
        extensions = @Extension(properties = { @ExtensionProperty(name = "iicl-purpose", value = "reporting", parseValue = true) })
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful search", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = Release.class)))}),
        @ApiResponse(responseCode = "400", description = "an error occurred", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "security disallows access"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse index(@Nullable @QueryValue("releaseNumber") @Parameter(name = "releaseNumber", description = "the release number to filter to", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", example = "RHAMG000000", maxLength = 16)) String releaseNumber,
                              @Nullable @QueryValue("includeCandidates") @Parameter(name = "includeCandidates", description = "whether to include candidate units for any found release", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "boolean", example = "false")) Boolean includeCandidates,
                              @Nullable @QueryValue("gateCheck") @Parameter(name = "gateCheck", description = "flag to indicate this search is to check if the found advices are valid for gate out", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "boolean", example = "true", defaultValue = "true")) Boolean gateCheck
                              ) {
        LOG.info("Received Release Search");
        Stream.of(Optional.of("Release Number:"), Optional.ofNullable(releaseNumber)).filter(Optional::isPresent).map(Optional::get).reduce(String::concat).ifPresent(LOG::info);
        Stream.of(Optional.of("Gate Check:"), Optional.of(gateCheck == null || gateCheck).map(Object::toString)).map(Optional::get).reduce(String::concat).ifPresent(LOG::info);

        List<Release> releases = new ArrayList<>();
        if (releaseNumber != null) {
            Optional<Release> release = releaseRepository.findByReleaseNumber(releaseNumber);
            release.ifPresent(releases::add);
        }
        else {
            for (Release release : releaseRepository.findAll()) {
                releases.add(release);
            }
        }

        if (releases.isEmpty()) {
            LOG.info("\tRelease Search - 404 - Not Found");
            return HttpResponse.notFound();
        }
        else {
            LOG.info("\tRelease Search - 200 - Found Releases");
            return HttpResponse.ok(releases);
        }
    }

    @Post(produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "create release",
        description = "Creates a Release for the given criteria.",
        method = "POST",
        operationId = "saveRelease",
        extensions = @Extension(properties = { @ExtensionProperty(name = "iicl-purpose", value = "activity", parseValue = true) })
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful create"),
        @ApiResponse(responseCode = "400", description = "an error occurred", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "security disallows access"),
        @ApiResponse(responseCode = "404", description = "the release depot was not found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse<HttpStatus> create(@Body @RequestBody(description = "Data to use to update the given Release", required = true, content = {@Content(schema = @Schema(implementation = Release.class))}) Release release, @Parameter(hidden = true) HttpHeaders headers) {
        LOG.info("Received Release Create");
        conversionService.convert(release, JsonNode.class).ifPresent(jsonNode -> LOG.info(jsonNode.toString()));
        Optional.of(headers.names().stream().collect(LinkedHashMap::new, (m, v)->m.put(v, headers.get(v)), HashMap::putAll).toString()).ifPresent(LOG::info);

        if (securityService.username().equals(AuthenticationProviderUserPassword.VALIDATE_USER_NAME) && releaseRepository.existsByReleaseNumber(release.getReleaseNumber())) {
            throw new IllegalArgumentException("Redelivery already exists; please update instead.");
        }

        saveParties(release);

        releaseRepository.save(release);
        return HttpResponse.ok();
    }

    private void saveParties(Release release) {
        for (ReleaseDetail detail : release.getDetails()) {
            if (detail.getCustomer() != null) {
                detail.setCustomer(partyRepository.save(detail.getCustomer()));
            }

            if(!Objects.isNull(detail.getCriteria())) {
                for (ReleaseDetailCriteria criteria : detail.getCriteria()) {
                    criteria.setReleaseDetail(detail);
                }
            }
        }

        if (release.getOwner() != null) {
            release.setOwner(partyRepository.save(release.getOwner()));
        }

        if (release.getDepot() != null) {
            release.setDepot(partyRepository.save(release.getDepot()));
        }

        if (release.getRecipient() != null) {
            release.setRecipient(partyRepository.save(release.getRecipient()));
        }
    }

    @Put(uri = "/{releaseNumber}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "update release",
        description = "Updates an existing Release.",
        method = "PUT",
        operationId = "updateRelease",
        extensions = @Extension(properties = { @ExtensionProperty(name = "iicl-purpose", value = "activity", parseValue = true) }))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful update"),
        @ApiResponse(responseCode = "400", description = "an error occurred", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "security disallows access"),
        @ApiResponse(responseCode = "404", description = "the release was not found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse<HttpStatus> update(@Parameter(description = "name that need to be updated", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string", example = "RHAMG000000", maxLength = 16)) String releaseNumber,
                       @Body @RequestBody(description = "Data to use to update the given Release", required = true, content = {@Content(schema = @Schema(implementation = Release.class))}) Release release, @Parameter(hidden = true) HttpHeaders headers) {
        LOG.info("Received Release Update");
        conversionService.convert(release, JsonNode.class).ifPresent(jsonNode -> LOG.info(jsonNode.toString()));
        Optional.of(headers.names().stream().collect(LinkedHashMap::new, (m, v)->m.put(v, headers.get(v)), HashMap::putAll).toString()).ifPresent(LOG::info);

        if(!releaseRepository.existsByReleaseNumber(releaseNumber)) {
            if (securityService.username().equals(AuthenticationProviderUserPassword.VALIDATE_USER_NAME)) {
                throw new IllegalArgumentException("Release does not exist.");
            }

            LOG.info("Release DNE -> Forcing Create Workflow");
            return create(release, headers);
        }

        saveParties(release);

        releaseRepository.update(release);
        return HttpResponse.ok();
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
