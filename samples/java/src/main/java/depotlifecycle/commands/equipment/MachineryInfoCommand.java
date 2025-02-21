package depotlifecycle.commands.equipment;

import com.fasterxml.jackson.annotation.JsonView;
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
}
