package interfaces;

import exception.CafeException;
import java.util.List;
import model.TransaksiRecord;

public interface TransaksiRepository extends Repository<TransaksiRecord> {
    void prosesTransaksi(String kasirUsername, int menuId, int jumlahBeli, double diskon) throws CafeException;
    List<String[]> getHistoryTransaksi() throws CafeException;
}