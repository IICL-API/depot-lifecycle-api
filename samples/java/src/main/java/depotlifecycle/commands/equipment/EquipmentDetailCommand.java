package depotlifecycle.commands.equipment;

import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@JsonView
@Introspected
public class EquipmentDetailCommand {
    @NotNull
    @Max(10)
    @NotBlank
    String equipment;

    @NotNull
    MachineryInfoCommand machineryInfo;

    @NotNull
    LocalDate manufactureDate;

    @Nullable
    Integer desiredTemperature;

    @Nullable
    Integer desiredHumidity;

    @Nullable
    List<InspectionReportCommand> inspections;

    @Nullable
    ChassisInfoCommand chassisInfo;

    @Nullable
    GensetInfoCommand gensetInfo;
}
