package depotlifecycle.commands;

import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.TireTreadLocation;
import depotlifecycle.domain.UnitOfMeasure;
import io.micronaut.core.annotation.Introspected;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonView
@Introspected
public class TireTreadMeasurementCommand {
    @NotNull
    TireTreadLocation location;

    @NotNull
    UnitOfMeasure unitOfMeasure;

    @NotNull
    @Min(0)
    BigDecimal depth;
}
