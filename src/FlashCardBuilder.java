import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Antud klass laseb ehitada omale sobivate küsimuste ja vastustega "Flashcardi" komplekti, Küsimuse aknasse
 * saab kirjutada küsimuse ja Vastuse aknasse vastuse, järgmise kaardi tegemise juurde saab liikuda vajutades
 * nuppu "lisa kaart".
 */
public class FlashCardBuilder {
    private JTextArea küsimus;
    private JTextArea vastus;
    private ArrayList<FlashCard> kaardid;
    private JFrame frame;
    private JButton järgmine; //muutus

    public FlashCardBuilder(){

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        //UI
        frame = new JFrame("FlashCards Builder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // peamine paneel, kuhu kõik kastid ja tekst läheb
        JPanel mainPanel =new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); //kastid tekivad uksteise alla
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));//loob tuhja ala umber

        Font font = new Font("Helvetica Neue", Font.BOLD, 20);

        //vastava kasti üleval, et teha kasutajale mõistetavaks, kuhu kirjutada antud Flashcardi küsimus
        JLabel küsimuseLabel = new JLabel("Küsimus");
        küsimus = new JTextArea(4, 10);
        küsimus.setLineWrap(true);
        küsimus.setWrapStyleWord(true);
        küsimus.setFont(font);

        //JScrollPane
        JScrollPane küsimuseJScrollPane = new JScrollPane(küsimus);
        küsimuseJScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        küsimuseJScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);


        //antud kasti uleval, et teha kasutajale mõistetavaks, kuhu kirjutada flashcardi vastus
        JLabel vastuseLabel = new JLabel("Vastus");
        vastus = new JTextArea(4, 10);
        vastus.setLineWrap(true);
        vastus.setWrapStyleWord(true);
        vastus.setFont(font);

        JScrollPane vastuseJScrollPane = new JScrollPane(vastus);
        vastuseJScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        vastuseJScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);


        //nupp uue kaardi lisamiseks, voimalik peale selle vajutamist uut kaarti hakata taitma
        JButton järgmine = new JButton("Lisa kaart");
        järgmine.setBackground(new Color(104, 155, 198));
        järgmine.setForeground(Color.WHITE);
        järgmine.setFont(new Font("Arial", Font.BOLD, 14));
        järgmine.setAlignmentX(Component.CENTER_ALIGNMENT);

        //kaartide ArrayList kuhu lähevad küsimused koos vastustega
        kaardid = new ArrayList<FlashCard>();



        //Komponentide lisamine peapaneeli
        mainPanel.add(küsimuseLabel);
        mainPanel.add(küsimuseJScrollPane);
        mainPanel.add(Box.createVerticalStrut(10));

        mainPanel.add(vastuseLabel);
        mainPanel.add(vastuseJScrollPane);
        mainPanel.add(Box.createVerticalStrut(10));

        mainPanel.add(järgmine);

        //tuvastab kui vajutatakse nuppu, et liikuda järgmise kaardi juurde, ehk lisa kaart.
        järgmine.addActionListener(new NextCardListener());

        //Menüü nupud
        JMenuBar menüü = new JMenuBar();
        JMenu failimenüü = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem saveMenuItem = new JMenuItem("Save");

        failimenüü.add(newMenuItem);
        failimenüü.add(saveMenuItem);

        menüü.add(failimenüü);

        //Eventlisteners, selleks, et tuvastada kui vajutatakse New või Save nuppu.
        newMenuItem.addActionListener(new NewMenuItemListener());
        saveMenuItem.addActionListener(new SaveMenuItemListener());

        frame.setJMenuBar(menüü);

        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setSize(600, 400);
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

    /**
     * Klassi abil lisatakse küsimus ja vastus Flashcardina kaartide ArrayListi. Küsimuse ja vastuse tekst
     * saadakse mõlema JTextArea käest juhul kui küsimus ega vastus pole tühjad.
     * Kõige lõpus tehakse kaardi väljad tühjaks, et järgmisel kaardil pole juba teksti ees.
     */
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

    /**
     * Klassi abil on võimalik salvestada antud kaartide ArrayList soovitud faili, vajutades Menüüs "save"
     * peale siis avaneb n-ö fail explorer vaade ja võimalik sealtkaudu fail salvestada kasutades saveFile meetodit.
     */
    class SaveMenuItemListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!küsimus.equals("") || !vastus.equals("")){
                FlashCard card = new FlashCard(küsimus.getText(), vastus.getText());
                kaardid.add(card);
            }

            //File dialog koos file chooser-iga
            JFileChooser fileSave = new JFileChooser();
            fileSave.showSaveDialog(frame);
            saveFile(fileSave.getSelectedFile());
        }
    }

    /**
     * @param selectedFile antud fail kuhu kaartide arraylistist flashcardid (küsimus ja vastus) salvestatakse.
     *kirjutab nii: küsimus -- vastus (reavahetus). lõpus sulgeb faili ja errori korral väljastab teate
     */
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

    /**
     * Meetod tühjendab väljad, et järgmisel kaardil poleks infot ees
     * ning saaks alustada uuesti küsimuse ja vastuse kirjutamist
     */
    private void clearCard() {
        küsimus.setText("");
        vastus.setText("");
        küsimus.requestFocus();
    }

}