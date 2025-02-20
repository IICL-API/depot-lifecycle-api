package depotlifecycle.domain;

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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "Provides Genset specific information related to a shipping container")
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class GensetInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "the last date that maintenance was performed\n\n( full-date notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", example = "2001-07-21", type = "string", format = "date", required = false, nullable = true)
    @Column(nullable = true)
    LocalDate lastMaintenanceDate;

    @Schema(description = "the number runtime hours at the last maintenance", type = "integer", format = "int32", required = false, nullable = true, example = "12", minimum = "0")
    @Column(nullable = true)
    Integer lastMaintenanceHours;

    @Schema(description = "the last date that the belt was changed\n\n( full-date notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", example = "2001-07-21", type = "string", format = "date", required = false, nullable = true)
    @Column(nullable = true)
    LocalDate lastBeltChangeDate;

    @Schema(description = "the number runtime hours at the last belt change", type = "integer", format = "int32", required = false, nullable = true, example = "12", minimum = "0")
    @Column(nullable = true)
    Integer lastBeltChangeHours;

    @Schema(description = "the total runtime hours", type = "integer", format = "int32", required = false, nullable = true, example = "24", minimum = "0")
    @Column(nullable = true)
    Integer totalHours;

    @Schema(description = "the current runtime hours", type = "integer", format = "int32", required = false, nullable = true, example = "1", minimum = "0")
    @Column(nullable = true)
    Integer currentHours;

    @Schema(description = "the fuel level as a percentage", type = "integer", format = "int32", required = false, nullable = true, example = "100", minimum = "0", maximum = "100")
    @Column(nullable = true)
    Integer fuelLevel;

    @Schema(description = "the mechanism that a genset attaches to a container.\n\n`CLIP` - Clip\n\n`UNDER` - Under slung", example = "CLIP", required = false, nullable = true, implementation = AttachmentType.class)
    @Column(nullable = true, length = 4)
    @Enumerated(EnumType.STRING)
    AttachmentType attachmentType;
}
