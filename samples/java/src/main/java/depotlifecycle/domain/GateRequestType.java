package depotlifecycle.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "gate type indicator\n\n`IN` - Gate In\n\n`OUT` - Gate Out", maxLength = 3, example = "IN")
public enum GateRequestType {
    IN,
    OUT
}
