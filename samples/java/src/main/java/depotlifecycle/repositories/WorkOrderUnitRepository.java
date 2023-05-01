package depotlifecycle.repositories;

import depotlifecycle.domain.WorkOrderUnit;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface WorkOrderUnitRepository extends CrudRepository<WorkOrderUnit, Long> {
}
