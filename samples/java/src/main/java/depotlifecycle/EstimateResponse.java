package depotlifecycle;

import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonView
@NoArgsConstructor
@Schema(description = "Indicates the allocated estimate totals, including any comments on the allocation and if the box is considered a constructive loss")
@Introspected
public class EstimateResponse {
    @Schema(required = true, type = "number", format = "double", minimum = "0.0", example = "544.95", description = "the total for the estimate")
    BigDecimal total;

    @Schema(required = true, type = "number", format = "double", minimum = "0.0", example = "0.00", description = "the total of the owner portion for the estimate")
    BigDecimal ownerTotal;

    @Schema(required = true, type = "number", format = "double", minimum = "0.0", example = "422.96", description = "the total of the customer portion for the estimate")
    BigDecimal customerTotal;

    @Schema(required = true, type = "number", format = "double", minimum = "0.0", example = "121.99", description = "the total of the insurance portion for the estimate")
    BigDecimal insuranceTotal;

    @Schema(required = true, type = "boolean", description = "indicates if the estimate causes the unit to be a constructive total lost", example = "false")
    Boolean ctl;

    @Schema(maxLength = 500, required = false, description = "comments pertaining to the estimate creation", example = "Base Currency is: EUR; Based on estimate, user damages total: 0.00; Calculated DV (base): 1151.84; Coverage amount: 121.99; Damage exceeds DPP Coverage.; Damages exceed coverage amount. User pays excess.; Total paid by DPP Coverage: 121.99; Total owed by user: 422.96;")
    String comments;

    @Schema(required = false, type = "object", description = "when possible, this is set to an expected sell/fix decision to indicate the likely estimate owner approval action")
    PreliminaryDecision preliminaryDecision;
}
