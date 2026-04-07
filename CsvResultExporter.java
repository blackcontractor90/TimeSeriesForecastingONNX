package export;

import domain.ForecastResult;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Exports forecasting results and evaluation metrics to CSV.
 *
 * Output format:
 *
 *  - Section 1: Time-series predictions
 *      index,actual,predicted,error
 *
 *  - Section 2: Evaluation metrics
 *      metric,value
 * ${user}blackcontractor@farid
 */
public class CsvResultExporter {

    public void export(
            Path outputPath,
            ForecastResult result,
            Map<String, Double> metrics
    ) throws IOException {

        ensureParentDirectoryExists(outputPath);

        try (BufferedWriter writer =
                     Files.newBufferedWriter(outputPath)) {

            writePredictionSection(writer, result);
            writer.newLine();
            writeMetricsSection(writer, metrics);
        }
    }

    private void writePredictionSection(
            BufferedWriter writer,
            ForecastResult result
    ) throws IOException {

        writer.write("index,actual,predicted,error");
        writer.newLine();

        float[] actual = result.getActual();
        float[] predicted = result.getPredicted();

        int length = Math.min(actual.length, predicted.length);

        for (int i = 0; i < length; i++) {
            double error = actual[i] - predicted[i];

            writer.write(
                    i + "," +
                    actual[i] + "," +
                    predicted[i] + "," +
                    error
            );
            writer.newLine();
        }
    }

    private void writeMetricsSection(
            BufferedWriter writer,
            Map<String, Double> metrics
    ) throws IOException {

        writer.write("metric,value");
        writer.newLine();

        for (Map.Entry<String, Double> entry
                : metrics.entrySet()) {

            writer.write(
                    entry.getKey() + "," +
                    entry.getValue()
            );
            writer.newLine();
        }
    }


    private void ensureParentDirectoryExists(Path outputPath)
            throws IOException {

        Path parent = outputPath.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }
}
