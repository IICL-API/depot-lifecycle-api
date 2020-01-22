package depotlifecycle;

import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonView
@NoArgsConstructor
@Schema(description = "an expected sell/fix decision to indicate the likely estimate owner approval action", requiredProperties = {"recommendation"})
@Introspected
public class PreliminaryDecision {
    @Schema(required = true, maxLength = 11, description = "a speculative repair decision code to indicate if a box will be repaired, sold, held for repair, could not determine a decision, requires a survey before proceeding, etc", example = "SELL")
    String recommendation;

    @Schema(required = false, maxLength = 255, description = "in the event a decision could not be determined, or a prerequisite exists, a more descriptive message than the code provided by the `recommendation` field")
    String reason;

    @Schema(required = false, type = "number", format = "double", description = "A positive or negative amount showing how close the repair estimate was to a decision change")
    BigDecimal difference;
}
