package depotlifecycle.repositories.gate;

import depotlifecycle.domain.gate.GateCreateRequest;
import depotlifecycle.domain.gate.GateRequestType;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import jakarta.validation.constraints.NotNull;

@Repository
public interface GateCreateRequestRepository extends CrudRepository<GateCreateRequest, Long> {
    boolean existsByAdviceNumberAndUnitNumberAndType(@NotNull @NonNull String adviceNumber, @NotNull @NonNull String unitNumber, @NotNull @NonNull GateRequestType type);
}
