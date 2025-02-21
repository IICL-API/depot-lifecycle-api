package depotlifecycle.domain.repair;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Schema(enumAsRef = true, description = "which amount should taxes apply\n\n`B` - Both Labor Cost & Material Cost\n\n`N` - Neither\n\n`L` - Labor Cost\n\n`M` - Material Cost")
public enum EstimateTaxRule {
    B("Both"),
    N("Neither"),
    L("Labor"),
    M("Material");

    public final String description;

    EstimateTaxRule(String description) {
        this.description = description;
    }

    public static Collection<EstimateTaxRule> getTaxRateRules() {
        List<EstimateTaxRule> rules = new ArrayList<>();
        for (EstimateTaxRule rule : values()) {
            if(rule != EstimateTaxRule.N) {
                rules.add(rule);
            }
        }
        return rules;
    }
}
