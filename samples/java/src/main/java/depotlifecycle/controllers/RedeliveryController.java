package depotlifecycle.controllers;

import depotlifecycle.ErrorResponse;
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
import io.micronaut.http.annotation.Put;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.security.annotation.Secured;
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
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag(name = "redelivery")
@Validated
@Secured("isAuthenticated()")
@Controller("/api/v2/redelivery")
@RequiredArgsConstructor
public class RedeliveryController {
    private final RedeliveryRepository redeliveryRepository;

    @Get(produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "search for a redelivery", description = "Finds Redeliveries for the given the criteria.", method = "GET", operationId = "indexRedelivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful search", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = Redelivery.class)))}),
        @ApiResponse(responseCode = "400", description = "an error occurred", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "security disallows access"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse index(@Parameter(name = "redeliveryNumber", description = "the redelivery number to filter to", in = ParameterIn.QUERY, required = false, schema = @Schema(example = "AHAMG000000", maxLength = 16)) String redeliveryNumber) {
        List<Redelivery> redeliveries = new ArrayList<>();
        if (redeliveryNumber != null) {
            Optional<Redelivery> redelivery = redeliveryRepository.findById(redeliveryNumber);
            redelivery.ifPresent(redeliveries::add);
        }
        else {
            for (Redelivery redelivery : redeliveryRepository.findAll()) {
                redeliveries.add(redelivery);
            }
        }

        if (redeliveries.isEmpty()) {
            return HttpResponse.notFound();
        }
        else {
            return HttpResponse.ok(redeliveries);
        }
    }

    @Post(produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "create redelivery", description = "Creates a Redelivery for the given criteria.", method = "POST", operationId = "saveRedelivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful create"),
        @ApiResponse(responseCode = "400", description = "an error occurred", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "security disallows access"),
        @ApiResponse(responseCode = "404", description = "the redelivery depot was not found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public void create(@RequestBody(description = "Data to use to update the given Redelivery", required = true, content = {@Content(schema = @Schema(implementation = Redelivery.class))}) Redelivery redelivery) {
        redeliveryRepository.save(redelivery);
    }

    @Put(uri = "/{redeliveryNumber}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "update redelivery", description = "Updates an existing Redelivery.", method = "PUT", operationId = "updateRedelivery")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful update"),
        @ApiResponse(responseCode = "400", description = "an error occurred", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "security disallows access"),
        @ApiResponse(responseCode = "404", description = "the redelivery was not found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public void update(@Parameter(description = "the redelivery number that needs updated", required = true, in = ParameterIn.PATH, schema = @Schema(example = "AHAMG000000", maxLength = 16)) String redeliveryNumber,
                       @RequestBody(description = "Data to use to update the given Redelivery", required = true, content = {@Content(schema = @Schema(implementation = Redelivery.class))}) Redelivery redelivery) {
        if (!redeliveryRepository.existsById(redeliveryNumber)) {
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