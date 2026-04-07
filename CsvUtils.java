package util;



/**
 * ${user}blackcontractor@farid
 */
public class CsvUtils {

    public static String[] split(String line) {
        return line.trim().split("\\s*,\\s*");
    }

    public static boolean isEmpty(String line) {
        return line == null || line.trim().isEmpty();
    }
}
