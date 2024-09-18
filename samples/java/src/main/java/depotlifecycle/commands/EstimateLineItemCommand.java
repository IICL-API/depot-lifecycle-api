package depotlifecycle.commands;

import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.EstimateLineItemParty;
import depotlifecycle.domain.EstimateTaxRule;
import depotlifecycle.domain.UnitOfMeasure;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonView
@Introspected
public class EstimateLineItemCommand {
    @NotNull
    @NotBlank
    Integer line;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]{2}$", message = "Invalid Repair Code.")
    String repair;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]{2}$", message = "Invalid Damage Code.")
    String damage;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]{2}$", message = "Invalid Material Code.")
    String material;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]{3}$", message = "Invalid Component Code.")
    String component;

    @Nullable
    @Pattern(regexp = "^[A-Z0-9]{4}$", message = "Invalid Location Code.")
    String location;

    @Nullable
    @Min(0)
    Integer length;

    @Nullable
    @Min(0)
    Integer width;

    @Nullable
    @Min(0)
    Integer height;

    @Nullable
    UnitOfMeasure unitOfMeasure;

    @NotNull
    BigDecimal hours;

    @NotNull
    BigDecimal materialCost;

    @NotNull
    BigDecimal laborRate;

    @NotNull
    EstimateLineItemParty party;

    @Nullable
    @Size(max = 500)
    String comments;

    @Nullable
    EstimateTaxRule taxRule;

    @Nullable
    @Min(1)
    Integer quantity;

    @Nullable
    List<EstimateLineItemPartCommand> parts;

    @Nullable
    List<EstimatePhotoCommand> photos;
}
