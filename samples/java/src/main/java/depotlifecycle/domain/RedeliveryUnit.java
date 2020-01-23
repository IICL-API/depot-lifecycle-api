package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "information for a specific unit on a redelivery", requiredProperties = {"unitNumber", "manufactureDate", "status"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class RedeliveryUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @ManyToOne(optional = false)
    @JsonIgnore
    RedeliveryDetail redeliveryDetail;

    @Schema(description = "the current remark of the shipping container", pattern = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", required = true, example = "CONU1234561", maxLength = 11)
    @Column(nullable = false, length = 11)
    String unitNumber;

    @Schema(description = "date and month this unit was manufactured\n\n( full-date notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", example = "2001-07-21", type = "string", format = "date", required = true)
    @Column(nullable = false)
    LocalDate manufactureDate;

    @Schema(description = "the date this unit was on hired to the customer for this unit's detail\n\n( full-date notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", example = "2001-07-21", type = "string", format = "date")
    @Column
    LocalDate lastOnHireDate;

    @Schema(description = "The location this unit was last on-hired.")
    @ManyToOne(fetch = FetchType.EAGER)
    Party lastOnHireLocation;

    @Schema(description = "Describes the state of the shipping container for this redelivery: \n\n`TIED` - shipping container is assigned to this redelivery and ready to turn in.\n\n`REMOVED` - shipping container was attached to this redelivery, but is no longer valid for redelivery.\n\n`LOT` - shipping container has turned into the storage location of this redelivery.", allowableValues = {"REMOVED", "TIED", "TIN"}, example = "TIED")
    @Column(nullable = false, length = 7)
    String status;

    @Schema(description = "comments pertaining to this unit for the intended recipient of this message", maxLength = 500, example = "Unit requires reefer testing before repair!")
    @Column(length = 500)
    String comments;
}
