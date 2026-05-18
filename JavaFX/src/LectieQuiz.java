import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LectieQuiz extends Lectie {
    private final List<String> intrebari;
    private final int punctajMax;

    public LectieQuiz(int id, String titlu, int durata, TipLectie tip, List<String> intrebari, int punctajMax) {
        super(id, titlu, durata, tip);
        this.intrebari = new ArrayList<>(intrebari);
        this.punctajMax = punctajMax;
    }

    public List<String> getIntrebari() {
        return Collections.unmodifiableList(intrebari);
    }

    public int getPunctajMax() {
        return punctajMax;
    }

    public int evalueaza(List<String> raspunsuri) {
        if (raspunsuri == null || raspunsuri.isEmpty()) {
            return 0;
        }
        int raspunsCorect = Math.min(raspunsuri.size(), intrebari.size());
        int scorePerItem = punctajMax / Math.max(1, intrebari.size());
        return Math.min(punctajMax, raspunsCorect * scorePerItem);
    }

    @Override
    public String reda() {
        return String.format("Quiz: %s - %d intrebari, punctaj maxim %d", getTitlu(), intrebari.size(), punctajMax);
    }
}
