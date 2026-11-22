package controller;

import exception.AksesDitolakException;
import exception.CafeException;
import java.util.List;
import model.BahanBaku;
import model.User;
import repository.DatabaseManager;

public class CafeController {

    private DatabaseManager db;

    public CafeController() {
        db = new DatabaseManager();
    }

    // LOGIN
    public User login(String username, String password) throws CafeException {
        return db.login(username, password);
    }

    // MENGAMBIL DAFTAR MENU
    public List<String[]> getDaftarMenu() throws CafeException {
        return db.getDaftarMenu();
    }

    // MEMPROSES TRANSAKSI
    public void prosesTransaksi(String username, int menuId, int jumlah)
            throws CafeException {
        db.prosesTransaksi(username, menuId, jumlah);
    }

    // MENGUBAH STOK BAHAN
    public void updateStok(int idBahan, double perubahan)
            throws CafeException {
        db.updateStok(idBahan, perubahan);
    }

    // MENGAMBIL SEMUA BAHAN BAKU
    public List<BahanBaku> getDaftarBahanBaku() throws CafeException {
        return db.getAll();
    }

    // MENGAMBIL HISTORY TRANSAKSI
    public List<String[]> getHistoryTransaksi() throws CafeException {
        return db.getHistoryTransaksi();
    }

    // CEK AKSES PANEL KASIR
    public void cekAksesKasir(User user) throws CafeException {
        if (!user.bolehAksesKasir()) {
            throw new AksesDitolakException(user.getRole());
        }
    }

    // CEK AKSES PANEL GUDANG
    public void cekAksesGudang(User user) throws CafeException {
        if (!user.bolehAksesGudang()) {
            throw new AksesDitolakException(user.getRole());
    }
}
}
