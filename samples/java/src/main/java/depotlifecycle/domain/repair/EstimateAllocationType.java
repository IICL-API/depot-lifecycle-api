package depotlifecycle.domain.repair;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
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
@Schema(description = "Represents a specific estimate transmission defined by a combination of estimate type and optional upgrade type", requiredProperties = {"type"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id", "type", "upgradeType"})
@Introspected
public class EstimateAllocationType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(defaultValue = "R", description = "delineates the type of estimate; whether it was the initial, primary estimate or an ancillary / secondary repair after the initial decision\n\n`R` - Primary (Unknown Estimation Standard)\n\n`RI` - Primary (IICL)\n\n`RC` - Primary (CWCA)\n\n`RW` - Primary (UCIRC)\n\n`SC` - Secondary (CWCA)\n\n`SW` - Secondary (UCIRC)\n\n`SU` - Secondary Upgrade\n\n`AS` - Sell Upgrade\n\n`AU` - Ancillary Upgrade\n\n`AR` - Ancillary Repair", required = true, nullable = false, implementation = EstimateType.class)
    @Column(length = 2, nullable = false)
    @Enumerated(EnumType.STRING)
    EstimateType type;

    @Schema(description = "the type of secondary upgrade this estimate transmission represents.\n\n`FG` - Food grade\n\n`ML` - Malt\n\n`DB` - Dairy Board\n\n`EV` - Evian\n\n`WH` - Whiskey\n\n`SU` - Sugar\n\n`CF` - Coffee\n\n`TB` - Tobacco\n\n`MC` - Milk cartons\n\n`MP` - Milk powder\n\n`AM` - Ammunition\n\n`CH` - Cotton/Hay\n\n`TE` - Tea\n\n`FT` - Flexitank", example = "AM", required = false, nullable = true, implementation = UpgradeType.class)
    @Column(length = 2, nullable = true)
    @Enumerated(EnumType.STRING)
    UpgradeType upgradeType;
}

