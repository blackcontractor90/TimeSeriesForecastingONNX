package metrics;

/**
 * ${user}blackcontractor@farid
 */
public class ForecastMetrics {

    private final double mae;
    private final double rmse;

    public ForecastMetrics(double mae, double rmse) {
        this.mae = mae;
        this.rmse = rmse;
    }

    public double getMae() {
        return mae;
    }

    public double getRmse() {
        return rmse;
    }
}
