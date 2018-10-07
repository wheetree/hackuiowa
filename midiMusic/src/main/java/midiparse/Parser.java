/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hackuiowa.midiparse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map;

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
    private static final String[] NOTE_NAMES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };

    public static Sequencer getSequencer(String fileName) {
        try {
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();
            InputStream is = new BufferedInputStream(new FileInputStream(new File(fileName)));
            sequencer.setSequence(is);
            return sequencer;
        } catch (MidiUnavailableException | IOException | InvalidMidiDataException e) {
            System.out.println("Error received: " + e.getMessage());
        }
        return null;
    }

    public static List<ArrayList<Note>> getTopChannels(List<HashMap<Integer, ArrayList<Note>>> trackList, int n) {
        // assume that track count < 1000
        SortedMap<Long, Integer> topChannels = new TreeMap<>(Comparator.reverseOrder());

        for (int i = 0; i < trackList.size(); i++) {
            for (Map.Entry<Integer, ArrayList<Note>> channel : trackList.get(i).entrySet()) {
                long score = getMaxScore(channel.getValue());
                System.err.println("Track " + i + " channel " + channel.getKey() + " has a max score of " + score);
                topChannels.put(score, 1000 * i + channel.getKey());
            }
        }

        int selected = 0;
        int num = Math.min(n, topChannels.size());
        System.err.println("keeping top " + num + " channels from midi");

        List<ArrayList<Note>> channelList = new ArrayList<ArrayList<Note>>(num);
        for (Map.Entry<Long, Integer> channel : topChannels.entrySet()) {
            if (selected++ >= num)
                break;

            int index = channel.getValue();
            channelList.add(trackList.get(index / 1000).get(index % 1000));
        }

        return channelList;
    }

    public static List<HashMap<Integer, ArrayList<Note>>> parse(String fileName)
            throws InvalidMidiDataException, IOException {
        return parse(fileName, -1);
    }

    public static List<HashMap<Integer, ArrayList<Note>>> parse(String fileName, int num)
            throws InvalidMidiDataException, IOException {
        Sequence sequence = MidiSystem.getSequence(new File(fileName));
        ArrayList<HashMap<Integer, ArrayList<Note>>> noteChannelTracks = new ArrayList<HashMap<Integer, ArrayList<Note>>>();

        if (num < 0 || num > sequence.getTracks().length)
            num = sequence.getTracks().length;
        System.err.println("keeping first " + num + " tracks from midi");

        for (int q = 0; q < num; q++) {
            Track track = sequence.getTracks()[q];
            HashMap<Integer, ArrayList<Note>> noteChannels = new HashMap<Integer, ArrayList<Note>>();
            HashMap<Integer, HashMap<Integer, Note>> unfinishedNotes = new HashMap<Integer, HashMap<Integer, Note>>();
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if (sm.getCommand() == NOTE_ON) {
                        int key = sm.getData1();
                        int octave = (key / 12) - 1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();

                        if (unfinishedNotes.containsKey(sm.getChannel()) && velocity > 0) {
                            HashMap<Integer, Note> channelMap = unfinishedNotes.get(sm.getChannel());
                            channelMap.put(key, new Note(event.getTick(), -1, noteName + octave, key, velocity));
                        } else if (velocity == 0) {
                            HashMap<Integer, Note> channelMap = unfinishedNotes.get(sm.getChannel());
                            Note currNote = channelMap.get(key);
                            currNote.setDuration(event.getTick() - currNote.getStart());
                            if (noteChannels.containsKey(sm.getChannel())) {
                                ArrayList<Note> channel = noteChannels.get(sm.getChannel());
                                if (channel.get(channel.size() - 1).getStart() < event.getTick())
                                    channel.add(currNote);
                                else {
                                    int n = channel.size() - 2;
                                    while (channel.get(n).getStart() < event.getTick()) {
                                        n--;
                                    }
                                    channel.add(n, currNote);
                                }
                            } else {
                                ArrayList<Note> channel = new ArrayList<Note>();
                                channel.add(currNote);
                                noteChannels.put(sm.getChannel(), channel);
                            }
                        } else {
                            HashMap<Integer, Note> channelMap = new HashMap<Integer, Note>();
                            channelMap.put(key, new Note(event.getTick(), -1, noteName, key, velocity));
                            unfinishedNotes.put(sm.getChannel(), channelMap);
                        }
                    } else if (sm.getCommand() == NOTE_OFF) {
                        int key = sm.getData1();

                        HashMap<Integer, Note> channelMap = unfinishedNotes.get(sm.getChannel());
                        Note currNote = channelMap.get(key);
                        currNote.setDuration(event.getTick() - currNote.getStart());
                        if (noteChannels.containsKey(sm.getChannel())) {
                            ArrayList<Note> channel = noteChannels.get(sm.getChannel());
                            if (channel.get(channel.size() - 1).getStart() < event.getTick())
                                channel.add(currNote);
                            else {
                                int n = channel.size() - 2;
                                while (channel.get(n).getStart() < event.getTick()) {
                                    n--;
                                }
                                channel.add(n, currNote);
                            }
                        } else {
                            ArrayList<Note> channel = new ArrayList<Note>();
                            channel.add(currNote);
                            noteChannels.put(sm.getChannel(), channel);
                        }
                    }
                }
            }
            noteChannelTracks.add(noteChannels);
        }
        return noteChannelTracks;
    }

    // assuming original is sorted by start time on notes
    public static long compare(ArrayList<Note> original, ArrayList<Note> input) {
        long accum = 0;
        for (Note curr : input) {
            long end = curr.getStart() + curr.getDuration();
            int maxIndex = getMaxIndex(original, 0, original.size(), end);
            long matching = 0;
            for (int i = 0; i < maxIndex; i++) {
                Note a = original.get(i);
                if (a.getkey() == curr.getkey()) {
                    if (end > a.getStart()) {
                        if (curr.getStart() < a.getStart() + a.getDuration()) {
                            long durStart;
                            long durEnd;
                            if (curr.getStart() > a.getStart())
                                durStart = curr.getStart();
                            else
                                durStart = a.getStart();
                            if (end < a.getStart() + a.getDuration())
                                durEnd = end;
                            else
                                durEnd = a.getStart() + a.getDuration();

                            matching += durEnd - durStart;
                        }
                    }
                }
            }

            accum += matching;
        }
        return accum;
    }

    public static long getMaxScore(ArrayList<Note> list) {
        long accum = 0;
        for (Note n : list) {
            accum += n.getDuration();
        }

        return accum;
    }

    private static int getMaxIndex(ArrayList<Note> list, int start, int end, long n) {
        if (start == end)
            return start;
        if (end - start == 1)
            return end;

        int mid = start + (end - start) / 2;
        if (list.get(mid).getStart() == n)
            return mid;
        else if (list.get(mid).getStart() < n)
            return getMaxIndex(list, mid, end, n);
        else
            return getMaxIndex(list, start, mid, n);
    }
}
