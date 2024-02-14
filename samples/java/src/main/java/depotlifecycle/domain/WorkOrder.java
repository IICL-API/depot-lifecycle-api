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
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "An approval of a damage estimate signifying a depot may repair a shipping container", requiredProperties = {"workOrderNumber", "depot", "owner", "type", "approvalDate", "lineItems"})
@EqualsAndHashCode(of = {"workOrderNumber"})
@Introspected
@ToString(of = {"workOrderNumber"})
public class WorkOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "*Field is currently proposed to be added - not currently production approved.*\n\nAn internal system identifier to be used to upload Estimate Photos or compare related activities.", type = "integer", format = "int64", example = "10102561", required = false, nullable = true)
    @Column(nullable = true)
    Long relatedId;

    @Schema(description = "the identifier for this work order; this will be the approval number for repairs", example = "WHAMG46019", minLength = 1, maxLength = 16, required = true, nullable = false)
    @Column(nullable = false, length = 16)
    String workOrderNumber;

    @Schema(required = true, nullable = false, description = "the storage location where the shipping container is being repaired")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party depot;

    @Schema(required = true, nullable = false, description = "the owner of the shipping container")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party owner;

    @Schema(required = false, nullable = true, description = "the party that will bill the customer portion of damages for this repair")
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    Party billingParty;

    @Schema(description = "the type of repair approved", example = "SELLCWCA", required = true, nullable = false, maxLength = 11)
    @Column(nullable = false, length = 11)
    String type;

    //Issue #124 micronaut-openapi - example is represented wrong, so example is not listed here. example = "2017-05-10T19:37:04Z"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    @Schema(description = "the date and time in the depot local time zone (i.e. `2017-05-10T19:37:04Z`) that this work order is authorized for repair\n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", type = "string", format = "date-time", required = true, nullable = false)
    @Column(nullable = false)
    ZonedDateTime approvalDate;

    @Schema(description = "the total amount approved for repair", required = false, nullable = true, type = "number", format = "double", example = "175.00")
    @Column
    BigDecimal approvalTotal;

    @Schema(description = "the currency of the approval total", required = false, nullable = true, example = "EUR", pattern = "^[A-Z]{3}$", maxLength = 3)
    @Column(length = 3)
    String approvalCurrency;

    //Issue #124 micronaut-openapi - example is represented wrong, so example is not listed here. example = "2020-07-21T17:32:28Z"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    @Schema(description = "the date and time in the depot local time zone (i.e. `2020-07-21T17:32:28Z`) that this repair must be completed by\n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", required=false, nullable = true, type = "string", format = "date-time")
    @Column
    ZonedDateTime expirationDate;

    @Schema(description = "comments pertaining to this repair for the intended recipient of this message", maxLength = 500, example = "CWCA repairs for unit TCKU3456654 total 175.00 EUR per DEHAMCE1856373.1", required = false, nullable = true)
    @Column(length = 500)
    String comments;

    @Schema(description = "units associated to this work order", required = true, nullable = false, minLength = 1, maxLength = 200)
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    List<WorkOrderUnit> lineItems = new ArrayList<>();
}
