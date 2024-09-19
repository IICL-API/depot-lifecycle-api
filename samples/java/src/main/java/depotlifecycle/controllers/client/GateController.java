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

@Secured(SecurityRule.IS_ANONYMOUS)
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
            gateClient.delete(cmd.getAdviceNumber(), cmd.getUnitNumber(), cmd.getDepot());
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

//    @ExecuteOn(TaskExecutors.BLOCKING)
//    @Post("/updateTotals")
//    @View("estimateMessage")
//    Mono<Map<String, Object>> updateTotals(@Body EstimateTotalsCommand cmd) {
//        LOG.info("Client - Estimate - Update Totals");
//        Set<ConstraintViolation<EstimateTotalsCommand>> violations = validator.validate(cmd);
//        if (!violations.isEmpty()) {
//            ConstraintViolation<EstimateTotalsCommand> violation = violations.iterator().next();
//            String errorMessage = String.join(" ", violation.getPropertyPath().toString(), violation.getMessage());
//            throw new HtmlStatusException(HttpStatus.BAD_REQUEST, errorMessage);
//        }
//
//        estimateClient.update(cmd.getEstimateNumber(), cmd);
//
//        return Mono.just(Map.of("title", "Estimate Totals", "message", "Totals Updated"));
//    }
//
//    @ExecuteOn(TaskExecutors.BLOCKING)
//    @Post("/customerApprove")
//    @View("estimateAllocationList")
//    Mono<Map<String, Object>> customerApprove(@Body EstimateCustomerApproveCommand cmd) {
//        LOG.info("Client - Estimate - Customer Approve");
//
//        Set<ConstraintViolation<EstimateCustomerApproveCommand>> violations = validator.validate(cmd);
//        if (!violations.isEmpty()) {
//            ConstraintViolation<EstimateCustomerApproveCommand> violation = violations.iterator().next();
//            String errorMessage = String.join(" ", violation.getPropertyPath().toString(), violation.getMessage());
//            throw new HtmlStatusException(HttpStatus.BAD_REQUEST, errorMessage);
//        }
//
//        EstimateCustomerApproval customerApproval = new EstimateCustomerApproval();
//        customerApproval.setApprovalNumber(cmd.getApprovalNumber());
//        customerApproval.setApprovalDateTime(cmd.getApprovalDateTime());
//        customerApproval.setApprovalUser(cmd.getApprovalUser());
//        customerApproval.setApprovalTotal(cmd.getApprovalTotal());
//        Publisher<EstimateAllocation> estimatePublisher = estimateClient.customerApprove(cmd.getEstimateNumber(), cmd.getDepot(), customerApproval);
//
//        return Mono.from(estimatePublisher)
//                .onErrorMap(HttpClientResponseException.class, e -> {
//                    String responseBody = e.getResponse().getBody(String.class).orElse("No response body");
//                    String responseHeaders = e.getResponse().getHeaders().toString();
//                    int statusCode = e.getStatus().getCode();
//
//                    String errorMessage = String.format(
//                            "HttpClientResponseException: %s\nStatus Code: %d\nResponse Body: %s\nResponse Headers: %s",
//                            e.getMessage(), statusCode, responseBody, responseHeaders
//                    );
//
//                    String message = String.join(": ", "Unexpected Error Calling API", errorMessage);
//                    throw new HtmlStatusException(e.getStatus(), message);
//                }).map(estimate -> Map.of("estimates", List.of(estimate)));
//    }
//

//
//    @ExecuteOn(TaskExecutors.BLOCKING)
//    @Post("/create")
//    @View("estimateAllocationList")
//    Mono<Map<String, Object>> create(@Body EstimateCreateCommand cmd) {
//        LOG.info("Client - Estimate - Create");
//        Set<ConstraintViolation<EstimateCreateCommand>> violations = validator.validate(cmd);
//        if (!violations.isEmpty()) {
//            ConstraintViolation<EstimateCreateCommand> violation = violations.iterator().next();
//            String errorMessage = String.join(" ", violation.getPropertyPath().toString(), violation.getMessage());
//            throw new HtmlStatusException(HttpStatus.BAD_REQUEST, errorMessage);
//        }
//
//        Publisher<EstimateAllocation> estimatePublisher = estimateClient.create(cmd);
//
//        return Mono.from(estimatePublisher)
//                .onErrorMap(HttpClientResponseException.class, e -> {
//                    String responseBody = e.getResponse().getBody(String.class).orElse("No response body");
//                    String responseHeaders = e.getResponse().getHeaders().toString();
//                    int statusCode = e.getStatus().getCode();
//
//                    String errorMessage = String.format(
//                            "HttpClientResponseException: %s\nStatus Code: %d\nResponse Body: %s\nResponse Headers: %s",
//                            e.getMessage(), statusCode, responseBody, responseHeaders
//                    );
//
//                    String message = String.join(": ", "Unexpected Error Calling API", errorMessage);
//                    throw new HtmlStatusException(e.getStatus(), message);
//                }).map(estimate ->
//                        Map.of("estimates", List.of(estimate))
//                );
//    }
//
//

//
//    @ExecuteOn(TaskExecutors.BLOCKING)
//    @Post("/list")
//    @View("estimateList")
//    Mono<Map<String, Object>> list(@Body EstimateSearchCommand cmd) {
//        LOG.info("Client - Estimate - List");
//        Set<ConstraintViolation<EstimateSearchCommand>> violations = validator.validate(cmd);
//        if (!violations.isEmpty()) {
//            ConstraintViolation<EstimateSearchCommand> violation = violations.iterator().next();
//            String errorMessage = String.join(" ", violation.getPropertyPath().toString(), violation.getMessage());
//            throw new HtmlStatusException(HttpStatus.BAD_REQUEST, errorMessage);
//        }
//
//        Publisher<List<Estimate>> estimatePublisher = estimateClient.search(cmd.getEstimateNumber(), cmd.getUnitNumber(), cmd.getDepot(), cmd.getLessee(), cmd.getRevision(), cmd.getEquipmentCode());
//
//        return Mono.from(estimatePublisher)
//                .onErrorMap(HttpClientResponseException.class, e -> {
//                    String responseBody = e.getResponse().getBody(String.class).orElse("No response body");
//                    String responseHeaders = e.getResponse().getHeaders().toString();
//                    int statusCode = e.getStatus().getCode();
//
//                    String errorMessage = String.format(
//                            "HttpClientResponseException: %s\nStatus Code: %d\nResponse Body: %s\nResponse Headers: %s",
//                            e.getMessage(), statusCode, responseBody, responseHeaders
//                    );
//
//                    String message = String.join(": ", "Unexpected Error Calling API", errorMessage);
//                    throw new HtmlStatusException(e.getStatus(), message);
//                }).map(estimates -> Map.of("estimates", estimates));
//    }
}
