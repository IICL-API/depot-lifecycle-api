package depotlifecycle.repositories.gate;

import depotlifecycle.domain.gate.GateDeleteRequest;
import depotlifecycle.domain.Party;
import io.micronaut.data.annotation.Repository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.repository.CrudRepository;

import jakarta.validation.constraints.NotNull;

@Repository
public interface GateDeleteRequestRepository extends CrudRepository<GateDeleteRequest, Long> {
    boolean existsByDepotAndAdviceNumberAndUnitNumber(@NotNull @NonNull Party depot, @NotNull @NonNull String adviceNumber, @NotNull @NonNull String unitNumber);
}
