package depotlifecycle.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "an indicator of the unit state on this work order\n\n`TIED` - shipping container is considered under repair on this work order\n\n`REMOVED` - shipping container is removed from this work order\n\n`REPAIRED` - shipping container is considered repaired", example = "TIED")
public enum WorkOrderUnitStatus {
    TIED,
    REMOVED,
    REPAIRED
}
