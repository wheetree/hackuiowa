import javax.sound.midi.*;
import java.util.Scanner;
import java.util.Vector;

public class MidiUtil {

    private Vector synthInfos;
    private MidiDevice device;
    private MidiDevice.Info[] infos;
    private Transmitter trans;

    private MidiMessage currentMessage;

    private DrawingPanel drawPanel;

    public MidiUtil() {
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

    public void parseMessage() {
        MidiMessage toParse = currentMessage;
        if(toParse instanceof ShortMessage) {
            ShortMessage sm = (ShortMessage) toParse;
            int key = sm.getData1();
            int velocity = sm.getData2();

            if(sm.getCommand() == 0x90) {
                drawPanel.setPitch(key);
                drawPanel.setVolume(velocity);
                drawPanel.addOval();
                drawPanel.repaint();
            }
        }
    }

    public void startCapture() {
        try {
            device.open();
            MyMidiDevice receive = new MyMidiDevice();
            receive.setUtil(this);
            trans = device.getTransmitter();
            trans.setReceiver(receive);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stopCapture() {
        trans.close();
    }

    public void updateMessage(MidiMessage newMessage) {
        currentMessage = newMessage;
    }

    public DrawingPanel getDrawPanel() {
        return drawPanel;
    }

    public void setDrawPanel(DrawingPanel drawPanel) {
        this.drawPanel = drawPanel;
    }
}
