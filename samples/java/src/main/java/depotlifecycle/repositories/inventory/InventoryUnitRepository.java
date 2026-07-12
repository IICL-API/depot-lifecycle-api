package depotlifecycle.repositories.inventory;

import depotlifecycle.domain.Party;
import depotlifecycle.domain.inventory.InventoryUnit;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryUnitRepository extends CrudRepository<InventoryUnit, Long> {
    @NonNull
    List<InventoryUnit> findByDepot(@NotNull @NonNull Party depot);

    @NonNull
    Optional<InventoryUnit> findByDepotAndUnitNumber(@NotNull @NonNull Party depot, @NotNull @NonNull String unitNumber);
}
