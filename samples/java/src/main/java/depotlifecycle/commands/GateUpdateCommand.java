package depotlifecycle.commands;

import com.fasterxml.jackson.annotation.JsonFormat;
import depotlifecycle.domain.GateRequestStatus;
import depotlifecycle.domain.GateRequestType;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Introspected
public class GateUpdateCommand {
    @NotNull
    @NotBlank
    @Size(max = 16)
    String adviceNumber;

    @NotNull
    @Size(max = 11)
    @Pattern(regexp = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", message = "Unit Number must match the Unit Number pattern.")
    String unitNumber;

    @NotNull
    @NotBlank
    @Size(max = 9)
    @Pattern(regexp = "^[A-Z0-9]{9}$", message = "Depot must match the Company Id pattern.")
    String depot;

    @Nullable
    GateRequestStatus status;

    @Nullable
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    ZonedDateTime activityTime;

    @Nullable
    GateRequestType type;

    @Nullable
    List<GatePhotoCommand> photos;
}
