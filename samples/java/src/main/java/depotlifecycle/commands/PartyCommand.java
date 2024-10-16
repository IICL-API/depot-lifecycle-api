package depotlifecycle.commands;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.Party;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonView
@Introspected
public class PartyCommand {
    @NotNull
    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]{9}$", message = "CompanyId must be a valid EDI Address.")
    String companyId;

    @Nullable
    String userCode;

    @Nullable
    String userName;

    @Nullable
    List<String> faxNumber;

    @Nullable
    List<String> phoneNumber;

    @Nullable
    List<String> emailAddress;

    @Nullable
    @Size(max = 150)
    String name;

    @Nullable
    @Size(max = 10)
    String code;

    @Nullable
    List<String> streetAddress;

    @Nullable
    @Size(max = 28)
    String city;

    @Nullable
    @Size(max = 2)
    String country;

    @Nullable
    @Size(max = 20)
    String postalCode;

    @Nullable
    @Size(max = 20)
    String stateProvince;

    @Nullable
    BigDecimal latitude;

    @Nullable
    BigDecimal longitude;

    @JsonIgnore
    public Party toParty() {
        Party party = new Party();
        party.setCompanyId(companyId);
        party.setUserCode(userCode);
        party.setUserName(userName);
        party.setFaxNumber(faxNumber);
        party.setPhoneNumber(phoneNumber);
        party.setEmailAddress(emailAddress);
        party.setName(name);
        party.setCode(code);
        party.setStreetAddress(streetAddress);
        party.setCity(city);
        party.setCountry(country);
        party.setPostalCode(postalCode);
        party.setStateProvince(stateProvince);
        party.setLatitude(latitude);
        party.setLongitude(longitude);
        return party;
    }
}
