package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Schema(description = "represents a company (or location) involving shipping containers", requiredProperties = {"companyId"})
@EqualsAndHashCode(of = {"companyId"}, callSuper = false)
@ToString(of = {"companyId"})
@Introspected
public class Party extends ExternalParty {

    @Schema(description = "the identifier for this party, often referred to as an EDI Address", pattern = "^[A-Z0-9]{9}$", example = "DEHAMCMRA", maxLength = 9, required = true, nullable = false)
    @Column(nullable = false, length = 9)
    String companyId;

    @Schema(description = "the internal system code for this company, will be system specific to the system delivering or receiving this message", example = "HAMG", maxLength = 10, required = false, nullable = true)
    @Column(length = 10)
    String code;

}
