import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.Random;

/**
 * Antud klassi abil on võimalik läbi mängida/teha oma loodud flashkaartide kogumit. Mängides on võimalik
 * näidata vastust, peale mida saab liikuda järgmise kaardi juurde. Lisaks on võimalus alustada kaartide kogumit uuesti algusest
 */
public class FlashCardMängija {
    private JTextArea display;
    private ArrayList<FlashCard> kaardid;
    private Iterator<FlashCard> cardIterator;
    private FlashCard hetkeKaart;
    private JFrame frame;
    private boolean näitaVastus;
    private JButton näitaVastustNupp;
    private JButton käiUuestiLäbiNupp;

    public FlashCardMängija() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        frame = new JFrame("FlashCard Mängija");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(new ImageIcon("icon.png").getImage());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        Font font = new Font("Helvetica Neue", Font.BOLD, 20);
        display = new JTextArea(10, 20);
        display.setFont(font);
        display.setLineWrap(true);
        display.setWrapStyleWord(true);
        display.setEditable(false);
        display.setBackground(new Color(255, 255, 255));

        JScrollPane qScroll = new JScrollPane(display);
        qScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        //nupud kas vastuse näitamiseks või uuesti tegemiseks
        näitaVastustNupp = new JButton("Näita vastust");
        käiUuestiLäbiNupp = new JButton("Tee uuesti");

        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        näitaVastustNupp.setFont(buttonFont);
        käiUuestiLäbiNupp.setFont(buttonFont);

        näitaVastustNupp.setBackground(new Color(104, 155, 198));
        näitaVastustNupp.setForeground(Color.WHITE);
        käiUuestiLäbiNupp.setBackground(new Color(104, 155, 198));
        käiUuestiLäbiNupp.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(näitaVastustNupp);
        buttonPanel.add(käiUuestiLäbiNupp);

        mainPanel.add(qScroll, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        //ActionListener, et saada aru kui nuppu on vajutatud,
        näitaVastustNupp.addActionListener(new järgmineKaart());

        //ActionListener, et saada aru kui uuesti läbikäimise nuppu vajutati
        // kui kaardid on olemas siis nuppu vajutades hakkavad küsimused otsast peale
        käiUuestiLäbiNupp.addActionListener(e -> {
            if (kaardid != null && !kaardid.isEmpty()) {
                cardIterator = kaardid.iterator();
                näitaVastustNupp.setEnabled(true);
                näitaJärgmineKaart();
            }
        });

        //nupud menüüreale
        JMenuBar menüü = new JMenuBar();
        JMenu fileMenu = new JMenu("Fail");
        JMenuItem laeMenüü = new JMenuItem("Lae kaartide kogum");
        laeMenüü.addActionListener(new laeMenüüListener());

        fileMenu.add(laeMenüü);
        menüü.add(fileMenu);
        frame.setJMenuBar(menüü);
        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FlashCardMängija::new);
    }

    /**
     * Antud klassi nii-öelda kuulab nuppu "järgmine kaart / näita vastust". Klassi
     * abil kuvatakse küsimuse vastus kui seda pole veel näidatud. Muul juhul kuvab järgmise kaardi.
     * Lisaks liigub ka järgmise kaarti juurde ja kui kaardid on läbi mängitud kuvab toreda teksti.
      */
    class järgmineKaart implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if(näitaVastus){
                tekstiEffekt(hetkeKaart.getVastus());
                näitaVastustNupp.setText("Järgmine kaart"); //muudab nupu nimetust
                näitaVastus = false;
            } else {
                if (cardIterator.hasNext()){
                    näitaJärgmineKaart();
                } else { //kaardid otsas
                    display.setText("See oli viimane kaart, hea töö!");
                    näitaVastustNupp.setEnabled(false);
                }
            }
        }
    }

    /**
     * Nii öelda "kuulaja", mis avab faili, kui oleme file exploreris valinud .txt faili ja vajutanud "Open" nuppu
     */
    private class laeMenüüListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser antudFailAvamiseks = new JFileChooser(); //loob failivaliku
            antudFailAvamiseks.showOpenDialog(frame); //kuvab seda kasutajale
            laeFail(antudFailAvamiseks.getSelectedFile()); //laeb meetodi abil faili
        }
    }

    /**
     * Meetod laeb ja töötab faili läbi,ehk splitib teksti " -- " juurest,
     * kus esimene pool on küsimus ja teine pool vastus, need listakse Flaschardi objektina kaartide Listi.
     * @param antudFail antud fail millest hakatakse lugema teksti kaartide ArrayListi.
     */
    private void laeFail(File antudFail) {
        kaardid = new ArrayList<FlashCard>();

        try{
            BufferedReader reader = new BufferedReader(new FileReader(antudFail));
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

        //segab järjekorra ära
        Collections.shuffle(kaardid, new Random());
        cardIterator = kaardid.iterator();
        näitaJärgmineKaart();
    }

    /**
     * Meetod abil liigutakse Iteratori abil järgmise kaardi juurde ja kuvatakse küsimust
     */
    private void näitaJärgmineKaart() {
        hetkeKaart = (FlashCard) cardIterator.next();
        tekstiEffekt(hetkeKaart.getKüsimus());  //kuvab küsimuse fadeInText meetodi abil
        näitaVastustNupp.setText("Näita vastust"); //muudab nupu nime
        näitaVastus = true;
    }

    /**
     * Meetod toob teksti sujuvamalt sisse, alustades läbipaistvast ja jõudes täielikult nähtavaks
     * @param tekst tekst mida kuvatakse sujuva effektiga
     */
    private void tekstiEffekt(String tekst) {
        display.setText(tekst);
        display.setForeground(new Color(0, 0, 0, 0)); // Alguses läbipaistev

        Timer fadeTimer = new Timer(50, new ActionListener() {
            float opacity = 0.0f; // Läbipaistvus algab nullist

            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.1f;
                if (opacity >= 1.0f) { //kui tekst jõuab nähtavuseni siis peatub
                    opacity = 1.0f;
                    ((Timer) e.getSource()).stop(); // Kui täisnähtav, peatame
                }
                //uuendab teksti värvi
                display.setForeground(new Color(0, 0, 0, (int) (opacity * 255)));
            }
        });

        fadeTimer.start();
    }
}
