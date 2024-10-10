package depotlifecycle.commands;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@JsonView
@Introspected
public class BaseCustomerApprovalCommand {
    @Nullable
    @Size(max = 128)
    String approvalNumber;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Z")
    ZonedDateTime approvalDateTime;

    @Nullable
    @Size(max = 64)
    String approvalUser;

    @NotNull
    @Min(0)
    BigDecimal approvalTotal;
}
