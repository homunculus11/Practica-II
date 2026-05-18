public class NameParts {
    private final String lastName;
    private final String firstName;

    private NameParts(String lastName, String firstName) {
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public static NameParts fromFullName(String fullName) {
        String cleaned = RomanianText.clean(fullName);
        if (cleaned.isEmpty()) {
            return new NameParts("", "");
        }

        String[] parts = cleaned.split("\\s+", 2);
        String lastName = parts[0];
        String firstName = parts.length > 1 ? parts[1] : "";
        return new NameParts(lastName, firstName);
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }
}
