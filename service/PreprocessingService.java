package service;

import util.StandardScalerUtil;

/**
 * ${user}blackcontractor@farid
 */
public class PreprocessingService {

    private final StandardScalerUtil scaler =
            new StandardScalerUtil();

    public float[] fitAndScale(float[] values) {
        return scaler.fitTransform(values);
    }

    public float[] inverseScale(float[] values) {
        return scaler.inverseTransform(values);
    }
}
