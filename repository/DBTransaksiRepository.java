package repository;

import config.DatabaseConfig;
import exception.CafeException;
import exception.StokTidakCukupException;
import interfaces.TransaksiRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.TransaksiRecord;

public class DBTransaksiRepository implements TransaksiRepository {
    @Override
    public void prosesTransaksi(String kasirUsername, int menuId, int jumlahBeli, double diskon) throws CafeException {
        String sqlMenu = "SELECT nama_menu, harga FROM menu WHERE id = ?";
        String sqlResep = "SELECT r.bahan_id, b.nama_bahan, b.stok, r.jumlah_dibutuhkan " +
                          "FROM resep_menu r JOIN bahan_baku b ON r.bahan_id = b.id WHERE r.menu_id = ?";
        String sqlPotong = "UPDATE bahan_baku SET stok = stok - ? WHERE id = ?";
        String sqlHistory = "INSERT INTO transaksi (kasir_username, nama_menu, jumlah_beli, total_harga) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String namaMenu;
                double hargaMenu;
                try (PreparedStatement psMenu = conn.prepareStatement(sqlMenu)) {
                    psMenu.setInt(1, menuId);
                    ResultSet rsMenu = psMenu.executeQuery();
                    if (!rsMenu.next()) throw new CafeException("Menu tidak ditemukan!");
                    namaMenu = rsMenu.getString("nama_menu");
                    hargaMenu = rsMenu.getDouble("harga");
                }

                List<double[]> daftarPotong = new ArrayList<>();
                try (PreparedStatement psResep = conn.prepareStatement(sqlResep)) {
                    psResep.setInt(1, menuId);
                    ResultSet rsResep = psResep.executeQuery();
                    while (rsResep.next()) {
                        int bahanId = rsResep.getInt("bahan_id");
                        String namaBahan = rsResep.getString("nama_bahan");
                        double stokTersedia = rsResep.getDouble("stok");
                        double butuhPerPorsi = rsResep.getDouble("jumlah_dibutuhkan");
                        double totalButuh = butuhPerPorsi * jumlahBeli;

                        if (stokTersedia < totalButuh) {
                            throw new StokTidakCukupException(namaBahan + " (Butuh " + totalButuh + ", Sisa " + stokTersedia + ")");
                        }
                        daftarPotong.add(new double[]{bahanId, totalButuh});
                    }
                }

                try (PreparedStatement psPotong = conn.prepareStatement(sqlPotong)) {
                    for (double[] item : daftarPotong) {
                        psPotong.setDouble(1, item[1]);
                        psPotong.setInt(2, (int) item[0]);
                        psPotong.executeUpdate();
                    }
                }

                try (PreparedStatement psHist = conn.prepareStatement(sqlHistory)) {
                    double subtotal = hargaMenu * jumlahBeli;
                    double totalHarga = subtotal - (subtotal * diskon);

                    psHist.setString(1, kasirUsername);
                    psHist.setString(2, namaMenu);
                    psHist.setInt(3, jumlahBeli);
                    psHist.setDouble(4, totalHarga);
                    psHist.executeUpdate();
                }

                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                if (ex instanceof CafeException) throw (CafeException) ex;
                throw new CafeException("Transaksi gagal: " + ex.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new CafeException("Koneksi database error: " + e.getMessage());
        }
    }

    @Override
    public List<String[]> getHistoryTransaksi() throws CafeException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT id, tanggal, kasir_username, nama_menu, jumlah_beli, total_harga FROM transaksi ORDER BY id DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("tanggal"),
                    rs.getString("kasir_username"),
                    rs.getString("nama_menu"),
                    String.valueOf(rs.getInt("jumlah_beli")),
                    "Rp " + rs.getDouble("total_harga")
                });
            }
        } catch (SQLException e) {
            throw new CafeException("Gagal memuat history: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<TransaksiRecord> getAll() throws CafeException {
        List<TransaksiRecord> list = new ArrayList<>();
        String sql = "SELECT id, tanggal, kasir_username, nama_menu, jumlah_beli, total_harga FROM transaksi ORDER BY id DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new TransaksiRecord(
                    rs.getInt("id"),
                    rs.getString("tanggal"),
                    rs.getString("kasir_username"),
                    rs.getString("nama_menu"),
                    rs.getInt("jumlah_beli"),
                    rs.getDouble("total_harga")
                ));
            }
        } catch (SQLException e) {
            throw new CafeException("Gagal memuat history: " + e.getMessage());
        }
        return list;
    }
}