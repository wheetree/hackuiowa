import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

public class MyMidiDevice implements Transmitter, Receiver
{
    private MidiMessage message;

    private Receiver receiver;

    private MidiUtil util;

    private int messageCount = 2;

    @Override
    public Receiver getReceiver()
    {
        return this.receiver;
    }

    @Override
    public void setReceiver(Receiver receiver)
    {
        this.receiver = receiver;
    }

    @Override
    public void close()
    {
    }

    @Override
    public void send(MidiMessage message, long timeStamp)
    {
        messageCount--;
        if(messageCount < 1) {
            return;
        }
        System.out.println(message);
        setMessage(message);
        util.updateMessage(message);
        util.parseMessage();
        messageCount = 2;
    }

    public MidiMessage getMessage() {
        return message;
    }

    public void setMessage(MidiMessage message) {
        this.message = message;
    }

    public void setUtil(MidiUtil util) {
        this.util = util;
    }
}