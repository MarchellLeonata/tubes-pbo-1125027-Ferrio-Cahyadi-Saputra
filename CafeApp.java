import exception.AksesDitolakException;
import exception.CafeException;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import model.BahanBaku;
import model.User;
import repository.DatabaseManager;

public class CafeApp extends JFrame {
    private static final Color MC_YELLOW = new Color(255, 188, 0);
    private static final Color MC_RED = new Color(218, 41, 28);
    private static final Color DARK = new Color(35, 35, 35);
    private static final Color LIGHT_BG = new Color(247, 247, 247);
    private static final Color MUTED = new Color(110, 110, 110);
    
    
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
    private JLabel lblMenuTerpilih = new JLabel("Belum ada menu dipilih");
    private JLabel lblHargaTerpilih = new JLabel("Pilih menu untuk memulai pesanan");
    private JPanel panelDaftarMenu = new JPanel(new GridLayout(0, 3, 16, 16));
    // Komponen Gudang & History
    private DefaultTableModel modelBahan = new DefaultTableModel(new String[]{"ID", "Nama Bahan", "Stok", "Satuan"}, 0);
    private JTable tblBahan = new JTable(modelBahan);
    private JTextField txtIdBahan = new JTextField(5);
    private JTextField txtUbahStok = new JTextField(5);

    private DefaultTableModel modelHistory = new DefaultTableModel(new String[]{"ID", "Waktu", "Kasir", "Menu", "Qty", "Total"}, 0);
    private JTable tblHistory = new JTable(modelHistory);
    private Frame background;

    public CafeApp() {
        setTitle("Sistem Kasir & Inventori Cafe");
        setSize(1180, 760);
        setMinimumSize(new Dimension(980, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(LIGHT_BG);

        // Bar Status di Bagian Bawah (Pengganti JOptionPane)
        lblStatus.setOpaque(true);
        lblStatus.setBackground(Color.WHITE);
        lblStatus.setBorder(new EmptyBorder(10, 18, 10, 18));
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 12));
        add(lblStatus, BorderLayout.SOUTH);

        // Siapkan Halaman Login & Halaman Utama
        mainPanel.add(buatPanelLogin(), "LOGIN");
        mainPanel.add(buatPanelUtama(), "UTAMA");
        add(mainPanel, BorderLayout.CENTER);

        cardLayout.show(mainPanel, "LOGIN");
    }

    private void setPesan(String pesan, boolean isError) {
        lblStatus.setText(pesan);
        lblStatus.setForeground(isError ? MC_RED : new Color(0, 130, 65));
    }
    
