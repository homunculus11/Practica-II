import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class DatabaseConfig {
    private static final String DEFAULT_SERVER = "localhost:1433";
    private static final String DEFAULT_DATABASE = "Cursuri_Online";
    final String server;
    final String database;
    final boolean integratedSecurity;
    final String user;
    final String password;

    DatabaseConfig(String server, String database, boolean integratedSecurity, String user, String password) {
        this.server = server;
        this.database = database;
        this.integratedSecurity = integratedSecurity;
        this.user = user;
        this.password = password;
    }

    static DatabaseConfig load() {
        Properties props = new Properties();
        loadFromFile(props, Paths.get("resources", "db.properties"));
        loadFromFile(props, Paths.get("db.properties"));

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

    String jdbcUrl() {
        String url = "jdbc:sqlserver://" + server
                + ";databaseName=" + database
                + ";encrypt=true;trustServerCertificate=true"
                + ";sendStringParametersAsUnicode=true";
        return integratedSecurity ? url + ";integratedSecurity=true" : url;
    }

    String describe() {
        return server + " / " + database + " / " + (integratedSecurity ? "Windows Authentication" : "SQL Login");
    }

    private static void loadFromFile(Properties props, Path path) {
        if (!Files.exists(path)) {
            return;
        }
        try (InputStream input = new FileInputStream(path.toFile());
             InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            props.load(reader);
        } catch (IOException ex) {
            System.out.println("Nu s-a putut citi " + path + ": " + ex.getMessage());
        }
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

