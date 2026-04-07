package service;

import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.NoopTranslator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class OnnxForecastingModel implements ForecastingModel {

    private final Predictor<NDList, NDList> predictor;
    private final NDManager manager;

    private final float[] mean;
    private final float[] scale;

    /**
     * @param modelResourcePath  e.g. "/models/etth1_mlp_sliding_window.onnx" or "models/file.onnx"
     * @param meanResourcePath   e.g. "/models/scaler_mean.csv" or "models/scaler_mean.csv"
     * @param scaleResourcePath  e.g. "/models/scaler_scale.csv" or "models/scaler_scale.csv"
     */
    public OnnxForecastingModel(String modelResourcePath,
                                String meanResourcePath,
                                String scaleResourcePath) throws Exception {

        manager = NDManager.newBaseManager();

        Model model = Model.newInstance("onnx-model");
        model.load(loadResource(modelResourcePath));
        predictor = model.newPredictor(new NoopTranslator());

        this.mean = loadCsv(meanResourcePath);
        this.scale = loadCsv(scaleResourcePath);

        if (mean.length != scale.length) {
            throw new IllegalStateException("Scaler mean/scale length mismatch.");
        }
    }

    /**
     * Predicts one value per sliding window.
     * Input shape: [N, T]
     * Model input shape: [N, T] (NOT [N, T, 1] as originally thought)
     */
    public float[] predict(float[][] windows) throws Exception {

        float[][] scaled = new float[windows.length][windows[0].length];

        for (int i = 0; i < windows.length; i++) {
            for (int j = 0; j < windows[i].length; j++) {
                scaled[i][j] = (windows[i][j] - mean[j]) / scale[j];
            }
        }

        // Model expects 2D input [N, T], so we just pass the scaled data directly
        NDArray input = manager.create(scaled);

        NDList output = predictor.predict(new NDList(input));

        return output.singletonOrThrow().toFloatArray();
    }

    @Override
    public float[] predict(float[] input) {
        throw new UnsupportedOperationException(
                "Use predict(float[][]) for sliding-window forecasting."
        );
    }

    /**
     * Loads a resource from either file system or classpath.
     * First tries to load as a file path, then falls back to classpath resource.
     */
    private InputStream loadResource(String path) throws Exception {
        // Try loading as file path first (for paths like "models/file.onnx")
        if (!path.startsWith("/")) {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath)) {
                return Files.newInputStream(filePath);
            }
            
            // Try with src/resources prefix
            Path srcResourcePath = Paths.get("src", "resources", path);
            if (Files.exists(srcResourcePath)) {
                return Files.newInputStream(srcResourcePath);
            }
        }
        
        // Fall back to classpath resource loading (for paths like "/models/file.onnx")
        InputStream is = getClass().getResourceAsStream(path);
        if (is != null) {
            return is;
        }
        
        // Try without leading slash
        if (path.startsWith("/")) {
            is = getClass().getResourceAsStream(path.substring(1));
            if (is != null) {
                return is;
            }
        }
        
        throw new IllegalStateException(
            "Resource not found: " + path + 
            "\nTried file system paths and classpath resources."
        );
    }

    private float[] loadCsv(String path) throws Exception {
        try (BufferedReader br =
                     new BufferedReader(new InputStreamReader(loadResource(path)))) {

            double[] doubles = br.lines()
                     .flatMap(line -> Arrays.stream(line.split(",")))
                     .map(String::trim)
                     .filter(s -> !s.isEmpty())
                     .mapToDouble(Double::parseDouble)
                     .toArray();

            float[] floats = new float[doubles.length];
            for (int i = 0; i < doubles.length; i++) {
                floats[i] = (float) doubles[i];
            }
            return floats;
        }
    }
}