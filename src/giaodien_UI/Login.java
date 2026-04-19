package giaodien_UI;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Login extends JPanel {
    private JTextField txtUser = new JTextField(15);
    private JPasswordField txtPass = new JPasswordField(15);
    private CustomButton btnLogin = new CustomButton("Đăng nhập", new Color(70, 130, 180), Color.WHITE);

    public Login() {
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Mã nhân viên:"), gbc);
        gbc.gridx = 1;
        add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        add(txtPass, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(btnLogin, gbc);
    }

    // --- QUAN TRỌNG: Cần hàm này để lấy nút ra gắn sự kiện ---
    public CustomButton getBtnLogin() {
        return btnLogin;
    }

    public void addLoginEvent(ActionListener event) {
        btnLogin.addActionListener(event);
    }
}