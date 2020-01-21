package depotlifecycle.repositories;

import depotlifecycle.domain.EstimateLineItem;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface EstimateLineItemRepository extends CrudRepository<EstimateLineItem, Long> {
}
