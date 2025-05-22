import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Antud klassi abil on võimalik läbi mängida/teha oma loodud flashkaartide kogumit. Mängides on võimalik
 * näidata vastust, peale mida saab liikuda järgmise kaardi juurde. Lisaks on võimalus alustada kaartide kogumit uuesti algusest
 */
public class FlashCardMängija {
    //private JTextArea display;
    private JLabel display;
    private ArrayList<FlashCard> kaardid;
    private Iterator<FlashCard> cardIterator;
    private FlashCard hetkeKaart;
    private JFrame frame;
    private boolean näitaVastus;
    private JButton näitaVastustNupp;
    private JButton käiUuestiLäbiNupp;
    private JButton ValeNupp;

    private ArrayList<FlashCard> ValestiVastatud;
    private ArrayList<FlashCard> Uuedkaardid;

    public FlashCardMängija() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
        } catch (Exception ignored) {}

        frame = new JFrame("FlashCard Mängija");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(new ImageIcon("flash-cards.png").getImage());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        JPanel keskPanel = new JPanel(new BorderLayout());

        Font font = new Font("Playfair Display", Font.PLAIN, 24);
        Font fontTekst = new Font("Comic Sans MS", Font.PLAIN, 24);

        display = new Taust("", "pilt.jpg");
        display.setFont(fontTekst);
        display.setVerticalAlignment(SwingConstants.CENTER);

        keskPanel.add(display, BorderLayout.CENTER);
        mainPanel.add(keskPanel, BorderLayout.CENTER);
        /*
        display = new JTextArea(10, 20);
        display.setFont(font);
        display.setLineWrap(true);
        display.setWrapStyleWord(true);
        display.setEditable(false);
        display.setBackground(new Color(255, 255, 255));
         */

        JScrollPane qScroll = new JScrollPane(display);
        qScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        //nupud kas vastuse näitamiseks või uuesti tegemiseks
        näitaVastustNupp = new JButton("Näita vastust");
        käiUuestiLäbiNupp = new JButton("Tee uuesti");

        // uued nupud õige ja vale märkimiseks
        ValeNupp = new JButton("Vale");


        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        näitaVastustNupp.setFont(buttonFont);
        käiUuestiLäbiNupp.setFont(buttonFont);
        // MUUDATUS
        ValeNupp.setFont(buttonFont);

        näitaVastustNupp.setBackground(Color.GRAY);
        näitaVastustNupp.setForeground(Color.WHITE);
        käiUuestiLäbiNupp.setBackground(Color.GRAY);
        käiUuestiLäbiNupp.setForeground(Color.WHITE);

        // MUUDATUS
        ValeNupp.setBackground(Color.RED);
        ValeNupp.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(näitaVastustNupp);
        buttonPanel.add(käiUuestiLäbiNupp);

        // MUUDATUS
        buttonPanel.add(ValeNupp);
        ValeNupp.setVisible(false);
        käiUuestiLäbiNupp.setVisible(false);

        buttonPanel.setBackground(Color.DARK_GRAY);


        mainPanel.add(qScroll, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.setBackground(Color.DARK_GRAY);


        //ActionListener, et saada aru kui nuppu on vajutatud,
        näitaVastustNupp.addActionListener(new järgmineKaart());

        //ActionListener, et saada aru kui uuesti läbikäimise nuppu vajutati
        // kui kaardid on olemas siis nuppu vajutades hakkavad küsimused otsast peale
        käiUuestiLäbiNupp.addActionListener(e -> {
            käiUuestiLäbiNupp.setVisible(false);
            näitaVastustNupp.setVisible(true);

            // kui valede vastuste listis on flashcardid kaime need labi
            if(!ValestiVastatud.isEmpty()){
                // kuva näitavastust nupud
                näitaVastustNupp.setVisible(true);

                näitaVastustNupp.setText("Näita vastust");
                näitaVastustNupp.setBackground(Color.GRAY);
                System.out.println("Võtab valede kaartide listi");
                Uuedkaardid = new ArrayList<>(ValestiVastatud);

                ValestiVastatud.clear();

                cardIterator = Uuedkaardid.iterator();

                näitaVastustNupp.setEnabled(true);
                näitaJärgmineKaart();


            }

            else if (kaardid != null && !kaardid.isEmpty() && ValestiVastatud.isEmpty()) {
                // kuva näitavastust nupud
                System.out.println("Võtab kaardid listi");
                käiUuestiLäbiNupp.setVisible(false);

                näitaVastustNupp.setText("Näita vastust");
                näitaVastustNupp.setBackground(Color.GRAY);

                cardIterator = kaardid.iterator();
                näitaVastustNupp.setEnabled(true);
                näitaJärgmineKaart();
            }

        });

        // MUUDATUS VALELE
        ValeNupp.addActionListener(new ValestiVastanud());

        // VALE ARRAYLIST
        ValestiVastatud = new ArrayList<>();

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
                näitaVastustNupp.setText("Õige"); //muudab nupu nimetust
                ValeNupp.setVisible(true);
                näitaVastustNupp.setBackground(Color.GREEN);
                näitaVastus = false;
            } else {
                if (cardIterator.hasNext()){
                    näitaJärgmineKaart();
                    näitaVastustNupp.setBackground(Color.GRAY);
                }

                else { //kaardid otsas
                    if(!ValestiVastatud.isEmpty()){
                        käiUuestiLäbiNupp.setText("Proovi veel");
                        käiUuestiLäbiNupp.setVisible(true);
                        display.setForeground(Color.RED);
                        display.setText("Vastasid mõne küsimuse valesti, proovi veel!");

                    }
                    else{
                        käiUuestiLäbiNupp.setText("Tee uuesti");
                        käiUuestiLäbiNupp.setVisible(true);
                        display.setForeground(Color.GREEN);
                        display.setText("See oli viimane kaart, hea töö!");

                    }

                    for (FlashCard flashCard : ValestiVastatud) {
                        System.out.println(flashCard);

                    }
                    näitaVastustNupp.setEnabled(false);
                    näitaVastustNupp.setVisible(false);
                    ValeNupp.setVisible(false);
                    näitaVastustNupp.setText("Näita vastust");
                    näitaVastustNupp.setBackground(Color.GRAY);
                }
            }
        }
    }

    class ValestiVastanud implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("VALE!");
            if (!ValestiVastatud.contains(hetkeKaart)) {
                ValestiVastatud.add(hetkeKaart);
            }
            //ValestiVastatud.add(hetkeKaart);

            if(cardIterator.hasNext()){
                näitaJärgmineKaart();
                ValeNupp.setVisible(false);
                näitaVastustNupp.setBackground(Color.GRAY);
            }
            else {
                if(!ValestiVastatud.isEmpty()){
                    käiUuestiLäbiNupp.setText("Proovi veel");
                    käiUuestiLäbiNupp.setVisible(true);
                    display.setText("Vastasid mõne küsimuse valesti, proovi veel!");

                }
                else{
                    käiUuestiLäbiNupp.setText("Tee uuesti");
                    käiUuestiLäbiNupp.setVisible(true);
                    display.setText("See oli viimane kaart, hea töö!");


                }
                for (FlashCard flashCard : ValestiVastatud) {
                    System.out.println(flashCard);
                }
                ValeNupp.setVisible(false);
                näitaVastustNupp.setVisible(false);

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
        try {
            BufferedReader reader = new BufferedReader(new FileReader(antudFail));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] osad = line.split(" -- ");
                if (osad.length != 2) {
                    display.setForeground(Color.RED);
                    display.setText("Faili formaat ei vasta nõuetele (küsimus -- vastus)");
                    throw new ValeFailiFormaatErind("Faili formaat ei vasta nõuetele (küsimus -- vastus)");
                }

                String küsimus = osad[0];
                String vastus = osad[1];

                FlashCard kaart = new FlashCard(küsimus, vastus);
                kaardid.add(kaart);
            }
            reader.close();

        } catch (ValeFailiFormaatErind e) {
            System.out.println("Viga: " + e.getMessage());
            // Kui vaja, saad siin lõpetada meetodi varakult:
            return;

        } catch (IOException e) {
            display.setForeground(Color.RED);
            display.setText("Ei saanud failist lugeda.");
            System.out.println("Ei saanud failist lugeda.");
            e.printStackTrace();

        } catch (Exception e) {
            display.setForeground(Color.RED);
            display.setText("Tundmatu viga failist lugemisel.");
            System.out.println("Tundmatu viga failist lugemisel.");
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
        display.setForeground(Color.BLUE);
        tekstiEffekt(hetkeKaart.getKüsimus());  //kuvab küsimuse fadeInText meetodi abil
        näitaVastustNupp.setText("Näita vastust"); //muudab nupu nime
        näitaVastus = true;
        ValeNupp.setVisible(false);
    }

    /**
     * Meetod toob teksti sujuvamalt sisse, alustades läbipaistvast ja jõudes täielikult nähtavaks
     * @param tekst tekst mida kuvatakse sujuva effektiga
     */
    private void tekstiEffekt(String tekst) {
        display.setText(tekst);
        display.setForeground(new Color(0, 0, 255, 0)); // Alguses läbipaistev

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
                display.setForeground(new Color(0, 0, 255, (int) (opacity * 255)));
            }
        });

        fadeTimer.start();
    }
}