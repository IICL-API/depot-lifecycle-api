package depotlifecycle.controllers.client.inventory;

import depotlifecycle.DepotLifecycleConfiguration;
import depotlifecycle.clients.inventory.InventoryClient;
import depotlifecycle.commands.inventory.InventoryFetchCommand;
import depotlifecycle.domain.inventory.InventorySnapshot;
import depotlifecycle.domain.inventory.InventoryStatus;
import depotlifecycle.system.ClientErrorHandling;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
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

import java.util.Map;

@Validated
@Controller("/client/inventory")
@RequiredArgsConstructor
@Produces(MediaType.TEXT_HTML)
@Hidden
public class InventoryController {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryController.class);
    private final DepotLifecycleConfiguration projectConfig;
    private final Validator validator;
    private final InventoryClient inventoryClient;

    @Consumes(MediaType.ALL)
    @View("inventory/index")
    @Get
    Mono<Map<String, Object>> index() {
        Map<String, Object> model = Map.ofEntries(
                Map.entry("projectConfig", projectConfig),
                Map.entry("inventoryStatuses", InventoryStatus.values())
        );
        return Mono.just(model);
    }

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @ExecuteOn(TaskExecutors.BLOCKING)
    @Post("/fetch")
    @View("inventory/inventoryList")
    Mono<Map<String, Object>> fetch(@Body InventoryFetchCommand cmd) {
        LOG.info("Client - Inventory - Fetch");

        ClientErrorHandling.validate(cmd, validator);

        Publisher<InventorySnapshot> snapshotPublisher = inventoryClient.index(cmd.getDepot(), cmd.getUnitNumber(), cmd.getStatus(), cmd.getStatusChangedAfter());

        return Mono.from(snapshotPublisher)
                .onErrorMap(HttpClientResponseException.class, ClientErrorHandling::handleError)
                .map(snapshot -> Map.of("snapshot", snapshot));
    }
}
