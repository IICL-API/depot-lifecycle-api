package depotlifecycle.commands;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PreliminaryDecisionCommand {
    @NotNull
    @NotBlank
    String recommendation;

    @Nullable
    String reason;

    @Nullable
    BigDecimal difference;
}
