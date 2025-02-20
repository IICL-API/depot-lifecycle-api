package depotlifecycle.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "the result of the regulatory inspection.\n\n`PASS` - Pass\n\n`FAIL` - Fail", example = "PASS")
public enum RegulatoryStatus {
    PASS("Pass"),
    FAIL("Fail");

    public final String description;

    RegulatoryStatus(String description) {
        this.description = description;
    }
}
