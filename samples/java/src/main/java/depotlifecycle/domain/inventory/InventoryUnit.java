package depotlifecycle.domain.inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.ExternalParty;
import depotlifecycle.domain.Party;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "*This model is only proposed at this time and is in an alpha state - it is not production approved and may change without notice.*\n\nthe current status of a shipping container in a depot's inventory", requiredProperties = {"unitNumber", "status", "statusDateTime"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class InventoryUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party depot;

    @Schema(description = "the current unit number of the shipping container", pattern = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", required = true, nullable = false, example = "CONU1234561", minLength = 1, maxLength = 11)
    @Column(nullable = false, length = 11)
    String unitNumber;

    @Schema(description = "the equipment type ISO code or an internal code if one does not exist for the given shipping container", required = false, nullable = true, example = "22G1", minLength = 1, maxLength = 10)
    @Column(length = 10)
    String equipment;

    @Schema(description = "Describes where the shipping container currently is in the depot lifecycle.  This status intentionally only conveys lifecycle position; the quality of the unit is reported separately as a grade and holds are reported separately as a flag: \n\n`AWAITING_ESTIMATE` - gated in damaged and no estimate has been submitted\n\n`AWAITING_CUSTOMER_APPROVAL` - an estimate revision is awaiting customer approval\n\n`AWAITING_OWNER_DECISION` - the estimate is awaiting the owner decision (work order)\n\n`REPAIR_AUTHORIZED` - a work order authorizes repair and the repair is not yet complete\n\n`AVAILABLE` - available for lease out\n\n`FOR_SALE` - designated for sale\n\n`SOLD` - sold and awaiting removal\n\n`TIED_OUTBOUND` - allocated to an outbound release / booking", example = "AVAILABLE", required = true, nullable = false)
    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    InventoryStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    @Schema(description = "the date and time in local time the shipping container entered the current status; used to buffer in-flight status transitions from reconciliation noise\n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", type = "string", format = "date-time", required = true, nullable = false)
    @Column(nullable = false)
    ZonedDateTime statusDateTime;

    @Schema(description = "the grade / category the shipping container currently holds", required = false, nullable = true, example = "IICL", minLength = 1, maxLength = 10)
    @Column(length = 10)
    String currentGrade;

    @Schema(description = "the grade / category the shipping container is expected to hold when work is complete - often used in lieu of an estimate inspection criteria.", required = false, nullable = true, example = "IICL", minLength = 1, maxLength = 10)
    @Column(length = 10)
    String targetGrade;

    @Schema(description = "indicates the shipping container is on hold; a hold is reported separately from the lifecycle status since a unit may be held at any lifecycle position", required = false, nullable = true, defaultValue = "false", example = "false")
    @Column
    Boolean onHold;

    @Schema(description = "if the shipping container is on hold, an optional explanation of the hold", required = false, nullable = true, example = "technical hold - pending bulletin TB1234", minLength = 1, maxLength = 255)
    @Column(length = 255)
    String holdReason;

    @Schema(description = "an indicator of whether the shipping container is currently considered damaged", required = false, nullable = true, example = "false")
    @Column
    Boolean damaged;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    @Schema(description = "the date and time in local time the shipping container gated in to the depot\n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", type = "string", format = "date-time", required = false, nullable = true)
    @Column
    ZonedDateTime gateInDateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    @Schema(description = "the date and time in local time the primary damage repair (see the primary estimate types of `EstimateType`) was completed, if it has been\n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", type = "string", format = "date-time", required = false, nullable = true)
    @Column
    ZonedDateTime primaryRepairCompleteDateTime;

    @Schema(description = "the customer that turned the shipping container in to the depot", required = false, nullable = true, implementation = ExternalParty.class)
    @ManyToOne(fetch = FetchType.EAGER)
    ExternalParty inCustomer;

    @Schema(description = "the redelivery advice number the shipping container turned in against", required = false, nullable = true, example = "AHAMG000000", minLength = 1, maxLength = 16)
    @Column(length = 16)
    String redeliveryNumber;

    @Schema(description = "if an estimate exists for the shipping container, the estimate number", required = false, nullable = true, example = "DEHAMCE1856373", minLength = 1, maxLength = 16)
    @Column(length = 16)
    String estimateNumber;

    @Schema(description = "if an estimate exists for the shipping container, the current revision of that estimate", required = false, nullable = true, type = "integer", format = "int32", example = "0")
    @Column
    Integer estimateRevision;

    @Schema(description = "if a work order authorizes activity for the shipping container, the work order number", required = false, nullable = true, example = "WHAMG46019", minLength = 1, maxLength = 16)
    @Column(length = 16)
    String workOrderNumber;

    @Schema(description = "the customer the shipping container is allocated to lease out to", required = false, nullable = true, implementation = ExternalParty.class)
    @ManyToOne(fetch = FetchType.EAGER)
    ExternalParty outCustomer;

    @Schema(description = "if the shipping container is allocated outbound, the release advice number authorizing it to leave the depot", required = false, nullable = true, example = "RHAMG000000", minLength = 1, maxLength = 16)
    @Column(length = 16)
    String releaseNumber;

    @ArraySchema(schema = @Schema(example = "An example unit level comment."))
    @Schema(description = "comments pertaining to this unit for the intended recipient of this message", required = false, nullable = false)
    @Lob
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable
    List<String> comments;
}
