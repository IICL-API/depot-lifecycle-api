package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static jakarta.persistence.DiscriminatorType.STRING;
import static jakarta.persistence.InheritanceType.SINGLE_TABLE;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table(name = "Party")
@Inheritance(strategy=SINGLE_TABLE)
@DiscriminatorColumn(name="DISC", discriminatorType=STRING, length=20)
@Schema(description = "represents a company (or location) involving shipping containers that may not have a companyId.  Either companyId or code is required to identify the party")
@EqualsAndHashCode(of = {"companyId", "code"}, callSuper = false)
@ToString(of = {"companyId", "code"})
@Introspected
public class ExternalParty extends BaseParty {
    @Schema(description = "a possible identifier for this party, often referred to as an EDI Address", pattern = "^[A-Z0-9]{9}$", example = "DEHAMCMRA", maxLength = 9, required = false, nullable = true)
    @Column(length = 9)
    String companyId;

    @Schema(description = "a possible identifier for this party using the internal system code for this company, will be system specific to the system delivering or receiving this message", example = "HAMG", maxLength = 10, required = false, nullable = true)
    @Column(length = 10)
    String code;

    @PreUpdate
    @PrePersist
    protected void validateExternalParty() {
        if(getCompanyId() == null && getCode() == null) {
            throw new IllegalArgumentException("Either companyId or code is required to identify an external party");
        }
    }
}
