import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private final Database database;

    StudentDAO(Database database) {
        this.database = database;
    }

    List<Student> findAll() throws SQLException {
        String sql = "SELECT s.StudentID, s.NumeStudent, s.PrenumeStudent, s.DataNasterii, "
                + "COUNT(p.ParticipareID) AS NumarInrolari "
                + "FROM Studenti s LEFT JOIN Participari_Cursuri p ON s.StudentID = p.StudentID "
                + "GROUP BY s.StudentID, s.NumeStudent, s.PrenumeStudent, s.DataNasterii ORDER BY s.StudentID";
        return query(sql);
    }

    List<Student> search(String text) throws SQLException {
        String sql = "SELECT s.StudentID, s.NumeStudent, s.PrenumeStudent, s.DataNasterii, "
                + "COUNT(p.ParticipareID) AS NumarInrolari "
                + "FROM Studenti s LEFT JOIN Participari_Cursuri p ON s.StudentID = p.StudentID "
                + "WHERE s.NumeStudent LIKE ? OR s.PrenumeStudent LIKE ? "
                + "OR CONCAT(s.NumeStudent, ' ', s.PrenumeStudent) LIKE ? "
                + "OR CONCAT(s.PrenumeStudent, ' ', s.NumeStudent) LIKE ? "
                + "GROUP BY s.StudentID, s.NumeStudent, s.PrenumeStudent, s.DataNasterii ORDER BY s.StudentID";
        String filter = "%" + text + "%";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++) {
                statement.setString(i, filter);
            }
            return map(statement);
        }
    }

    List<Student> findByIdRange(IdRange range) throws SQLException {
        String sql = "SELECT s.StudentID, s.NumeStudent, s.PrenumeStudent, s.DataNasterii, "
                + "COUNT(p.ParticipareID) AS NumarInrolari "
                + "FROM Studenti s LEFT JOIN Participari_Cursuri p ON s.StudentID = p.StudentID "
                + "WHERE s.StudentID BETWEEN ? AND ? "
                + "GROUP BY s.StudentID, s.NumeStudent, s.PrenumeStudent, s.DataNasterii ORDER BY s.StudentID";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, range.from);
            statement.setInt(2, range.to);
            return map(statement);
        }
    }

    Student save(Student student) throws SQLException {
        int id = student.id > 0 ? student.id : nextId("Studenti", "StudentID");
        NameParts name = NameParts.from(student.fullName);
        String sql = "INSERT INTO Studenti (StudentID, IDNP, NumeStudent, PrenumeStudent, DataNasterii, SexStudent, NrTelefon, LocalitateID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, String.format("9%012d", id));
            statement.setString(3, name.lastName);
            statement.setString(4, name.firstName);
            statement.setDate(5, Date.valueOf(student.birthDate));
            statement.setString(6, "M");
            statement.setString(7, "+37300000000");
            statement.setInt(8, 1);
            statement.executeUpdate();
        }
        return new Student(id, student.fullName, student.birthDate, student.enrollments);
    }

    void update(Student student) throws SQLException {
        NameParts name = NameParts.from(student.fullName);
        String sql = "UPDATE Studenti SET NumeStudent = ?, PrenumeStudent = ?, DataNasterii = ? WHERE StudentID = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name.lastName);
            statement.setString(2, name.firstName);
            statement.setDate(3, Date.valueOf(student.birthDate));
            statement.setInt(4, student.id);
            statement.executeUpdate();
        }
    }

    boolean delete(int id) throws SQLException {
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

    private List<Student> query(String sql) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            return map(statement);
        }
    }

    private List<Student> map(PreparedStatement statement) throws SQLException {
        List<Student> rows = new ArrayList<>();
        try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                LocalDate date = rs.getDate("DataNasterii") == null ? LocalDate.now() : rs.getDate("DataNasterii").toLocalDate();
                rows.add(new Student(
                        rs.getInt("StudentID"),
                        ConsoleText.join(rs.getString("NumeStudent"), rs.getString("PrenumeStudent")),
                        date,
                        rs.getInt("NumarInrolari")));
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

