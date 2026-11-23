package controller;

import exception.AksesDitolakException;
import exception.CafeException;
import interfaces.*;
import java.util.List;
import model.BahanBaku;
import model.User;
import repository.*;
import strategy.DiscountStrategy;
import strategy.DiscountStrategyFactory;

public class CafeController {

    private UserRepository userRepo;
    private BahanBakuRepository bahanBakuRepo;
    private MenuRepository menuRepo;
    private TransaksiRepository transaksiRepo;

    public CafeController() {
        this(
            new DBUserRepository(),
            new DBBahanBakuRepository(),
            new DBMenuRepository(),
            new DBTransaksiRepository()
        );
    }

    public CafeController(
            UserRepository userRepo,
            BahanBakuRepository bahanBakuRepo,
            MenuRepository menuRepo,
            TransaksiRepository transaksiRepo) {
        this.userRepo = userRepo;
        this.bahanBakuRepo = bahanBakuRepo;
        this.menuRepo = menuRepo;
        this.transaksiRepo = transaksiRepo;
    }

    // LOGIN
    public User login(String username, String password) throws CafeException {
        return userRepo.login(username, password);
    }

    // MENGAMBIL DAFTAR MENU
    public List<String[]> getDaftarMenu() throws CafeException {
        return menuRepo.getDaftarMenu();
    }

    // MEMPROSES TRANSAKSI DENGAN STRATEGI DISKON
    public void prosesTransaksi(String username, int menuId, int jumlah)
        throws CafeException {

        double diskon = getDiskonRate(jumlah);

        transaksiRepo.prosesTransaksi(username, menuId, jumlah, diskon);
    }

    // MENGUBAH STOK BAHAN
    public void updateStok(int idBahan, double perubahan)
            throws CafeException {
        bahanBakuRepo.updateStok(idBahan, perubahan);
    }

    // MENGAMBIL SEMUA BAHAN BAKU
    public List<BahanBaku> getDaftarBahanBaku() throws CafeException {
        return bahanBakuRepo.getAll();
    }

    // MENGAMBIL HISTORY TRANSAKSI
    public List<String[]> getHistoryTransaksi() throws CafeException {
        return transaksiRepo.getHistoryTransaksi();
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

    // MENGAMBIL PERSENTASE DISKON
    public double getDiskonRate(int jumlah) {
        DiscountStrategy strategy =
        DiscountStrategyFactory.createStrategy(jumlah);

        return strategy.getDiscountRate(jumlah);
    }
}