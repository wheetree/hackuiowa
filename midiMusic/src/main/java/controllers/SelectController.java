package hackuiowa.controllers;

import hackuiowa.midiparse.Parser;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.Stage;

public class SelectController {
    private int tracks;
    private String trackName;

    @FXML
    private JFXButton startButton;

    @FXML
    private JFXListView trackList;

    public SelectController() {

    }

    @FXML
    public void startTrack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/play.fxml"));
        Parent play = loader.load();
        PlayController controller = loader.getController();
        controller.setNotes(Parser.getTopChannels(Parser.parse(trackName), tracks));

        Scene newScene = new Scene(play);
        Stage stage = (Stage) startButton.getScene().getWindow();
        stage.setScene(newScene);
    }

    public void setTracks(int n) {
        tracks = n;
    }
}