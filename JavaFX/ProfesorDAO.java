import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfesorDAO {
    private final Database database;

    public ProfesorDAO(Database database) {
        this.database = database;
    }

    public List<Profesor> findAll() throws SQLException {
        String sql = "SELECT ProfesorID, NumeProfesor, PrenumeProfesor, Email, TipCertificare FROM Profesori ORDER BY ProfesorID";
        List<Profesor> profesori = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                profesori.add(mapProfesor(rs));
            }
        }
        return profesori;
    }

    public List<Profesor> search(String text) throws SQLException {
        String sql = "SELECT ProfesorID, NumeProfesor, PrenumeProfesor, Email, TipCertificare FROM Profesori "
                + "WHERE NumeProfesor LIKE ? OR PrenumeProfesor LIKE ? OR TipCertificare LIKE ? ORDER BY ProfesorID";
        List<Profesor> profesori = new ArrayList<>();
        String filter = "%" + text + "%";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, filter);
            statement.setString(2, filter);
            statement.setString(3, filter);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    profesori.add(mapProfesor(rs));
                }
            }
        }
        return profesori;
    }

    public Optional<Profesor> findById(int id) throws SQLException {
        String sql = "SELECT ProfesorID, NumeProfesor, PrenumeProfesor, Email, TipCertificare FROM Profesori WHERE ProfesorID = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapProfesor(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Profesor save(Profesor profesor) throws SQLException {
        int id = profesor.getId() > 0 ? profesor.getId() : nextId("Profesori", "ProfesorID");
        String sql = "INSERT INTO Profesori (ProfesorID, IDNP, NumeProfesor, PrenumeProfesor, DataNasterii, SexProfesor, NrTelefon, Email, DataAngajarii, InstitutieID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, generatedIdnp(id));
            statement.setString(3, profesor.getNume());
            statement.setString(4, "");
            statement.setDate(5, Date.valueOf(LocalDate.of(1990, 1, 1)));
            statement.setString(6, "M");
            statement.setString(7, "+37300000000");
            statement.setString(8, profesor.getEmail());
            statement.setDate(9, Date.valueOf(LocalDate.now()));
            statement.setInt(10, 1);
            statement.executeUpdate();
        }
        return new Profesor(id, profesor.getNume(), profesor.getEmail(), "secret1", profesor.getSpecializare(), profesor.getRating());
    }

    public Profesor update(Profesor profesor) throws SQLException {
        String sql = "UPDATE Profesori SET NumeProfesor = ?, PrenumeProfesor = ?, Email = ?, TipCertificare = ? WHERE ProfesorID = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, profesor.getNume());
            statement.setString(2, "");
            statement.setString(3, profesor.getEmail());
            statement.setString(4, null);
            statement.setInt(5, profesor.getId());
            statement.executeUpdate();
        }
        return profesor;
    }

    public boolean delete(int id) throws SQLException {
        try (Connection connection = database.getConnection()) {
            try (PreparedStatement lectii = connection.prepareStatement("UPDATE Lectii SET ProfesorID = NULL WHERE ProfesorID = ?")) {
                lectii.setInt(1, id);
                lectii.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Profesori WHERE ProfesorID = ?")) {
                statement.setInt(1, id);
                return statement.executeUpdate() > 0;
            }
        }
    }

    private Profesor mapProfesor(ResultSet rs) throws SQLException {
        int id = rs.getInt("ProfesorID");
        String nume = RomanianText.clean(join(rs.getString("NumeProfesor"), rs.getString("PrenumeProfesor")));
        String email = rs.getString("Email") == null ? "profesor" + id + "@cursuri.local" : rs.getString("Email");
        String specializare = rs.getString("TipCertificare") == null ? "Cursuri online" : RomanianText.clean(rs.getString("TipCertificare"));
        return new Profesor(id, nume, email, "secret1", specializare, 4.5);
    }

    private int nextId(String table, String column) throws SQLException {
        String sql = "SELECT ISNULL(MAX(" + column + "), 0) + 1 FROM " + table;
        try (Connection connection = database.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private String generatedIdnp(int id) {
        return String.format("8%012d", id);
    }

    private String join(String first, String second) {
        String value = ((first == null ? "" : first) + " " + (second == null ? "" : second)).trim();
        return value.isEmpty() ? "Profesor fara nume" : value;
    }
}
