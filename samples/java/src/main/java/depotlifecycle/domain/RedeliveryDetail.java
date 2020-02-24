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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "unit criteria that groups similar units on a redelivery", requiredProperties = {"customer", "contract", "equipment", "inspectionCriteria", "billingParty", "quantity"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class RedeliveryDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "The customer for the contract on this detail.", required = true)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party customer;

    @Schema(description = "the contract code for the given shipping containers", required = true, example = "CNCX05-100000", maxLength = 16)
    @Column(nullable = false, length = 16)
    String contract;

    @Schema(description = "the equipment type ISO code or an internal code if one does not exist for the given shipping containers", required = true, example = "22G1", maxLength = 10)
    @Column(nullable = false, length = 10)
    String equipment;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @Schema(description = "the insurance coverage for damage repairs")
    InsuranceCoverage insuranceCoverage;

    @Schema(description = "the grade / category of the unit as it was when it last left a depot", required = true, example = "IICL", maxLength = 10)
    @Column(nullable = false, length = 10)
    String inspectionCriteria;

    @Schema(description = "an indicator for the upgrades applied to units on this detail.\n\n`FG` - Food grade\n\n`ML` - Malt\n\n`DB` - Dairy Board\n\n`EV` - Evian\n\n`WH` - Whiskey\n\n`SU` - Sugar\n\n`CF` - Coffee\n\n`TB` - Tobacco\n\n`MC` - Milk cartons\n\n`MP` - Milk powder\n\n`AM` - Ammunition\n\n`CH` - Cotton/Hay\n\n`TE` - Tea\n\n`FT` - Flexitank", allowableValues = {"FG", "ML", "DB", "EV", "WH", "SU", "CF", "TB", "MC", "MP", "AM", "CH", "TE", "FT"}, required = false, example = "AM", maxLength = 2)
    @Column(length = 2)
    String upgradeType;

    @Schema(description = "The party that will handle any repair billing for units associated with this detail.", required = true)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party billingParty;

    @Schema(description = "the specific units for this redelivery if defined, if not, assumed blanket (any unit matching criteria can be tied up to the quantity limit of this detail)")
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    List<RedeliveryUnit> units = new ArrayList<>();

    @Schema(description = "the number of shipping containers assigned to this detail", required = true, example = "1", minimum = "0")
    @Column(nullable = false)
    Integer quantity;
}
