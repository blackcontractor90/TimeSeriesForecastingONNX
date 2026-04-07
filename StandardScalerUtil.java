package util;


/**
 * ${user}blackcontractor@farid
 */
public class StandardScalerUtil {

    private double mean;
    private double std;

    public float[] fitTransform(float[] values) {
        mean = 0;
        for (float v : values) {
            mean += v;
        }
        mean /= values.length;

        std = 0;
        for (float v : values) {
            std += Math.pow(v - mean, 2);
        }
        std = Math.sqrt(std / values.length);

        float[] scaled = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            scaled[i] = (float) ((values[i] - mean) / std);
        }
        return scaled;
    }

    public float[] inverseTransform(float[] values) {
        float[] restored = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            restored[i] = (float) (values[i] * std + mean);
        }
        return restored;
    }
}
