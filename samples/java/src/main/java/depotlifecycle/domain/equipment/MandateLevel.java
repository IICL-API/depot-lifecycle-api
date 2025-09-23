package depotlifecycle.domain.equipment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "how or by whom the inspection was mandated\n\n`NATIONAL` - National\n\n`REGIONAL` - Regional\n\n`INDUSTRY` - Industry", example = "INDUSTRY")
public enum MandateLevel {
    NATIONAL("National"),
    REGIONAL("Regional"),
    INDUSTRY("Industry");

    public final String description;

    MandateLevel(String description) {
        this.description = description;
    }
}
