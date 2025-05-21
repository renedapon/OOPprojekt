import javax.swing.*;
import java.awt.*;

public class TaustPanel extends JPanel {
    private Image taustPilt;

    public TaustPanel(String pildiTee) {
        taustPilt = new ImageIcon(pildiTee).getImage();
        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (taustPilt != null) {
            g.drawImage(taustPilt, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
