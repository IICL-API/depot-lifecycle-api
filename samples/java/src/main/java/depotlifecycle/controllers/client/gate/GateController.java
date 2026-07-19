package depotlifecycle.controllers.client.gate;

import depotlifecycle.DepotLifecycleConfiguration;
import depotlifecycle.GateResponse;
import depotlifecycle.GateStatus;
import depotlifecycle.PendingResponse;
import depotlifecycle.clients.gate.GateClient;
import depotlifecycle.commands.gate.GateCreateCommand;
import depotlifecycle.commands.gate.GateDeleteCommand;
import depotlifecycle.commands.gate.GateFetchCommand;
import depotlifecycle.commands.gate.GateUpdateCommand;
import depotlifecycle.domain.gate.*;
import depotlifecycle.system.ClientErrorHandling;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.validation.Validated;
import io.micronaut.views.View;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Validated
@Controller("/client/gate")
@RequiredArgsConstructor
@Produces(MediaType.TEXT_HTML)
@Hidden
public class GateController {
    private static final Logger LOG = LoggerFactory.getLogger(GateController.class);
    private final DepotLifecycleConfiguration projectConfig;
    private final Validator validator;
    private final GateClient gateClient;

    @Consumes(MediaType.ALL)
    @View("gate/index")
    @Get
    Mono<Map<String, Object>> index() {
        Map<String, Object> model = Map.ofEntries(
                Map.entry("projectConfig", projectConfig),
                Map.entry("gateStatuses", GateRequestStatus.values()),
                Map.entry("gateTypes", GateRequestType.values()),
                Map.entry("gateTransportTypes", GateTransportType.values())
        );
        return Mono.just(model);
    }

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post("/fetch")
    @View("gate/gateStatusList")
    Mono<Map<String, Object>> fetch(@Body GateFetchCommand cmd) {
        LOG.info("Client - Gate - Fetch");

        ClientErrorHandling.validate(cmd, validator);

        Publisher<GateStatus> gatePublisher = gateClient.get(cmd.getUnitNumber());

        return Mono.from(gatePublisher)
                .onErrorMap(HttpClientResponseException.class, ClientErrorHandling::handleError).map(gate -> Map.of("gates", List.of(gate)));
    }

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post("/delete")
    @View("gate/gateMessage")
    Mono<Map<String, Object>> delete(@Body GateDeleteCommand cmd) {
        LOG.info("Client - Gate - Delete");

        ClientErrorHandling.validate(cmd, validator);

        try {
            gateClient.delete(cmd.getDepot(), cmd.getAdviceNumber(), cmd.getUnitNumber());
        } catch (HttpClientResponseException e) {
            throw ClientErrorHandling.handleError(e);
        }

        Map<String, Object> results = new HashMap<>();
        results.put("title", "Gate Delete Results");
        results.put("message", "Gate Deleted");
        return Mono.just(results);
    }

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post("/update")
    @View("gate/gateResponseList")
    Mono<Map<String, Object>> update(@Body GateUpdateCommand cmd) {
        LOG.info("Client - Gate - Update");

        ClientErrorHandling.validate(cmd, validator);

        GateUpdateRequest gateRequest = new GateUpdateRequest();
        gateRequest.setStatus(cmd.getStatus());
        gateRequest.setActivityTime(cmd.getActivityTime());
        gateRequest.setType(cmd.getType());
        gateRequest.setTransportType(cmd.getTransportType());
        if (cmd.getPhotos() != null) {
            gateRequest.setPhotos(cmd.getPhotos().stream().map(photo -> {
                GatePhoto gatePhoto = new GatePhoto();
                gatePhoto.setUrl(photo.getUrl());
                return gatePhoto;
            }).toList());
        }
        if(cmd.getTrucker() != null) {
            gateRequest.setTrucker(cmd.getTrucker().toExternalParty());
        }

        Publisher<GateResponse> gatePublisher = gateClient.update(cmd.getAdviceNumber(), cmd.getUnitNumber(), cmd.getDepot(), gateRequest);

        return Mono.from(gatePublisher)
                .onErrorMap(HttpClientResponseException.class, ClientErrorHandling::handleError)
                .map(gate -> Map.of("gates", List.of(gate)));
    }

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post("/create")
    @View("gate/gateResponseList")
    Mono<Map<String, Object>> create(@Body GateCreateCommand cmd) {
        LOG.info("Client - Gate - Create");

        ClientErrorHandling.validate(cmd, validator);

        GateCreateRequest gateRequest = new GateCreateRequest();
        gateRequest.setAdviceNumber(cmd.getAdviceNumber());
        gateRequest.setDepot(cmd.getDepot().toParty());
        gateRequest.setUnitNumber(cmd.getUnitNumber());
        gateRequest.setStatus(cmd.getStatus());
        gateRequest.setActivityTime(cmd.getActivityTime());
        gateRequest.setType(cmd.getType());
        gateRequest.setTransportType(cmd.getTransportType());
        if (cmd.getPhotos() != null) {
            gateRequest.setPhotos(cmd.getPhotos().stream().map(photo -> {
                GatePhoto gatePhoto = new GatePhoto();
                gatePhoto.setUrl(photo.getUrl());
                return gatePhoto;
            }).toList());
        }
        if(cmd.getTrucker() != null) {
            gateRequest.setTrucker(cmd.getTrucker().toExternalParty());
        }
        if(cmd.getEquipmentDetail() != null) {
            gateRequest.setEquipmentDetail(cmd.getEquipmentDetail().toEquipmentDetail());
        }

        Publisher<GateResponse> gatePublisher = gateClient.create(gateRequest);

        return Mono.from(gatePublisher)
                .onErrorMap(HttpClientResponseException.class, ClientErrorHandling::handleError)
                .map(gate -> Map.of("gates", List.of(gate)));
    }

    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post("/uploadPhoto")
    @View("gate/gateMessage")
    Mono<Map<String, Object>> uploadPhoto(@Part Long relatedId, @Part CompletedFileUpload file) {
        LOG.info("Client - Gate - Upload Photo");

        MultipartBody body = buildPhotoBody(file);

        Publisher<HttpResponse<PendingResponse>> publisher = gateClient.uploadPhoto(relatedId, body);

        return Mono.from(publisher)
                .onErrorMap(HttpClientResponseException.class, ClientErrorHandling::handleError)
                .map(response -> {
                    Map<String, Object> results = new HashMap<>();
                    results.put("title", "Gate Photo Upload Results");
                    if (response.getStatus() == HttpStatus.ACCEPTED) {
                        results.put("message", "Photo accepted for processing (pending manual review).");
                    } else {
                        results.put("message", "Photo uploaded successfully.");
                    }
                    return results;
                });
    }

    private static MultipartBody buildPhotoBody(CompletedFileUpload file) {
        if (file == null || file.getFilename() == null || file.getFilename().isEmpty()) {
            throw new IllegalArgumentException("Must provide a photo to upload.");
        }

        try {
            byte[] bytes = file.getBytes();
            if (bytes == null || bytes.length == 0) {
                throw new IllegalArgumentException("Must provide a photo to upload.");
            }
            return MultipartBody.builder()
                    .addPart("file", file.getFilename(), file.getContentType().orElseGet(() -> MediaType.forFilename(file.getFilename().toLowerCase(Locale.ROOT))), bytes)
                    .build();
        } catch (IOException ioException) {
            throw new IllegalArgumentException("Must provide a photo to upload.");
        }
    }
}
