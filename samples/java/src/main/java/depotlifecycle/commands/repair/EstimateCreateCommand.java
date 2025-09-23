package depotlifecycle.commands.repair;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.commands.PartyCommand;
import depotlifecycle.domain.repair.EstimateCondition;
import depotlifecycle.domain.repair.EstimateType;
import depotlifecycle.domain.repair.UpgradeType;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@JsonView
@Introspected
public class EstimateCreateCommand {
    @NotNull
    @NotBlank
    @Size(max = 16)
    String estimateNumber;

    @NotNull
    @NotBlank
    @Size(min = 10, max = 11)
    @Pattern(regexp = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", message = "Unit Number must match the Unit Number pattern.")
    String unitNumber;

    @NotNull
    EstimateCondition condition;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Z")
    ZonedDateTime estimateTime;

    @Nullable
    @NotBlank
    @Size(max = 500)
    String comments;

    @Nullable
    PartyCommand requester;

    @NotNull
    PartyCommand depot;

    @Nullable
    PartyCommand owner;

    @Nullable
    PartyCommand customer;

    @NotNull
    @NotBlank
    @Size(max = 3)
    String currency;

    @NotNull
    BigDecimal total;

    @Nullable
    BaseCustomerApprovalCommand customerApproval;

    @Nullable
    EstimateType type;

    @Nullable
    UpgradeType upgradeType;

    @Nullable
    Integer revision;

    @Nullable
    List<EstimatePhotoCommand> photos;

    @Nullable
    List<EstimateLineItemCommand> lineItems;

    @Nullable
    List<EstimateTaxRateCommand> taxRates;
}
