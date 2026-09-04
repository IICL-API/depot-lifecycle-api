package depotlifecycle.system;

import depotlifecycle.domain.Party;
import depotlifecycle.domain.gate.GateCreateRequest;
import depotlifecycle.domain.gate.GateRequestStatus;
import depotlifecycle.domain.gate.GateRequestType;
import depotlifecycle.domain.inventory.InventoryStatus;
import depotlifecycle.domain.inventory.InventoryUnit;
import depotlifecycle.domain.redelivery.RedeliveryDetail;
import depotlifecycle.domain.redelivery.RedeliveryUnit;
import depotlifecycle.domain.release.Release;
import depotlifecycle.domain.release.ReleaseDetail;
import depotlifecycle.domain.release.ReleaseUnit;
import depotlifecycle.domain.repair.Estimate;
import depotlifecycle.domain.repair.EstimateCondition;
import depotlifecycle.domain.repair.RepairComplete;
import depotlifecycle.domain.repair.WorkOrder;
import depotlifecycle.domain.repair.WorkOrderUnit;
import depotlifecycle.repositories.inventory.InventoryUnitRepository;
import depotlifecycle.repositories.redelivery.RedeliveryRepository;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Maintains the example depot inventory as lifecycle activity (gates, estimates, work orders,
 * repair completes, and releases) is processed, so the inventory status API reflects the
 * shipping containers currently on site and where each is in the lifecycle.
 */
