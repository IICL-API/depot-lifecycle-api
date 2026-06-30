package depotlifecycle.controllers.client.repair;

import depotlifecycle.DepotLifecycleConfiguration;
import depotlifecycle.PendingResponse;
import depotlifecycle.clients.repair.EstimateClient;
import depotlifecycle.commands.repair.*;
import depotlifecycle.domain.repair.*;
import depotlifecycle.system.ClientErrorHandling;
import io.micronaut.core.annotation.Nullable;
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
import java.util.Map;

@Validated
@Controller("/client/estimate")
@RequiredArgsConstructor
@Produces(MediaType.TEXT_HTML)
@Hidden
public class EstimateController {
    private static final Logger LOG = LoggerFactory.getLogger(EstimateController.class);
    private final DepotLifecycleConfiguration projectConfig;
    private final Validator validator;
    private final EstimateClient estimateClient;

    @Consumes(MediaType.ALL)
    @View("repair/index")
    @Get
    Mono<Map<String, Object>> index() {
        Map<String, Object> model = Map.ofEntries(
                Map.entry("projectConfig", projectConfig),
                Map.entry("conditions", EstimateCondition.values()),
                Map.entry("upgradeTypes", UpgradeType.values()),
                Map.entry("estimateTypes", EstimateType.values())
        );
        return Mono.just(model);
    }

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post("/updateTotals")
    @View("estimateMessage")
    Mono<Map<String, Object>> updateTotals(@Body EstimateTotalsCommand cmd) {
        LOG.info("Client - Estimate - Update Totals");

        ClientErrorHandling.validate(cmd, validator);

        estimateClient.update(cmd.getEstimateNumber(), cmd);

        return Mono.just(Map.of("title", "Estimate Totals", "message", "Totals Updated"));
    }

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post("/customerApprove")
    @View("repair/estimateAllocationList")
    Mono<Map<String, Object>> customerApprove(@Body EstimateCustomerApproveCommand cmd) {
        LOG.info("Client - Estimate - Customer Approve");

        ClientErrorHandling.validate(cmd, validator);

        EstimateCustomerApproval customerApproval = new EstimateCustomerApproval();
        customerApproval.setApprovalNumber(cmd.getApprovalNumber());
        customerApproval.setApprovalDateTime(cmd.getApprovalDateTime());
        customerApproval.setApprovalUser(cmd.getApprovalUser());
        customerApproval.setApprovalTotal(cmd.getApprovalTotal());
        Publisher<EstimateAllocation> estimatePublisher = estimateClient.customerApprove(cmd.getEstimateNumber(), cmd.getDepot(), customerApproval);

        return Mono.from(estimatePublisher)
                .onErrorMap(HttpClientResponseException.class, ClientErrorHandling::handleError)
                .map(estimate -> Map.of("estimates", List.of(estimate)));
    }

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post("/cancel")
    @View("estimateMessage")
    Mono<Map<String, Object>> delete(@Body EstimateCancelCommand cmd) {
        LOG.info("Client - Estimate - Cancel");

        ClientErrorHandling.validate(cmd, validator);

        try {
            estimateClient.delete(cmd.getEstimateNumber(), cmd.getDepot());
        } catch (HttpClientResponseException e) {
            throw ClientErrorHandling.handleError(e);
        }

        Map<String, Object> results = new HashMap<>();
        results.put("title", "Estimate Cancel Results");
        results.put("message", "Estimate Deleted");
        return Mono.just(results);
    }

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post("/create")
    @View("repair/estimateAllocationList")
    Mono<Map<String, Object>> create(@Body EstimateCreateCommand cmd) {
        LOG.info("Client - Estimate - Create");

        ClientErrorHandling.validate(cmd, validator);

        Publisher<EstimateAllocation> estimatePublisher = estimateClient.create(cmd);

        return Mono.from(estimatePublisher)
                .onErrorMap(HttpClientResponseException.class, ClientErrorHandling::handleError)
                .map(estimate ->
                        Map.of("estimates", List.of(estimate))
                );
    }


    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post("/fetch")
    @View("estimateList")
    Mono<Map<String, Object>> fetch(@Body EstimateFetchCommand cmd) {
        LOG.info("Client - Estimate - Fetch");

        ClientErrorHandling.validate(cmd, validator);

        Publisher<Estimate> estimatePublisher = estimateClient.get(cmd.getEstimateNumber(), cmd.getDepot(), cmd.getRevision());

        return Mono.from(estimatePublisher)
                .onErrorMap(HttpClientResponseException.class, ClientErrorHandling::handleError)
                .map(estimate -> Map.of("estimates", List.of(estimate)));
    }

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post("/list")
    @View("estimateList")
    Mono<Map<String, Object>> list(@Body EstimateSearchCommand cmd) {
        LOG.info("Client - Estimate - List");

        ClientErrorHandling.validate(cmd, validator);

        Publisher<List<Estimate>> estimatePublisher = estimateClient.search(cmd.getEstimateNumber(), cmd.getUnitNumber(), cmd.getDepot(), cmd.getLessee(), cmd.getRevision(), cmd.getEquipmentCode());

        return Mono.from(estimatePublisher)
                .onErrorMap(HttpClientResponseException.class, ClientErrorHandling::handleError)
                .map(estimates -> Map.of("estimates", estimates));
    }

    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post("/uploadPhoto")
    @View("estimateMessage")
    Mono<Map<String, Object>> uploadPhoto(@Part Long relatedId,
                                          @Nullable @Part Integer line,
                                          @Nullable @Part String status,
                                          @Part CompletedFileUpload file) {
        LOG.info("Client - Estimate - Upload Photo");

        if (file == null || file.getFilename() == null || file.getFilename().isEmpty()) {
            throw new IllegalArgumentException("Must provide a photo to upload.");
        }

        MultipartBody body;
        try {
            byte[] bytes = file.getBytes();
            if (bytes == null || bytes.length == 0) {
                throw new IllegalArgumentException("Must provide a photo to upload.");
            }
            body = MultipartBody.builder()
                    .addPart("file", file.getFilename(), file.getContentType().orElse(MediaType.APPLICATION_OCTET_STREAM_TYPE), bytes)
                    .build();
        } catch (IOException ioException) {
            throw new IllegalArgumentException("Must provide a photo to upload.");
        }

        Publisher<HttpResponse<PendingResponse>> publisher = estimateClient.uploadPhoto(relatedId, line, status, body);

        return Mono.from(publisher)
                .onErrorMap(HttpClientResponseException.class, ClientErrorHandling::handleError)
                .map(response -> {
                    Map<String, Object> results = new HashMap<>();
                    results.put("title", "Estimate Photo Upload Results");
                    if (response.getStatus() == HttpStatus.ACCEPTED) {
                        results.put("message", "Photo accepted for processing (pending manual review).");
                    } else {
                        results.put("message", "Photo uploaded successfully.");
                    }
                    return results;
                });
    }
}
