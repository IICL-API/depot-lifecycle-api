package depotlifecycle.domain.inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.Party;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@JsonView
@NoArgsConstructor
@Schema(description = "*This model is only proposed at this time and is in an alpha state - it is not production approved and may change without notice.*\n\na point in time view of the shipping containers at a depot for the calling party, as seen by the responding system; used for near real time inventory status reconciliation between depot and owner systems", requiredProperties = {"reportedBy", "depot", "snapshotDateTime", "units"})
@Introspected
public class InventorySnapshot {
    @Schema(description = "the party whose system produced this view of the depot inventory; since either the depot or the owner may implement this API, this identifies whose view is being reported", required = true, nullable = false, implementation = Party.class)
    Party reportedBy;

    @Schema(description = "the storage location this inventory list is for", required = true, nullable = false, implementation = Party.class)
    Party depot;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    @Schema(description = "the date and time in local time this inventory view was produced; compare unit statuses as of this moment, not the moment the response is read\n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", type = "string", format = "date-time", required = true, nullable = false)
    ZonedDateTime snapshotDateTime;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    @ArraySchema(schema = @Schema(implementation = InventoryUnit.class))
    @Schema(description = "the shipping containers currently at the depot for the calling party that match the search criteria; an empty array indicates no matching shipping containers are on site", required = true, nullable = false)
    List<InventoryUnit> units;
}
