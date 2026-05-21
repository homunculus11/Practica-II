import java.util.List;
import java.util.Optional;

public interface Searchable<T> {
    Optional<T> cauta(int id);
    List<T> filtreaza(TipLectie tip);
}
