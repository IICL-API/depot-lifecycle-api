package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "An approval to deliver units to a storage location.", requiredProperties = {"redeliveryNumber", "status", "approvalDate", "depot", "recipient", "owner", "details"})
@EqualsAndHashCode(of = {"redeliveryNumber"})
@ToString(of = {"redeliveryNumber"})
@Introspected
public class Redelivery {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "the identifier for this redelivery, also referred to as the advice number or redelivery number", example = "AHAMG33141", maxLength = 16, required = true, nullable = false)
    @Column(nullable = false, length = 16)
    String redeliveryNumber;

    //Issue #124 micronaut-openapi - example is represented wrong, so example is not listed here. example = "2019-07-21T17:32:28Z"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    @Schema(description = "the date and time in the depot local time zone (i.e. `2019-07-21T17:32:28Z`) that this redelivery is considered approved / effective\n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", type = "string", format = "date-time", required = true, nullable = false)
    @Column(nullable = false)
    ZonedDateTime approvalDate;

    //Issue #124 micronaut-openapi - example is represented wrong, so example is not listed here. example = "2020-07-21T17:32:28Z"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    @Schema(description = "the date and time in the depot local time zone (i.e. `2020-07-21T17:32:28Z`) that this redelivery is considered no longer valid\n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", type = "string", format = "date-time", required = false, nullable = true)
    @Column
    ZonedDateTime expirationDate;

    @Schema(required = true, nullable = false, description = "Describes the status for this redelivery: \n\n`PENDING` - redelivery is pending and not yet valid for turn in\n\n`APPROVED` - redelivery is approved\n\n`COMPLETE` - all units are turned in and no more may be turned in\n\n`EXPIRED` - the redelivery is now expired and any remaining units are no longer valid for turn in\n\n`CANCELLED` - the redelivery is cancelled and not valid for turn in", example = "APPROVED", implementation = RedeliveryStatus.class)
    @Column(nullable = false, length = 9)
    @Enumerated(EnumType.STRING)
    RedeliveryStatus status;

    @ArraySchema(schema = @Schema(example = "customer@example.com"))
    @Schema(description = "list of emails to notify for an estimate revision", required = false, nullable = false)
    @Lob
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable
    List<String> estimateEmailRecipients;

    @ArraySchema(schema = @Schema(example = "An example redelivery level comment."))
    @Schema(description = "comments pertaining to this unit for the intended recipient of this message", required = false, nullable = false)
    @Lob
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable
    List<String> comments;

    @Schema(description = "The location for this redelivery", required = true, nullable = false, implementation = Party.class)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party depot;

    @Schema(description = "the shipping container's owner", required = true, nullable = false, implementation = Party.class)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party owner;

    @Schema(description = "The intended recipient for this message representing a redelivery", required = true, nullable = false, implementation = Party.class)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party recipient;

    @Schema(description = "the number of shipping containers assigned to this redelivery.", required = true, nullable = false, minimum = "0", example = "1")
    @Column(nullable = false)
    Integer quantity;

    @ArraySchema(minItems = 1, schema = @Schema(implementation = RedeliveryDetail.class))
    @Schema(description = "groups of like-criteria units", required = true, nullable = false)
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    List<RedeliveryDetail> details = new ArrayList<>();
}
