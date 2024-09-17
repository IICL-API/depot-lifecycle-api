package depotlifecycle.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "an indicator for the upgrades applied to units on this detail.\n\n`FG` - Food grade\n\n`ML` - Malt\n\n`DB` - Dairy Board\n\n`EV` - Evian\n\n`WH` - Whiskey\n\n`SU` - Sugar\n\n`CF` - Coffee\n\n`TB` - Tobacco\n\n`MC` - Milk cartons\n\n`MP` - Milk powder\n\n`AM` - Ammunition\n\n`CH` - Cotton/Hay\n\n`TE` - Tea\n\n`FT` - Flexitank", example = "AM")
public enum UpgradeType {
    FG,
    ML,
    DB,
    EV,
    WH,
    SU,
    CF,
    TB,
    MC,
    MP,
    AM,
    CH,
    TE,
    FT
}
