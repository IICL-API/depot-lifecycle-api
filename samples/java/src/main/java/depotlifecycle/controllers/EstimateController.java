package depotlifecycle.controllers;

import depotlifecycle.ErrorResponse;
import depotlifecycle.domain.EstimateAllocation;
import depotlifecycle.PendingResponse;
import depotlifecycle.domain.Estimate;
import depotlifecycle.domain.EstimateCustomerApproval;
import depotlifecycle.domain.WorkOrder;
import depotlifecycle.repositories.EstimateRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpResponseFactory;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Patch;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
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
import lombok.RequiredArgsConstructor;

@Tag(name = "estimate")
@Validated
@Controller("/api/v2/estimate")
@RequiredArgsConstructor
public class EstimateController {
    private final EstimateRepository estimateRepository;

    @Get(produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "search for estimate(s)", description = "Given search criteria, return estimates that match that criteria.  This interface is *limited* to a maximum of 10 estimates.", operationId = "indexEstimate")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful found at least one estimate", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = Estimate.class)))}),
        @ApiResponse(responseCode = "400", description = "invalid estimate search object was provided", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "searching for estimates is disallowed by security"),
        @ApiResponse(responseCode = "404", description = "no estimates were found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse index(@Parameter(name = "estimateNumber", description = "the estimate number", in = ParameterIn.QUERY, required = false, schema = @Schema(example = "DEHAMCE1856373", maxLength = 16)) String estimateNumber,
                              @Parameter(name = "unitNumber", description = "the unit number of the shipping container at the time of estimate creation", in = ParameterIn.QUERY, required = false, schema = @Schema(maxLength = 11, pattern = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", example = "CONU1234561")) String unitNumber,
                              @Parameter(name = "depot", description = "the identifier of the depot", in = ParameterIn.QUERY, required = false, schema = @Schema(pattern = "^[A-Z0-9]{9}$", example = "DEHAMCMRA", maxLength = 9)) String depot,
                              @Parameter(name = "lessee", description = "the identifier of the lessee", in = ParameterIn.QUERY, required = false, schema = @Schema(pattern = "^[A-Z0-9]{9}$", example = "SGSINONEA", maxLength = 9)) String lessee,
                              @Parameter(name = "revision", description = "the revision number of the estimate", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "integer", format = "int32", example = "0")) Integer revision,
                              @Parameter(name = "equipmentCode", description = "the ISO equipment code of the shipping container", in = ParameterIn.QUERY, required = false, schema = @Schema(example = "22G1", maxLength = 10)) String equipmentCode
    ) {
        return HttpResponseFactory.INSTANCE.status(HttpStatus.NOT_IMPLEMENTED);
    }

    @Post(produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "create an estimate revision", description = "Create a damage estimate or a revision to an existing estimate that documents the type of damage and the cost of the repairs.", method = "POST", operationId = "saveEstimate")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successfully created and accepted the estimate revision", content = {@Content(schema = @Schema(implementation = EstimateAllocation.class))}),
        @ApiResponse(responseCode = "201", description = "successfully created the estimate revision, accepted it, and created a repair authorization for the estimate", content = {@Content(schema = @Schema(implementation = WorkOrder.class))}),
        @ApiResponse(responseCode = "202", description = "estimate accepted for processing, but not created due to manual processing requirement", content = {@Content(schema = @Schema(implementation = PendingResponse.class))}),
        @ApiResponse(responseCode = "400", description = "an invalid request was provided", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "creating an estimate is disallowed by security"),
        @ApiResponse(responseCode = "404", description = "the shipping container or depot could not be found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse create(@RequestBody(description = "Estimate object to create a new estimate revision", required = true, content = {@Content(schema = @Schema(implementation = Estimate.class))}) Estimate estimate) {
        return HttpResponseFactory.INSTANCE.status(HttpStatus.NOT_IMPLEMENTED);
    }

    @Get(uri = "/{estimateNumber}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "fetch an estimate revision", description = "Finds an estimate by the given estimate number and depot, returning the revision specified.  If revision is not specified, the current estimate revision is returned.", operationId = "showEstimate")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successfully found the estimate", content = {@Content(schema = @Schema(implementation = Estimate.class))}),
        @ApiResponse(responseCode = "400", description = "invalid request was supplied", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "fetching estimate is disallowed by security"),
        @ApiResponse(responseCode = "404", description = "estimate not found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse get(@Parameter(name = "estimateNumber", description = "the estimate number", in = ParameterIn.PATH, required = true, schema = @Schema(example = "DEHAMCE1856373", maxLength = 16)) String estimateNumber,
                            @Parameter(name = "depot", description = "the identifier of the depot", in = ParameterIn.QUERY, required = true, schema = @Schema(pattern = "^[A-Z0-9]{9}$", example = "DEHAMCMRA", maxLength = 9)) String depot,
                            @Parameter(name = "revision", description = "the revision number to show for the estimate; when not specified the current revision will be returned.", in = ParameterIn.QUERY, required = false, schema = @Schema(required = false, type = "integer", format = "int32", example = "0")) Integer revision
    ) {
        return HttpResponseFactory.INSTANCE.status(HttpStatus.NOT_IMPLEMENTED);
    }

    @Put(uri = "/{estimateNumber}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "customer approve an estimate", description = "Instead of sending in a full estimate revision, this endpoint can be used to approve an estimate without revising it.", method = "PUT", operationId = "customerApproveEstimate")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successfully approved the estimate revision", content = {@Content(schema = @Schema(implementation = EstimateAllocation.class))}),
        @ApiResponse(responseCode = "201", description = "successfully received the customer approval and issued a repair authorization for the estimate", content = {@Content(schema = @Schema(implementation = WorkOrder.class))}),
        @ApiResponse(responseCode = "202", description = "customer approval accepted for processing, but not created due to manual processing requirement", content = {@Content(schema = @Schema(implementation = PendingResponse.class))}),
        @ApiResponse(responseCode = "400", description = "an error occurred trying to customer approve the estimate", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "customer approval is disallowed by security"),
        @ApiResponse(responseCode = "404", description = "the estimate or depot was not found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse update(@Parameter(name = "estimateNumber", description = "the estimate number", in = ParameterIn.PATH, required = true, schema = @Schema(example = "DEHAMCE1856373", maxLength = 16)) String estimateNumber,
                               @Parameter(name = "depot", description = "the identifier of the depot", in = ParameterIn.QUERY, required = true, schema = @Schema(pattern = "^[A-Z0-9]{9}$", example = "DEHAMCMRA", maxLength = 9)) String depot,
                               @RequestBody(description = "customer approval object to update an existing estimate", required = true, content = {@Content(schema = @Schema(implementation = EstimateCustomerApproval.class))}) EstimateCustomerApproval customerApproval) {
        return HttpResponseFactory.INSTANCE.status(HttpStatus.NOT_IMPLEMENTED);
    }

    @Patch(uri = "/{estimateNumber}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "update estimate totals", description = "When the creation of the estimate is delayed due to a 202, after the manual processing is complete, this method is called to perform the update of the totals", method = "PATCH", operationId = "updateTotals")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successfully received estimate totals"),
        @ApiResponse(responseCode = "400", description = "an error occurred trying to update totals", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "estimate total update is disallowed by security"),
        @ApiResponse(responseCode = "404", description = "the estimate was not found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse allocate(@Parameter(name = "estimateNumber", description = "the estimate number", in = ParameterIn.PATH, required = true, schema = @Schema(example = "DEHAMCE1856373", maxLength = 16)) String estimateNumber,
                                 @Parameter(name = "depot", description = "the identifier of the depot", in = ParameterIn.QUERY, required = true, schema = @Schema(pattern = "^[A-Z0-9]{9}$", example = "DEHAMCMRA", maxLength = 9)) String depot,
                                 @RequestBody(description = "total breakdowns to finish creating an estimate", required = true, content = {@Content(schema = @Schema(implementation = EstimateAllocation.class))}) EstimateAllocation allocation) {
        return HttpResponseFactory.INSTANCE.status(HttpStatus.NOT_IMPLEMENTED);
    }

    @Error(status = HttpStatus.NOT_FOUND)
    public HttpResponse notFound(HttpRequest request) {
        JsonError error = new JsonError("Not Found");

        return HttpResponse.<JsonError>notFound()
            .body(error);
    }
}
