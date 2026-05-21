import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CursDAO {
    private final Database database;

    public CursDAO(Database database) {
        this.database = database;
    }

    public List<Curs> findAll() throws SQLException {
        String sql = "SELECT CursID, DenumireCurs, PretCurs, TipPredare, Coordonator FROM Cursuri ORDER BY CursID";
        List<Curs> cursuri = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                cursuri.add(mapCurs(rs));
            }
        }
        return cursuri;
    }

    public List<Curs> search(String text, String tipPredare) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT CursID, DenumireCurs, PretCurs, TipPredare, Coordonator FROM Cursuri WHERE DenumireCurs LIKE ?");
        if (tipPredare != null && !tipPredare.trim().isEmpty() && !"Toate".equals(tipPredare)) {
            sql.append(" AND TipPredare = ?");
        }
        sql.append(" ORDER BY CursID");

        List<Curs> cursuri = new ArrayList<>();
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            statement.setString(1, "%" + text + "%");
            if (sql.indexOf("TipPredare = ?") >= 0) {
                statement.setString(2, tipPredare);
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    cursuri.add(mapCurs(rs));
                }
            }
        }
        return cursuri;
    }

    public Optional<Curs> findById(int id) throws SQLException {
        String sql = "SELECT CursID, DenumireCurs, PretCurs, TipPredare, Coordonator FROM Cursuri WHERE CursID = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCurs(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Curs save(Curs curs) throws SQLException {
        if (curs == null) {
            throw new SQLException("Cursul nu poate fi null.");
        }
        int id = curs.getId() > 0 ? curs.getId() : nextId("Cursuri", "CursID");
        String sql = "INSERT INTO Cursuri (CursID, DenumireCurs, LimbaPredare, TipPredare, PretCurs, Coordonator, DomeniuID, InstitutieID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, curs.getTitlu());
            statement.setString(3, null);
            statement.setString(4, curs.getDescriere());
            statement.setDouble(5, curs.getPret());
            statement.setString(6, uniqueCoordinator(curs, id));
            statement.setInt(7, 1);
            statement.setInt(8, 1);
            statement.executeUpdate();
        }
        return new Curs(id, curs.getTitlu(), curs.getDescriere(), curs.getPret(), curs.getNivel(), curs.getDurata(), curs.getProfesor());
    }

    public Curs update(Curs curs) throws SQLException {
        if (curs == null || curs.getId() <= 0) {
            throw new SQLException("Selecteaza un curs valid pentru modificare.");
        }
        String sql = "UPDATE Cursuri SET DenumireCurs = ?, TipPredare = ?, PretCurs = ?, Coordonator = ? WHERE CursID = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, curs.getTitlu());
            statement.setString(2, curs.getDescriere());
            statement.setDouble(3, curs.getPret());
            statement.setString(4, curs.getProfesor() == null ? "Profesor neatribuit" : curs.getProfesor().getNume());
            statement.setInt(5, curs.getId());
            if (statement.executeUpdate() == 0) {
                throw new SQLException("Cursul nu a fost gasit pentru modificare.");
            }
        }
        return curs;
    }

    public boolean delete(int id) throws SQLException {
        if (id <= 0) {
            throw new SQLException("ID curs invalid pentru stergere.");
        }
        try (Connection connection = database.getConnection()) {
            connection.setAutoCommit(false);
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
            try (PreparedStatement grupe = connection.prepareStatement("DELETE FROM Grupe_Cursuri WHERE CursID = ?")) {
                grupe.setInt(1, id);
                grupe.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Cursuri WHERE CursID = ?")) {
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

    private Curs mapCurs(ResultSet rs) throws SQLException {
        int id = rs.getInt("CursID");
        String title = RomanianText.clean(rs.getString("DenumireCurs"));
        double price = rs.getDouble("PretCurs");
        String type = rs.getString("TipPredare") == null ? "Online" : RomanianText.clean(rs.getString("TipPredare"));
        String coordinator = rs.getString("Coordonator") == null ? "Profesor neatribuit" : RomanianText.clean(rs.getString("Coordonator"));
        Profesor profesor = new Profesor(0, coordinator, "coordonator@cursuri.local", "secret1", "Coordonator", 4.5);
        return new Curs(id, title, type, price, NivelCurs.MEDIU, 90, profesor);
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

    private String uniqueCoordinator(Curs curs, int id) {
        String name = curs.getProfesor() == null ? null : curs.getProfesor().getNume();
        if (name == null || name.trim().isEmpty() || "Profesor neatribuit".equals(name.trim())) {
            return "Coordonator curs " + id;
        }
        return name.trim();
    }
}
