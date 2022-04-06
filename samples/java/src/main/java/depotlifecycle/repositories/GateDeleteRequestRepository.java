package depotlifecycle.repositories;

import depotlifecycle.domain.GateDeleteRequest;
import depotlifecycle.domain.Party;
import io.micronaut.data.annotation.Repository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Repository
public interface GateDeleteRequestRepository extends CrudRepository<GateDeleteRequest, Long> {
    boolean existsByDepotAndAdviceNumberAndUnitNumber(@NotNull @NonNull Party depot, @NotNull @NonNull String adviceNumber, @NotNull @NonNull String unitNumber);
}
