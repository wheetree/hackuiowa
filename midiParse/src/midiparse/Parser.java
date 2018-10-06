/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package midiparse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 *
 * @author Pig
 */
public class Parser {
    private static final int NOTE_ON = 0x90;
    private static final int NOTE_OFF = 0x80;
    private static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    
    public static Sequencer getSequencer(String fileName)
    {
        try {
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();
            InputStream is = new BufferedInputStream(new FileInputStream(new File(fileName)));
            sequencer.setSequence(is);
            return sequencer;
        }
        catch (MidiUnavailableException | IOException | InvalidMidiDataException e)
        {
            System.out.println("Error received: " + e.getMessage());
        }
        return null;
    }
    
    public static List<HashMap<Integer, ArrayList<Note>>> parse(String fileName)
    {
        Sequence sequence;
        try {
            sequence = MidiSystem.getSequence(new File(fileName));
        }
        catch (InvalidMidiDataException | IOException e)
        {
            System.out.println("Error received: " + e.getMessage());
            return null;
        }
        
        ArrayList<HashMap<Integer, ArrayList<Note>>> noteChannelTracks = new ArrayList<HashMap<Integer, ArrayList<Note>>>();
        
        int trackNumber = 0;
        for (Track track :  sequence.getTracks()) {
            HashMap<Integer, ArrayList<Note>> noteChannels = new HashMap<Integer, ArrayList<Note>>();
            HashMap<Integer, HashMap<Integer, Note>> unfinishedNotes = new HashMap<Integer, HashMap<Integer, Note>>();
            //System.out.println("Track " + trackNumber + ": size = " + track.size());
            //System.out.println();
            for (int i=0; i < track.size(); i++) { 
                MidiEvent event = track.get(i);
                //System.out.print("@" + event.getTick() + " ");
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    //System.out.print("Channel: " + sm.getChannel() + " ");
                    if (sm.getCommand() == NOTE_ON) {
                        int key = sm.getData1();
                        int octave = (key / 12)-1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        //System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        
                        if (unfinishedNotes.containsKey(sm.getChannel()) && velocity > 0)
                        {
                            HashMap<Integer, Note> channelMap = unfinishedNotes.get(sm.getChannel());
                            channelMap.put(key, new Note(event.getTick(), -1, noteName, key, velocity));
                        }
                        else if (velocity == 0)
                        {
                            HashMap<Integer, Note> channelMap = unfinishedNotes.get(sm.getChannel());
                            Note currNote = channelMap.get(key);
                            currNote.setDuration(event.getTick() - currNote.getStart());
                            if (noteChannels.containsKey(sm.getChannel()))
                            {
                                ArrayList<Note> channel = noteChannels.get(sm.getChannel());
                                channel.add(currNote);
                            }
                            else
                            {
                                ArrayList<Note> channel = new ArrayList<Note>();
                                channel.add(currNote);
                                noteChannels.put(sm.getChannel(), channel);
                            }
                        }
                        else {
                            HashMap<Integer, Note> channelMap = new HashMap<Integer, Note>();
                            channelMap.put(key, new Note(event.getTick(), -1, noteName, key, velocity));
                            unfinishedNotes.put(sm.getChannel(), channelMap);
                        }
                    } else if (sm.getCommand() == NOTE_OFF) {
                        int key = sm.getData1();
                        int octave = (key / 12)-1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        //System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        
                        HashMap<Integer, Note> channelMap = unfinishedNotes.get(sm.getChannel());
                        Note currNote = channelMap.get(key);
                        currNote.setDuration(event.getTick() - currNote.getStart());
                        if (noteChannels.containsKey(sm.getChannel()))
                        {
                            ArrayList<Note> channel = noteChannels.get(sm.getChannel());
                            channel.add(currNote);
                        }
                        else
                        {
                            ArrayList<Note> channel = new ArrayList<Note>();
                            channel.add(currNote);
                            noteChannels.put(sm.getChannel(), channel);
                        }
                    } else {
                        //System.out.println("Command:" + sm.getCommand());
                    }
                } else {
                    //System.out.println("Other message: " + message.getClass());
                }
            }
            noteChannelTracks.add(noteChannels);
            trackNumber++;
            //System.out.println();
        }
        return noteChannelTracks;
    }
}
