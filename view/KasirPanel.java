
package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class KasirPanel extends JPanel {

    private static final Color MC_YELLOW = new Color(255, 188, 0);
    private static final Color MC_RED = new Color(218, 41, 28);
    private static final Color DARK = new Color(35, 35, 35);
    private static final Color LIGHT_BG = new Color(247, 247, 247);
    private static final Color MUTED = new Color(110, 110, 110);

    private JComboBox<String> cmbMenu = new JComboBox<>();
    private JTextField txtJumlahBeli = new JTextField(5);

    private JLabel lblMenuTerpilih =
        new JLabel("Belum ada menu dipilih");

    private JLabel lblHargaTerpilih =
        new JLabel("Pilih menu untuk memulai pesanan");

    private JLabel lblInfoDiskon =
        new JLabel("Masukkan jumlah porsi untuk melihat diskon.");

    private JLabel lblTotalHarga =
        new JLabel("Total: Rp 0");

    private JPanel panelDaftarMenu =
        new JPanel(new GridLayout(0, 3, 16, 16));

    private JButton btnPesan = new JButton("PROSES PESANAN");

    public KasirPanel() {
        setLayout(new BorderLayout(18, 14));
        setBackground(LIGHT_BG);
        setBorder(new EmptyBorder(22, 24, 20, 24));

        // Bagian Judul
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Coffe");
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        title.setForeground(DARK);

        JLabel subtitle = new JLabel(
            "Pilih Kopi favorit kamu dan mulai pesanan"
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        heading.add(title);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitle);

        add(heading, BorderLayout.NORTH);

        // Daftar Menu
        panelDaftarMenu.setBackground(LIGHT_BG);
        panelDaftarMenu.setBorder(new EmptyBorder(4, 2, 4, 2));

        JScrollPane scrollMenu = new JScrollPane(panelDaftarMenu);
        scrollMenu.setBorder(null);
        scrollMenu.getViewport().setBackground(LIGHT_BG);
        scrollMenu.getVerticalScrollBar().setUnitIncrement(18);

        add(scrollMenu, BorderLayout.CENTER);

        // Bagian Pesanan
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

        lblMenuTerpilih.setFont(
            new Font("SansSerif", Font.BOLD, 15)
        );
        lblMenuTerpilih.setForeground(DARK);
        lblMenuTerpilih.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHargaTerpilih.setFont(
            new Font("SansSerif", Font.PLAIN, 12)
        );
        lblHargaTerpilih.setForeground(MUTED);
        lblHargaTerpilih.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblInfoDiskon.setFont(
            new Font("SansSerif", Font.PLAIN, 11)
        );
        lblInfoDiskon.setForeground(MUTED);

        lblTotalHarga.setFont(
            new Font("SansSerif", Font.BOLD, 12)
        );
        lblTotalHarga.setForeground(MUTED);

        JLabel qtyLabel = new JLabel("JUMLAH PORSI");
        qtyLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        qtyLabel.setForeground(MUTED);

        txtJumlahBeli.setHorizontalAlignment(JTextField.CENTER);
        txtJumlahBeli.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtJumlahBeli.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(6, 9, 6, 9)
        ));
        txtJumlahBeli.setMaximumSize(new Dimension(210, 40));

        btnPesan.setMaximumSize(new Dimension(220, 44));
        btnPesan.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPesan.setBackground(MC_RED);
        btnPesan.setForeground(Color.WHITE);
        btnPesan.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnPesan.setFocusPainted(false);
        btnPesan.setBorderPainted(false);

        orderCard.add(orderTitle);
        orderCard.add(Box.createVerticalStrut(24));
        orderCard.add(selectedCaption);
        orderCard.add(Box.createVerticalStrut(8));
        orderCard.add(lblMenuTerpilih);
        orderCard.add(Box.createVerticalStrut(5));
        orderCard.add(lblHargaTerpilih);
        orderCard.add(Box.createVerticalStrut(8));
        orderCard.add(lblInfoDiskon);
        orderCard.add(lblTotalHarga);
        orderCard.add(Box.createVerticalStrut(25));
        orderCard.add(new JSeparator());
        orderCard.add(Box.createVerticalStrut(22));
        orderCard.add(qtyLabel);
        orderCard.add(Box.createVerticalStrut(8));
        orderCard.add(txtJumlahBeli);
        orderCard.add(Box.createVerticalStrut(22));
        orderCard.add(btnPesan);
        orderCard.add(Box.createVerticalGlue());

        add(orderCard, BorderLayout.EAST);
    }

    public JComboBox<String> getCmbMenu() {
        return cmbMenu;
    }

    public JTextField getTxtJumlahBeli() {
        return txtJumlahBeli;
    }

    public JLabel getLblMenuTerpilih() {
        return lblMenuTerpilih;
    }

    public JLabel getLblHargaTerpilih() {
        return lblHargaTerpilih;
    }

    public JLabel getLblInfoDiskon() {
        return lblInfoDiskon;
    }

    public JLabel getLblTotalHarga() {
        return lblTotalHarga;
    }

    public JPanel getPanelDaftarMenu() {
        return panelDaftarMenu;
    }

    public JButton getBtnPesan() {
        return btnPesan;
    }
}
