package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "Indicates the allocated estimate totals, including any comments on the allocation and if the box is considered a constructive loss.", requiredProperties = {"estimateNumber", "revision", "depot", "total", "ownerTotal", "customerTotal", "insuranceTotal", "ctl"}, accessMode = Schema.AccessMode.READ_ONLY)
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class EstimateAllocation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "*Field is currently proposed to be added - not currently production approved.*\n\nAn internal system identifier to be used to upload Estimate Photos.", type = "integer", format = "int64", example = "10102561", required = false)
    Long id;

    @Schema(description = "The estimate number this allocation applies to", example = "DEHAMCE1856373", minLength = 1, maxLength = 16, required = true)
    @Column(nullable = false, length = 16)
    String estimateNumber;

    @Schema(description = "the revision number of the estimate this allocation applies to", type = "integer", format = "int32", example = "0", required = true)
    @Column(nullable = false)
    Integer revision;

    @Schema(description = "the location of the estimate", required = true)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "depot_id", nullable = false)
    Party depot;

    @Schema(required = true, type = "number", format = "double", minimum = "0.0", example = "544.95", description = "the total for the estimate")
    @Column(nullable = false)
    BigDecimal total;

    @Schema(required = true, type = "number", format = "double", minimum = "0.0", example = "0.00", description = "the total of the owner portion for the estimate")
    @Column(nullable = false)
    BigDecimal ownerTotal;

    @Schema(required = true, type = "number", format = "double", minimum = "0.0", example = "422.96", description = "the total of the customer portion for the estimate")
    @Column(nullable = false)
    BigDecimal customerTotal;

    @Schema(required = true, type = "number", format = "double", minimum = "0.0", example = "121.99", description = "the total of the insurance portion for the estimate")
    @Column(nullable = false)
    BigDecimal insuranceTotal;

    @Schema(required = true, type = "boolean", description = "indicates if the estimate causes the unit to be a constructive total lost", example = "false")
    @Column(nullable = false)
    Boolean ctl;

    @Schema(maxLength = 500, required = false, description = "comments pertaining to the estimate creation", example = "Base Currency is: EUR; Based on estimate, user damages total: 0.00; Calculated DV (base): 1151.84; Coverage amount: 121.99; Damage exceeds DPP Coverage.; Damages exceed coverage amount. User pays excess.; Total paid by DPP Coverage: 121.99; Total owed by user: 422.96;")
    @Column(length = 500)
    String comments;

    @Schema(required = false, type = "object", description = "when possible, this is set to an expected sell/fix decision to indicate the likely estimate owner approval action")
    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    PreliminaryDecision preliminaryDecision;
}
