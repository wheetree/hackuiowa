package hackuiowa.midiApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/hackuiowa/midiViews/playing.fxml"));
            Scene scene = new Scene(root);

            this.primaryStage = primaryStage;
            this.primaryStage.setTitle("Music Maker");
            this.primaryStage.setScene(scene);
            this.primaryStage.show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}