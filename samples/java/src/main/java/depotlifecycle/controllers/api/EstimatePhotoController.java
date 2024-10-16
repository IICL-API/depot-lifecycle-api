package depotlifecycle.controllers.api;

import depotlifecycle.ErrorResponse;
import depotlifecycle.PendingResponse;
import depotlifecycle.domain.Estimate;
import depotlifecycle.repositories.EstimateRepository;
import depotlifecycle.security.AuthenticationProviderUserPassword;
import depotlifecycle.system.ApiErrorHandling;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.*;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.AuthorizationException;
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

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Tag(name = "estimate proposals")
@Validated
@Secured("isAuthenticated()")
@Controller("/api/v2/estimatePhoto")
@RequiredArgsConstructor
public class EstimatePhotoController {
    private static final Logger LOG = LoggerFactory.getLogger(EstimatePhotoController.class);
    private final EstimateRepository estimateRepository;
    private final SecurityService securityService;

    @Post(uri = "/{relatedId}", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON)
    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "upload an estimate photo",
        description = "Instead of using a link, upload a photo for a previously allocated estimate",
        method = "POST",
        operationId = "uploadEstimatePhoto",
        extensions = @Extension(properties = { @ExtensionProperty(name = "iicl-purpose", value = "activity", parseValue = true) })
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successfully uploaded an estimate photo", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "202", description = "photo accepted for processing, but not created due to manual processing requirement", content = {@Content(schema = @Schema(implementation = PendingResponse.class))}),
        @ApiResponse(responseCode = "400", description = "an invalid request was provided", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "uploading an estimate photo is disallowed by security"),
        @ApiResponse(responseCode = "404", description = "the estimate or line item could not be found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    @RequestBody(description = "The photo to upload (expected name of part is `file`)", required = true, content = {@Content(mediaType = MediaType.MULTIPART_FORM_DATA, schema = @Schema(name="file", type = "string", format = "binary", description = "the photo data"))})
    public HttpResponse<HttpStatus> create(@Parameter(name = "relatedId", description = "the related identifier (from the estimate allocation) that this photo should be attached", in = ParameterIn.PATH, required = true, schema = @Schema(example = "10102561", type = "integer", format = "int64")) Long relatedId,
                                           @Nullable @QueryValue("line") @Parameter(name = "line", description = "an optional line number to associate this photo to", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "integer", format="int32", example = "1")) Integer line,
                                           @Nullable @QueryValue("status") @Parameter(name = "status", description = "indicator of when this photo applies\n\n`REPAIRED` - Photo is after repair \n\n`BEFORE` - Photo is before repair", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", allowableValues = {"REPAIRED", "BEFORE"}, defaultValue = "BEFORE", example = "BEFORE", maxLength = 8)) String status,
                                           CompletedFileUpload file) {
        LOG.info("Received Estimate Photo with name: {} of size {} bytes for line {} with status {} for relatedId {}", file.getFilename(), file.getSize(), line, status, relatedId);

        if(Objects.isNull(status) || status.isEmpty()) {
            LOG.info("No status received, defaulting to BEFORE.");
            status = "BEFORE";
        }

        if(!status.equals("BEFORE") && !status.equals("REPAIRED")) {
            throw new IllegalArgumentException("Status may only be BEFORE or REPAIRED.");
        }

        if (securityService.username().equals(AuthenticationProviderUserPassword.VALIDATE_USER_NAME)) {
            Optional<Estimate> estimate = estimateRepository.findById(relatedId);
            if (estimate.isEmpty()) {
                throw new HttpStatusException(HttpStatus.NOT_FOUND, "Estimate does not exist for photo upload.");
            }

            if(!Objects.isNull(line)) {
                boolean hasLineItem = estimate.get().getLineItems().stream().anyMatch(estimateLineItem -> estimateLineItem.getLine().equals(line));
                if(!hasLineItem) {
                    throw new HttpStatusException(HttpStatus.NOT_FOUND, "Estimate does not have line [" + line + "].");
                }
            }
        }

        try {
            if(Objects.isNull(file) || Objects.isNull(file.getBytes()) || file.getBytes().length == 0) {
                throw new IllegalArgumentException("Must provide a photo to upload.");
            }
        }
        catch(IOException ioException) {
            throw new IllegalArgumentException("Must provide a photo to upload.");
        }

        if(file.getContentType().isEmpty()) {
            throw new IllegalArgumentException("Media Type must be defined.");
        }

        if(Objects.isNull(file.getFilename()) || file.getFilename().isEmpty()) {
            throw new IllegalArgumentException("Must provide a photo filename to upload.");
        }

        return HttpResponse.ok();
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
