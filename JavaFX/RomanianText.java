public class RomanianText {
    public static String clean(String value) {
        if (value == null) {
            return "";
        }

        String text = value.trim();
        text = text.replace("Romana", "Română")
                .replace("Engleza", "Engleză")
                .replace("Rusa", "Rusă")
                .replace("Franceza", "Franceză")
                .replace("Fara grad didactic", "Fără grad didactic")
                .replace("Stapanirea", "Stăpânirea")
                .replace("in programarea", "în programarea")
                .replace("in baza", "în baza")
                .replace("avansata", "avansată")
                .replace("moderna", "modernă")
                .replace("incepatori", "începători")
                .replace("esentiale", "esențiale")
                .replace("esential", "esențial");

        text = text.replace(" ?i ", " și ")
                .replace("?i ", "Și ")
                .replace(" si ", " și ")
                .replace("Cu?nir", "Cușnir")
                .replace("?chiopu", "Șchiopu")
                .replace("Ro?ca", "Roșca")
                .replace("Dumitri?a", "Dumitrița")
                .replace("esen?iale", "esențiale")
                .replace("?tefan", "Ștefan")
                .replace("?tefania", "Ștefania")
                .replace("Gheorghi?ă", "Gheorghiță")
                .replace("Cre?u", "Crețu")
                .replace("Mustea?ă", "Musteată")
                .replace("Pânzari", "Pînzari")
                .replace("Pinzari", "Pînzari");

        return text;
    }
}
