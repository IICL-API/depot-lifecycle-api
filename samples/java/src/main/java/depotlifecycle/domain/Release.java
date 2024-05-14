package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

    @Schema(required = true, nullable = false, description = "Describes the status for this release: \n\n`PENDING` - release is pending and not yet valid\n\n`APPROVED` - release is approved for leaving the storage location\n\n`COMPLETE` - all containers have left the depot and no more may be processed\n\n`EXPIRED` - the release is now expired and any remaining units are no longer valid\n\n`CANCELLED` - the release is cancelled and not valid", allowableValues = {"PENDING", "APPROVED", "COMPLETE", "EXPIRED", "CANCELLED"}, example = "APPROVED")
    @Column(nullable = false, length = 9)
    String status;

    @Schema(description = "Describes the intended purpose of the release: \n\n`SALE` - shipping containers are being sold\n\n`BOOK` - shipping containers are being leased to a customer\n\n`REPO` - shipping containers are being relocated by the owner to another storage location", allowableValues = {"SALE", "BOOK", "REPO"}, required = true, nullable = false)
    @Column(nullable = false, length = 4)
    String type;

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

    @ArraySchema(schema = @Schema(description = "comments pertaining to this unit for the intended recipient of this message", example = "An example release level comment.", required = false, nullable = false))
    @Schema(description = "comments pertaining to this unit for the intended recipient of this message", required = false, nullable = false)
    @Lob
    @ElementCollection
    @CollectionTable
    @LazyCollection(LazyCollectionOption.FALSE)
    List<String> comments;

    @Schema(description = "The location for this release", required = true)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party depot;

    @Schema(description = "The owner of the shipping container that approved the release", required = true)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party owner;

    @Schema(description = "The intended recipient for this message representing a release", required = true)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party recipient;

    @Schema(description = "indicates if an on-hire survey is required for units associated to this release")
    @Column
    Boolean onHireSurveyRequired;

    @Schema(description = "the number of shipping containers assigned to this release.", required = true, minimum = "0", example = "1")
    @Column(nullable = false)
    Integer quantity;

    @Schema(description = "groups of like-criteria units", required = true, minLength = 1, nullable = false)
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    List<ReleaseDetail> details = new ArrayList<>();
}
