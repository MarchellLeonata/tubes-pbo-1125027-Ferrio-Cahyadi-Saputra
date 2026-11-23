package interfaces;

import exception.CafeException;
import model.BahanBaku;

public interface BahanBakuRepository extends Repository<BahanBaku> {
    void updateStok(int id, double perubahan) throws CafeException;
}