import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Student extends Persoana {
    private final LocalDate dataInrolare;
    private final List<Inrolare> inrolari = new ArrayList<>();
    private double progres;

    public Student(int id, String nume, String email, String parola, LocalDate dataInrolare) {
        super(id, nume, email, parola);
        this.dataInrolare = dataInrolare;
        this.progres = 0.0;
    }

    public LocalDate getDataInrolare() {
        return dataInrolare;
    }

    public List<Inrolare> getInrolarile() {
        return Collections.unmodifiableList(inrolari);
    }

    public void adaugaInrolare(Inrolare inrolare) {
        if (inrolare != null) {
            inrolari.add(inrolare);
        }
    }

    public void calculeazaProgres() {
        if (inrolari.isEmpty()) {
            progres = 0.0;
            return;
        }

        long completa = inrolari.stream()
                .filter(inrolare -> inrolare.getStatus() == StatusInrolare.FINALIZAT)
                .count();
        progres = 100.0 * completa / inrolari.size();
    }

    public double getProgres() {
        return progres;
    }

    @Override
    public String toString() {
        return String.format("Student{id=%d, nume='%s', email='%s', dataInrolare=%s, progres=%.1f%%}",
                getId(), getNume(), getEmail(), dataInrolare, progres);
    }
}
