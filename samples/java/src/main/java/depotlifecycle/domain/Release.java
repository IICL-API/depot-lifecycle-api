package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "An approval to remove shipping containers from a storage location.", requiredProperties = {"releaseNumber", "type", "approvalDate", "depot", "owner", "recipient", "details"})
@EqualsAndHashCode(of = {"releaseNumber"})
@ToString(of = {"releaseNumber"})
@Introspected
public class Release {
    @Id
    @Schema(description = "the identifier for this release, also referred to as the advice number or release number", example = "AHAMG33141", maxLength = 16, required = true)
    @Column(nullable = false, unique = true, length = 16)
    String releaseNumber;

    @Schema(description = "Describes the intended purpose of the release: \n\n`SALE` - Shipping Containers are being sold\n\n`BOOK` - Shipping Containers are being leased to a customer\n\n`REPO` - Shipping Containers are being relocated by the owner to another storage location", allowableValues = {"SALE", "BOOK", "REPO"}, required = true)
    @Column(nullable = false, length = 4)
    String type;

    //Issue #124 micronaut-openapi - example is represented wrong, so example is not listed here. example = "2020-07-21T17:32:28Z"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    @Schema(description = "the date and time in the depot local time zone (i.e. `2020-07-21T17:32:28Z`) that this release is considered no longer valid\n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", type = "string", format = "date-time")
    @Column
    ZonedDateTime expirationDate;

    //Issue #124 micronaut-openapi - example is represented wrong, so example is not listed here. example = "2019-07-21T17:32:28Z"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    @Schema(description = "the date and time in the depot local time zone (i.e. `2019-07-21T17:32:28Z`) that this release is considered approved\n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", type = "string", format = "date-time")
    @Column(nullable = false)
    ZonedDateTime approvalDate;

    @Schema(description = "comments pertaining to this release for the intended recipient of this message", maxLength = 500, example = "an example release level comment")
    @Column(length = 500)
    String comments;

    @Schema(description = "The location for this release", required = true)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party depot;

    @Schema(description = "The owner of the shipping container that approved the release", required = true)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party owner;

    @Schema(description = "The intended recipient for this message representing a release", required = true)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party recipient;

    @Schema(description = "groups of like-criteria units", required = true, minLength = 1)
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    List<ReleaseDetail> details = new ArrayList<>();
}
