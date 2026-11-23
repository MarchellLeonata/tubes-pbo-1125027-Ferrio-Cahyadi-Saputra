package repository;

import config.DatabaseConfig;
import exception.CafeException;
import interfaces.MenuRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
}