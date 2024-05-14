package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "unit criteria that groups similar units on a redelivery", requiredProperties = {"customer", "contract", "equipment", "inspectionCriteria", "quantity"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class RedeliveryDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "The customer for the contract on this detail.", required = true, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party customer;

    @Schema(description = "the contract code for the given shipping containers", required = true, nullable = false, example = "CNCX05-100000", maxLength = 16)
    @Column(nullable = false, length = 16)
    String contract;

    @Schema(description = "the equipment type ISO code or an internal code if one does not exist for the given shipping containers", required = true, nullable = false, example = "22G1", maxLength = 10)
    @Column(nullable = false, length = 10)
    String equipment;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @Schema(description = "the insurance coverage for damage repairs", required = false, nullable = true)
    InsuranceCoverage insuranceCoverage;

    @Schema(description = "the grade / category of the unit as it was when it last left a depot", required = false, nullable = true, example = "IICL", maxLength = 10)
    @Column(nullable = true, length = 10)
    String grade;

    @Schema(description = "an indicator for the upgrades applied to units on this detail.\n\n`FG` - Food grade\n\n`ML` - Malt\n\n`DB` - Dairy Board\n\n`EV` - Evian\n\n`WH` - Whiskey\n\n`SU` - Sugar\n\n`CF` - Coffee\n\n`TB` - Tobacco\n\n`MC` - Milk cartons\n\n`MP` - Milk powder\n\n`AM` - Ammunition\n\n`CH` - Cotton/Hay\n\n`TE` - Tea\n\n`FT` - Flexitank", allowableValues = {"FG", "ML", "DB", "EV", "WH", "SU", "CF", "TB", "MC", "MP", "AM", "CH", "TE", "FT"}, required = false, nullable = true, example = "AM", maxLength = 2)
    @Column(nullable = true, length = 2)
    String upgradeType;

    @Schema(description = "the specific units for this redelivery if defined, if not, assumed blanket (any unit matching criteria can be tied up to the quantity limit of this detail)", required = false, nullable = false)
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    List<RedeliveryUnit> units = new ArrayList<>();

    @Schema(description = "the number of shipping containers assigned to this detail", required = true, nullable = false, minimum = "0", example = "1")
    @Column(nullable = false)
    Integer quantity;

    @ArraySchema(schema = @Schema(description = "comments pertaining to this unit for the intended recipient of this message", example = "An example detail level comment.", required = false, nullable = false))
    @Schema(description = "comments pertaining to this unit for the intended recipient of this message", required = false, nullable = false)
    @Lob
    @ElementCollection
    @CollectionTable
    @LazyCollection(LazyCollectionOption.FALSE)
    List<String> comments;
}
