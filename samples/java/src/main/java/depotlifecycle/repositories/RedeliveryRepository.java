package depotlifecycle.repositories;

import depotlifecycle.domain.Redelivery;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;

@Repository
public interface RedeliveryRepository extends CrudRepository<Redelivery, Long> {
    boolean existsByRedeliveryNumber(@NotNull @NonNull String redeliveryNumber);

    @NonNull
    Optional<Redelivery> findByRedeliveryNumber(@NotNull @NonNull String redeliveryNumber);
}
