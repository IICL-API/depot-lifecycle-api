package depotlifecycle.commands;

import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.EstimatePhotoStatus;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonView
@Introspected
public class EstimatePhotoCommand {
    @NotNull
    @NotBlank
    String url;

    @Nullable
    EstimatePhotoStatus status;
}
