import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InrolareDAO {
    private final Database database;

    InrolareDAO(Database database) {
        this.database = database;
    }

    List<Inrolare> findAll() throws SQLException {
        String sql = "SELECT p.ParticipareID, s.StudentID, s.NumeStudent, s.PrenumeStudent, "
                + "c.CursID, c.DenumireCurs, c.PretCurs, c.TipPredare, c.Coordonator "
                + "FROM Participari_Cursuri p "
                + "INNER JOIN Studenti s ON p.StudentID = s.StudentID "
                + "INNER JOIN Grupe_Cursuri g ON p.GrupaID = g.GrupaID "
                + "INNER JOIN Cursuri c ON g.CursID = c.CursID ORDER BY p.ParticipareID";
        List<Inrolare> rows = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Student student = new Student(
                        rs.getInt("StudentID"),
                        ConsoleText.join(rs.getString("NumeStudent"), rs.getString("PrenumeStudent")),
                        LocalDate.now(),
                        0);
                Curs course = new Curs(
                        rs.getInt("CursID"),
                        rs.getString("DenumireCurs"),
                        rs.getString("TipPredare") == null ? "Online" : rs.getString("TipPredare"),
                        rs.getDouble("PretCurs"),
                        rs.getString("Coordonator") == null ? "Profesor neatribuit" : rs.getString("Coordonator"));
                rows.add(new Inrolare(rs.getInt("ParticipareID"), student, course));
            }
        }
        return rows;
    }
}

