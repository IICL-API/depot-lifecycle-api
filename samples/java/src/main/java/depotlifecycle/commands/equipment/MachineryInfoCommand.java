package depotlifecycle.commands.equipment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.equipment.MachineryInfo;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonView
@Introspected
public class MachineryInfoCommand {
    @Nullable
    @Size(max = 50)
    String manufacturer;

    @Nullable
    @Size(max = 50)
    String modelName;

    @Nullable
    @Size(max = 50)
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
