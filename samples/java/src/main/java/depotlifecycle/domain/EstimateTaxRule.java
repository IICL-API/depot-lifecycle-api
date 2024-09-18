package depotlifecycle.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "which amount should taxes apply\n\n`B` - Both Labor Cost & Material Cost\n\n`N` - Neither\n\n`L` - Labor Cost\n\n`M` - Material Cost")
public enum EstimateTaxRule {
    B,
    N,
    L,
    M
}
