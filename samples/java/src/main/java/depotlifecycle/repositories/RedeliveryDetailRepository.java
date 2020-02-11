package depotlifecycle.repositories;

import depotlifecycle.domain.RedeliveryDetail;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface RedeliveryDetailRepository extends CrudRepository<RedeliveryDetail, String> {
}
