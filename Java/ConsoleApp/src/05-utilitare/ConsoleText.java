import java.util.Arrays;
import java.util.List;

public class ConsoleText {
    public static String join(String first, String second) {
        return ((first == null ? "" : first) + " " + (second == null ? "" : second)).trim();
    }

    public static String normalizeCertification(String value) {
        List<String> allowed = Arrays.asList("Fără grad didactic", "Grad didactic II", "Grad didactic I", "Grad didactic superior");
        return allowed.contains(value) ? value : "Fără grad didactic";
    }

    public static String normalizeCourseType(String value) {
        if ("Offline".equals(value) || "Hibrid".equals(value)) {
            return value;
        }
        return "Online";
    }

    public static String coordinator(String value, int id) {
        return value == null || value.trim().isEmpty() || "Profesor neatribuit".equals(value.trim())
                ? "Coordonator curs " + id
                : value.trim();
    }
}
