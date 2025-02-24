package depotlifecycle.commands.equipment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.equipment.MachineryInfo;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonView
@Introspected
public class MachineryInfoCommand {
    @Nullable
    @Max(50)
    @NotBlank
    String manufacturer;

    @Nullable
    @Max(50)
    @NotBlank
    String modelName;

    @Nullable
    @Max(50)
    @NotBlank
    String modelNumber;

    @JsonIgnore
    public MachineryInfo toMachineryInfo() {
        MachineryInfo machineryInfo = new MachineryInfo();
        machineryInfo.setManufacturer(manufacturer);
        machineryInfo.setModelName(modelName);
        machineryInfo.setModelNumber(modelNumber);
        return machineryInfo;
    }
}
