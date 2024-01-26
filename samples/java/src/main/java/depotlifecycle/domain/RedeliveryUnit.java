package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "information for a specific unit on a redelivery", requiredProperties = {"unitNumber", "manufactureDate", "status", "billingParty"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class RedeliveryUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "the current unit number of the shipping container", pattern = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", required = true, nullable = false, example = "CONU1234561", maxLength = 11)
    @Column(nullable = false, length = 11)
    String unitNumber;

    @Schema(description = "date and month this unit was manufactured\n\n( full-date notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", example = "2001-07-21", type = "string", format = "date", required = true, nullable = false)
    @Column(nullable = false)
    LocalDate manufactureDate;

    @Schema(description = "the date this unit was on hired to the customer for this unit's detail\n\n( full-date notation as defined by [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6) )", example = "2001-07-21", type = "string", format = "date", required = false, nullable = true)
    @Column
    LocalDate lastOnHireDate;

    @Schema(description = "The location this unit was last on-hired.", required = false, nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    Party lastOnHireLocation;

    @Schema(description = "Describes the state of the shipping container for this redelivery: \n\n`TIED` - shipping container is assigned to this redelivery and ready to turn in.\n\n`REMOVED` - shipping container was attached to this redelivery, but is no longer valid for redelivery.\n\n`TIN` - shipping container has turned into the storage location of this redelivery.", allowableValues = {"REMOVED", "TIED", "TIN"}, example = "TIED", required = true, nullable = false)
    @Column(nullable = false, length = 7)
    String status;

    @ArraySchema(schema = @Schema(description = "comments pertaining to this unit for the intended recipient of this message", example = "An example unit level comment.", required = false, nullable = false))
    @Schema(description = "comments pertaining to this unit for the intended recipient of this message", required = false, nullable = false)
    @Lob
    @ElementCollection
    @CollectionTable
    @LazyCollection(LazyCollectionOption.FALSE)
    List<String> comments;

    @Schema(description = "a description of the last cargo this shipping container carried", maxLength = 255, example = "Aroset PS 5191", required = false, nullable = true)
    @Column(length = 255)
    String lastCargo;

    @Schema(description = "A number (such as a UN Number for hazardous materials) used to identify the type of cargo this container carried", required = false, nullable = true, minLength = 4, maxLength = 7, pattern = "^([A-Z]{2})??\\s?[A-Z0-9]{4}$", example="UN 0305")
    @Column(length = 7)
    String lastCargoNumber;

    @Schema(description = "if this is a tank, then this describes the type of liquids it can contain: \n\n`F` - Food\n\n`C` - Chemical ", maxLength = 1, example = "C", allowableValues = {"F", "C"}, required = false, nullable = true)
    @Column(length = 1)
    String tankGrade;

    @ArraySchema(schema = @Schema(description = "list of technical bulletins associated to this unit - we suggest this be fixed identifiers, codes, or urls", example = "https://technical.example.com/bulletin/1234", required = false, nullable = false))
    @Schema(description = "list of technical bulletins associated to this unit - we suggest this be fixed identifiers, codes, or urls", required = false, nullable = false)
    @Lob
    @ElementCollection
    @CollectionTable
    @LazyCollection(LazyCollectionOption.FALSE)
    List<String> technicalBulletins;

    @Schema(description = "The party that will handle any repair (estimate & work order) billing for units associated with this detail.", required = true, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Party billingParty;

    @Schema(description = "conveys the estimate instructions to the depot; if the unit is damaged on turn in, the estimate standard that the shipping container should be estimated to and if it should not be estimated, then null", required = false, nullable = true, example = "IICL", maxLength = 10)
    @Column(nullable = true, length = 10)
    String inspectionCriteria;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @Schema(description = "if this detail is for a reefer shipping container, then this details the cooling machinery information", required = false, nullable = true)
    MachineryInfo machineryInfo;
}
