package depotlifecycle.controllers.client;

import depotlifecycle.DepotLifecycleConfiguration;
import depotlifecycle.GateResponse;
import depotlifecycle.GateStatus;
import depotlifecycle.clients.GateClient;
import depotlifecycle.commands.GateCreateCommand;
import depotlifecycle.commands.GateDeleteCommand;
import depotlifecycle.commands.GateFetchCommand;
import depotlifecycle.commands.GateUpdateCommand;
import depotlifecycle.domain.*;
import depotlifecycle.system.ClientErrorHandling;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.micronaut.views.View;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@Controller("/client/gate")
@RequiredArgsConstructor
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
@Hidden
public class GateController {
    private static final Logger LOG = LoggerFactory.getLogger(GateController.class);
    private final DepotLifecycleConfiguration projectConfig;
    private final Validator validator;
    private final GateClient gateClient;

    @View("gate")
    @Get
    Mono<Map<String, Object>> index() {
        Map<String, Object> model = Map.ofEntries(
                Map.entry("projectConfig", projectConfig),
                Map.entry("gateStatuses", GateRequestStatus.values()),
                Map.entry("gateTypes", GateRequestType.values())
        );
        return Mono.just(model);
    }

    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post("/fetch")
    @View("gateStatusList")
    Mono<Map<String, Object>> fetch(@Body GateFetchCommand cmd) {
        LOG.info("Client - Gate - Fetch");

        ClientErrorHandling.validate(cmd, validator);

        Publisher<GateStatus> gatePublisher = gateClient.get(cmd.getUnitNumber());

        return Mono.from(gatePublisher)
                .onErrorMap(HttpClientResponseException.class, ClientErrorHandling::handleError).map(gate -> Map.of("gates", List.of(gate)));
    }

    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post("/delete")
    @View("gateMessage")
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

    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post("/update")
    @View("gateResponseList")
    Mono<Map<String, Object>> update(@Body GateUpdateCommand cmd) {
        LOG.info("Client - Gate - Update");

        ClientErrorHandling.validate(cmd, validator);

        GateUpdateRequest gateRequest = new GateUpdateRequest();
        gateRequest.setStatus(cmd.getStatus());
        gateRequest.setActivityTime(cmd.getActivityTime());
        gateRequest.setType(cmd.getType());
        if (cmd.getPhotos() != null) {
            gateRequest.setPhotos(cmd.getPhotos().stream().map(photo -> {
                GatePhoto gatePhoto = new GatePhoto();
                gatePhoto.setUrl(photo.getUrl());
                return gatePhoto;
            }).toList());
        }

        Publisher<GateResponse> gatePublisher = gateClient.update(cmd.getAdviceNumber(), cmd.getUnitNumber(), cmd.getDepot(), gateRequest);

        return Mono.from(gatePublisher)
                .onErrorMap(HttpClientResponseException.class, ClientErrorHandling::handleError)
                .map(gate -> Map.of("gates", List.of(gate)));
    }

    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post("/create")
    @View("gateResponseList")
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
        if (cmd.getPhotos() != null) {
            gateRequest.setPhotos(cmd.getPhotos().stream().map(photo -> {
                GatePhoto gatePhoto = new GatePhoto();
                gatePhoto.setUrl(photo.getUrl());
                return gatePhoto;
            }).toList());
        }

        Publisher<GateResponse> gatePublisher = gateClient.create(gateRequest);

        return Mono.from(gatePublisher)
                .onErrorMap(HttpClientResponseException.class, ClientErrorHandling::handleError)
                .map(gate -> Map.of("gates", List.of(gate)));
    }
}
