package hackuiowa.controllers;

import hackuiowa.controllers.MenuController;
import hackuiowa.controllers.PlayController;

import com.jfoenix.controls.JFXButton;

import java.io.IOException;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;

public class EndController {
    @FXML
    private JFXButton returnButton;

    @FXML
    private Label message;

    @FXML
    private Ellipse messageBackground;

    @FXML
    public void restartTrack() throws IOException {
        System.err.println("returning to track");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/play.fxml"));
        Parent select = loader.load();
        System.err.println("loaded play screen");

        Scene newScene = new Scene(select);
        Stage stage = (Stage) returnButton.getScene().getWindow();
        stage.setScene(newScene);
    }

    @FXML
    public void returnMenu() throws IOException {
        System.err.println("returning to menu");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/menu.fxml"));
        Parent select = loader.load();
        System.err.println("loaded menu screen");

        Scene newScene = new Scene(select);
        Stage stage = (Stage) returnButton.getScene().getWindow();
        stage.setScene(newScene);
    }
}