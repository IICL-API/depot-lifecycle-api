package depotlifecycle.commands;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.Party;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@JsonView
@Introspected
@EqualsAndHashCode(callSuper=true)
public class PartyCommand extends BasePartyCommand {
    @Nonnull
    @Pattern(regexp = "^[A-Z0-9]{9}$", message = "CompanyId must be a valid EDI Address.")
    String companyId;

    @Nullable
    @NotBlank
    @Size(max = 10)
    String code;

    @JsonIgnore
    public Party toParty() {
        Party party = new Party();
        party.setCompanyId(companyId);
        party.setCode(code);
        fillParty(party);
        return party;
    }
}
