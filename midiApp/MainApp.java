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
        System.out.println("start");

        try {
            URL resource = getClass().getResource("/hackuiowa/midiViews/visual.fxml");

            System.out.println(resource.getPath());

            Parent root = FXMLLoader.load(resource);
            Scene scene = new Scene(root);

            this.primaryStage = primaryStage;
            this.primaryStage.setTitle("Music Maker");
            this.primaryStage.setScene(scene);
            this.primaryStage.show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // public void initRootLayout() {
    // try {
    // // Load root layout from fxml file.
    // FXMLLoader loader = new FXMLLoader();
    // loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
    // rootLayout = (BorderPane) loader.load();

    // // Show the scene containing the root layout.
    // Scene scene = new Scene(rootLayout);
    // primaryStage.setScene(scene);
    // primaryStage.show();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    // public void showPersonOverview() {
    // try {
    // // Load person overview.
    // FXMLLoader loader = new FXMLLoader();
    // loader.setLocation(MainApp.class.getResource("view/PersonOverview.fxml"));
    // AnchorPane personOverview = (AnchorPane) loader.load();

    // // Set person overview into the center of root layout.
    // rootLayout.setCenter(personOverview);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}