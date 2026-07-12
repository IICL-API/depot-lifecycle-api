package depotlifecycle;

import depotlifecycle.domain.*;
import depotlifecycle.domain.inventory.InventoryStatus;
import depotlifecycle.domain.inventory.InventoryUnit;
import depotlifecycle.domain.redelivery.*;
import depotlifecycle.domain.release.*;
import depotlifecycle.domain.repair.InsuranceCoverage;
import depotlifecycle.repositories.ExternalPartyRepository;
import depotlifecycle.repositories.PartyRepository;
import depotlifecycle.repositories.inventory.InventoryUnitRepository;
import depotlifecycle.repositories.redelivery.RedeliveryRepository;
import depotlifecycle.repositories.release.ReleaseRepository;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.Micronaut;
import io.micronaut.runtime.event.annotation.EventListener;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@OpenAPIDefinition(
    info = @Info(
        title = "Depot Life Cycle",
            version = "2.2.11",
        description = "${depotlifecycle.documentation.application.description}",
        license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
        contact = @Contact(email = "technical@iicl.org")
    ),
    externalDocs = @ExternalDocumentation(description = "Find out more about this api", url = "https://github.com/IICL-API/depot-lifecycle-api"),
    tags = {
        @Tag(name = "inventory", description = "*This API is only proposed at this time and is in an alpha state - it is not production approved and may change without notice.*\n\n*near real time inventory status of shipping containers at a depot for status reconciliation between depot and owner systems*", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="Inventory")})}),
        @Tag(name = "estimate photos", description = "*estimate photo uploads*"),
        @Tag(name = "gate photos", description = "*gate photo uploads*"),
        @Tag(name = "redelivery", description = "*turn in approval for shipping containers*"),
        @Tag(name = "release", description = "*lease out approval for shipping containers*"),
        @Tag(name = "gate", description = "*manage gate ins and gate outs of shipping containers*"),
        @Tag(name = "estimate", description = "*a damage or upgrade estimate for a shipping container after turn in*"),
        @Tag(name = "workOrder", description = "*manage damage estimates that are approved for repair*"),
        @Tag(name="m_error_response", description="<SchemaDefinition schemaRef=\"#/components/schemas/ErrorResponse\" />", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="ErrorResponse")})}),
        @Tag(name="m_insurance_coverage", description="<SchemaDefinition schemaRef=\"#/components/schemas/InsuranceCoverage\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="InsuranceCoverage")})}),
        @Tag(name="m_party", description="<SchemaDefinition schemaRef=\"#/components/schemas/Party\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="Party")})}),
        @Tag(name="m_pending_response", description="<SchemaDefinition schemaRef=\"#/components/schemas/PendingResponse\" />", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="PendingResponse")})}),
        @Tag(name="m_redelivery", description="<SchemaDefinition schemaRef=\"#/components/schemas/Redelivery\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="Redelivery")})}),
        @Tag(name="m_redelivery_detail", description="<SchemaDefinition schemaRef=\"#/components/schemas/RedeliveryDetail\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="RedeliveryDetail")})}),
        @Tag(name="m_redelivery_unit", description="<SchemaDefinition schemaRef=\"#/components/schemas/RedeliveryUnit\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="RedeliveryUnit")})}),
        @Tag(name="m_release", description="<SchemaDefinition schemaRef=\"#/components/schemas/Release\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="Release")})}),
        @Tag(name="m_release_detail", description="<SchemaDefinition schemaRef=\"#/components/schemas/ReleaseDetail\" />", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="ReleaseDetail")})}),
        @Tag(name="m_release_detail_criteria", description="<SchemaDefinition schemaRef=\"#/components/schemas/ReleaseDetailCriteria\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="ReleaseDetailCriteria")})}),
        @Tag(name="m_release_unit", description="<SchemaDefinition schemaRef=\"#/components/schemas/ReleaseUnit\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="ReleaseUnit")})}),
        @Tag(name="m_gate_create", description="<SchemaDefinition schemaRef=\"#/components/schemas/GateCreateRequest\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="GateCreateRequest")})}),
        @Tag(name="m_gate_photo", description="<SchemaDefinition schemaRef=\"#/components/schemas/GatePhoto\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="GatePhoto")})}),
        @Tag(name="m_gate_response", description="<SchemaDefinition schemaRef=\"#/components/schemas/GateResponse\"/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="GateResponse")})}),
        @Tag(name="m_gate_status", description="<SchemaDefinition schemaRef=\"#/components/schemas/GateStatus\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="GateStatus")})}),
        @Tag(name="m_gate_update_request", description="<SchemaDefinition schemaRef=\"#/components/schemas/GateUpdateRequest\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="GateUpdateRequest")})}),
        @Tag(name="m_estimate", description="<SchemaDefinition schemaRef=\"#/components/schemas/Estimate\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="Estimate")})}),
        @Tag(name="m_estimate_photo", description="<SchemaDefinition schemaRef=\"#/components/schemas/EstimatePhoto\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="EstimatePhoto")})}),
        @Tag(name="m_estimate_line_item", description="<SchemaDefinition schemaRef=\"#/components/schemas/EstimateLineItem\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="EstimateLineItem")})}),
        @Tag(name="m_estimate_line_item_part", description="<SchemaDefinition schemaRef=\"#/components/schemas/EstimateLineItemPart\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="EstimateLineItemPart")})}),
        @Tag(name="m_estimate_line_item_photo", description="<SchemaDefinition schemaRef=\"#/components/schemas/EstimateLineItemPhoto\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="EstimateLineItemPhoto")})}),
        @Tag(name="m_estimate_tax_rate", description="<SchemaDefinition schemaRef=\"#/components/schemas/EstimateTaxRate\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="EstimateTaxRate")})}),
        @Tag(name="m_estimate_allocation", description="<SchemaDefinition schemaRef=\"#/components/schemas/EstimateAllocation\"/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="EstimateAllocation")})}),
        @Tag(name="m_estimate_allocation_type", description="<SchemaDefinition schemaRef=\"#/components/schemas/EstimateAllocationType\"/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="EstimateAllocationType")})}),
        @Tag(name="m_preliminary_decision", description="<SchemaDefinition schemaRef=\"#/components/schemas/PreliminaryDecision\"/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="PreliminaryDecision")})}),
        @Tag(name="m_estimate_customer_approval", description="<SchemaDefinition schemaRef=\"#/components/schemas/EstimateCustomerApproval\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="EstimateCustomerApproval")})}),
        @Tag(name="m_work_order", description="<SchemaDefinition schemaRef=\"#/components/schemas/WorkOrder\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="WorkOrder")})}),
        @Tag(name="m_work_order_unit", description="<SchemaDefinition schemaRef=\"#/components/schemas/WorkOrderUnit\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="WorkOrderUnit")})}),
        @Tag(name="m_repair_complete", description="<SchemaDefinition schemaRef=\"#/components/schemas/RepairComplete\" showReadOnly={false}/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="RepairComplete")})}),
        @Tag(name="m_inventory_snapshot", description="<SchemaDefinition schemaRef=\"#/components/schemas/InventorySnapshot\"/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="InventorySnapshot")})}),
        @Tag(name="m_inventory_unit", description="<SchemaDefinition schemaRef=\"#/components/schemas/InventoryUnit\"/>", extensions = { @Extension(properties = {@ExtensionProperty(name = "x-displayName", value="InventoryUnit")})})
    },
    extensions = {
        @Extension(properties = {@ExtensionProperty(name = "tagGroups", value = "[{ \"name\": \"API: Proposed (Alpha)\", \"tags\": [\"inventory\" ] }, { \"name\": \"API: Under Development (Beta)\", \"tags\": [\"estimate photos\", \"gate photos\" ] }, { \"name\": \"API: Production Ready\", \"tags\": [  \"redelivery\", \"release\", \"gate\", \"estimate\", \"workOrder\" ] }, { \"name\": \"Models\", \"tags\": [ \"m_error_response\", \"m_insurance_coverage\", \"m_party\", \"m_pending_response\", \"m_redelivery\", \"m_redelivery_detail\", \"m_redelivery_unit\", \"m_release\", \"m_release_detail\", \"m_release_detail_criteria\", \"m_release_unit\", \"m_gate_create\", \"m_gate_photo\", \"m_gate_response\", \"m_gate_status\", \"m_gate_update_request\", \"m_estimate\", \"m_estimate_photo\", \"m_estimate_line_item\", \"m_estimate_line_item_part\", \"m_estimate_line_item_photo\", \"m_estimate_tax_rate\", \"m_estimate_allocation\", \"m_estimate_allocation_type\", \"m_preliminary_decision\", \"m_estimate_customer_approval\", \"m_work_order\", \"m_work_order_unit\", \"m_repair_complete\", \"m_inventory_snapshot\", \"m_inventory_unit\" ] }]", parseValue = true)})
    },
    servers = {
        @Server(url = "https://api.example.com/examplecontextpath")
    },
    security = {
        @SecurityRequirement(name = "Dynamic_Token"),
        @SecurityRequirement(name = "Static_Token"),
    }
)
@SecuritySchemes (
    value = {
            @SecurityScheme(
                    name = "JWT_TOKEN",
                    description = "JWT Bearer Authentication",
                    type = SecuritySchemeType.HTTP,
                    bearerFormat = "JWT",
                    scheme = "bearer"
            )
    }
)
@Singleton
@RequiredArgsConstructor
//@Introspected(packages="depotlifecycle.domain", includedAnnotations=Entity.class)
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    private final RedeliveryRepository redeliveryRepository;
    private final ReleaseRepository releaseRepository;
    private final PartyRepository partyRepository;
    private final ExternalPartyRepository externalPartyRepository;
    private final InventoryUnitRepository inventoryUnitRepository;

    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }

    @EventListener
    void init(StartupEvent event) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Populating data");
        }

        buildTestData();
    }

    private void buildTestData() {
        Party depot1 = new Party();
        depot1.setCompanyId("DEHAMCMRA");
        depot1.setUserCode("JDOE");
        depot1.setUserName("John Doe");
        depot1.setCode("HAMG");
        depot1.setName("Depot Operator #1");

        Party depot2 = new Party();
        depot2.setCompanyId("DEHAMCMRB");
        depot2.setUserCode("JDOE");
        depot2.setUserName("John Doe");
        depot2.setCode("HAMB");
        depot2.setName("Depot Operator #2");

        ExternalParty customer = new ExternalParty();
        customer.setCompanyId("GBLONCUST");
        customer.setUserCode("JD");
        customer.setUserName("Jane Doe");
        customer.setCode("EXCUST");
        customer.setName("Example Customer");

        Party owner = new Party();
        owner.setCompanyId("USSFOEXAM");
        owner.setUserCode("JD");
        owner.setUserName("Jane Doe");
        owner.setCode("EXAM");
        owner.setName("Example Lessor Name");

        externalPartyRepository.save(customer);
        partyRepository.saveAll(Arrays.asList(depot1, depot2, owner));

        buildRedeliveries(depot1, depot2, customer, owner);
        buildReleases(depot1, depot2, customer, owner);
        buildInventory(depot1, customer);
    }

    private void buildInventory(Party depot, ExternalParty customer) {
        InventoryUnit awaitingEstimate = new InventoryUnit();
        awaitingEstimate.setDepot(depot);
        awaitingEstimate.setUnitNumber("CONU1234561");
        awaitingEstimate.setEquipment("22G1");
        awaitingEstimate.setStatus(InventoryStatus.AWAITING_ESTIMATE);
        awaitingEstimate.setStatusDateTime(getLocal(LocalDateTime.now().minusDays(2)));
        awaitingEstimate.setDamaged(true);
        awaitingEstimate.setTargetGrade("IICL");
        awaitingEstimate.setGateInDateTime(getLocal(LocalDateTime.now().minusDays(2)));
        awaitingEstimate.setInCustomer(customer);
        awaitingEstimate.setRedeliveryNumber("AHAMG33141");
        awaitingEstimate.setComments(List.of("Example inventory comment #1."));

        InventoryUnit underRepair = new InventoryUnit();
        underRepair.setDepot(depot);
        underRepair.setUnitNumber("CONU1234526");
        underRepair.setEquipment("22G2");
        underRepair.setStatus(InventoryStatus.REPAIR_AUTHORIZED);
        underRepair.setStatusDateTime(getLocal(LocalDateTime.now().minusDays(4)));
        underRepair.setDamaged(true);
        underRepair.setTargetGrade("CWCA");
        underRepair.setGateInDateTime(getLocal(LocalDateTime.now().minusDays(12)));
        underRepair.setInCustomer(customer);
        underRepair.setRedeliveryNumber("AHAMG33141");
        underRepair.setEstimateNumber("DEHAMCE1856373");
        underRepair.setEstimateRevision(1);
        underRepair.setWorkOrderNumber("WHAMG46019");

        InventoryUnit tiedOutbound = new InventoryUnit();
        tiedOutbound.setDepot(depot);
        tiedOutbound.setUnitNumber("CONU1234592");
        tiedOutbound.setEquipment("42G1");
        tiedOutbound.setStatus(InventoryStatus.TIED_OUTBOUND);
        tiedOutbound.setStatusDateTime(getLocal(LocalDateTime.now().minusDays(1)));
        tiedOutbound.setDamaged(false);
        tiedOutbound.setCurrentGrade("IICL");
        tiedOutbound.setGateInDateTime(getLocal(LocalDateTime.now().minusMonths(1)));
        tiedOutbound.setPrimaryRepairCompleteDateTime(getLocal(LocalDateTime.now().minusDays(9)));
        tiedOutbound.setOutCustomer(customer);
        tiedOutbound.setReleaseNumber("RHAMG134512");

        InventoryUnit onHold = new InventoryUnit();
        onHold.setDepot(depot);
        onHold.setUnitNumber("CONU1234618");
        onHold.setEquipment("22G1");
        onHold.setStatus(InventoryStatus.AVAILABLE);
        onHold.setStatusDateTime(getLocal(LocalDateTime.now().minusDays(30)));
        onHold.setDamaged(false);
        onHold.setCurrentGrade("CWCA");
        onHold.setOnHold(true);
        onHold.setHoldReason("technical hold - pending bulletin TB1234");
        onHold.setGateInDateTime(getLocal(LocalDateTime.now().minusMonths(2)));

        inventoryUnitRepository.saveAll(Arrays.asList(awaitingEstimate, underRepair, tiedOutbound, onHold));
    }

    private void buildReleases(Party depot1, Party depot2, ExternalParty customer, Party owner) {
        Release release = new Release();
        release.setStatus(ReleaseStatus.APPROVED);
        release.setReleaseNumber("RHAMG134512");
        release.setType(ReleaseType.BOOK);
        release.setApprovalDate(getLocal(LocalDateTime.now().minusDays(5)));
        release.setExpirationDate(getLocal(LocalDateTime.now().plusMonths(4)));
        release.setComments(List.of("an example release level comment"));
        release.setDepot(depot1);
        release.setOwner(owner);
        release.setRecipient(depot1);
        release.setQuantity(1);

        ReleaseDetail blanketDetail = new ReleaseDetail();
        blanketDetail.setCustomer(customer);
        blanketDetail.setContract("EXCUST01-100000");
        blanketDetail.setEquipment("22G1");
        blanketDetail.setGrade("IICL");
        blanketDetail.setQuantity(1);

        ReleaseDetail unitDetail = new ReleaseDetail();
        unitDetail.setCustomer(customer);
        unitDetail.setContract("EXCUST01-100000");
        unitDetail.setEquipment("42G1");
        unitDetail.setGrade("IICL");
        unitDetail.setQuantity(1);

        ReleaseUnit unit1 = new ReleaseUnit();
        unit1.setUnitNumber("CONU1234561");
        unit1.setComments(List.of("Example unit comment #1."));
        unit1.setStatus(ReleaseUnitStatus.TIED);

        ReleaseUnit unit2 = new ReleaseUnit();
        unit2.setUnitNumber("CONU1234526");
        unit2.setComments(List.of("Example unit comment #2."));
        unit2.setStatus(ReleaseUnitStatus.TIED);
        unit2.setManufactureDate(LocalDate.of(2012, 1, 1));

        release.getDetails().add(blanketDetail);
        release.getDetails().add(unitDetail);
        unitDetail.getUnits().add(unit1);
        unitDetail.getUnits().add(unit2);

        releaseRepository.save(release);
    }

    private void buildRedeliveries(Party depot1, Party depot2, ExternalParty customer, Party owner) {
        Redelivery redelivery = new Redelivery();
        redelivery.setStatus(RedeliveryStatus.APPROVED);
        redelivery.setRedeliveryNumber("AHAMG33141");
        redelivery.setApprovalDate(getLocal(LocalDateTime.now().minusDays(5)));
        redelivery.setExpirationDate(getLocal(LocalDateTime.now().plusMonths(4)));
        redelivery.setComments(List.of("an example redelivery level comment"));
        redelivery.setDepot(depot1);
        redelivery.setRecipient(depot1);
        redelivery.setOwner(owner);
        redelivery.setQuantity(2);

        RedeliveryDetail noInsuranceDetail = new RedeliveryDetail();
        noInsuranceDetail.setCustomer(customer);
        noInsuranceDetail.setContract("EXCUST01-100000");
        noInsuranceDetail.setEquipment("22G1");
        noInsuranceDetail.setQuantity(1);

        InsuranceCoverage coverage = new InsuranceCoverage();
        coverage.setAmountCovered(new BigDecimal("2000.0"));
        coverage.setAmountCurrency("USD");
        coverage.setAllOrNothing(false);
        coverage.setExceptions(Arrays.asList("Exception #1", "Exception #2"));
        coverage.setExclusions(Arrays.asList("Exclusion #1", "Exclusion #2"));
        coverage.setInclusions(Arrays.asList("Inclusion #1", "Inclusion #2"));

        RedeliveryDetail insuranceDetail = new RedeliveryDetail();
        insuranceDetail.setCustomer(customer);
        insuranceDetail.setContract("EXCUST01-100000");
        insuranceDetail.setEquipment("22G2");
        insuranceDetail.setGrade("IICL");
        insuranceDetail.setInsuranceCoverage(coverage);
        insuranceDetail.setQuantity(1);

        RedeliveryUnit unit1 = new RedeliveryUnit();
        unit1.setUnitNumber("CONU1234561");
        unit1.setManufactureDate(LocalDate.of(2012, 1, 1));
        unit1.setLastOnHireDate(LocalDate.of(2012, 2, 1));
        unit1.setLastOnHireLocation(depot2);
        unit1.setComments(List.of("Example unit comment #1."));
        unit1.setBillingParty(depot1);
        unit1.setInspectionCriteria("IICL");
        unit1.setStatus(RedeliveryUnitStatus.TIED);

        RedeliveryUnit unit2 = new RedeliveryUnit();
        unit2.setUnitNumber("CONU1234526");
        unit2.setManufactureDate(LocalDate.of(2012, 1, 1));
        unit2.setComments(List.of("Example unit comment #2."));
        unit2.setBillingParty(depot1);
        unit2.setInspectionCriteria("CWCA");
        unit2.setStatus(RedeliveryUnitStatus.TIED);

        redelivery.getDetails().add(insuranceDetail);
        redelivery.getDetails().add(noInsuranceDetail);
        noInsuranceDetail.getUnits().add(unit1);
        insuranceDetail.getUnits().add(unit2);

        redeliveryRepository.save(redelivery);
    }

    private static ZonedDateTime getLocal(LocalDateTime date) {
        //Don't actually store a time zone for the purposes of this application
        return ZonedDateTime.of(date, ZoneId.systemDefault());
    }
}
