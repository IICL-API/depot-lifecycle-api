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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "Provides a tire tread measurement in the indicated location")
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class TireTreadMeasurement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "the tire location of the tread depth measurement.\n\n`RFI` - Right Front Inner\n\n `RFO` - Right Front Outer\n\n `RRI` - Right Rear Inner\n\n `RRO` - Right Rear Outer\n\n `LFI` - Left Front Inner\n\n `LFO` - Left Front Outer\n\n `LRI` - Left Rear Inner\n\n `LRO` - Left Rear Outer", example = "RFI", required = true, nullable = false, implementation = TireTreadLocation.class)
    @Column(nullable = false, length = 3)
    @Enumerated(EnumType.STRING)
    TireTreadLocation location;

    @Schema(description = "the measurement type of the tread depth\n\n`CMT` - \"Centimeters\"\n\n `FOT` - \"Feet\"\n\n `GRM` - \"Grams\"\n\n `INH` - \"Inches\"\n\n `KGM` - \"Kilograms\"\n\n `MTR` - \"Meters\"\n\n `TON` - \"Tons\"\n\n `MTT` - \"Metric Tons\"\n\n `MMT` - \"Millimeters\"\n\n", required = true,  nullable = false, implementation = UnitOfMeasure.class)
    @Column(nullable = false, length = 3)
    @Enumerated(EnumType.STRING)
    UnitOfMeasure unitOfMeasure;

    @Schema(description = "the measurement of the tire depth", required = true, nullable = false, type = "number", format = "double", minimum = "0.00", example = "9.95")
    @Column(nullable = false)
    BigDecimal depth;
}
