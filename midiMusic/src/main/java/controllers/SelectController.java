package hackuiowa.controllers;

import hackuiowa.controllers.PlayController;
import hackuiowa.midiparse.Parser;
import hackuiowa.midiconnect.MidiConn;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.sound.midi.InvalidMidiDataException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.Stage;

public class SelectController implements Initializable {
    private MidiConn conn;
    private File folder;
    private int channels;
    private String trackName;

    @FXML
    private JFXButton startButton;

    @FXML
    private JFXListView<String> trackList;

    public SelectController() {
        URL songURL = getClass().getClassLoader().getResource("midi");
        folder = new File(songURL.getPath());
    }

    @FXML
    public void initialize(URL url, ResourceBundle resource) {
        File[] songList = folder.listFiles();
        System.err.println(songList.length + " songs found");

        ObservableList<String> items = FXCollections.observableArrayList();
        for (File f : songList) {
            items.add(f.getName());
        }

        trackList.setItems(items);
        System.err.println("tracklist populated");

        trackList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                trackName = newValue;
                System.err.println("selected track: " + newValue);
            }
        });
        conn = new MidiConn();
        conn.selectDevice();

    }

    @FXML
    public void startTrack() throws IOException {
        if (trackName == null)
            return;

        String fullName = folder.getPath() + "/" + trackName;
        System.err.println("starting track " + fullName);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/play.fxml"));
        Parent play = loader.load();
        System.err.println("loaded play screen");

        PlayController controller = loader.getController();
        controller.setMidiConn(conn);

        try {
            controller.setNotes(Parser.getTopChannels(Parser.parse(fullName), channels));
            controller.setSequencer(Parser.getSequencer(fullName));

            Scene newScene = new Scene(play);
            Stage stage = (Stage) startButton.getScene().getWindow();
            stage.setScene(newScene);
        } catch (InvalidMidiDataException | IOException e) {
            System.err.println("Error received: " + e.getMessage());
        }
    }

    public void setChannelCount(int n) {
        channels = n;
    }
}