@Singleton
@RequiredArgsConstructor
public class InventoryUpdater {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryUpdater.class);
    private final InventoryUnitRepository inventoryUnitRepository;
    private final RedeliveryRepository redeliveryRepository;
    private final PartyResolver partyResolver;

    public void recordGate(GateCreateRequest gate) {
        Party depot = resolveDepot(gate.getDepot());
        if (depot == null || gate.getType() == null || gate.getUnitNumber() == null) {
            return;
        }

        if (gate.getType() == GateRequestType.OUT) {
            inventoryUnitRepository.findByDepotAndUnitNumber(depot, gate.getUnitNumber()).ifPresent(unit -> {
                LOG.info("Inventory - {} gated out of {}; removing from inventory", unit.getUnitNumber(), depot.getCompanyId());
                inventoryUnitRepository.delete(unit);
            });
            return;
        }

        InventoryUnit unit = inventoryUnitRepository.findByDepotAndUnitNumber(depot, gate.getUnitNumber()).orElseGet(InventoryUnit::new);
        unit.setDepot(depot);
        unit.setUnitNumber(gate.getUnitNumber());

        boolean damaged = gate.getStatus() == GateRequestStatus.D;
        unit.setDamaged(damaged);
        unit.setStatus(damaged ? InventoryStatus.AWAITING_ESTIMATE : InventoryStatus.AVAILABLE);

        ZonedDateTime activityTime = Optional.ofNullable(gate.getActivityTime()).orElseGet(ZonedDateTime::now);
        unit.setStatusDateTime(activityTime);
        unit.setGateInDateTime(activityTime);
        unit.setRedeliveryNumber(gate.getAdviceNumber());

        //If the redelivery advice is known, note the customer that turned the shipping container in
        if (gate.getAdviceNumber() != null) {
            redeliveryRepository.findByRedeliveryNumber(gate.getAdviceNumber()).ifPresent(redelivery -> {
                for (RedeliveryDetail detail : redelivery.getDetails()) {
                    boolean unitListed = detail.getUnits().stream().map(RedeliveryUnit::getUnitNumber).anyMatch(number -> Objects.equals(number, gate.getUnitNumber()));
                    if (unitListed || detail.getUnits().isEmpty()) {
                        unit.setInCustomer(detail.getCustomer());
                        unit.setTargetGrade(detail.getGrade());
                        if (unitListed) {
                            break;
                        }
                    }
                }
            });
        }

        LOG.info("Inventory - {} gated in to {} as {}", unit.getUnitNumber(), depot.getCompanyId(), unit.getStatus());
        save(unit);
    }

    public void recordEstimate(Estimate estimate) {
        Party depot = resolveDepot(estimate.getDepot());
        if (depot == null || estimate.getUnitNumber() == null) {
            return;
        }

        inventoryUnitRepository.findByDepotAndUnitNumber(depot, estimate.getUnitNumber()).ifPresent(unit -> {
            unit.setEstimateNumber(estimate.getEstimateNumber());
            unit.setEstimateRevision(estimate.getRevision());

            boolean customerApproved = estimate.getCustomerApproval() != null || estimate.getCondition() == EstimateCondition.F || estimate.getCondition() == EstimateCondition.G;
            unit.setStatus(customerApproved ? InventoryStatus.AWAITING_OWNER_DECISION : InventoryStatus.AWAITING_CUSTOMER_APPROVAL);
            unit.setStatusDateTime(Optional.ofNullable(estimate.getEstimateTime()).orElseGet(ZonedDateTime::now));

            LOG.info("Inventory - {} now {} per estimate {}", unit.getUnitNumber(), unit.getStatus(), estimate.getEstimateNumber());
            save(unit);
        });
    }

    public void recordWorkOrder(WorkOrder workOrder) {
        Party depot = resolveDepot(workOrder.getDepot());
        if (depot == null || workOrder.getLineItems() == null) {
            return;
        }

        for (WorkOrderUnit lineItem : workOrder.getLineItems()) {
            inventoryUnitRepository.findByDepotAndUnitNumber(depot, lineItem.getUnitNumber()).ifPresent(unit -> {
                unit.setStatus(InventoryStatus.REPAIR_AUTHORIZED);
                unit.setStatusDateTime(Optional.ofNullable(workOrder.getApprovalDate()).orElseGet(ZonedDateTime::now));
                unit.setWorkOrderNumber(workOrder.getWorkOrderNumber());
                unit.setEstimateNumber(Optional.ofNullable(lineItem.getEstimateNumber()).orElse(unit.getEstimateNumber()));
                unit.setTargetGrade(Optional.ofNullable(lineItem.getEffectiveInspectionCriteria()).orElse(unit.getTargetGrade()));

                LOG.info("Inventory - {} now {} per work order {}", unit.getUnitNumber(), unit.getStatus(), workOrder.getWorkOrderNumber());
                save(unit);
            });
        }
    }

    public void recordRepairComplete(Party depot, RepairComplete repairComplete) {
        depot = resolveDepot(depot);
        if (depot == null || repairComplete.getUnitNumber() == null) {
            return;
        }

        inventoryUnitRepository.findByDepotAndUnitNumber(depot, repairComplete.getUnitNumber()).ifPresent(unit -> {
            ZonedDateTime completionDate = Optional.ofNullable(repairComplete.getCompletionDate()).orElseGet(ZonedDateTime::now);
            unit.setStatus(InventoryStatus.AVAILABLE);
            unit.setStatusDateTime(completionDate);
            unit.setPrimaryRepairCompleteDateTime(completionDate);
            unit.setDamaged(false);
            unit.setCurrentGrade(Optional.ofNullable(unit.getTargetGrade()).orElse(unit.getCurrentGrade()));

            LOG.info("Inventory - {} now {} per repair complete of work order {}", unit.getUnitNumber(), unit.getStatus(), repairComplete.getWorkOrderNumber());
            save(unit);
        });
    }

    public void recordRelease(Release release) {
        Party depot = resolveDepot(release.getDepot());
        if (depot == null || release.getDetails() == null) {
            return;
        }

        for (ReleaseDetail detail : release.getDetails()) {
            for (ReleaseUnit releaseUnit : detail.getUnits()) {
                inventoryUnitRepository.findByDepotAndUnitNumber(depot, releaseUnit.getUnitNumber()).ifPresent(unit -> {
                    unit.setStatus(InventoryStatus.TIED_OUTBOUND);
                    unit.setStatusDateTime(Optional.ofNullable(release.getApprovalDate()).orElseGet(ZonedDateTime::now));
                    unit.setReleaseNumber(release.getReleaseNumber());
                    unit.setOutCustomer(detail.getCustomer());

                    LOG.info("Inventory - {} now {} per release {}", unit.getUnitNumber(), unit.getStatus(), release.getReleaseNumber());
                    save(unit);
                });
            }
        }
    }

    /**
     * Resolves the depot carried on a message to the stored party row for its identity, so
     * inventory lookups key off the same row {@link PartyResolver} stores.
     */
    private Party resolveDepot(Party depot) {
        return partyResolver.find(depot).orElse(depot);
    }

    private void save(InventoryUnit unit) {
        if (unit.getId() == null) {
            inventoryUnitRepository.save(unit);
        }
        else {
            inventoryUnitRepository.update(unit);
        }
    }
}
