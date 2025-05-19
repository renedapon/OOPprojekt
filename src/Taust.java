import javax.swing.*;
import java.awt.*;

public class Taust extends JLabel {
    private Image taustapilt;

    public Taust(String tekst, String pildiTee) {
        super(tekst, SwingConstants.CENTER);
        this.taustapilt = new ImageIcon(pildiTee).getImage();
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setFont(new Font("Roboto", Font.PLAIN, 24));
        setForeground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(taustapilt, 0, 0, getWidth(), getHeight(), this);
        super.paintComponent(g); // joonistab teksti peale
    }



}
