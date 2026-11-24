package controller;

import exception.AksesDitolakException;
import exception.CafeException;
import interfaces.*;
import java.util.List;
import model.BahanBaku;
import model.CartItem;
import model.KustomisasiOpsi;
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

    public User login(String username, String password) throws CafeException {
        return userRepo.login(username, password);
    }

    public List<String[]> getDaftarMenu() throws CafeException {
        return menuRepo.getDaftarMenu();
    }

    public List<KustomisasiOpsi> getDaftarKustomisasi() throws CafeException {
        return menuRepo.getDaftarKustomisasi();
    }

    public void prosesKeranjang(String username, List<CartItem> items)
            throws CafeException {
        if (items == null || items.isEmpty()) {
            throw new CafeException("Keranjang masih kosong! Tambahkan menu terlebih dahulu.");
        }

        int totalPorsi = hitungTotalPorsi(items);
        double diskon = getDiskonRate(totalPorsi);

        transaksiRepo.prosesTransaksiKeranjang(username, items, diskon);
    }

    public int hitungTotalPorsi(List<CartItem> items) {
        int total = 0;
        for (CartItem item : items) {
            total += item.getJumlah();
        }
        return total;
    }

    public double hitungSubtotalKeranjang(List<CartItem> items) {
        double subtotal = 0;
        for (CartItem item : items) {
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }

    public void updateStok(int idBahan, double perubahan)
            throws CafeException {
        bahanBakuRepo.updateStok(idBahan, perubahan);
    }

    public List<BahanBaku> getDaftarBahanBaku() throws CafeException {
        return bahanBakuRepo.getAll();
    }

    public List<String[]> getHistoryTransaksi() throws CafeException {
        return transaksiRepo.getHistoryTransaksi();
    }

    public void cekAksesKasir(User user) throws CafeException {
        if (user == null || !user.bolehAksesKasir()) {
            throw new AksesDitolakException(user == null ? "UNKNOWN" : user.getRole());
        }
    }

    public void cekAksesGudang(User user) throws CafeException {
        if (user == null || !user.bolehAksesGudang()) {
            throw new AksesDitolakException(user == null ? "UNKNOWN" : user.getRole());
        }
    }

    public void validasiTransaksi(int jumlah) throws CafeException {
        if (jumlah <= 0) {
            throw new CafeException("Jumlah beli harus lebih dari 0!");
        }
    }

    public void validasiPerubahanStok(double jumlah) throws CafeException {
        if (jumlah <= 0) {
            throw new CafeException("Jumlah perubahan harus lebih dari 0!");
        }
    }

    public void validasiLogin(String username, String password)
            throws CafeException {
        if (username.isEmpty() || password.isEmpty()) {
            throw new CafeException("Username dan password wajib diisi!");
        }
    }

    public boolean bolehAksesKasir(User user) {
        return user != null && user.bolehAksesKasir();
    }

    public boolean bolehAksesGudang(User user) {
        return user != null && user.bolehAksesGudang();
    }

    public double getDiskonRate(int totalPorsi) {
        DiscountStrategy strategy =
                DiscountStrategyFactory.createStrategy(totalPorsi);
        return strategy.getDiscountRate(totalPorsi);
    }
}