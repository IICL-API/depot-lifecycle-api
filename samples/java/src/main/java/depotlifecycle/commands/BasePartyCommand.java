package depotlifecycle.commands;

import depotlifecycle.domain.BaseParty;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Introspected
public abstract class BasePartyCommand {
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
