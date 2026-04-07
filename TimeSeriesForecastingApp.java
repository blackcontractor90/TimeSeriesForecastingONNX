

import controller.ForecastController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Entry point of the Time-Series Forecasting application.
 *
 * Responsibilities:
 *  - JavaFX lifecycle management
 *  - UI shell and user interaction
 *  - Delegation of all logic to ForecastController
 *
 * This class intentionally contains NO:
 *  - Data loading logic
 *  - Model inference logic
 *  - Evaluation logic
 *  - Visualization construction logic
 */
/**
 * ${user}blackcontractor@farid
 */
public class TimeSeriesForecastingApp extends Application {

    private ForecastController controller;

    @Override
    public void start(Stage primaryStage) {
        controller = new ForecastController();

        primaryStage.setTitle("Time-Series Forecasting Framework");

        // ---- UI Components ----
        Label titleLabel = new Label("Time-Series Forecasting");
        titleLabel.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitleLabel = new Label(
                "ONNX-based Time-Series Forecast Inference"
        );

        Button runForecastButton = new Button("Run Forecast");
        runForecastButton.setPrefWidth(200);

        Button exitButton = new Button("Exit");
        exitButton.setPrefWidth(200);

        // ---- Button Actions ----
        runForecastButton.setOnAction(event ->
                controller.runForecast(primaryStage)
        );

        exitButton.setOnAction(event ->
                primaryStage.close()
        );

        // ---- Layout ----
        VBox controlBox = new VBox(15);
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setPadding(new Insets(20));
        controlBox.getChildren().addAll(
                titleLabel,
                subtitleLabel,
                runForecastButton,
                exitButton
        );

        BorderPane root = new BorderPane();
        root.setCenter(controlBox);

        Scene scene = new Scene(root, 500, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
