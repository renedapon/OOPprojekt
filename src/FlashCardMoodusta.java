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
public class FlashCardMoodusta {
    private JTextArea küsimus;
    private JTextArea vastus;
    private ArrayList<FlashCard> kaardid;
    private JFrame frame;
    private JButton järgmine; //muutus

    public FlashCardMoodusta(){

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
        } catch (Exception ignored) {}

        //UI
        frame = new JFrame("FlashCards Moodustamine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon("flash-cards.png").getImage());


        // peamine paneel, kuhu kõik kastid ja tekst läheb
        JPanel mainPanel =new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); //kastid tekivad uksteise alla
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));//loob tuhja ala umber

        Font font = new Font("Roboto", Font.BOLD, 20);
        Font fontTekst = new Font("Comic Sans MS", Font.PLAIN, 20);
        Font fontKaks = new Font("Oswald", Font.BOLD, 10);

        // Et oleks võimalik tausta pilti kuvada
        TaustPanel küsimusePaneel = new TaustPanel("pilt.jpg");
        TaustPanel vastusePaneel = new TaustPanel("pilt.jpg");


        //vastava kasti üleval, et teha kasutajale mõistetavaks, kuhu kirjutada antud Flashcardi küsimus
        JLabel küsimuseLabel = new JLabel("KÜSIMUS");
        küsimuseLabel.setForeground(Color.WHITE);
        küsimuseLabel.setFont(fontKaks);
        küsimus = new JTextArea(4, 10);
        küsimus.setMargin(new Insets(10,20,10,10));
        küsimus.setForeground(Color.BLUE);
        küsimus.setLineWrap(true);
        küsimus.setWrapStyleWord(true);
        küsimus.setFont(fontTekst);

        //JScrollPane
        JScrollPane küsimuseJScrollPane = new JScrollPane(küsimus);
        küsimuseJScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        küsimuseJScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);


        //antud kasti uleval, et teha kasutajale mõistetavaks, kuhu kirjutada flashcardi vastus
        JLabel vastuseLabel = new JLabel("VASTUS");
        vastuseLabel.setForeground(Color.WHITE);
        vastuseLabel.setFont(fontKaks);
        vastus = new JTextArea(4, 10);
        //äärised
        vastus.setMargin(new Insets(10,20,10,10));
        vastus.setForeground(Color.BLUE);
        vastus.setLineWrap(true);
        vastus.setWrapStyleWord(true);
        vastus.setFont(fontTekst);

        JScrollPane vastuseJScrollPane = new JScrollPane(vastus);
        vastuseJScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        vastuseJScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);


        //nupp uue kaardi lisamiseks, voimalik peale selle vajutamist uut kaarti hakata taitma
        JButton järgmine = new JButton("Lisa kaart");
        järgmine.setBackground(Color.GRAY);
        järgmine.setForeground(Color.WHITE);
        järgmine.setFont(new Font("Arial", Font.BOLD, 14));
        järgmine.setAlignmentX(Component.CENTER_ALIGNMENT);

        //kaartide ArrayList kuhu lähevad küsimused koos vastustega
        kaardid = new ArrayList<FlashCard>();

        // LISAD
        küsimus.setOpaque(false); // et taust paistaks läbi
        küsimus.setBackground(new Color(0, 0, 0, 0)); // läbipaistev
        küsimuseJScrollPane.setOpaque(false);
        küsimuseJScrollPane.getViewport().setOpaque(false);

        // Tee sama vastuse jaoks
        vastus.setOpaque(false);
        vastus.setBackground(new Color(0, 0, 0, 0));
        vastuseJScrollPane.setOpaque(false);
        vastuseJScrollPane.getViewport().setOpaque(false);

        küsimusePaneel.add(küsimuseJScrollPane);
        vastusePaneel.add(vastuseJScrollPane);



        //Komponentide lisamine peapaneeli
        mainPanel.add(küsimuseLabel);
        //mainPanel.add(küsimuseJScrollPane);
        mainPanel.add(küsimusePaneel);
        mainPanel.add(Box.createVerticalStrut(10));

        mainPanel.add(vastuseLabel);
        //mainPanel.add(vastuseJScrollPane);
        mainPanel.add(vastusePaneel);
        mainPanel.add(Box.createVerticalStrut(10));

        mainPanel.add(järgmine);
        mainPanel.setBackground(Color.DARK_GRAY);

        //tuvastab kui vajutatakse nuppu, et liikuda järgmise kaardi juurde, ehk lisa kaart.
        järgmine.addActionListener(new järgmineKaart());

        //Menüü nupud
        JMenuBar menüü = new JMenuBar();
        JMenu failimenüü = new JMenu("Fail");
        JMenuItem uusMenüü = new JMenuItem("Uus");
        JMenuItem salvestaMenüü = new JMenuItem("Salvesta");

        menüü.setBackground(Color.DARK_GRAY);
        menüü.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        failimenüü.add(uusMenüü);
        failimenüü.add(salvestaMenüü);

        menüü.add(failimenüü);

        //Eventlisteners, selleks, et tuvastada kui vajutatakse New või Save nuppu.
        uusMenüü.addActionListener(new uusMenüüListener());
        salvestaMenüü.addActionListener(new salvestaMenüüListener());

        frame.setJMenuBar(menüü);

        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setSize(600, 400);
        frame.setVisible(true);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FlashCardMoodusta();
            }
        });
    }

    /**
     * Klassi abil lisatakse küsimus ja vastus Flashcardina kaartide ArrayListi. Küsimuse ja vastuse tekst
     * saadakse mõlema JTextArea käest juhul kui küsimus ega vastus pole tühjad.
     * Kõige lõpus tehakse kaardi väljad tühjaks, et järgmisel kaardil pole juba teksti ees.
     */
    class järgmineKaart implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            //FlashCardi tegemine
            if(!küsimus.equals("") || !vastus.equals("")){
                FlashCard kaart = new FlashCard(küsimus.getText(), vastus.getText());
                kaardid.add(kaart);
            }
            puhastaKaart();
        }

    }

    /**
     * Klassi abil on võimalik kõik eelenvalt kirjutatud väljad tühjendada ka alustada algusest FlashKaartide tegemisega
     */
    class uusMenüüListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            kaardid.clear();
            puhastaKaart();
        }
    }

    /**
     * Klassi abil on võimalik salvestada antud kaartide ArrayList soovitud faili, vajutades Menüüs "save"
     * peale siis avaneb n-ö fail explorer vaade ja võimalik sealtkaudu fail salvestada kasutades saveFile meetodit.
     */
    class salvestaMenüüListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!küsimus.equals("") || !vastus.equals("")){
                FlashCard card = new FlashCard(küsimus.getText(), vastus.getText());
                kaardid.add(card);
            }

            //File dialog koos file chooser-iga
            JFileChooser antudFail = new JFileChooser();
            antudFail.showSaveDialog(frame);
            salvestaFaili(antudFail.getSelectedFile());
        }
    }

    /**
     * @param etteantudFail antud fail kuhu kaartide arraylistist flashcardid (küsimus ja vastus) salvestatakse.
     *kirjutab nii: küsimus -- vastus (reavahetus). lõpus sulgeb faili ja errori korral väljastab teate
     */
    private void salvestaFaili(File etteantudFail) {
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(etteantudFail));

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
    private void puhastaKaart() {
        küsimus.setText("");
        vastus.setText("");
        küsimus.requestFocus();
    }

}