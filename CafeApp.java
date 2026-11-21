import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import repository.DatabaseManager;
import exception.AksesDitolakException;
import exception.CafeException;
import model.BahanBaku;
import model.User;

public class CafeApp extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private JLabel lblStatus = new JLabel("Status: Silakan Login terlebih dahulu.");

    private DatabaseManager db = new DatabaseManager();
    private User currentUser;

    // Komponen Login
    private JTextField txtUser = new JTextField(15);
    private JPasswordField txtPass = new JPasswordField(15);

    // Komponen Dashboard & Navigasi
    private JLabel lblWelcome = new JLabel();
    private JButton btnMenuKasir = new JButton("Panel Kasir");
    private JButton btnMenuGudang = new JButton("Panel Stok & History");
    private CardLayout subCardLayout = new CardLayout();
    private JPanel subPanel = new JPanel(subCardLayout);

    // Komponen Kasir
    private JComboBox<String> cmbMenu = new JComboBox<>();
    private JTextField txtJumlahBeli = new JTextField(5);

    // Komponen Gudang & History
    private DefaultTableModel modelBahan = new DefaultTableModel(new String[]{"ID", "Nama Bahan", "Stok", "Satuan"}, 0);
    private JTable tblBahan = new JTable(modelBahan);
    private JTextField txtIdBahan = new JTextField(5);
    private JTextField txtUbahStok = new JTextField(5);

    private DefaultTableModel modelHistory = new DefaultTableModel(new String[]{"ID", "Waktu", "Kasir", "Menu", "Qty", "Total"}, 0);
    private JTable tblHistory = new JTable(modelHistory);

    public CafeApp() {
        setTitle("Sistem Kasir & Inventori Cafe");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Bar Status di Bagian Bawah (Pengganti JOptionPane)
        lblStatus.setOpaque(true);
        lblStatus.setBackground(new Color(230, 230, 230));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        add(lblStatus, BorderLayout.SOUTH);

        // Siapkan Halaman Login & Halaman Utama
        mainPanel.add(buatPanelLogin(), "LOGIN");
        mainPanel.add(buatPanelUtama(), "UTAMA");
        add(mainPanel, BorderLayout.CENTER);

        cardLayout.show(mainPanel, "LOGIN");
    }

    private void setPesan(String pesan, boolean isError) {
        lblStatus.setText(pesan);
        lblStatus.setForeground(isError ? Color.RED : new Color(0, 128, 0));
    }

    private JPanel buatPanelLogin() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel title = new JLabel("LOGIN SISTEM CAFE");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JButton btnLogin = new JButton("Masuk");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(txtPass, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> prosesLogin());
        return panel;
    }

    private JPanel buatPanelUtama() {
        JPanel container = new JPanel(new BorderLayout());

        // Header Navigasi
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnLogout = new JButton("Logout");
        topBar.add(lblWelcome);
        topBar.add(btnMenuKasir);
        topBar.add(btnMenuGudang);
        topBar.add(btnLogout);
        container.add(topBar, BorderLayout.NORTH);

        // Sub-panel untuk Kasir dan Gudang/History
        subPanel.add(buatPanelKasir(), "KASIR");
        subPanel.add(buatPanelGudangDanHistory(), "GUDANG");
        container.add(subPanel, BorderLayout.CENTER);

        // Event Tombol Navigasi dengan Pengecekan Access Control
        btnMenuKasir.addActionListener(e -> {
            try {
                if (!currentUser.bolehAksesKasir()) {
                    throw new AksesDitolakException(currentUser.getRole());
                }
                muatDataMenu();
                subCardLayout.show(subPanel, "KASIR");
                setPesan("Membuka Panel Kasir.", false);
            } catch (CafeException ex) {
                setPesan(ex.getMessage(), true);
            }
        });

        btnMenuGudang.addActionListener(e -> {
            try {
                if (!currentUser.bolehAksesGudang()) {
                    throw new AksesDitolakException(currentUser.getRole());
                }
                refreshTabelGudangDanHistory();
                subCardLayout.show(subPanel, "GUDANG");
                setPesan("Membuka Panel Stok Bahan & History.", false);
            } catch (CafeException ex) {
                setPesan(ex.getMessage(), true);
            }
        });

        btnLogout.addActionListener(e -> {
            currentUser = null;
            txtUser.setText("");
            txtPass.setText("");
            cardLayout.show(mainPanel, "LOGIN");
            setPesan("Berhasil logout.", false);
        });

        return container;
    }

    private JPanel buatPanelKasir() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel title = new JLabel("TRANSAKSI KASIR CAFE");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        JButton btnPesan = new JButton("Proses Pesanan");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1;
        panel.add(new JLabel("Pilih Menu:"), gbc);
        gbc.gridx = 1;
        panel.add(cmbMenu, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Jumlah Porsi:"), gbc);
        gbc.gridx = 1;
        panel.add(txtJumlahBeli, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(btnPesan, gbc);

        btnPesan.addActionListener(e -> {
            try {
                if (cmbMenu.getSelectedItem() == null) throw new CafeException("Menu belum dipilih!");
                String selected = cmbMenu.getSelectedItem().toString();
                int menuId = Integer.parseInt(selected.split(" - ")[0]);
                int qty = Integer.parseInt(txtJumlahBeli.getText().trim());

                if (qty <= 0) throw new CafeException("Jumlah beli harus lebih dari 0!");

                db.prosesTransaksi(currentUser.getUsername(), menuId, qty);
                txtJumlahBeli.setText("");
                setPesan("Transaksi berhasil! Stok bahan baku otomatis berkurang.", false);
            } catch (NumberFormatException ex) {
                setPesan("Jumlah porsi harus berupa angka!", true);
            } catch (CafeException ex) {
                setPesan(ex.getMessage(), true);
            }
        });

        return panel;
    }

    private JPanel buatPanelGudangDanHistory() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));

        // Bagian Atas: Manajemen Stok Bahan Baku
        JPanel panelStok = new JPanel(new BorderLayout());
        panelStok.setBorder(BorderFactory.createTitledBorder("Persediaan Bahan Baku"));
        panelStok.add(new JScrollPane(tblBahan), BorderLayout.CENTER);

        JPanel formStok = new JPanel(new FlowLayout());
        JButton btnTambahStok = new JButton("Tambah Stok (+)");
        JButton btnKurangStok = new JButton("Kurangi Stok (-)");
        formStok.add(new JLabel("ID Bahan:"));
        formStok.add(txtIdBahan);
        formStok.add(new JLabel("Jumlah:"));
        formStok.add(txtUbahStok);
        formStok.add(btnTambahStok);
        formStok.add(btnKurangStok);
        panelStok.add(formStok, BorderLayout.SOUTH);

        btnTambahStok.addActionListener(e -> ubahStokBahan(true));
        btnKurangStok.addActionListener(e -> ubahStokBahan(false));

        // Bagian Bawah: History Pembelian
        JPanel panelHistory = new JPanel(new BorderLayout());
        panelHistory.setBorder(BorderFactory.createTitledBorder("History Transaksi Pembelian"));
        panelHistory.add(new JScrollPane(tblHistory), BorderLayout.CENTER);

        panel.add(panelStok);
        panel.add(panelHistory);
        return panel;
    }

    private void prosesLogin() {
        try {
            String u = txtUser.getText().trim();
            String p = new String(txtPass.getPassword()).trim();
            if (u.isEmpty() || p.isEmpty()) {
                throw new CafeException("Username dan password wajib diisi!");
            }

            currentUser = db.login(u, p);
            lblWelcome.setText("Halo, " + currentUser.getDeskripsi() + " | ");

            // Atur visibilitas tombol berdasarkan Access Control Role
            btnMenuKasir.setEnabled(currentUser.bolehAksesKasir());
            btnMenuGudang.setEnabled(currentUser.bolehAksesGudang());

            if (currentUser.bolehAksesKasir()) {
                muatDataMenu();
                subCardLayout.show(subPanel, "KASIR");
            } else if (currentUser.bolehAksesGudang()) {
                refreshTabelGudangDanHistory();
                subCardLayout.show(subPanel, "GUDANG");
            }

            cardLayout.show(mainPanel, "UTAMA");
            setPesan("Login berhasil sebagai " + currentUser.getRole(), false);
        } catch (CafeException ex) {
            setPesan(ex.getMessage(), true);
        }
    }

        private void muatDataMenu() throws CafeException {
        cmbMenu.removeAllItems();
        List<String[]> menus = db.getDaftarMenu();
        for (String[] m : menus) {
            cmbMenu.addItem(m[0] + " - " + m[1] + " (Rp " + m[2] + ")");
        }
    }

    private void ubahStokBahan(boolean isTambah) {
        try {
            int idBahan = Integer.parseInt(txtIdBahan.getText().trim());
            double jumlah = Double.parseDouble(txtUbahStok.getText().trim());
            if (jumlah <= 0) throw new CafeException("Jumlah perubahan harus lebih dari 0!");

            double perubahan = isTambah ? jumlah : -jumlah;
            db.updateStok(idBahan, perubahan);
            refreshTabelGudangDanHistory();
            txtIdBahan.setText("");
            txtUbahStok.setText("");
            setPesan("Stok bahan baku berhasil diperbarui!", false);
        } catch (NumberFormatException ex) {
            setPesan("ID dan Jumlah harus berupa angka!", true);
        } catch (CafeException ex) {
            setPesan(ex.getMessage(), true);
        }
    }

        private void refreshTabelGudangDanHistory() throws CafeException {
        modelBahan.setRowCount(0);
        for (BahanBaku b : db.getAll()) {
            modelBahan.addRow(new Object[]{b.getId(), b.getNamaBahan(), b.getStok(), b.getSatuan()});
        }

        modelHistory.setRowCount(0);
        for (String[] h : db.getHistoryTransaksi()) {
            modelHistory.addRow(h);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CafeApp().setVisible(true);
        });
    }
}