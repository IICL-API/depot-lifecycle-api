package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"estimate_id", "description"})})
@Schema(description = "Represents a tax rate to be used on an estimate", requiredProperties = {"estimate", "description", "rule", "rate"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class EstimateTaxRate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @ManyToOne(optional = false)
    @JsonIgnore
    @JoinColumn(name="estimate_id")
    Estimate estimate;

    @Schema(description = "a unique, descriptive explanation for this tax rate", required = true, nullable = false, example = "PST Labor Tax Rate", maxLength = 255)
    @Column(nullable = false, length = 255)
    String description;

    @Schema(description = "which amount should taxes apply\n\n`B` - Both Labor Cost & Material Cost\n\n`L` - Labor Cost\n\n`M` - Material Cost\n\n_Note: Neither(N) is not an allowed value on this domain_", required = true, nullable = false, example = "B", implementation = EstimateTaxRule.class)
    @Column(length = 1)
    @Enumerated(EnumType.STRING)
    EstimateTaxRule rule;

    @Schema(description = "the tax rate as a percentage", required = true, nullable = false, type = "number", format = "double", minimum = "0.00", example = "19.5")
    @Column(nullable = false)
    BigDecimal rate;
}
