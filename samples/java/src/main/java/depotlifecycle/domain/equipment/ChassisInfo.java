package depotlifecycle.domain.equipment;

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
@Schema(description = "Provides Chassis specific information for a given shipping container", requiredProperties = {"licensePlate"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class ChassisInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "the license plate for the person represented by this party", minLength = 1, maxLength = 14, required = true, nullable = false)
    @Column(length = 14, nullable = false)
    String licensePlate;

    @ArraySchema(schema = @Schema(implementation = TireTreadMeasurement.class))
    @Schema(description = "An optional list of tread depth measurements for this Chassis tires", required = false, nullable = false)
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    List<TireTreadMeasurement> treadMeasurements = new ArrayList<>();

    @Schema(description = "the condition of the breaks on this Chassis.\n\n`GOOD` - Good\n\n`WARN` - Warn\n\n`BAD` - Bad", example = "GOOD", required = false, nullable = true, implementation = BrakeCondition.class)
    @Column(nullable = true, length = 4)
    @Enumerated(EnumType.STRING)
    BrakeCondition brakeCondition;

    @Schema(description = "the current reading of the hubometer on the Chassis", required = false, nullable = true, example = "15000", type = "integer", format = "int32", minimum = "0")
    @Column(nullable = true)
    Integer hubometer;
}
