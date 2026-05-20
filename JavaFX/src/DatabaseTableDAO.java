import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.math.BigDecimal;
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
                    row.put(column, formatValue(tableName, column, value));
                }
                rows.add(row);
            }
            return new TableData(columns, rows);
        }
    }

    public int countRows(String tableName) throws SQLException {
        if (!TABLES.containsKey(tableName)) {
            throw new SQLException("Tabel necunoscut: " + tableName);
        }

        String sql = "SELECT COUNT(*) FROM " + quote(tableName);
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private String quote(String identifier) {
        return "[" + identifier.replace("]", "]]") + "]";
    }

    private String formatValue(String tableName, String columnName, Object value) {
        if (value == null) {
            return "";
        }

        if ("Lectii".equals(tableName) && "DataLectie".equals(columnName) && value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime().toLocalDate().toString();
        }

        if ("Lectii".equals(tableName) && "DurataLectie".equals(columnName)) {
            return formatLessonDuration(value);
        }

        return String.valueOf(value);
    }

    private String formatLessonDuration(Object value) {
        double duration = parseDuration(value);

        int hours = (int) duration;
        int minutes = (int) Math.round((duration - hours) * 60);
        if (minutes == 60) {
            hours++;
            minutes = 0;
        }

        return String.format("%02dh %02dmin", hours, minutes);
    }

    private double parseDuration(Object value) {
        try {
            if (value instanceof BigDecimal decimal) {
                return decimal.doubleValue();
            }
            if (value instanceof Number number) {
                return number.doubleValue();
            }
            return Double.parseDouble(String.valueOf(value));
        } catch (RuntimeException ex) {
            return 0;
        }
    }
}
