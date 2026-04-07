package util;



/**
 * ${user}blackcontractor@farid
 */
public class FeatureVectorUtil {

    public static float[][] buildSlidingWindows(
            float[] series,
            int windowSize
    ) {
        int samples = series.length - windowSize;
        float[][] windows = new float[samples][windowSize];

        for (int i = 0; i < samples; i++) {
            System.arraycopy(
                    series,
                    i,
                    windows[i],
                    0,
                    windowSize
            );
        }
        return windows;
    }
}
