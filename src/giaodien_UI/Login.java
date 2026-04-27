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
        
        // Sử dụng GridLayout đơn giản với 6 dòng, 1 cột, khoảng cách 10px
        setLayout(new GridLayout(6, 1, 10, 10));

        // Tiêu đề
        JLabel title = new JLabel("Đăng nhập hệ thống", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(33, 47, 61));
        
        add(title);
        add(new JLabel("Mã nhân viên:"));
        add(txtUser);
        add(new JLabel("Mật khẩu:"));
        add(txtPass);
        add(btnLogin);
    }

    public CustomButton getBtnLogin() { return btnLogin; }

    /** Trả về Mã nhân viên đã nhập */
    public String getMaNV()    { return txtUser.getText().trim(); }

    /** Trả về Mật khẩu đã nhập */
    public String getMatKhau() { return new String(txtPass.getPassword()); }
}