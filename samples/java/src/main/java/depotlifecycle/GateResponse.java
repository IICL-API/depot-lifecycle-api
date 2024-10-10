package depotlifecycle;

import com.fasterxml.jackson.annotation.JsonView;
import depotlifecycle.domain.InsuranceCoverage;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Lob;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonView
@NoArgsConstructor
@Schema(description = "A confirmation that a gate completed successfully and the necessary information to perform a damage estimate if required.", requiredProperties = {"adviceNumber"})
@Introspected
public class GateResponse {
    @Schema(pattern = "^[A-Z0-9]{3}[0-9]{3}$", description = "indicator code for this response", example = "TRI521", required = false, nullable = true, maxLength = 6)
    String code;

    @Schema(description = "A descriptive message concerning this gate in", required = false, nullable = true, example = "Info TRI521 - Unit has been gated-in but is not off-hired")
    String message;

    @Schema(required = false, nullable = true, description = "either the submitted advice number for the gate record or the adjusted one", example = "AHAMG000000", maxLength = 16)
    String adviceNumber;

    @Schema(description = "the customer reference for the unit; typically an internal customer identifier or contract code", maxLength = 35, example = "MAEX", required = false, nullable = true)
    String customerReference;

    @Schema(description = "the transaction reference for this activity", maxLength = 35, example = "74454D", required = false, nullable = true)
    String transactionReference;

    @Schema(description = "*Field is currently proposed to be added - not currently production approved.*\n\nAn internal system identifier to be used to upload Gate Photos or compare related activities.", type = "integer", format = "int64", example = "10102561", required = false, nullable = true)
    Long relatedId;

    @Schema(description = "The applicable insurance coverage for damage estimate purposes", required = false, nullable = true)
    InsuranceCoverage insuranceCoverage;

    @Schema(description = "the exchange rate to convert billed currency to the local currency for damage estimate totals", required = false, nullable = true, type = "number", format = "double", example = "0.8133")
    BigDecimal currentExchangeRate;

    @ArraySchema(schema = @Schema(example = "ALL CLEANING MUST BE CODED TO 'O' FOR OWNER."))
    @Schema(description = "comments pertaining to this gate record", required = false, nullable = false)
    @Lob
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable
    List<String> comments;

    @Schema(description = "the grade the unit should be inspected to for estimates; if none, no estimate is allowed", required = false, nullable = true, example = "IICL", maxLength = 10)
    String currentInspectionCriteria;
}
