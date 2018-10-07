package hackuiowa.controllers;

import hackuiowa.midiparse.Note;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.Stage;

public class PlayController {
    public PlayController() {

    }

    public void setNotes(ArrayList<Note>[] trackList) {
        for (ArrayList<Note> notes : trackList) {
            for (Note n : notes) {
                System.out.println(n.getkey());
            }
        }
    }
}