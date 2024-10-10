package depotlifecycle.clients;

import depotlifecycle.DepotLifecycleConfiguration;
import depotlifecycle.ErrorResponse;
import depotlifecycle.commands.EstimateCreateCommand;
import depotlifecycle.commands.EstimateTotalsCommand;
import depotlifecycle.domain.Estimate;
import depotlifecycle.domain.EstimateAllocation;
import depotlifecycle.domain.EstimateCustomerApproval;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.reactivestreams.Publisher;

import java.util.List;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.USER_AGENT;

@Client(configuration = DepotLifecycleConfiguration.class, value = "${" + DepotLifecycleConfiguration.PREFIX + ".url}", errorType = ErrorResponse.class)
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
    Publisher<Estimate> get(@PathVariable @NonNull String estimateNumber, @QueryValue @NonNull String depot, @QueryValue @Nullable Integer revision);

    @Header(name = "Authorization", value = "${" + DepotLifecycleConfiguration.PREFIX + ".authorization}")
    @Delete("/api/v2/estimate/{estimateNumber}")
    void delete(@PathVariable @NonNull String estimateNumber, @QueryValue @NonNull String depot);

    @Header(name = "Authorization", value = "${" + DepotLifecycleConfiguration.PREFIX + ".authorization}")
    @Put("/api/v2/estimate/{estimateNumber}")
    Publisher<EstimateAllocation> customerApprove(@PathVariable @NonNull String estimateNumber, @QueryValue @NonNull String depot, @RequestBody @NonNull EstimateCustomerApproval customerApproval);

    @Header(name = "Authorization", value = "${" + DepotLifecycleConfiguration.PREFIX + ".authorization}")
    @Post("/api/v2/estimate")
    Publisher<EstimateAllocation> create(@NonNull @Body EstimateCreateCommand cmd);

    @Header(name = "Authorization", value = "${" + DepotLifecycleConfiguration.PREFIX + ".authorization}")
    @Patch("/api/v2/estimate/{estimateNumber}")
    void update(@PathVariable @NonNull String estimateNumber, @NonNull @Body EstimateTotalsCommand cmd);
}
