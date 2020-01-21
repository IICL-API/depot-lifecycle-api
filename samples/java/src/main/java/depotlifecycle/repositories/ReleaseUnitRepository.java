package depotlifecycle.repositories;

import depotlifecycle.domain.ReleaseUnit;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface ReleaseUnitRepository extends CrudRepository<ReleaseUnit, String> {
}
