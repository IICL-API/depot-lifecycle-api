package depotlifecycle.controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import depotlifecycle.ErrorResponse;
import depotlifecycle.domain.*;
import depotlifecycle.PendingResponse;
import depotlifecycle.repositories.*;
import depotlifecycle.security.AuthenticationProviderUserPassword;
import depotlifecycle.system.ApiErrorHandling;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.http.*;
import io.micronaut.http.annotation.*;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.AuthorizationException;
import io.micronaut.security.utils.SecurityService;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Tag(name = "estimate")
@Validated
@Secured("isAuthenticated()")
@Controller("/api/v2/estimate")
@RequiredArgsConstructor
public class EstimateController {
    private static final Logger LOG = LoggerFactory.getLogger(EstimateController.class);
    private final PartyRepository partyRepository;
    private final EstimateRepository estimateRepository;
    private final EstimateCancelRequestRepository estimateCancelRequestRepository;
    private final EstimateAllocationRepository estimateAllocationRepository;
    private final EstimateCustomerApprovalRepository estimateCustomerApprovalRepository;
    private final ConversionService conversionService;
    private final SecurityService securityService;

    @Get(produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "search for estimate(s)",
        description = "Given search criteria, return estimates that match that criteria.  This interface is *limited* to a maximum of 10 estimates.",
        operationId = "indexEstimate",
        extensions = @Extension(properties = { @ExtensionProperty(name = "iicl-purpose", value = "reporting", parseValue = true) })
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful found at least one estimate", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = Estimate.class)))}),
        @ApiResponse(responseCode = "400", description = "invalid estimate search object was provided", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "searching for estimates is disallowed by security"),
        @ApiResponse(responseCode = "404", description = "no estimates were found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public List<Estimate> index(@Nullable @QueryValue("estimateNumber") @Parameter(name = "estimateNumber", description = "the estimate number", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", example = "DEHAMCE1856373", maxLength = 16, required = false, nullable = true)) String estimateNumber,
                                @Nullable @QueryValue("unitNumber") @Parameter(name = "unitNumber", description = "the unit number of the shipping container at the time of estimate creation", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", maxLength = 11, pattern = "^[A-Z]{4}[X0-9]{6}[A-Z0-9]{0,1}$", example = "CONU1234561", required = false, nullable = true)) String unitNumber,
                                @Nullable @QueryValue("depot") @Parameter(name = "depot", description = "the identifier of the depot", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", pattern = "^[A-Z0-9]{9}$", example = "DEHAMCMRA", maxLength = 9, required = false, nullable = true)) String depot,
                                @Nullable @QueryValue("lessee") @Parameter(name = "lessee", description = "the identifier of the lessee", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", pattern = "^[A-Z0-9]{9}$", example = "SGSINONEA", maxLength = 9, required = false, nullable = true)) String lessee,
                                @Nullable @QueryValue("revision") @Parameter(name = "revision", description = "the revision number of the estimate", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "integer", format = "int32", example = "0", required = false, nullable = true)) Integer revision,
                                @Nullable @QueryValue("equipmentCode") @Parameter(name = "equipmentCode", description = "the ISO equipment code of the shipping container", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", example = "22G1", maxLength = 10, required = false, nullable = true)) String equipmentCode
    ) {
        Optional<Party> depotParty = Optional.empty();
        if(!Objects.isNull(depot)) {
            depotParty = partyRepository.findByCompanyId(depot);
            if(depotParty.isEmpty()) {
                ErrorResponse error = new ErrorResponse();
                error.setCode("ERR001");
                error.setMessage(String.format("Depot Party %s does not exist", depot));
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, error);
            }
        }

        Optional<Party> customerParty = Optional.empty();
        if(!Objects.isNull(depot)) {
            customerParty = partyRepository.findByCompanyId(lessee);
            if(customerParty.isEmpty()) {
                ErrorResponse error = new ErrorResponse();
                error.setCode("ERR002");
                error.setMessage(String.format("Lessee Party %s does not exist", depot));
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, error);
            }
        }

        if(!Objects.isNull(equipmentCode)) {
            ErrorResponse error = new ErrorResponse();
            error.setCode("ERR003");
            error.setMessage("Equipment Type searching is not implemented");
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, error);
        }

