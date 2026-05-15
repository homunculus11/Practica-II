import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private final DatabaseConfig config;

    public Database(DatabaseConfig config) {
        this.config = config;
    }

    public Connection getConnection() throws SQLException {
        if (config.isIntegratedSecurity()) {
            return DriverManager.getConnection(config.getJdbcUrl());
        }
        return DriverManager.getConnection(config.getJdbcUrl(), config.getUser(), config.getPassword());
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
