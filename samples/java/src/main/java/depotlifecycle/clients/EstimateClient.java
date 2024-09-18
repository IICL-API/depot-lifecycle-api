package depotlifecycle.clients;

import depotlifecycle.DepotLifecycleConfiguration;
import depotlifecycle.domain.Estimate;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.reactivestreams.Publisher;

import java.util.List;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.USER_AGENT;

@Client(configuration = DepotLifecycleConfiguration.class, value = "${" + DepotLifecycleConfiguration.PREFIX + ".url}")
@Header(name = USER_AGENT, value = "Micronaut HTTP Client")
@Header(name = ACCEPT, value = "application/json")
public interface EstimateClient {
    @Header(name = "Authorization", value = "${" + DepotLifecycleConfiguration.PREFIX + ".authorization}")
    @Get("/api/v2/estimate")
    Publisher<List<Estimate>> search(@Nullable @QueryValue String estimateNumber, @Nullable @QueryValue String unitNumber,
                                     @Nullable @QueryValue String depot, @Nullable @QueryValue String lessee,
                                     @Nullable @QueryValue Integer revision, @Nullable @QueryValue String equipmentCode);

    @Header(name = "Authorization", value = "${" + DepotLifecycleConfiguration.PREFIX + ".authorization}")
    @Get("/api/v2/estimate/{estimateNumber}")
    Publisher<Estimate> get(@PathVariable @NotNull @NonNull String estimateNumber);

}
