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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "Necessary information to mark a specific unit under a work order repaired", requiredProperties = {"workOrderNumber", "depot", "completionDate", "unitNumber"})
@EqualsAndHashCode(of = {"workOrderNumber", "unitNumber"})
@Introspected
@ToString(of = {"workOrderNumber", "unitNumber"})
public class RepairComplete {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "the identifier of the work order to repair complete", example = "WHAMG46019", minLength = 1, maxLength = 16, required = true, nullable = false)
    @Column(nullable = false, length = 16)
    String workOrderNumber;

    @Schema(required = true, nullable = false, description = "the storage location where the shipping container is being repaired", implementation = Party.class)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party depot;

    //Issue #124 micronaut-openapi - example is represented wrong, so example is not listed here. example = "2018-04-10T19:37:04Z"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    @Schema(required = true, nullable = false, description = "the date and time in the depot local time zone (i.e. `2018-04-10T19:37:04Z`) that this work order was repaired.\n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", type = "string", format = "date-time")
    @Column(nullable = false)
    ZonedDateTime completionDate;

    @Schema(description = "the unit number of the shipping container at the time of repair approval", pattern = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", required = true, nullable = false, example = "CONU1234561", maxLength = 11)
    @Column(nullable = false, length = 11)
    String unitNumber;
}
