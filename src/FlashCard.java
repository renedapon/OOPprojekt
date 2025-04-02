/**
 * Klass Flashcard sisaldab nii küsimust kui ka vastust, sealt on võimali getterite abil kätte saada
 * nii küsimuse kui ka vastuse, kui ka setterite abil võimalik uued määrata
 */
public class FlashCard {
    private String küsimus;
    private String vastus;

    public FlashCard(String küsimus, String vastus) {
        this.küsimus = küsimus;
        this.vastus = vastus;
    }

    public String getKüsimus() {
        return küsimus;
    }

    public void setKüsimus(String küsimus) {
        this.küsimus = küsimus;
    }

    public String getVastus() {
        return vastus;
    }

    public void setVastus(String vastus) {
        this.vastus = vastus;
    }

}
