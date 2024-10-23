package depotlifecycle.repositories;

import depotlifecycle.domain.Estimate;
import depotlifecycle.domain.Party;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Repository
public interface EstimateRepository extends CrudRepository<Estimate, Long> {
    boolean existsByEstimateNumberAndDepot(@NotNull @NonNull String estimateNumber, @NotNull @NonNull Party depot);

    Estimate findByEstimateNumberAndDepot(@NotNull @NonNull String estimateNumber, @NotNull @NonNull Party depot);

    @Query("SELECT e FROM Estimate e WHERE " +
            "(:estimateNumber IS NULL OR e.estimateNumber = :estimateNumber) AND " +
            "(:depot IS NULL OR e.depot = :depot) AND " +
            "(:unitNumber IS NULL OR e.unitNumber = :unitNumber) AND " +
            "(:customer IS NULL OR e.customer = :customer) AND " +
            "(:revision IS NULL OR e.revision = :revision)")
    List<Estimate> searchEstimates(@Nullable String estimateNumber, @Nullable Party depot, @Nullable String unitNumber, @Nullable Party customer, @Nullable Integer revision, Pageable pageable);
}
