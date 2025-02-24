package depotlifecycle.controllers.client;

import depotlifecycle.domain.*;
import depotlifecycle.domain.repair.EstimateLineItemParty;
import depotlifecycle.domain.repair.EstimatePhotoStatus;
import depotlifecycle.domain.repair.EstimateTaxRule;
import depotlifecycle.domain.equipment.*;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
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
    private static final Pattern digitsBetweenBrackets = Pattern.compile("\\[(\\d+)\\]");
    private static final Logger LOG = LoggerFactory.getLogger(TagController.class);

    @Get("/addParty")
    @View("tags/party")
    Mono<Map<String, Object>> addParty(@NonNull @Parameter String title, @NonNull @Parameter String id, @NonNull @Parameter String propertyPath) {
        return Mono.just(Map.of(
                "title", title,
                "id", id,
                "isExternalParty", false,
                "propertyPath", propertyPath
        ));
    }

    @Get("/addExternalParty")
    @View("tags/party")
    Mono<Map<String, Object>> addExternalParty(@NonNull @Parameter String title, @NonNull @Parameter String id, @NonNull @Parameter String propertyPath) {
        return Mono.just(Map.of(
                "title", title,
                "id", id,
                "isExternalParty", true,
                "propertyPath", propertyPath
        ));
    }

    @Get("/addMachineryInfo")
    @View("tags/machineryInfo")
    Mono<Map<String, Object>> addMachineryInfo(@NonNull @Parameter String title, @NonNull @Parameter String id, @NonNull @Parameter String propertyPath) {
        return Mono.just(Map.of(
                "title", title,
                "id", id,
                "propertyPath", propertyPath
        ));
    }

    @Get("/addEquipmentDetail")
    @View("tags/equipmentDetail")
    Mono<Map<String, Object>> addEquipmentDetail(@NonNull @Parameter String title, @NonNull @Parameter String id, @NonNull @Parameter String propertyPath) {
        return Mono.just(Map.of(
                "title", title,
                "id", id,
                "propertyPath", propertyPath
        ));
    }

    @Get("/addChassisInfo")
    @View("tags/chassisInfo")
    Mono<Map<String, Object>> addChassisInfo(@NonNull @Parameter String title, @NonNull @Parameter String id, @NonNull @Parameter String propertyPath) {
        return Mono.just(Map.of(
                "title", title,
                "id", id,
                "propertyPath", propertyPath,
                "brakeConditions", BrakeCondition.values()
        ));
    }

    @Get("/addGensetInfo")
    @View("tags/gensetInfo")
    Mono<Map<String, Object>> addGensetInfo(@NonNull @Parameter String title, @NonNull @Parameter String id, @NonNull @Parameter String propertyPath) {
        return Mono.just(Map.of(
                "title", title,
                "id", id,
                "propertyPath", propertyPath,
                "mountTypes", MountType.values()
        ));
    }

    @Get("/addInspectionReport")
    @View("tags/inspectionReport")
    Mono<Map<String, Object>> addInspectionReport(@Nullable @Parameter String propertyPath, @NonNull @Parameter Integer count) {
        String id = propertyPath.replace('[', '_').replace(']', '_').replace('.', '_');
        return Mono.just(Map.of("title", "Inspection #" + (count + 1),
                "propertyPath", propertyPath + "inspections[" + count + "].",
                "id", id + "InspectionReport" + count,
                "mandateLevels", MandateLevel.values(),
                "inspectionResults", InspectionResult.values()
        ));
    }

    @Get("/addTireTreadMeasurement")
    @View("tags/tireTreadMeasurement")
    Mono<Map<String, Object>> addTireTreadMeasurement(@Nullable @Parameter String propertyPath, @NonNull @Parameter Integer count) {
        String id = propertyPath.replace('[', '_').replace(']', '_').replace('.', '_');
        return Mono.just(Map.of("title", "Tread Measurement #" + (count + 1),
                "propertyPath", propertyPath + "treadMeasurements[" + count + "].",
                "id", id + "TireTreadMeasurement" + count,
                "count", count + 1,
                "unitOfMeasures", UnitOfMeasure.values(),
                "tireTreadLocations", TireTreadLocation.values()
        ));
    }

    @Get("/addTextInput")
    @View("tags/textInput")
    Mono<Map<String, Object>> addTextInput(
            @NonNull @Parameter String propertyPath,
            @NonNull @Parameter String fieldLabel,
            @NonNull @Parameter Integer count) {
        String id = propertyPath.replace('[', '_').replace(']', '_').replace('.', '_');
        return Mono.just(Map.of(
                "fieldLabel", fieldLabel + " #" + (count + 1),
                "propertyPath", propertyPath,
                "id", id + count
        ));
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

    @Get("/addTaxRate")
    @View("tags/taxRate")
    Mono<Map<String, Object>> addTaxRate(@NonNull @Parameter Integer count) {
        return Mono.just(Map.of("title", "Tax Rate #" + (count + 1),
                "propertyPath", "taxRates[" + count + "].",
                "id", "estimateCreateTaxRate" + count,
                "count", count + 1,
                "taxRules", EstimateTaxRule.getTaxRateRules()));
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
