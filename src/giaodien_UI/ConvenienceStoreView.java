package giaodien_UI;

import javax.swing.*;
import java.awt.*;

public class ConvenienceStoreView extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContent;
    private Login loginPanel;

    private CustomButton btnBanHang, btnHoaDon, btnSanPham, btnLoHang, btnKhachHang, btnThongKe, btnNhanVien;

    public ConvenienceStoreView() {
        initInitialWindow();
        initComponents();
        addEvents(); 
    }

    private void initInitialWindow() {
        setTitle("Hệ thống Cửa hàng Tiện lợi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        loginPanel = new Login();
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.add(loginPanel);
        setContentPane(wrapper);
    }

    private void addEvents() {
        // Sự kiện Đăng nhập
        loginPanel.getBtnLogin().addActionListener(e -> {
            // 1. Dựng giao diện (Khởi tạo các nút và Panel)
            buildMainDashboard(); 
            
            // 2. Gắn sự kiện cho các nút vừa tạo
            setupMenuEvents(); 
            
            // 3. Phóng to màn hình
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            revalidate();
            repaint();
        });
    }

    private void setupMenuEvents() {
        btnBanHang.addActionListener(e -> cardLayout.show(mainContent, "BAN_HANG"));
        btnHoaDon.addActionListener(e -> cardLayout.show(mainContent, "HOA_DON"));
        btnSanPham.addActionListener(e -> cardLayout.show(mainContent, "SAN_PHAM"));
        btnLoHang.addActionListener(e -> cardLayout.show(mainContent, "LO_HANG"));
        btnKhachHang.addActionListener(e -> cardLayout.show(mainContent, "KHACH_HANG"));
        btnThongKe.addActionListener(e -> cardLayout.show(mainContent, "THONG_KE"));
        btnNhanVien.addActionListener(e -> cardLayout.show(mainContent, "NHAN_VIEN"));
    }

    private void buildMainDashboard() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setPreferredSize(new Dimension(250, 0));

        JLabel lblTitle = new JLabel("DANH MỤC QUẢN LÝ");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        sidebar.add(lblTitle);

        // Khởi tạo các đối tượng nút
        btnBanHang = createBtn("Bán hàng");
        btnHoaDon = createBtn("Hóa đơn");
        btnSanPham = createBtn("Sản phẩm");
        btnLoHang = createBtn("Lô hàng");
        btnKhachHang = createBtn("Khách hàng");
        btnThongKe = createBtn("Thống kê");
        btnNhanVien = createBtn("Nhân viên");

        sidebar.add(btnBanHang); sidebar.add(btnHoaDon); sidebar.add(btnSanPham);
        sidebar.add(btnLoHang); sidebar.add(btnKhachHang); sidebar.add(btnThongKe);
        sidebar.add(btnNhanVien);

        // Main Content sử dụng CardLayout
        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        
        // --- FIX: Phải thêm đủ tất cả các trang đã khai báo ở setupMenuEvents ---
        mainContent.add(createPage("TRANG BÁN HÀNG"), "BAN_HANG");
        mainContent.add(createPage("TRANG HÓA ĐƠN"), "HOA_DON");
        mainContent.add(createPage("QUẢN LÝ SẢN PHẨM"), "SAN_PHAM");
        mainContent.add(createPage("QUẢN LÝ LÔ HÀNG"), "LO_HANG");
        mainContent.add(createPage("QUẢN LÝ KHÁCH HÀNG"), "KHACH_HANG");
        mainContent.add(createPage("THỐNG KÊ DOANH THU"), "THONG_KE");
        mainContent.add(createPage("QUẢN LÝ NHÂN VIÊN"), "NHAN_VIEN");

        add(sidebar, BorderLayout.WEST);
        add(mainContent, BorderLayout.CENTER);
    }

    private CustomButton createBtn(String text) {
        CustomButton btn = new CustomButton(text, new Color(52, 73, 94), Color.WHITE);
        btn.setPreferredSize(new Dimension(230, 45));
        return btn;
    }

    private JPanel createPage(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(text, SwingConstants.CENTER));
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConvenienceStoreView().setVisible(true));
    }
}