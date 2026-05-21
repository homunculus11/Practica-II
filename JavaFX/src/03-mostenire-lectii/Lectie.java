public abstract class Lectie {
    private final int id;
    private final String titlu;
    private final int durata;
    private final TipLectie tip;

    public Lectie(int id, String titlu, int durata, TipLectie tip) {
        this.id = id;
        this.titlu = titlu;
        this.durata = durata;
        this.tip = tip;
    }

    public int getId() {
        return id;
    }

    public String getTitlu() {
        return titlu;
    }

    public int getDurata() {
        return durata;
    }

    public TipLectie getTip() {
        return tip;
    }

    public abstract String reda();
}
