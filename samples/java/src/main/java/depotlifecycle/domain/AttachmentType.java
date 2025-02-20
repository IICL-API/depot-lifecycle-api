package depotlifecycle.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "the mechanism that a genset attaches to a container.\n\n`CLIP` - Clip\n\n`UNDER` - Under slung", example = "CLIP")
public enum AttachmentType {
    CLIP("Clip"),
    UNDER("Under slung");

    public final String description;

    AttachmentType(String description) {
        this.description = description;
    }
}
