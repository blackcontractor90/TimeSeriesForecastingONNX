package domain;


/**
 * ${user}blackcontractor@farid
 */
public class ForecastResult {

    private final float[] actual;
    private final float[] predicted;

    public ForecastResult(
            float[] actual,
            float[] predicted
    ) {
        this.actual = actual;
        this.predicted = predicted;
    }

    public float[] getActual() {
        return actual;
    }

    public float[] getPredicted() {
        return predicted;
    }
}
