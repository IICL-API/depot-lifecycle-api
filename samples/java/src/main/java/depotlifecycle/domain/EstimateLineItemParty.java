package depotlifecycle.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "The party that is responsible for the cost of this repair\n\n `O` - Owner\n\n `U` - Customer\n\n `I` - Insurance\n\n `W` - Warranty\n\n `S` - Special\n\n `D` - Depot\n\n `X` - Deleted\n\n `T` - Third Party\n\n")
public enum EstimateLineItemParty {
    O,
    U,
    I,
    W,
    S,
    D,
    X,
    T
}
