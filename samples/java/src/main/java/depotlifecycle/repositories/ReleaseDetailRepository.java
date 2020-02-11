package depotlifecycle.repositories;

import depotlifecycle.domain.ReleaseDetail;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface ReleaseDetailRepository extends CrudRepository<ReleaseDetail, String> {
}
