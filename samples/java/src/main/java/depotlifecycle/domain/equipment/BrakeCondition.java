package depotlifecycle.domain.equipment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "the status of the breaks on a Chassis.\n\n`GOOD` - Good\n\n`WARN` - Warn\n\n`BAD` - Bad", example = "GOOD")
public enum BrakeCondition {
    GOOD("Good"),
    WARN("Warn"),
    BAD("Bad");

    public final String description;

    BrakeCondition(String description) {
        this.description = description;
    }
}
