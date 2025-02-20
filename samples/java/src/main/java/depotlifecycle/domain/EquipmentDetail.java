package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "Provides various equipment information related to a shipping container")
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class EquipmentDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "the equipment type ISO code or an internal code if one does not exist for this shipping container", required = true, nullable = false, example = "22G1", maxLength = 10)
    @Column(nullable = false, length = 10)
    String equipment;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @Schema(description = "if this detail is for a reefer shipping container, then this details the cooling machinery information", required = false, nullable = true, implementation = MachineryInfo.class)
    MachineryInfo machineryInfo;

    @Schema(description = "date and month this unit was manufactured\n\n( full-date notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", example = "2001-07-21", type = "string", format = "date", required = true, nullable = false)
    @Column(nullable = false)
    LocalDate manufactureDate;

    @Schema(example = "-23", description = "the reefer setpoint / desired temperature in Celsius", required = false, nullable = true)
    @Column
    Integer desiredTemperature;

    @Schema(example = "65", description = "the reefer desired humidity percentage", required = false, nullable = true)
    @Column
    Integer desiredHumidity;

    @ArraySchema(schema = @Schema(implementation = RegulatoryInspection.class))
    @Schema(description = "the regulatory inspections related to this shipping container", required = false, nullable = false)
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    List<RegulatoryInspection> inspections = new ArrayList<>();

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @Schema(description = "if this detail is for a Chassis, then this details specific Chassis information", required = false, nullable = true, implementation = ChassisInfo.class)
    ChassisInfo chassisInfo;
}
