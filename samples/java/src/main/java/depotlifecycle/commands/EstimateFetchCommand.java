package depotlifecycle.commands;

import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Introspected
public class EstimateFetchCommand {
    @NotNull
    @NotBlank
    @Size(max = 16)
    String estimateNumber;

    @NotNull
    @NotBlank
    @Size(max = 9)
    @Pattern(regexp = "^[A-Z0-9]{9}$", message = "Depot must match the Company Id pattern.")
    String depot;

    @Nullable
    @Min(0)
    Integer revision;
}
