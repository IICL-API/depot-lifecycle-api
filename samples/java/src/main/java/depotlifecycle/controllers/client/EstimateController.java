package depotlifecycle.controllers.client;

import depotlifecycle.DepotLifecycleConfiguration;
import depotlifecycle.clients.EstimateClient;
import depotlifecycle.commands.EstimateSearchCommand;
import depotlifecycle.domain.Estimate;
import depotlifecycle.view.HtmlStatusException;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.micronaut.views.View;
import io.micronaut.views.ViewsRenderer;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Secured(SecurityRule.IS_ANONYMOUS)
@Validated
@Controller("/client/estimate")
@RequiredArgsConstructor
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
@Hidden
public class EstimateController {
    private static final Logger LOG = LoggerFactory.getLogger(EstimateController.class);
    private final ViewsRenderer viewsRenderer;
    private final DepotLifecycleConfiguration projectConfig;
    private final Validator validator;
    private final EstimateClient estimateClient;

    @View("estimate")
    @Get
    Mono<Map<String, Object>> index() {
        Map<String, Object> model = Map.ofEntries(
                Map.entry("projectConfig", projectConfig)
        );
        return Mono.just(model);
    }

    @Post("/list")
    @View("estimateList")
    Mono<Map<String, Object>> list(@Body EstimateSearchCommand cmd) {
        LOG.info("Client - Estimate - List");
        Set<ConstraintViolation<EstimateSearchCommand>> violations = validator.validate(cmd);
        if (!violations.isEmpty()) {
            ConstraintViolation<EstimateSearchCommand> violation = violations.iterator().next();
            String errorMessage = String.join(" ", violation.getPropertyPath().toString(), violation.getMessage());
            throw new HtmlStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        Publisher<List<Estimate>> estimatePublisher = estimateClient.search(cmd.getEstimateNumber(), cmd.getUnitNumber(), cmd.getDepot(), cmd.getLessee(), cmd.getRevision(), cmd.getEquipmentCode());

        return Mono.from(estimatePublisher)
                .onErrorMap(HttpClientResponseException.class, e -> {
                    String responseBody = e.getResponse().getBody(String.class).orElse("No response body");
                    String responseHeaders = e.getResponse().getHeaders().toString();
                    int statusCode = e.getStatus().getCode();

                    String errorMessage = String.format(
                            "HttpClientResponseException: %s\nStatus Code: %d\nResponse Body: %s\nResponse Headers: %s",
                            e.getMessage(), statusCode, responseBody, responseHeaders
                    );

                    String message = String.join(": ", "Unexpected Error Calling API", errorMessage);
                    throw new HtmlStatusException(e.getStatus(), message);
                }).map(estimates -> Map.of("estimates", estimates));
    }
}
