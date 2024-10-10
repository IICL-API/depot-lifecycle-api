package depotlifecycle.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@JsonView
@NoArgsConstructor
@Entity
@Table
@Schema(description = "Links to a photo for a single instance of damage for a shipping container.", requiredProperties = {"url"})
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Introspected
public class EstimateLineItemPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @Schema(description = "a URL as defined by [RFC 3986](https://tools.ietf.org/html/rfc3986) that is a photo for this line item.", required = true, nullable = false, example = "https://www.example.com/photo.png")
    @Column(length = 2048, nullable = false)
    String url;

    @Schema(description = "indicator of when this photo applies\n\n`REPAIRED` - Photo is after repair \n\n`BEFORE` - Photo is before repair", required = false, nullable = true, defaultValue = "BEFORE", example = "BEFORE", maxLength = 8, implementation = EstimatePhotoStatus.class)
    @Column(length = 8, nullable = false)
    @Enumerated(EnumType.STRING)
    EstimatePhotoStatus status = EstimatePhotoStatus.BEFORE;
}
