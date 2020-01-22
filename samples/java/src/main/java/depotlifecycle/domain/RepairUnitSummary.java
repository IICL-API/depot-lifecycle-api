package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "information for a specific unit on a work order", requiredProperties = {"unitNumber", "effectiveInspectionCriteria"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class RepairUnitSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "the unit number of the shipping container at the time of repair approval", pattern = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", required = true, example = "CONU1234561", maxLength = 11)
    @Column(nullable = false, length = 11)
    String unitNumber;

    @Schema(description = "repair the shipping container to this grade or category standard", required = true, example = "CWCA-1", maxLength = 10)
    @Column(nullable = false, length = 10)
    String effectiveInspectionCriteria;

    @Schema(description = "the unit number to remark the shipping container on repair", pattern = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", required = false, maxLength = 11)
    @Column(length = 11)
    String remark;

    @Schema(description = "the Release approved for gate out after repair", required = false, maxLength = 16)
    @Column(length = 16)
    String releaseNumber;
}
