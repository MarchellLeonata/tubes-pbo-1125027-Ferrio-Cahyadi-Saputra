import controller.CafeController;
import exception.CafeException;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import model.User;
import view.GudangHistoryPanel;
import view.KasirPanel;
import view.LoginPanel;

public class CafeApp extends JFrame {
    private static final Color MC_YELLOW = new Color(255, 188, 0);
    private static final Color MC_RED = new Color(218, 41, 28);
    private static final Color DARK = new Color(35, 35, 35);
    private static final Color LIGHT_BG = new Color(247, 247, 247);

    
    
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private JLabel lblStatus = new JLabel("Status: Silakan Login terlebih dahulu.");

    private CafeController controller = new CafeController();
    private User currentUser;
    private LoginPanel loginPanel;
    private GudangHistoryPanel gudangHistoryPanel;
    private KasirPanel kasirPanel;

    // Komponen Dashboard & Navigasi
    private JLabel lblWelcome = new JLabel();
    private JButton btnMenuKasir = new JButton("Panel Kasir");
    private JButton btnMenuGudang = new JButton("Panel Stok & History");
    private CardLayout subCardLayout = new CardLayout();
    private JPanel subPanel = new JPanel(subCardLayout);
    
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
    loginPanel = new LoginPanel();

    loginPanel.getBtnLogin().addActionListener(
        e -> prosesLogin()
    );

    return loginPanel;
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
        JLabel logo = new JLabel("C");
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
                 controller.cekAksesKasir(currentUser);

