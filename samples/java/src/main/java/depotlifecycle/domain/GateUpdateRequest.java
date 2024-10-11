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
@Schema(description = "Data required to update a gate in or gate out record. Any data not provided will result in no update.")
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class GateUpdateRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(example = "D", description = "a new indicator of the shipping container's status\n\n`A` - Non-damaged\n\n`D` - Damaged\n\n`S` - Sold", required = false, nullable = true)
    @Column(nullable = true, length = 1)
    @Enumerated(EnumType.STRING)
    GateRequestStatus status;

    //Issue #124 micronaut-openapi - example is represented wrong, so example is not listed here. example = "2019-04-10T19:37:04Z"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    @Schema(description = "the new date and time of the gate activity in local time; i.e. `2019-04-10T19:37:04Z` \n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", type = "string", format = "date-time", required = false, nullable = true)
    @Column
    ZonedDateTime activityTime;

    @Schema(description = "gate type indicator\n\n`IN` - Gate In\n\n`OUT` - Gate Out", maxLength = 3, example = "IN", required = false, nullable = true)
    @Column(nullable = true, length = 3)
    @Enumerated(EnumType.STRING)
    GateRequestType type;

    @ArraySchema(schema = @Schema(implementation = GatePhoto.class))
    @Schema(description = "An optional photo list of the shipping container at gate update", required = false, nullable = false)
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    List<GatePhoto> photos = new ArrayList<>();
}
