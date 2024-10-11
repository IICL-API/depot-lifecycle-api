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
@Schema(description = "An approval to remove shipping containers from a storage location.", requiredProperties = {"releaseNumber", "status", "type", "approvalDate", "depot", "owner", "recipient", "details"})
@EqualsAndHashCode(of = {"releaseNumber"})
@ToString(of = {"releaseNumber"})
@Introspected
public class Release {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "the identifier for this release, also referred to as the advice number or release number", example = "AHAMG33141", maxLength = 16, required = true, nullable = false)
    @Column(nullable = false, length = 16)
    String releaseNumber;

    @Schema(required = true, nullable = false, description = "Describes the status for this release: \n\n`PENDING` - release is pending and not yet valid\n\n`APPROVED` - release is approved for leaving the storage location\n\n`COMPLETE` - all containers have left the depot and no more may be processed\n\n`EXPIRED` - the release is now expired and any remaining units are no longer valid\n\n`CANCELLED` - the release is cancelled and not valid", example = "APPROVED", implementation = ReleaseStatus.class)
    @Column(nullable = false, length = 9)
    @Enumerated(EnumType.STRING)
    ReleaseStatus status;

    @Schema(description = "Describes the intended purpose of the release: \n\n`SALE` - shipping containers are being sold\n\n`BOOK` - shipping containers are being leased to a customer\n\n`REPO` - shipping containers are being relocated by the owner to another storage location", required = true, nullable = false)
    @Column(nullable = false, length = 4)
    @Enumerated(EnumType.STRING)
    ReleaseType type;

    //Issue #124 micronaut-openapi - example is represented wrong, so example is not listed here. example = "2019-07-21T17:32:28Z"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    @Schema(description = "the date and time in the depot local time zone (i.e. `2019-07-21T17:32:28Z`) that this release is considered approved / effective\n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", type = "string", format = "date-time", required = true, nullable = false)
    @Column(nullable = false)
    ZonedDateTime approvalDate;

    //Issue #124 micronaut-openapi - example is represented wrong, so example is not listed here. example = "2020-07-21T17:32:28Z"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    @Schema(description = "the date and time in the depot local time zone (i.e. `2020-07-21T17:32:28Z`) that this release is considered no longer valid\n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", type = "string", format = "date-time", required = true, nullable = false)
    @Column
    ZonedDateTime expirationDate;

    @ArraySchema(schema = @Schema(example = "An example release level comment."))
    @Schema(description = "comments pertaining to this unit for the intended recipient of this message", required = false, nullable = false)
    @Lob
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable
    List<String> comments;

    @Schema(description = "The location for this release", required = true, implementation = Party.class)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party depot;

    @Schema(description = "The owner of the shipping container that approved the release", required = true, implementation = Party.class)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party owner;

    @Schema(description = "The intended recipient for this message representing a release", required = true, implementation = ExternalParty.class)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    ExternalParty recipient;

    @Schema(description = "indicates if an on-hire survey is required for units associated to this release")
    @Column
    Boolean onHireSurveyRequired;

    @Schema(description = "the number of shipping containers assigned to this release.", required = true, minimum = "0", example = "1")
    @Column(nullable = false)
    Integer quantity;

    @ArraySchema(minItems = 1, schema = @Schema(implementation = ReleaseDetail.class))
    @Schema(description = "groups of like-criteria units", required = true, nullable = false)
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    List<ReleaseDetail> details = new ArrayList<>();
}
