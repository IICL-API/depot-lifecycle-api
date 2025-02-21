package depotlifecycle.repositories.gate;

import depotlifecycle.domain.gate.GateUpdateRequest;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface GateUpdateRequestRepository extends CrudRepository<GateUpdateRequest, Long> {
}
