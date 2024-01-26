package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Schema(description = "data required to create a gate in or gate out record", requiredProperties = {"adviceNumber", "depot", "unitNumber", "status", "activityTime", "type"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class GateCreateRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "the redelivery or release advice number for the gate record", example = "AHAMG000000", maxLength = 16, required = true, nullable = false)
    @Column(nullable = false, length = 16)
    String adviceNumber;

    @Schema(description = "the storage location for the given advice number", required = true, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party depot;

    @Schema(description = "the unit number of the shipping container", pattern = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", example = "CONU1234561", maxLength = 11, required = true, nullable = false)
    @Column(nullable = false, length = 11)
    String unitNumber;

    @Schema(allowableValues = {"A", "D", "S"}, example = "D", maxLength = 1, description = "an indicator of the shipping container's status\n\n`A` - Non-damaged\n\n`D` - Damaged\n\n`S` - Sold", required = true, nullable = false)
    @Column(nullable = false, length = 1)
    String status;

    //Issue #124 micronaut-openapi - example is represented wrong, so example is not listed here. example = "2019-04-10T19:37:04Z"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    @Schema(description = "the date and time of the gate activity in local time; i.e. `2019-04-10T19:37:04Z` \n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", type = "string", format = "date-time", required = true, nullable = false)
    @Column(nullable = false)
    ZonedDateTime activityTime;

    @Schema(description = "gate type indicator\n\n`IN` - Gate In\n\n`OUT` - Gate Out", maxLength = 3, example = "IN", allowableValues = {"IN", "OUT"}, required = true, nullable = false)
    @Column(nullable = false, length = 3)
    String type;

    @Schema(description = "An optional photo list of the shipping container at gate creation", required = false, nullable = false)
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL})
    List<GateCreatePhoto> photos = new ArrayList<>();
}
