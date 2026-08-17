package depotlifecycle.domain.redelivery;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.repair.UpgradeType;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "an additional estimate the depot should produce for this unit, beyond the primary `inspectionCriteria`; the criteria dictates the estimate type", requiredProperties = {"inspectionCriteria"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id", "inspectionCriteria", "upgradeType"})
@Introspected
public class SecondaryInspectionCriteria {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "the estimate standard this additional estimate should be estimated to", required = true, nullable = false, example = "SC", minLength = 1, maxLength = 10)
    @Column(nullable = false, length = 10)
    String inspectionCriteria;

    @Schema(description = "the type of secondary upgrade this estimate transmission represents.\n\n`FG` - Food grade\n\n`ML` - Malt\n\n`DB` - Dairy Board\n\n`EV` - Evian\n\n`WH` - Whiskey\n\n`SU` - Sugar\n\n`CF` - Coffee\n\n`TB` - Tobacco\n\n`MC` - Milk cartons\n\n`MP` - Milk powder\n\n`AM` - Ammunition\n\n`CH` - Cotton/Hay\n\n`TE` - Tea\n\n`FT` - Flexitank", example = "AM", required = false, nullable = true, implementation = UpgradeType.class)
    @Column(length = 2, nullable = true)
    @Enumerated(EnumType.STRING)
    UpgradeType upgradeType;
}
