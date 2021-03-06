package hackuiowa.midiconnect;

import hackuiowa.midiparse.Note;

import java.util.Vector;
import javax.sound.midi.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.IOException;

public class MidiConn {

	private Vector synthInfos;
	private MidiDevice device;
	private MidiDevice.Info[] infos;
	private Transmitter trans;
	private MidiMessage currentMessage;
	private Sequencer sequencer;
	
	public MidiConn() {
		device = null;
		synthInfos = new Vector<MidiDevice.Info>();
		infos = MidiSystem.getMidiDeviceInfo();
	}

	public void selectDevice() {
		//Print out MIDI devices
		System.out.println("List of MIDI Devices: ");

		for (int i = 0; i < infos.length; i++) {
		    try {
		        device = MidiSystem.getMidiDevice(infos[i]);
		        System.out.println("" + i + ". " + device.getDeviceInfo() + " # of connections: " + device.getMaxTransmitters());

		    } catch (MidiUnavailableException e) {
		          e.printStackTrace();
		    }
		    if (device instanceof Synthesizer) {
		        synthInfos.add(infos[i]);
		    }
		}
		// Now, display strings from synthInfos list in GUI.    

		//Set device to that selected by user
		try {
			System.out.println("Select your device: ");
			Scanner scanner = new Scanner(System.in);
			int selection = scanner.nextInt();

			device = MidiSystem.getMidiDevice(infos[selection]);
			device.open();

		} catch (MidiUnavailableException e) {
		          e.printStackTrace();
		}
	}

    public Note parseMessage() {
        MidiMessage toParse = currentMessage;
        Note note = new Note(-1, -1, "", -1 ,-1);
        if(toParse instanceof ShortMessage) {
            ShortMessage sm = (ShortMessage) toParse;
            int key = sm.getData1();
            int velocity = sm.getData2();

            if(sm.getCommand() == 0x90) {
                note.setKey(key);
                note.setVelocity(velocity);
                note.setDuration(2);
            }
        }
        return note;
    }

    public void startCapture() {
	    try {
	        device.open();
	        sequencer = MidiSystem.getSequencer();
	        sequencer.open();
	        MyMidiDevice receive = new MyMidiDevice();
	        receive.setConn(this);
	        trans = device.getTransmitter();
	        trans.setReceiver(receive);

            Transmitter seqTrans = device.getTransmitter();
            seqTrans.setReceiver(sequencer.getReceiver());

            Sequence sequence = new Sequence(Sequence.PPQ, 960);
            Track currentTrack = sequence.createTrack();

            sequencer.setSequence(sequence);
            sequencer.setTickPosition(0);
            sequencer.recordEnable(currentTrack, -1);

            sequencer.startRecording();
        } catch (Exception e) {
	        e.printStackTrace();
        }
    }

    public Sequence stopCapture() {
        sequencer.stopRecording();

        Sequence temp = sequencer.getSequence();

        sequencer.close();
        device.close();

        return temp;
    }

	public void writeSeqToFile(Sequence sequence) throws IOException{
		MidiSystem.write(sequence, 0, new File("MyMidiFile.mid"));
	}

	public void closeDevice() {
		device.close();
	}

    public void updateMessage(MidiMessage newMessage) {
        currentMessage = newMessage;
    }
}
