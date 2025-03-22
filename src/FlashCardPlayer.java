import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

public class FlashCardPlayer {

    private JTextArea display;
    private JTextArea vastus;
    private ArrayList<FlashCard> kaardid;
    private Iterator cardIterator;
    private FlashCard currentCard;
    private JFrame frame;
    private boolean näitaVastus;
    private JButton näitaVastustNupp;
    private JButton käiUuestiLäbiNupp; /// karel

    public FlashCardPlayer(){
        frame = new JFrame("Flash Card Player");
        JPanel mainPanel = new JPanel();
        Font font = new Font("Helvetica Neue", Font.BOLD, 20);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        display = new JTextArea(10,20);
        display.setFont(font);
        display.setLineWrap(true);
        display.setWrapStyleWord(true);

        JScrollPane qScroll = new JScrollPane(display);
        qScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        näitaVastustNupp = new JButton("Näita vastust.");
        käiUuestiLäbiNupp = new JButton("Tee uuesti."); /// karel

        mainPanel.add(qScroll);
        mainPanel.add(näitaVastustNupp);
        näitaVastustNupp.addActionListener(new NextCardListener());

        mainPanel.add(käiUuestiLäbiNupp); /// karel
        käiUuestiLäbiNupp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(kaardid != null && !kaardid.isEmpty()){
                    cardIterator = kaardid.iterator();
                    näitaVastustNupp.setEnabled(true);
                    showNextCard();
                }
            }
        });  /// karel




        JMenuBar menüü = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem loadMenuItem = new JMenuItem("Load card set");
        loadMenuItem.addActionListener(new OpenMenuListener());

        frame.setJMenuBar(menüü);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setSize(640, 400);
        frame.setVisible(true);

        fileMenu.add(loadMenuItem);
        menüü.add(fileMenu);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FlashCardPlayer();
            }
        });
    }

    class NextCardListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if(näitaVastus){
                display.setText(currentCard.getVastus());
                näitaVastustNupp.setText("Järgmine kaart");
                näitaVastus = false;
            } else {
                if (cardIterator.hasNext()){
                    showNextCard();
                } else { //kaardid otsas
                    display.setText("See oli viimane kaart, hea töö!");
                    näitaVastustNupp.setEnabled(false);
                }
            }
        }
    }

    private class OpenMenuListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileOpen = new JFileChooser();
            fileOpen.showOpenDialog(frame);
            loadFile(fileOpen.getSelectedFile());
        }
    }

    private void loadFile(File selectedFile) {
        kaardid = new ArrayList<FlashCard>();

        try{
            BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
            String line = null;
            while ((line = reader.readLine()) != null){
                String[] osad = line.split(" -- ");
                String küsimus = osad[0];
                String vastus = osad[1];
                FlashCard kaart = new FlashCard(küsimus, vastus);
                kaardid.add(kaart);
            }

        } catch (Exception e){
            System.out.println("Ei saanud failist lugeda.");
            e.printStackTrace();
        }

        cardIterator = kaardid.iterator();
        showNextCard();
    }

    private void showNextCard() {
        currentCard = (FlashCard) cardIterator.next();
        display.setText(currentCard.getKüsimus());
        näitaVastustNupp.setText("Näita vastust");
        näitaVastus = true;
    }
}
