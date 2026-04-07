package service;

/**
 * ${user}blackcontractor@farid
 */
public class SlidingWindowService {

    /**
     * Builds sliding windows for sequence-to-one forecasting.
     *
     * @param series input time-series
     * @param windowSize length of each window
     * @return 2D array [numSamples][windowSize]
     */
    public float[][] buildWindows(
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

    /**
     * Extracts ground truth aligned with windows.
     */
    public float[] buildTargets(
            float[] series,
            int windowSize
    ) {
        int samples = series.length - windowSize;
        float[] targets = new float[samples];

        for (int i = 0; i < samples; i++) {
            targets[i] = series[i + windowSize];
        }
        return targets;
    }
}
