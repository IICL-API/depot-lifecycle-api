package depotlifecycle.domain.repair;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "an eligible repair criteria for or not for insurance coverage")
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class InsuranceCoverageItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "specifies the damage location code on a container, if null all values are considered acceptable\n\n[see IICL Preferred Location Codes, Section 5.7](https://www.iicl.org/iiclforms/assets/File/public/bulletins/TB002_EDIS_February_2003.pdf)", required = false, pattern = "^[A-Z0-9]{4}$", example = "UR1N", minLength = 4, maxLength = 4)
    @Column(length = 4)
    String location;

    @Schema(description = "component code, if null all values are considered acceptable\n\n[see IICL Preferred Component Codes, Section 5.1](https://www.iicl.org/iiclforms/assets/File/public/bulletins/TB002_EDIS_February_2003.pdf)", required = true, nullable = false, pattern = "^[A-Z0-9]{3}$", example = "CMA", maxLength = 3)
    @Column(length = 3)
    String component;

    @Schema(description = "damage code, if null all values are considered acceptable\n\n[see IICL Preferred Damage Codes, Section 5.2](https://www.iicl.org/iiclforms/assets/File/public/bulletins/TB002_EDIS_February_2003.pdf)", required = true, nullable = false, pattern = "^[A-Z0-9]{2}$", example = "CK", maxLength = 2)
    @Column(length = 2)
    String damage;

    @Schema(description = "repair code, if null all values are considered acceptable\n\n[see IICL Preferred Repair Codes, Section 5.4](https://www.iicl.org/iiclforms/assets/File/public/bulletins/TB002_EDIS_February_2003.pdf)", required = true, nullable = false, pattern = "^[A-Z0-9]{2}$", example = "IT", maxLength = 2)
    @Column(length = 2)
    String repair;

    @Schema(description = "component material code, if null all values are considered acceptable\n\n[see IICL Preferred Material Type Codes, Section 5.5](https://www.iicl.org/iiclforms/assets/File/public/bulletins/TB002_EDIS_February_2003.pdf)", required = true, nullable = false, pattern = "^[A-Z0-9]{2}$", example = "MU", maxLength = 2)
    @Column(length = 2)
    String material;

    @ArraySchema(schema = @Schema(implementation = EstimateLineItemPart.class))
    @Schema(description = "An optional, detailed part list covered under this repair criteria", required = false, nullable = false)
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    List<EstimateLineItemPart> parts = new ArrayList<>();
}
