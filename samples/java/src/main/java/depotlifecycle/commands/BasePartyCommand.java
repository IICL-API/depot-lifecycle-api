package depotlifecycle.commands;

import depotlifecycle.domain.BaseParty;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Introspected
public abstract class BasePartyCommand {
    @Nullable
    @NotBlank
    String userCode;

    @Nullable
    @NotBlank
    String userName;

    @Nullable
    List<String> faxNumber;

    @Nullable
    List<String> phoneNumber;

    @Nullable
    List<String> emailAddress;

    @Nullable
    @NotBlank
    @Size(max = 150)
    String name;

    @Nullable
    List<String> streetAddress;

    @Nullable
    @NotBlank
    @Size(max = 28)
    String city;

    @Nullable
    @NotBlank
    @Size(max = 2)
    String country;

    @Nullable
    @NotBlank
    @Size(max = 20)
    String postalCode;

    @Nullable
    @NotBlank
    @Size(max = 20)
    String stateProvince;

    @Nullable
    @NotBlank
    @Size(max = 14)
    String licensePlate;

    @Nullable
    BigDecimal latitude;

    @Nullable
    BigDecimal longitude;

    protected <T extends BaseParty> void fillParty(T party) {
        party.setUserCode(userCode);
        party.setUserName(userName);
        party.setFaxNumber(faxNumber);
        party.setPhoneNumber(phoneNumber);
        party.setEmailAddress(emailAddress);
        party.setName(name);
        party.setStreetAddress(streetAddress);
        party.setCity(city);
        party.setCountry(country);
        party.setPostalCode(postalCode);
        party.setStateProvince(stateProvince);
        party.setLicensePlate(licensePlate);
        party.setLatitude(latitude);
        party.setLongitude(longitude);
    }
}
