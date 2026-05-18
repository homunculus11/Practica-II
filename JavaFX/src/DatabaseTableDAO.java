import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabaseTableDAO {
    private static final Map<String, String> TABLES = new LinkedHashMap<>();

    static {
        TABLES.put("Domenii", "DomeniuID");
        TABLES.put("Raioane", "RaionID");
        TABLES.put("Localitati", "LocalitateID");
        TABLES.put("Institutii", "InstitutieID");
        TABLES.put("Profesori", "ProfesorID");
        TABLES.put("Cursuri", "CursID");
        TABLES.put("Grupe_Cursuri", "GrupaID");
        TABLES.put("Lectii", "LectieID");
        TABLES.put("Studenti", "StudentID");
        TABLES.put("Participari_Cursuri", "ParticipareID");
    }

    private final Database database;

    public DatabaseTableDAO(Database database) {
        this.database = database;
    }

    public List<String> tableNames() {
        return new ArrayList<>(TABLES.keySet());
    }

    public TableData findAll(String tableName) throws SQLException {
        String orderColumn = TABLES.get(tableName);
        if (orderColumn == null) {
            throw new SQLException("Tabel necunoscut: " + tableName);
        }

        String sql = "SELECT * FROM " + quote(tableName) + " ORDER BY " + quote(orderColumn);
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            ResultSetMetaData metadata = rs.getMetaData();
            List<String> columns = new ArrayList<>();
            for (int i = 1; i <= metadata.getColumnCount(); i++) {
                columns.add(metadata.getColumnName(i));
            }

            List<TableRowData> rows = new ArrayList<>();
            while (rs.next()) {
                TableRowData row = new TableRowData();
                for (String column : columns) {
                    Object value = rs.getObject(column);
                    row.put(column, value == null ? "" : String.valueOf(value));
                }
                rows.add(row);
            }
            return new TableData(columns, rows);
        }
    }

    private String quote(String identifier) {
        return "[" + identifier.replace("]", "]]") + "]";
    }
}
