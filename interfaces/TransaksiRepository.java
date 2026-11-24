package interfaces;

import exception.CafeException;
import java.util.List;
import model.CartItem;
import model.TransaksiRecord;

public interface TransaksiRepository extends Repository<TransaksiRecord> {
    void prosesTransaksiKeranjang(String kasirUsername, List<CartItem> items, double diskon) throws CafeException;
    List<String[]> getHistoryTransaksi() throws CafeException;
}