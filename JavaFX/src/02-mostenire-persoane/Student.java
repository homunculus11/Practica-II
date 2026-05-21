import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Student extends Persoana {
    private final LocalDate dataInrolare;
    private final List<Inrolare> inrolari = new ArrayList<>();
    private int numarInrolari;

    public Student(int id, String nume, String email, String parola, LocalDate dataInrolare) {
        super(id, nume, email, parola);
        this.dataInrolare = dataInrolare;
        this.numarInrolari = 0;
    }

    public LocalDate getDataInrolare() {
        return dataInrolare;
    }

    public List<Inrolare> getînrolările() {
        return Collections.unmodifiableList(inrolari);
    }

    public void adaugaInrolare(Inrolare inrolare) {
        if (inrolare != null) {
            inrolari.add(inrolare);
        }
    }

    public void calculeazaProgres() {
        numarInrolari = inrolari.size();
    }

    public double getProgres() {
        return numarInrolari;
    }

    public int getNumarInrolari() {
        return numarInrolari;
    }

    public void setNumarInrolari(int numarInrolari) {
        this.numarInrolari = Math.max(0, numarInrolari);
    }

    @Override
    public String toString() {
        return String.format("Student{id=%d, nume='%s', email='%s', dataInrolare=%s, inrolari=%d}",
                getId(), getNume(), getEmail(), dataInrolare, numarInrolari);
    }
}
