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
		        System.out.println("" + i + ". " + device.getDeviceInfo());
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

	public Sequence recordAndPlaySequence(int secondsLong) {
		try {
			Sequencer sequencer = MidiSystem.getSequencer();
			Transmitter transmitter;
			Receiver receiver;
			
			device.open();
			sequencer.open();
			
			Synthesizer synth = MidiSystem.getSynthesizer();
			synth.open();
			Instrument[] instr = synth.getDefaultSoundbank().getInstruments();
			synth.loadInstrument(instr[10]);


			
			Transmitter synthTrans = device.getTransmitter();
			Transmitter seqTrans = device.getTransmitter();
			synthTrans.setReceiver(synth.getReceiver());
			seqTrans.setReceiver(sequencer.getReceiver());

			System.out.println("# synth receivers: " + synthTrans.getReceiver());
			System.out.println("# sequencer receivers: " + seqTrans.getReceiver());
			
			synth.getReceiver().send(new ShortMessage(ShortMessage.NOTE_ON, 4, 60, 93), -1);

			Sequence sequence = new Sequence(Sequence.PPQ, 24);
			Track currentTrack = sequence.createTrack();

			sequencer.setSequence(sequence);
			sequencer.setTickPosition(0);
			sequencer.recordEnable(currentTrack, -1);

			sequencer.startRecording();

			TimeUnit.SECONDS.sleep(secondsLong);

			sequencer.stopRecording();

			Sequence temp = sequencer.getSequence();

			sequencer.close();
			device.close();
			//synth.close();

			return temp;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public Sequence recordSequence(int secondsLong) {
		try {
			Receiver receiver;
			Transmitter transmitter = device.getTransmitter();
			Sequencer sequencer = MidiSystem.getSequencer();
			sequencer.open();
			receiver = sequencer.getReceiver();
			transmitter.setReceiver(receiver);

			Sequence sequence = new Sequence(Sequence.PPQ, 24);
			Track currentTrack = sequence.createTrack();

			sequencer.setSequence(sequence);
			sequencer.setTickPosition(0);
			sequencer.recordEnable(currentTrack, -1);

			sequencer.startRecording();

			TimeUnit.SECONDS.sleep(secondsLong);

			sequencer.stopRecording();

			Sequence temp = sequencer.getSequence();

			sequencer.close();

			return temp;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void writeSeqToFile(Sequence sequence) throws IOException{
		MidiSystem.write(sequence, 0, new File("MyMidiFile.mid"));
	}

	public void closeDevice() {
		device.close();
	}
}
