package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "describes the type of insurance coverage a given shipping container has for a damage repair")
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class InsuranceCoverage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "the total amount allowable to by covered by insurance", format = "double", required = false, nullable = true)
    @Column
    BigDecimal amountCovered;

    @Schema(description = "The currency for the insurance coverage amount", pattern = "^[A-Z]{3}$", example = "EUR", required = false, nullable = true)
    @Column(length = 3)
    String amountCurrency;

    @Schema(description = "indicates if this coverage should apply to scenarios where the damage exceeds the depreciated value of the container", required = false, nullable = true)
    @Column
    Boolean appliesToCTL;

    @Schema(description = "indicates if the amount of damage exceeds the insurance coverage, whether the lessee if fully responsible for the damage", required = false, nullable = true)
    @Column
    Boolean allOrNothing;

    @Schema(description = "any contractual reason why the coverage may not apply", required = false, nullable = false)
    @Lob
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable
    List<String> exceptions;

    @Schema(description = "reasons insurance coverage would be excluded from a repair", required = false, nullable = false)
    @Lob
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable
    List<String> exclusions;

    @Schema(description = "reasons insurance coverage would include a repair", required = false, nullable = false)
    @Lob
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable
    List<String> inclusions;
}
