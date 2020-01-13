package depotlifecycle.repositories;

import depotlifecycle.domain.Redelivery;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface RedeliveryRepository extends CrudRepository<Redelivery, String> {
}
