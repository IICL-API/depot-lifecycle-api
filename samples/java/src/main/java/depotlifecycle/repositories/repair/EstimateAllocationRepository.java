package depotlifecycle.repositories.repair;

import depotlifecycle.domain.repair.EstimateAllocation;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface EstimateAllocationRepository extends CrudRepository<EstimateAllocation, Long> {
}
