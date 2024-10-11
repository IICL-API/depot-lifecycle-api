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

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "Represents an estimation of costs to repair or upgrade a shipping container.", requiredProperties = {"estimateNumber", "unitNumber", "condition", "estimateTime", "depot", "currency", "total", "exchangeRate"})
@EqualsAndHashCode(of = {"estimateNumber", "depot", "revision"})
@ToString(of = {"estimateNumber", "depot", "revision"})
@Introspected
public class Estimate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "The identifier for this estimate", example = "DEHAMCE1856373", minLength = 1, maxLength = 16, required = true, nullable = false)
    @Column(nullable = false, length = 16)
    String estimateNumber;

    @Schema(description = "The current unit number of the shipping container.", pattern = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", required = true, nullable = false, example = "CONU1234561", maxLength = 11)
    @Column(name = "unitNumber", nullable = false, length = 11)
    String unitNumber;

    @Schema(description = "an indicator on the status of the estimate and where it is in the revision process\n\n`D` - Damaged, Initial Estimate\n\n`E` - Customer Surveyed, No Approval\n\n`F` - Customer Approved Estimate, No Survey\n\n`G` - Customer Approved Estimate, Surveyed\n\n`L` - Owner Surveyed, No Approval", required = true, nullable = false, implementation = EstimateCondition.class)
    @Column(length = 1, nullable = false)
    EstimateCondition condition;

    //Issue #124 micronaut-openapi - example is represented wrong, so example is not listed here. example = "2020-07-21T17:32:28Z"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    @Schema(description = "the date and time of the estimate revision activity in local time; i.e. `2020-07-21T17:32:28Z` \n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", type = "string", format = "date-time", required = true, nullable = false)
    @Column
    ZonedDateTime estimateTime;

    @Schema(description = "the comments concerning this estimate", maxLength = 500, required = false, nullable = true)
    @Column(length = 500)
    String comments;

    @Schema(description = "the party submitting this estimate", required = false, nullable = true, implementation = Party.class)
    @ManyToOne(fetch = FetchType.EAGER)
    Party requester;

    @Schema(description = "the location of this estimate", required = true, nullable = false, implementation = Party.class)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "depot_id", nullable = false)
    Party depot;

    @Schema(description = "the shipping container's owner", required = false, nullable = true, implementation = Party.class)
    @ManyToOne(fetch = FetchType.EAGER)
    Party owner;

    @Schema(description = "the lessee of the shipping container to whom customer line items should apply.", required = false, nullable = true, implementation = Party.class)
    @ManyToOne(fetch = FetchType.EAGER)
    Party customer;

    @Schema(description = "the currency of amounts entered on this estimate", required = true, nullable = false, example = "EUR", pattern = "^[A-Z]{3}$", maxLength = 3)
    @Column(length = 3, nullable = false)
    String currency;

    @Schema(description = "the sum of billed line items for this estimate", required = true, nullable = false, type = "number", format = "double", example = "544.95")
    @Column(nullable = false)
    BigDecimal total;

    @Schema(description = "the exchange rate to convert billed currency to the local currency of this estimate", required = true, nullable = false, type = "number", format = "double", example = "0.8133", accessMode = Schema.AccessMode.READ_ONLY )
    @Column(nullable = true)
    BigDecimal exchangeRate;

    @Schema(description = "lessee approval information for this estimate", required = false, nullable = true, implementation = EstimateCustomerApproval.class)
    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    EstimateCustomerApproval customerApproval;

    @Schema(defaultValue = "R", description = "delineates the type of estimate; whether it was the initial, primary estimate or an ancillary / secondary repair after the initial decision\n\n`R` - Primary (Unknown Estimation Standard)\n\n`RI` - Primary (IICL)\n\n`RC` - Primary (CWCA)\n\n`SC` - Secondary (CWCA)\n\n`SU` - Secondary Upgrade\n\n`AS` - Sell Upgrade\n\n`AU` - Ancillary Upgrade\n\n`AR` - Ancillary Repair", required = false, nullable = true, implementation = EstimateType.class)
    @Column(length = 2)
    @Enumerated(EnumType.STRING)
    EstimateType type = EstimateType.R;

    @Schema(description = "the type of secondary upgrade this estimate represents.\n\n`FG` - Food grade\n\n`ML` - Malt\n\n`DB` - Dairy Board\n\n`EV` - Evian\n\n`WH` - Whiskey\n\n`SU` - Sugar\n\n`CF` - Coffee\n\n`TB` - Tobacco\n\n`MC` - Milk cartons\n\n`MP` - Milk powder\n\n`AM` - Ammunition\n\n`CH` - Cotton/Hay\n\n`TE` - Tea\n\n`FT` - Flexitank", example = "AM", required = false, nullable = true, implementation = UpgradeType.class)
    @Column(length = 2, nullable = true)
    @Enumerated(EnumType.STRING)
    UpgradeType upgradeType;

    @Schema(description = "the revision number of the estimate", type = "integer", format = "int32", example = "0", required = false, nullable = true)
    @Column(nullable = true)
    Integer revision;

    @ArraySchema(minItems = 0, schema = @Schema( implementation = EstimateLineItem.class))
    @Schema(description = "detailed damage descriptions that when combined represent the damages being repaired by this estimate", required = false, nullable = false)
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    List<EstimateLineItem> lineItems = new ArrayList<>();

    @ArraySchema(schema = @Schema(implementation = EstimatePhoto.class))
    @Schema(description = "An optional photo list for the shipping container damages.", required = false, nullable = false)
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    List<EstimatePhoto> photos = new ArrayList<>();

    // https://github.com/swagger-api/swagger-ui/issues/5418 prevents the read only access from correctly working so explicitly mention this in the description
    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @Schema(description = "the amount break downs by party for this estimate; typically only defined for a response and not included in requests", accessMode = Schema.AccessMode.READ_ONLY, nullable = false, required = true, implementation = EstimateAllocation.class)
    EstimateAllocation allocation;

    @JsonIgnore
    public BigDecimal getPartyTotal(EstimateLineItemParty party) {
        BigDecimal total = BigDecimal.ZERO;
        for (EstimateLineItem lineItem : lineItems) {
            if(!Objects.isNull(lineItem.party) && lineItem.party == party) {
                total = total.add(lineItem.getTotal());
            }
        }

        return total;
    }
}
