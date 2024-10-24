package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "an expected sell/fix decision to indicate the likely estimate owner approval action", requiredProperties = {"recommendation"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class PreliminaryDecision {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(required = true, nullable = false, maxLength = 11, description = "a speculative repair decision code to indicate if a box will be repaired, sold, held for repair, could not determine a decision, requires a survey before proceeding, etc", example = "SELL")
    @Column(nullable = false, length = 11)
    String recommendation;

    @Schema(required = false, nullable = true, maxLength = 255, description = "in the event a decision could not be determined, or a prerequisite exists, a more descriptive message than the code provided by the `recommendation` field")
    @Column(length = 255)
    String reason;

    @Schema(required = false, nullable = true, type = "number", format = "double", description = "A positive or negative amount showing how close the repair estimate was to a decision change")
    @Column
    BigDecimal difference;
}
