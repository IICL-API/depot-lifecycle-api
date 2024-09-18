package depotlifecycle.commands;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Introspected
public class EstimateFetchCommand {
    @NonNull
    @Size(max = 16)
    String estimateNumber;

    @NonNull
    @Size(max = 9)
    @Pattern(regexp = "^[A-Z0-9]{9}$", message = "Depot must match the Company Id pattern.")
    String depot;

    @Nullable
    @Min(0)
    Integer revision;
}
