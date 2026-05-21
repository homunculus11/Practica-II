import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService {
    private final List<Curs> courses;
    private final List<Inrolare> enrollments;
    private final List<Student> students;

    ReportService(List<Curs> courses, List<Inrolare> enrollments, List<Student> students) {
        this.courses = courses;
        this.enrollments = enrollments;
        this.students = students;
    }

    String summary() {
        double income = enrollments.stream().mapToDouble(enrollment -> enrollment.course.price).sum();
        double averagePrice = courses.stream().mapToDouble(course -> course.price).average().orElse(0);
        return String.format("""
                Sumar general:
                - Cursuri: %d
                - Studenți: %d
                - înrolări: %d
                - Venit estimat din Înrolări: %.2f lei
                - Preț mediu curs: %.2f lei
                """, courses.size(), students.size(), enrollments.size(), income, averagePrice);
    }

    String studentsPerCourse() {
        Map<String, Long> count = enrollments.stream()
                .collect(Collectors.groupingBy(enrollment -> enrollment.course.title, LinkedHashMap::new, Collectors.counting()));
        StringBuilder builder = new StringBuilder("Studenți per curs:\n");
        if (count.isEmpty()) {
            return builder.append("- Nu există Înrolări.\n").toString();
        }
        count.forEach((course, total) -> builder.append("- ").append(course).append(": ").append(total).append('\n'));
        return builder.toString();
    }

    String popularCourses() {
        String popular = enrollments.stream()
                .collect(Collectors.groupingBy(enrollment -> enrollment.course.title, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(entry -> "- " + entry.getKey() + " (" + entry.getValue() + " inrolari)")
                .collect(Collectors.joining("\n"));
        return "Cursuri populare:\n" + (popular.isEmpty() ? "- Nu există Înrolări." : popular) + "\n";
    }

    String courseCatalog() {
        StringBuilder builder = new StringBuilder("Catalog cursuri:\n");
        for (Curs course : courses) {
            builder.append(String.format("- %d | %s | %s | %.2f lei | %s%n",
                    course.id, course.title, course.type, course.price, course.coordinator));
        }
        return builder.toString();
    }

    String coursePrices() {
        StringBuilder builder = new StringBuilder("Prețuri cursuri:\n");
        if (courses.isEmpty()) {
            return builder.append("- Nu există cursuri.\n").toString();
        }
        Curs cheapest = courses.stream().min((a, b) -> Double.compare(a.price, b.price)).orElse(null);
        Curs mostExpensive = courses.stream().max((a, b) -> Double.compare(a.price, b.price)).orElse(null);
        double average = courses.stream().mapToDouble(course -> course.price).average().orElse(0);
        builder.append(String.format("- Cel mai ieftin: %s - %.2f lei%n", cheapest.title, cheapest.price));
        builder.append(String.format("- Cel mai scump: %s - %.2f lei%n", mostExpensive.title, mostExpensive.price));
        builder.append(String.format("- Preț mediu: %.2f lei%n", average));
        return builder.toString();
    }

    String studentEnrollments() {
        String text = enrollments.stream()
                .collect(Collectors.groupingBy(enrollment -> enrollment.student.fullName, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(entry -> "- " + entry.getKey() + ": " + entry.getValue() + " cursuri")
                .collect(Collectors.joining("\n"));
        return "Înrolări studenți:\n" + (text.isEmpty() ? "- Nu există Înrolări." : text) + "\n";
    }

    String generate(String selection) {
        StringBuilder builder = new StringBuilder();
        for (String option : selectedOptions(selection)) {
            builder.append(switch (option) {
                case "1" -> summary();
                case "2" -> studentsPerCourse();
                case "3" -> popularCourses();
                case "4" -> courseCatalog();
                case "5" -> coursePrices();
                case "6" -> studentEnrollments();
                default -> "";
            }).append('\n');
        }
        return builder.toString().trim() + "\n";
    }

    String exportCSV(String selection) {
        StringBuilder builder = new StringBuilder();
        for (String option : selectedOptions(selection)) {
            switch (option) {
                case "1" -> appendSummaryCsv(builder);
                case "2" -> appendStudentsPerCourseCsv(builder);
                case "3" -> appendPopularCoursesCsv(builder);
                case "4" -> appendCatalogCsv(builder);
                case "5" -> appendPricesCsv(builder);
                case "6" -> appendStudentEnrollmentsCsv(builder);
                default -> {
                }
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    private List<String> selectedOptions(String selection) {
        if (selection == null || selection.trim().isEmpty() || selection.contains("7")) {
            return Arrays.asList("1", "2", "3", "4", "5", "6");
        }
        return Arrays.stream(selection.split("[,\\s]+"))
                .map(String::trim)
                .filter(value -> value.matches("[1-6]"))
                .distinct()
                .toList();
    }

    private void appendSummaryCsv(StringBuilder builder) {
        double income = enrollments.stream().mapToDouble(enrollment -> enrollment.course.price).sum();
        double averagePrice = courses.stream().mapToDouble(course -> course.price).average().orElse(0);
        builder.append("Raport,Metric,Valoare\n");
        builder.append("Sumar general,Cursuri,").append(courses.size()).append('\n');
        builder.append("Sumar general,Studenți,").append(students.size()).append('\n');
        builder.append("Sumar general,înrolări,").append(enrollments.size()).append('\n');
        builder.append("Sumar general,Venit estimat,").append(String.format("%.2f", income)).append('\n');
        builder.append("Sumar general,Preț mediu,").append(String.format("%.2f", averagePrice)).append('\n');
    }

    private void appendStudentsPerCourseCsv(StringBuilder builder) {
        builder.append("Raport,Curs,Studenți\n");
        enrollments.stream()
                .collect(Collectors.groupingBy(enrollment -> enrollment.course.title, LinkedHashMap::new, Collectors.counting()))
                .forEach((course, total) -> builder.append("Studenți per curs,").append(csv(course)).append(',').append(total).append('\n'));
    }

    private void appendPopularCoursesCsv(StringBuilder builder) {
        builder.append("Raport,Curs,înrolări\n");
        enrollments.stream()
                .collect(Collectors.groupingBy(enrollment -> enrollment.course.title, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .forEach(entry -> builder.append("Cursuri populare,").append(csv(entry.getKey())).append(',').append(entry.getValue()).append('\n'));
    }

    private void appendCatalogCsv(StringBuilder builder) {
        builder.append("Raport,ID,Curs,Tip,Preț,Coordonator\n");
        for (Curs course : courses) {
            builder.append("Catalog cursuri,")
                    .append(course.id).append(',')
                    .append(csv(course.title)).append(',')
                    .append(csv(course.type)).append(',')
                    .append(String.format("%.2f", course.price)).append(',')
                    .append(csv(course.coordinator)).append('\n');
        }
    }

    private void appendPricesCsv(StringBuilder builder) {
        builder.append("Raport,Metric,Curs,Preț\n");
        if (courses.isEmpty()) {
            return;
        }
        Curs cheapest = courses.stream().min((a, b) -> Double.compare(a.price, b.price)).orElse(null);
        Curs mostExpensive = courses.stream().max((a, b) -> Double.compare(a.price, b.price)).orElse(null);
        double average = courses.stream().mapToDouble(course -> course.price).average().orElse(0);
        builder.append("Prețuri cursuri,Cel mai ieftin,").append(csv(cheapest.title)).append(',').append(String.format("%.2f", cheapest.price)).append('\n');
        builder.append("Prețuri cursuri,Cel mai scump,").append(csv(mostExpensive.title)).append(',').append(String.format("%.2f", mostExpensive.price)).append('\n');
        builder.append("Prețuri cursuri,Preț mediu,,").append(String.format("%.2f", average)).append('\n');
    }

    private void appendStudentEnrollmentsCsv(StringBuilder builder) {
        builder.append("Raport,Student,Cursuri\n");
        enrollments.stream()
                .collect(Collectors.groupingBy(enrollment -> enrollment.student.fullName, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .forEach(entry -> builder.append("Înrolări studenți,").append(csv(entry.getKey())).append(',').append(entry.getValue()).append('\n'));
    }

    private String csv(String value) {
        String text = value == null ? "" : value;
        if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }
}

