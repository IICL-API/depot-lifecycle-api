package depotlifecycle.commands.repair;

import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonView
@Introspected
public class PreliminaryDecisionCommand {
    @NotNull
    @NotBlank
    @Size(max = 11)
    String recommendation;

    @Nullable
    @Size(max = 255)
    String reason;

    @Nullable
    BigDecimal difference;
}
