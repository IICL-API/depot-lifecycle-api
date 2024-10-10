package depotlifecycle.controllers.api;

import depotlifecycle.ErrorResponse;
import depotlifecycle.PendingResponse;
import depotlifecycle.domain.GateCreateRequest;
import depotlifecycle.repositories.GateCreateRequestRepository;
import depotlifecycle.security.AuthenticationProviderUserPassword;
import depotlifecycle.system.ApiErrorHandling;
import io.micronaut.http.*;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Post;
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

@Tag(name = "gate photos")
@Validated
@Secured("isAuthenticated()")
@Controller("/api/v2/gatePhoto")
@RequiredArgsConstructor
public class GatePhotoController {
    private static final Logger LOG = LoggerFactory.getLogger(GatePhotoController.class);
    private final GateCreateRequestRepository gateCreateRequestRepository;
    private final SecurityService securityService;

    @Post(uri = "/{relatedId}", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON)
    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "upload a gate photo",
        description = "Instead of using a link, upload a photo for a previously created gate record",
        method = "POST",
        operationId = "uploadGatePhoto",
        extensions = @Extension(properties = { @ExtensionProperty(name = "iicl-purpose", value = "activity", parseValue = true) })
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successfully uploaded a gate photo", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "202", description = "photo accepted for processing, but not created due to manual processing requirement", content = {@Content(schema = @Schema(implementation = PendingResponse.class))}),
        @ApiResponse(responseCode = "400", description = "an invalid request was provided", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "uploading a gate photo is disallowed by security"),
        @ApiResponse(responseCode = "404", description = "the gate record could not be found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    @RequestBody(description = "The photo to upload (expected name of part is `file`)", required = true, content = {@Content(mediaType = MediaType.MULTIPART_FORM_DATA, schema = @Schema(name="file", type = "string", format = "binary", description = "the photo data"))})
    public HttpResponse<HttpStatus> create(@Parameter(name = "relatedId", description = "the related identifier (from the gate record) that this photo should be attached", in = ParameterIn.PATH, required = true, schema = @Schema(example = "10102561", type = "integer", format = "int64")) Long relatedId,
                                           CompletedFileUpload file) {
        LOG.info("Received Gate Photo with name: {} of size {} bytes for relatedId {}", file.getFilename(), file.getSize(), relatedId);

        if (securityService.username().equals(AuthenticationProviderUserPassword.VALIDATE_USER_NAME)) {
            Optional<GateCreateRequest> gate = gateCreateRequestRepository.findById(relatedId);
            if (gate.isEmpty()) {
                throw new HttpStatusException(HttpStatus.NOT_FOUND, "Gate does not exist for upload photo.");
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
