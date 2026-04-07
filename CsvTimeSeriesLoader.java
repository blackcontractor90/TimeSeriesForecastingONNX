package data;

import domain.TimeSeries;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * ${user}blackcontractor@farid
 */
public class CsvTimeSeriesLoader {
    
    /**
     * Loads time series data from CSV file, handling missing values.
     * Missing values (NA, empty, or invalid) are skipped.
     * 
     * @param csvPath Path to CSV file
     * @param valueColumnIndex Column index of target variable
     * @return TimeSeries object with valid values only
     * @throws Exception if file cannot be read or no valid values found
     */
    public TimeSeries load(Path csvPath, int valueColumnIndex)
            throws Exception {
        List<Float> values = new ArrayList<>();
        int totalRows = 0;
        int skippedRows = 0;
        
        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            String line;
            boolean headerSkipped = false;
            
            while ((line = br.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                
                totalRows++;
                String[] tokens = line.split(",");
                
                // Check if column exists
                if (valueColumnIndex >= tokens.length) {
                    skippedRows++;
                    continue;
                }
                
                String valueStr = tokens[valueColumnIndex].trim();
                
                // Skip missing or invalid values
                if (valueStr.isEmpty() || 
                    valueStr.equalsIgnoreCase("NA") || 
                    valueStr.equalsIgnoreCase("NaN") ||
                    valueStr.equals("?")) {
                    skippedRows++;
                    continue;
                }
                
                try {
                    float value = Float.parseFloat(valueStr);
                    
                    // Optional: Skip infinite or NaN values
                    if (Float.isFinite(value)) {
                        values.add(value);
                    } else {
                        skippedRows++;
                    }
                } catch (NumberFormatException e) {
                    // Skip unparseable values
                    skippedRows++;
                }
            }
        }
        
        // Report statistics
        System.out.println("CSV Loading Statistics:");
        System.out.println("  Total rows (excluding header): " + totalRows);
        System.out.println("  Valid values loaded: " + values.size());
        System.out.println("  Skipped rows (missing/invalid): " + skippedRows);
        
        if (values.isEmpty()) {
            throw new IllegalStateException(
                    "No valid values found in CSV file at column index " + valueColumnIndex
            );
        }
        
        // Convert to float array
        float[] series = new float[values.size()];
        for (int i = 0; i < values.size(); i++) {
            series[i] = values.get(i);
        }
        
        return new TimeSeries(series);
    }
}