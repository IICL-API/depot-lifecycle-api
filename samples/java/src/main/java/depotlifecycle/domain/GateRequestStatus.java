package depotlifecycle.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "an indicator of the shipping container's status\n\n`A` - Non-damaged\n\n`D` - Damaged\n\n`S` - Sold", example = "D")
public enum GateRequestStatus {
    A,
    D,
    S
}
