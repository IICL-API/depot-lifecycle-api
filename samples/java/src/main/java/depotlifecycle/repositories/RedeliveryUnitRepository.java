package depotlifecycle.repositories;

import depotlifecycle.domain.RedeliveryUnit;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface RedeliveryUnitRepository extends CrudRepository<RedeliveryUnit, String> {
}
