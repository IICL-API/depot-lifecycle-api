package depotlifecycle;

import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonView
@NoArgsConstructor
@Schema(description = "used inform why an activity could not be immediately processed")
@Introspected
public class PendingResponse {
    @Schema(pattern = "^[A-Z0-9]{3}[0-9]{3}$", description = "indicator code for this response", example = "TRI365", required = false, maxLength = 6)
    String code;

    @Schema(description = "a descriptive reason why the activity was not immediately created", required = false, example = "Info TRI365 - Estimate accepted, but requires manual DV calculation to process.")
    String message;
}
