import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProfesorDAO {
    private final Database database;

    ProfesorDAO(Database database) {
        this.database = database;
    }

    List<Profesor> findAll() throws SQLException {
        String sql = "SELECT ProfesorID, NumeProfesor, PrenumeProfesor, TipCertificare FROM Profesori ORDER BY ProfesorID";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            return map(statement);
        }
    }

    List<Profesor> search(String text) throws SQLException {
        String sql = "SELECT ProfesorID, NumeProfesor, PrenumeProfesor, TipCertificare FROM Profesori "
                + "WHERE NumeProfesor LIKE ? OR PrenumeProfesor LIKE ? "
                + "OR CONCAT(NumeProfesor, ' ', PrenumeProfesor) LIKE ? "
                + "OR CONCAT(PrenumeProfesor, ' ', NumeProfesor) LIKE ? "
                + "OR TipCertificare LIKE ? ORDER BY ProfesorID";
        String filter = "%" + text + "%";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) {
                statement.setString(i, filter);
            }
            return map(statement);
        }
    }

    List<Profesor> findByIdRange(IdRange range) throws SQLException {
        String sql = "SELECT ProfesorID, NumeProfesor, PrenumeProfesor, TipCertificare FROM Profesori "
                + "WHERE ProfesorID BETWEEN ? AND ? ORDER BY ProfesorID";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, range.from);
            statement.setInt(2, range.to);
            return map(statement);
        }
    }

    Profesor save(Profesor professor) throws SQLException {
        if (professor == null) {
            throw new SQLException("Profesorul nu poate fi null.");
        }
        int id = professor.id > 0 ? professor.id : nextId("Profesori", "ProfesorID");
        NameParts name = NameParts.from(professor.fullName);
        String sql = "INSERT INTO Profesori (ProfesorID, IDNP, NumeProfesor, PrenumeProfesor, DataNasterii, SexProfesor, NrTelefon, Email, TipCertificare, DataAngajarii, InstitutieID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, String.format("8%012d", id));
            statement.setString(3, name.lastName);
            statement.setString(4, name.firstName);
            statement.setDate(5, Date.valueOf(LocalDate.of(1990, 1, 1)));
            statement.setString(6, "M");
            statement.setString(7, "+37300000000");
            statement.setString(8, "profesor" + id + "@intern.local");
            statement.setString(9, ConsoleText.normalizeCertification(professor.certification));
            statement.setDate(10, Date.valueOf(LocalDate.now()));
            statement.setInt(11, 1);
            statement.executeUpdate();
        }
        return new Profesor(id, professor.fullName, professor.certification, professor.rating);
    }

    void update(Profesor professor) throws SQLException {
        if (professor == null || professor.id <= 0) {
            throw new SQLException("Selecteaza un profesor valid pentru modificare.");
        }
        NameParts name = NameParts.from(professor.fullName);
        String sql = "UPDATE Profesori SET NumeProfesor = ?, PrenumeProfesor = ?, TipCertificare = ? WHERE ProfesorID = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name.lastName);
            statement.setString(2, name.firstName);
            statement.setString(3, ConsoleText.normalizeCertification(professor.certification));
            statement.setInt(4, professor.id);
            if (statement.executeUpdate() == 0) {
                throw new SQLException("Profesorul nu a fost gasit pentru modificare.");
            }
        }
    }

    boolean delete(int id) throws SQLException {
        if (id <= 0) {
            throw new SQLException("ID profesor invalid pentru stergere.");
        }
        try (Connection connection = database.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement lectii = connection.prepareStatement("UPDATE Lectii SET ProfesorID = NULL WHERE ProfesorID = ?")) {
                lectii.setInt(1, id);
                lectii.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Profesori WHERE ProfesorID = ?")) {
                statement.setInt(1, id);
                boolean deleted = statement.executeUpdate() > 0;
                connection.commit();
                return deleted;
            } catch (SQLException ex) {
                rollback(connection, ex);
                throw ex;
            }
        }
    }

    private void rollback(Connection connection, SQLException original) {
        try {
            connection.rollback();
        } catch (SQLException rollbackEx) {
            original.addSuppressed(rollbackEx);
        }
    }

    private List<Profesor> map(PreparedStatement statement) throws SQLException {
        List<Profesor> rows = new ArrayList<>();
        try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("ProfesorID");
                rows.add(new Profesor(
                        id,
                        ConsoleText.join(rs.getString("NumeProfesor"), rs.getString("PrenumeProfesor")),
                        rs.getString("TipCertificare") == null ? "Fără grad didactic" : rs.getString("TipCertificare"),
                        3.5 + (Math.abs(id * 37) % 16) / 10.0));
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

