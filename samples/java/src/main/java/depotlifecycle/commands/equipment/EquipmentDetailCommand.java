package depotlifecycle.commands.equipment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.equipment.EquipmentDetail;
import depotlifecycle.domain.equipment.InspectionReport;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
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
    LocalDate manufactureDate;

    @Nullable
    Boolean loaded;

    @Nullable
    List<String> cargoSeals;

    @Nullable
    Integer desiredTemperature;

    @Nullable
    Integer desiredHumidity;

    @Nullable
    List<InspectionReportCommand> inspections;

    @Nullable
    MachineryInfoCommand machineryInfo;

    @Nullable
    ChassisInfoCommand chassisInfo;

    @Nullable
    GensetInfoCommand gensetInfo;

    @JsonIgnore
    public EquipmentDetail toEquipmentDetail() {
        EquipmentDetail equipmentDetail = new EquipmentDetail();
        equipmentDetail.setEquipment(equipment);
        equipmentDetail.setManufactureDate(manufactureDate);
        equipmentDetail.setLoaded(loaded);
        equipmentDetail.setCargoSeals(cargoSeals);
        equipmentDetail.setDesiredTemperature(desiredTemperature);
        equipmentDetail.setDesiredHumidity(desiredHumidity);
        List<InspectionReport> inspections = new ArrayList<>();
        if (this.inspections != null) {
            this.inspections.forEach(inspectionReportCommand -> {
                inspections.add(inspectionReportCommand.toInspectionReport());
            });
        }
        equipmentDetail.setInspections(inspections);
        equipmentDetail.setMachineryInfo(machineryInfo == null ? null : machineryInfo.toMachineryInfo());
        equipmentDetail.setChassisInfo(chassisInfo == null ? null : chassisInfo.toChassisInfo());
        equipmentDetail.setGensetInfo(gensetInfo == null ? null : gensetInfo.toGensetInfo());
        return equipmentDetail;
    }
}
