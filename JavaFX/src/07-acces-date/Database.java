import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private final DatabaseConfig config;

    public Database(DatabaseConfig config) {
        this.config = config;
    }

    public Connection getConnection() throws SQLException {
        try {
            if (config.isIntegratedSecurity()) {
                return DriverManager.getConnection(config.getJdbcUrl());
            }
            if (config.getUser() == null || config.getUser().isBlank()) {
                throw new SQLException("Utilizatorul SQL nu este completat in db.properties.");
            }
            return DriverManager.getConnection(config.getJdbcUrl(), config.getUser(), config.getPassword());
        } catch (SQLException ex) {
            throw new SQLException(AppErrors.databaseMessage(ex), ex.getSQLState(), ex.getErrorCode(), ex);
        }
    }

    public boolean testConnection() {
        try (Connection ignored = getConnection()) {
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    public DatabaseConfig getConfig() {
        return config;
    }
}
