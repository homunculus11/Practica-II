import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class Main extends Application {
    private Database database;
    private StudentDAO studentDAO;
    private ProfesorDAO profesorDAO;
    private CursDAO cursDAO;
    private InrolareDAO inrolareDAO;

    private final ListView<Student> studentList = new ListView<>();
    private final ListView<Profesor> profesorList = new ListView<>();
    private final ListView<Curs> cursList = new ListView<>();
    private final TextArea reportArea = new TextArea();
    private final Label coursesMetric = new Label("-");
    private final Label studentsMetric = new Label("-");
    private final Label enrollmentsMetric = new Label("-");
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        DatabaseConfig config = DatabaseConfig.load();
        database = new Database(config);
        studentDAO = new StudentDAO(database);
        profesorDAO = new ProfesorDAO(database);
        cursDAO = new CursDAO(database);
        inrolareDAO = new InrolareDAO(database);

        statusLabel = new Label("Conectare: " + config.describe());
        statusLabel.getStyleClass().add("status-label");

        configureLists();

        TabPane tabs = new TabPane();
        tabs.getStyleClass().add("main-tabs");
        tabs.getTabs().add(createStudentTab());
        tabs.getTabs().add(createProfesorTab());
        tabs.getTabs().add(createCursTab());
        tabs.getTabs().add(createReportTab(primaryStage));
        tabs.getTabs().forEach(tab -> tab.setClosable(false));

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setTop(createTopBar());
        root.setCenter(tabs);
        root.setBottom(statusLabel);
        BorderPane.setMargin(statusLabel, new Insets(0, 24, 18, 24));

        Scene scene = new Scene(root, 1180, 760);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setMinWidth(980);
        primaryStage.setMinHeight(640);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Practica II - Cursuri Online");
        primaryStage.show();

        testAndLoad();
    }

    private void configureLists() {
        studentList.getStyleClass().add("data-list");
        profesorList.getStyleClass().add("data-list");
        cursList.getStyleClass().add("data-list");

        studentList.setCellFactory(list -> new ListCell<Student>() {
            @Override
            protected void updateItem(Student item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                setGraphic(empty || item == null ? null : dataRow(
                        item.getNume(),
                        "Data nașterii: " + item.getDataInrolare(),
                        "ID " + item.getId() + "  |  Progres: " + String.format("%.1f", item.getProgres()) + "%"));
            }
        });

        profesorList.setCellFactory(list -> new ListCell<Profesor>() {
            @Override
            protected void updateItem(Profesor item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                setGraphic(empty || item == null ? null : dataRow(
                        item.getNume(),
                        "Specializare: " + item.getSpecializare(),
                        "ID " + item.getId() + "  |  Rating " + String.format("%.1f", item.getRating())));
            }
        });

        cursList.setCellFactory(list -> new ListCell<Curs>() {
            @Override
            protected void updateItem(Curs item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                String price = String.format("%.2f lei", item.getPret());
                setGraphic(empty || item == null ? null : dataRow(
                        item.getTitlu(),
                        item.getProfesor().getNume(),
                        "ID " + item.getId() + "  |  " + item.getDescriere() + "  |  " + price));
            }
        });
    }

    private HBox createTopBar() {
        Label title = new Label("Cursuri Online");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label("Administrare cursuri, profesori, studenți, rapoarte și export");
        subtitle.getStyleClass().add("app-subtitle");

        VBox copy = new VBox(4, title, subtitle);

        Label db = new Label(database.getConfig().describe());
        db.getStyleClass().add("db-pill");

        Button testButton = new Button("Verifica baza de date");
        testButton.getStyleClass().addAll("button", "secondary-button");
        testButton.setOnAction(event -> testAndLoad());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(18, copy, spacer, db, testButton);
        bar.getStyleClass().add("hero-bar");
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    private Tab createStudentTab() {
        TextField id = field("Lăsați gol pentru adăugare nouă");
        TextField nume = field("Exemplu: Popescu Ion");
        TextField dataNasterii = field("Exemplu: 2004-05-21");
        TextField search = field("Scrieți un nume și apăsați caută");

        Button refresh = secondary("Arată toți studenții");
        refresh.setOnAction(event -> loadStudents());
        Button searchButton = secondary("Caută student");
        searchButton.setOnAction(event -> runSafely(() -> studentList.setItems(FXCollections.observableArrayList(studentDAO.search(search.getText())))));
        Button add = primary("Adaugă student nou");
        add.setOnAction(event -> runSafely(() -> {
            Validator.required(nume.getText(), "Nume");
            LocalDate date = parseDateOrToday(dataNasterii.getText());
            Student saved = studentDAO.save(new Student(parseId(id.getText()), nume.getText().trim(), hiddenEmail("student", parseId(id.getText())), "secret1", date));
            statusLabel.setText("Student salvat: " + saved.getNume());
            loadStudents();
        }));
        Button update = secondary("Salvează modificările");
        update.setOnAction(event -> runSafely(() -> {
            Student selected = requireSelected(studentList, "Selectează un student pentru modificare.");
            Validator.required(nume.getText(), "Nume");
            studentDAO.update(new Student(selected.getId(), nume.getText().trim(), selected.getEmail(), "secret1", parseDateOrToday(dataNasterii.getText())));
            loadStudents();
        }));
        Button delete = danger("Șterge studentul selectat");
        delete.setOnAction(event -> runSafely(() -> {
            Student selected = requireSelected(studentList, "Selectează un student pentru ștergere.");
            if (confirmDelete("Ștergere student", "Sigur vrei să ștergi studentul selectat?")) {
                studentDAO.delete(selected.getId());
                loadStudents();
            }
        }));

        studentList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, value) -> {
            if (value != null) {
                id.setText(String.valueOf(value.getId()));
                nume.setText(value.getNume());
                dataNasterii.setText(String.valueOf(value.getDataInrolare()));
            }
        });

        return new Tab("Studenti", crudLayout(
                "Studenti",
                "Completează formularul din stânga. Pentru modificare, alege mai întâi un student din listă.",
                studentList,
                form(
                        labeled("ID", id, "Opțional. Se poate lăsa gol la adăugare."),
                        labeled("Nume complet", nume, "Câmp obligatoriu."),
                        labeled("Data nașterii", dataNasterii, "Format recomandat: an-lună-zi, de exemplu 2004-05-21."),
                        labeled("Căutare", search, "Caută rapid după nume.")),
                actionPanel("Studenti", refresh, searchButton, add, update, delete)));
    }

    private Tab createProfesorTab() {
        TextField id = field("Lăsați gol pentru adăugare nouă");
        TextField nume = field("Exemplu: Ionescu Maria");
        TextField specializare = field("Exemplu: Programare Java");
        TextField search = field("Scrieți nume sau specializare");

        Button refresh = secondary("Arată toți profesorii");
        refresh.setOnAction(event -> loadProfesori());
        Button searchButton = secondary("Caută profesor");
        searchButton.setOnAction(event -> runSafely(() -> profesorList.setItems(FXCollections.observableArrayList(profesorDAO.search(search.getText())))));
        Button add = primary("Adaugă profesor nou");
        add.setOnAction(event -> runSafely(() -> {
            Validator.required(nume.getText(), "Nume");
            Profesor profesor = new Profesor(parseId(id.getText()), nume.getText().trim(), hiddenEmail("profesor", parseId(id.getText())), "secret1", specializare.getText().trim(), 4.5);
            profesorDAO.save(profesor);
            loadProfesori();
        }));
        Button update = secondary("Salvează modificările");
        update.setOnAction(event -> runSafely(() -> {
            Profesor selected = requireSelected(profesorList, "Selectează un profesor pentru modificare.");
            Validator.required(nume.getText(), "Nume");
            profesorDAO.update(new Profesor(selected.getId(), nume.getText().trim(), selected.getEmail(), "secret1", specializare.getText().trim(), selected.getRating()));
            loadProfesori();
        }));
        Button delete = danger("Șterge profesorul selectat");
        delete.setOnAction(event -> runSafely(() -> {
            Profesor selected = requireSelected(profesorList, "Selectează un profesor pentru ștergere.");
            if (confirmDelete("Ștergere profesor", "Sigur vrei să ștergi profesorul selectat?")) {
                profesorDAO.delete(selected.getId());
                loadProfesori();
            }
        }));

        profesorList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, value) -> {
            if (value != null) {
                id.setText(String.valueOf(value.getId()));
                nume.setText(value.getNume());
                specializare.setText(value.getSpecializare());
            }
        });

        return new Tab("Profesori", crudLayout(
                "Profesori",
                "Alege un profesor din listă pentru editare sau completează formularul pentru unul nou.",
                profesorList,
                form(
                        labeled("ID", id, "Opțional. Se poate lăsa gol la adăugare."),
                        labeled("Nume complet", nume, "Câmp obligatoriu."),
                        labeled("Specializare", specializare, "Domeniul sau certificarea profesorului."),
                        labeled("Căutare", search, "Caută după nume sau specializare.")),
                actionPanel("Profesori", refresh, searchButton, add, update, delete)));
    }

    private Tab createCursTab() {
        TextField id = field("Lăsați gol pentru curs nou");
        TextField titlu = field("Exemplu: Introducere în Java");
        TextField pret = field("Exemplu: 1200");
        TextField coordonator = field("Numele profesorului coordonator");
        ComboBox<String> tip = new ComboBox<>(FXCollections.observableArrayList("Toate", "Online", "Offline", "Hibrid"));
        tip.setValue("Online");
        tip.getStyleClass().add("input");
        TextField search = field("Scrieți denumirea cursului");

        Button refresh = secondary("Arată toate cursurile");
        refresh.setOnAction(event -> loadCursuri());
        Button searchButton = secondary("Caută sau filtrează");
        searchButton.setOnAction(event -> runSafely(() -> cursList.setItems(FXCollections.observableArrayList(cursDAO.search(search.getText(), tip.getValue())))));
        Button add = primary("Adaugă curs nou");
        add.setOnAction(event -> runSafely(() -> {
            Validator.required(titlu.getText(), "Denumire curs");
            double price = parsePrice(pret.getText());
            Validator.nonNegative(price, "Pret");
            Profesor profesor = new Profesor(0, blankDefault(coordonator.getText(), "Profesor neatribuit"), "coordonator@cursuri.local", "secret1", "Coordonator", 4.5);
            cursDAO.save(new Curs(parseId(id.getText()), titlu.getText().trim(), tip.getValue(), price, NivelCurs.MEDIU, 90, profesor));
            loadCursuri();
        }));
        Button update = secondary("Salvează modificările");
        update.setOnAction(event -> runSafely(() -> {
            Curs selected = requireSelected(cursList, "Selectează un curs pentru modificare.");
            Validator.required(titlu.getText(), "Denumire curs");
            double price = parsePrice(pret.getText());
            Validator.nonNegative(price, "Pret");
            Profesor profesor = new Profesor(0, blankDefault(coordonator.getText(), "Profesor neatribuit"), "coordonator@cursuri.local", "secret1", "Coordonator", 4.5);
            cursDAO.update(new Curs(selected.getId(), titlu.getText().trim(), tip.getValue(), price, NivelCurs.MEDIU, selected.getDurata(), profesor));
            loadCursuri();
        }));
        Button delete = danger("Șterge cursul selectat");
        delete.setOnAction(event -> runSafely(() -> {
            Curs selected = requireSelected(cursList, "Selectează un curs pentru ștergere.");
            if (confirmDelete("Ștergere curs", "Sigur vrei să ștergi cursul selectat?")) {
                cursDAO.delete(selected.getId());
                loadCursuri();
            }
        }));

        cursList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, value) -> {
            if (value != null) {
                id.setText(String.valueOf(value.getId()));
                titlu.setText(value.getTitlu());
                pret.setText(String.valueOf(value.getPret()));
                coordonator.setText(value.getProfesor().getNume());
                tip.setValue(value.getDescriere());
            }
        });

        return new Tab("Cursuri", crudLayout(
                "Cursuri",
                "Completează datele cursului. Prețul trebuie să fie zero sau un număr pozitiv.",
                cursList,
                form(
                        labeled("ID", id, "Opțional. Se poate lăsa gol la adăugare."),
                        labeled("Denumire curs", titlu, "Câmp obligatoriu."),
                        labeled("Preț", pret, "Nu poate fi negativ."),
                        labeled("Coordonator", coordonator, "Numele profesorului responsabil."),
                        labeled("Tip predare", tip, "Alege Online, Offline sau Hibrid."),
                        labeled("Căutare", search, "Caută după denumirea cursului.")),
                actionPanel("Cursuri", refresh, searchButton, add, update, delete)));
    }

    private Tab createReportTab(Stage stage) {
        reportArea.setEditable(false);
        reportArea.setWrapText(false);
        reportArea.getStyleClass().add("report-area");

        Button allReports = primary("Generează rapoartele");
        allReports.setOnAction(event -> runSafely(() -> {
            RaportService service = new RaportService(cursDAO.findAll(), inrolareDAO.findAll(), studentDAO.findAll());
            reportArea.setText(service.genRaport());
            updateMetrics();
        }));
        Button exportCsv = secondary("Salvează ca CSV");
        exportCsv.setOnAction(event -> runSafely(() -> export(stage, "raport-cursuri.csv", new RaportService(cursDAO.findAll(), inrolareDAO.findAll(), studentDAO.findAll()).exportCSV())));
        Button exportTxt = secondary("Salvează ca TXT");
        exportTxt.setOnAction(event -> runSafely(() -> export(stage, "raport-cursuri.txt", reportArea.getText().isEmpty() ? "Nu există raport generat." : reportArea.getText())));

        HBox metrics = new HBox(14,
                metricCard("Cursuri", coursesMetric, "Oferta activa"),
                metricCard("Studenti", studentsMetric, "In baza de date"),
                metricCard("Inrolari", enrollmentsMetric, "Participari cursuri"));
        metrics.getStyleClass().add("metrics-row");

        VBox panel = new VBox(16,
                sectionHeader("Rapoarte și export", "Generează rapoarte centralizate și salvează datele în fișiere CSV sau TXT."),
                metrics,
                reportActions(allReports, exportCsv, exportTxt),
                reportArea);
        panel.getStyleClass().add("content-panel");
        VBox.setVgrow(reportArea, Priority.ALWAYS);

        VBox content = new VBox(panel);
        content.getStyleClass().add("page");
        return new Tab("Rapoarte", content);
    }

    private VBox crudLayout(String title, String description, ListView<?> list, GridPane form, VBox actions) {
        VBox side = new VBox(18, sectionHeader(title, description), form, actions);
        side.getStyleClass().add("side-panel");
        side.setMinWidth(330);
        side.setPrefWidth(370);

        VBox data = new VBox(12, panelTitle("Date"), list);
        data.getStyleClass().add("content-panel");
        VBox.setVgrow(list, Priority.ALWAYS);

        HBox body = new HBox(18, side, data);
        body.getStyleClass().add("split-layout");
        HBox.setHgrow(data, Priority.ALWAYS);

        VBox page = new VBox(body);
        page.getStyleClass().add("page");
        VBox.setVgrow(body, Priority.ALWAYS);
        return page;
    }

    private VBox sectionHeader(String title, String description) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-title");
        Label descriptionLabel = new Label(description);
        descriptionLabel.getStyleClass().add("section-description");
        descriptionLabel.setWrapText(true);
        return new VBox(5, titleLabel, descriptionLabel);
    }

    private VBox labeled(String label, Node input, String helper) {
        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("field-label");
        Label helperNode = new Label(helper);
        helperNode.getStyleClass().add("field-helper");
        helperNode.setWrapText(true);
        VBox box = new VBox(5, labelNode, input, helperNode);
        box.getStyleClass().add("field-box");
        VBox.setVgrow(input, Priority.NEVER);
        return box;
    }

    private HBox panelTitle(String title) {
        Label label = new Label(title);
        label.getStyleClass().add("panel-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label hint = new Label("Selectează un rând pentru editare");
        hint.getStyleClass().add("panel-hint");
        HBox box = new HBox(10, label, spacer, hint);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private VBox metricCard(String title, Label value, String subtitle) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("metric-title");
        value.getStyleClass().add("metric-value");
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("metric-subtitle");
        VBox card = new VBox(4, titleLabel, value, subtitleLabel);
        card.getStyleClass().add("metric-card");
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private HBox dataRow(String title, String subtitle, String meta) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("row-title");
        titleLabel.setWrapText(true);
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("row-subtitle");
        Label metaLabel = new Label(meta);
        metaLabel.getStyleClass().add("row-meta");
        VBox text = new VBox(4, titleLabel, subtitleLabel, metaLabel);
        HBox row = new HBox(text);
        row.getStyleClass().add("data-row");
        HBox.setHgrow(text, Priority.ALWAYS);
        return row;
    }

    private TextField field(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("input");
        field.setMinHeight(42);
        return field;
    }

    private Button primary(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("primary-button");
        return button;
    }

    private Button secondary(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("secondary-button");
        return button;
    }

    private Button danger(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("danger-button");
        return button;
    }

    private GridPane form(Node... nodes) {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("field-grid");
        grid.setHgap(10);
        grid.setVgap(10);
        for (int i = 0; i < nodes.length; i++) {
            grid.add(nodes[i], i % 2, i / 2);
            GridPane.setHgrow(nodes[i], Priority.ALWAYS);
        }
        return grid;
    }

    private VBox actionPanel(String entity, Button refresh, Button search, Button add, Button update, Button delete) {
        refresh.setMaxWidth(Double.MAX_VALUE);
        search.setMaxWidth(Double.MAX_VALUE);
        add.setMaxWidth(Double.MAX_VALUE);
        update.setMaxWidth(Double.MAX_VALUE);
        delete.setMaxWidth(Double.MAX_VALUE);

        Label title = new Label("Actiuni");
        title.getStyleClass().add("action-title");

        Label findLabel = new Label("1. Găsește date");
        findLabel.getStyleClass().add("action-step");
        HBox findRow = new HBox(8, refresh, search);
        HBox.setHgrow(refresh, Priority.ALWAYS);
        HBox.setHgrow(search, Priority.ALWAYS);

        Label saveLabel = new Label("2. Adaugă sau salvează");
        saveLabel.getStyleClass().add("action-step");
        VBox saveRow = new VBox(8, add, update);

        Label dangerLabel = new Label("3. Ștergere");
        dangerLabel.getStyleClass().add("action-step-danger");
        VBox dangerRow = new VBox(8, delete);

        VBox panel = new VBox(10, title, findLabel, findRow, saveLabel, saveRow, dangerLabel, dangerRow);
        panel.getStyleClass().add("action-panel");
        return panel;
    }

    private HBox reportActions(Button... buttons) {
        HBox box = new HBox(10, buttons);
        box.getStyleClass().add("action-row");
        box.setAlignment(Pos.CENTER_LEFT);
        for (Button button : buttons) {
            button.setMinWidth(180);
        }
        return box;
    }

    private void testAndLoad() {
        runSafely(() -> {
            if (!database.testConnection()) {
                throw new SQLException("Conexiunea la SQL Server a eșuat. Verifică db.properties și baza de date din SSMS.");
            }
            statusLabel.setText("Conectat la " + database.getConfig().describe());
            loadStudents();
            loadProfesori();
            loadCursuri();
            updateMetrics();
        });
    }

    private void updateMetrics() throws SQLException {
        coursesMetric.setText(String.valueOf(cursDAO.findAll().size()));
        studentsMetric.setText(String.valueOf(studentDAO.findAll().size()));
        enrollmentsMetric.setText(String.valueOf(inrolareDAO.findAll().size()));
    }

    private void loadStudents() {
        runSafely(() -> studentList.setItems(FXCollections.observableArrayList(studentDAO.findAll())));
    }

    private void loadProfesori() {
        runSafely(() -> profesorList.setItems(FXCollections.observableArrayList(profesorDAO.findAll())));
    }

    private void loadCursuri() {
        runSafely(() -> cursList.setItems(FXCollections.observableArrayList(cursDAO.findAll())));
    }

    private void export(Stage stage, String defaultName, String content) throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName(defaultName);
        File file = chooser.showSaveDialog(stage);
        if (file == null) {
            return;
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        statusLabel.setText("Export realizat: " + file.getAbsolutePath());
    }

    private boolean confirmDelete(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText("Confirmare necesara");
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private <T> T requireSelected(ListView<T> list, String message) throws ValidationException {
        T selected = list.getSelectionModel().getSelectedItem();
        if (selected == null) {
            throw new ValidationException(message);
        }
        return selected;
    }

    private int parseId(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(text.trim());
    }

    private double parsePrice(String text) throws ValidationException {
        try {
            return Double.parseDouble(text.trim());
        } catch (Exception ex) {
            throw new ValidationException("Pretul trebuie sa fie un numar valid.");
        }
    }

    private LocalDate parseDateOrToday(String text) throws ValidationException {
        if (text == null || text.trim().isEmpty()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(text.trim());
        } catch (Exception ex) {
            throw new ValidationException("Data trebuie să fie în formatul 2004-05-21.");
        }
    }

    private String hiddenEmail(String prefix, int id) {
        int safeId = id > 0 ? id : (int) (System.currentTimeMillis() % 1_000_000);
        return prefix + safeId + "@intern.local";
    }

    private String blankDefault(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private void runSafely(CheckedRunnable action) {
        try {
            action.run();
        } catch (Exception ex) {
            statusLabel.setText("Eroare: " + ex.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Eroare");
            alert.setHeaderText("Operația nu a reușit");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private interface CheckedRunnable {
        void run() throws Exception;
    }
}
