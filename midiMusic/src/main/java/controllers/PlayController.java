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

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class PlayController {
    private Sequencer sequencer;
    private boolean playing;

    private final double NOTE_WIDTH_MULT = 0.2;
    private final int NOTE_HEIGHT_MULT = 4;

    @FXML
    private Group score;

    @FXML
    private JFXButton playPause;

    public void setNotes(List<ArrayList<Note>> notesList) {
        int yOffset = 0;
        for (ArrayList<Note> channel : notesList) {
            System.err.println("placing channel");
            for (Note note : channel) {
                Rectangle rect = new Rectangle(note.getDuration() * NOTE_WIDTH_MULT, 10, Color.BLUE);
                rect.setLayoutX(note.getStart() * NOTE_WIDTH_MULT);
                rect.setLayoutY(yOffset + 100 - note.getkey() * NOTE_HEIGHT_MULT);
                score.getChildren().add(rect);
            }

            yOffset += 100;
        }
    }

    public void setSequencer(Sequencer sequencer) {
        this.sequencer = sequencer;
        this.playing = false;
        Line scanLine = new Line(0, 0, 0, 1000);
        score.getChildren().add(scanLine);

        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                int i = 0;
                while (true) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            double xPos = sequencer.getTickPosition() * NOTE_WIDTH_MULT;
                            scanLine.setStartX(xPos);
                            scanLine.setEndX(xPos);
                        }
                    });
                    Thread.sleep(100);
                }
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    @FXML
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