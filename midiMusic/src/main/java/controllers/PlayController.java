package hackuiowa.controllers;

import hackuiowa.controllers.EndController;
import hackuiowa.midiparse.Note;

import com.jfoenix.controls.JFXButton;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.sound.midi.Sequencer;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class PlayController {
    private Sequencer sequencer;
    private boolean playing;

    @FXML
    private Group score;

    @FXML
    private JFXButton playPause;

    public void setNotes(List<ArrayList<Note>> notesList) {
        int yOffset = 0;
        for (ArrayList<Note> channel : notesList) {
            System.err.println("placing channel");
            for (Note note : channel) {
                Rectangle rect = new Rectangle(note.getDuration() * 0.2, 10, Color.BLUE);
                rect.setLayoutX(note.getStart() * 0.2);
                rect.setLayoutY(yOffset + 100 - note.getkey() * 4);
                score.getChildren().add(rect);
            }

            yOffset += 100;
        }
    }

    public void setSequencer(Sequencer sequencer) {
        this.sequencer = sequencer;
        this.playing = false;
    }

    public void togglePlaying() {
        if (!playing) {
            sequencer.start();
            playing = true;
            playPause.setText("Pause");
        } else {
            sequencer.stop();
            playing = false;
            playPause.setText("Play");
        }
    }
}