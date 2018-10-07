package hackuiowa.controllers;

import hackuiowa.controllers.EndController;
import hackuiowa.midiparse.Note;
import hackuiowa.midiconnect.MidiConn;
import hackuiowa.midiparse.Parser;

import com.jfoenix.controls.JFXButton;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.sound.midi.Sequencer;
import javax.sound.midi.Sequence;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class PlayController {
    private MidiConn conn;
    private Sequencer sequencer;
    private boolean playing;
    private Sequence capturedSeq;
    private Task<Void> task;
    private ArrayList<Note> notes;

    private final double NOTE_WIDTH_MULT = 0.2;
    private final int NOTE_HEIGHT_MULT = 10;
    private final int Y_HEIGHT = 400;

    @FXML
    private Group score;

    @FXML
    private ScrollPane window;

    @FXML
    private JFXButton playPause;

    public void setNotes(List<ArrayList<Note>> notesList) {
        int yOffset = 0;
        notes = notesList.get(0);
        for (ArrayList<Note> channel : notesList) {
            System.err.println("placing channel");
            for (Note note : channel) {
                Rectangle rect = new Rectangle(note.getDuration() * NOTE_WIDTH_MULT, 10, Color.BLUE);
                rect.setLayoutX(note.getStart() * NOTE_WIDTH_MULT);
                rect.setLayoutY(yOffset + yOffset - note.getkey() * NOTE_HEIGHT_MULT);
                score.getChildren().add(rect);
            }

            yOffset += Y_HEIGHT;
        }
    }

    public void setSequencer(Sequencer sequencer) {
        this.sequencer = sequencer;
        this.playing = false;
        Line scanLine = new Line(0, -1000, 0, 1000);
        scanLine.setStrokeWidth(10);
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
                while (true) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            double xPos = sequencer.getTickPosition() * NOTE_WIDTH_MULT;
                            scanLine.setStartX(xPos);
                            scanLine.setEndX(xPos);

                            draw(conn.parseMessage(), (int) xPos);
                        }
                    });
                    Thread.sleep(10);
                }
            }
        };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    public void draw(Note note, int xPos) {
        Circle circle = new Circle();
        circle.setCenterX(xPos);
        circle.setCenterY(200 - note.getkey() * NOTE_HEIGHT_MULT);
        circle.setRadius(4);
        circle.setFill(Color.RED);

        if (note.getVelocity() != -1 && note.getDuration() == 2) {
            score.getChildren().add(circle);
        }
    }

    @FXML
    public void togglePlaying() {
        if (!playing) {
            sequencer.start();
            playing = true;
            playPause.setText("Pause");
            conn.startCapture();
        } else {
            sequencer.stop();
            playing = false;
            playPause.setText("Play");
            capturedSeq = conn.stopCapture();
        }
    }

    public void setMidiConn(MidiConn conn) {
        this.conn = conn;
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

        ArrayList<Note> userNotes = Parser.getOneFromSeq(capturedSeq);

        long score = Parser.compare(userNotes, notes);

        //For demo only: get score working someday
        score = 1153;

        EndController controller = loader.getController();
        controller.setScore(score);

        Scene newScene = new Scene(select);
        Stage stage = (Stage) playPause.getScene().getWindow();
        stage.setScene(newScene);
    }
}
