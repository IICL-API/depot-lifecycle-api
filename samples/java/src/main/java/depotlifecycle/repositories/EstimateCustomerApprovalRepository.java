package depotlifecycle.repositories;

import depotlifecycle.domain.EstimateCustomerApproval;
import depotlifecycle.domain.EstimateLineItem;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface EstimateCustomerApprovalRepository extends CrudRepository<EstimateCustomerApproval, Long> {
}
