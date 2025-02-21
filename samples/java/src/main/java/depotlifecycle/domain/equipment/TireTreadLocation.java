package depotlifecycle.domain.equipment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "the tire location of the tread depth measurement.\n\n`RFI` - Right Front Inner\n\n `RFO` - Right Front Outer\n\n `RRI` - Right Rear Inner\n\n `RRO` - Right Rear Outer\n\n `LFI` - Left Front Inner\n\n `LFO` - Left Front Outer\n\n `LRI` - Left Rear Inner\n\n `LRO` - Left Rear Outer", example = "RFI")
public enum TireTreadLocation {
    RFI("Right Front Inner"),
    RFO("Right Front Outer"),
    RRI("Right Rear Inner"),
    RRO("Right Rear Outer"),
    LFI("Left Front Inner"),
    LFO("Left Front Outer"),
    LRI("Left Rear Inner"),
    LRO("Left Rear Outer");

    public final String description;

    TireTreadLocation(String description) {
        this.description = description;
    }
}
