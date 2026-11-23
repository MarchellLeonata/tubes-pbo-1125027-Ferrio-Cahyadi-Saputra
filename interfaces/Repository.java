package interfaces;
import exception.CafeException;
import java.util.List;

public interface Repository<T> {
    List<T> getAll() throws CafeException;
}