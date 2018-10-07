package hackuiowa.midiconnect;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

public class MyMidiDevice implements Transmitter, Receiver
{
    private MidiMessage message;

    private Receiver receiver;

    private MidiConn conn;

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
        setMessage(message);
        conn.updateMessage(message);

        System.out.println(message);

        messageCount = 2;
    }

    public MidiMessage getMessage() {
        return message;
    }

    public void setMessage(MidiMessage message) {
        this.message = message;
    }

    public void setConn(MidiConn conn) {
        this.conn = conn;
    }

}
