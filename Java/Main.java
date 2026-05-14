
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

// Define the main class that extends Application to create a JavaFX application
public class Main extends Application {

    // Override the start method, which is called when the application is launched
    // This method receives the primary stage (main window) as a parameter
    @Override
    public void start(Stage primaryStage) {

        // Create a new Label control with the text "Hello, JavaFX!"
        // Labels are used to display non-editable text in the UI
        Label label = new Label("Hello, JavaFX!");

        // Create a StackPane layout container
        // StackPane arranges its children in a stack, one on top of the other
        // It's useful for centering content or layering elements
        StackPane root = new StackPane();

        // Add the label to the StackPane's children list
        // This places the label inside the layout container
        root.getChildren().add(label);

        // Create a new Scene with the StackPane as the root node
        // The Scene has a width of 300 pixels and height of 200 pixels
        // A Scene contains the UI elements and is placed inside a Stage
        Scene scene = new Scene(root, 300, 200);

        // Set the created scene on the primary stage
        // This associates the UI content with the main window
        primaryStage.setScene(scene);

        // Set the title of the primary stage (window title bar)
        // This text will appear in the window's title bar
        primaryStage.setTitle("My JavaFX Window");

        // Make the primary stage visible by calling show()
        // This displays the window on the screen
        primaryStage.show();
    }

    // The main method is the entry point for the Java application
    // In JavaFX, we call launch() to start the JavaFX application lifecycle
    public static void main(String[] args) {
        // Launch the JavaFX application
        // This method starts the JavaFX runtime and calls the start() method
        launch(args);
    }
}