package repository;

import config.DatabaseConfig;
import exception.CafeException;
import exception.StokTidakCukupException;
import interfaces.BahanBakuRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.BahanBaku;

public class DBBahanBakuRepository implements BahanBakuRepository {
    @Override
    public List<BahanBaku> getAll() throws CafeException {
        List<BahanBaku> list = new ArrayList<>();
        String sql = "SELECT id, nama_bahan, stok, satuan FROM bahan_baku ORDER BY id";
        try (Connection conn = DatabaseConfig.getConnection();
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
        try (Connection conn = DatabaseConfig.getConnection();
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
}