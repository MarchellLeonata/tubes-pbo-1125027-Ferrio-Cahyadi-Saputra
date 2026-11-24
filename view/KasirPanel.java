package view;

import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import model.CartItem;
import model.KustomisasiOpsi;

public class KasirPanel extends JPanel {

    private static final Color MC_YELLOW = new Color(255, 188, 0);
    private static final Color MC_RED = new Color(218, 41, 28);
    private static final Color DARK = new Color(35, 35, 35);
    private static final Color LIGHT_BG = new Color(247, 247, 247);
    private static final Color MUTED = new Color(110, 110, 110);

    private JComboBox<String> cmbMenu = new JComboBox<>();
    private JTextField txtJumlahBeli = new JTextField("1", 5);

    private boolean rotiMode = false;

    // Kustomisasi Kopi / Minuman
    private JPanel panelKopiCustom = new JPanel(new GridLayout(0, 2, 8, 8));
    private JComboBox<KustomisasiOpsi> cmbLevelEs = new JComboBox<>();
    private JComboBox<KustomisasiOpsi> cmbEspresso = new JComboBox<>();
    private JComboBox<KustomisasiOpsi> cmbKaramel = new JComboBox<>();

    // Kustomisasi Roti / Roti Bakar
    private JPanel panelRotiCustom = new JPanel(new GridLayout(0, 2, 8, 8));
    private JComboBox<KustomisasiOpsi> cmbToppingRoti = new JComboBox<>();
    private JComboBox<KustomisasiOpsi> cmbPanggang = new JComboBox<>();

    private JLabel lblMenuTerpilih = new JLabel("Belum ada menu dipilih");
    private JLabel lblHargaTerpilih = new JLabel("Pilih menu untuk memulai pesanan");
    private JLabel lblInfoDiskon = new JLabel("Subtotal: Rp 0 | Diskon: 0%");
    private JLabel lblTotalHarga = new JLabel("Total: Rp 0");

    private JPanel panelDaftarMenu = new JPanel(new GridLayout(0, 3, 16, 16));

    private JButton btnTambahKeranjang = new JButton("+ TAMBAH KE KERANJANG");
    private JButton btnHapusItem = new JButton("Hapus Item");
    private JButton btnKosongkan = new JButton("Kosongkan");
    private JButton btnPesan = new JButton("PROSES PESANAN");