        return estimateRepository.searchEstimates(estimateNumber, depotParty.orElse(null), unitNumber, customerParty.orElse(null), revision);
    }

    @Post(produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "create an estimate revision",
        description = "Create a damage estimate or a revision to an existing estimate that documents the type of damage and the cost of the repairs.",
        method = "POST",
        operationId = "saveEstimate",
        extensions = @Extension(properties = { @ExtensionProperty(name = "iicl-purpose", value = "activity", parseValue = true) })
    )
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
    public HttpResponse<EstimateAllocation> create(@Body @RequestBody(description = "Estimate object to create a new estimate revision", required = true, content = {@Content(schema = @Schema(implementation = Estimate.class))}) Estimate estimate) {
        LOG.info("Received Estimate Create");
        conversionService.convert(estimate, JsonNode.class).ifPresent(jsonNode -> LOG.info(jsonNode.toString()));

        saveParties(estimate);

        estimateRepository.save(estimate);

        //Generate an example allocation for the purposes of this demo
        EstimateAllocation allocation = new EstimateAllocation();
        allocation.setRelatedId(estimate.getId());
        allocation.setEstimateNumber(estimate.getEstimateNumber());
        allocation.setDepot(estimate.getDepot());
        allocation.setRevision(estimate.getRevision());
        allocation.setTotal(estimate.getTotal());
        allocation.setOwnerTotal(estimate.getPartyTotal(EstimateLineItemParty.O));
        allocation.setInsuranceTotal(estimate.getPartyTotal(EstimateLineItemParty.I));
        allocation.setCustomerTotal(estimate.getPartyTotal(EstimateLineItemParty.U));
        allocation.setCtl(false); //assume not a CTL for demo purposes
        allocation.setComments(estimate.getComments());//Assume the returned comments are the same for demo

        PreliminaryDecision preliminaryDecision = new PreliminaryDecision();
        preliminaryDecision.setRecommendation("FIX");
        allocation.setPreliminaryDecision(preliminaryDecision);

        LOG.info("Responding with example Estimate Allocation");
        conversionService.convert(allocation, JsonNode.class).ifPresent(jsonNode -> LOG.info(jsonNode.toString()));

        return HttpResponse.ok(allocation);
    }

    private void saveParties(Estimate estimate) {
        if (estimate.getDepot() != null) {
            estimate.setDepot(partyRepository.save(estimate.getDepot()));
        }

        if (estimate.getRequester() != null) {
            estimate.setRequester(partyRepository.save(estimate.getRequester()));
        }

        if (estimate.getOwner() != null) {
            estimate.setOwner(partyRepository.save(estimate.getOwner()));
        }

        if (estimate.getCustomer() != null) {
            estimate.setCustomer(partyRepository.save(estimate.getCustomer()));
        }

        if (estimate.getAllocation() != null) {
            EstimateAllocation allocation = estimate.getAllocation();
            if (allocation.getDepot() != null) {
                allocation.setDepot(partyRepository.save(allocation.getDepot()));
            }
        }
    }

    @Get(uri = "/{estimateNumber}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "fetch an estimate revision",
        description = "Finds an estimate by the given estimate number and depot, returning the revision specified.  If revision is not specified, the current estimate revision is returned.",
        operationId = "showEstimate",
        extensions = @Extension(properties = { @ExtensionProperty(name = "iicl-purpose", value = "reporting", parseValue = true) })
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successfully found the estimate", content = {@Content(schema = @Schema(implementation = Estimate.class))}),
        @ApiResponse(responseCode = "400", description = "invalid request was supplied", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "fetching estimate is disallowed by security"),
        @ApiResponse(responseCode = "404", description = "estimate not found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public Estimate get(@Parameter(name = "estimateNumber", description = "the estimate number", in = ParameterIn.PATH, required = true, schema = @Schema(type = "string", example = "DEHAMCE1856373", maxLength = 16)) String estimateNumber,
                                        @QueryValue("depot") @Parameter(name = "depot", description = "the identifier of the depot", in = ParameterIn.QUERY, required = true, schema = @Schema(type = "string", pattern = "^[A-Z0-9]{9}$", example = "DEHAMCMRA", maxLength = 9)) String depot,
                                        @Nullable @QueryValue("revision") @Parameter(name = "revision", description = "the revision number to show for the estimate; when not specified the current revision will be returned.", in = ParameterIn.QUERY, required = false, schema = @Schema(required = false, type = "integer", format = "int32", example = "0")) Integer revision
    ) {
        Optional<Party> depotParty = Optional.empty();
        if(!Objects.isNull(depot)) {
            depotParty = partyRepository.findByCompanyId(depot);
            if(depotParty.isEmpty()) {
                ErrorResponse error = new ErrorResponse();
                error.setCode("ERR001");
                error.setMessage(String.format("Depot Party %s does not exist", depot));
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, error);
            }
        }

        return estimateRepository.searchEstimates(estimateNumber, depotParty.orElse(null), null, null, revision).stream().findFirst().orElse(null);
    }

    @Put(uri = "/{estimateNumber}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "customer approve an estimate",
        description = "Instead of sending in a full estimate revision, this endpoint can be used to approve an estimate without revising it.  This endpoint would typically be implemented by a lessee.  A depot would use the send revision to report approvals.",
        method = "PUT",
        operationId = "customerApproveEstimate",
        extensions = @Extension(properties = { @ExtensionProperty(name = "iicl-purpose", value = "activity", parseValue = true) })
    )
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
    public HttpResponse<EstimateAllocation> update(@Parameter(name = "estimateNumber", description = "the estimate number", in = ParameterIn.PATH, required = true, schema = @Schema(type = "string", example = "DEHAMCE1856373", maxLength = 16)) String estimateNumber,
                                           @QueryValue("depot") @Parameter(name = "depot", description = "the identifier of the depot", in = ParameterIn.QUERY, required = true, schema = @Schema(type = "string", pattern = "^[A-Z0-9]{9}$", example = "DEHAMCMRA", maxLength = 9)) String depot,
                                           @Body @RequestBody(description = "customer approval object to update an existing estimate", required = true, content = {@Content(schema = @Schema(implementation = EstimateCustomerApproval.class))}) EstimateCustomerApproval customerApproval) {


        LOG.info("Received Estimate Approve: [Estimate = {}, Depot = {}]", estimateNumber, depot);
        conversionService.convert(customerApproval, JsonNode.class).ifPresent(jsonNode -> LOG.info(jsonNode.toString()));

        Optional<Party> depotParty = partyRepository.findByCompanyId(depot);
        if (depotParty.isEmpty()) {
            LOG.info("Party DNE -> Returning NOT FOUND");
            return HttpResponse.notFound();
        }

        if (!estimateRepository.existsByEstimateNumberAndDepot(estimateNumber, depotParty.get())) {
            LOG.info("Estimate DNE -> Returning NOT FOUND");
            return HttpResponse.notFound();
        }

        if (estimateCancelRequestRepository.existsByEstimateNumberAndDepot(estimateNumber, depotParty.get())) {
            LOG.info("Estimate Already Deleted -> Returning NOT FOUND");
            return HttpResponse.notFound();
        }

        Estimate estimate = estimateRepository.findByEstimateNumberAndDepot(estimateNumber, depotParty.get());
        estimate.setCustomerApproval(estimateCustomerApprovalRepository.save(customerApproval));
        estimateRepository.update(estimate);

        //Generate an example allocation for the purposes of this demo
        EstimateAllocation allocation = new EstimateAllocation();
        allocation.setRelatedId(estimate.getId());
        allocation.setEstimateNumber(estimate.getEstimateNumber());
        allocation.setDepot(estimate.getDepot());
        allocation.setRevision(estimate.getRevision());
        allocation.setTotal(estimate.getTotal());
        allocation.setOwnerTotal(estimate.getPartyTotal(EstimateLineItemParty.O));
        allocation.setInsuranceTotal(estimate.getPartyTotal(EstimateLineItemParty.I));
        allocation.setCustomerTotal(estimate.getPartyTotal(EstimateLineItemParty.U));
        allocation.setCtl(false); //assume not a CTL for demo purposes
        allocation.setComments(estimate.getComments());//Assume the returned comments are the same for demo

        PreliminaryDecision preliminaryDecision = new PreliminaryDecision();
        preliminaryDecision.setRecommendation("FIX");
        allocation.setPreliminaryDecision(preliminaryDecision);

        LOG.info("Responding with example Estimate Allocation");
        conversionService.convert(allocation, JsonNode.class).ifPresent(jsonNode -> LOG.info(jsonNode.toString()));

        return HttpResponse.ok(allocation);
    }

    @Delete(uri = "/{estimateNumber}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "cancels an estimate",
        description = "Cancels an estimate (deletes it and all revisions from the system)",
        method = "DELETE",
        operationId = "deleteEstimate",
        extensions = @Extension(properties = { @ExtensionProperty(name = "iicl-purpose", value = "activity", parseValue = true) })
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successfully cancelled the estimate", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "400", description = "an error occurred trying to cancel the estimate", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "cancel estimate is disallowed by security"),
        @ApiResponse(responseCode = "404", description = "the estimate or depot was not found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse<HttpStatus> delete(@Parameter(name = "estimateNumber", description = "the estimate number", in = ParameterIn.PATH, required = true, schema = @Schema(type = "string", example = "DEHAMCE1856373", maxLength = 16)) String estimateNumber,
                                           @QueryValue("depot") @Parameter(name = "depot", description = "the identifier of the depot", in = ParameterIn.QUERY, required = true, schema = @Schema(type = "string", pattern = "^[A-Z0-9]{9}$", example = "DEHAMCMRA", maxLength = 9)) String depot) {
        LOG.info("Received Estimate Cancel for {} @ {}", estimateNumber, depot);

        Optional<Party> depotParty = partyRepository.findByCompanyId(depot);
        if (securityService.username().equals(AuthenticationProviderUserPassword.VALIDATE_USER_NAME)) {
            if (depotParty.isEmpty()) {
                LOG.info("Party DNE -> Returning NOT FOUND");
                return HttpResponse.notFound();
            }

            if (!estimateRepository.existsByEstimateNumberAndDepot(estimateNumber, depotParty.get())) {
                LOG.info("Estimate DNE -> Returning NOT FOUND");
                return HttpResponse.notFound();
            }

            if (estimateCancelRequestRepository.existsByEstimateNumberAndDepot(estimateNumber, depotParty.get())) {
                LOG.info("Estimate Already Deleted -> Returning NOT FOUND");
                return HttpResponse.notFound();
            }
        }
        else if (depotParty.isEmpty()) {
            depotParty = Optional.of(new Party());
            depotParty.get().setCompanyId(depot);
            depotParty = Optional.of(partyRepository.save(depotParty.get()));
        }

        EstimateCancelRequest cancelRequest = new EstimateCancelRequest();
        cancelRequest.setEstimateNumber(estimateNumber);
        cancelRequest.setDepot(depotParty.get());
        estimateCancelRequestRepository.save(cancelRequest);

        return HttpResponse.ok();
    }

    @Patch(uri = "/{estimateNumber}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "update estimate totals",
        description = "When the creation of the estimate is delayed due to a 202, after the manual processing is complete, this method is called to perform the update of the totals.  It is often implemented by a depot so that the lessor can report updates.",
        method = "PATCH",
        operationId = "updateTotals",
        extensions = @Extension(properties = { @ExtensionProperty(name = "iicl-purpose", value = "activity", parseValue = true) })
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successfully received estimate totals", content = {@Content(schema = @Schema(implementation = Estimate.class))}),
        @ApiResponse(responseCode = "400", description = "an error occurred trying to update totals", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "403", description = "estimate total update is disallowed by security"),
        @ApiResponse(responseCode = "404", description = "the estimate was not found"),
        @ApiResponse(responseCode = "501", description = "this feature is not supported by this server"),
        @ApiResponse(responseCode = "503", description = "API is temporarily paused, and not accepting any activity"),
    })
    public HttpResponse<HttpStatus> allocate(@Parameter(name = "estimateNumber", description = "the estimate number", in = ParameterIn.PATH, required = true, schema = @Schema(type = "string", example = "DEHAMCE1856373", maxLength = 16)) String estimateNumber,
                                             @Body @RequestBody(description = "total breakdowns to finish creating an estimate", required = true, content = {@Content(schema = @Schema(implementation = EstimateAllocation.class, accessMode = Schema.AccessMode.AUTO))}) EstimateAllocation allocation) {
        LOG.info("Received Estimate Totals Allocation");
        conversionService.convert(allocation, JsonNode.class).ifPresent(jsonNode -> LOG.info(jsonNode.toString()));

        if (securityService.username().equals(AuthenticationProviderUserPassword.VALIDATE_USER_NAME)) {
            if (Objects.isNull(estimateNumber) || !estimateRepository.existsByEstimateNumberAndDepot(estimateNumber, allocation.getDepot())) {
                throw new IllegalArgumentException("Estimate does not exist to allocate.");
            }
        }

        if (allocation.getDepot() != null) {
            allocation.setDepot(partyRepository.save(allocation.getDepot()));
        }

        estimateAllocationRepository.save(allocation);

        LOG.info("Responding with OK");
        return HttpResponse.ok();
    }

    @Error(status = HttpStatus.NOT_FOUND)
    public HttpResponse<JsonError> notFound(HttpRequest request) {
        LOG.info("\tError - 404 - Not Found");
        return ApiErrorHandling.notFound(request);
    }

    @Error
    public HttpResponse<ErrorResponse> onSavedFailed(HttpRequest request, Throwable ex) {
        LOG.info("\tError - 400 - Bad Request", ex);
        return ApiErrorHandling.onSavedFailed(request, ex);
    }
}