                muatDataMenu();
                subCardLayout.show(subPanel, "KASIR");
                setPesan("Membuka Panel Kasir.", false);

             } catch (CafeException ex) {
                setPesan(ex.getMessage(), true);
         }
    });

         btnMenuGudang.addActionListener(e -> {
                try {
                    controller.cekAksesGudang(currentUser);

                    refreshTabelGudangDanHistory();
                    subCardLayout.show(subPanel, "GUDANG");
                    setPesan("Membuka Panel Stok Bahan & History.", false);

                } catch (CafeException ex) {
                    setPesan(ex.getMessage(), true);
         }
    });

        btnLogout.addActionListener(e -> {
            currentUser = null;
            
            loginPanel.clearForm();
            cardLayout.show(mainPanel, "LOGIN");
            setPesan("Berhasil logout.", false);
        });

        return container;
    }

    //Panel Kasir
    private JPanel buatPanelKasir() {
        kasirPanel = new KasirPanel();

    // Update informasi diskon saat jumlah berubah
        kasirPanel.getTxtJumlahBeli().getDocument().addDocumentListener(
            new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                perbaruiInfoDiskon();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                perbaruiInfoDiskon();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                perbaruiInfoDiskon();
            }
        }
    );

    // Update diskon saat menu berubah
    kasirPanel.getCmbMenu().addActionListener(
        e -> perbaruiInfoDiskon()
    );

    // Tombol proses pesanan
    kasirPanel.getBtnPesan().addActionListener(e -> {
        try {
            if (kasirPanel.getCmbMenu().getSelectedItem() == null) {
                throw new CafeException("Menu belum dipilih!");
            }

            String selected = kasirPanel.getCmbMenu()
                    .getSelectedItem().toString();

            int menuId = Integer.parseInt(selected.split(" - ")[0]);

            int qty = Integer.parseInt(
                kasirPanel.getTxtJumlahBeli().getText().trim()
            );

            controller.validasiTransaksi(qty);

            controller.prosesTransaksi(
                currentUser.getUsername(), menuId, qty
            );

            kasirPanel.getTxtJumlahBeli().setText("");

            setPesan(
                "Transaksi berhasil! Stok bahan baku otomatis berkurang.",
                false
            );

        } catch (NumberFormatException ex) {
            setPesan("Jumlah porsi harus berupa angka!", true);

        } catch (CafeException ex) {
            setPesan(ex.getMessage(), true);
        }
    });

        return kasirPanel;
    }


    private JPanel buatPanelGudangDanHistory() {
        gudangHistoryPanel = new GudangHistoryPanel();

        gudangHistoryPanel.getBtnTambahStok().addActionListener(
            e -> ubahStokBahan(true)
        );

        gudangHistoryPanel.getBtnKurangStok().addActionListener(
            e -> ubahStokBahan(false)
        );

        return gudangHistoryPanel;
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

        JLabel foodMark = new JLabel("C");
        foodMark.setFont(new Font("Arial", Font.BOLD, 42));
        foodMark.setForeground(MC_RED);
        imageArea.add(foodMark);

        JLabel nameLabel = new JLabel(nama);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
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
        kasirPanel.getCmbMenu().setSelectedIndex(index);
        kasirPanel.getLblMenuTerpilih().setText(namaMenu);
        kasirPanel.getLblHargaTerpilih().setText(hargaMenu);

        setPesan("Menu " + namaMenu + " dipilih.", false);
    });

        card.add(imageArea, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        card.add(choose, BorderLayout.SOUTH);

        return card;
    }
    
    private void buatUlangKartuMenu() {
        JPanel panelMenu = kasirPanel.getPanelDaftarMenu();
        JComboBox<String> comboMenu = kasirPanel.getCmbMenu();

        panelMenu.removeAll();

        for (int i = 0; i < comboMenu.getItemCount(); i++) {
            panelMenu.add(
            buatKartuMenu(comboMenu.getItemAt(i), i)
        );
    }

        panelMenu.revalidate();
        panelMenu.repaint();
    }

    private void prosesLogin() {
        try {
            String u = loginPanel.getUsername();
            String p = loginPanel.getPassword();
            
            controller.validasiLogin(u, p);

            currentUser = controller.login(u, p);
            lblWelcome.setText("Halo, " + currentUser.getDeskripsi() + " | ");

            // Atur visibilitas tombol berdasarkan Access Control Role
            btnMenuKasir.setEnabled(controller.bolehAksesKasir(currentUser));
            btnMenuGudang.setEnabled(controller.bolehAksesGudang(currentUser));

            if (controller.bolehAksesKasir(currentUser)) {
                muatDataMenu();
                subCardLayout.show(subPanel, "KASIR");
            } else if (controller.bolehAksesGudang(currentUser)) {
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
        kasirPanel.getCmbMenu().removeAllItems();

        List<String[]> menus = controller.getDaftarMenu();

        for (String[] m : menus) {
            kasirPanel.getCmbMenu().addItem(
             m[0] + " - " + m[1] + " (Rp " + m[2] + ")"
        );
    }

        if (kasirPanel.getCmbMenu().getItemCount() > 0) {
             kasirPanel.getCmbMenu().setSelectedIndex(0);

                 String first = kasirPanel.getCmbMenu()
                .getSelectedItem().toString();

            String[] data = first.split(" - ", 2);
            String nama = data.length > 1 ? data[1] : first;

            int p = nama.lastIndexOf(" (Rp ");

            if (p >= 0) {
                kasirPanel.getLblHargaTerpilih().setText(
                 nama.substring(p + 2, nama.length() - 1)
            );

             nama = nama.substring(0, p);
        }

            kasirPanel.getLblMenuTerpilih().setText(nama);
    }

        buatUlangKartuMenu();
    }

    //Menghitung dan menampilkan Diskon
    private void perbaruiInfoDiskon() {
     try {
        if (kasirPanel.getCmbMenu().getSelectedItem() == null ||
                kasirPanel.getTxtJumlahBeli().getText().trim().isEmpty()) {

            kasirPanel.getLblInfoDiskon().setText(
                "Masukkan jumlah porsi untuk melihat diskon."
            );
            kasirPanel.getLblTotalHarga().setText("Total: Rp 0");
            return;
        }

        String selected = kasirPanel.getCmbMenu()
                .getSelectedItem().toString();

        int posisiHarga = selected.lastIndexOf("(Rp ");

        if (posisiHarga < 0) {
            return;
        }

        String hargaText = selected.substring(
            posisiHarga + 4, selected.length() - 1
        );

        double harga = Double.parseDouble(hargaText);

        int jumlah = Integer.parseInt(
            kasirPanel.getTxtJumlahBeli().getText().trim()
        );

        if (jumlah <= 0) {
            kasirPanel.getLblInfoDiskon().setText(
                "Jumlah harus lebih dari 0."
            );
            return;
        }

        double subtotal = harga * jumlah;
        double diskon = controller.getDiskonRate(jumlah);
        double total = subtotal - (subtotal * diskon);

        NumberFormat format = NumberFormat.getNumberInstance(
            new Locale("id", "ID")
        );

        kasirPanel.getLblInfoDiskon().setText(
            "Subtotal: Rp " + format.format(subtotal)
            + " | Diskon: " + (int)(diskon * 100) + "%"
        );

        kasirPanel.getLblTotalHarga().setText(
            "Total: Rp " + format.format(total)
        );

    } catch (NumberFormatException ex) {
        kasirPanel.getLblInfoDiskon().setText(
            "Masukkan jumlah porsi berupa angka."
        );
    }
}

    private void ubahStokBahan(boolean isTambah) {
        try {
           int idBahan = Integer.parseInt(gudangHistoryPanel.getIdBahan());
           double jumlah = Double.parseDouble(gudangHistoryPanel.getJumlahStok());
            
            controller.validasiPerubahanStok(jumlah);

            double perubahan = isTambah ? jumlah : -jumlah;
            controller.updateStok(idBahan, perubahan);
            refreshTabelGudangDanHistory();
            gudangHistoryPanel.clearForm();
            setPesan("Stok bahan baku berhasil diperbarui!", false);
        } catch (NumberFormatException ex) {
            setPesan("ID dan Jumlah harus berupa angka!", true);
        } catch (CafeException ex) {
            setPesan(ex.getMessage(), true);
        }
    }

        private void refreshTabelGudangDanHistory() throws CafeException {
        
        gudangHistoryPanel.refreshTabel(
        controller.getDaftarBahanBaku(),
        controller.getHistoryTransaksi()
    );
}

    private void styleNavButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 11));
        button.setForeground(DARK);
        button.setBackground(new Color(255, 246, 218));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(11, 14, 11, 14));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CafeApp().setVisible(true);
        });
    }

}