package depotlifecycle.repositories;

import depotlifecycle.domain.Estimate;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface EstimateRepository extends CrudRepository<Estimate, Long> {
}
