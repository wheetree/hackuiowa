import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MusicArtGUI {

    private MidiUtil midi;
    public MusicArtGUI() {
        JFrame mainFrame = new JFrame("Music Art");
        JPanel mainPanel = new JPanel();
        DrawingPanel drawingPanel = new DrawingPanel();
        JPanel buttonPanel = new JPanel();
        JButton startButton = new JButton("Start");

        midi = new MidiUtil();
        midi.selectDevice();
        midi.setDrawPanel(drawingPanel);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(startButton.getText().equals("Start")) {
                    midi.startCapture();
                    startButton.setText("Stop");
                } else {
                    midi.stopCapture();
                    startButton.setText("Start");
                }
            }
        });

        drawingPanel.setPreferredSize(new Dimension(600,600));
        drawingPanel.setOpaque(true);
        drawingPanel.setBackground(Color.WHITE);

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(drawingPanel);
        mainPanel.add(buttonPanel);
        mainPanel.add(startButton);

        mainFrame.add(mainPanel);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        MusicArtGUI gui = new MusicArtGUI();
    }
}
