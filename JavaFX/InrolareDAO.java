import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InrolareDAO {
    private final Database database;

    public InrolareDAO(Database database) {
        this.database = database;
    }

    public List<Inrolare> findAll() throws SQLException {
        String sql = "SELECT p.ParticipareID, s.StudentID, s.NumeStudent, s.PrenumeStudent, "
                + "c.CursID, c.DenumireCurs, c.PretCurs, c.TipPredare, c.Coordonator "
                + "FROM Participari_Cursuri p "
                + "INNER JOIN Studenti s ON p.StudentID = s.StudentID "
                + "INNER JOIN Grupe_Cursuri g ON p.GrupaID = g.GrupaID "
                + "INNER JOIN Cursuri c ON g.CursID = c.CursID "
                + "ORDER BY p.ParticipareID";
        List<Inrolare> inrolari = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                inrolari.add(mapInrolare(rs));
            }
        }
        return inrolari;
    }

    public Optional<Inrolare> findById(int id) throws SQLException {
        for (Inrolare inrolare : findAll()) {
            if (inrolare.getId() == id) {
                return Optional.of(inrolare);
            }
        }
        return Optional.empty();
    }

    public Inrolare save(Inrolare inrolare) {
        throw new UnsupportedOperationException("Inrolarile se gestioneaza prin tabela Participari_Cursuri in SSMS.");
    }

    public Inrolare update(Inrolare inrolare) {
        throw new UnsupportedOperationException("Schema curenta nu contine status pentru participari.");
    }

    public boolean delete(int id) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM Participari_Cursuri WHERE ParticipareID = ?")) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private Inrolare mapInrolare(ResultSet rs) throws SQLException {
        int studentId = rs.getInt("StudentID");
        String studentName = RomanianText.clean((safe(rs.getString("NumeStudent")) + " " + safe(rs.getString("PrenumeStudent"))).trim());
        Student student = new Student(studentId, studentName, "student" + studentId + "@cursuri.local", "secret1", LocalDate.now());

        String coordinator = rs.getString("Coordonator") == null ? "Profesor neatribuit" : RomanianText.clean(rs.getString("Coordonator"));
        Profesor profesor = new Profesor(0, coordinator, "coordonator@cursuri.local", "secret1", "Coordonator", 4.5);
        Curs curs = new Curs(
                rs.getInt("CursID"),
                RomanianText.clean(rs.getString("DenumireCurs")),
                rs.getString("TipPredare") == null ? "Online" : RomanianText.clean(rs.getString("TipPredare")),
                rs.getDouble("PretCurs"),
                NivelCurs.MEDIU,
                90,
                profesor);

        return new Inrolare(rs.getInt("ParticipareID"), student, curs, StatusInrolare.ACTIV, LocalDate.now());
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
