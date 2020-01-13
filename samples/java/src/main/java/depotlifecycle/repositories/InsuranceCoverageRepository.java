package depotlifecycle.repositories;

import depotlifecycle.domain.InsuranceCoverage;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface InsuranceCoverageRepository extends CrudRepository<InsuranceCoverage, String> {
}
