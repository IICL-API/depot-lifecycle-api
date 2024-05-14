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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "Represents the approval of the customer amount portion.", requiredProperties = {"approvalDateTime", "approvalTotal"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class EstimateCustomerApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "The approval number from the customer", example = "RAMON ROUBAL", maxLength = 128, required = false, nullable = true)
    @Column(length = 128)
    String approvalNumber;

    //Issue #124 micronaut-openapi - example is represented wrong, so example is not listed here. example = "2017-04-10T19:37:04Z"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    @Schema(description = "The time the estimate was approved by the customer; i.e. `2017-04-10T19:37:04Z` \n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", type = "string", format = "date-time", required = true, nullable = false)
    @Column(nullable = false)
    ZonedDateTime approvalDateTime;

    @Schema(description = "the user name or code who approved this estimate on behalf of the customer", example = "KAERTS", maxLength = 64, required = false, nullable = true)
    @Column(length = 64)
    String approvalUser;

    @Schema(description = "the amount in the currency of the estimate that's approved by the customer", type = "number", format = "double", required = true, minimum = "0.00", example = "422.96", nullable = false)
    @Column(name = "amountCovered", nullable = false)
    BigDecimal approvalTotal;
}
