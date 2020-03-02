package depotlifecycle.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import depotlifecycle.ErrorResponse;
import depotlifecycle.domain.RepairComplete;
import depotlifecycle.domain.WorkOrder;
import depotlifecycle.repositories.PartyRepository;
import depotlifecycle.repositories.WorkOrderRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.jackson.convert.ObjectToJsonNodeConverter;
import io.micronaut.security.annotation.Secured;
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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "workOrder")
@Validated
@Secured("isAuthenticated()")
@Controller("/api/v2/workOrder")
@RequiredArgsConstructor
public class WorkOrderController {
    private static final Logger LOG = LoggerFactory.getLogger(WorkOrderController.class);
    private final PartyRepository partyRepository;
    private final WorkOrderRepository workOrderRepository;
    private final ObjectToJsonNodeConverter objectToJsonNodeConverter;

    @Post(produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "authorizes a repair", description = "Submits a work order to repair a shipping container to the given inspection criteria.", method = "POST", operationId = "saveWorkOrder")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successfully approved repair", content = {@Content(schema = @Schema(implementation = WorkOrder.class))}),
        @ApiResponse(responseCode = "400", description = "an error occurred trying to create the work order", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "repair authorization is not allowed by security"),
        @ApiResponse(responseCode = "404", description = "the shipping container or depot could not be found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public void create(@RequestBody(description = "repair authorization object", required = true, content = {@Content(schema = @Schema(implementation = WorkOrder.class))}) WorkOrder workOrder) {
        LOG.info("Received Work Order Create");
        objectToJsonNodeConverter.convert(workOrder, JsonNode.class).ifPresent(jsonNode -> LOG.info(jsonNode.toString()));
        if (workOrderRepository.existsById(workOrder.getWorkOrderNumber())) {
            throw new IllegalArgumentException("Work Order already exists; please update instead.");
        }

        saveParties(workOrder);

        workOrderRepository.save(workOrder);
    }

    private void saveParties(WorkOrder workOrder) {
        if (workOrder.getDepot() != null) {
            workOrder.setDepot(partyRepository.saveOrUpdate(workOrder.getDepot()));
        }

        if (workOrder.getOwner() != null) {
            workOrder.setOwner(partyRepository.saveOrUpdate(workOrder.getOwner()));
        }

        if (workOrder.getBillingParty() != null) {
            workOrder.setBillingParty(partyRepository.saveOrUpdate(workOrder.getBillingParty()));
        }
    }

    @Put(uri = "/{workOrderNumber}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "updates a repair work order", description = "Submits updates to the given work order.", method = "PUT", operationId = "updateWorkOrder")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successfully update the workOrder", content = {@Content(schema = @Schema(implementation = WorkOrder.class))}),
        @ApiResponse(responseCode = "400", description = "an error occurred trying to update the work order", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "work order updates are not allowed by security"),
        @ApiResponse(responseCode = "404", description = "shipping container, depot, or work order could not be found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public void update(@Parameter(name = "workOrderNumber", description = "the work order number", in = ParameterIn.PATH, required = true, schema = @Schema(example = "WHAMG30001", maxLength = 16)) String workOrderNumber,
                               @RequestBody(description = "the updated work order record", required = true, content = {@Content(schema = @Schema(implementation = RepairComplete.class))}) WorkOrder workOrder) {
        LOG.info("Received Work Order Update");
        objectToJsonNodeConverter.convert(workOrder, JsonNode.class).ifPresent(jsonNode -> LOG.info(jsonNode.toString()));
        if (!workOrderRepository.existsById(workOrderNumber)) {
            LOG.info("Work Order DNE -> Forcing Create Workflow");
            create(workOrder);
            return;
        }

        saveParties(workOrder);

        workOrderRepository.update(workOrder);
    }

    @Error(status = HttpStatus.NOT_FOUND)
    public HttpResponse notFound(HttpRequest request) {
        LOG.info("\tError - 404 - Not Found");
        JsonError error = new JsonError("Not Found");

        return HttpResponse.<JsonError>notFound()
            .body(error);
    }

    @Error
    public HttpResponse onSavedFailed(HttpRequest request, Throwable ex) {
        LOG.info("\tError - 400 - Bad Request", ex);
        ErrorResponse error = new ErrorResponse();
        error.setCode("ERR000");
        error.setMessage(ex.getMessage());

        return HttpResponse.badRequest().body(error);
    }
}
