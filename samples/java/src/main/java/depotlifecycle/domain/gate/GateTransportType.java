package depotlifecycle.domain.gate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "How the shipping container was transported", maxLength = 5, example = "TRUCK")
public enum GateTransportType {
    RAIL,
    TRUCK,
    BARGE
}
