package depotlifecycle;

import com.fasterxml.jackson.annotation.JsonView;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Lob;
import java.util.List;

@Getter
@Setter
@JsonView
@NoArgsConstructor
@Schema(description = "a general response object used when an error occurs to describe why it occurred\n\n`400` http status codes are used when a request is invalid or the basic requirements are not met\n\n`405` http status codes are used for business validations")
@Introspected
public class ErrorResponse {
    @Schema(pattern = "^[A-Z0-9]{3}[0-9]{3}$", description = "indicator code specific to this error", example = "TRI521", required = false, nullable = true, maxLength = 6)
    String code;

    @Schema(description = "a descriptive error message", required = false, nullable = true, example = "Info TRI521 - Unit has been gated-in but is not off-hired")
    String message;

    @ArraySchema(schema =
    @Schema(description = "details for the descriptive error message", example = "Customer may turn in unit, but will continued to be billed.", required = false, nullable = false)
    )
    @Schema(description = "details for the descriptive error message", required = false, nullable = false)
    @Lob
    @ElementCollection
    @CollectionTable
    @LazyCollection(LazyCollectionOption.FALSE)
    List<String> details;
}
