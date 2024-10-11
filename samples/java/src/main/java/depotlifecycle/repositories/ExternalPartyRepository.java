package depotlifecycle.repositories;

import depotlifecycle.domain.ExternalParty;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@Repository
public interface ExternalPartyRepository extends CrudRepository<ExternalParty, Long> {
    Optional<ExternalParty> findByCompanyIdOrCode(@Nullable String companyId, @Nullable String code);
}
