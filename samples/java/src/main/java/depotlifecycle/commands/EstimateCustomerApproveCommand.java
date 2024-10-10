package depotlifecycle.commands;

import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonView
@Introspected
public class EstimateCustomerApproveCommand extends BaseCustomerApprovalCommand {
    @NotNull
    @NotBlank
    @Size(max = 16)
    String estimateNumber;

    @NotNull
    @NotBlank
    @Size(max = 9)
    @Pattern(regexp = "^[A-Z0-9]{9}$", message = "Depot must match the Company Id pattern.")
    String depot;
}
