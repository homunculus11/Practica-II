import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class DatabaseConfig {
    private static final String DEFAULT_SERVER = "localhost\\SQLEXPRESS";
    private static final String DEFAULT_DATABASE = "Cursuri_Online";

    private final String server;
    private final String database;
    private final boolean integratedSecurity;
    private final String user;
    private final String password;

    private DatabaseConfig(String server, String database, boolean integratedSecurity, String user, String password) {
        this.server = server;
        this.database = database;
        this.integratedSecurity = integratedSecurity;
        this.user = user;
        this.password = password;
    }

    public static DatabaseConfig load() {
        Properties props = new Properties();
        Path configPath = Paths.get("db.properties");
        if (Files.exists(configPath)) {
            try (FileInputStream input = new FileInputStream(configPath.toFile())) {
                props.load(input);
            } catch (IOException ex) {
                System.err.println("Nu s-a putut citi db.properties: " + ex.getMessage());
            }
        }

        String server = firstNonBlank(System.getenv("DB_SERVER"), props.getProperty("db.server"), DEFAULT_SERVER);
        String database = firstNonBlank(System.getenv("DB_NAME"), props.getProperty("db.name"), DEFAULT_DATABASE);
        boolean integrated = Boolean.parseBoolean(firstNonBlank(
                System.getenv("DB_INTEGRATED_SECURITY"),
                props.getProperty("db.integratedSecurity"),
                "true"));
        String user = firstNonBlank(System.getenv("DB_USER"), props.getProperty("db.user"), "");
        String password = firstNonBlank(System.getenv("DB_PASSWORD"), props.getProperty("db.password"), "");

        return new DatabaseConfig(server, database, integrated, user, password);
    }

    public String getJdbcUrl() {
        StringBuilder url = new StringBuilder("jdbc:sqlserver://")
                .append(server)
                .append(";databaseName=")
                .append(database)
                .append(";encrypt=true;trustServerCertificate=true");

        if (integratedSecurity) {
            url.append(";integratedSecurity=true");
        }

        return url.toString();
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public boolean isIntegratedSecurity() {
        return integratedSecurity;
    }

    public String describe() {
        return server + " / " + database + " / "
                + (integratedSecurity ? "Windows Authentication" : "SQL Login");
    }

    private static String firstNonBlank(String first, String second, String fallback) {
        if (first != null && !first.trim().isEmpty()) {
            return first.trim();
        }
        if (second != null && !second.trim().isEmpty()) {
            return second.trim();
        }
        return fallback;
    }
}
