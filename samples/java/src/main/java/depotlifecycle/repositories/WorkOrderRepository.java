package depotlifecycle.repositories;

import depotlifecycle.domain.WorkOrder;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface WorkOrderRepository extends CrudRepository<WorkOrder, String> {
}
