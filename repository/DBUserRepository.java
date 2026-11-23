package repository;

import config.DatabaseConfig;
import exception.CafeException;
import interfaces.UserRepository;
import java.sql.*;
import model.Karyawan;
import model.User;

public class DBUserRepository implements UserRepository {
    @Override
    public User login(String username, String password) throws CafeException {
        String sql = "SELECT id, username, nama_lengkap, role FROM karyawan WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConfig.getConnection();
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
}