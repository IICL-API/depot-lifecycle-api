package depotlifecycle.commands.equipment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.equipment.BrakeCondition;
import depotlifecycle.domain.equipment.ChassisInfo;
import depotlifecycle.domain.equipment.TireTreadMeasurement;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonView
@Introspected
public class ChassisInfoCommand {
    @NotNull
    @Max(14)
    @NotBlank
    String licensePlate;

    @Nullable
    List<TireTreadMeasurementCommand> treadMeasurements;

    @Nullable
    BrakeCondition brakeCondition;

    @Nullable
    @Min(0)
    Integer hubometer;

    @JsonIgnore
    public ChassisInfo toChassisInfo() {
        ChassisInfo chassisInfo = new ChassisInfo();
        chassisInfo.setLicensePlate(licensePlate);

        List<TireTreadMeasurement> treadMeasurements = new ArrayList<>();
        if (this.treadMeasurements != null) {
            this.treadMeasurements.forEach(tireTreadMeasurementCommand -> {
                treadMeasurements.add(tireTreadMeasurementCommand.toTireTreadMeasurement());
            });
        }
        chassisInfo.setTreadMeasurements(treadMeasurements);
        chassisInfo.setBrakeCondition(brakeCondition);
        chassisInfo.setHubometer(hubometer);
        return chassisInfo;
    }
}
