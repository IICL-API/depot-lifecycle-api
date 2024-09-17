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
@Schema(description = "groups similar units on a release", requiredProperties = {"customer", "contract", "equipment", "grade", "quantity"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class ReleaseDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "The customer for the contract on this detail.", required = true, nullable = false, implementation = Party.class)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party customer;

    @Schema(description = "the contract code for the given shipping containers", required = true, nullable = false, example = "CNCX05-100000", maxLength = 16)
    @Column(nullable = false, length = 16)
    String contract;

    @Schema(description = "the equipment type ISO code or an internal code if one does not exist for the given shipping containers", required = true, nullable = false, example = "22G1", maxLength = 10)
    @Column(nullable = false, length = 10)
    String equipment;

    @Schema(description = "the current grade of the unit", required = true, nullable = false, example = "IICL", maxLength = 10)
    @Column(nullable = false, length = 10)
    String grade;

    @Schema(description = "the type of secondary upgrade this estimate represents.\n\n`FG` - Food grade\n\n`ML` - Malt\n\n`DB` - Dairy Board\n\n`EV` - Evian\n\n`WH` - Whiskey\n\n`SU` - Sugar\n\n`CF` - Coffee\n\n`TB` - Tobacco\n\n`MC` - Milk cartons\n\n`MP` - Milk powder\n\n`AM` - Ammunition\n\n`CH` - Cotton/Hay\n\n`TE` - Tea\n\n`FT` - Flexitank", example = "AM", required = false, nullable = true, implementation = UpgradeType.class)
    @Column(length = 2)
    @Enumerated(EnumType.STRING)
    UpgradeType upgradeType;

    @ArraySchema(schema = @Schema(implementation = ReleaseUnit.class))
    @Schema(description = "the specific units for this release if defined, if not, assumed blanket (any unit matching criteria can be tied up to the quantity limit of this detail)", required = false, nullable = false)
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    List<ReleaseUnit> units = new ArrayList<>();

    @ArraySchema(schema = @Schema(implementation = ReleaseDetailCriteria.class))
    @Schema(description = "additional criteria beyond the required properties of this detail to further restrict units.  i.e. <= 2003 manufacture year. ", required = false, nullable = false)
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    List<ReleaseDetailCriteria> criteria = new ArrayList<>();

    @ArraySchema(schema = @Schema(example = "An example detail level comment."))
    @Schema(description = "comments pertaining to this unit for the intended recipient of this message", required = false, nullable = false)
    @Lob
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable
    List<String> comments;

    @Schema(description = "the number of shipping containers assigned to this detail", required = true, nullable = false, minimum = "0", example = "1")
    @Column(nullable = false)
    Integer quantity;

    @Schema(description = "indicator if mechanical equipment must be tested prior to lease out", required = false, nullable = true)
    @Column
    Boolean preTripInspectionRequired;

    @Schema(example = "-23", description = "the reefer setpoint / desired temperature in Celsius", required = false, nullable = true)
    @Column
    Integer desiredTemperature;

    @Schema(description = "if the equipment has fresh air ventilation, the rate of the fresh air ventilation", example = "90 CBM", maxLength = 10, required = false, nullable = true)
    @Column(length = 10)
    String ventilation;
}
