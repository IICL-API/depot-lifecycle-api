package depotlifecycle;

import depotlifecycle.domain.Redelivery;
import depotlifecycle.repositories.RedeliveryRepository;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;


@Tag(name = "redelivery")
@Validated
@Controller("/redelivery")
public class RedeliveryController {
    private final RedeliveryRepository redeliveryRepository;

    public RedeliveryController(RedeliveryRepository redeliveryRepository) {
        this.redeliveryRepository = redeliveryRepository;
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Redelivery Search", description = "Finds Redeliveries for the given the criteria.", operationId = "searchRedeliveries")
    @ApiResponse(responseCode = "400", description = "An error occurred creating the redelivery")
    public List<Redelivery> index() {
        List<Redelivery> redeliveries = new ArrayList<>();

        for (Redelivery redelivery : redeliveryRepository.findAll()) {
            redeliveries.add(redelivery);
        }

        return redeliveries;
    }

    @Post(produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Redelivery Create", description = "Creates a Redelivery for the given criteria.", method = "POST", operationId = "saveRedelivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Redelivery was successfully created."),
        @ApiResponse(responseCode = "400", description = "An error occurred creating the redelivery"),
        @ApiResponse(responseCode = "404", description = "A depot was not found")
    })
    public void create(@Parameter(description = "Data to use to create the given Redelivery", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = Redelivery.class))) Redelivery redelivery) {
        redeliveryRepository.save(redelivery);
    }

    @Post(uri = "/{redeliveryNumber}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Redelivery Update", description = "Updates an existing Redelivery.", method = "POST", operationId = "updateRedelivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Redelivery was successfully created."),
        @ApiResponse(responseCode = "400", description = "An error occurred updating the redelivery"),
        @ApiResponse(responseCode = "404", description = "A depot was not found")
    })
    public void update(@Parameter(description = "name that need to be updated", required = true, in = ParameterIn.PATH) String redeliveryNumber,
                       @Parameter(description = "Data to use to update the given Redelivery", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = Redelivery.class))) Redelivery redelivery) {
        if(!redeliveryRepository.existsById(redeliveryNumber)) {
            throw new IllegalArgumentException("Redelivery does not exist.");
        }
        redeliveryRepository.update(redelivery);
    }
}