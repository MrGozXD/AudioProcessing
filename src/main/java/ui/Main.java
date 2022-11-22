package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
   /** @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml")); //variante du xml
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);   //window dimension
        stage.setTitle("Hello Yannis!");   //window name
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
*/
    public void start(Stage primaryStage) {
        try {
            BorderPane root = new BorderPane();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Node createToolbar()
    {
        ToolBar toolbar = new ToolBar();
        toolbar.getItems().add(new Button("File"));
        toolbar.getItems().add(new Button("Edit"));
        toolbar.getItems().add(new Button("View"));
        toolbar.getItems().add(new Button("Help"));
        return toolbar;
    }
}