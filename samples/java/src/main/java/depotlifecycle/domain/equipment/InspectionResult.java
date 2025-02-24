package depotlifecycle.domain.equipment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "the result of the inspection\n\n`PASS` - Pass\n\n`FAIL` - Fail", example = "PASS")
public enum InspectionResult {
    PASS("Pass"),
    FAIL("Fail");

    public final String description;

    InspectionResult(String description) {
        this.description = description;
    }
}
