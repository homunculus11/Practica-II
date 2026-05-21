import java.util.LinkedHashMap;
import java.util.Map;

public class RomanianText {
    private static final Map<String, String> REPLACEMENTS = new LinkedHashMap<>();

    static {
        // UTF-8 text that was read with the wrong Windows code page.
        REPLACEMENTS.put("Гў", "â");
        REPLACEMENTS.put("î", "î");
        REPLACEMENTS.put("ă", "ă");
        REPLACEMENTS.put("Д‚", "Ă");
        REPLACEMENTS.put("ș", "ș");
        REPLACEMENTS.put("И", "Ș");
        REPLACEMENTS.put("ț", "ț");
        REPLACEMENTS.put("Ић", "Ț");

        // Common values already damaged in SQL Server as question marks.
        REPLACEMENTS.put("Romana", "Română");
        REPLACEMENTS.put("Engleza", "Engleză");
        REPLACEMENTS.put("Rusa", "Rusă");
        REPLACEMENTS.put("Franceza", "Franceză");
        REPLACEMENTS.put("Fără grad didactic", "Fără grad didactic");
        REPLACEMENTS.put("Făra grad didactic", "Fără grad didactic");
        REPLACEMENTS.put("fara", "fără");
        REPLACEMENTS.put("Fără", "Fără");
        REPLACEMENTS.put("Stapanirea", "Stăpânirea");
        REPLACEMENTS.put("stapanirea", "stăpânirea");
        REPLACEMENTS.put("in programarea", "în programarea");
        REPLACEMENTS.put("in baza", "în baza");
        REPLACEMENTS.put("incepatori", "începători");
        REPLACEMENTS.put("avansata", "avansată");
        REPLACEMENTS.put("moderna", "modernă");
        REPLACEMENTS.put("esentiale", "esențiale");
        REPLACEMENTS.put("esential", "esențial");

        REPLACEMENTS.put(" și ", " și ");
        REPLACEMENTS.put("și ", "Și ");
        REPLACEMENTS.put(" și ", " și ");
        REPLACEMENTS.put("nașterii", "nașterii");
        REPLACEMENTS.put("studenți", "studenți");
        REPLACEMENTS.put("studenții", "studenții");
        REPLACEMENTS.put("profesorii", "profesorii");
        REPLACEMENTS.put("rapoarte și", "rapoarte și");
        REPLACEMENTS.put("cauta", "caută");
        REPLACEMENTS.put("Caută", "Caută");
        REPLACEMENTS.put("adauga", "adaugă");
        REPLACEMENTS.put("Adaugă", "Adaugă");
        REPLACEMENTS.put("sterge", "șterge");
        REPLACEMENTS.put("Șterge", "Șterge");
        REPLACEMENTS.put("Șterge", "Șterge");
        REPLACEMENTS.put("Ștergere", "Ștergere");

        REPLACEMENTS.put("Cuînir", "Cușnir");
        REPLACEMENTS.put("?chiopu", "Șchiopu");
        REPLACEMENTS.put("Roșca", "Roșca");
        REPLACEMENTS.put("Dumitrița", "Dumitrița");
        REPLACEMENTS.put("esenșiale", "esențiale");
        REPLACEMENTS.put("Ștefan", "Ștefan");
        REPLACEMENTS.put("Ștefania", "Ștefania");
        REPLACEMENTS.put("Gheorghiță", "Gheorghiță");
        REPLACEMENTS.put("Crețu", "Crețu");
        REPLACEMENTS.put("Musteață", "Musteată");
        REPLACEMENTS.put("Pânzari", "Pînzari");
        REPLACEMENTS.put("Pinzari", "Pînzari");
    }

    public static String clean(String value) {
        if (value == null) {
            return "";
        }

        String text = value.trim();
        for (Map.Entry<String, String> entry : REPLACEMENTS.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }
        return text;
    }
}
