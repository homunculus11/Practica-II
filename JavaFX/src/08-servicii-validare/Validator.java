import java.util.regex.Pattern;

public class Validator {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public static void required(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("Câmpul '" + fieldName + "' este obligatoriu.");
        }
    }

    public static void email(String value, String fieldName) throws ValidationException {
        required(value, fieldName);
        if (!EMAIL.matcher(value.trim()).matches()) {
            throw new ValidationException("Câmpul '" + fieldName + "' trebuie să conțină un email valid.");
        }
    }

    public static void positive(double value, String fieldName) throws ValidationException {
        if (value <= 0) {
            throw new ValidationException("Câmpul '" + fieldName + "' trebuie să fie pozitiv.");
        }
    }

    public static void nonNegative(double value, String fieldName) throws ValidationException {
        if (value < 0) {
            throw new ValidationException("Câmpul '" + fieldName + "' nu poate fi negativ.");
        }
    }
}
