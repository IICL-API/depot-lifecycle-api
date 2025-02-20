package depotlifecycle.commands;

import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.AttachmentType;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonView
@Introspected
public class GensetInfoCommand {
    @Nullable
    LocalDate lastMaintenanceDate;

    @Nullable
    @Min(0)
    Integer lastMaintenanceHours;

    @Nullable
    LocalDate lastBeltChangeDate;

    @Nullable
    @Min(0)
    Integer lastBeltChangeHours;

    @Nullable
    @Min(0)
    Integer totalHours;

    @Nullable
    @Min(0)
    Integer currentHours;

    @Nullable
    @Min(0)
    @Max(100)
    Integer fuelLevel;

    @Nullable
    AttachmentType attachmentType;
}
