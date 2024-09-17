package depotlifecycle.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "indicator of when this photo applies\n\n`REPAIRED` - Photo is after repair \n\n`BEFORE` - Photo is before repair", defaultValue = "BEFORE", example = "BEFORE")
public enum EstimatePhotoStatus {
    BEFORE,
    REPAIR
}
