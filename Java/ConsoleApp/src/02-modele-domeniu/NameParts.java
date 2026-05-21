public class NameParts {
    final String lastName;
    final String firstName;

    private NameParts(String lastName, String firstName) {
        this.lastName = lastName;
        this.firstName = firstName;
    }

    static NameParts from(String fullName) {
        String[] parts = fullName.trim().split("\\s+", 2);
        return new NameParts(parts[0], parts.length > 1 ? parts[1] : "");
    }
}

