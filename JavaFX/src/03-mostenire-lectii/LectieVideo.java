public class LectieVideo extends Lectie {
    private final String urlVideo;
    private final String rezolutie;

    public LectieVideo(int id, String titlu, int durata, TipLectie tip, String urlVideo, String rezolutie) {
        super(id, titlu, durata, tip);
        this.urlVideo = urlVideo;
        this.rezolutie = rezolutie;
    }

    public String getUrlVideo() {
        return urlVideo;
    }

    public String getRezolutie() {
        return rezolutie;
    }

    @Override
    public String reda() {
        return String.format("Redare video: %s (%s) la %s", getTitlu(), rezolutie, urlVideo);
    }
}
