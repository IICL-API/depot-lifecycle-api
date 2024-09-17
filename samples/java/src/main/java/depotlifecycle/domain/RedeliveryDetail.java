package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    @Schema(description = "the insurance coverage for damage repairs", required = false, nullable = true, implementation = InsuranceCoverage.class)
    InsuranceCoverage insuranceCoverage;

    @Schema(description = "the grade / category of the unit as it was when it last left a depot", required = false, nullable = true, example = "IICL", maxLength = 10)
    @Column(nullable = true, length = 10)
    String grade;

    @Schema(description = "the type of secondary upgrade this estimate represents.\n\n`FG` - Food grade\n\n`ML` - Malt\n\n`DB` - Dairy Board\n\n`EV` - Evian\n\n`WH` - Whiskey\n\n`SU` - Sugar\n\n`CF` - Coffee\n\n`TB` - Tobacco\n\n`MC` - Milk cartons\n\n`MP` - Milk powder\n\n`AM` - Ammunition\n\n`CH` - Cotton/Hay\n\n`TE` - Tea\n\n`FT` - Flexitank", example = "AM", required = false, nullable = true, implementation = UpgradeType.class)
    @Column(nullable = true, length = 2)
    @Enumerated(EnumType.STRING)
    UpgradeType upgradeType;

    @ArraySchema(schema = @Schema(implementation = RedeliveryUnit.class))
    @Schema(description = "the specific units for this redelivery if defined, if not, assumed blanket (any unit matching criteria can be tied up to the quantity limit of this detail)", required = false, nullable = false)
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    List<RedeliveryUnit> units = new ArrayList<>();

    @Schema(description = "the number of shipping containers assigned to this detail", required = true, nullable = false, minimum = "0", example = "1")
    @Column(nullable = false)
    Integer quantity;

    @ArraySchema(schema = @Schema(example = "An example detail level comment."))
    @Schema(description = "comments pertaining to this unit for the intended recipient of this message", required = false, nullable = false)
    @Lob
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable
    List<String> comments;
}
