import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Profesor extends Persoana {
    private final String specializare;
    private final List<Curs> cursuri = new ArrayList<>();
    private double rating;

    public Profesor(int id, String nume, String email, String parola, String specializare, double rating) {
        super(id, nume, email, parola);
        this.specializare = specializare;
        this.rating = rating;
    }

    public String getSpecializare() {
        return specializare;
    }

    public List<Curs> getCursuri() {
        return Collections.unmodifiableList(cursuri);
    }

    public void adaugaCurs(Curs curs) {
        if (curs != null && !cursuri.contains(curs)) {
            cursuri.add(curs);
        }
    }

    public void calculeazaRating() {
        if (cursuri.isEmpty()) {
            rating = 0.0;
            return;
        }

        rating = Math.min(5.0, 3.0 + 0.5 * cursuri.size());
    }

    public double getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return String.format("Profesor{id=%d, nume='%s', email='%s', specializare='%s', rating=%.2f, cursuri=%d}",
                getId(), getNume(), getEmail(), specializare, rating, cursuri.size());
    }
}
