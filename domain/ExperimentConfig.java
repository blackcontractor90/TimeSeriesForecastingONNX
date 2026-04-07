package domain;

import java.nio.file.Path;

/**
 * ${user}blackcontractor@farid
 */
public class ExperimentConfig {

    private final Path datasetPath;
    private final int valueColumnIndex;
    private final Path modelPath;
    private final Path exportPath;

    public ExperimentConfig(
            Path datasetPath,
            int valueColumnIndex,
            Path modelPath,
            Path exportPath
    ) {
        this.datasetPath = datasetPath;
        this.valueColumnIndex = valueColumnIndex;
        this.modelPath = modelPath;
        this.exportPath = exportPath;
    }

    public Path getDatasetPath() {
        return datasetPath;
    }

    public int getValueColumnIndex() {
        return valueColumnIndex;
    }

    public Path getModelPath() {
        return modelPath;
    }

    public Path getExportPath() {
        return exportPath;
    }
}
