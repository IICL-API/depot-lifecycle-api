package depotlifecycle;

import depotlifecycle.domain.Redelivery;
import depotlifecycle.repositories.RedeliveryRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Tag(name = "redelivery")
@Validated
@Controller("redelivery")
public class RedeliveryController {
    private final RedeliveryRepository redeliveryRepository;

    public RedeliveryController(RedeliveryRepository redeliveryRepository) {
        this.redeliveryRepository = redeliveryRepository;
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Redelivery Search", description = "Finds Redeliveries for the given the criteria.", operationId = "searchRedeliveries")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful search", content = { @Content( array = @ArraySchema(schema = @Schema(implementation = Redelivery.class))) }),
        @ApiResponse(responseCode = "400", description = "an error occurred"),
        @ApiResponse(responseCode = "403", description = "security disallows access"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse index(@Parameter(name="redeliveryNumber", description = "the redelivery number to filter to", in = ParameterIn.QUERY, required = false) String redeliveryNumber) {
        List<Redelivery> redeliveries = new ArrayList<>();
        if(redeliveryNumber != null) {
            Optional<Redelivery> redelivery = redeliveryRepository.findById(redeliveryNumber);
            redelivery.ifPresent(redeliveries::add);
        }
        else {
            for (Redelivery redelivery : redeliveryRepository.findAll()) {
                redeliveries.add(redelivery);
            }
        }

        if(redeliveries.isEmpty()) {
            return HttpResponse.notFound();
        }
        else {
            return HttpResponse.ok(redeliveries);
        }
    }

    @Post(produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Redelivery Create", description = "Creates a Redelivery for the given criteria.", method = "POST", operationId = "saveRedelivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful create"),
        @ApiResponse(responseCode = "400", description = "an error occurred"),
        @ApiResponse(responseCode = "403", description = "security disallows access"),
        @ApiResponse(responseCode = "404", description = "the redelivery depot was not found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public void create(@RequestBody( description =  "Data to use to update the given Redelivery", required = true, content = { @Content( schema = @Schema(implementation = Redelivery.class) ) }) Redelivery redelivery) {
        redeliveryRepository.save(redelivery);
    }

    @Post(uri = "/{redeliveryNumber}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Redelivery Update", description = "Updates an existing Redelivery.", method = "POST", operationId = "updateRedelivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful update"),
        @ApiResponse(responseCode = "400", description = "an error occurred"),
        @ApiResponse(responseCode = "403", description = "security disallows access"),
        @ApiResponse(responseCode = "404", description = "the redelivery was not found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public void update(@Parameter(description = "name that need to be updated", required = true, in = ParameterIn.PATH) String redeliveryNumber,
                       @RequestBody( description =  "Data to use to update the given Redelivery", required = true, content = { @Content( schema = @Schema(implementation = Redelivery.class) ) })  Redelivery redelivery) {
        if(!redeliveryRepository.existsById(redeliveryNumber)) {
            throw new IllegalArgumentException("Redelivery does not exist.");
        }
        redeliveryRepository.update(redelivery);
    }

    @Error(status = HttpStatus.NOT_FOUND)
    public HttpResponse notFound(HttpRequest request) {
        JsonError error = new JsonError("Not Found");

        return HttpResponse.<JsonError>notFound()
            .body(error);
    }
}