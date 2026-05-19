import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CursDAO {
    private final Database database;

    CursDAO(Database database) {
        this.database = database;
    }

    List<Curs> findAll() throws SQLException {
        String sql = "SELECT CursID, DenumireCurs, TipPredare, PretCurs, Coordonator FROM Cursuri ORDER BY CursID";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            return map(statement);
        }
    }

    List<Curs> search(String text, String type) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT CursID, DenumireCurs, TipPredare, PretCurs, Coordonator FROM Cursuri WHERE DenumireCurs LIKE ?");
        boolean useType = type != null && !type.trim().isEmpty() && !"Toate".equalsIgnoreCase(type.trim());
        if (useType) {
            sql.append(" AND TipPredare = ?");
        }
        sql.append(" ORDER BY CursID");

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            statement.setString(1, "%" + text + "%");
            if (useType) {
                statement.setString(2, type.trim());
            }
            return map(statement);
        }
    }

    List<Curs> findByIdRange(IdRange range) throws SQLException {
        String sql = "SELECT CursID, DenumireCurs, TipPredare, PretCurs, Coordonator FROM Cursuri "
                + "WHERE CursID BETWEEN ? AND ? ORDER BY CursID";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, range.from);
            statement.setInt(2, range.to);
            return map(statement);
        }
    }

    Curs save(Curs course) throws SQLException {
        int id = course.id > 0 ? course.id : nextId("Cursuri", "CursID");
        String sql = "INSERT INTO Cursuri (CursID, DenumireCurs, LimbaPredare, TipPredare, PretCurs, Coordonator, DomeniuID, InstitutieID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, course.title);
            statement.setString(3, null);
            statement.setString(4, ConsoleText.normalizeCourseType(course.type));
            statement.setDouble(5, Math.max(0, course.price));
            statement.setString(6, ConsoleText.coordinator(course.coordinator, id));
            statement.setInt(7, 1);
            statement.setInt(8, 1);
            statement.executeUpdate();
        }
        return new Curs(id, course.title, course.type, course.price, course.coordinator);
    }

    void update(Curs course) throws SQLException {
        String sql = "UPDATE Cursuri SET DenumireCurs = ?, TipPredare = ?, PretCurs = ?, Coordonator = ? WHERE CursID = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, course.title);
            statement.setString(2, ConsoleText.normalizeCourseType(course.type));
            statement.setDouble(3, Math.max(0, course.price));
            statement.setString(4, ConsoleText.coordinator(course.coordinator, course.id));
            statement.setInt(5, course.id);
            statement.executeUpdate();
        }
    }

    boolean delete(int id) throws SQLException {
        try (Connection connection = database.getConnection()) {
            try (PreparedStatement groups = connection.prepareStatement("SELECT GrupaID FROM Grupe_Cursuri WHERE CursID = ?")) {
                groups.setInt(1, id);
                try (ResultSet rs = groups.executeQuery()) {
                    while (rs.next()) {
                        try (PreparedStatement part = connection.prepareStatement("DELETE FROM Participari_Cursuri WHERE GrupaID = ?")) {
                            part.setInt(1, rs.getInt(1));
                            part.executeUpdate();
                        }
                    }
                }
            }
            try (PreparedStatement lectii = connection.prepareStatement("DELETE l FROM Lectii l INNER JOIN Grupe_Cursuri g ON l.GrupaID = g.GrupaID WHERE g.CursID = ?")) {
                lectii.setInt(1, id);
                lectii.executeUpdate();
            }
            try (PreparedStatement groups = connection.prepareStatement("DELETE FROM Grupe_Cursuri WHERE CursID = ?")) {
                groups.setInt(1, id);
                groups.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Cursuri WHERE CursID = ?")) {
                statement.setInt(1, id);
                return statement.executeUpdate() > 0;
            }
        }
    }

    private List<Curs> map(PreparedStatement statement) throws SQLException {
        List<Curs> rows = new ArrayList<>();
        try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                rows.add(new Curs(
                        rs.getInt("CursID"),
                        rs.getString("DenumireCurs"),
                        rs.getString("TipPredare") == null ? "Online" : rs.getString("TipPredare"),
                        rs.getDouble("PretCurs"),
                        rs.getString("Coordonator") == null ? "Profesor neatribuit" : rs.getString("Coordonator")));
            }
        }
        return rows;
    }

    private int nextId(String table, String column) throws SQLException {
        try (Connection connection = database.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT ISNULL(MAX(" + column + "), 0) + 1 FROM " + table)) {
            rs.next();
            return rs.getInt(1);
        }
    }
}

