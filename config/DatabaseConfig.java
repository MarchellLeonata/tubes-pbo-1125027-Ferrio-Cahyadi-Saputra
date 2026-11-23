package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DatabaseConfig {
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
}