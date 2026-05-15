import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RaportService implements Reportable, Exportable {
    private final List<Curs> cursuri;
    private final List<Inrolare> inrolari;
    private final List<Student> studenti;

    public RaportService(List<Curs> cursuri, List<Inrolare> inrolari, List<Student> studenti) {
        this.cursuri = new ArrayList<>(cursuri);
        this.inrolari = new ArrayList<>(inrolari);
        this.studenti = new ArrayList<>(studenti);
    }

    public String raportStudentiPerCurs() {
        Map<String, Long> countPerCurs = inrolari.stream()
                .collect(Collectors.groupingBy(inrolare -> inrolare.getCurs().getTitlu(), Collectors.counting()));
        StringBuilder builder = new StringBuilder("Raport studenti per curs:\n");
        countPerCurs.forEach((titlu, count) -> builder.append(String.format("- %s: %d studenti\n", titlu, count)));
        return builder.toString();
    }

    public String raportVenituriProfesor() {
        Map<String, Double> venitPerProfesor = new HashMap<>();
        for (Inrolare inrolare : inrolari) {
            String profesor = inrolare.getCurs().getProfesor().getNume();
            venitPerProfesor.merge(profesor, inrolare.getCurs().getPret(), Double::sum);
        }
        StringBuilder builder = new StringBuilder("Raport venituri profesori:\n");
        venitPerProfesor.forEach((nume, venit) -> builder.append(String.format("- %s: %.2f lei\n", nume, venit)));
        return builder.toString();
    }

    public String raportCursuriPopulare() {
        Map<String, Long> countPerCurs = inrolari.stream()
                .collect(Collectors.groupingBy(inrolare -> inrolare.getCurs().getTitlu(), Collectors.counting()));
        String popular = countPerCurs.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(entry -> String.format("- %s (%d inrolari)", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));
        return "Raport cursuri populare:\n" + popular + "\n";
    }

    @Override
    public String genRaport() {
        StringBuilder builder = new StringBuilder();
        builder.append(raportStudentiPerCurs()).append("\n");
        builder.append(raportVenituriProfesor()).append("\n");
        builder.append(raportCursuriPopulare());
        return builder.toString();
    }

    @Override
    public Map<String, Object> getStatistici() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("cursuri", cursuri.size());
        stats.put("inrolari", inrolari.size());
        stats.put("studenti", studenti.size());
        return stats;
    }

    @Override
    public String export() {
        return exportCSV();
    }

    @Override
    public String getFormat() {
        return "CSV";
    }

    public String exportCSV() {
        StringBuilder builder = new StringBuilder("Profesor,Curs,Pret\n");
        for (Curs curs : cursuri) {
            builder.append(String.format("%s,%s,%.2f\n", curs.getProfesor().getNume(), curs.getTitlu(), curs.getPret()));
        }
        return builder.toString();
    }

    public String exportTXT() {
        return genRaport();
    }
}
