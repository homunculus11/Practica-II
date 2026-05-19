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

public class StudentDAO {
    private final Database database;

    public StudentDAO(Database database) {
        this.database = database;
    }

    public List<Student> findAll() throws SQLException {
        String sql = "SELECT s.StudentID, s.NumeStudent, s.PrenumeStudent, s.DataNasterii, "
                + "COUNT(p.ParticipareID) AS NumarInrolari "
                + "FROM Studenti s "
                + "LEFT JOIN Participari_Cursuri p ON s.StudentID = p.StudentID "
                + "GROUP BY s.StudentID, s.NumeStudent, s.PrenumeStudent, s.DataNasterii "
                + "ORDER BY s.StudentID";
        List<Student> studenti = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                studenti.add(mapStudent(rs));
            }
        }
        return studenti;
    }

    public List<Student> search(String text) throws SQLException {
        String sql = "SELECT s.StudentID, s.NumeStudent, s.PrenumeStudent, s.DataNasterii, "
                + "COUNT(p.ParticipareID) AS NumarInrolari "
                + "FROM Studenti s "
                + "LEFT JOIN Participari_Cursuri p ON s.StudentID = p.StudentID "
                + "WHERE s.NumeStudent LIKE ? OR s.PrenumeStudent LIKE ? "
                + "OR CONCAT(s.NumeStudent, ' ', s.PrenumeStudent) LIKE ? "
                + "OR CONCAT(s.PrenumeStudent, ' ', s.NumeStudent) LIKE ? "
                + "GROUP BY s.StudentID, s.NumeStudent, s.PrenumeStudent, s.DataNasterii "
                + "ORDER BY s.StudentID";
        List<Student> studenti = new ArrayList<>();
        String filter = "%" + text + "%";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, filter);
            statement.setString(2, filter);
            statement.setString(3, filter);
            statement.setString(4, filter);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    studenti.add(mapStudent(rs));
                }
            }
        }
        return studenti;
    }

    public Optional<Student> findById(int id) throws SQLException {
        String sql = "SELECT s.StudentID, s.NumeStudent, s.PrenumeStudent, s.DataNasterii, "
                + "COUNT(p.ParticipareID) AS NumarInrolari "
                + "FROM Studenti s "
                + "LEFT JOIN Participari_Cursuri p ON s.StudentID = p.StudentID "
                + "WHERE s.StudentID = ? "
                + "GROUP BY s.StudentID, s.NumeStudent, s.PrenumeStudent, s.DataNasterii";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapStudent(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Student save(Student student) throws SQLException {
        int id = student.getId() > 0 ? student.getId() : nextId("Studenti", "StudentID");
        String sql = "INSERT INTO Studenti (StudentID, IDNP, NumeStudent, PrenumeStudent, DataNasterii, SexStudent, NrTelefon, LocalitateID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            NameParts name = NameParts.fromFullName(student.getNume());
            statement.setInt(1, id);
            statement.setString(2, generatedIdnp(id));
            statement.setString(3, name.getLastName());
            statement.setString(4, name.getFirstName());
            statement.setDate(5, Date.valueOf(student.getDataInrolare()));
            statement.setString(6, "M");
            statement.setString(7, "+37300000000");
            statement.setInt(8, 1);
            statement.executeUpdate();
        }
        return new Student(id, student.getNume(), student.getEmail(), "secret1", student.getDataInrolare());
    }

    public Student update(Student student) throws SQLException {
        String sql = "UPDATE Studenti SET NumeStudent = ?, PrenumeStudent = ?, DataNasterii = ? WHERE StudentID = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            NameParts name = NameParts.fromFullName(student.getNume());
            statement.setString(1, name.getLastName());
            statement.setString(2, name.getFirstName());
            statement.setDate(3, Date.valueOf(student.getDataInrolare()));
            statement.setInt(4, student.getId());
            statement.executeUpdate();
        }
        return student;
    }

    public boolean delete(int id) throws SQLException {
        try (Connection connection = database.getConnection()) {
            try (PreparedStatement children = connection.prepareStatement("DELETE FROM Participari_Cursuri WHERE StudentID = ?")) {
                children.setInt(1, id);
                children.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Studenti WHERE StudentID = ?")) {
                statement.setInt(1, id);
                return statement.executeUpdate() > 0;
            }
        }
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        int id = rs.getInt("StudentID");
        String nume = RomanianText.clean(join(rs.getString("NumeStudent"), rs.getString("PrenumeStudent")));
        LocalDate date = rs.getDate("DataNasterii") == null ? LocalDate.now() : rs.getDate("DataNasterii").toLocalDate();
        Student student = new Student(id, nume, "student" + id + "@cursuri.local", "secret1", date);
        student.setNumarInrolari(rs.getInt("NumarInrolari"));
        return student;
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
        return String.format("9%012d", id);
    }

    private String join(String first, String second) {
        String value = ((first == null ? "" : first) + " " + (second == null ? "" : second)).trim();
        return value.isEmpty() ? "Student fără nume" : value;
    }
}
