import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class DrawingPanel extends JPanel {

    private int pitch;

    private int volume;

    private Color color = new Color(0, 0, 0);

    private int shapeCount;

    private List<Tuple<Shape, Color>> tuples = new ArrayList<Tuple<Shape, Color>>();

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        for (Tuple<Shape, Color> tuple : tuples) {
            g2.setColor(tuple.y);
            g2.draw(tuple.x);
            g2.fill(tuple.x);
        }
    }

    public void addOval() {
        shapeCount++;
        if(shapeCount > 5) {
            System.out.println("nani");
            int red = color.getRed();
            int blue = color.getBlue();
            int green = color.getGreen();
            Color newColor = new Color(((red + 5) % 255), ((green + 10) % 255), ((blue + 15) % 255));
            setColor(newColor);
            shapeCount = 0;
        }
        tuples.add(new Tuple(new Ellipse2D.Double((getHeight()/2)+getXAxis(), (getWidth()/2)+getYAxis(), Math.random()*20+20, Math.random()*20+20), color));
    }

    public int getXAxis() {
        return 11 * (getPitch() - 64) + (int) (Math.random()*10 - 10);
    }

    public int getYAxis() {
        return 4 * (getVolume() - 64) + (int) (Math.random()*10 - 10);
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}