package repository;

import config.DatabaseConfig;
import exception.CafeException;
import exception.StokTidakCukupException;
import interfaces.TransaksiRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.CartItem;
import model.KustomisasiOpsi;
import model.TransaksiRecord;

public class DBTransaksiRepository implements TransaksiRepository {

    @Override
    public void prosesTransaksiKeranjang(String kasirUsername, List<CartItem> items, double diskon) throws CafeException {
        if (items == null || items.isEmpty()) {
            throw new CafeException("Keranjang pesanan masih kosong!");
        }

        String sqlMenu = "SELECT nama_menu, harga FROM menu WHERE id = ?";
        String sqlResep = "SELECT r.bahan_id, b.nama_bahan, b.stok, r.jumlah_dibutuhkan " +
                          "FROM resep_menu r JOIN bahan_baku b ON r.bahan_id = b.id WHERE r.menu_id = ?";
        String sqlKustom = "SELECT k.nama_opsi, k.harga_tambahan, k.bahan_id, k.jumlah_dibutuhkan, " +
                           "b.nama_bahan, b.stok " +
                           "FROM kustomisasi_menu k LEFT JOIN bahan_baku b ON k.bahan_id = b.id " +
                           "WHERE k.id = ?";
        String sqlPotong = "UPDATE bahan_baku SET stok = stok - ? WHERE id = ?";
        String sqlHistory = "INSERT INTO transaksi (kasir_username, nama_menu, jumlah_beli, total_harga) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Map<Integer, String> namaBahanMap = new LinkedHashMap<>();
                Map<Integer, Double> stokBahanMap = new LinkedHashMap<>();
                Map<Integer, Double> totalPemakaianMap = new LinkedHashMap<>();
                List<Object[]> dataInsertTransaksi = new ArrayList<>();

                for (CartItem item : items) {
                    String namaMenuDb;
                    double hargaMenuDb;

                    try (PreparedStatement psMenu = conn.prepareStatement(sqlMenu)) {
                        psMenu.setInt(1, item.getMenuId());
                        try (ResultSet rsMenu = psMenu.executeQuery()) {
                            if (!rsMenu.next()) {
                                throw new CafeException("Menu ID " + item.getMenuId() + " tidak ditemukan!");
                            }
                            namaMenuDb = rsMenu.getString("nama_menu");
                            hargaMenuDb = rsMenu.getDouble("harga");
                        }
                    }

                    // 1. Pemakaian bahan dari resep dasar menu di database
                    try (PreparedStatement psResep = conn.prepareStatement(sqlResep)) {
                        psResep.setInt(1, item.getMenuId());
                        try (ResultSet rsResep = psResep.executeQuery()) {
                            while (rsResep.next()) {
                                int bahanId = rsResep.getInt("bahan_id");
                                String namaBahan = rsResep.getString("nama_bahan");
                                double stokTersedia = rsResep.getDouble("stok");
                                double butuhPerPorsi = rsResep.getDouble("jumlah_dibutuhkan");
                                double totalButuh = butuhPerPorsi * item.getJumlah();

                                namaBahanMap.put(bahanId, namaBahan);
                                stokBahanMap.put(bahanId, stokTersedia);
                                totalPemakaianMap.merge(bahanId, totalButuh, Double::sum);
                            }
                        }
                    }

                    // 2. Harga & pemakaian bahan dari kustomisasi berbasis database (jika id > 0)
                    double biayaKustomisasiDb = 0;
                    List<String> labelOpsi = new ArrayList<>();

                    for (KustomisasiOpsi opsi : item.getOpsiTerpilih()) {
                        if (opsi == null) continue;

                        if (opsi.getId() > 0) {
                            try (PreparedStatement psKustom = conn.prepareStatement(sqlKustom)) {
                                psKustom.setInt(1, opsi.getId());
                                try (ResultSet rsKustom = psKustom.executeQuery()) {
                                    if (!rsKustom.next()) {
                                        throw new CafeException("Opsi kustomisasi ID " + opsi.getId() + " tidak ditemukan!");
                                    }
                                    labelOpsi.add(rsKustom.getString("nama_opsi"));
                                    biayaKustomisasiDb += rsKustom.getDouble("harga_tambahan");

                                    int bahanId = rsKustom.getInt("bahan_id");
                                    if (!rsKustom.wasNull()) {
                                        double butuhPerPorsi = rsKustom.getDouble("jumlah_dibutuhkan");
                                        if (butuhPerPorsi > 0) {
                                            String namaBahan = rsKustom.getString("nama_bahan");
                                            double stokTersedia = rsKustom.getDouble("stok");
                                            double totalButuh = butuhPerPorsi * item.getJumlah();

                                            namaBahanMap.put(bahanId, namaBahan);
                                            stokBahanMap.put(bahanId, stokTersedia);
                                            totalPemakaianMap.merge(bahanId, totalButuh, Double::sum);
                                        }
                                    }
                                }
                            }
                        } else {
                            // Opsi non-database (tanpa bahan baku & tanpa biaya tambahan)
                            labelOpsi.add(opsi.getNamaOpsi());
                        }
                    }

                    // 3. Hitung harga akhir per item setelah diskon keranjang
                    double hargaSatuan = hargaMenuDb + biayaKustomisasiDb;
                    double subtotalItem = hargaSatuan * item.getJumlah();
                    double totalAkhirItem = subtotalItem - (subtotalItem * diskon);

                    String rincian = labelOpsi.isEmpty() ? "Standar" : String.join(", ", labelOpsi);
                    String namaTercatat = namaMenuDb + " (" + rincian + ")";

                    dataInsertTransaksi.add(new Object[]{
                        kasirUsername,
                        namaTercatat,
                        item.getJumlah(),
                        totalAkhirItem
                    });
                }

                // 4. Validasi stok gabungan sebelum pemotongan
                for (Map.Entry<Integer, Double> entry : totalPemakaianMap.entrySet()) {
                    int bahanId = entry.getKey();
                    double totalButuh = entry.getValue();
                    double stokTersedia = stokBahanMap.getOrDefault(bahanId, 0.0);
                    String namaBahan = namaBahanMap.getOrDefault(bahanId, "Bahan #" + bahanId);

                    if (stokTersedia < totalButuh) {
                        throw new StokTidakCukupException(
                            namaBahan + " (Butuh " + totalButuh + ", Sisa " + stokTersedia + ")"
                        );
                    }
                }

                // 5. Potong stok bahan baku
                try (PreparedStatement psPotong = conn.prepareStatement(sqlPotong)) {
                    for (Map.Entry<Integer, Double> entry : totalPemakaianMap.entrySet()) {
                        psPotong.setDouble(1, entry.getValue());
                        psPotong.setInt(2, entry.getKey());
                        psPotong.executeUpdate();
                    }
                }

                // 6. Simpan riwayat transaksi
                try (PreparedStatement psHist = conn.prepareStatement(sqlHistory)) {
                    for (Object[] row : dataInsertTransaksi) {
                        psHist.setString(1, (String) row[0]);
                        psHist.setString(2, (String) row[1]);
                        psHist.setInt(3, (Integer) row[2]);
                        psHist.setDouble(4, (Double) row[3]);
                        psHist.executeUpdate();
                    }
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