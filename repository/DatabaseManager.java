package repository;


import exception.CafeException;
import exception.StokTidakCukupException;
import interfaces.Repository;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import model.BahanBaku;
import model.Karyawan;
import model.MenuItem;
import model.TransaksiRecord;
import model.User;

public class DatabaseManager implements Repository<BahanBaku> {
    private static Properties config = new Properties();

static {
    try {
        FileInputStream file = new FileInputStream("config.properties");
        config.load(file);
        file.close();
    } catch (IOException e) {
        throw new RuntimeException("File config.properties tidak ditemukan!", e);
    }
}

    public static Connection getConnection() throws SQLException {
        String url = config.getProperty("db.url");
        String user = config.getProperty("db.username");
        String pass = config.getProperty("db.password");

        if (url == null || user == null || pass == null) {
            throw new SQLException("Konfigurasi database belum lengkap!");
    }

        return DriverManager.getConnection(url, user, pass);
    }

    public User login(String username, String password) throws CafeException {
        String sql = "SELECT id, username, nama_lengkap, role FROM karyawan WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Karyawan(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("nama_lengkap"),
                    rs.getString("role")
                );
            } else {
                throw new CafeException("Username atau password salah!");
            }
        } catch (SQLException e) {
            throw new CafeException("Kesalahan database saat login: " + e.getMessage());
        }
    }

    @Override
    public List<BahanBaku> getAll() throws CafeException {
        List<BahanBaku> list = new ArrayList<>();
        String sql = "SELECT id, nama_bahan, stok, satuan FROM bahan_baku ORDER BY id";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new BahanBaku(
                    rs.getInt("id"),
                    rs.getString("nama_bahan"),
                    rs.getDouble("stok"),
                    rs.getString("satuan")
                ));
            }
        } catch (SQLException e) {
            throw new CafeException("Gagal memuat bahan baku: " + e.getMessage());
        }
        return list;
    }

    @Override
    public void updateStok(int idBahan, double perubahan) throws CafeException {
        String cekSql = "SELECT nama_bahan, stok FROM bahan_baku WHERE id = ?";
        String updSql = "UPDATE bahan_baku SET stok = stok + ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement psCek = conn.prepareStatement(cekSql);
             PreparedStatement psUpd = conn.prepareStatement(updSql)) {

            psCek.setInt(1, idBahan);
            ResultSet rs = psCek.executeQuery();
            if (!rs.next()) throw new CafeException("ID Bahan tidak ditemukan!");

            double stokSekarang = rs.getDouble("stok");
            String namaBahan = rs.getString("nama_bahan");

            if (stokSekarang + perubahan < 0) {
                throw new StokTidakCukupException(namaBahan);
            }

            psUpd.setDouble(1, perubahan);
            psUpd.setInt(2, idBahan);
            psUpd.executeUpdate();
        } catch (SQLException e) {
            throw new CafeException("Gagal update stok: " + e.getMessage());
        }
    }

    public void prosesTransaksi(String kasirUsername, int menuId, int jumlahBeli,double diskon) throws CafeException {
        String sqlMenu = "SELECT nama_menu, harga FROM menu WHERE id = ?";
        String sqlResep = "SELECT r.bahan_id, b.nama_bahan, b.stok, r.jumlah_dibutuhkan " +
                          "FROM resep_menu r JOIN bahan_baku b ON r.bahan_id = b.id WHERE r.menu_id = ?";
        String sqlPotong = "UPDATE bahan_baku SET stok = stok - ? WHERE id = ?";
        String sqlHistory = "INSERT INTO transaksi (kasir_username, nama_menu, jumlah_beli, total_harga) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection()) {
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

    public List<String[]> getHistoryTransaksi() throws CafeException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT id, tanggal, kasir_username, nama_menu, jumlah_beli, total_harga FROM transaksi ORDER BY id DESC";
        try (Connection conn = getConnection();
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

    public List<String[]> getDaftarMenu() throws CafeException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT id, nama_menu, harga FROM menu ORDER BY id";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("nama_menu"),
                    String.valueOf(rs.getDouble("harga"))
                });
            }
        } catch (SQLException e) {
            throw new CafeException("Gagal memuat menu: " + e.getMessage());
        }
        return list;
    }

    public List<MenuItem> getDaftarMenuModel() throws CafeException {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT id, nama_menu, harga FROM menu ORDER BY id";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new MenuItem(
                    rs.getInt("id"),
                    rs.getString("nama_menu"),
                    rs.getDouble("harga")
                ));
            }
        } catch (SQLException e) {
            throw new CafeException("Gagal memuat menu: " + e.getMessage());
        }
        return list;
    }

    public List<TransaksiRecord> getHistoryTransaksiModel() throws CafeException {
        List<TransaksiRecord> list = new ArrayList<>();
        String sql = "SELECT id, tanggal, kasir_username, nama_menu, jumlah_beli, total_harga FROM transaksi ORDER BY id DESC";
        try (Connection conn = getConnection();
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