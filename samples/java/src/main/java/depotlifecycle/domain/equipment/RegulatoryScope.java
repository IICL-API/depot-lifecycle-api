package depotlifecycle.domain.equipment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "the grain the regulatory inspection applies.\n\n`NATIONAL` - National\n\n`REGIONAL` - Regional\n\n`INDUSTRY` - Industry", example = "INDUSTRY")
public enum RegulatoryScope {
    NATIONAL("National"),
    REGIONAL("Regional"),
    INDUSTRY("Industry");

    public final String description;

    RegulatoryScope(String description) {
        this.description = description;
    }
}