    private DefaultTableModel modelKeranjang = new DefaultTableModel(
        new String[]{"Menu & Kustomisasi", "Qty", "Subtotal"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private JTable tblKeranjang = new JTable(modelKeranjang);

    public KasirPanel() {
        setLayout(new BorderLayout(18, 14));
        setBackground(LIGHT_BG);
        setBorder(new EmptyBorder(20, 24, 18, 24));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Menu Cafe");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(DARK);

        JLabel subtitle = new JLabel(
            "Pilih menu, atur kustomisasi sesuai jenis menu, masukkan ke keranjang lalu proses"
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        heading.add(title);
        heading.add(Box.createVerticalStrut(4));
        heading.add(subtitle);

        add(heading, BorderLayout.NORTH);

        panelDaftarMenu.setBackground(LIGHT_BG);
        panelDaftarMenu.setBorder(new EmptyBorder(4, 2, 4, 2));

        JScrollPane scrollMenu = new JScrollPane(panelDaftarMenu);
        scrollMenu.setBorder(null);
        scrollMenu.getViewport().setBackground(LIGHT_BG);
        scrollMenu.getVerticalScrollBar().setUnitIncrement(18);

        add(scrollMenu, BorderLayout.CENTER);

        JPanel orderCard = new JPanel();
        orderCard.setLayout(new BoxLayout(orderCard, BoxLayout.Y_AXIS));
        orderCard.setBackground(Color.WHITE);
        orderCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(16, 18, 16, 18)
        ));
        orderCard.setPreferredSize(new Dimension(385, 10));

        JLabel orderTitle = new JLabel("Kustomisasi & Keranjang");
        orderTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        orderTitle.setForeground(DARK);
        orderTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblMenuTerpilih.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblMenuTerpilih.setForeground(DARK);
        lblMenuTerpilih.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHargaTerpilih.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblHargaTerpilih.setForeground(MUTED);
        lblHargaTerpilih.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Kustomisasi Kopi
        panelKopiCustom.setOpaque(false);
        panelKopiCustom.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelKopiCustom.setMaximumSize(new Dimension(350, 110));

        JLabel lblEs = new JLabel("Jumlah Es:");
        lblEs.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblEs.setForeground(DARK);

        JLabel lblEsp = new JLabel("Extra Espresso:");
        lblEsp.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblEsp.setForeground(DARK);

        JLabel lblKar = new JLabel("Karamel:");
        lblKar.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblKar.setForeground(DARK);

        styleCombo(cmbLevelEs);
        styleCombo(cmbEspresso);
        styleCombo(cmbKaramel);

        panelKopiCustom.add(lblEs);
        panelKopiCustom.add(cmbLevelEs);
        panelKopiCustom.add(lblEsp);
        panelKopiCustom.add(cmbEspresso);
        panelKopiCustom.add(lblKar);
        panelKopiCustom.add(cmbKaramel);

        // Kustomisasi Roti
        panelRotiCustom.setOpaque(false);
        panelRotiCustom.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelRotiCustom.setMaximumSize(new Dimension(350, 76));

        JLabel lblTopping = new JLabel("Topping Roti:");
        lblTopping.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblTopping.setForeground(DARK);

        JLabel lblPanggang = new JLabel("Panggang:");
        lblPanggang.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblPanggang.setForeground(DARK);

        styleCombo(cmbToppingRoti);
        styleCombo(cmbPanggang);

        panelRotiCustom.add(lblTopping);
        panelRotiCustom.add(cmbToppingRoti);
        panelRotiCustom.add(lblPanggang);
        panelRotiCustom.add(cmbPanggang);

        JPanel qtyPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        qtyPanel.setOpaque(false);
        qtyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        qtyPanel.setMaximumSize(new Dimension(350, 36));

        JLabel lblQty = new JLabel("Jumlah Porsi:");
        lblQty.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblQty.setForeground(DARK);

        txtJumlahBeli.setHorizontalAlignment(JTextField.CENTER);
        txtJumlahBeli.setFont(new Font("SansSerif", Font.BOLD, 12));
        txtJumlahBeli.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(210, 210, 210), 1, true),
            new EmptyBorder(4, 6, 4, 6)
        ));

        qtyPanel.add(lblQty);
        qtyPanel.add(txtJumlahBeli);

        btnTambahKeranjang.setMaximumSize(new Dimension(350, 38));
        btnTambahKeranjang.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnTambahKeranjang.setBackground(MC_YELLOW);
        btnTambahKeranjang.setForeground(DARK);
        btnTambahKeranjang.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnTambahKeranjang.setFocusPainted(false);
        btnTambahKeranjang.setBorderPainted(false);

        JLabel cartLabel = new JLabel("DAFTAR KERANJANG");
        cartLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        cartLabel.setForeground(MUTED);
        cartLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        tblKeranjang.setRowHeight(26);
        tblKeranjang.setFont(new Font("SansSerif", Font.PLAIN, 11));
        tblKeranjang.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        tblKeranjang.getTableHeader().setBackground(new Color(245, 245, 245));
        tblKeranjang.setSelectionBackground(new Color(255, 238, 190));
        tblKeranjang.getColumnModel().getColumn(0).setPreferredWidth(195);
        tblKeranjang.getColumnModel().getColumn(1).setPreferredWidth(40);
        tblKeranjang.getColumnModel().getColumn(2).setPreferredWidth(95);

        JScrollPane scrollKeranjang = new JScrollPane(tblKeranjang);
        scrollKeranjang.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollKeranjang.setPreferredSize(new Dimension(350, 155));
        scrollKeranjang.setMaximumSize(new Dimension(350, 170));

        JPanel cartActions = new JPanel(new GridLayout(1, 2, 8, 0));
        cartActions.setOpaque(false);
        cartActions.setAlignmentX(Component.LEFT_ALIGNMENT);
        cartActions.setMaximumSize(new Dimension(350, 32));

        btnHapusItem.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnHapusItem.setBackground(new Color(245, 245, 245));
        btnHapusItem.setForeground(DARK);
        btnHapusItem.setFocusPainted(false);

        btnKosongkan.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnKosongkan.setBackground(new Color(245, 245, 245));
        btnKosongkan.setForeground(MC_RED);
        btnKosongkan.setFocusPainted(false);

        cartActions.add(btnHapusItem);
        cartActions.add(btnKosongkan);

        lblInfoDiskon.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblInfoDiskon.setForeground(MUTED);
        lblInfoDiskon.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTotalHarga.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTotalHarga.setForeground(DARK);
        lblTotalHarga.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnPesan.setMaximumSize(new Dimension(350, 42));
        btnPesan.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPesan.setBackground(MC_RED);
        btnPesan.setForeground(Color.WHITE);
        btnPesan.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnPesan.setFocusPainted(false);
        btnPesan.setBorderPainted(false);

        orderCard.add(orderTitle);
        orderCard.add(Box.createVerticalStrut(10));
        orderCard.add(lblMenuTerpilih);
        orderCard.add(Box.createVerticalStrut(3));
        orderCard.add(lblHargaTerpilih);
        orderCard.add(Box.createVerticalStrut(12));
        orderCard.add(panelKopiCustom);
        orderCard.add(panelRotiCustom);
        orderCard.add(Box.createVerticalStrut(8));
        orderCard.add(qtyPanel);
        orderCard.add(Box.createVerticalStrut(10));
        orderCard.add(btnTambahKeranjang);
        orderCard.add(Box.createVerticalStrut(12));
        orderCard.add(new JSeparator());
        orderCard.add(Box.createVerticalStrut(10));
        orderCard.add(cartLabel);
        orderCard.add(Box.createVerticalStrut(6));
        orderCard.add(scrollKeranjang);
        orderCard.add(Box.createVerticalStrut(8));
        orderCard.add(cartActions);
        orderCard.add(Box.createVerticalStrut(10));
        orderCard.add(lblInfoDiskon);
        orderCard.add(Box.createVerticalStrut(4));
        orderCard.add(lblTotalHarga);
        orderCard.add(Box.createVerticalStrut(10));
        orderCard.add(btnPesan);
        orderCard.add(Box.createVerticalGlue());

        add(orderCard, BorderLayout.EAST);

        setDefaultOpsiTanpaBahan();
        setRotiMode(false);
    }

    private void styleCombo(JComboBox<KustomisasiOpsi> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        combo.setBackground(Color.WHITE);
    }

    private void setDefaultOpsiTanpaBahan() {
        cmbLevelEs.removeAllItems();
        cmbLevelEs.addItem(new KustomisasiOpsi(0, "KOPI", "ES", "Normal Ice", 0, null, 0));
        cmbLevelEs.addItem(new KustomisasiOpsi(0, "KOPI", "ES", "Less Ice", 0, null, 0));
        cmbLevelEs.addItem(new KustomisasiOpsi(0, "KOPI", "ES", "No Ice", 0, null, 0));

        cmbEspresso.removeAllItems();
        cmbEspresso.addItem(new KustomisasiOpsi(0, "KOPI", "ESPRESSO", "Tanpa Extra Shot", 0, null, 0));

        cmbKaramel.removeAllItems();
        cmbKaramel.addItem(new KustomisasiOpsi(0, "KOPI", "KARAMEL", "Tanpa Karamel", 0, null, 0));

        cmbToppingRoti.removeAllItems();
        cmbToppingRoti.addItem(new KustomisasiOpsi(0, "ROTI", "TOPPING", "Polos", 0, null, 0));

        cmbPanggang.removeAllItems();
        cmbPanggang.addItem(new KustomisasiOpsi(0, "ROTI", "PANGGANG", "Normal", 0, null, 0));
        cmbPanggang.addItem(new KustomisasiOpsi(0, "ROTI", "PANGGANG", "Garing", 0, null, 0));
    }

    public void isiOpsiKustomisasiDariDatabase(List<KustomisasiOpsi> daftarOpsi) {
        setDefaultOpsiTanpaBahan();

        for (KustomisasiOpsi opsi : daftarOpsi) {
            String kat = opsi.getKategori() != null ? opsi.getKategori().toUpperCase(Locale.ROOT) : "";
            String tipe = opsi.getTipeOpsi() != null ? opsi.getTipeOpsi().toUpperCase(Locale.ROOT) : "";

            if ("KOPI".equals(kat)) {
                if ("ES".equals(tipe)) {
                    cmbLevelEs.addItem(opsi);
                } else if ("ESPRESSO".equals(tipe)) {
                    cmbEspresso.addItem(opsi);
                } else if ("KARAMEL".equals(tipe)) {
                    cmbKaramel.addItem(opsi);
                }
            } else if ("ROTI".equals(kat)) {
                if ("TOPPING".equals(tipe)) {
                    cmbToppingRoti.addItem(opsi);
                } else if ("PANGGANG".equals(tipe)) {
                    cmbPanggang.addItem(opsi);
                }
            }
        }
    }

    public void setRotiMode(boolean isRoti) {
        this.rotiMode = isRoti;
        panelKopiCustom.setVisible(!isRoti);
        panelRotiCustom.setVisible(isRoti);
        revalidate();
        repaint();
    }

    public boolean isRotiMode() {
        return rotiMode;
    }

    public void resetKustomisasi() {
        if (cmbLevelEs.getItemCount() > 0) cmbLevelEs.setSelectedIndex(0);
        if (cmbEspresso.getItemCount() > 0) cmbEspresso.setSelectedIndex(0);
        if (cmbKaramel.getItemCount() > 0) cmbKaramel.setSelectedIndex(0);
        if (cmbToppingRoti.getItemCount() > 0) cmbToppingRoti.setSelectedIndex(0);
        if (cmbPanggang.getItemCount() > 0) cmbPanggang.setSelectedIndex(0);
        txtJumlahBeli.setText("1");
    }

    public List<KustomisasiOpsi> getOpsiTerpilih() {
        List<KustomisasiOpsi> hasil = new ArrayList<>();
        if (!rotiMode) {
            if (cmbLevelEs.getSelectedItem() != null) {
                hasil.add((KustomisasiOpsi) cmbLevelEs.getSelectedItem());
            }
            if (cmbEspresso.getSelectedItem() != null) {
                hasil.add((KustomisasiOpsi) cmbEspresso.getSelectedItem());
            }
            if (cmbKaramel.getSelectedItem() != null) {
                hasil.add((KustomisasiOpsi) cmbKaramel.getSelectedItem());
            }
        } else {
            if (cmbToppingRoti.getSelectedItem() != null) {
                hasil.add((KustomisasiOpsi) cmbToppingRoti.getSelectedItem());
            }
            if (cmbPanggang.getSelectedItem() != null) {
                hasil.add((KustomisasiOpsi) cmbPanggang.getSelectedItem());
            }
        }
        return hasil;
    }

    public void refreshKeranjang(List<CartItem> keranjang, double subtotal, double diskonRate) {
        NumberFormat format = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        modelKeranjang.setRowCount(0);

        for (CartItem item : keranjang) {
            modelKeranjang.addRow(new Object[]{
                item.getNamaMenuLengkap(),
                item.getJumlah(),
                "Rp " + format.format(item.getSubtotal())
            });
        }

        double total = subtotal - (subtotal * diskonRate);
        lblInfoDiskon.setText(
            "Subtotal: Rp " + format.format(subtotal) + " | Diskon: " + (int) (diskonRate * 100) + "%"
        );
        lblTotalHarga.setText("Total: Rp " + format.format(total));
    }

    public int getSelectedKeranjangRow() {
        return tblKeranjang.getSelectedRow();
    }

    public JComboBox<String> getCmbMenu() { return cmbMenu; }
    public JTextField getTxtJumlahBeli() { return txtJumlahBeli; }
    public JLabel getLblMenuTerpilih() { return lblMenuTerpilih; }
    public JLabel getLblHargaTerpilih() { return lblHargaTerpilih; }
    public JLabel getLblInfoDiskon() { return lblInfoDiskon; }
    public JLabel getLblTotalHarga() { return lblTotalHarga; }
    public JPanel getPanelDaftarMenu() { return panelDaftarMenu; }
    public JButton getBtnTambahKeranjang() { return btnTambahKeranjang; }
    public JButton getBtnHapusItem() { return btnHapusItem; }
    public JButton getBtnKosongkan() { return btnKosongkan; }
    public JButton getBtnPesan() { return btnPesan; }
}