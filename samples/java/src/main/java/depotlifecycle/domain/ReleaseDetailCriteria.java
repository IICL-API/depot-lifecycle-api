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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"releaseDetail_id", "fieldId"})})
@Schema(description = "represents unit criteria to restrict units on a given detail", requiredProperties = {"fieldName", "fieldId", "fieldDescription", "fieldValue"})
@EqualsAndHashCode(of = {"releaseDetail", "fieldId"})
@ToString(of = {"releaseDetail", "fieldId"})
@Introspected
public class ReleaseDetailCriteria {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @ManyToOne(optional = false)
    @JsonIgnore
    @JoinColumn(name="releaseDetail_id")
    ReleaseDetail releaseDetail;

    @Schema(description = "a legible field name", required = true, nullable = false, example = "Minimum Tonnage", maxLength = 50)
    @Column(nullable = false, length = 50)
    String fieldName;

    @Schema(description = "an abbreviated form of the field name", required = true, nullable = false, example = "tonnageMin", maxLength = 30)
    @Column(nullable = false, length = 30)
    String fieldId;

    @Schema(description = "an explanation of what the field value is", required = true, nullable = false, example = "minimum tonnage rating of the unit (inclusive)", maxLength = 255)
    @Column(nullable = false, length = 255)
    String fieldDescription;

    @Schema(description = "the value of the field", required = true, nullable = false, example = "30", maxLength = 500)
    @Column(nullable = false, length = 500)
    String fieldValue;
}
