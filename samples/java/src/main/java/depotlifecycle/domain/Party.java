package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "represents a company (or location) involving shipping containers", requiredProperties = {"companyId"})
@EqualsAndHashCode(of = {"companyId"})
@ToString(of = {"companyId"})
@Introspected
public class Party {

    @Schema(description = "the identifier for this party, often referred to as an EDI Address", pattern = "^[A-Z0-9]{9}$", example = "DEHAMCMRA", maxLength = 9, required = true)
    @Column(nullable = false, unique = true, length = 9)
    @Id
    String companyId;

    @Schema(description = "the user identifier at this company that concerns this message", example = "JDOE", maxLength = 16)
    @Column(length = 16)
    String userCode;

    @Schema(description = "the full name for the user identified by `userCode`", example = "John Doe", maxLength = 70)
    @Column(length = 70)
    String userName;

    @Schema(description = "the name of this company", example = "CMR Container Maintenance Rep.", maxLength = 150)
    @Column(length = 150)
    String name;

    @Schema(description = "the internal system code for this company, will be system specific to the system delivering or receiving this message", example = "HAMG", maxLength = 10)
    @Column(length = 10)
    String code;
}
