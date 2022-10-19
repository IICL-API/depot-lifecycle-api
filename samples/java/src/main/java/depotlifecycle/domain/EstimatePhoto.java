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
import javax.persistence.Table;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "Links to a photo for a damage estimate.", requiredProperties = {"url", "status"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class EstimatePhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "a URL as defined by [RFC 3986](https://tools.ietf.org/html/rfc3986) that is a photo for this estimate.", required = true, example = "https://www.example.com/photo.png")
    @Column(length = 2048, nullable = false)
    String url;

    @Schema(description = "indicator of when this photo applies\n\n`REPAIRED` - Photo is after repair \n\n`BEFORE` - Photo is before repair", required = true, allowableValues = {"REPAIRED", "BEFORE"}, example = "BEFORE", maxLength = 8)
    @Column(length = 8, nullable = false)
    String status;
}
