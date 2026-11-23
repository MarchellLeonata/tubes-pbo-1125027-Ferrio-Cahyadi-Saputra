
package view;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import model.BahanBaku;

public class GudangHistoryPanel extends JPanel {

    private static final Color DARK = new Color(35, 35, 35);
    private static final Color LIGHT_BG = new Color(247, 247, 247);
    private static final Color MC_YELLOW = new Color(255, 188, 0);

    private DefaultTableModel modelBahan = new DefaultTableModel(
        new String[]{"ID", "Nama Bahan", "Stok", "Satuan"}, 0
    );

    private JTable tblBahan = new JTable(modelBahan);

    private JTextField txtIdBahan = new JTextField(5);
    private JTextField txtUbahStok = new JTextField(5);

    private DefaultTableModel modelHistory = new DefaultTableModel(
        new String[]{"ID", "Waktu", "Kasir", "Menu", "Qty", "Total"}, 0
    );

    private JTable tblHistory = new JTable(modelHistory);

    private JButton btnTambahStok = new JButton("Tambah Stok (+)");
    private JButton btnKurangStok = new JButton("Kurangi Stok (-)");

    public GudangHistoryPanel() {
        setLayout(new GridLayout(2, 1, 14, 14));
        setBackground(LIGHT_BG);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        styleTable(tblBahan);
        styleTable(tblHistory);

        // Panel Persediaan Bahan Baku
        JPanel panelStok = new JPanel(new BorderLayout(8, 12));
        panelStok.setBackground(Color.WHITE);
        panelStok.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(15, 16, 15, 16)
        ));

        JLabel titleStok = new JLabel("Persediaan Bahan Baku");
        titleStok.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleStok.setForeground(DARK);

        panelStok.add(titleStok, BorderLayout.NORTH);
        panelStok.add(new JScrollPane(tblBahan), BorderLayout.CENTER);

        // Form Pengubahan Stok
        JPanel formStok = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 5));
        formStok.setBackground(Color.WHITE);

        btnTambahStok.setBackground(MC_YELLOW);
        btnTambahStok.setForeground(DARK);
        btnTambahStok.setFocusPainted(false);
        btnTambahStok.setBorder(new EmptyBorder(10, 14, 10, 14));

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

        // Panel History Transaksi
        JPanel panelHistory = new JPanel(new BorderLayout(8, 12));
        panelHistory.setBackground(Color.WHITE);
        panelHistory.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(15, 16, 15, 16)
        ));

        JLabel titleHistory = new JLabel("Riwayat Transaksi Pembelian");
        titleHistory.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleHistory.setForeground(DARK);

        panelHistory.add(titleHistory, BorderLayout.NORTH);
        panelHistory.add(new JScrollPane(tblHistory), BorderLayout.CENTER);

        add(panelStok);
        add(panelHistory);
    }

    private void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(
            new Font("SansSerif", Font.BOLD, 12)
        );
        table.getTableHeader().setBackground(MC_YELLOW);
        table.getTableHeader().setForeground(DARK);
        table.setSelectionBackground(new Color(255, 238, 190));
        table.setShowVerticalLines(false);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(6, 9, 6, 9)
        ));
    }

    public JButton getBtnTambahStok() {
        return btnTambahStok;
    }

    public JButton getBtnKurangStok() {
        return btnKurangStok;
    }

    public String getIdBahan() {
        return txtIdBahan.getText().trim();
    }

    public String getJumlahStok() {
        return txtUbahStok.getText().trim();
    }

    public void clearForm() {
        txtIdBahan.setText("");
        txtUbahStok.setText("");
    }

    public void refreshTabel(
        List<BahanBaku> daftarBahan,
        List<String[]> daftarHistory
    ) {
        modelBahan.setRowCount(0);

        for (BahanBaku b : daftarBahan) {
            modelBahan.addRow(new Object[]{
                b.getId(),
                b.getNamaBahan(),
                b.getStok(),
                b.getSatuan()
            });
        }

        modelHistory.setRowCount(0);

        for (String[] h : daftarHistory) {
            modelHistory.addRow(h);
        }
    }
}
