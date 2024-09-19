package depotlifecycle.clients;

import depotlifecycle.DepotLifecycleConfiguration;
import depotlifecycle.ErrorResponse;
import depotlifecycle.GateResponse;
import depotlifecycle.GateStatus;
import depotlifecycle.domain.GateCreateRequest;
import depotlifecycle.domain.GateUpdateRequest;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;
import org.reactivestreams.Publisher;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.USER_AGENT;

@Client(configuration = DepotLifecycleConfiguration.class, value = "${" + DepotLifecycleConfiguration.PREFIX + ".url}", errorType = ErrorResponse.class)
@Header(name = USER_AGENT, value = "Micronaut HTTP Client")
@Header(name = ACCEPT, value = "application/json")
public interface GateClient {
    @Header(name = "Authorization", value = "${" + DepotLifecycleConfiguration.PREFIX + ".authorization}")
    @Get("/api/v2/gate/{unitNumber}")
    Publisher<GateStatus> get(@PathVariable @NonNull String unitNumber);

    @Header(name = "Authorization", value = "${" + DepotLifecycleConfiguration.PREFIX + ".authorization}")
    @Put("/api/v2/gate/{depot}/{adviceNumber}/{unitNumber}")
    Publisher<GateResponse> update(@PathVariable @NonNull String depot, @PathVariable @NonNull String adviceNumber, @PathVariable @NonNull String unitNumber, @Body @NonNull GateUpdateRequest gateUpdateRequest);

    @Header(name = "Authorization", value = "${" + DepotLifecycleConfiguration.PREFIX + ".authorization}")
    @Post("/api/v2/gate")
    Publisher<GateResponse> create(@Body @NonNull GateCreateRequest gateUpdateRequest);

    @Header(name = "Authorization", value = "${" + DepotLifecycleConfiguration.PREFIX + ".authorization}")
    @Delete("/api/v2/gate/{depot}/{adviceNumber}/{unitNumber}")
    void delete(@PathVariable @NonNull String depot, @PathVariable @NonNull String adviceNumber, @PathVariable @NonNull String unitNumber);
}
