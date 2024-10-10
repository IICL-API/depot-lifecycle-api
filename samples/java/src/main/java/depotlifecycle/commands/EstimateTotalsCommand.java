package depotlifecycle.commands;

import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonView
@Introspected
public class EstimateTotalsCommand {
    @NotNull
    @NotBlank
    @Size(max = 16)
    String estimateNumber;

    @Nullable
    Long relatedId;

    @NotNull
    Integer revision;

    @NotNull
    PartyCommand depot;

    @NotNull
    @Min(0)
    BigDecimal total;

    @NotNull
    @Min(0)
    BigDecimal ownerTotal;

    @NotNull
    @Min(0)
    BigDecimal customerTotal;

    @NotNull
    @Min(0)
    BigDecimal insuranceTotal;

    @NotNull
    Boolean ctl = false;

    @Nullable
    @Size(max = 500)
    String comments;

    @Nullable
    PreliminaryDecisionCommand preliminaryDecision;
}
