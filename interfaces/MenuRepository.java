package interfaces;

import exception.CafeException;
import java.util.List;
import model.MenuItem;

public interface MenuRepository extends Repository<MenuItem> {
    List<String[]> getDaftarMenu() throws CafeException;
}