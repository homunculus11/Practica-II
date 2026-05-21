import java.util.List;
import java.util.Map;

public class TableData {
    final List<String> columns;
    final List<Map<String, String>> rows;

    TableData(List<String> columns, List<Map<String, String>> rows) {
        this.columns = columns;
        this.rows = rows;
    }
}

