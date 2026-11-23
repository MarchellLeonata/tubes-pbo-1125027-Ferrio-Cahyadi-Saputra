package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class LoginPanel extends JPanel {

    private JTextField txtUser = new JTextField(15);
    private JPasswordField txtPass = new JPasswordField(15);
    private JButton btnLogin = new JButton("MASUK");

    private static final Color MC_YELLOW = new Color(255, 188, 0);
    private static final Color MC_RED = new Color(218, 41, 28);
    private static final Color DARK = new Color(35, 35, 35);
    private static final Color MUTED = new Color(110, 110, 110);

    public LoginPanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 245, 245));

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

        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(330, 44));
        btnLogin.setBackground(MC_RED);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);

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

        add(card);
    }

    private JLabel leftLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
    }
    
    public void clearForm() {
        txtUser.setText("");
        txtPass.setText("");
    }

    public String getUsername() {
        return txtUser.getText().trim();
    }

    public String getPassword() {
        return new String(txtPass.getPassword()).trim();
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }
}