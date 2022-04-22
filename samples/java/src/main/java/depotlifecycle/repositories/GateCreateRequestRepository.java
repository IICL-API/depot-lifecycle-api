package depotlifecycle.repositories;

import depotlifecycle.domain.GateCreateRequest;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.constraints.NotNull;

@Repository
public interface GateCreateRequestRepository extends CrudRepository<GateCreateRequest, Long> {
    boolean existsByAdviceNumberAndUnitNumberAndType(@NotNull @NonNull String adviceNumber, @NotNull @NonNull String unitNumber, @NotNull @NonNull String type);
}
