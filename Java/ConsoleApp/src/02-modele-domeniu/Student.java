import java.time.LocalDate;

public class Student {
    final int id;
    final String fullName;
    final LocalDate birthDate;
    final int enrollments;

    Student(int id, String fullName, LocalDate birthDate, int enrollments) {
        this.id = id;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.enrollments = enrollments;
    }
}

