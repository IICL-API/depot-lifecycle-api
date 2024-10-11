package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonView
@NoArgsConstructor
@Introspected
@MappedSuperclass
public abstract class BaseParty {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "the user identifier at this company that concerns this message", example = "JDOE", maxLength = 16, required = false, nullable = true)
    @Column(length = 16)
    String userCode;

    @Schema(description = "the full name for the user identified by `userCode`", example = "John Doe", maxLength = 70, required = false, nullable = true)
    @Column(length = 70)
    String userName;

    @Schema(description = "the contact fax number(s) for this party", required = false, nullable = false)
    @Lob
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable
    List<String> faxNumber;

    @Schema(description = "the contact phone number(s) for this party", required = false, nullable = false)
    @Lob
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable
    List<String> phoneNumber;

    @Schema(description = "the contact email address(es) for this party", required = false, nullable = false)
    @Lob
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable
    List<String> emailAddress;

    @Schema(description = "the name of this company", example = "CMR Container Maintenance Rep.", maxLength = 150, required = false, nullable = true)
    @Column(length = 150)
    String name;

    // Address Information
    @Schema(description = "the street address lines", required = false, nullable = false)
    @Lob
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable
    List<String> streetAddress;

    @Schema(description = "the city for the address", maxLength = 28, required = false, nullable = true)
    @Column(length = 28)
    String city;

    @Schema(description = "the 2 digit ISO country code for the address", maxLength = 2, required = false, nullable = true)
    @Column(length = 2)
    String country;

    @Schema(description = "the postal code for the address", maxLength = 20, required = false, nullable = true)
    @Column(length = 20)
    String postalCode;

    @Schema(description = "the optional state or province code for the address", maxLength = 20, required = false, nullable = true)
    @Column(length = 20)
    String stateProvince;

    @Schema(type = "number", format = "double", description = "Instead of an address, provides the latitude of this party", required = false, nullable = true)
    @Column
    BigDecimal latitude;

    @Schema(type = "number", format = "double", description = "Instead of an address, provides the longitude of this party", required = false, nullable = true)
    @Column
    BigDecimal longitude;
}
