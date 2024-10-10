package depotlifecycle.repositories;

import depotlifecycle.domain.Release;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;

@Repository
public interface ReleaseRepository extends CrudRepository<Release, Long> {
    boolean existsByReleaseNumber(@NotNull @NonNull String releaseNumber);

    @NonNull
    Optional<Release> findByReleaseNumber(@NotNull @NonNull String releaseNumber);
}
