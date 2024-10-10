package depotlifecycle.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "Describes the status for this redelivery: \n\n`PENDING` - redelivery is pending and not yet valid for turn in\n\n`APPROVED` - redelivery is approved\n\n`COMPLETE` - all units are turned in and no more may be turned in\n\n`EXPIRED` - the redelivery is now expired and any remaining units are no longer valid for turn in\n\n`CANCELLED` - the redelivery is cancelled and not valid for turn in", example = "APPROVED")
public enum RedeliveryStatus {
    PENDING,
    APPROVED,
    COMPLETE,
    EXPIRED,
    CANCELLED
}
