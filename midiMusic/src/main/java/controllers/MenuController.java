package hackuiowa.controllers;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;
import hackuiowa.app.MainApp;
import hackuiowa.app.State;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.Stage;

public class MenuController {
    @FXML
    private JFXButton menuQuit;

    @FXML
    public void start1p() throws IOException {
        start(1);
    }

    @FXML
    public void start2p() throws IOException {
        start(2);
    }

    private void start(int n) throws IOException {
        Parent select = FXMLLoader.load(getClass().getResource("select.fxml"));
        Scene newScene = new Scene(select);

        Stage stage = (Stage) menuQuit.getScene().getWindow();
        stage.setScene(newScene);
    }

    @FXML
    public void exit() {
        Stage stage = (Stage) menuQuit.getScene().getWindow();
        stage.close();
    }
}