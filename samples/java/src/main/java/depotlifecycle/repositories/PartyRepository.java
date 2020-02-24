package depotlifecycle.repositories;

import depotlifecycle.domain.Party;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Repository
public abstract class PartyRepository implements CrudRepository<Party, String> {
    public Party saveOrUpdate(@Valid @NotNull @NonNull Party entity) {
        Optional<Party> existing = findById(entity.getCompanyId());
        if (existing.isPresent()) {
            return update(entity);
        }
        else {
            return save(entity);
        }
    }
}
