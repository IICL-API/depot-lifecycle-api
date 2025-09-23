package depotlifecycle.commands;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.ExternalParty;
import depotlifecycle.domain.Party;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonView
@Introspected
public class ExternalPartyCommand extends BasePartyCommand {
    @Nullable
    @Pattern(regexp = "^[A-Z0-9]{9}$", message = "CompanyId must be a valid EDI Address.")
    String companyId;

    @Nullable
    @NotBlank
    @Size(max = 10)
    String code;

    @JsonIgnore
    public ExternalParty toExternalParty() {
        ExternalParty party = new ExternalParty();
        party.setCompanyId(companyId);
        party.setCode(code);
        fillParty(party);
        return party;
    }
}
