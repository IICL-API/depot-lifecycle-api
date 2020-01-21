package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "unit criteria that groups similar units on a release")
@EqualsAndHashCode(of= {"id"} )
@ToString(of= {"id"} )
@Introspected
public class ReleaseDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @ManyToOne(optional = false)
    @JsonIgnore
    Release release;

    @Schema(description = "The customer for the contract on this detail.", required = true)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party customer;

    @Schema(description = "the contract code for the given shipping containers", required = true, example = "CNCX05-100000")
    @Column(nullable = false)
    String contract;

    @Schema(description = "the equipment type ISO code or an internal code if one does not exist for the given shipping containers", required = true, example = "22G1")
    @Column(nullable = false)
    String equipment;

    @Schema(description = "the current grade of the unit", required = true, example = "IICL")
    @Column(nullable = false)
    String grade;

    @Schema(description = "an indicator for the upgrades applied to units on this detail.  This will be lessor specific, but an example could be AMMO to indicate the box is capable of carrying ammunition.", required = false, example = "AMMO")
    @Column()
    String upgradeType;

    @Schema(description = "the specific units for this release if defined, if not, assumed blanket")
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    List<ReleaseUnit> units = new ArrayList<>();

    @Schema(description = "the number of shipping containers assigned to this detail", required = true, example = "1", minimum = "0")
    @Column(nullable = false)
    Integer quantity;
}
