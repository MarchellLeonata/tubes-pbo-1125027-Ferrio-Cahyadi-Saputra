import controller.CafeController;
import exception.CafeException;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import model.CartItem;
import model.KustomisasiOpsi;
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

    private List<CartItem> keranjang = new ArrayList<>();

    private JLabel lblWelcome = new JLabel();
    private JButton btnMenuKasir = new JButton("Panel Kasir");
    private JButton btnMenuGudang = new JButton("Panel Stok & History");
    private CardLayout subCardLayout = new CardLayout();
    private JPanel subPanel = new JPanel(subCardLayout);

    public CafeApp() {
        setTitle("Sistem Kasir & Inventori Cafe");
        setSize(1240, 780);
        setMinimumSize(new Dimension(1040, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(LIGHT_BG);

        lblStatus.setOpaque(true);
        lblStatus.setBackground(Color.WHITE);
        lblStatus.setBorder(new EmptyBorder(10, 18, 10, 18));
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 12));
        add(lblStatus, BorderLayout.SOUTH);

        mainPanel.add(buatPanelLogin(), "LOGIN");
        mainPanel.add(buatPanelUtama(), "UTAMA");
        add(mainPanel, BorderLayout.CENTER);

        cardLayout.show(mainPanel, "LOGIN");
    }

    private boolean isMenuRoti(String namaMenu) {
        return namaMenu != null && namaMenu.toLowerCase(Locale.ROOT).contains("roti");
    }

    private void setPesan(String pesan, boolean isError) {
        lblStatus.setText(pesan);
        lblStatus.setForeground(isError ? MC_RED : new Color(0, 130, 65));
    }

    private JPanel buatPanelLogin() {
        loginPanel = new LoginPanel();
        loginPanel.getBtnLogin().addActionListener(e -> prosesLogin());
        return loginPanel;
    }

    private JPanel buatPanelUtama() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(LIGHT_BG);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
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
            keranjang.clear();
            if (kasirPanel != null) {
                kasirPanel.refreshKeranjang(keranjang, 0, 0);
            }
            loginPanel.clearForm();
            cardLayout.show(mainPanel, "LOGIN");
            setPesan("Berhasil logout.", false);
        });

        return container;
    }

    private JPanel buatPanelKasir() {
        kasirPanel = new KasirPanel();

        kasirPanel.getBtnTambahKeranjang().addActionListener(e -> tambahKeKeranjang());

        kasirPanel.getBtnHapusItem().addActionListener(e -> {
            int row = kasirPanel.getSelectedKeranjangRow();
            if (row < 0 || row >= keranjang.size()) {
                setPesan("Pilih baris item di tabel keranjang yang ingin dihapus.", true);
                return;
            }
            CartItem removed = keranjang.remove(row);
            perbaruiTampilanKeranjang();
            setPesan("Menghapus " + removed.getNamaMenu() + " dari keranjang.", false);
        });

        kasirPanel.getBtnKosongkan().addActionListener(e -> {
            keranjang.clear();
            perbaruiTampilanKeranjang();
            setPesan("Keranjang pesanan dikosongkan.", false);
        });

        kasirPanel.getBtnPesan().addActionListener(e -> {
            try {
                controller.prosesKeranjang(currentUser.getUsername(), keranjang);
                keranjang.clear();
                perbaruiTampilanKeranjang();
                kasirPanel.resetKustomisasi();
                setPesan("Semua pesanan di keranjang berhasil diproses! Stok bahan baku berkurang.", false);
            } catch (CafeException ex) {
                setPesan(ex.getMessage(), true);
            }
        });

        return kasirPanel;
    }

    private void tambahKeKeranjang() {
        try {
            if (kasirPanel.getCmbMenu().getSelectedItem() == null) {
                throw new CafeException("Pilih menu terlebih dahulu!");
            }

            String selected = kasirPanel.getCmbMenu().getSelectedItem().toString();
            String[] bagianUtama = selected.split(" - ", 2);
            int menuId = Integer.parseInt(bagianUtama[0].trim());

            String sisa = bagianUtama.length > 1 ? bagianUtama[1] : "";
            int posHarga = sisa.lastIndexOf(" (Rp ");
            if (posHarga < 0) {
                throw new CafeException("Format harga menu tidak valid!");
            }

            String namaMenu = sisa.substring(0, posHarga).trim();
            double hargaDasar = Double.parseDouble(sisa.substring(posHarga + 5, sisa.length() - 1).trim());

            int qty = Integer.parseInt(kasirPanel.getTxtJumlahBeli().getText().trim());
            controller.validasiTransaksi(qty);

            List<KustomisasiOpsi> opsiTerpilih = kasirPanel.getOpsiTerpilih();
            boolean digabung = false;

            for (CartItem item : keranjang) {
                if (item.isSameConfiguration(menuId, opsiTerpilih)) {
                    item.setJumlah(item.getJumlah() + qty);
                    digabung = true;
                    break;
                }
            }

            if (!digabung) {
                keranjang.add(new CartItem(
                    menuId, namaMenu, hargaDasar, qty, opsiTerpilih
                ));
            }

            perbaruiTampilanKeranjang();
            kasirPanel.resetKustomisasi();
            setPesan("Ditambahkan ke keranjang: " + namaMenu + " x" + qty, false);

        } catch (NumberFormatException ex) {
            setPesan("Jumlah porsi harus berupa angka bulat!", true);
        } catch (CafeException ex) {
            setPesan(ex.getMessage(), true);
        }
    }

    private void perbaruiTampilanKeranjang() {
        double subtotal = controller.hitungSubtotalKeranjang(keranjang);
        int totalPorsi = controller.hitungTotalPorsi(keranjang);
        double diskonRate = keranjang.isEmpty() ? 0 : controller.getDiskonRate(totalPorsi);
        kasirPanel.refreshKeranjang(keranjang, subtotal, diskonRate);
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
        String hargaTeks = "";
        double hargaAngka = 0;

        int posisiHarga = nama.lastIndexOf(" (Rp ");
        if (posisiHarga >= 0) {
            try {
                hargaAngka = Double.parseDouble(nama.substring(posisiHarga + 5, nama.length() - 1));
                NumberFormat format = NumberFormat.getNumberInstance(new Locale("id", "ID"));
                hargaTeks = "Rp " + format.format(hargaAngka);
            } catch (NumberFormatException e) {
                hargaTeks = nama.substring(posisiHarga + 2, nama.length() - 1);
            }
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

        JLabel foodMark = new JLabel(isMenuRoti(nama) ? "R" : "C");
        foodMark.setFont(new Font("Arial", Font.BOLD, 42));
        foodMark.setForeground(MC_RED);
        imageArea.add(foodMark);

        JLabel nameLabel = new JLabel(nama);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        nameLabel.setForeground(DARK);

        JLabel priceLabel = new JLabel(hargaTeks);
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        priceLabel.setForeground(MC_RED);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(nameLabel);
        info.add(Box.createVerticalStrut(6));
        info.add(priceLabel);

        final String namaMenu = nama;
        final String hargaMenu = hargaTeks;

        JButton choose = new JButton("PILIH MENU  +");
        choose.setFont(new Font("SansSerif", Font.BOLD, 10));
        choose.setForeground(DARK);
        choose.setBackground(MC_YELLOW);
        choose.setFocusPainted(false);
        choose.setBorder(new EmptyBorder(9, 8, 9, 8));

        choose.addActionListener(e -> {
            kasirPanel.getCmbMenu().setSelectedIndex(index);
            kasirPanel.getLblMenuTerpilih().setText(namaMenu);
            kasirPanel.getLblHargaTerpilih().setText("Harga Dasar: " + hargaMenu);
            kasirPanel.setRotiMode(isMenuRoti(namaMenu));
            kasirPanel.resetKustomisasi();
            setPesan("Menu " + namaMenu + " dipilih. Silakan atur kustomisasi lalu tambah ke keranjang.", false);
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
            panelMenu.add(buatKartuMenu(comboMenu.getItemAt(i), i));
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

        kasirPanel.isiOpsiKustomisasiDariDatabase(controller.getDaftarKustomisasi());

        if (kasirPanel.getCmbMenu().getItemCount() > 0) {
            kasirPanel.getCmbMenu().setSelectedIndex(0);
            String first = kasirPanel.getCmbMenu().getSelectedItem().toString();
            String[] data = first.split(" - ", 2);
            String nama = data.length > 1 ? data[1] : first;
            int p = nama.lastIndexOf(" (Rp ");

            if (p >= 0) {
                try {
                    double h = Double.parseDouble(nama.substring(p + 5, nama.length() - 1));
                    NumberFormat format = NumberFormat.getNumberInstance(new Locale("id", "ID"));
                    kasirPanel.getLblHargaTerpilih().setText("Harga Dasar: Rp " + format.format(h));
                } catch (NumberFormatException ex) {
                    kasirPanel.getLblHargaTerpilih().setText("Harga Dasar: " + nama.substring(p + 2, nama.length() - 1));
                }
                nama = nama.substring(0, p);
            }
            kasirPanel.getLblMenuTerpilih().setText(nama);
            kasirPanel.setRotiMode(isMenuRoti(nama));
        }

        buatUlangKartuMenu();
        perbaruiTampilanKeranjang();
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