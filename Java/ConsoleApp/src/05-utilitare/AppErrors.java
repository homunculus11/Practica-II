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
            return "Fișierul nu a putut fi citit sau salvat: " + ex.getMessage();
        }
        if (ex instanceof SQLException sqlException) {
            return databaseMessage(sqlException);
        }
        if (ex instanceof SecurityException) {
            return "Aplicașia nu are permisiunea necesară pentru această operație.";
        }
        if (ex instanceof NullPointerException) {
            return "Operația nu poate continua deoarece lipsesc date necesare.";
        }
        String message = ex.getMessage();
        return message == null || message.isBlank()
                ? "A apărut o eroare neașteptată."
                : message;
    }

    static String databaseMessage(SQLException ex) {
        if (ex instanceof SQLInvalidAuthorizationSpecException) {
            return "Autentificarea la SQL Server a eșuat. Verifică utilizatorul, parola sau Windows Authentication.";
        }
        if (ex instanceof SQLNonTransientConnectionException) {
            return "Nu se poate realiza conexiunea la SQL Server. Verifică serverul, portul și serviciul SQL Server.";
        }
        if (ex instanceof SQLIntegrityConstraintViolationException || isConstraintError(ex)) {
            return "Operația încalcă o regulă din baza de date: ID duplicat, valoare unică duplicată sau relație lipsă.";
        }
        String message = ex.getMessage();
        return message == null || message.isBlank()
                ? "A apărut o eroare la baza de date."
                : "Eroare SQL: " + message;
    }

    private static boolean isConstraintError(SQLException ex) {
        int code = Math.abs(ex.getErrorCode());
        String state = ex.getSQLState();
        return code == 2601 || code == 2627 || code == 547 || (state != null && state.startsWith("23"));
    }
}
