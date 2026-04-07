package visualization;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.nio.file.Path;
import java.util.Map;

/**
 * Window for visualizing forecast evaluation metrics.
 *
 * This class:
 *  - Displays evaluation metrics as a BarChart
 *  - Accepts a metrics map from EvaluationService
 *  - Is dataset and domain agnostic
 *  - Can save the chart as a PNG image
 */
/**
 * ${user}blackcontractor@farid
 */
public class MetricsGraphWindow {
    private final Map<String, Double> metrics;
    private final String windowTitle;
    private Scene scene;
    
    public MetricsGraphWindow(
            Map<String, Double> metrics,
            String windowTitle
    ) {
        this.metrics = metrics;
        this.windowTitle = windowTitle;
    }
    
    public void show(Stage owner) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.setTitle(windowTitle);
        
        // ---- Axes ----
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Metric");
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Value");
        
        // ---- Chart ----
        BarChart<String, Number> barChart =
                new BarChart<>(xAxis, yAxis);
        barChart.setAnimated(false);
        barChart.setLegendVisible(false);
        
        // ---- Series ----
        XYChart.Series<String, Number> series =
                new XYChart.Series<>();
        
        for (Map.Entry<String, Double> entry
                : metrics.entrySet()) {
            // Skip NaN values
            if (!Double.isNaN(entry.getValue())) {
                series.getData().add(
                        new XYChart.Data<>(
                                entry.getKey(),
                                entry.getValue()
                        )
                );
            }
        }
        
        barChart.getData().add(series);
        
        // ---- Layout ----
        Label headerLabel = new Label(
                "Forecast Evaluation Metrics"
        );
        headerLabel.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setTop(headerLabel);
        BorderPane.setMargin(headerLabel, new Insets(0, 0, 10, 0));
        root.setCenter(barChart);
        
        scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Saves the chart as a high-resolution PNG image file.
     * 
     * @param outputPath Path where the image should be saved
     * @throws Exception if saving fails
     */
    public void saveAsImage(Path outputPath) throws Exception {
        if (scene == null) {
            throw new IllegalStateException(
                    "Cannot save image: window has not been shown yet. Call show() first."
            );
        }
        
        // Set scale factor for higher resolution (2x = 2x width and height)
        double scaleFactor = 2.0;
        
        // Take a high-resolution snapshot
        WritableImage image = new WritableImage(
                (int) (scene.getWidth() * scaleFactor),
                (int) (scene.getHeight() * scaleFactor)
        );
        
        // Create snapshot parameters with scale transform
        javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
        params.setTransform(javafx.scene.transform.Transform.scale(scaleFactor, scaleFactor));
        
        // Take snapshot with parameters
        scene.getRoot().snapshot(params, image);
        
        // Convert to BufferedImage and save as PNG
        File outputFile = outputPath.toFile();
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);
    }
}
