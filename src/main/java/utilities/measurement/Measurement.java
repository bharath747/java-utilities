package com.dynamic.enumFile;

/**
 * Created by sateesh on 22/11/16.
 */
public enum Measurement {
    LENGTH (null, 1, null, null),
        CENTI_METER(LENGTH, 12, 1L, "CM"),
        METER(LENGTH, 13, 100L, "Mtr"),
        KILO_METER(LENGTH, 14, 100000L, "KM"),
    AREA (null, 2, null, null),
        SQUARE_CM(AREA, 22, 1L, "Sq CM"),
        SQUARE_M(AREA, 23, 10000L, "Sq Mtr"),
        HECTARE(AREA, 24, 100000000L, "Htrs"),
        SQUARE_KM(AREA, 25, 10000000000L, "Sq KM"),
    VOLUME (null, 3, null, null),
        CUBIC_CM(VOLUME, 31, 1L, "CC"),
        LITRE(VOLUME, 32, 1000L, "Ltr"),
        CUBIC_M(VOLUME, 33, 1000000L, "CUM"),
    MASS (null, 4, null, null),
        GRAM(MASS, 42, 1L, "Gr"),
        KILO_GRAM(MASS, 43, 1000L, "KG"),
        QUINTAL(MASS, 44, 100000L, "Quintal"),
        METRIC_TON(MASS, 45, 1000000L, "MT"),
    NUMBER (null, 5, null, null),
        ONE(NUMBER, 50, 1L, "No"),
        TEN(NUMBER, 51, 10L, "No"),
        HUNDRED(NUMBER, 52, 100L, "No"),
        THOUSAND(NUMBER, 53, 1000L, "No"),
        TEN_THOUSAND(NUMBER, 54, 10000L, "No"),
        LAKH(NUMBER, 55, 100000L, null),
        TEN_LAKH(NUMBER, 56, 1000000L, "No"),
    TIME (null, 6, null, null),
        SECOND(TIME, 61, 1L, "Sec"),
        MINUTE(TIME, 62, 60L, "Min"),
        HOUR(TIME, 63, 3600L, "Hr"),
        DAY(TIME, 64, 86400L, "Day"),
        WEEK(TIME, 65, 604800L, "Week"),
        MONTH(TIME, 66, 2628000L, "Month"),
        YEAR(TIME, 67, 31540000L, "Year"),
    MAN_TIME (null, 7, null, null),
        MAN_SECOND(MAN_TIME, 71, 1L, "Sec"),
        MAN_MINUTE(MAN_TIME, 72, 60L, "Min"),
        MAN_HOUR(MAN_TIME, 73, 3600L, "Hr"),
        MAN_DAY(MAN_TIME, 74, 86400L, "Day"),
        MAN_WEEK(MAN_TIME, 75, 604800L, "Week"),
        MAN_MONTH(MAN_TIME, 76, 2628000L, "Month"),
        MAN_YEAR(MAN_TIME, 77, 31540000L, "Year"),
    MACHINE_TIME (null, 8, null, null),
        MACHINE_SECOND(MACHINE_TIME, 81, 1L,"Sec"),
        MACHINE_MINUTE(MACHINE_TIME, 82, 60L, "Min"),
        MACHINE_HOUR(MACHINE_TIME, 83, 3600L, "Hr"),
        MACHINE_DAY(MACHINE_TIME, 84, 86400L, "Day"),
        MACHINE_WEEK(MACHINE_TIME, 85, 604800L, "Week"),
        MACHINE_MONTH(MACHINE_TIME, 86, 2628000L, "Month"),
        MACHINE_YEAR(MACHINE_TIME, 87, 31540000L, "Year"),
    SETS (null, 9, null, null),
        NO_OF_SETS(SETS, 90, 1L,"SETS"),
        LUMP_SUM_ONE(SETS, 99, 1L, "LS");

    private Measurement parent;
    private Integer code;
    private Long factor;
    private String unit;
    private static Measurement[] measurements = Measurement.values();

    private Measurement(final Measurement parent, final Integer code, final Long factor, final String unit) {
        this.parent = parent;
        this.code = code;
        this.factor = factor;
        this.unit = unit;
    }

    public Integer value() {
        return this.code;
    }

    public Measurement getParent() {
        return this.parent;
    }

    public Long getFactor() {
        return this.factor;
    }

    public String getUnit() {
        return this.unit;
    }

    public static Measurement getUnit(final Integer value) {
        Measurement measurement = null;
        for(int i = 0; i < measurements.length; i++) {
            if (measurements[i].value().equals(value)) {
                measurement = measurements[i];
                break;
            }
        }
        return measurement;
    }

    @Override
    public String toString() {
        return "Measurement {" +
                "parent = " + parent +
                " , code = " + code +
                " , factor = " + factor +
                " , unit = " + unit +
                '}';
    }
}
