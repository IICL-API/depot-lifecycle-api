package depotlifecycle.repositories;

import depotlifecycle.domain.Party;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;

@Repository
public interface PartyRepository extends CrudRepository<Party, Long> {
    Optional<Party> findByCompanyId(@NotNull @NonNull String companyId);
}
