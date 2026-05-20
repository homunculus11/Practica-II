import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final String RESET = "";
    private static final String BOLD = "";
    private static final String DIM = "";
    private static final String CYAN = "";
    private static final String GREEN = "";
    private static final String RED = "";
    private static final String YELLOW = "";
    private static final String BLUE_BG = "";
    private static final String WHITE = "";
    private final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    private final Database database;
    private final StudentDAO studentDAO;
    private final ProfesorDAO profesorDAO;
    private final CursDAO cursDAO;
    private final InrolareDAO inrolareDAO;
    private final TableDAO tableDAO;

    public Main() {
        DatabaseConfig config = DatabaseConfig.load();
        database = new Database(config);
        studentDAO = new StudentDAO(database);
        profesorDAO = new ProfesorDAO(database);
        cursDAO = new CursDAO(database);
        inrolareDAO = new InrolareDAO(database);
        tableDAO = new TableDAO(database);
    }

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        printHeader();
        checkConnection();

        boolean running = true;
        while (running) {
            printMenu("MENIU PRINCIPAL",
                    "1. Studenti",
                    "2. Profesori",
                    "3. Cursuri",
                    "4. Rapoarte",
                    "5. Vizualizare tabele SQL",
                    "6. Verifica baza de date",
                    "0. Iesire");
            String choice = prompt("Alege optiunea: ");

            try {
                switch (choice) {
                    case "1":
                        studentsMenu();
                        break;
                    case "2":
                        professorsMenu();
                        break;
                    case "3":
                        coursesMenu();
                        break;
                    case "4":
                        reportsMenu();
                        break;
                    case "5":
                        tablesMenu();
                        break;
                    case "6":
                        checkConnection();
                        break;
                    case "0":
                        running = false;
                        break;
                    default:
                        printWarning("Optiune necunoscuta.");
                        break;
                }
            } catch (SQLException ex) {
                printError(AppErrors.databaseMessage(ex));
            } catch (IllegalArgumentException ex) {
                printWarning(ex.getMessage());
            } catch (Exception ex) {
                printError(AppErrors.userMessage(ex));
            }
        }

        printInfo("Aplicatia console s-a inchis.");
    }

    private void printHeader() {
        String title = "Practica II - Cursuri Online Console";
        String line = "=".repeat(64);
        System.out.println(CYAN + line + RESET);
        System.out.println(BLUE_BG + WHITE + BOLD + center(title, 64) + RESET);
        System.out.println(CYAN + line + RESET);
        System.out.println(DIM + "Configuratie DB: " + database.getConfig().describe() + RESET);
    }

    private void checkConnection() {
        if (database.testConnection()) {
            printSuccess("Conectat la " + database.getConfig().describe());
        } else {
            printError("Nu se poate realiza conexiunea la " + database.getConfig().describe());
            printWarning("Verifica SQL Server, Java/ConsoleApp/resources/db.properties si baza de date din SSMS.");
        }
    }

    private void studentsMenu() throws SQLException {
        boolean back = false;
        while (!back) {
            printMenu("STUDENTI",
                    "1. Listeaza studenti",
                    "2. Cauta student",
                    "3. Afiseaza dupa interval ID",
                    "4. Adauga student",
                    "5. Modifica student",
                    "6. Sterge student",
                    "0. Inapoi");
            switch (prompt("Alege optiunea: ")) {
                case "1":
                    printStudents(studentDAO.findAll());
                    break;
                case "2":
                    printStudents(studentDAO.search(prompt("Nume/prenume: ")));
                    break;
                case "3":
                    printStudents(studentDAO.findByIdRange(promptRange()));
                    break;
                case "4":
                    saveStudent();
                    break;
                case "5":
                    updateStudent();
                    break;
                case "6":
                    deleteStudent();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    printWarning("Optiune necunoscuta.");
                    break;
            }
        }
    }

    private void professorsMenu() throws SQLException {
        boolean back = false;
        while (!back) {
            printMenu("PROFESORI",
                    "1. Listeaza profesori",
                    "2. Cauta profesor",
                    "3. Afiseaza dupa interval ID",
                    "4. Adauga profesor",
                    "5. Modifica profesor",
                    "6. Sterge profesor",
                    "0. Inapoi");
            switch (prompt("Alege optiunea: ")) {
                case "1":
                    printProfessors(profesorDAO.findAll());
                    break;
                case "2":
                    printProfessors(profesorDAO.search(prompt("Nume/certificare: ")));
                    break;
                case "3":
                    printProfessors(profesorDAO.findByIdRange(promptRange()));
                    break;
                case "4":
                    saveProfessor();
                    break;
                case "5":
                    updateProfessor();
                    break;
                case "6":
                    deleteProfessor();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    printWarning("Optiune necunoscuta.");
                    break;
            }
        }
    }

    private void coursesMenu() throws SQLException {
        boolean back = false;
        while (!back) {
            printMenu("CURSURI",
                    "1. Listeaza cursuri",
                    "2. Cauta / filtreaza curs",
                    "3. Afiseaza dupa interval ID",
                    "4. Adauga curs",
                    "5. Modifica curs",
                    "6. Sterge curs",
                    "0. Inapoi");
            switch (prompt("Alege optiunea: ")) {
                case "1":
                    printCourses(cursDAO.findAll());
                    break;
                case "2":
                    String text = prompt("Denumire curs: ");
                    String type = prompt("Tip predare (Toate/Online/Offline/Hibrid): ");
                    printCourses(cursDAO.search(text, type));
                    break;
                case "3":
                    printCourses(cursDAO.findByIdRange(promptRange()));
                    break;
                case "4":
                    saveCourse();
                    break;
                case "5":
                    updateCourse();
                    break;
                case "6":
                    deleteCourse();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    printWarning("Optiune necunoscuta.");
                    break;
            }
        }
    }

    private void reportsMenu() throws SQLException {
        List<Curs> courses = cursDAO.findAll();
        List<Inrolare> enrollments = inrolareDAO.findAll();
        List<Student> students = studentDAO.findAll();
        ReportService report = new ReportService(courses, enrollments, students);

        printSection("RAPOARTE");
        System.out.println("1. Sumar general");
        System.out.println("2. Studenti per curs");
        System.out.println("3. Cursuri populare");
        System.out.println("4. Catalog cursuri");
        System.out.println("5. Preturi cursuri");
        System.out.println("6. Inrolari studenti");
        System.out.println("7. Toate rapoartele");
        String selection = prompt("Alege rapoarte (ex: 1,3,6 sau 7): ");
        String textReport = report.generate(selection);

        printSection("REZULTAT RAPORT");
        System.out.println(textReport);

        System.out.println("1. Salveaza ca TXT");
        System.out.println("2. Salveaza ca CSV");
        System.out.println("0. Nu salva");
        switch (prompt("Alege exportul: ")) {
            case "1":
                saveTextFile(promptDefault("Fisier TXT", "raport-cursuri.txt"), textReport);
                break;
            case "2":
                saveTextFile(promptDefault("Fisier CSV", "raport-cursuri.csv"), report.exportCSV(selection));
                break;
            case "0":
                break;
            default:
                printWarning("Optiune export necunoscuta.");
                break;
        }
    }

    private void tablesMenu() throws SQLException {
        printSection("TABELE SQL");
        List<String> tables = tableDAO.tableNames();
        for (int i = 0; i < tables.size(); i++) {
            System.out.printf("  %s%2d%s  %s%n", CYAN, i + 1, RESET, tables.get(i));
        }
        int index = parseInt(prompt("Alege tabela: "), -1) - 1;
        if (index < 0 || index >= tables.size()) {
            printWarning("Tabela invalida.");
            return;
        }
        String tableName = tables.get(index);
        System.out.println("1. Toate randurile");
        System.out.println("2. Interval ID");
        TableData data = "2".equals(prompt("Alege afisarea: "))
                ? tableDAO.findByIdRange(tableName, promptRange())
                : tableDAO.findAll(tableName);
        printTable(data, 40);
    }

    private void saveStudent() throws SQLException {
        int id = parseInt(prompt("ID (gol pentru automat): "), 0);
        String name = requireText(prompt("Nume complet: "), "Numele este obligatoriu.");
        LocalDate birthDate = parseDate(prompt("Data nasterii (YYYY-MM-DD, gol pentru azi): "), LocalDate.now());
        Student saved = studentDAO.save(new Student(id, name, birthDate, 0));
        printSuccess("Student salvat: " + saved.fullName);
    }

    private void updateStudent() throws SQLException {
        int id = parseInt(prompt("ID student: "), -1);
        requirePositiveId(id, "ID student");
        String name = requireText(prompt("Nume complet nou: "), "Numele este obligatoriu.");
        LocalDate birthDate = parseDate(prompt("Data nasterii (YYYY-MM-DD, gol pentru azi): "), LocalDate.now());
        studentDAO.update(new Student(id, name, birthDate, 0));
        printSuccess("Student modificat.");
    }

    private void deleteStudent() throws SQLException {
        int id = parseInt(prompt("ID student: "), -1);
        requirePositiveId(id, "ID student");
        if (confirm("Stergi studentul " + id + "?")) {
            printResult(studentDAO.delete(id), "Student sters.", "Nu a fost gasit.");
        }
    }

    private void saveProfessor() throws SQLException {
        int id = parseInt(prompt("ID (gol pentru automat): "), 0);
        String name = requireText(prompt("Nume complet: "), "Numele este obligatoriu.");
        String certification = promptDefault("Certificare", "Fara grad didactic");
        Profesor saved = profesorDAO.save(new Profesor(id, name, certification, 4.5));
        printSuccess("Profesor salvat: " + saved.fullName);
    }

    private void updateProfessor() throws SQLException {
        int id = parseInt(prompt("ID profesor: "), -1);
        requirePositiveId(id, "ID profesor");
        String name = requireText(prompt("Nume complet nou: "), "Numele este obligatoriu.");
        String certification = promptDefault("Certificare", "Fara grad didactic");
        profesorDAO.update(new Profesor(id, name, certification, 4.5));
        printSuccess("Profesor modificat.");
    }

    private void deleteProfessor() throws SQLException {
        int id = parseInt(prompt("ID profesor: "), -1);
        requirePositiveId(id, "ID profesor");
        if (confirm("Stergi profesorul " + id + "?")) {
            printResult(profesorDAO.delete(id), "Profesor sters.", "Nu a fost gasit.");
        }
    }

    private void saveCourse() throws SQLException {
        int id = parseInt(prompt("ID (gol pentru automat): "), 0);
        String title = requireText(prompt("Denumire curs: "), "Denumirea este obligatorie.");
        String type = promptDefault("Tip predare (Online/Offline/Hibrid)", "Online");
        double price = parseDouble(prompt("Pret: "), 0);
        String coordinator = promptDefault("Coordonator", "Profesor neatribuit");
        Curs saved = cursDAO.save(new Curs(id, title, type, price, coordinator));
        printSuccess("Curs salvat: " + saved.title);
    }

    private void updateCourse() throws SQLException {
        int id = parseInt(prompt("ID curs: "), -1);
        requirePositiveId(id, "ID curs");
        String title = requireText(prompt("Denumire curs noua: "), "Denumirea este obligatorie.");
        String type = promptDefault("Tip predare (Online/Offline/Hibrid)", "Online");
        double price = parseDouble(prompt("Pret: "), 0);
        String coordinator = promptDefault("Coordonator", "Profesor neatribuit");
        cursDAO.update(new Curs(id, title, type, price, coordinator));
        printSuccess("Curs modificat.");
    }

    private void deleteCourse() throws SQLException {
        int id = parseInt(prompt("ID curs: "), -1);
        requirePositiveId(id, "ID curs");
        if (confirm("Stergi cursul " + id + "?")) {
            printResult(cursDAO.delete(id), "Curs sters.", "Nu a fost gasit.");
        }
    }

    private void saveTextFile(String fileName, String content) {
        if (fileName == null || fileName.trim().isEmpty()) {
            printWarning("Numele fisierului este obligatoriu.");
            return;
        }
        if (content == null || content.isBlank()) {
            printWarning("Nu exista continut de salvat.");
            return;
        }
        try (FileWriter writer = new FileWriter(fileName, StandardCharsets.UTF_8)) {
            writer.write(content);
            printSuccess("Fisier salvat: " + Paths.get(fileName).toAbsolutePath());
        } catch (InvalidPathException ex) {
            printError("Calea fisierului nu este valida: " + ex.getInput());
        } catch (SecurityException ex) {
            printError("Nu ai permisiune sa salvezi in aceasta locatie.");
        } catch (IOException ex) {
            printError("Nu s-a putut salva fisierul: " + ex.getMessage());
        }
    }

    private void printStudents(List<Student> students) {
        if (students.isEmpty()) {
            printInfo("Nu exista studenti.");
            return;
        }
        printTableHeader(new int[] { 6, 35, 12, 8 }, "ID", "Nume", "Data", "Cursuri");
        for (Student student : students) {
            printTableRow(new int[] { 6, 35, 12, 8 },
                    String.valueOf(student.id),
                    trim(student.fullName, 35),
                    String.valueOf(student.birthDate),
                    String.valueOf(student.enrollments));
        }
        printTotal(students.size(), "studenti");
    }

    private void printProfessors(List<Profesor> professors) {
        if (professors.isEmpty()) {
            printInfo("Nu exista profesori.");
            return;
        }
        printTableHeader(new int[] { 6, 35, 30, 6 }, "ID", "Nume", "Certificare", "Rating");
        for (Profesor professor : professors) {
            printTableRow(new int[] { 6, 35, 30, 6 },
                    String.valueOf(professor.id),
                    trim(professor.fullName, 35),
                    trim(professor.certification, 30),
                    String.format("%.1f", professor.rating));
        }
        printTotal(professors.size(), "profesori");
    }

    private void printCourses(List<Curs> courses) {
        if (courses.isEmpty()) {
            printInfo("Nu exista cursuri.");
            return;
        }
        printTableHeader(new int[] { 6, 42, 10, 10, 30 }, "ID", "Denumire", "Tip", "Pret", "Coordonator");
        for (Curs course : courses) {
            printTableRow(new int[] { 6, 42, 10, 10, 30 },
                    String.valueOf(course.id),
                    trim(course.title, 42),
                    course.type,
                    String.format("%.2f", course.price),
                    trim(course.coordinator, 30));
        }
        printTotal(courses.size(), "cursuri");
    }

    private void printTable(TableData data, int maxWidth) {
        if (data.rows.isEmpty()) {
            printInfo("Tabela nu contine randuri.");
            return;
        }
        int[] widths = new int[data.columns.size()];
        Arrays.fill(widths, maxWidth);
        printTableHeader(widths, data.columns.toArray(new String[0]));
        for (Map<String, String> row : data.rows) {
            String[] values = data.columns.stream()
                    .map(column -> trim(row.get(column), maxWidth))
                    .toArray(String[]::new);
            printTableRow(widths, values);
        }
        printTotal(data.rows.size(), "randuri incarcate");
    }

    private String prompt(String label) {
        System.out.print(CYAN + "> " + RESET + label);
        return scanner.nextLine().trim();
    }

    private String promptDefault(String label, String fallback) {
        String value = prompt(label + " [" + fallback + "]: ");
        return value.isEmpty() ? fallback : value;
    }

    private IdRange promptRange() {
        int from = parseInt(prompt("ID de la: "), 1);
        int to = parseInt(prompt("ID pana la: "), from);
        if (to < from) {
            int swap = from;
            from = to;
            to = swap;
        }
        return new IdRange(from, to);
    }

    private boolean confirm(String message) {
        String answer = prompt(message + " (da/nu): ");
        return answer.equalsIgnoreCase("da") || answer.equalsIgnoreCase("d") || answer.equalsIgnoreCase("yes");
    }

    private void printMenu(String title, String... options) {
        printSection(title);
        for (String option : options) {
            int dot = option.indexOf('.');
            if (dot > 0) {
                System.out.println("  " + CYAN + pad(option.substring(0, dot), 2) + RESET + " " + option.substring(dot + 1).trim());
            } else {
                System.out.println("  " + option);
            }
        }
        System.out.println(DIM + "-".repeat(42) + RESET);
    }

    private void printSection(String title) {
        System.out.println();
        System.out.println(CYAN + BOLD + "== " + title + " " + "=".repeat(Math.max(2, 38 - title.length())) + RESET);
    }

    private void printSuccess(String message) {
        System.out.println(GREEN + "[OK] " + RESET + message);
    }

    private void printError(String message) {
        System.out.println(RED + "[EROARE] " + RESET + message);
    }

    private void printWarning(String message) {
        System.out.println(YELLOW + "[ERROR] " + RESET + message);
    }

    private void printInfo(String message) {
        System.out.println(CYAN + "[INFO] " + RESET + message);
    }

    private void printResult(boolean success, String successMessage, String failureMessage) {
        if (success) {
            printSuccess(successMessage);
        } else {
            printWarning(failureMessage);
        }
    }

    private void printTableHeader(int[] widths, String... headers) {
        printTableBorder(widths);
        printTableRow(widths, headers, true);
        printTableBorder(widths);
    }

    private void printTableRow(int[] widths, String... values) {
        printTableRow(widths, values, false);
    }

    private void printTableRow(int[] widths, String[] values, boolean header) {
        StringBuilder row = new StringBuilder();
        row.append(CYAN).append("|").append(RESET);
        for (int i = 0; i < widths.length; i++) {
            String value = i < values.length && values[i] != null ? values[i] : "";
            row.append(' ');
            row.append(header ? BOLD : "");
            row.append(pad(trim(value, widths[i]), widths[i]));
            row.append(header ? RESET : "");
            row.append(' ').append(CYAN).append("|").append(RESET);
        }
        System.out.println(row);
    }

    private void printTableBorder(int[] widths) {
        StringBuilder border = new StringBuilder(CYAN);
        border.append("+");
        for (int width : widths) {
            border.append("-".repeat(width + 2)).append("+");
        }
        border.append(RESET);
        System.out.println(border);
    }

    private void printTotal(int count, String label) {
        System.out.println(DIM + count + " " + label + RESET);
    }

    private String requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private void requirePositiveId(int id, String fieldName) {
        if (id <= 0) {
            throw new IllegalArgumentException(fieldName + " trebuie sa fie mai mare decat 0.");
        }
    }

    private int parseInt(String value, int fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Valoarea trebuie sa fie un numar intreg valid.");
        }
    }

    private double parseDouble(String value, double fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        try {
            double result = Double.parseDouble(value.trim());
            if (result < 0) {
                throw new IllegalArgumentException("Valoarea numerica nu poate fi negativa.");
            }
            return result;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Valoarea trebuie sa fie un numar valid.");
        }
    }

    private LocalDate parseDate(String value, LocalDate fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Data trebuie sa fie in formatul YYYY-MM-DD.");
        }
    }

    private String trim(String value, int max) {
        String text = value == null ? "" : value;
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }

    private String pad(String value, int width) {
        String text = value == null ? "" : value;
        if (text.length() >= width) {
            return text;
        }
        return text + " ".repeat(width - text.length());
    }

    private String center(String value, int width) {
        if (value.length() >= width) {
            return value;
        }
        int left = (width - value.length()) / 2;
        int right = width - value.length() - left;
        return " ".repeat(left) + value + " ".repeat(right);
    }

}

