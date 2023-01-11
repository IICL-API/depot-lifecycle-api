package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "information for a specific unit on a release", requiredProperties = {"unitNumber", "status"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class ReleaseUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "the current unit number of the shipping container", pattern = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", required = true, example = "CONU1234561", maxLength = 11)
    @Column(nullable = false, length = 11)
    String unitNumber;

    @Schema(description = "comments pertaining to this unit for the intended recipient of this message", example = "['An example unit level comment.']")
    @Lob
    @ElementCollection
    @CollectionTable
    @LazyCollection(LazyCollectionOption.FALSE)
    List<String> comments;

    @Schema(description = "Describes the state of the shipping container for this release: \n\n`TIED` - shipping container is assigned to this release and ready to lease out.\n\n`REMOVED` - shipping container was attached to this release, but is no longer valid for release.\n\n`LOT` - shipping container has left the storage location.\n\n`CANDIDATE` - this container is eligible for this release but not currently assigned.", allowableValues = {"REMOVED", "TIED", "LOT", "CANDIDATE"}, example = "TIED")
    @Column(nullable = false, length = 9)
    String status;

    @Schema(description = "specific to Genset equipment, indicator for California Air Resources Board compliance")
    @Column
    Boolean carbCompliant;

    @Schema(description = "date and month this unit was manufactured\n\n( full-date notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", example = "2001-07-21", type = "string", format = "date", required = false)
    @Column(nullable = true)
    LocalDate manufactureDate;
}
