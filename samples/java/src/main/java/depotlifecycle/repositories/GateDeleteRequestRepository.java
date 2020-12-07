package depotlifecycle.repositories;

import depotlifecycle.domain.GateDeleteRequest;
import io.micronaut.data.annotation.Repository;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Repository
public interface GateDeleteRequestRepository extends CrudRepository<GateDeleteRequest, Long> {
    boolean existsByAdviceNumberAndUnitNumberAndType(@NotNull @NonNull String adviceNumber, @NotNull @NonNull String unitNumber, @NotNull @NonNull String type, @NotNull @NonNull ZonedDateTime activityTime);
}
