package repository;

import config.DatabaseConfig;
import exception.CafeException;
import interfaces.MenuRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.KustomisasiOpsi;
import model.MenuItem;

public class DBMenuRepository implements MenuRepository {
    @Override
    public List<String[]> getDaftarMenu() throws CafeException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT id, nama_menu, harga FROM menu ORDER BY id";
        try (Connection conn = DatabaseConfig.getConnection();
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

    @Override
    public List<MenuItem> getAll() throws CafeException {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT id, nama_menu, harga FROM menu ORDER BY id";
        try (Connection conn = DatabaseConfig.getConnection();
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

    @Override
    public List<KustomisasiOpsi> getDaftarKustomisasi() throws CafeException {
        List<KustomisasiOpsi> list = new ArrayList<>();
        String sql = "SELECT id, kategori, tipe_opsi, nama_opsi, harga_tambahan, bahan_id, jumlah_dibutuhkan " +
                     "FROM kustomisasi_menu ORDER BY id";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int rawBahanId = rs.getInt("bahan_id");
                Integer bahanId = rs.wasNull() ? null : rawBahanId;

                list.add(new KustomisasiOpsi(
                    rs.getInt("id"),
                    rs.getString("kategori"),
                    rs.getString("tipe_opsi"),
                    rs.getString("nama_opsi"),
                    rs.getDouble("harga_tambahan"),
                    bahanId,
                    rs.getDouble("jumlah_dibutuhkan")
                ));
            }
        } catch (SQLException e) {
            // Jika tabel kustomisasi_menu belum dibuat, kembalikan list kosong agar menu tetap berjalan
            return list;
        }
        return list;
    }
}