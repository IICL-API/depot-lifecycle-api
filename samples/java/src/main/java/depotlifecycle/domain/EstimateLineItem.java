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
import java.math.BigDecimal;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "Represents an estimation of costs to repair or upgrade a single instance of damage for a Shipping Container.", requiredProperties = {"line", "repair", "damage", "material", "component", "hours", "materialCost", "laborRate", "party"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class EstimateLineItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "The line number", required = true)
    @Column(nullable = false)
    Integer line;

    @Schema(description = "repair code\n\n[see IICL Preferred Repair Codes, Section 5.4](https://www.iicl.org/iiclforms/assets/File/public/bulletins/TB002_EDIS_February_2003.pdf)", required = true, pattern = "^[A-Z0-9]{2}$", example = "IT", maxLength = 2)
    @Column(nullable = false, length = 2)
    String repair;

    @Schema(description = "damage code\n\n[see IICL Preferred Damage Codes, Section 5.2](https://www.iicl.org/iiclforms/assets/File/public/bulletins/TB002_EDIS_February_2003.pdf)", required = true, pattern = "^[A-Z0-9]{2}$", example = "CK", maxLength = 2)
    @Column(nullable = false, length = 2)
    String damage;

    @Schema(description = "component material code\n\n[see IICL Preferred Material Type Codes, Section 5.5](https://www.iicl.org/iiclforms/assets/File/public/bulletins/TB002_EDIS_February_2003.pdf)", required = true, pattern = "^[A-Z0-9]{2}$", example = "MU", maxLength = 2)
    @Column(nullable = false, length = 2)
    String material;

    @Schema(description = "component code\n\n[see IICL Preferred Component Codes, Section 5.1](https://www.iicl.org/iiclforms/assets/File/public/bulletins/TB002_EDIS_February_2003.pdf)", required = true, pattern = "^[A-Z0-9]{3}$", example = "CMA", maxLength = 3)
    @Column(nullable = false, length = 3)
    String component;

    @Schema(description = "specifies the damage location code on a container\n\n[see IICL Preferred Location Codes, Section 5.7](https://www.iicl.org/iiclforms/assets/File/public/bulletins/TB002_EDIS_February_2003.pdf)", required = false, pattern = "^[A-Z0-9]{4}$", example = "UR1N", maxLength = 4)
    @Column(length = 4)
    String location;

    @Schema(description = "the length dimension of the damage", type = "number", format = "int32", required = false, example = "15", minimum = "0")
    @Column
    Integer length;

    @Schema(description = "the width dimension of the damage", type = "number", format = "int32", required = false, example = "1", minimum = "0")
    @Column
    Integer width;

    @Schema(description = "the height dimension of the damage", type = "number", format = "int32", required = false, example = "1", minimum = "0")
    @Column
    Integer height;

    @Schema(description = "the measurement of the damage dimensions for this line item\n\n`CMT` - \"Centimeters\"\n\n `FOT` - \"Feet\"\n\n `GRM` - \"Grams\"\n\n `INH` - \"Inches\"\n\n `KGM` - \"Kilograms\"\n\n `MTR` - \"Meters\"\n\n `TON` - \"Tons\"\n\n `MTT` - \"Metric Tons\"\n\n `MMT` - \"Millimeters\"\n\n", required = false, allowableValues = {"CMT", "FOT", "GRM", "INH", "KGM", "MTR", "TON", "MTT", "MMT"}, maxLength = 3)
    @Column(length = 3)
    String unitOfMeasure;

    @Schema(description = "the number of hours to repair this damage", required = true, type = "number", format = "double", minimum = "0.00", example = "9.95")
    @Column(nullable = false)
    BigDecimal hours;

    @Schema(description = "the cost of materials to repair this damage", required = true, type = "number", format = "double", minimum = "0.00", example = "9.95")
    @Column(nullable = false)
    BigDecimal materialCost;

    @Schema(description = "rate to pay the labor to repair this damage", required = true, type = "number", format = "double", minimum = "0.00", example = "35.00")
    @Column(nullable = false)
    BigDecimal laborRate;

    @Schema(description = "The party that is responsible for the cost of this repair\n\n `O` - Owner\n\n `U` - Customer\n\n `I` - Insurance\n\n `W` - Warranty\n\n `S` - Special\n\n `D` - Deleted\n\n", required = true, allowableValues = {"O", "U", "I", "W", "S", "D"}, maxLength = 1)
    @Column(nullable = false, length = 1)
    String party;

    @Schema(description = "comments concerning this line item repair", required = false, maxLength = 256)
    @Column(length = 256)
    String comments;

    @Schema(description = "which amount should taxes apply\n\n`B` - Both Labor Cost & Material Cost\n\n`N` - Neither\n\n`L` - Labor Cost\n\n`M` - Material Cost", required = false, allowableValues = {"B", "N", "L", "M"}, example = "B", maxLength = 1)
    @Column(length = 1)
    String taxRule;

    @Schema(description = "the number of damages", required = false, type = "number", format = "int32", example = "1", minimum = "0")
    @Column
    Integer quantity;
}
