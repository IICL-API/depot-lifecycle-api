package depotlifecycle.commands.equipment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.equipment.GensetInfo;
import depotlifecycle.domain.equipment.MountType;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonView
@Introspected
public class GensetInfoCommand {
    @Nullable
    LocalDate lastMaintenanceDate;

    @Nullable
    @Min(0)
    Integer lastMaintenanceHours;

    @Nullable
    @Min(0)
    Integer currentMaintenanceHours;

    @Nullable
    LocalDate lastBeltChangeDate;

    @Nullable
    @Min(0)
    Integer lastBeltChangeHours;

    @Nullable
    @Min(0)
    Integer totalHours;

    @Nullable
    @Min(0)
    @Max(100)
    Integer fuelLevel;

    @NotNull
    MountType mountType;

    @JsonIgnore
    public GensetInfo toGensetInfo() {
        GensetInfo gensetInfo = new GensetInfo();
        gensetInfo.setLastMaintenanceDate(lastMaintenanceDate);
        gensetInfo.setLastMaintenanceHours(lastMaintenanceHours);
        gensetInfo.setCurrentMaintenanceHours(currentMaintenanceHours);
        gensetInfo.setLastBeltChangeDate(lastBeltChangeDate);
        gensetInfo.setLastBeltChangeHours(lastBeltChangeHours);
        gensetInfo.setTotalHours(totalHours);
        gensetInfo.setFuelLevel(fuelLevel);
        gensetInfo.setMountType(mountType);
        return gensetInfo;
    }
}
