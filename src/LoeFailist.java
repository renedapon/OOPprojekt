import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LoeFailist {
    public String failinimi;

    public LoeFailist(String failinimi) {
        this.failinimi = failinimi;
    }

    public List<String>  FailistKüsimused() throws IOException {
        List<String> küsimused = new ArrayList<>();
        File fail = new File(failinimi);
        Scanner sc = new Scanner(fail, StandardCharsets.UTF_8);
        while (sc.hasNextLine()){
            String rida = sc.nextLine();
            String[] osad = rida.split(" -- ");
            String küsimus = osad[0];
            küsimused.add(küsimus);
        }
        return küsimused;
    }

    public List<String>  FailistVastused() throws IOException {
        List<String> vastused = new ArrayList<>();
        File fail = new File(failinimi);
        Scanner sc = new Scanner(fail, StandardCharsets.UTF_8);
        while (sc.hasNextLine()){
            String rida = sc.nextLine();
            String[] osad = rida.split(" -- ");
            String vastus = osad[1];
            vastused.add(vastus);
        }
        return vastused;
    }
}
