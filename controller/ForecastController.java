package controller;

import data.CsvTimeSeriesLoader;
import domain.ForecastResult;
import domain.TimeSeries;
import export.CsvResultExporter;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import service.*;
import visualization.MetricsGraphWindow;
import visualization.PredictionGraphWindow;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ForecastController {

    private static final int WINDOW_SIZE = 24;
    
    // Timestamp for this run (generated once per execution)
    private static final String TIMESTAMP = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

    // Dataset configurations
    private enum Dataset {
        ORIGINAL("src/data/dataset.csv", 2, "Original Dataset"),
        ETTH1("src/data/ETTh1.csv", 7, "ETTh1 Benchmark");
    	/*ETTM1("src/data/ETTm1.csv", 7, "ETTm1 Benchmark");*/
    	
        final Path path;
        final int columnIndex;
        final String displayName;

        Dataset(String pathStr, int columnIndex, String displayName) {
            this.path = Paths.get(pathStr);
            this.columnIndex = columnIndex;
            this.displayName = displayName;
        }
    }

    // Select which dataset to use (change this to switch datasets)
    //private static final Dataset ACTIVE_DATASET = Dataset.ETTH1;
    private static final Dataset ACTIVE_DATASET = Dataset.ETTH1;
    
    // Output path with timestamp and dataset name
    private static final Path OUTPUT = Paths.get("output", 
            String.format("forecast_%s_%s.csv", 
                    ACTIVE_DATASET.name().toLowerCase(), 
                    TIMESTAMP));
    
    // Output directory for graphs
    private static final Path GRAPH_OUTPUT_DIR = Paths.get("output", "graphs");

    // Model file paths (relative to project root or src/resources)
    private static final String MODEL_PATH = "src/resources/models/etth1_mlp_sliding_window.onnx";
    private static final String SCALER_MEAN_PATH = "src/resources/models/scaler_mean.csv";
    private static final String SCALER_SCALE_PATH = "src/resources/models/scaler_scale.csv";

    public void runForecast(Stage owner) {
        try {
            validateDatasetExists();
            
            System.out.println("Running forecast on: " + ACTIVE_DATASET.displayName);
            System.out.println("Timestamp: " + TIMESTAMP);
            System.out.println("Column index: " + ACTIVE_DATASET.columnIndex);
            
            // 1. Load data (OT column)
            TimeSeries series = loadTimeSeries();
            System.out.println("Loaded " + series.getValues().length + " data points");

            // 2. Build sliding windows (RAW values)
            SlidingWindowService windowService = new SlidingWindowService();
            float[][] windows = windowService.buildWindows(
                    series.getValues(), 
                    WINDOW_SIZE
            );
            float[] actual = windowService.buildTargets(
                    series.getValues(), 
                    WINDOW_SIZE
            );
            System.out.println("Created " + windows.length + " sliding windows");

            // 3. Model inference (scaling happens inside the model)
            ForecastingModel model = loadModel();
            float[] predictions = model.predict(windows);
            System.out.println("Generated " + predictions.length + " predictions");

            // 4. Create forecast result
            ForecastResult result = new ForecastResult(actual, predictions);

            // 5. Evaluate predictions
            Map<String, Double> metrics = evaluatePredictions(actual, predictions);
            
            // Print metrics to console
            System.out.println("\n=== Evaluation Metrics ===");
            metrics.forEach((key, value) -> 
                System.out.printf("%s: %.4f%n", key, value)
            );
            System.out.println("==========================\n");

            // 6. Visualize results (and save graphs)
            displayVisualizations(owner, result, metrics);

            // 7. Export results
            exportResults(result, metrics);

            // 8. Show success message
            showSuccessMessage(owner, metrics);

        } catch (Exception ex) {
            handleError(owner, ex);
        }
    }

    private void validateDatasetExists() throws Exception {
        if (!Files.exists(ACTIVE_DATASET.path)) {
            throw new IllegalStateException(
                    "Dataset not found: " + ACTIVE_DATASET.displayName + 
                    "\nPath: " + ACTIVE_DATASET.path.toAbsolutePath() + 
                    "\nPlease ensure the file exists at this location.\n\n" +
                    "To download ETTh1.csv:\n" +
                    "https://raw.githubusercontent.com/zhouhaoyi/ETDataset/main/ETT-small/ETTh1.csv"
            );
        }
    }

    private TimeSeries loadTimeSeries() throws Exception {
        return new CsvTimeSeriesLoader().load(ACTIVE_DATASET.path, ACTIVE_DATASET.columnIndex);
    }

    private ForecastingModel loadModel() throws Exception {
        return new OnnxForecastingModel(
                MODEL_PATH,
                SCALER_MEAN_PATH,
                SCALER_SCALE_PATH
        );
    }

    private Map<String, Double> evaluatePredictions(float[] actual, float[] predictions) {
        EvaluationService evaluator = new EvaluationService();
        return evaluator.evaluate(actual, predictions);
    }

    private void displayVisualizations(Stage owner, ForecastResult result, 
                                      Map<String, Double> metrics) throws Exception {
        // Ensure graph output directory exists
        Files.createDirectories(GRAPH_OUTPUT_DIR);
        
        // Generate filenames with timestamp
        String predictionGraphFile = String.format("prediction_%s_%s.png", 
                ACTIVE_DATASET.name().toLowerCase(), 
                TIMESTAMP);
        String metricsGraphFile = String.format("metrics_%s_%s.png", 
                ACTIVE_DATASET.name().toLowerCase(), 
                TIMESTAMP);
        
        Path predictionGraphPath = GRAPH_OUTPUT_DIR.resolve(predictionGraphFile);
        Path metricsGraphPath = GRAPH_OUTPUT_DIR.resolve(metricsGraphFile);
        
        // Show and save prediction graph
        PredictionGraphWindow predWindow = new PredictionGraphWindow(
                result,
                "Forecast: " + ACTIVE_DATASET.displayName,
                result
        );
        predWindow.show(owner);
        predWindow.saveAsImage(predictionGraphPath);
        System.out.println("Saved prediction graph: " + predictionGraphPath);

        // Show and save metrics graph
        MetricsGraphWindow metricsWindow = new MetricsGraphWindow(
                metrics,
                "Metrics: " + ACTIVE_DATASET.displayName
        );
        metricsWindow.show(owner);
        metricsWindow.saveAsImage(metricsGraphPath);
        System.out.println("Saved metrics graph: " + metricsGraphPath);
    }

    private void exportResults(ForecastResult result, Map<String, Double> metrics) 
            throws Exception {
        // Ensure output directory exists
        Files.createDirectories(OUTPUT.getParent());
        
        new CsvResultExporter().export(OUTPUT, result, metrics);
        System.out.println("Saved results CSV: " + OUTPUT);
    }

    private void showSuccessMessage(Stage owner, Map<String, Double> metrics) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(owner);
        alert.setTitle("Forecast Complete");
        alert.setHeaderText("Forecasting completed successfully!");
        
        StringBuilder content = new StringBuilder();
        content.append("Dataset: ").append(ACTIVE_DATASET.displayName).append("\n");
        content.append("Timestamp: ").append(TIMESTAMP).append("\n\n");
        content.append("Files saved:\n");
        content.append("  CSV: ").append(OUTPUT.getFileName()).append("\n");
        content.append("  Graphs: output/graphs/\n\n");
        content.append("Metrics:\n");
        metrics.forEach((key, value) -> {
            if (Double.isNaN(value)) {
                content.append(String.format("  %s: N/A\n", key));
            } else {
                content.append(String.format("  %s: %.4f\n", key, value));
            }
        });
        
        alert.setContentText(content.toString());
        alert.showAndWait();
    }

    private void handleError(Stage owner, Exception ex) {
        ex.printStackTrace();
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(owner);
        alert.setTitle("Forecast Error");
        alert.setHeaderText("An error occurred during forecasting");
        
        String errorMsg = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
        alert.setContentText(errorMsg);
        
        alert.showAndWait();
    }
}