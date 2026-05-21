import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TableDAO {
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

    TableDAO(Database database) {
        this.database = database;
    }

    List<String> tableNames() {
        return new ArrayList<>(TABLES.keySet());
    }

    TableData findAll(String tableName) throws SQLException {
        String orderColumn = TABLES.get(tableName);
        if (orderColumn == null) {
            throw new SQLException("Tabel necunoscut: " + tableName);
        }
        String sql = "SELECT * FROM " + quote(tableName) + " ORDER BY " + quote(orderColumn);
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            return query(tableName, statement);
        }
    }

    TableData findByIdRange(String tableName, IdRange range) throws SQLException {
        String orderColumn = TABLES.get(tableName);
        if (orderColumn == null) {
            throw new SQLException("Tabel necunoscut: " + tableName);
        }
        String sql = "SELECT * FROM " + quote(tableName)
                + " WHERE " + quote(orderColumn) + " BETWEEN ? AND ?"
                + " ORDER BY " + quote(orderColumn);
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, range.from);
            statement.setInt(2, range.to);
            return query(tableName, statement);
        }
    }

    private TableData query(String tableName, PreparedStatement statement) throws SQLException {
        try (ResultSet rs = statement.executeQuery()) {
            ResultSetMetaData metadata = rs.getMetaData();
            List<String> columns = new ArrayList<>();
            for (int i = 1; i <= metadata.getColumnCount(); i++) {
                columns.add(metadata.getColumnName(i));
            }

            List<Map<String, String>> rows = new ArrayList<>();
            while (rs.next()) {
                Map<String, String> row = new LinkedHashMap<>();
                for (String column : columns) {
                    row.put(column, formatValue(tableName, column, rs.getObject(column)));
                }
                rows.add(row);
            }
            return new TableData(columns, rows);
        }
    }

    private String quote(String identifier) {
        return "[" + identifier.replace("]", "]]") + "]";
    }

    private String formatValue(String tableName, String columnName, Object value) {
        if (value == null) {
            return "";
        }
        if ("Lectii".equals(tableName) && "DataLectie".equals(columnName) && value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime().toLocalDate().toString();
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
            if (value instanceof BigDecimal) {
                return ((BigDecimal) value).doubleValue();
            }
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            return Double.parseDouble(String.valueOf(value));
        } catch (RuntimeException ex) {
            return 0;
        }
    }
}

