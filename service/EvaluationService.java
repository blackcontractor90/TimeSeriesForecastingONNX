package service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ${user}blackcontractor@farid
 */
public class EvaluationService {

    private static final double EPSILON = 1e-10; // Small value to prevent division by zero

    public Map<String, Double> evaluate(
            float[] actual,
            float[] predicted
    ) {
        Map<String, Double> metrics = new LinkedHashMap<>();

        metrics.put("MAE", mae(actual, predicted));
        metrics.put("RMSE", rmse(actual, predicted));
        metrics.put("MAPE", mape(actual, predicted));
        metrics.put("SMAPE", smape(actual, predicted)); // Better alternative for data with zeros
        metrics.put("R²", r2Score(actual, predicted));

        return metrics;
    }

    /**
     * Mean Absolute Error
     */
    private double mae(float[] a, float[] p) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.abs(a[i] - p[i]);
        }
        return sum / a.length;
    }

    /**
     * Root Mean Square Error
     */
    private double rmse(float[] a, float[] p) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - p[i], 2);
        }
        return Math.sqrt(sum / a.length);
    }

    /**
     * Mean Absolute Percentage Error
     * Skips values where actual is near zero to avoid division by zero
     */
    private double mape(float[] a, float[] p) {
        double sum = 0;
        int count = 0;
        
        for (int i = 0; i < a.length; i++) {
            // Skip if actual value is too close to zero
            if (Math.abs(a[i]) > EPSILON) {
                sum += Math.abs((a[i] - p[i]) / a[i]);
                count++;
            }
        }
        
        // If all values were zero, return NaN
        if (count == 0) {
            return Double.NaN;
        }
        
        return (sum / count) * 100.0;
    }

    /**
     * Symmetric Mean Absolute Percentage Error
     * Better for data with zeros - uses sum of absolute values in denominator
     */
    private double smape(float[] a, float[] p) {
        double sum = 0;
        int count = 0;
        
        for (int i = 0; i < a.length; i++) {
            double denominator = (Math.abs(a[i]) + Math.abs(p[i]));
            
            // Skip if both actual and predicted are zero
            if (denominator > EPSILON) {
                sum += Math.abs(a[i] - p[i]) / denominator;
                count++;
            }
        }
        
        if (count == 0) {
            return Double.NaN;
        }
        
        return (sum / count) * 100.0;
    }

    /**
     * R-squared (coefficient of determination)
     * Indicates how well predictions fit the actual data
     * 1.0 = perfect fit, 0.0 = no better than mean, negative = worse than mean
     */
    private double r2Score(float[] a, float[] p) {
        // Calculate mean of actual values
        double mean = 0;
        for (float value : a) {
            mean += value;
        }
        mean /= a.length;
        
        // Calculate total sum of squares and residual sum of squares
        double ssTot = 0; // Total sum of squares
        double ssRes = 0; // Residual sum of squares
        
        for (int i = 0; i < a.length; i++) {
            ssTot += Math.pow(a[i] - mean, 2);
            ssRes += Math.pow(a[i] - p[i], 2);
        }
        
        // Avoid division by zero
        if (ssTot < EPSILON) {
            return Double.NaN;
        }
        
        return 1.0 - (ssRes / ssTot);
    }
}
