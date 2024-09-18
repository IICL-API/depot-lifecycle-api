package depotlifecycle.commands;

import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonView
@Introspected
public class EstimateLineItemPartCommand {
    @Nullable
    @Size(max = 500)
    String description;

    @NotNull
    @NotBlank
    @Size(max = 50)
    String number;

    @NotNull
    @Min(1)
    Integer quantity;

    @NotNull
    BigDecimal price;
}
