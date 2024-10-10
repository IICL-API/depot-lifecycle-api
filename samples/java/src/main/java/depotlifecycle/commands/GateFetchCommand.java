package depotlifecycle.commands;

import io.micronaut.core.annotation.Introspected;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Introspected
public class GateFetchCommand {
    @NotNull
    @Size(max = 11)
    @Pattern(regexp = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", message = "Unit Number must match the Unit Number pattern.")
    String unitNumber;
}
