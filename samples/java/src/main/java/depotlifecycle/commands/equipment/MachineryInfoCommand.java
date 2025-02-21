package depotlifecycle.commands.equipment;

import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import lombok.Data;

@Data
@JsonView
@Introspected
public class MachineryInfoCommand {
    @Nullable
    @Max(50)
    String manufacturer;

    @Nullable
    @Max(50)
    String modelName;

    @Nullable
    @Max(50)
    String modelNumber;
}
