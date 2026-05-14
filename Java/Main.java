
// Import the Application class, which is the base class for all JavaFX applications
import javafx.application.Application;

// Import Scene class, which represents the content of a window in JavaFX
import javafx.scene.Scene;

// Import Label class, which is a UI control that displays text
import javafx.scene.control.Label;

// Import StackPane class, which is a layout container that stacks its children on top of each other
import javafx.scene.layout.StackPane;

// Import Stage class, which represents the main window of the application
import javafx.stage.Stage;

// Import SQL Server JDBC classes
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

// Define the main class that extends Application to create a JavaFX application
public class Main extends Application {

    // Database connection details
    private static final String DB_URL = "jdbc:sqlserver://Niku\\SQLEXPRESS;databaseName=Cursuri_Online;integratedSecurity=true;encrypt=true;trustServerCertificate=true";
    private Connection connection;

    // Override the start method, which is called when the application is launched
    // This method receives the primary stage (main window) as a parameter
    @Override
    public void start(Stage primaryStage) {

        // Try to connect to the database
        boolean dbConnected = connectToDatabase();

        // Create a new Label control with connection status
        String statusText = dbConnected ? "Connected to SQL Server Database!" : "Failed to connect to database";
        Label label = new Label(statusText);

        // Create a StackPane layout container
        // StackPane arranges its children in a stack, one on top of the other
        // It's useful for centering content or layering elements
        StackPane root = new StackPane();

        // Add the label to the StackPane's children list
        // This places the label inside the layout container
        root.getChildren().add(label);

        // Create a new Scene with the StackPane as the root node
        // The Scene has a width of 400 pixels and height of 200 pixels
        // A Scene contains the UI elements and is placed inside a Stage
        Scene scene = new Scene(root, 400, 200);

        // Set the created scene on the primary stage
        // This associates the UI content with the main window
        primaryStage.setScene(scene);

        // Set the title of the primary stage (window title bar)
        // This text will appear in the window's title bar
        primaryStage.setTitle("Practica II - Cursuri Online");

        // Make the primary stage visible by calling show()
        // This displays the window on the screen
        primaryStage.show();
    }

    /**
     * Connects to the SQL Server database using Windows Authentication
     * @return true if connection successful, false otherwise
     */
    private boolean connectToDatabase() {
        try {
            // Load the SQL Server JDBC driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Establish the connection
            System.out.println("Attempting to connect to SQL Server...");
            System.out.println("Server: Niku\\SQLEXPRESS");
            System.out.println("Database: Cursuri_Online");
            System.out.println("Authentication: Windows Authentication");
            System.out.println("Connection URL: " + DB_URL);

            connection = DriverManager.getConnection(DB_URL);

            if (connection != null) {
                System.out.println("Successfully connected to SQL Server database!");

                // Test the connection by executing a simple query
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT @@VERSION AS version");

                if (rs.next()) {
                    String version = rs.getString("version");
                    System.out.println("SQL Server Version: " + version.substring(0, Math.min(50, version.length())) + "...");
                }

                rs.close();
                stmt.close();

                return true;
            }

        } catch (ClassNotFoundException e) {
            System.err.println("SQL Server JDBC Driver not found or incompatible!");
            System.err.println("Make sure mssql-jdbc.jar is in the Java folder");
            System.err.println("Current classpath should include: ..\\mssql-jdbc.jar");
            System.err.println("");
            System.err.println("IMPORTANT: The JDBC driver must be compatible with JDK 25!");
            System.err.println("- Download version 12.6.3 or newer");
            System.err.println("- Choose jre17 or jre21 version (NOT jre11)");
            System.err.println("- From: https://learn.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server");
            System.err.println("- The old jre11 version does NOT work with JDK 25");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to SQL Server database!");
            System.err.println("Error: " + e.getMessage());

            // Provide specific guidance based on error
            if (e.getMessage().contains("Cannot open database")) {
                System.err.println("");
                System.err.println("SOLUTION: Database 'Cursuri_Online' does not exist!");
                System.err.println("1. Open SQL Server Management Studio (SSMS)");
                System.err.println("2. Connect to: Niku\\SQLEXPRESS");
                System.err.println("3. Create database: Cursuri_Online");
                System.err.println("4. Run the SQL script: SQL\\SQL Demo Files\\All-in-One\\structure-and-inserts-in-order.sql");
            } else if (e.getMessage().contains("Login failed")) {
                System.err.println("");
                System.err.println("SOLUTION: Authentication failed!");
                System.err.println("Make sure SQL Server is configured for Windows Authentication");
            } else if (e.getMessage().contains("TCP/IP")) {
                System.err.println("");
                System.err.println("SOLUTION: SQL Server network configuration issue!");
                System.err.println("Enable TCP/IP in SQL Server Configuration Manager");
            }

            e.printStackTrace();
        }

        return false;
    }

    // The main method is the entry point for the Java application
    // In JavaFX, we call launch() to start the JavaFX application lifecycle
    public static void main(String[] args) {
        // Launch the JavaFX application
        // This method starts the JavaFX runtime and calls the start() method
        launch(args);
    }
}