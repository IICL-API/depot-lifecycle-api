package depotlifecycle.repositories.repair;

import depotlifecycle.domain.repair.WorkOrderUnit;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface WorkOrderUnitRepository extends CrudRepository<WorkOrderUnit, Long> {
}
