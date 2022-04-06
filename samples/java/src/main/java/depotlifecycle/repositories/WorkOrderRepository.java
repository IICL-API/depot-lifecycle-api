package depotlifecycle.repositories;

import depotlifecycle.domain.WorkOrder;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Repository
public interface WorkOrderRepository extends CrudRepository<WorkOrder, Long> {
    boolean existsByWorkOrderNumber(@NotNull @NonNull String workOrderNumber);

    @NonNull
    Optional<WorkOrder> findByWorkOrderNumber(@NotNull @NonNull String workOrderNumber);
}
