package depotlifecycle.domain.inventory;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "Describes where the shipping container currently is in the depot lifecycle.  This status intentionally only conveys lifecycle position; the quality of the unit is reported separately as a grade and holds are reported separately as a flag: \n\n`AWAITING_ESTIMATE` - gated in damaged and no estimate has been submitted\n\n`AWAITING_CUSTOMER_APPROVAL` - an estimate revision is awaiting customer approval\n\n`AWAITING_OWNER_DECISION` - the estimate is awaiting the owner decision (work order)\n\n`REPAIR_AUTHORIZED` - a work order authorizes repair and the repair is not yet complete\n\n`AVAILABLE` - available for lease out\n\n`FOR_SALE` - designated for sale\n\n`SOLD` - sold and awaiting removal\n\n`TIED_OUTBOUND` - allocated to an outbound release / booking", example = "AVAILABLE")
public enum InventoryStatus {
    AWAITING_ESTIMATE,
    AWAITING_CUSTOMER_APPROVAL,
    AWAITING_OWNER_DECISION,
    REPAIR_AUTHORIZED,
    AVAILABLE,
    FOR_SALE,
    SOLD,
    TIED_OUTBOUND
}
