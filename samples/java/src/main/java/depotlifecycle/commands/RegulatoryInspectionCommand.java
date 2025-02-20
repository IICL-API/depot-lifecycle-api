package depotlifecycle.commands;

import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.RegulatoryScope;
import depotlifecycle.domain.RegulatoryStatus;
import io.micronaut.core.annotation.Introspected;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@JsonView
@Introspected
public class RegulatoryInspectionCommand {
    @NotNull
    RegulatoryScope scope;

    @NotNull
    @NotBlank
    @Size(max = 16)
    String name;

    @Nullable
    LocalDate lastInspection;

    @Nullable
    @Min(0)
    Integer validFor;

    @NotNull
    RegulatoryStatus regulatoryStatus;

    @NotNull
    ExternalPartyCommand inspector;

    @Nullable
    List<String> comments;
}
