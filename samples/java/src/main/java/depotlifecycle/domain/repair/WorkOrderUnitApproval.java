package depotlifecycle.domain.repair;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "*Type is currently proposed to be added - not currently production approved.*\n\nRepresents the approval details for a specific estimate type on a work order unit. When multiple estimates are approved for a unit, each estimate type will have its own approval entry.", requiredProperties = {"allocationType", "approvalTotal"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class WorkOrderUnitApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "the estimate type and optional upgrade type combination that this approval applies to", required = true, nullable = false, implementation = EstimateAllocationType.class)
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinColumn(name = "allocationType_id", nullable = false)
    EstimateAllocationType allocationType;

    @Schema(description = "the total amount approved for this specific estimate type", required = true, nullable = false, type = "number", format = "double", example = "175.00")
    @Column(nullable = false)
    BigDecimal approvalTotal;

}
