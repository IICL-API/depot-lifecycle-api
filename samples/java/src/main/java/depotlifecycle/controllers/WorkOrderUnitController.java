package depotlifecycle.controllers;

import depotlifecycle.ErrorResponse;
import depotlifecycle.domain.RepairComplete;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpResponseFactory;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Put;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "workOrder")
@Validated
@Controller("/api/v2/workOrderUnit")
public class WorkOrderUnitController {
    @Put(uri = "/{workOrderNumber}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "marks a shipping container repaired", description = "For the given work order, attempt to mark it repair complete.", method = "PUT", operationId = "updateWorkOrderUnit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successfully repair completed the workOrder"),
        @ApiResponse(responseCode = "400", description = "an error occurred trying to repair complete the work order", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "repair completion is not allowed by security"),
        @ApiResponse(responseCode = "404", description = "shipping container, depot, or work order could not be found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse update(@Parameter(name = "workOrderNumber", description = "the work order number", in = ParameterIn.PATH, required = true, schema = @Schema(example = "WHAMG30001", maxLength = 16)) String workOrderNumber,
                               @RequestBody(description = "Necessary information to mark a shipping container repair complete", required = true, content = {@Content(schema = @Schema(implementation = RepairComplete.class))}) RepairComplete repairComplete) {
        return HttpResponseFactory.INSTANCE.status(HttpStatus.NOT_IMPLEMENTED);
    }
}
