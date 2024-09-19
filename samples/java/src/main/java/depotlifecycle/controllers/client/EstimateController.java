package depotlifecycle.controllers.client;

import depotlifecycle.DepotLifecycleConfiguration;
import depotlifecycle.clients.EstimateClient;
import depotlifecycle.commands.*;
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
@Controller("/client/estimate")
@RequiredArgsConstructor
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
@Hidden
public class EstimateController {
    private static final Logger LOG = LoggerFactory.getLogger(EstimateController.class);
    private final DepotLifecycleConfiguration projectConfig;
    private final Validator validator;
    private final EstimateClient estimateClient;

    @View("estimate")
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

    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post("/updateTotals")
    @View("estimateMessage")
    Mono<Map<String, Object>> updateTotals(@Body EstimateTotalsCommand cmd) {
        LOG.info("Client - Estimate - Update Totals");

        ClientErrorHandling.validate(cmd, validator);

        estimateClient.update(cmd.getEstimateNumber(), cmd);

        return Mono.just(Map.of("title", "Estimate Totals", "message", "Totals Updated"));
    }

    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post("/customerApprove")
    @View("estimateAllocationList")
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

    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post("/create")
    @View("estimateAllocationList")
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
}
