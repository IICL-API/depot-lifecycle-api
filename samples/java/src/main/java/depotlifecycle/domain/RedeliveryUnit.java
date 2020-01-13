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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table(name = "redeliveryUnit")
@Schema(description = "information for a specific unit on a redelivery")
@EqualsAndHashCode(of= {"id"} )
@ToString(of= {"id"} )
@Introspected
public class RedeliveryUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @ManyToOne(optional = false)
    @JsonIgnore
    RedeliveryDetail redeliveryDetail;

    @Schema(description = "the current remark of the shipping container", pattern = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", required = true, example = "CONU1234561")
    @Column(name = "unitNumber", nullable = false)
    String unitNumber;

    @Schema(description = "date and month this unit was manufactured\n( full-date notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", example = "2001-07-21", type = "string", format = "date", required = true)
    @Column(name = "manufactureDate", nullable = false)
    LocalDate manufactureDate;

    @Schema(description = "the date this unit was on hired to the customer for this unit's detail\n( full-date notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", example = "2001-07-21", type = "string", format = "date")
    @Column(name = "lastOnHireDate")
    LocalDate lastOnHireDate;

    @Schema(description = "The location this unit was last on-hired.")
    @ManyToOne()
    Party lastOnHireLocation;

    @Schema(description = "comments pertaining to this unit for the intended recipient of this message", maxLength = 500, example = "Unit requires reefer testing before repair!")
    @Column(name = "comments", nullable = false, length = 500)
    String comments;
}
