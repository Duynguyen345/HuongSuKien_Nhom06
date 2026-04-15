package giaodien_UI;

import javax.swing.*;
import java.awt.*;

public class ConvenienceStoreView extends JFrame {

    public ConvenienceStoreView() {
        setTitle("Hệ thống Cửa hàng Tiện lợi");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Mở full màn hình
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        showLogin(); // Lúc đầu hiện Login
    }

    private void showLogin() {
        Login loginPanel = new Login();
        
        // Khi bấm nút Đăng nhập ở panel Login
        loginPanel.addLoginEvent(e -> {
            // Bước 1: Xóa sạch những gì đang có trên Frame
            getContentPane().removeAll(); 
            
            // Bước 2: Thay thế bằng giao diện bán hàng
            showMainSystem(); 
            
            // Bước 3: Refresh lại giao diện để Java vẽ lại cái mới
            revalidate();
            repaint();
        });

        // Canh lề cho Login Panel nằm giữa Frame
        JPanel center = new JPanel(new GridBagLayout());
        center.add(loginPanel);
        add(center);
    }

    private void showMainSystem() {
        setLayout(new BorderLayout());
        
        // Tạo một cái Sidebar bên trái cho giống app thật
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.add(new JLabel("<html><font color='white'>MENU CHÍNH</font></html>"));
        
        // Vùng nội dung chính ở giữa
        JPanel mainContent = new JPanel();
        mainContent.add(new JLabel("CHÀO MỪNG BẠN ĐẾN VỚI HỆ THỐNG BÁN HÀNG!"));

        add(sidebar, BorderLayout.WEST);
        add(mainContent, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        new ConvenienceStoreView().setVisible(true);
    }
}