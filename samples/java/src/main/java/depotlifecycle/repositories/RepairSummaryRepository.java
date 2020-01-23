package depotlifecycle.repositories;

import depotlifecycle.domain.WorkOrder;
import io.micronaut.data.repository.CrudRepository;

public interface RepairSummaryRepository extends CrudRepository<WorkOrder, String> {
}
