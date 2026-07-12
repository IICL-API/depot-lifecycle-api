package depotlifecycle.clients.inventory;

import depotlifecycle.DepotLifecycleConfiguration;
import depotlifecycle.ErrorResponse;
import depotlifecycle.domain.inventory.InventorySnapshot;
import depotlifecycle.domain.inventory.InventoryStatus;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import org.reactivestreams.Publisher;

import java.time.ZonedDateTime;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.USER_AGENT;

@Client(configuration = DepotLifecycleConfiguration.class, value = "${" + DepotLifecycleConfiguration.PREFIX + ".url}", errorType = ErrorResponse.class)
@Header(name = USER_AGENT, value = "Micronaut HTTP Client")
@Header(name = ACCEPT, value = "application/json")
public interface InventoryClient {
    @Header(name = "Authorization", value = "${" + DepotLifecycleConfiguration.PREFIX + ".authorization}")
    @Get("/api/v2/inventory/{depot}{?unitNumber,status,statusChangedAfter}")
    Publisher<InventorySnapshot> index(@PathVariable @NonNull String depot,
                                       @Nullable @QueryValue String unitNumber,
                                       @Nullable @QueryValue InventoryStatus status,
                                       @Nullable @QueryValue ZonedDateTime statusChangedAfter);
}
