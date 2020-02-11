package depotlifecycle.repositories;

import depotlifecycle.domain.Party;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface PartyRepository extends CrudRepository<Party, String> {
}
