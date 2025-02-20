package depotlifecycle.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "the measurement of the damage dimensions for this line item\n\n`CMT` - \"Centimeters\"\n\n `FOT` - \"Feet\"\n\n `GRM` - \"Grams\"\n\n `INH` - \"Inches\"\n\n `KGM` - \"Kilograms\"\n\n `MTR` - \"Meters\"\n\n `TON` - \"Tons\"\n\n `MTT` - \"Metric Tons\"\n\n `MMT` - \"Millimeters\"\n\n")
public enum UnitOfMeasure {
    CMT("Centimeters"),
    FOT("Feet"),
    GRM("Grams"),
    INH("Inches"),
    KGM("Kilograms"),
    MTR("Meters"),
    TON("Tons"),
    MTT("Metric Tons"),
    MMT("Millimeters");

    public final String description;

    UnitOfMeasure(String description) {
        this.description = description;
    }
}
