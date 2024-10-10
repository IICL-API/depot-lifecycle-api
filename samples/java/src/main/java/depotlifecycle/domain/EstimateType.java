package depotlifecycle.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, defaultValue = "R", description = "delineates the type of estimate; whether it was the initial, primary estimate or an ancillary / secondary repair after the initial decision\n\n`R` - Primary (Unknown Estimation Standard)\n\n`RI` - Primary (IICL)\n\n`RC` - Primary (CWCA)\n\n`SC` - Secondary (CWCA)\n\n`SU` - Secondary Upgrade\n\n`AS` - Sell Upgrade\n\n`AU` - Ancillary Upgrade\n\n`AR` - Ancillary Repair")
public enum EstimateType {
    R,
    RI,
    RC,
    SC,
    SU,
    AS,
    AU,
    AR
}
