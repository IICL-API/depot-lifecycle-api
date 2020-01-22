package depotlifecycle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.InsuranceCoverage;
import depotlifecycle.domain.Party;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@JsonView
@NoArgsConstructor
@Schema(description = "the current gate status of a given shipping container", requiredProperties = {"adviceNumber", "depot", "status", "activityTime", "currentInspectionCriteria"})
@Introspected
public class GateStatus {
    @Schema(required = true, description = "the redelivery or release advice number for the gate record", example = "AHAMG000000", maxLength = 16)
    String adviceNumber;

    @Schema(required = true, description = "the storage location for the given advice number")
    Party depot;

    @Schema(required = true, allowableValues = {"A", "D"}, example = "D", maxLength = 1, description = "the current damage indicator status\n\n`A` - Non-damaged\n\n`D` - Damaged")
    String status;

    //Issue #124 micronaut-openapi - example is represented wrong, so example is not listed here. example = "2017-07-21T17:32:28Z"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    @Schema(description = "the date and time of the gate activity in local time\n\n( notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", type = "string", format = "date-time", required = true)
    ZonedDateTime activityTime;

    @Schema(description = "The applicable insurance coverage for damage estimate purposes", required = false)
    InsuranceCoverage insuranceCoverage;

    @Schema(description = "the exchange rate to convert billed currency to the local currency for damage estimate totals", required = false, type = "number", format = "double", example = "0.8133")
    BigDecimal currentExchangeRate;

    @Schema(description = "the last reported grade or category standard this unit", required = true, example = "IICL", maxLength = 10)
    String currentInspectionCriteria;
}
