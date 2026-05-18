import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Curs implements Exportable, Reportable, Searchable<Lectie> {
    private final int id;
    private final String titlu;
    private final String descriere;
    private final double pret;
    private final NivelCurs nivel;
    private final int durata;
    private final Profesor profesor;
    private final List<Lectie> lectii = new ArrayList<>();

    public Curs(int id, String titlu, String descriere, double pret, NivelCurs nivel, int durata, Profesor profesor) {
        this.id = id;
        this.titlu = titlu;
        this.descriere = descriere;
        this.pret = pret;
        this.nivel = nivel;
        this.durata = durata;
        this.profesor = profesor;
    }

    public int getId() {
        return id;
    }

    public String getTitlu() {
        return titlu;
    }

    public String getDescriere() {
        return descriere;
    }

    public double getPret() {
        return pret;
    }

    public NivelCurs getNivel() {
        return nivel;
    }

    public int getDurata() {
        return durata;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public List<Lectie> getLectii() {
        return new ArrayList<>(lectii);
    }

    public void adaugaLectie(Lectie lectie) {
        if (lectie != null) {
            lectii.add(lectie);
        }
    }

    @Override
    public String export() {
        return String.format("%d,%s,%s,%.2f,%s,%d,%s", id, titlu, nivel, pret, descriere, durata, profesor.getNume());
    }

    @Override
    public String getFormat() {
        return "CSV";
    }

    @Override
    public String genRaport() {
        return String.format("Curs[id=%d, titlu='%s', nivel=%s, durata=%d minute, lectii=%d, profesor='%s']", id, titlu, nivel, durata, lectii.size(), profesor.getNume());
    }

    @Override
    public Map<String, Object> getStatistici() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("lectiiCount", lectii.size());
        stats.put("durataTotala", lectii.stream().mapToInt(Lectie::getDurata).sum());
        stats.put("pret", pret);
        return stats;
    }

    @Override
    public Optional<Lectie> cauta(int id) {
        return lectii.stream().filter(lectie -> lectie.getId() == id).findFirst();
    }

    @Override
    public List<Lectie> filtreaza(TipLectie tip) {
        List<Lectie> rezultate = new ArrayList<>();
        for (Lectie lectie : lectii) {
            if (lectie.getTip() == tip) {
                rezultate.add(lectie);
            }
        }
        return rezultate;
    }

    @Override
    public String toString() {
        return String.format("Curs{id=%d, titlu='%s', nivel=%s, pret=%.2f, durata=%d, profesor='%s'}", id, titlu, nivel, pret, durata, profesor.getNume());
    }
}
