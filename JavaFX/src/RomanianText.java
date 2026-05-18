import java.util.LinkedHashMap;
import java.util.Map;

public class RomanianText {
    private static final Map<String, String> REPLACEMENTS = new LinkedHashMap<>();

    static {
        // UTF-8 text that was read with the wrong Windows code page.
        REPLACEMENTS.put("Гў", "â");
        REPLACEMENTS.put("Г®", "î");
        REPLACEMENTS.put("Дѓ", "ă");
        REPLACEMENTS.put("Д‚", "Ă");
        REPLACEMENTS.put("И™", "ș");
        REPLACEMENTS.put("И", "Ș");
        REPLACEMENTS.put("И›", "ț");
        REPLACEMENTS.put("Ић", "Ț");

        // Common values already damaged in SQL Server as question marks.
        REPLACEMENTS.put("Romana", "Română");
        REPLACEMENTS.put("Engleza", "Engleză");
        REPLACEMENTS.put("Rusa", "Rusă");
        REPLACEMENTS.put("Franceza", "Franceză");
        REPLACEMENTS.put("Fara grad didactic", "Fără grad didactic");
        REPLACEMENTS.put("Făra grad didactic", "Fără grad didactic");
        REPLACEMENTS.put("fara", "fără");
        REPLACEMENTS.put("Fara", "Fără");
        REPLACEMENTS.put("Stapanirea", "Stăpânirea");
        REPLACEMENTS.put("stapanirea", "stăpânirea");
        REPLACEMENTS.put("in programarea", "în programarea");
        REPLACEMENTS.put("in baza", "în baza");
        REPLACEMENTS.put("incepatori", "începători");
        REPLACEMENTS.put("avansata", "avansată");
        REPLACEMENTS.put("moderna", "modernă");
        REPLACEMENTS.put("esentiale", "esențiale");
        REPLACEMENTS.put("esential", "esențial");

        REPLACEMENTS.put(" ?i ", " și ");
        REPLACEMENTS.put("?i ", "Și ");
        REPLACEMENTS.put(" si ", " și ");
        REPLACEMENTS.put("na?terii", "nașterii");
        REPLACEMENTS.put("studen?i", "studenți");
        REPLACEMENTS.put("studen?ii", "studenții");
        REPLACEMENTS.put("profesorii", "profesorii");
        REPLACEMENTS.put("rapoarte ?i", "rapoarte și");
        REPLACEMENTS.put("cauta", "caută");
        REPLACEMENTS.put("Cauta", "Caută");
        REPLACEMENTS.put("adauga", "adaugă");
        REPLACEMENTS.put("Adauga", "Adaugă");
        REPLACEMENTS.put("sterge", "șterge");
        REPLACEMENTS.put("Sterge", "Șterge");
        REPLACEMENTS.put("?terge", "Șterge");
        REPLACEMENTS.put("?tergere", "Ștergere");

        REPLACEMENTS.put("Cu?nir", "Cușnir");
        REPLACEMENTS.put("?chiopu", "Șchiopu");
        REPLACEMENTS.put("Ro?ca", "Roșca");
        REPLACEMENTS.put("Dumitri?a", "Dumitrița");
        REPLACEMENTS.put("esen?iale", "esențiale");
        REPLACEMENTS.put("?tefan", "Ștefan");
        REPLACEMENTS.put("?tefania", "Ștefania");
        REPLACEMENTS.put("Gheorghi?ă", "Gheorghiță");
        REPLACEMENTS.put("Cre?u", "Crețu");
        REPLACEMENTS.put("Mustea?ă", "Musteată");
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
