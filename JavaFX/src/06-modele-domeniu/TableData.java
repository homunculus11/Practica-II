import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableData {
    private final List<String> columns;
    private final List<TableRowData> rows;

    public TableData(List<String> columns, List<TableRowData> rows) {
        this.columns = new ArrayList<>(columns);
        this.rows = new ArrayList<>(rows);
    }

    public List<String> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public List<TableRowData> getRows() {
        return Collections.unmodifiableList(rows);
    }
}
