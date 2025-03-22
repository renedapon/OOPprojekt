import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class FlashCardBuilder {
    private JTextArea küsimus;
    private JTextArea vastus;
    private ArrayList<FlashCard> kaardid;
    private JFrame frame;

    public FlashCardBuilder(){
        //UI
        frame = new JFrame("FlashCards");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel =new JPanel();
        Font font = new Font("Helvetica Neue", Font.BOLD, 20);


        küsimus = new JTextArea(6, 20);
        küsimus.setLineWrap(true);
        küsimus.setWrapStyleWord(true);
        küsimus.setFont(font);

        //JScrollPane
        JScrollPane küsimuseJScrollPane = new JScrollPane(küsimus);
        küsimuseJScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        küsimuseJScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        vastus = new JTextArea(6, 20);
        vastus.setLineWrap(true);
        vastus.setWrapStyleWord(true);
        vastus.setFont(font);

        JScrollPane vastuseJScrollPane = new JScrollPane(vastus);
        vastuseJScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        vastuseJScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JButton järgmine = new JButton("Lisa järgmine kaart");

        kaardid = new ArrayList<FlashCard>();

        JLabel küsimuseLabel = new JLabel("Küsimus");
        JLabel vastuseLabel = new JLabel("Vastus");


        //Komponentide lisamine peapaneeli
        mainPanel.add(küsimuseLabel);
        mainPanel.add(küsimuseJScrollPane);

        mainPanel.add(vastuseLabel);
        mainPanel.add(vastuseJScrollPane);

        mainPanel.add(järgmine);

        järgmine.addActionListener(new NextCardListener());

        JMenuBar menüü = new JMenuBar();
        JMenu failimenüü = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem saveMenuItem = new JMenuItem("Save");

        failimenüü.add(newMenuItem);
        failimenüü.add(saveMenuItem);

        menüü.add(failimenüü);



        //Eventlisteners
        newMenuItem.addActionListener(new NewMenuItemListener());
        saveMenuItem.addActionListener(new SaveMenuItemListener());

        frame.setJMenuBar(menüü);

        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setSize(900, 300);
        frame.setVisible(true);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FlashCardBuilder();
            }
        });
    }

    class NextCardListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            //FlashCardi tegemine
            if(!küsimus.equals("") || !vastus.equals("")){
                FlashCard kaart = new FlashCard(küsimus.getText(), vastus.getText());
                kaardid.add(kaart);
            }
            clearCard();
        }

    }

    class NewMenuItemListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("new");
        }
    }

    class SaveMenuItemListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!küsimus.equals("") || !vastus.equals("")){
                FlashCard card = new FlashCard(küsimus.getText(), vastus.getText());
                kaardid.add(card);
            }


            //File dialog with file chooser
            JFileChooser fileSave = new JFileChooser();
            fileSave.showSaveDialog(frame);
            saveFile(fileSave.getSelectedFile());
        }
    }

    private void saveFile(File selectedFile) {
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile));

            Iterator<FlashCard> cardIterator = kaardid.iterator();
            while (cardIterator.hasNext()){
                FlashCard card = (FlashCard) cardIterator.next();
                writer.write(card.getKüsimus() + " -- ");
                writer.write((card.getVastus()) + "\n");
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Faili salvestamine ebaõnnestus!");
            e.printStackTrace();
        }
    }

    //Väljad tühjaks
    private void clearCard() {
        küsimus.setText("");
        vastus.setText("");
        küsimus.requestFocus();
    }
}
