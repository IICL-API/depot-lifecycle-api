package depotlifecycle.commands;

import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Introspected
public class EstimateSearchCommand {
    @Nullable
    @Size(max = 16)
    String estimateNumber;

    @Nullable
    @Size(max = 11)
    @Pattern(regexp = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", message = "Unit Number must match the Unit Number pattern.")
    String unitNumber;

    @Nullable
    @Size(max = 9)
    @Pattern(regexp = "^[A-Z0-9]{9}$", message = "Depot must match the Company Id pattern.")
    String depot;

    @Nullable
    @Size(max = 9)
    @Pattern(regexp = "^[A-Z0-9]{9}$", message = "Lessee must match the Company Id pattern.")
    String lessee;

    @Nullable
    @Min(0)
    Integer revision;

    @Nullable
    @Size(max = 10)
    String equipmentCode;
}
