package depotlifecycle.commands.inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import depotlifecycle.domain.inventory.InventoryStatus;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Introspected
public class InventoryFetchCommand {
    @NotNull
    @NotBlank
    @Size(min = 9, max = 9)
    @Pattern(regexp = "^[A-Z0-9]{9}$", message = "Depot must match the Company Id pattern.")
    String depot;

    @Nullable
    @Size(min = 10, max = 11)
    @Pattern(regexp = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", message = "Unit Number must match the Unit Number pattern.")
    String unitNumber;

    @Nullable
    InventoryStatus status;

    @Nullable
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Z")
    ZonedDateTime statusChangedAfter;
}
