package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table(name = "releaseUnit")
@Schema(description = "information for a specific unit on a release")
@EqualsAndHashCode(of= {"id"} )
@ToString(of= {"id"} )
@Introspected
public class ReleaseUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @ManyToOne(optional = false)
    @JsonIgnore
    ReleaseDetail releaseDetail;

    @Schema(description = "the current remark of the shipping container", pattern = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", required = true, example = "CONU1234561")
    @Column(name = "unitNumber", nullable = false)
    String unitNumber;

    @Schema(description = "comments pertaining only to this unit for the intended recipient of this message", maxLength = 500)
    @Column(name = "comments", nullable = false, length = 500)
    String comments;

    @Schema(description = "Describes the state of the shipping container for this release: \n\n`TIED` - Shipping Container is assigned to this release and ready to lease out.\n\n`REMOVED` - Shipping Container was attached to this release, but is no longer valid for release.\n\n`LOT` - Shipping Container has left the storage location.", allowableValues = {"REMOVED", "TIED", "LOT"}, example = "TIED")
    @Column(nullable = false, length = 7)
    String status;
}
