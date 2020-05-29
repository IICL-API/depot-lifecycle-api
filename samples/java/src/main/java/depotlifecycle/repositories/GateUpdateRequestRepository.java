package depotlifecycle.repositories;

import depotlifecycle.domain.GateUpdateRequest;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface GateUpdateRequestRepository extends CrudRepository<GateUpdateRequest, Long> {
}
