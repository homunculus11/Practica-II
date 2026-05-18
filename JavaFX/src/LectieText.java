public class LectieText extends Lectie {
    private final String continut;
    private final int nrCuvinte;

    public LectieText(int id, String titlu, int durata, TipLectie tip, String continut, int nrCuvinte) {
        super(id, titlu, durata, tip);
        this.continut = continut;
        this.nrCuvinte = nrCuvinte;
    }

    public String getContinut() {
        return continut;
    }

    public int getNrCuvinte() {
        return nrCuvinte;
    }

    @Override
    public String reda() {
        return String.format("Lectie text: %s - %d cuvinte", getTitlu(), nrCuvinte);
    }
}
