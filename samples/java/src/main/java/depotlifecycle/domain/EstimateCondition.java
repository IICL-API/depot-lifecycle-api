package depotlifecycle.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "an indicator on the status of the estimate and where it is in the revision process\n\n`D` - Damaged, Initial Estimate\n\n`E` - Customer Surveyed, No Approval\n\n`F` - Customer Approved Estimate, No Survey\n\n`G` - Customer Approved Estimate, Surveyed\n\n`L` - Owner Surveyed, No Approval")
public enum EstimateCondition {
    D,
    E,
    F,
    G,
    L
}
