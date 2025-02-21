package depotlifecycle.repositories.repair;

import depotlifecycle.domain.repair.EstimateCustomerApproval;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface EstimateCustomerApprovalRepository extends CrudRepository<EstimateCustomerApproval, Long> {
}
