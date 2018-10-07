package hackuiowa.app;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;

public class MainApp extends Application {
    private Stage primaryStage;
    private Scene current;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent menu = FXMLLoader.load(getClass().getClassLoader().getResource("menu.fxml"));
        current = new Scene(menu);

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("MIDI Music");
        this.primaryStage.setScene(current);
        this.primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
