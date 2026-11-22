package controller;

import exception.AksesDitolakException;
import exception.CafeException;
import java.util.List;
import model.BahanBaku;
import model.User;
import repository.DatabaseManager;
import strategy.DiscountStrategy;
import strategy.NoDiscountStrategy;
import strategy.QuantityDiscountStrategy;

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

   // MEMPROSES TRANSAKSI DENGAN STRATEGI DISKON
    public void prosesTransaksi(String username, int menuId, int jumlah)
        throws CafeException {

        DiscountStrategy strategy;

        if (jumlah >= 5) {
            strategy = new QuantityDiscountStrategy();
        } else {
            strategy = new NoDiscountStrategy();
        }

        double diskon = strategy.getDiscountRate(jumlah);

        db.prosesTransaksi(username, menuId, jumlah, diskon);
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

    // VALIDASI TRANSAKSI
    public void validasiTransaksi(int jumlah) throws CafeException {
        if (jumlah <= 0) {
            throw new CafeException("Jumlah beli harus lebih dari 0!");
        }
    }

    // VALIDASI PERUBAHAN STOK
    public void validasiPerubahanStok(double jumlah) throws CafeException {
        if (jumlah <= 0) {
            throw new CafeException("Jumlah perubahan harus lebih dari 0!");
        }
    }

    // VALIDASI LOGIN
    public void validasiLogin(String username, String password)
        throws CafeException {

        if (username.isEmpty() || password.isEmpty()) {
            throw new CafeException("Username dan password wajib diisi!");
        }
    }

    // CEK HAK AKSES KASIR
    public boolean bolehAksesKasir(User user) {
        return user.bolehAksesKasir();
    }

    // CEK HAK AKSES GUDANG
    public boolean bolehAksesGudang(User user) {
        return user.bolehAksesGudang();
    }


}
