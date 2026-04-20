package giaodien_UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Login extends JPanel {

    private JTextField     txtUser  = new JTextField(18);
    private JPasswordField txtPass  = new JPasswordField(18);
    private CustomButton   btnLogin = new CustomButton(
            "Đăng nhập", new Color(52, 152, 219), Color.WHITE);

    public Login() {
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 40, 30, 40));
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill   = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 0, 6, 0);
        c.gridx  = 0;

        // Tiêu đề
        JLabel title = new JLabel("Đăng nhập hệ thống", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(33, 47, 61));
        c.gridy = 0; c.insets = new Insets(0, 0, 20, 0);
        add(title, c);

        // Mã nhân viên
        c.gridy = 1; c.insets = new Insets(4, 0, 4, 0);
        add(new JLabel("Mã nhân viên:"), c);
        c.gridy = 2;
        add(txtUser, c);

        // Mật khẩu
        c.gridy = 3;
        add(new JLabel("Mật khẩu:"), c);
        c.gridy = 4;
        add(txtPass, c);

        // Nút đăng nhập
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnLogin.setPreferredSize(new Dimension(240, 38));
        c.gridy = 5; c.insets = new Insets(16, 0, 0, 0);
        add(btnLogin, c);
    }

    public CustomButton getBtnLogin() { return btnLogin; }
}