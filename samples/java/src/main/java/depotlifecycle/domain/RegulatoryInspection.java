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
public class RegulatoryInspection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "the grain the regulatory inspection applies.\n\n`NATIONAL` - National\n\n`REGIONAL` - Regional\n\n`INDUSTRY` - Industry", example = "NATIONAL", required = true, nullable = false, implementation = RegulatoryScope.class)
    @Column(nullable = false, length = 8)
    @Enumerated(EnumType.STRING)
    RegulatoryScope scope;

    @Schema(description = "the name of the regulatory inspection", required = true, nullable = false, example = "FMCSA", maxLength = 16)
    @Column(nullable = false, length = 16)
    String name;

    @Schema(description = "the date this inspection was last performed\n\n( full-date notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", example = "2001-07-21", type = "string", format = "date", required = false, nullable = true)
    @Column
    LocalDate lastInspection;

    @Schema(description = "the number of months this inspection is valid for", type = "integer", format = "int32", required = false, nullable = true, example = "12", minimum = "0")
    @Column(nullable = true)
    Integer validFor;

    @Schema(description = "the result of the regulatory inspection.\n\n`PASS` - Pass\n\n`FAIL` - Fail", example = "PASS", required = false, nullable = true, implementation = RegulatoryStatus.class)
    @Column(nullable = false, length = 4)
    @Enumerated(EnumType.STRING)
    RegulatoryStatus status;

    @Schema(description = "the party performing this inspection", required = false, nullable = true, implementation = ExternalParty.class)
    @ManyToOne(fetch = FetchType.EAGER)
    ExternalParty inspector;

    @ArraySchema(schema = @Schema(example = "An example inspection note."))
    @Schema(description = "comments pertaining to this inspection", required = false, nullable = false)
    @Lob
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable
    List<String> comments;
}
