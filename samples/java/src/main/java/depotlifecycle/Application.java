package depotlifecycle;

import depotlifecycle.domain.InsuranceCoverage;
import depotlifecycle.domain.Party;
import depotlifecycle.domain.Redelivery;
import depotlifecycle.domain.RedeliveryDetail;
import depotlifecycle.domain.RedeliveryUnit;
import depotlifecycle.repositories.PartyRepository;
import depotlifecycle.repositories.RedeliveryRepository;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.Micronaut;
import io.micronaut.runtime.event.annotation.EventListener;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

@OpenAPIDefinition(
    info = @Info(
        title = "Depot Life Cycle",
        version = "v3",
        description = "Sample implementation of the Depot Life Cycle API",
        license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
        contact = @Contact(url = "https://tritoninternationallimited.github.io/depot-lifecycle-api/", email = "api-edi@trtn.com")
    ),
    externalDocs = @ExternalDocumentation(description = "Find out more about this api", url = "https://github.com/TritonInternationalLimited/depot-lifecycle-api"),
    tags = {@Tag(name = "redelivery", description = "*notify turn in approval for shipping containers*")},
    servers = { @Server(url = "https://www.example.com/api/v3" )}
)
@Singleton
@RequiredArgsConstructor
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    private final RedeliveryRepository redeliveryRepository;
    private final PartyRepository partyRepository;

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

        Party customer = new Party();
        customer.setCompanyId("GBLONCUST");
        customer.setUserCode("JD");
        customer.setUserName("Jane Doe");
        customer.setCode("EXCUST");
        customer.setName("Example Customer");
        partyRepository.saveAll(Arrays.asList(depot1, depot2, customer));

        Redelivery redelivery = new Redelivery();
        redelivery.setRedeliveryNumber("AHAMG33141");
        redelivery.setApprovalDate(getLocal(LocalDateTime.now().minusDays(5)));
        redelivery.setExpirationDate(getLocal(LocalDateTime.now().plusMonths(4)));
        redelivery.setComments("an example redelivery level comment");
        redelivery.setDepot(depot1);
        redelivery.setRecipient(depot1);

        RedeliveryDetail noInsuranceDetail = new RedeliveryDetail();
        noInsuranceDetail.setRedelivery(redelivery);
        noInsuranceDetail.setCustomer(customer);
        noInsuranceDetail.setContract("EXCUST01-100000");
        noInsuranceDetail.setEquipment("22G1");
        noInsuranceDetail.setInspectionCriteria("IICL");
        noInsuranceDetail.setBillingParty(depot1);

        InsuranceCoverage coverage = new InsuranceCoverage();
        coverage.setAmountCovered(new BigDecimal(2000.0));
        coverage.setAmountCurrency("USD");
        coverage.setAllOrNothing(false);
        coverage.setExceptions(Arrays.asList("Exception #1", "Exception #2"));
        coverage.setExclusions(Arrays.asList("Exclusion #1", "Exclusion #2"));
        coverage.setInclusions(Arrays.asList("Inclusion #1", "Inclusion #2"));

        RedeliveryDetail insuranceDetail = new RedeliveryDetail();
        insuranceDetail.setRedelivery(redelivery);
        insuranceDetail.setCustomer(customer);
        insuranceDetail.setContract("EXCUST01-100000");
        insuranceDetail.setEquipment("22G2");
        insuranceDetail.setInspectionCriteria("IICL");
        insuranceDetail.setBillingParty(depot1);
        insuranceDetail.setInsuranceCoverage(coverage);

        RedeliveryUnit unit1 = new RedeliveryUnit();
        unit1.setRedeliveryDetail(noInsuranceDetail);
        unit1.setUnitNumber("CONU1234561");
        unit1.setManufactureDate(LocalDate.of(2012, 1, 1));
        unit1.setLastOnHireDate(LocalDate.of(2012, 2, 1));
        unit1.setLastOnHireLocation(depot2);
        unit1.setComments("Example unit comment #1.");

        RedeliveryUnit unit2 = new RedeliveryUnit();
        unit2.setRedeliveryDetail(insuranceDetail);
        unit2.setUnitNumber("CONU1234526");
        unit2.setManufactureDate(LocalDate.of(2012, 1, 1));
        unit2.setComments("Example unit comment #2.");

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