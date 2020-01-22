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
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
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
        version = "2.1.0",
        description = "# Purpose\n\n A depot centric API for managing the interchange activity & repair lifecycle of a shipping container.  The API is expected to be used by Customers, Depots, and Owners to facilitate real time communication between systems instead of traditional EDI files.\n\n \n\n # Overview\n\n The depot lifecycle API is a RESTful API.  It's requests & responses are loosely based upon traditional EDI files.  For code defintions, explanations, or traditional EDI definitions refer to [IICL TB 002, February 2003](https://www.iicl.org/iiclforms/assets/File/public/bulletins/TB002_EDIS_February_2003.pdf).  The requests and responses are formatted according to the [JSON](https://www.json.org/) standard.\n\n \n\n # Reference\n\n This API is documented by the [OpenAPI](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md) specification.  This page was created by the [Swagger-Editor](https://github.com/swagger-api/swagger-editor) tool.  The *yaml* specification file can be found [here](depot-lifecycle-openapi.yaml).\n\n \n\n # Expectations\n\n Not all parties are required or expected to implement every feature.  Any feature not implemented should return a http status code of `501`.\n\n \n\n # Depreciation\n\n If this API were to be discontinued, a minimum of 6 months time would pass before it's removal.\n\n # Security \n\n [JSON Web Token, or JWT,](https://jwt.io/) is used for stateless authentication to secure all endpoints of this API.  Since all requests are sent using https, tokens are not encrypted.  Tokens follow the [RFC 6750 Bearer Token](https://tools.ietf.org/html/rfc6750) format.\n\n## 1. Example JWT Token\n\n \n\n ```\n\n {\n\n \"username\": \"jdoe\",\n\n \"roles\": [\n\n \"ROLE_GATE_CREATE\",\n\n \"ROLE_GATE_UPDATE\"\n\n ],\n\n \"email\":\"j.doe@example.com\",\n\n \"token_type\":\"Bearer\",\n\n \"access_token\":\"eyJhbGciOiJIUzI1NiJ9...\",\n\n \"expires_in\":3600,        \n\n \"refresh_token\":\"eyJhbGciOiJIUzI1NiJ9...\"\n\n }\n\n ```\n\n \n\n \n\n An access_token is provided for authentication to API endpoints and a refresh_token is provided to generate a new access_token when one expires.\n\n \n\n \n\n ## 2. Obtaining an access token\n\n Issuing a POST request to `/api/login` with a username and password payload will cause a JWT token to be issued in the response.\n\n ```\n\n POST /api/login HTTP/1.1\n\n Content-Type: text/plain; charset=utf-8\n\n Host: www.example.com\n\n \n\n \n\n {\n\n \"username\": \"jdoe\",\n\n \"password\": \"jdoepassword\"\n\n }\n\n ```\n\n \n\n \n\n ## 3. Refreshing an expired token\n\n Tokens expire after 1 hour.  Issuing a POST request to `/oauth/access_token` with the refresh_token from the JWT token previously issued and a grant_type of refresh_token will reissue the JWT.\n\n ```\n\n POST /oauth/access_token HTTP/1.1\n\n Host: www.example.com\n\n Content-Type: application/x-www-form-urlencoded\n\n \n\n \n\n grant_type=refresh_token&refresh_token=eyJhbGciOiJIUzI1NiJ9...\n\n ```\n\n \n\n \n\n ## 4. Checking if a token is valid\n\n Any token can be checked if it's still valid by issuing a POST request to `/api/validate`.\n\n ```\n\n GET /api/validate HTTP/1.1\n\n Host: www.example.com\n\n Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...\n\n Content-Type: application/json; charset=utf-8\n\n ```\n\n \n\n \n\n ## 5. Accessing a protected resource\n\n Use the authorization header to supply the JWT for authentication to a protected resource.\n\n ```\n\n GET /api/v3/gate/CONU1234561 HTTP/1.1\n\n Host: www.example.com\n\n Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...\n\n ```\n\n",
        license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
        contact = @Contact(url = "https://tritoninternationallimited.github.io/depot-lifecycle-api/", email = "api-edi@trtn.com")
    ),
    externalDocs = @ExternalDocumentation(description = "Find out more about this api", url = "https://github.com/TritonInternationalLimited/depot-lifecycle-api"),
    tags = {
        @Tag(name = "redelivery", description = "*turn in approval for shipping containers*"),
        @Tag(name = "release", description = "*lease out approval for shipping containers*"),
        @Tag(name = "gate", description = "*manage gate ins and gate outs of shipping containers*"),
        @Tag(name = "estimate", description = "*a damage or upgrade estimate for a shipping container after turn in*"),
        @Tag(name = "workOrder", description = "*manage damage estimates that are approved for repair*")
    },
    servers = {
        @Server(url = "https://testapi.trtn.com/triton")
    }
)
@SecuritySchemes({
    @SecurityScheme(name = "JWT Based Authentication", type = SecuritySchemeType.HTTP, paramName = "Authorization", in = SecuritySchemeIn.HEADER, scheme = "bearer", bearerFormat = "JWT", description = "[JSON Web Token, or JWT,](https://jwt.io/) is used for stateless authentication to secure all endpoints of this API.  Since all requests are sent using https, tokens are not encrypted.  Tokens follow the [RFC 6750 Bearer Token](https://tools.ietf.org/html/rfc6750) format.")
})
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
        noInsuranceDetail.setQuantity(1);

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
        insuranceDetail.setQuantity(1);

        RedeliveryUnit unit1 = new RedeliveryUnit();
        unit1.setRedeliveryDetail(noInsuranceDetail);
        unit1.setUnitNumber("CONU1234561");
        unit1.setManufactureDate(LocalDate.of(2012, 1, 1));
        unit1.setLastOnHireDate(LocalDate.of(2012, 2, 1));
        unit1.setLastOnHireLocation(depot2);
        unit1.setComments("Example unit comment #1.");
        unit1.setStatus("TIED");

        RedeliveryUnit unit2 = new RedeliveryUnit();
        unit2.setRedeliveryDetail(insuranceDetail);
        unit2.setUnitNumber("CONU1234526");
        unit2.setManufactureDate(LocalDate.of(2012, 1, 1));
        unit2.setComments("Example unit comment #2.");
        unit2.setStatus("TIED");

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