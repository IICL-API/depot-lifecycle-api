package depotlifecycle.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "Describes the status for this release: \n\n`PENDING` - release is pending and not yet valid\n\n`APPROVED` - release is approved for leaving the storage location\n\n`COMPLETE` - all containers have left the depot and no more may be processed\n\n`EXPIRED` - the release is now expired and any remaining units are no longer valid\n\n`CANCELLED` - the release is cancelled and not valid", example = "APPROVED")
public enum ReleaseStatus {
    PENDING,
    APPROVED,
    COMPLETE,
    EXPIRED,
    CANCELLED
}
