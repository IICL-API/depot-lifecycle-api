package depotlifecycle.domain.repair;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "information for a specific unit on a work order", requiredProperties = {"unitNumber", "effectiveInspectionCriteria", "status"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class WorkOrderUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "The estimate number that initiated this unit on this work order", example = "DEHAMCE1856373", minLength = 1, maxLength = 16, required = false, nullable = true)
    @Column
    String estimateNumber;

    @Schema(description = "the unit number of the shipping container at the time of repair approval", pattern = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", required = true, nullable = false, example = "CONU1234561", minLength = 10, maxLength = 11)
    @Column(nullable = false, length = 11)
    String unitNumber;

    @Schema(description = "repair the shipping container to this grade or category standard", required = true, nullable = false, example = "CWCA-1", minLength = 1, maxLength = 10)
    @Column(nullable = false, length = 10)
    String effectiveInspectionCriteria;

    @Schema(description = "the unit number to remark the shipping container on repair", pattern = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", required = false, nullable = true, minLength = 10, maxLength = 11)
    @Column(length = 11)
    String remark;

    @Schema(description = "the Release approved for gate out after repair", required = false, nullable = true, minLength = 1, maxLength = 16)
    @Column(length = 16)
    String releaseNumber;

    @Schema(description = "an indicator of the unit state on this work order\n\n`TIED` - shipping container is considered under repair on this work order\n\n`REMOVED` - shipping container is removed from this work order\n\n`REPAIRED` - shipping container is considered repaired", required = true, nullable = false, example = "TIED", implementation = WorkOrderUnitStatus.class)
    @Column(nullable = false, length = 8)
    WorkOrderUnitStatus status;

    @ArraySchema(schema = @Schema(implementation = WorkOrderUnitApproval.class))
    @Schema(description = "*Field is currently proposed to be added - not currently production approved.*\n\nwhen multiple estimates are approved for this unit, this represents a breakdown of approval amounts by specific estimate type (estimate type and upgrade type combinations). When empty or null, the unit uses the work order's single estimate context or does not have an estimate.", required = false, nullable = false)
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    List<WorkOrderUnitApproval> approvals = new ArrayList<>();
}
