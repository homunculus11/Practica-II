import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private final DatabaseConfig config;

    Database(DatabaseConfig config) {
        this.config = config;
    }

    Connection getConnection() throws SQLException {
        if (config.integratedSecurity) {
            return DriverManager.getConnection(config.jdbcUrl());
        }
        return DriverManager.getConnection(config.jdbcUrl(), config.user, config.password);
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

