import java.time.LocalDate;

public class Inrolare {
    private final int id;
    private final Student student;
    private final Curs curs;
    private StatusInrolare status;
    private final LocalDate dataStart;

    public Inrolare(int id, Student student, Curs curs, StatusInrolare status, LocalDate dataStart) {
        this.id = id;
        this.student = student;
        this.curs = curs;
        this.status = status;
        this.dataStart = dataStart;
    }

    public int getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public Curs getCurs() {
        return curs;
    }

    public StatusInrolare getStatus() {
        return status;
    }

    public LocalDate getDataStart() {
        return dataStart;
    }

    public void actualizeaza(StatusInrolare status) {
        if (status != null) {
            this.status = status;
        }
    }

    @Override
    public String toString() {
        return String.format("Inrolare{id=%d, student=%s, curs=%s, status=%s, dataStart=%s}",
                id, student.getNume(), curs.getTitlu(), status, dataStart);
    }
}
