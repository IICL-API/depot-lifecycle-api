package depotlifecycle.commands;

import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonView
@Introspected
public class GatePhotoCommand {
    @NotNull
    @NotBlank
    String url;
}
