import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private final DatabaseConfig config;

    Database(DatabaseConfig config) {
        this.config = config;
    }

    Connection getConnection() throws SQLException {
        try {
            if (config.integratedSecurity) {
                return DriverManager.getConnection(config.jdbcUrl());
            }
            if (config.user == null || config.user.isBlank()) {
                throw new SQLException("Utilizatorul SQL nu este completat in db.properties.");
            }
            return DriverManager.getConnection(config.jdbcUrl(), config.user, config.password);
        } catch (SQLException ex) {
            throw new SQLException(AppErrors.databaseMessage(ex), ex.getSQLState(), ex.getErrorCode(), ex);
        }
    }

    boolean testConnection() {
        try (Connection ignored = getConnection()) {
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    DatabaseConfig getConfig() {
        return config;
    }
}

