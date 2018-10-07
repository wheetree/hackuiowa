import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

public class MyMidiDevice implements Transmitter, Receiver
{

    private Receiver receiver;

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
        this.getReceiver().send(message, -1);
    }
}