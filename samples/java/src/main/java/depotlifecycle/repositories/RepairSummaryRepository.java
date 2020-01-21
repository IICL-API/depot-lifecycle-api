package depotlifecycle.repositories;

import depotlifecycle.domain.RepairSummary;
import io.micronaut.data.repository.CrudRepository;

public interface RepairSummaryRepository extends CrudRepository<RepairSummary, String> {
}
