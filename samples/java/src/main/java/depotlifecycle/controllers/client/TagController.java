package depotlifecycle.controllers.client;

import depotlifecycle.domain.EstimateLineItemParty;
import depotlifecycle.domain.EstimatePhotoStatus;
import depotlifecycle.domain.EstimateTaxRule;
import depotlifecycle.domain.UnitOfMeasure;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.micronaut.views.View;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Validated
@Controller("/client/tag")
@RequiredArgsConstructor
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
@Hidden
public class TagController {
    private static Pattern digitsBetweenBrackets = Pattern.compile("\\[(\\d+)\\]");
    private static final Logger LOG = LoggerFactory.getLogger(TagController.class);

    @Get("/addParty")
    @View("tags/party")
    Mono<Map<String, Object>> addParty(@NonNull @Parameter String title, @NonNull @Parameter String id) {
        return Mono.just(Map.of("title", title, "id", id, "isExternalParty", false));
    }

    @Get("/addExternalParty")
    @View("tags/party")
    Mono<Map<String, Object>> addExternalParty(@NonNull @Parameter String title, @NonNull @Parameter String id) {
        return Mono.just(Map.of("title", title, "id", id, "isExternalParty", true));
    }

    @Get("/addTextInput")
    @View("tags/textInput")
    Mono<Map<String, Object>> addTextInput(@NonNull @Parameter String fieldName, @NonNull @Parameter String fieldLabel, @NonNull @Parameter String id) {
        return Mono.just(Map.of("id", id, "fieldName", fieldName, "fieldLabel", fieldLabel));
    }

    @Get("/addLineItem")
    @View("tags/lineItem")
    Mono<Map<String, Object>> addLineItem(@NonNull @Parameter Integer count) {
        return Mono.just(Map.of("title", "Line Item #" + (count + 1),
                "propertyPath", "lineItems[" + count + "].",
                "id", "estimateCreateLineItem" + count,
                "count", count + 1,
                "unitOfMeasures", UnitOfMeasure.values(),
                "parties", EstimateLineItemParty.values(),
                "taxRules", EstimateTaxRule.values()));
    }

    @Get("/addEstimateLineItemPart")
    @View("tags/estimateLineItemPart")
    Mono<Map<String, Object>> addEstimateLineItemPart(@NonNull @Parameter String propertyPath, @NonNull @Parameter Integer count) {
        Matcher matcher = digitsBetweenBrackets.matcher(propertyPath);

        String lineItemCount = "0";
        if (matcher.find()) {
            lineItemCount = matcher.group(1);
        }
        return Mono.just(Map.of("title", "Part #" + (count + 1),
                "propertyPath", propertyPath + "parts[" + count + "].",
                "id", "estimateCreatePart" + lineItemCount + count
        ));
    }

    @Get("/addEstimatePhoto")
    @View("tags/estimatePhoto")
    Mono<Map<String, Object>> addEstimatePhoto(@Nullable @Parameter String propertyPath, @NonNull @Parameter Integer count) {
        if(propertyPath == null) {
            propertyPath = "";
        }
        Matcher matcher = digitsBetweenBrackets.matcher(propertyPath);

        String lineItemCount = "";
        if (matcher.find()) {
            lineItemCount = matcher.group(1);
        }
        return Mono.just(Map.of("title", "Photo #" + (count + 1),
                "propertyPath", propertyPath + "photos[" + count + "].",
                "id", "estimateCreatePhoto" + (lineItemCount.length() > 1 ? "LineItem" : "") + lineItemCount + count,
                "photoStatuses", EstimatePhotoStatus.values()
        ));
    }

    @Get("/addGatePhoto")
    @View("tags/gatePhoto")
    Mono<Map<String, Object>> addGatePhoto(@NonNull @Parameter String id, @NonNull @Parameter Integer count) {
        return Mono.just(Map.of("title", "Photo #" + (count + 1),
                "propertyPath", "photos[" + count + "].",
                "id", id + "GatePhoto" + count
        ));
    }

    @Get("/addCustomerApproval")
    @View("tags/estimateCustomerApprove")
    Mono<Map<String, Object>> addCustomerApproval(@NonNull @Parameter String id, @NonNull @Parameter String cardTitle, @Nullable @Parameter String propertyPath, @NonNull @Parameter Boolean footerSubmit) {
        return Mono.just(Map.of("id", id, "cardTitle", cardTitle, "propertyPath", propertyPath, "footerSubmit", footerSubmit));
    }

    @Get("/addPreliminaryDecision")
    @View("tags/preliminaryDecision")
    Mono<Map<String, Object>> addPreliminaryDecision() {
        return Mono.just(Map.of("id", "createEstimatePreliminary", "propertyPath", "preliminaryDecision."));
    }

}
