package depotlifecycle.repositories;

import depotlifecycle.domain.Estimate;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.constraints.NotNull;

@Repository
public interface EstimateRepository extends CrudRepository<Estimate, Long> {
    boolean existsByEstimateNumber(@NotNull @NonNull String estimateNumber);
}
