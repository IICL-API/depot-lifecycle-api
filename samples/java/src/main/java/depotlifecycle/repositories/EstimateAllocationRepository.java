package depotlifecycle.repositories;

import depotlifecycle.domain.EstimateAllocation;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface EstimateAllocationRepository extends CrudRepository<EstimateAllocation, Long> {
}
