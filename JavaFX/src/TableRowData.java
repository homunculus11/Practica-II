import java.util.LinkedHashMap;
import java.util.Map;

public class TableRowData {
    private final Map<String, String> values = new LinkedHashMap<>();

    public void put(String column, String value) {
        values.put(column, RomanianText.clean(value));
    }

    public String get(String column) {
        return values.getOrDefault(column, "");
    }
}
