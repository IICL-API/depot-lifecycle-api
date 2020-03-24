package depotlifecycle.repositories;

import depotlifecycle.domain.Party;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public abstract class PartyRepository implements CrudRepository<Party, Long> {
}