    //Panel Login
    private JPanel buatPanelLogin() {
        JPanel panelBackground = new JPanel(new GridBagLayout());
        panelBackground.setBackground(new Color(245, 245, 245));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(34, 42, 34, 42)
        ));
        card.setPreferredSize(new Dimension(420, 440));

        JLabel logo = new JLabel("C", SwingConstants.CENTER);
        logo.setOpaque(true);
        logo.setBackground(MC_YELLOW);
        logo.setForeground(MC_RED);
        logo.setFont(new Font("Arial", Font.BOLD, 42));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setPreferredSize(new Dimension(76, 76));
        logo.setMaximumSize(new Dimension(76, 76));

        JLabel title = new JLabel("SELAMAT DATANG!");
        title.setFont(new Font("SansSerif", Font.BOLD, 23));
        title.setForeground(DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Silakan masuk ke sistem cafe");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtUser.setMaximumSize(new Dimension(330, 42));
        txtPass.setMaximumSize(new Dimension(330, 42));
        styleTextField(txtUser);
        styleTextField(txtPass);

        JButton btnLogin = primaryButton("MASUK");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(330, 44));
        btnLogin.addActionListener(e -> prosesLogin());

        card.add(logo);
        card.add(Box.createVerticalStrut(22));
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(28));
        card.add(leftLabel("USERNAME"));
        card.add(Box.createVerticalStrut(7));
        card.add(txtUser);
        card.add(Box.createVerticalStrut(16));
        card.add(leftLabel("PASSWORD"));
        card.add(Box.createVerticalStrut(7));
        card.add(txtPass);
        card.add(Box.createVerticalStrut(24));
        card.add(btnLogin);

       panelBackground.add(card);
       return panelBackground;
    }

    //Panel Utama
    private JPanel buatPanelUtama() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(LIGHT_BG);

        // Header Navigasi
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        topBar.setBorder(new EmptyBorder(13, 22, 13, 22));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brand.setOpaque(false);
        JLabel logo = new JLabel("M");
        logo.setOpaque(true);
        logo.setBackground(MC_YELLOW);
        logo.setForeground(MC_RED);
        logo.setFont(new Font("Arial", Font.BOLD, 24));
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setPreferredSize(new Dimension(42, 42));
        JLabel brandName = new JLabel("Cafe");
        brandName.setFont(new Font("SansSerif", Font.BOLD, 21));
        brandName.setForeground(DARK);
        brand.add(logo);
        brand.add(brandName);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 4));
        nav.setOpaque(false);
        styleNavButton(btnMenuKasir);
        styleNavButton(btnMenuGudang);
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnLogout.setForeground(MC_RED);
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(new LineBorder(new Color(225, 225, 225), 1, true));
        btnLogout.setPreferredSize(new Dimension(95, 38));
        nav.add(btnMenuKasir);
        nav.add(btnMenuGudang);
        nav.add(btnLogout);

        JPanel rightHeader = new JPanel();
        rightHeader.setOpaque(false);
        rightHeader.setLayout(new BoxLayout(rightHeader, BoxLayout.Y_AXIS));
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblWelcome.setForeground(DARK);
        lblWelcome.setAlignmentX(Component.RIGHT_ALIGNMENT);
        rightHeader.add(lblWelcome);
        rightHeader.add(Box.createVerticalStrut(7));
        rightHeader.add(nav);

        topBar.add(brand, BorderLayout.WEST);
        topBar.add(rightHeader, BorderLayout.EAST);
        container.add(topBar, BorderLayout.NORTH);

        subPanel.setBackground(LIGHT_BG);
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

    //Panel Kasir
    private JPanel buatPanelKasir() {
        JPanel page = new JPanel(new BorderLayout(18, 14));
        page.setBackground(LIGHT_BG);
        page.setBorder(new EmptyBorder(22, 24, 20, 24));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Coffe");
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        title.setForeground(DARK);
        JLabel subtitle = new JLabel("Pilih Kopi favorit kamu dan mulai pesanan");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);
        heading.add(title);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitle);
        page.add(heading, BorderLayout.NORTH);

        panelDaftarMenu.setBackground(LIGHT_BG);
        panelDaftarMenu.setBorder(new EmptyBorder(4, 2, 4, 2));
        JScrollPane scrollMenu = new JScrollPane(panelDaftarMenu);
        scrollMenu.setBorder(null);
        scrollMenu.getViewport().setBackground(LIGHT_BG);
        scrollMenu.getVerticalScrollBar().setUnitIncrement(18);
        page.add(scrollMenu, BorderLayout.CENTER);

        JPanel orderCard = new JPanel();
        orderCard.setLayout(new BoxLayout(orderCard, BoxLayout.Y_AXIS));
        orderCard.setBackground(Color.WHITE);
        orderCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        orderCard.setPreferredSize(new Dimension(255, 10));

        JLabel orderTitle = new JLabel("Pesanan kamu");
        orderTitle.setFont(new Font("SansSerif", Font.BOLD, 19));
        orderTitle.setForeground(DARK);
        JLabel selectedCaption = new JLabel("MENU DIPILIH");
        selectedCaption.setFont(new Font("SansSerif", Font.BOLD, 10));
        selectedCaption.setForeground(MUTED);
        lblMenuTerpilih.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblMenuTerpilih.setForeground(DARK);
        lblMenuTerpilih.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblHargaTerpilih.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblHargaTerpilih.setForeground(MUTED);
        lblHargaTerpilih.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel qtyLabel = new JLabel("JUMLAH PORSI");
        qtyLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        qtyLabel.setForeground(MUTED);
        txtJumlahBeli.setHorizontalAlignment(JTextField.CENTER);
        styleTextField(txtJumlahBeli);
        txtJumlahBeli.setMaximumSize(new Dimension(210, 40));

        JButton btnPesan = primaryButton("PROSES PESANAN");
        btnPesan.setMaximumSize(new Dimension(220, 44));
        btnPesan.setAlignmentX(Component.LEFT_ALIGNMENT);

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

        orderCard.add(orderTitle);
        orderCard.add(Box.createVerticalStrut(24));
        orderCard.add(selectedCaption);
        orderCard.add(Box.createVerticalStrut(8));
        orderCard.add(lblMenuTerpilih);
        orderCard.add(Box.createVerticalStrut(5));
        orderCard.add(lblHargaTerpilih);
        orderCard.add(Box.createVerticalStrut(25));
        orderCard.add(new JSeparator());
        orderCard.add(Box.createVerticalStrut(22));
        orderCard.add(qtyLabel);
        orderCard.add(Box.createVerticalStrut(8));
        orderCard.add(txtJumlahBeli);
        orderCard.add(Box.createVerticalStrut(22));
        orderCard.add(btnPesan);
        orderCard.add(Box.createVerticalGlue());
        page.add(orderCard, BorderLayout.EAST);
        
        return page;
    }

    private JPanel buatKartuMenu(String menuText, int index) {
        String[] bagian = menuText.split(" - ", 2);
        String nama = bagian.length > 1 ? bagian[1] : menuText;
        String harga = "";
        int posisiHarga = nama.lastIndexOf(" (Rp ");
        if (posisiHarga >= 0) {
            harga = nama.substring(posisiHarga + 2, nama.length() - 1);
            nama = nama.substring(0, posisiHarga);
        }

        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(225, 225, 225), 1, true),
            new EmptyBorder(12, 12, 12, 12)
        ));
        card.setPreferredSize(new Dimension(190, 190));

        JPanel imageArea = new JPanel(new GridBagLayout());
        imageArea.setBackground(new Color(255, 246, 218));
        imageArea.setPreferredSize(new Dimension(150, 95));
        JLabel foodMark = new JLabel("M");
        foodMark.setFont(new Font("Arial", Font.BOLD, 42));
        foodMark.setForeground(MC_RED);
        imageArea.add(foodMark);

        JLabel nameLabel = new JLabel("<html><div style='width:145px'><b>" + escapeHtml(nama) + "</b></div></html>");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        nameLabel.setForeground(DARK);
        JLabel priceLabel = new JLabel(harga);
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        priceLabel.setForeground(MC_RED);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(nameLabel);
        info.add(Box.createVerticalStrut(6));
        info.add(priceLabel);

        final String namaMenu = nama;
        final String hargaMenu = harga;
        JButton choose = new JButton("PILIH MENU  +");
        choose.setFont(new Font("SansSerif", Font.BOLD, 10));
        choose.setForeground(DARK);
        choose.setBackground(MC_YELLOW);
        choose.setFocusPainted(false);
        choose.setBorder(new EmptyBorder(9, 8, 9, 8));
        choose.addActionListener(e -> {
            cmbMenu.setSelectedIndex(index);
            lblMenuTerpilih.setText(namaMenu);
            lblHargaTerpilih.setText(hargaMenu);
            setPesan("Menu " + namaMenu + " dipilih.", false);
        });

        card.add(imageArea, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        card.add(choose, BorderLayout.SOUTH);
        return card;
    }
    
    private void buatUlangKartuMenu() {
        panelDaftarMenu.removeAll();
        for (int i = 0; i < cmbMenu.getItemCount(); i++) {
            panelDaftarMenu.add(buatKartuMenu(cmbMenu.getItemAt(i), i));
        }
        panelDaftarMenu.revalidate();
        panelDaftarMenu.repaint();
    }

    private JPanel buatPanelGudangDanHistory() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 14, 14));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        styleTable(tblBahan);
        styleTable(tblHistory);

        JPanel panelStok = new JPanel(new BorderLayout(8, 12));
        panelStok.setBackground(Color.WHITE);
        panelStok.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true), new EmptyBorder(15, 16, 15, 16)
        ));
        JLabel titleStok = new JLabel("Persediaan Bahan Baku");
        titleStok.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleStok.setForeground(DARK);
        panelStok.add(titleStok, BorderLayout.NORTH);
        panelStok.add(new JScrollPane(tblBahan), BorderLayout.CENTER);

        JPanel formStok = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 5));
        formStok.setBackground(Color.WHITE);
        JButton btnTambahStok = primaryButton("Tambah Stok (+)");
        JButton btnKurangStok = new JButton("Kurangi Stok (-)");
        btnKurangStok.setBackground(new Color(245, 245, 245));
        btnKurangStok.setForeground(DARK);
        btnKurangStok.setFocusPainted(false);
        btnKurangStok.setBorder(new EmptyBorder(10, 14, 10, 14));
        styleTextField(txtIdBahan);
        styleTextField(txtUbahStok);
        formStok.add(new JLabel("ID Bahan:"));
        formStok.add(txtIdBahan);
        formStok.add(new JLabel("Jumlah:"));
        formStok.add(txtUbahStok);
        formStok.add(btnTambahStok);
        formStok.add(btnKurangStok);
        panelStok.add(formStok, BorderLayout.SOUTH);
        btnTambahStok.addActionListener(e -> ubahStokBahan(true));
        btnKurangStok.addActionListener(e -> ubahStokBahan(false));

        JPanel panelHistory = new JPanel(new BorderLayout(8, 12));
        panelHistory.setBackground(Color.WHITE);
        panelHistory.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true), new EmptyBorder(15, 16, 15, 16)
        ));
        JLabel titleHistory = new JLabel("Riwayat Transaksi Pembelian");
        titleHistory.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleHistory.setForeground(DARK);
        panelHistory.add(titleHistory, BorderLayout.NORTH);
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
        if (cmbMenu.getItemCount() > 0) {
            cmbMenu.setSelectedIndex(0);
            String first = cmbMenu.getSelectedItem().toString();
            String[] data = first.split(" - ", 2);
            String nama = data.length > 1 ? data[1] : first;
            int p = nama.lastIndexOf(" (Rp ");
            if (p >= 0) {
                lblHargaTerpilih.setText(nama.substring(p + 2, nama.length() - 1));
                nama = nama.substring(0, p);
            }
            lblMenuTerpilih.setText(nama);
        }
        buatUlangKartuMenu();
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


    private JLabel leftLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(DARK);
        button.setBackground(MC_YELLOW);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(11, 16, 11, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void styleNavButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 11));
        button.setForeground(DARK);
        button.setBackground(new Color(255, 246, 218));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(11, 14, 11, 14));
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setForeground(DARK);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(215, 215, 215), 1, true), new EmptyBorder(9, 11, 9, 11)
        ));
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.setGridColor(new Color(235, 235, 235));
        table.setSelectionBackground(new Color(255, 239, 190));
        table.setSelectionForeground(DARK);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(255, 246, 218));
        table.getTableHeader().setForeground(DARK);
        table.getTableHeader().setPreferredSize(new Dimension(0, 32));
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CafeApp().setVisible(true);
        });
    }

}