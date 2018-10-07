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
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class PlayController {
    private Sequencer sequencer;
    private boolean playing;
    private Task<Void> task;

    private final double NOTE_WIDTH_MULT = 0.2;
    private final int NOTE_HEIGHT_MULT = 4;

    @FXML
    private Group score;

    @FXML
    private ScrollPane window;

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

        DoubleProperty linePos = scanLine.endXProperty();
        window.hvalueProperty().bind(Bindings.createDoubleBinding(() -> {
            double frac = 0;
            int offset = 400;
            if (linePos.get() > offset)
                frac = ((linePos.get() - offset) / NOTE_WIDTH_MULT) / (sequencer.getTickLength() + offset);

            return frac;
        }, linePos));

        if (task != null && task.isRunning())
            task.cancel();

        task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                int i = 0;
                while (true) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            double xPos = sequencer.getTickPosition() * NOTE_WIDTH_MULT;
                            System.err.println(xPos);

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

    @FXML
    public void back() throws IOException {
        if (playing)
            togglePlaying();

        if (task != null && task.isRunning())
            task.cancel();

        System.err.println("back to song select");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/select.fxml"));
        Parent select = loader.load();
        System.err.println("loaded song select screen");

        Scene newScene = new Scene(select);
        Stage stage = (Stage) playPause.getScene().getWindow();
        stage.setScene(newScene);
    }

    @FXML
    public void endGame() throws IOException {
        if (playing)
            togglePlaying();

        if (task != null && task.isRunning())
            task.cancel();

        System.err.println("ending game");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/end.fxml"));
        Parent select = loader.load();
        System.err.println("loaded end screen");

        Scene newScene = new Scene(select);
        Stage stage = (Stage) playPause.getScene().getWindow();
        stage.setScene(newScene);
    }
}