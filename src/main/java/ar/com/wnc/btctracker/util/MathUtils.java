package ar.com.wnc.btctracker.util;

public final class MathUtils {

    /**
     * Static Class : No debe ser instanciada
     */
    private MathUtils() {}


    public static Double calculatePercentDiff(Double refValue, Double actualValue) {
        // null-safe + NaN-safe
        if (refValue == null || refValue.isNaN() || actualValue == null || actualValue.isNaN() || refValue.equals(0.0)) {
            return Double.NaN;
        }
        return (refValue - actualValue) / refValue * 100.0;
    }
}
