package depotlifecycle.clients;

import depotlifecycle.DepotLifecycleConfiguration;
import depotlifecycle.ErrorResponse;
import depotlifecycle.domain.Estimate;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;
import org.reactivestreams.Publisher;

import java.util.List;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.USER_AGENT;

@Client(configuration = DepotLifecycleConfiguration.class, value = "${" + DepotLifecycleConfiguration.PREFIX + ".url}", errorType = ErrorResponse.class)
@Header(name = USER_AGENT, value = "Micronaut HTTP Client")
@Header(name = ACCEPT, value = "application/json")
public interface EstimateClient {
    //@Consumes("application/json")
    @Header(name = "Authorization", value = "${" + DepotLifecycleConfiguration.PREFIX + ".authorization}")
    @Get("/api/v2/estimate")
    Publisher<List<Estimate>> search(@Nullable @QueryValue String estimateNumber, @Nullable @QueryValue String unitNumber,
                                     @Nullable @QueryValue String depot, @Nullable @QueryValue String lessee,
                                     @Nullable @QueryValue Integer revision, @Nullable @QueryValue String equipmentCode);

    //@Consumes("application/json")
    @Header(name = "Authorization", value = "${" + DepotLifecycleConfiguration.PREFIX + ".authorization}")
    @Get("/api/v2/estimate/{estimateNumber}")
    Publisher<Estimate> get(@PathVariable @NonNull String estimateNumber, @QueryValue @NonNull String depot, @QueryValue @Nullable Integer revision);

}
