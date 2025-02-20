package depotlifecycle.commands;

import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.BreakCondition;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@JsonView
@Introspected
public class ChassisInfoCommand {
    @Nullable
    @Max(14)
    String licensePlate;

    @Nullable
    List<TireTreadMeasurementCommand> treadMeasurements;

    @NotNull
    BreakCondition breakCondition;

    @NotNull
    @Min(0)
    Integer hubometer;
}
