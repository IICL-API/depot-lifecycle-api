package depotlifecycle.repositories;

import depotlifecycle.domain.EstimateCancelRequest;
import depotlifecycle.domain.Party;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.constraints.NotNull;

@Repository
public interface EstimateCancelRequestRepository extends CrudRepository<EstimateCancelRequest, Long> {
    boolean existsByEstimateNumberAndDepot(@NotNull @NonNull String estimateNumber, @NotNull @NonNull Party depot);
}
