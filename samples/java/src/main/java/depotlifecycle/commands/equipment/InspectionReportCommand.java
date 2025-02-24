package depotlifecycle.commands.equipment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.commands.ExternalPartyCommand;
import depotlifecycle.domain.equipment.InspectionReport;
import depotlifecycle.domain.equipment.MandateLevel;
import depotlifecycle.domain.equipment.InspectionResult;
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
public class InspectionReportCommand {
    @NotNull
    @NotBlank
    @Size(max = 16)
    String name;

    @NotNull
    MandateLevel mandateLevel;

    @Nullable
    LocalDate lastInspection;

    @Nullable
    @Min(0)
    Integer validFor;

    @NotNull
    InspectionResult result;

    @NotNull
    ExternalPartyCommand inspector;

    @Nullable
    List<String> comments;

    @JsonIgnore
    public InspectionReport toInspectionReport() {
        InspectionReport inspectionReport = new InspectionReport();
        inspectionReport.setName(name);
        inspectionReport.setMandateLevel(mandateLevel);
        inspectionReport.setLastInspection(lastInspection);
        inspectionReport.setValidFor(validFor);
        inspectionReport.setResult(result);
        inspectionReport.setInspector(inspector.toExternalParty());
        inspectionReport.setComments(comments);
        return inspectionReport;
    }
}
