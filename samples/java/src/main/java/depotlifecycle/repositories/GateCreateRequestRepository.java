package depotlifecycle.repositories;

import depotlifecycle.domain.GateCreateRequest;
import depotlifecycle.domain.GateRequestType;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import jakarta.validation.constraints.NotNull;

@Repository
public interface GateCreateRequestRepository extends CrudRepository<GateCreateRequest, Long> {
    boolean existsByAdviceNumberAndUnitNumberAndType(@NotNull @NonNull String adviceNumber, @NotNull @NonNull String unitNumber, @NotNull @NonNull GateRequestType type);
}
