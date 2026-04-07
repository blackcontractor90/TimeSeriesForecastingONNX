package visualization;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 * ${user}blackcontractor@farid
 */
public class TimeSeriesChart {

    @SuppressWarnings("unchecked")
	public LineChart<Number, Number> build(
            float[] actual,
            float[] predicted,
            String yLabel
    ) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yLabel);

        LineChart<Number, Number> chart =
                new LineChart<>(xAxis, yAxis);

        XYChart.Series<Number, Number> actualSeries =
                new XYChart.Series<>();
        actualSeries.setName("Actual");

        XYChart.Series<Number, Number> predictedSeries =
                new XYChart.Series<>();
        predictedSeries.setName("Predicted");

        for (int i = 0; i < actual.length; i++) {
            actualSeries.getData().add(
                    new XYChart.Data<>(i, actual[i])
            );
            predictedSeries.getData().add(
                    new XYChart.Data<>(i, predicted[i])
            );
        }

        chart.getData().addAll(
                actualSeries,
                predictedSeries
        );

        return chart;
    }
}
