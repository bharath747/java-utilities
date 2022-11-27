package com.dynamic.enumFile;

import java.text.DecimalFormat;

/**
 * Created by karthik on 22/3/17.
 */
public class MeasurementUtil {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    public static Long getPersistentValue(final Double value, final Integer units) {
        final Measurement measurement = fetchUnitObject(units);
        Double constant = value < 0 ? -0.0000000000001 : 0.0000000000001;
        // TODO CHECK by Sateesh
        final Long persistedValue = (long) ((value + constant) * measurement.getFactor());
        return persistedValue;
    }

    public static Double getDisplayValue(final Long value, final Integer units) {
        final Measurement measurement = fetchUnitObject(units);
        final Double displayValue = Double.valueOf(value)/ measurement.getFactor();
        return displayValue;
    }

    public static Double getPersistenceValue(final Double value, final Measurement measurement) {
        final Double persistedValue = value * measurement.getFactor();
        return persistedValue;
    }

    private static Measurement fetchUnitObject(final Integer units) {
        final Measurement measurement = Measurement.getUnit(units);
        if (measurement == null || measurement.getFactor() == null) {
            throw new RuntimeException(String.format("No utilities.measurement is found in the system! utilities.measurement value is - %d", units));
        }
        return measurement;
    }
    public static String formatInCr(final Double value) {
        String formatInCr = null;
        if (value != null) {
            Double valueInCr = value / 10000000;
            formatInCr = DECIMAL_FORMAT.format(valueInCr);
        }
        return formatInCr;
    }
    public static double metersToKM(final double meters) {
        double kms = 0;
        if (meters != 0) {
            kms = meters / 1000;
        }
        return kms;
    }
    public static final Double formatDistance(Double distance) {
        Double distanceVal = 0.0d;
        String distanceString = DECIMAL_FORMAT.format(distance);
        try {
            distance = (Double) DECIMAL_FORMAT.parse(distanceString);
        } catch (Exception e) {
            distance = Math.floor(distance);
        }
        if(distance != null && distance !=0d){
            distanceVal =distance;
        }
        return distanceVal;
    }
    public static long kmToMeters(final double kms) {
        long maters = 0;
        if (kms != 0) {
            maters =(long) (kms * 1000);
        }
        return maters;
    }
    public static Double convertKmToMeters(final Double kms) {
        Double maters = 0d;
        if (kms != 0) {
            maters =kms * 1000;
        }
        return maters;
    }

    public static Double convertMetersToKm(final Integer mtrs) {
        Double kms = 0d;
        if (mtrs != 0) {
            kms = mtrs / 1000d;
        }
        return kms;
    }

    public static final Double formatTwoDecimal(Double value) {
        String valueString = DECIMAL_FORMAT.format(value);
        try {
            value = (Double) DECIMAL_FORMAT.parse(valueString);
        } catch (Exception e) {
            value = Math.floor(value);
        }
        return value;
    }
}
