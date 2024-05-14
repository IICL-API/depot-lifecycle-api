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
@Schema(description = "Represents a part used for the repair of a line item.", requiredProperties = {"number", "quantity", "price"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class EstimateLineItemPart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "a description for this part", maxLength = 500, example = "Paint A", required = false, nullable = true)
    @Column(length = 500)
    String description;

    @Schema(description = "ID number used to signify what part is being used", required = true, nullable = false, example = "108106", maxLength = 50)
    @Column(length = 64, nullable = false)
    String number;

    @Schema(description = "the number of parts used", required = true, nullable = false, type = "number", format = "int32", example = "1", minimum = "1")
    @Column(nullable = false)
    Integer quantity;

    @Schema(description = "the price per part", required = true, nullable = false, type = "number", format = "double", example = "2.88")
    @Column(nullable = false)
    BigDecimal price;
}
