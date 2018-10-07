package hackuiowa.controllers;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;

import hackuiowa.app.MainApp;
import hackuiowa.app.State;
import hackuiowa.controllers.SelectController;

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
        System.err.println("started new " + n + " player game");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/select.fxml"));
        Parent select = loader.load();
        System.err.println("loaded song select screen");

        SelectController controller = loader.getController();
        controller.setChannelCount(n);

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