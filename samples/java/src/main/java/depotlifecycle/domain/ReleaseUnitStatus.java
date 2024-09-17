package depotlifecycle.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "Describes the state of the shipping container for this release: \n\n`TIED` - shipping container is assigned to this release and ready to lease out.\n\n`REMOVED` - shipping container was attached to this release, but is no longer valid for release.\n\n`LOT` - shipping container has left the storage location.\n\n`CANDIDATE` - this container is eligible for this release but not currently assigned.", example = "TIED")
public enum ReleaseUnitStatus {
    REMOVED,
    TIED,
    LOT,
    CANDIDATE
}
