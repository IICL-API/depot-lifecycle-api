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
@Schema(description = "An approval to deliver units to a storage location.")
@EqualsAndHashCode(of= {"redeliveryNumber"} )
@ToString(of= {"redeliveryNumber"} )
@Introspected
public class Redelivery {
    @Id
    @Schema(description = "the identifier for this redelivery, also referred to as the advice number or redelivery number", example = "AHAMG33141", maxLength = 16, required = true)
    @Column(nullable = false, unique = true)
    String redeliveryNumber;

    //Issue #124 micronaut-openapi - example is represented wrong, so example is not listed here. example = "2020-07-21T17:32:28Z"
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    @Schema(description = "the date and time in the depot local time zone that this redelivery is considered no longer valid\n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", type = "string", format = "date-time")
    @Column()
    ZonedDateTime expirationDate;

    //Issue #124 micronaut-openapi - example is represented wrong, so example is not listed here. example = "2019-07-21T17:32:28Z"
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    @Schema(description = "the date and time in the depot local time zone that this redelivery is considered approved\n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", type = "string", format = "date-time")
    @Column(nullable = false)
    ZonedDateTime approvalDate;

    @Schema(description = "comments pertaining to this redelivery for the intended recipient of this message", maxLength = 500, example = "an example redelivery level comment")
    @Column(length = 500)
    String comments;

    @Schema(description = "The location for this redelivery", required = true)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party depot;

    @Schema(description = "The intended recipient for this message representing a redelivery", required = true)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party recipient;

    @Schema(description = "groups of like-criteria units", required = true)
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    List<RedeliveryDetail> details = new ArrayList<>();
}
