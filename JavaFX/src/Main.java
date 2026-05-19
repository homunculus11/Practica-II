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
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class Main extends Application {
    private Database database;
    private StudentDAO studentDAO;
    private ProfesorDAO profesorDAO;
    private CursDAO cursDAO;
    private InrolareDAO inrolareDAO;
    private DatabaseTableDAO tableDAO;

    private final ListView<Student> studentList = new ListView<>();
    private final ListView<Profesor> profesorList = new ListView<>();
    private final ListView<Curs> cursList = new ListView<>();
    private final TextArea reportArea = new TextArea();
    private final Map<String, TableView<TableRowData>> tableViews = new LinkedHashMap<>();
    private final Map<String, Tab> tableTabs = new LinkedHashMap<>();
    private final Label coursesMetric = new Label("-");
    private final Label studentsMetric = new Label("-");
    private final Label enrollmentsMetric = new Label("-");
    private Image appLogo;
    private Label statusLabel;
    private Circle databaseStatusLight;
    private BorderPane appRoot;
    private Tab studentTab;
    private Tab profesorTab;
    private Tab cursTab;
    private Tab raioaneLocalitatiTab;
    private int raioaneCount = -1;
    private int localitatiCount = -1;
    private String activeRaioaneLocalitatiTable = "Raioane";
    private boolean darkMode;
    private double windowDragOffsetX;
    private double windowDragOffsetY;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        DatabaseConfig config = DatabaseConfig.load();
        database = new Database(config);
        studentDAO = new StudentDAO(database);
        profesorDAO = new ProfesorDAO(database);
        cursDAO = new CursDAO(database);
        inrolareDAO = new InrolareDAO(database);
        tableDAO = new DatabaseTableDAO(database);
        appLogo = new Image(getClass().getResourceAsStream("app-logo.png"));

        statusLabel = new Label("Conectare: " + config.describe());
        statusLabel.getStyleClass().add("status-label");

        configureLists();

        TabPane tabs = new TabPane();
        tabs.getStyleClass().add("main-tabs");
        studentTab = createStudentTab();
        profesorTab = createProfesorTab();
        cursTab = createCursTab();
        tabs.getTabs().add(studentTab);
        tabs.getTabs().add(profesorTab);
        tabs.getTabs().add(cursTab);
        addDatabaseTableTabs(tabs);
        tabs.getTabs().add(createReportTab(primaryStage));
        tabs.getTabs().forEach(tab -> tab.setClosable(false));
        tabs.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, selectedTab) -> updateStatusForSelectedTab(selectedTab));

        appRoot = new BorderPane();
        appRoot.getStyleClass().add("app-root");
        appRoot.setTop(new VBox(createWindowTitleBar(primaryStage), createTopBar()));
        appRoot.setCenter(tabs);
        appRoot.setBottom(statusLabel);
        BorderPane.setMargin(statusLabel, new Insets(0, 24, 18, 24));

        Scene scene = new Scene(appRoot, 1180, 760);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setMinWidth(980);
        primaryStage.setMinHeight(640);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Practica II - Cursuri Online");
        primaryStage.getIcons().add(appLogo);
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
                        "ID " + item.getId() + "  |  Cursuri inscrise: " + item.getNumarInrolari()));
            }
        });

        profesorList.setCellFactory(list -> new ListCell<Profesor>() {
            @Override
            protected void updateItem(Profesor item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                setGraphic(empty || item == null ? null : dataRow(
                        item.getNume(),
                        "Certificare: " + item.getSpecializare(),
                        "ID " + item.getId() + "  |  Rating " + String.format("%.1f", item.getRating())));
            }
        });

        cursList.setCellFactory(list -> new ListCell<Curs>() {
            @Override
            protected void updateItem(Curs item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                String price = String.format("%.2f lei", item.getPret());
                String profesor = item.getProfesor() == null ? "Profesor neatribuit" : item.getProfesor().getNume();
                setGraphic(dataRow(
                        item.getTitlu(),
                        profesor,
                        "ID " + item.getId() + "  |  " + item.getDescriere() + "  |  " + price));
            }
        });
    }

    private HBox createWindowTitleBar(Stage stage) {
        ImageView icon = new ImageView(appLogo);
        icon.getStyleClass().add("window-icon");
        icon.setFitWidth(18);
        icon.setFitHeight(18);
        icon.setPreserveRatio(true);
        icon.setSmooth(true);

        Label title = new Label("Practica II - Cursuri Online");
        title.getStyleClass().add("window-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button minimize = titleBarButton("_");
        minimize.setOnAction(event -> stage.setIconified(true));

        Button maximize = titleBarButton("[]");
        maximize.setOnAction(event -> stage.setMaximized(!stage.isMaximized()));

        Button close = titleBarButton("X");
        close.getStyleClass().add("window-close-button");
        close.setOnAction(event -> stage.close());

        HBox titleBar = new HBox(8, icon, title, spacer, minimize, maximize, close);
        titleBar.getStyleClass().add("window-title-bar");
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setOnMousePressed(event -> {
            windowDragOffsetX = event.getSceneX();
            windowDragOffsetY = event.getSceneY();
        });
        titleBar.setOnMouseDragged(event -> {
            if (!stage.isMaximized()) {
                stage.setX(event.getScreenX() - windowDragOffsetX);
                stage.setY(event.getScreenY() - windowDragOffsetY);
            }
        });
        titleBar.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                stage.setMaximized(!stage.isMaximized());
            }
        });
        return titleBar;
    }

    private Button titleBarButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("window-title-button");
        button.setFocusTraversable(false);
        return button;
    }

    private HBox createTopBar() {
        Label title = new Label("Cursuri Online");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label("Administrare cursuri, profesori, studenți, rapoarte și export");
        subtitle.getStyleClass().add("app-subtitle");

        VBox copy = new VBox(4, title, subtitle);

        databaseStatusLight = new Circle(4);
        databaseStatusLight.getStyleClass().addAll("db-status-light", "db-disconnected");
        databaseStatusLight.setManaged(true);

        Label db = new Label(database.getConfig().describe());
        db.getStyleClass().add("db-pill-text");

        HBox dbPill = new HBox(8, databaseStatusLight, db);
        dbPill.getStyleClass().add("db-pill");
        dbPill.setAlignment(Pos.CENTER_LEFT);

        Button testButton = new Button("Verifica baza de date");
        testButton.getStyleClass().addAll("button", "secondary-button");
        testButton.setOnAction(event -> verifyDatabaseWithDialog());

        Button themeButton = new Button("\u263E");
        themeButton.getStyleClass().addAll("button", "theme-toggle");
        themeButton.setTooltip(new Tooltip("Schimba tema"));
        themeButton.setOnAction(event -> toggleTheme(themeButton));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(18, copy, spacer, dbPill, testButton, themeButton);
        bar.getStyleClass().add("hero-bar");
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    private void toggleTheme(Button themeButton) {
        darkMode = !darkMode;
        if (darkMode) {
            appRoot.getStyleClass().add("dark-mode");
            themeButton.setText("\u2600");
            themeButton.setTooltip(new Tooltip("Schimba la light mode"));
        } else {
            appRoot.getStyleClass().remove("dark-mode");
            themeButton.setText("\u263E");
            themeButton.setTooltip(new Tooltip("Schimba la dark mode"));
        }
    }

    private void setDatabaseConnected(boolean connected) {
        if (databaseStatusLight == null) {
            return;
        }

        databaseStatusLight.getStyleClass().removeAll("db-connected", "db-disconnected");
        databaseStatusLight.getStyleClass().add(connected ? "db-connected" : "db-disconnected");
        databaseStatusLight.setAccessibleText(connected ? "Conectat la baza de date" : "Deconectat de la baza de date");
    }

    private Tab createStudentTab() {
        Button refresh = secondary("Toate inregistrarile");
        refresh.setOnAction(event -> showConfirmAction("Reincarca studenti", "Reincarci lista completa de studenti?", this::loadStudents));
        Button searchButton = secondary("Cauta");
        searchButton.setOnAction(event -> showStudentSearchDialog());
        Button add = primary("Adauga");
        add.setOnAction(event -> showStudentDialog(null));
        Button update = secondary("Modifica");
        update.setOnAction(event -> runSafely(() -> showStudentDialog(requireSelected(studentList, "Selecteaza un student pentru modificare."))));
        Button delete = danger("Sterge");
        delete.setOnAction(event -> runSafely(() -> {
            Student selected = requireSelected(studentList, "Selecteaza un student pentru stergere.");
            if (confirmDelete("Stergere student", "Sigur vrei sa stergi studentul selectat?")) {
                studentDAO.delete(selected.getId());
                loadStudents();
            }
        }));

        Tab tab = new Tab("Studenti", crudLayout(
                "Studenti",
                "Alege o optiune din bara de sus. Formularele se deschid in ferestre mici.",
                studentList,
                actionPanel("Studenti", refresh, update, add, delete, searchButton)));
        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                updateStudentStatus();
            }
        });
        return tab;
    }
    private Tab createProfesorTab() {
        Button refresh = secondary("Toate inregistrarile");
        refresh.setOnAction(event -> showConfirmAction("Reincarca profesori", "Reincarci lista completa de profesori?", this::loadProfesori));
        Button searchButton = secondary("Cauta");
        searchButton.setOnAction(event -> showProfesorSearchDialog());
        Button add = primary("Adauga");
        add.setOnAction(event -> showProfesorDialog(null));
        Button update = secondary("Modifica");
        update.setOnAction(event -> runSafely(() -> showProfesorDialog(requireSelected(profesorList, "Selecteaza un profesor pentru modificare."))));
        Button delete = danger("Sterge");
        delete.setOnAction(event -> runSafely(() -> {
            Profesor selected = requireSelected(profesorList, "Selecteaza un profesor pentru stergere.");
            if (confirmDelete("Stergere profesor", "Sigur vrei sa stergi profesorul selectat?")) {
                profesorDAO.delete(selected.getId());
                loadProfesori();
            }
        }));

        Tab tab = new Tab("Profesori", crudLayout(
                "Profesori",
                "Alege o optiune din bara de sus. Modificarea foloseste profesorul selectat.",
                profesorList,
                actionPanel("Profesori", refresh, update, add, delete, searchButton)));
        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                updateProfesorStatus();
            }
        });
        return tab;
    }
    private Tab createCursTab() {
        Button refresh = secondary("Toate inregistrarile");
        refresh.setOnAction(event -> showConfirmAction("Reincarca cursuri", "Reincarci lista completa de cursuri?", this::loadCursuri));
        Button searchButton = secondary("Cauta / filtreaza");
        searchButton.setOnAction(event -> showCursSearchDialog());
        Button add = primary("Adauga");
        add.setOnAction(event -> showCursDialog(null));
        Button update = secondary("Modifica");
        update.setOnAction(event -> runSafely(() -> showCursDialog(requireSelected(cursList, "Selecteaza un curs pentru modificare."))));
        Button delete = danger("Sterge");
        delete.setOnAction(event -> runSafely(() -> {
            Curs selected = requireSelected(cursList, "Selecteaza un curs pentru stergere.");
            if (confirmDelete("Stergere curs", "Sigur vrei sa stergi cursul selectat?")) {
                cursDAO.delete(selected.getId());
                loadCursuri();
            }
        }));

        Tab tab = new Tab("Cursuri", crudLayout(
                "Cursuri",
                "Alege o optiune din bara de sus. Modificarea foloseste cursul selectat.",
                cursList,
                actionPanel("Cursuri", refresh, update, add, delete, searchButton)));
        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                updateCursStatus();
            }
        });
        return tab;
    }
    private Tab createReportTab(Stage stage) {
        reportArea.setEditable(false);
        reportArea.setWrapText(false);
        reportArea.getStyleClass().add("report-area");

        ComboBox<String> reportType = new ComboBox<>(FXCollections.observableArrayList(
                "Sumar general",
                "Studenti per curs",
                "Cursuri populare",
                "Catalog cursuri",
                "Preturi cursuri",
                "Inrolari studenti",
                "Toate rapoartele"));
        reportType.setValue("Sumar general");
        reportType.getStyleClass().add("input");

        Button selectedReport = primary("Genereaza raportul ales");
        selectedReport.setOnAction(event -> runSafely(() -> {
            RaportService service = new RaportService(cursDAO.findAll(), inrolareDAO.findAll(), studentDAO.findAll());
            reportArea.setText(service.genereaza(reportType.getValue()));
            updateMetrics();
        }));
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
                sectionHeader("Rapoarte și export", "Alege raportul dorit, generează analiza și salvează datele în fișiere CSV sau TXT."),
                metrics,
                reportSelector(reportType, selectedReport, allReports),
                reportActions(exportCsv, exportTxt),
                reportArea);
        panel.getStyleClass().add("content-panel");
        VBox.setVgrow(reportArea, Priority.ALWAYS);

        VBox content = new VBox(panel);
        content.getStyleClass().add("page");
        Tab tab = new Tab("Rapoarte", content);
        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                updateReportStatus();
            }
        });
        return tab;
    }

    private void addDatabaseTableTabs(TabPane tabs) {
        for (String tableName : tableDAO.tableNames()) {
            if ("Studenti".equals(tableName) || "Profesori".equals(tableName) || "Cursuri".equals(tableName)) {
                continue;
            }
            if ("Raioane".equals(tableName)) {
                tabs.getTabs().add(createRaioaneLocalitatiTab());
                continue;
            }
            if ("Localitati".equals(tableName)) {
                continue;
            }

            TableView<TableRowData> tableView = new TableView<>();
            tableView.getStyleClass().add("data-table");
            tableViews.put(tableName, tableView);

            Button refresh = secondary("Reincarca tabelul");
            refresh.setOnAction(event -> loadDatabaseTable(tableName));

            VBox panel = new VBox(12,
                    sectionHeader(tableName, "Vizualizare directa din tabela SQL Server."),
                    refresh,
                    tableView);
            panel.getStyleClass().add("content-panel");
            VBox.setVgrow(tableView, Priority.ALWAYS);

            VBox page = new VBox(panel);
            page.getStyleClass().add("page");
            VBox.setVgrow(panel, Priority.ALWAYS);

            Tab tab = new Tab(tableName, page);
            tableTabs.put(tableName, tab);
            tab.setOnSelectionChanged(event -> {
                if (tab.isSelected() && tableView.getColumns().isEmpty()) {
                    loadDatabaseTable(tableName);
                }
            });
            tabs.getTabs().add(tab);
        }
    }

    private Tab createRaioaneLocalitatiTab() {
        TableView<TableRowData> raioaneTable = new TableView<>();
        raioaneTable.getStyleClass().add("data-table");
        tableViews.put("Raioane", raioaneTable);

        TableView<TableRowData> localitatiTable = new TableView<>();
        localitatiTable.getStyleClass().add("data-table");
        tableViews.put("Localitati", localitatiTable);

        VBox raioanePanel = databaseTablePanel("Raioane", "Lista raioanelor disponibile in baza de date.", raioaneTable);
        VBox localitatiPanel = databaseTablePanel("Localitati", "Localitati legate de raioane.", localitatiTable);
        VBox[] panels = { raioanePanel, localitatiPanel };
        String[] tableNames = { "Raioane", "Localitati" };
        int[] currentPanel = { 0 };

        Button previous = secondary("<");
        previous.setTooltip(new Tooltip("Afiseaza Raioane"));
        previous.setMinWidth(52);

        Button next = secondary(">");
        next.setTooltip(new Tooltip("Afiseaza Localitati"));
        next.setMinWidth(52);

        Label activeTable = new Label("Raioane");
        activeTable.getStyleClass().add("panel-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox navigator = new HBox(10, previous, next, spacer, activeTable);
        navigator.getStyleClass().add("options-bar");
        navigator.setAlignment(Pos.CENTER_LEFT);

        Runnable updateVisibleTable = () -> {
            for (int i = 0; i < panels.length; i++) {
                boolean selected = i == currentPanel[0];
                panels[i].setVisible(selected);
                panels[i].setManaged(selected);
            }

            previous.setDisable(currentPanel[0] == 0);
            next.setDisable(currentPanel[0] == panels.length - 1);
            activeTable.setText(tableNames[currentPanel[0]]);
            activeRaioaneLocalitatiTable = tableNames[currentPanel[0]];
        };

        Runnable loadVisibleTable = () -> {
            String tableName = tableNames[currentPanel[0]];
            TableView<TableRowData> tableView = tableViews.get(tableName);
            if (tableView.getColumns().isEmpty()) {
                loadDatabaseTable(tableName);
            } else {
                updateLoadedTableStatus(tableName);
            }
        };

        previous.setOnAction(event -> {
            if (currentPanel[0] > 0) {
                currentPanel[0]--;
                updateVisibleTable.run();
                loadVisibleTable.run();
            }
        });

        next.setOnAction(event -> {
            if (currentPanel[0] < panels.length - 1) {
                currentPanel[0]++;
                updateVisibleTable.run();
                loadVisibleTable.run();
            }
        });

        updateVisibleTable.run();

        VBox page = new VBox(14, navigator, raioanePanel, localitatiPanel);
        page.getStyleClass().add("page");
        VBox.setVgrow(raioanePanel, Priority.ALWAYS);
        VBox.setVgrow(localitatiPanel, Priority.ALWAYS);

        raioaneLocalitatiTab = new Tab("Raioane si Localitati", page);
        raioaneLocalitatiTab.setOnSelectionChanged(event -> {
            if (raioaneLocalitatiTab.isSelected()) {
                updateVisibleTable.run();
                loadVisibleTable.run();
            }
        });
        return raioaneLocalitatiTab;
    }

    private VBox databaseTablePanel(String tableName, String description, TableView<TableRowData> tableView) {
        Button refresh = secondary("Reincarca " + tableName);
        refresh.setOnAction(event -> loadDatabaseTable(tableName));

        VBox panel = new VBox(12,
                sectionHeader(tableName, description),
                refresh,
                tableView);
        panel.getStyleClass().add("content-panel");
        VBox.setVgrow(tableView, Priority.ALWAYS);
        return panel;
    }

    private void loadDatabaseTable(String tableName) {
        runSafely(() -> {
            TableData tableData = tableDAO.findAll(tableName);
            TableView<TableRowData> tableView = tableViews.get(tableName);
            tableView.getColumns().clear();

            for (String columnName : tableData.getColumns()) {
                TableColumn<TableRowData, String> column = new TableColumn<>(columnName);
                column.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(columnName)));
                column.setPrefWidth(Math.max(120, columnName.length() * 11));
                tableView.getColumns().add(column);
            }

            tableView.setItems(FXCollections.observableArrayList(tableData.getRows()));
            updateTableTabCount(tableName, tableData.getRows().size());
            updateLoadedTableStatus(tableName);
        });
    }

    private void updateLoadedTableStatus(String tableName) {
        TableView<TableRowData> tableView = tableViews.get(tableName);
        int rows = tableView == null ? 0 : tableView.getItems().size();
        statusLabel.setText(tableName + ": " + rows + " randuri incarcate");
    }

    private void updateTableTabCount(String tableName, int count) {
        if ("Raioane".equals(tableName)) {
            raioaneCount = count;
            updateRaioaneLocalitatiTabText();
            return;
        }
        if ("Localitati".equals(tableName)) {
            localitatiCount = count;
            updateRaioaneLocalitatiTabText();
            return;
        }

        Tab tab = tableTabs.get(tableName);
        if (tab != null) {
            setTabCount(tab, tableName, count);
        }
    }

    private void updateRaioaneLocalitatiTabText() {
        if (raioaneLocalitatiTab == null) {
            return;
        }
        if (raioaneCount >= 0 && localitatiCount >= 0) {
            setTabCount(raioaneLocalitatiTab, "Raioane si Localitati", raioaneCount + localitatiCount);
        } else if (raioaneCount >= 0) {
            setTabCount(raioaneLocalitatiTab, "Raioane si Localitati", raioaneCount);
        } else if (localitatiCount >= 0) {
            setTabCount(raioaneLocalitatiTab, "Raioane si Localitati", localitatiCount);
        }
    }

    private void setTabCount(Tab tab, String title, int count) {
        tab.setText(title + " (" + count + ")");
    }

    private VBox crudLayout(String title, String description, Node list, HBox actions) {
        VBox data = new VBox(12, sectionHeader(title, description), actions, panelTitle("Date"), list);
        data.getStyleClass().add("content-panel");
        VBox.setVgrow(list, Priority.ALWAYS);

        VBox page = new VBox(data);
        page.getStyleClass().add("page");
        VBox.setVgrow(data, Priority.ALWAYS);
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

    private HBox actionPanel(String entity, Button refresh, Button update, Button add, Button delete, Button search) {
        HBox panel = new HBox(10, refresh, update, add, delete, search);
        panel.getStyleClass().add("options-bar");
        panel.setAlignment(Pos.CENTER_LEFT);
        for (Button button : new Button[] { refresh, update, add, delete, search }) {
            button.setMinWidth(button == refresh ? 170 : 120);
        }
        return panel;
    }

    private void showStudentSearchDialog() {
        TextField search = field("Nume student");
        showOptionDialog("Cauta student", form(labeled("Cautare", search, "Scrie numele sau prenumele studentului.")), () -> {
            var rows = studentDAO.search(search.getText());
            studentList.setItems(FXCollections.observableArrayList(rows));
            setTabCount(studentTab, "Studenti", rows.size());
            statusLabel.setText("Cautare studenti: " + search.getText());
        });
    }

    private void showStudentDialog(Student selected) {
        TextField id = field("Lasati gol pentru adaugare noua");
        TextField nume = field("Exemplu: Popescu Ion");
        TextField dataNasterii = field("Exemplu: 2004-05-21");
        if (selected != null) {
            id.setText(String.valueOf(selected.getId()));
            id.setDisable(true);
            nume.setText(selected.getNume());
            dataNasterii.setText(String.valueOf(selected.getDataInrolare()));
        }

        showOptionDialog(selected == null ? "Adauga student" : "Modifica student",
                form(
                        labeled("ID", id, "Optional la adaugare."),
                        labeled("Nume complet", nume, "Camp obligatoriu."),
                        labeled("Data nasterii", dataNasterii, "Format: 2004-05-21.")),
                () -> {
                    Validator.required(nume.getText(), "Nume");
                    int studentId = selected == null ? parseId(id.getText()) : selected.getId();
                    LocalDate date = parseDateOrToday(dataNasterii.getText());
                    Student student = new Student(studentId, nume.getText().trim(), hiddenEmail("student", studentId), "secret1", date);
                    if (selected == null) {
                        Student saved = studentDAO.save(student);
                        statusLabel.setText("Student salvat: " + saved.getNume());
                    } else {
                        studentDAO.update(student);
                        statusLabel.setText("Student modificat: " + student.getNume());
                    }
                    loadStudents();
                });
    }

    private void showProfesorSearchDialog() {
        TextField search = field("Nume sau certificare");
        showOptionDialog("Cauta profesor", form(labeled("Cautare", search, "Scrie nume, prenume sau certificare.")), () -> {
            var rows = profesorDAO.search(search.getText());
            profesorList.setItems(FXCollections.observableArrayList(rows));
            setTabCount(profesorTab, "Profesori", rows.size());
            statusLabel.setText("Cautare profesori: " + search.getText());
        });
    }

    private void showProfesorDialog(Profesor selected) {
        TextField id = field("Lasati gol pentru adaugare noua");
        TextField nume = field("Exemplu: Ionescu Maria");
        ComboBox<String> certificare = new ComboBox<>(FXCollections.observableArrayList(
                "Fara grad didactic", "Grad didactic II", "Grad didactic I", "Grad didactic superior"));
        certificare.setValue("Fara grad didactic");
        certificare.getStyleClass().add("input");
        if (selected != null) {
            id.setText(String.valueOf(selected.getId()));
            id.setDisable(true);
            nume.setText(selected.getNume());
            certificare.setValue(selected.getSpecializare());
        }

        showOptionDialog(selected == null ? "Adauga profesor" : "Modifica profesor",
                form(
                        labeled("ID", id, "Optional la adaugare."),
                        labeled("Nume complet", nume, "Camp obligatoriu."),
                        labeled("Certificare", certificare, "Alege certificarea profesorului.")),
                () -> {
                    Validator.required(nume.getText(), "Nume");
                    int profesorId = selected == null ? parseId(id.getText()) : selected.getId();
                    Profesor profesor = new Profesor(profesorId, nume.getText().trim(), hiddenEmail("profesor", profesorId), "secret1", certificare.getValue(), 4.5);
                    if (selected == null) {
                        profesorDAO.save(profesor);
                        statusLabel.setText("Profesor adaugat: " + profesor.getNume());
                    } else {
                        profesorDAO.update(profesor);
                        statusLabel.setText("Profesor modificat: " + profesor.getNume());
                    }
                    loadProfesori();
                });
    }

    private void showCursSearchDialog() {
        TextField search = field("Denumire curs");
        ComboBox<String> tip = new ComboBox<>(FXCollections.observableArrayList("Toate", "Online", "Offline", "Hibrid"));
        tip.setValue("Toate");
        tip.getStyleClass().add("input");
        showOptionDialog("Cauta sau filtreaza cursuri",
                form(
                        labeled("Cautare", search, "Scrie o parte din denumire."),
                        labeled("Tip predare", tip, "Alege filtrul pentru tip.")),
                () -> {
                    var rows = cursDAO.search(search.getText(), tip.getValue());
                    cursList.setItems(FXCollections.observableArrayList(rows));
                    setTabCount(cursTab, "Cursuri", rows.size());
                    statusLabel.setText("Cautare cursuri: " + search.getText());
                });
    }

    private void showCursDialog(Curs selected) {
        TextField id = field("Lasati gol pentru curs nou");
        TextField titlu = field("Exemplu: Introducere in Java");
        TextField pret = field("Exemplu: 1200");
        TextField coordonator = field("Numele profesorului coordonator");
        ComboBox<String> tip = new ComboBox<>(FXCollections.observableArrayList("Online", "Offline", "Hibrid"));
        tip.setValue("Online");
        tip.getStyleClass().add("input");
        if (selected != null) {
            id.setText(String.valueOf(selected.getId()));
            id.setDisable(true);
            titlu.setText(selected.getTitlu());
            pret.setText(String.valueOf(selected.getPret()));
            coordonator.setText(selected.getProfesor().getNume());
            tip.setValue(selected.getDescriere());
        }

        showOptionDialog(selected == null ? "Adauga curs" : "Modifica curs",
                form(
                        labeled("ID", id, "Optional la adaugare."),
                        labeled("Denumire curs", titlu, "Camp obligatoriu."),
                        labeled("Pret", pret, "Nu poate fi negativ."),
                        labeled("Coordonator", coordonator, "Numele profesorului responsabil."),
                        labeled("Tip predare", tip, "Online, Offline sau Hibrid.")),
                () -> {
                    Validator.required(titlu.getText(), "Denumire curs");
                    double price = parsePrice(pret.getText());
                    Validator.nonNegative(price, "Pret");
                    int cursId = selected == null ? parseId(id.getText()) : selected.getId();
                    Profesor profesor = new Profesor(0, blankDefault(coordonator.getText(), "Profesor neatribuit"), "coordonator@cursuri.local", "secret1", "Coordonator", 4.5);
                    Curs curs = new Curs(cursId, titlu.getText().trim(), tip.getValue(), price, NivelCurs.MEDIU, selected == null ? 90 : selected.getDurata(), profesor);
                    if (selected == null) {
                        cursDAO.save(curs);
                        statusLabel.setText("Curs adaugat: " + curs.getTitlu());
                    } else {
                        cursDAO.update(curs);
                        statusLabel.setText("Curs modificat: " + curs.getTitlu());
                    }
                    loadCursuri();
                });
    }

    private void showConfirmAction(String title, String message, CheckedRunnable action) {
        if (confirmDelete(title, message)) {
            runSafely(action);
        }
    }

    private void showOptionDialog(String title, Node content, CheckedRunnable action) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setMinWidth(520);
        stylePopup(dialog);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            runSafely(action);
        }
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

    private HBox reportSelector(ComboBox<String> reportType, Button selectedReport, Button allReports) {
        reportType.setMinWidth(240);
        selectedReport.setMinWidth(210);
        allReports.setMinWidth(190);

        HBox box = new HBox(10, reportType, selectedReport, allReports);
        box.getStyleClass().add("action-row");
        box.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(reportType, Priority.ALWAYS);
        return box;
    }

    private void testAndLoad() {
        runSafely(() -> {
            if (!database.testConnection()) {
                setDatabaseConnected(false);
                throw new SQLException("Conexiunea la SQL Server a eșuat. Verifică db.properties și baza de date din SSMS.");
            }
            setDatabaseConnected(true);
            statusLabel.setText("Conectat la " + database.getConfig().describe());
        });
        loadStudents();
        loadProfesori();
        loadCursuri();
        loadDatabaseTableCounts();
        runSafely(() -> updateMetrics());
    }

    private void verifyDatabaseWithDialog() {
        if (database.testConnection()) {
            setDatabaseConnected(true);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Baza de date");
            alert.setHeaderText("Conexiune activa");
            alert.setContentText("Esti conectat la " + database.getConfig().describe() + ".");
            stylePopup(alert);
            alert.showAndWait();
            testAndLoad();
            return;
        }

        setDatabaseConnected(false);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Baza de date");
        alert.setHeaderText("Baza de date nu a fost gasita");
        alert.setContentText("Nu se poate realiza conexiunea la " + database.getConfig().describe()
                + ". Verifica SQL Server, db.properties si baza de date din SSMS.");
        stylePopup(alert);
        alert.showAndWait();
    }

    private void updateMetrics() throws SQLException {
        coursesMetric.setText(String.valueOf(cursDAO.findAll().size()));
        studentsMetric.setText(String.valueOf(studentDAO.findAll().size()));
        enrollmentsMetric.setText(String.valueOf(inrolareDAO.findAll().size()));
    }

    private void loadStudents() {
        runSafely(() -> {
            var rows = studentDAO.findAll();
            studentList.setItems(FXCollections.observableArrayList(rows));
            setTabCount(studentTab, "Studenti", rows.size());
            if (studentTab.isSelected()) {
                updateStudentStatus();
            }
        });
    }

    private void loadProfesori() {
        runSafely(() -> {
            var rows = profesorDAO.findAll();
            profesorList.setItems(FXCollections.observableArrayList(rows));
            setTabCount(profesorTab, "Profesori", rows.size());
            if (profesorTab.isSelected()) {
                updateProfesorStatus();
            }
        });
    }

    private void loadCursuri() {
        runSafely(() -> {
            var rows = cursDAO.findAll();
            cursList.setItems(FXCollections.observableArrayList(rows));
            setTabCount(cursTab, "Cursuri", rows.size());
            if (cursTab.isSelected()) {
                updateCursStatus();
            }
        });
    }

    private void updateStudentStatus() {
        int students = studentList.getItems().size();
        double averageEnrollments = studentList.getItems().stream()
                .mapToInt(Student::getNumarInrolari)
                .average()
                .orElse(0);
        statusLabel.setText(String.format("Studenti: %d inregistrari | Medie cursuri inscrise: %.1f", students, averageEnrollments));
    }

    private void updateProfesorStatus() {
        int professors = profesorList.getItems().size();
        double averageRating = profesorList.getItems().stream()
                .mapToDouble(Profesor::getRating)
                .average()
                .orElse(0);
        statusLabel.setText(String.format("Profesori: %d inregistrari | Rating mediu: %.1f", professors, averageRating));
    }

    private void updateCursStatus() {
        int courses = cursList.getItems().size();
        double averagePrice = cursList.getItems().stream()
                .mapToDouble(Curs::getPret)
                .average()
                .orElse(0);
        statusLabel.setText(String.format("Cursuri: %d inregistrari | Pret mediu: %.2f lei", courses, averagePrice));
    }

    private void updateReportStatus() {
        String text = String.format("Rapoarte: %s cursuri | %s studenti | %s inrolari",
                coursesMetric.getText(),
                studentsMetric.getText(),
                enrollmentsMetric.getText());
        statusLabel.setText(text);
    }

    private void updateStatusForSelectedTab(Tab selectedTab) {
        if (selectedTab == null) {
            return;
        }

        if (selectedTab == studentTab) {
            updateStudentStatus();
            return;
        }
        if (selectedTab == profesorTab) {
            updateProfesorStatus();
            return;
        }
        if (selectedTab == cursTab) {
            updateCursStatus();
            return;
        }
        if (selectedTab == raioaneLocalitatiTab) {
            updateTableStatusOrLoad(activeRaioaneLocalitatiTable);
            return;
        }
        if ("Rapoarte".equals(selectedTab.getText())) {
            updateReportStatus();
            return;
        }

        for (Map.Entry<String, Tab> entry : tableTabs.entrySet()) {
            if (entry.getValue() == selectedTab) {
                updateTableStatusOrLoad(entry.getKey());
                return;
            }
        }
    }

    private void updateTableStatusOrLoad(String tableName) {
        TableView<TableRowData> tableView = tableViews.get(tableName);
        if (tableView == null) {
            return;
        }
        if (tableView.getColumns().isEmpty()) {
            loadDatabaseTable(tableName);
        } else {
            updateLoadedTableStatus(tableName);
        }
    }

    private void loadDatabaseTableCounts() {
        runSafely(() -> {
            for (String tableName : tableDAO.tableNames()) {
                if ("Studenti".equals(tableName) || "Profesori".equals(tableName) || "Cursuri".equals(tableName)) {
                    continue;
                }
                updateTableTabCount(tableName, tableDAO.countRows(tableName));
            }
        });
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

    private void stylePopup(Dialog<?> dialog) {
        dialog.initOwner(appRoot.getScene().getWindow());
        dialog.getDialogPane().getStyleClass().add("app-dialog");
        if (darkMode) {
            dialog.getDialogPane().getStyleClass().add("app-dialog-dark");
        }
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
    }

    private boolean confirmDelete(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText("Confirmare necesara");
        alert.setContentText(message);
        stylePopup(alert);
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
            stylePopup(alert);
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
