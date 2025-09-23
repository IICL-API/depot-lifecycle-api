package depotlifecycle.commands.repair;

import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.repair.EstimateTaxRule;
import io.micronaut.core.annotation.Introspected;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonView
@Introspected
public class EstimateTaxRateCommand {
    @NotNull
    @NotBlank
    @Size(max = 255)
    String description;

    @NotNull
    EstimateTaxRule rule;

    @Min(0)
    @NotNull
    BigDecimal rate;
}
