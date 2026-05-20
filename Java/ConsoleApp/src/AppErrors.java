import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLInvalidAuthorizationSpecException;
import java.sql.SQLNonTransientConnectionException;

public class AppErrors {
    private AppErrors() {
    }

    static String userMessage(Exception ex) {
        if (ex instanceof IllegalArgumentException) {
            return ex.getMessage();
        }
        if (ex instanceof IOException) {
            return "Fisierul nu a putut fi citit sau salvat: " + ex.getMessage();
        }
        if (ex instanceof SQLException sqlException) {
            return databaseMessage(sqlException);
        }
        if (ex instanceof SecurityException) {
            return "Aplicatia nu are permisiunea necesara pentru aceasta operatie.";
        }
        if (ex instanceof NullPointerException) {
            return "Operatia nu poate continua deoarece lipsesc date necesare.";
        }
        String message = ex.getMessage();
        return message == null || message.isBlank()
                ? "A aparut o eroare neasteptata."
                : message;
    }

    static String databaseMessage(SQLException ex) {
        if (ex instanceof SQLInvalidAuthorizationSpecException) {
            return "Autentificarea la SQL Server a esuat. Verifica userul, parola sau Windows Authentication.";
        }
        if (ex instanceof SQLNonTransientConnectionException) {
            return "Nu se poate realiza conexiunea la SQL Server. Verifica serverul, portul si serviciul SQL Server.";
        }
        if (ex instanceof SQLIntegrityConstraintViolationException || isConstraintError(ex)) {
            return "Operatia incalca o regula din baza de date: ID duplicat, valoare unica duplicata sau relatie lipsa.";
        }
        String message = ex.getMessage();
        return message == null || message.isBlank()
                ? "A aparut o eroare la baza de date."
                : "Eroare SQL: " + message;
    }

    private static boolean isConstraintError(SQLException ex) {
        int code = Math.abs(ex.getErrorCode());
        String state = ex.getSQLState();
        return code == 2601 || code == 2627 || code == 547 || (state != null && state.startsWith("23"));
    }
}
