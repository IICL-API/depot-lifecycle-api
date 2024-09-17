package depotlifecycle.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "Describes the state of the shipping container for this redelivery: \n\n`TIED` - shipping container is assigned to this redelivery and ready to turn in.\n\n`REMOVED` - shipping container was attached to this redelivery, but is no longer valid for redelivery.\n\n`TIN` - shipping container has turned into the storage location of this redelivery.", example = "TIED")
public enum RedeliveryUnitStatus {
    TIED,
    REMOVED,
    TIN
}
