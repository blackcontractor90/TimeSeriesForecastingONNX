package service;


/**
 * ${user}blackcontractor@farid
 */
public interface ForecastingModel {

    float[] predict(float[][] windows) throws Exception;

    default float[] predict(float[] input) {
        throw new UnsupportedOperationException(
            "Use predict(float[][]) for sliding-window forecasting."
        );
    }
}
