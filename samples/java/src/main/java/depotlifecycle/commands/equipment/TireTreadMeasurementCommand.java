package depotlifecycle.commands.equipment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.equipment.TireTreadLocation;
import depotlifecycle.domain.UnitOfMeasure;
import depotlifecycle.domain.equipment.TireTreadMeasurement;
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

    @JsonIgnore
    public TireTreadMeasurement toTireTreadMeasurement() {
        TireTreadMeasurement tireTreadMeasurement = new TireTreadMeasurement();
        tireTreadMeasurement.setLocation(location);
        tireTreadMeasurement.setUnitOfMeasure(unitOfMeasure);
        tireTreadMeasurement.setDepth(depth);
        return tireTreadMeasurement;
    }
}
