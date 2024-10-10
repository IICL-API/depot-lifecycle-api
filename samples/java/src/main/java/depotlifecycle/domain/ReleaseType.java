package depotlifecycle.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "Describes the intended purpose of the release: \n\n`SALE` - shipping containers are being sold\n\n`BOOK` - shipping containers are being leased to a customer\n\n`REPO` - shipping containers are being relocated by the owner to another storage location")
public enum ReleaseType {
    SALE,
    BOOK,
    REPO
}
