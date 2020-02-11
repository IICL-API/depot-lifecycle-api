package depotlifecycle.repositories;

import depotlifecycle.domain.Release;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface ReleaseRepository extends CrudRepository<Release, String> {
}
