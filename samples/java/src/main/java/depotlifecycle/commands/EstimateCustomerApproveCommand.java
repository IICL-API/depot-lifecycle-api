package depotlifecycle.commands;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Introspected
public class EstimateCustomerApproveCommand {
    @NonNull
    @Size(max = 16)
    String estimateNumber;

    @NonNull
    @Size(max = 9)
    @Pattern(regexp = "^[A-Z0-9]{9}$", message = "Depot must match the Company Id pattern.")
    String depot;

    @Nullable
    @Size(max = 128)
    String approvalNumber;

    @NonNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    ZonedDateTime approvalDateTime;

    @Nullable
    @Size(max = 64)
    String approvalUser;

    @NonNull
    @Min(0)
    BigDecimal approvalTotal;
}
