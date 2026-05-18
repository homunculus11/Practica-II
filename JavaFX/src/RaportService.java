import java.util.ArrayList;
import java.util.HashMap;
import java.util.Comparator;
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
        if (countPerCurs.isEmpty()) {
            return builder.append("- Nu exista inrolari.\n").toString();
        }
        countPerCurs.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> builder.append(String.format("- %s: %d studenti\n", entry.getKey(), entry.getValue())));
        return builder.toString();
    }

    public String raportCursuriPopulare() {
        Map<String, Long> countPerCurs = inrolari.stream()
                .collect(Collectors.groupingBy(inrolare -> inrolare.getCurs().getTitlu(), Collectors.counting()));
        String popular = countPerCurs.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(entry -> String.format("- %s (%d inrolari)", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));
        return "Raport cursuri populare:\n" + (popular.isEmpty() ? "- Nu exista inrolari." : popular) + "\n";
    }

    public String raportSumarGeneral() {
        double venitTotal = inrolari.stream().mapToDouble(inrolare -> inrolare.getCurs().getPret()).sum();
        double pretMediu = cursuri.stream().mapToDouble(Curs::getPret).average().orElse(0);
        return String.format("""
                Raport sumar general:
                - Cursuri: %d
                - Studenti: %d
                - Inrolari: %d
                - Venit estimat din inrolari: %.2f lei
                - Pret mediu curs: %.2f lei
                """, cursuri.size(), studenti.size(), inrolari.size(), venitTotal, pretMediu);
    }

    public String raportCatalogCursuri() {
        StringBuilder builder = new StringBuilder("Raport catalog cursuri:\n");
        if (cursuri.isEmpty()) {
            return builder.append("- Nu exista cursuri.\n").toString();
        }

        builder.append(String.format("%-6s | %-45s | %-10s | %12s | %-30s\n",
                "ID", "Denumire curs", "Tip", "Pret", "Coordonator"));
        builder.append("-".repeat(116)).append("\n");
        cursuri.stream()
                .sorted(Comparator.comparing(Curs::getTitlu))
                .forEach(curs -> builder.append(String.format("%-6d | %-45s | %-10s | %9.2f lei | %-30s\n",
                        curs.getId(),
                        trim(curs.getTitlu(), 45),
                        trim(curs.getDescriere(), 10),
                        curs.getPret(),
                        trim(curs.getProfesor().getNume(), 30))));
        return builder.toString();
    }

    public String raportPreturiCursuri() {
        StringBuilder builder = new StringBuilder("Raport preturi cursuri:\n");
        if (cursuri.isEmpty()) {
            return builder.append("- Nu exista cursuri.\n").toString();
        }

        Curs ieftin = cursuri.stream().min(Comparator.comparingDouble(Curs::getPret)).orElse(null);
        Curs scump = cursuri.stream().max(Comparator.comparingDouble(Curs::getPret)).orElse(null);
        double medie = cursuri.stream().mapToDouble(Curs::getPret).average().orElse(0);

        builder.append(String.format("- Cel mai ieftin: %s - %.2f lei\n", ieftin.getTitlu(), ieftin.getPret()));
        builder.append(String.format("- Cel mai scump: %s - %.2f lei\n", scump.getTitlu(), scump.getPret()));
        builder.append(String.format("- Pret mediu: %.2f lei\n", medie));
        return builder.toString();
    }

    public String raportInrolariStudenti() {
        Map<String, Long> countPerStudent = inrolari.stream()
                .collect(Collectors.groupingBy(inrolare -> inrolare.getStudent().getNume(), Collectors.counting()));
        String raport = countPerStudent.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(entry -> String.format("- %s: %d cursuri", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));
        return "Raport inrolari studenti:\n" + (raport.isEmpty() ? "- Nu exista inrolari." : raport) + "\n";
    }

    public String genereaza(String tipRaport) {
        return switch (tipRaport) {
            case "Sumar general" -> raportSumarGeneral();
            case "Studenti per curs" -> raportStudentiPerCurs();
            case "Cursuri populare" -> raportCursuriPopulare();
            case "Catalog cursuri" -> raportCatalogCursuri();
            case "Preturi cursuri" -> raportPreturiCursuri();
            case "Inrolari studenti" -> raportInrolariStudenti();
            default -> genRaport();
        };
    }

    @Override
    public String genRaport() {
        StringBuilder builder = new StringBuilder();
        builder.append(raportSumarGeneral()).append("\n");
        builder.append(raportStudentiPerCurs()).append("\n");
        builder.append(raportCursuriPopulare()).append("\n");
        builder.append(raportCatalogCursuri()).append("\n");
        builder.append(raportPreturiCursuri()).append("\n");
        builder.append(raportInrolariStudenti());
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

    private String trim(String value, int maxLength) {
        String text = value == null ? "" : value;
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, Math.max(0, maxLength - 3)) + "...";
    }
}
