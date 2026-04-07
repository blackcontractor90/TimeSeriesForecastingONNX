package visualization;

import domain.ForecastResult;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.nio.file.Path;

/**
 * Window for visualizing actual vs predicted time-series values.
 *
 * This class:
 *  - Displays a JavaFX LineChart
 *  - Is domain-agnostic (no rainfall semantics)
 *  - Accepts ForecastResult as input
 *  - Can save the chart as a PNG image
 */
/**
 * ${user}blackcontractor@farid
 */
public class PredictionGraphWindow {
    private final ForecastResult result;
    private final String windowTitle;
    private Scene scene;
    
    public PredictionGraphWindow(ForecastResult result2, String windowTitle2, ForecastResult result) {
        this.result = result;
        this.windowTitle = windowTitle2;
    }
    
    public void show(Stage owner) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.setTitle(windowTitle);
        
        // ---- Axes ----
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time Step");
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Value");
        
        // ---- Chart ----
        LineChart<Number, Number> lineChart =
                new LineChart<>(xAxis, yAxis);
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);
        
        // ---- Series ----
        XYChart.Series<Number, Number> actualSeries =
                new XYChart.Series<>();
        actualSeries.setName("Actual");
        
        XYChart.Series<Number, Number> predictedSeries =
                new XYChart.Series<>();
        predictedSeries.setName("Predicted");
        
        float[] actual = result.getActual();
        float[] predicted = result.getPredicted();
        
        int length = Math.min(actual.length, predicted.length);
        
        for (int i = 0; i < length; i++) {
            actualSeries.getData().add(
                    new XYChart.Data<>(i, actual[i])
            );
            predictedSeries.getData().add(
                    new XYChart.Data<>(i, predicted[i])
            );
        }
        
        lineChart.getData().addAll(
                actualSeries,
                predictedSeries
        );
        
        // ---- Layout ----
        Label headerLabel = new Label(
                "Actual vs Predicted Time-Series"
        );
        headerLabel.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setTop(headerLabel);
        BorderPane.setMargin(headerLabel, new Insets(0, 0, 10, 0));
        root.setCenter(lineChart);
        
        scene = new Scene(root, 900, 500);
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