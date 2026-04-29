package giaodien_UI;

import dAO.NHANVIEN_DAO;
import model.NhanVien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import dAO.HangHoa_DAO;
import dAO.KhachHang_DAO;
import model.*;

public class ConvenienceStoreView extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContent;
    private Login loginPanel;
    private CustomButton btnBanHang, btnHoaDon, btnSanPham, btnLoHang,
                         btnKhachHang, btnThongKe, btnNhanVien;
    private CustomButton[] menuButtons;

    /** Thông tin nhân viên đang đăng nhập */
    private NhanVien currentUser = null;

    public ConvenienceStoreView() {
        setTitle("Hệ thống Cửa hàng Tiện lợi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 360);
        setLocationRelativeTo(null);

        loginPanel = new Login();
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(52, 73, 94));
        wrapper.add(loginPanel);
        setContentPane(wrapper);

        // ── Sự kiện đăng nhập – xác thực qua SQL Server ──
        loginPanel.getBtnLogin().addActionListener(e -> handleLogin());
    }

    /** Xử lý đăng nhập: xác thực với DB, nếu đúng mới vào Dashboard. */
    private void handleLogin() {
        String maNV    = loginPanel.getMaNV();
        String matKhau = loginPanel.getMatKhau();

        if (maNV.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập đầy đủ Mã nhân viên và Mật khẩu.",
                "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            NHANVIEN_DAO dao = new NHANVIEN_DAO();
            currentUser = dao.dangNhap(maNV, matKhau);

            if (currentUser != null) {
                buildMainDashboard();
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                revalidate();
                repaint();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Mã nhân viên hoặc mật khẩu không đúng.\nVui lòng thử lại.",
                    "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Lỗi kết nối cơ sở dữ liệu:\n" + ex.getMessage(),
                "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // ------------------------------------------------------------------ //
    //  DASHBOARD CHÍNH
    // ------------------------------------------------------------------ //
    private void buildMainDashboard() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        add(buildSidebar(),  BorderLayout.WEST);
        add(buildHeader(),   BorderLayout.NORTH);
        add(buildContent(),  BorderLayout.CENTER);
        setupMenuEvents();
        // Mặc định hiển thị trang đầu tiên
        showPage(btnBanHang, "BAN_HANG", "Bán hàng");
    }

    // ---------- SIDEBAR ----------
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(33, 47, 61));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(20, 12, 20, 12));

        // Tiêu đề hệ thống
        JLabel title = new JLabel("Cửa Hàng TL");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(title);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));

        // Đường kẻ
        sidebar.add(makeSeparator());
        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));

        // Các nút menu
        Color btnColor = new Color(44, 62, 80);
        btnBanHang   = makeMenuBtn("Bán hàng",    btnColor);
        btnHoaDon    = makeMenuBtn("Hóa đơn",     btnColor);
        btnSanPham   = makeMenuBtn("Sản phẩm",    btnColor);
        btnLoHang    = makeMenuBtn("Lô hàng",     btnColor);
        btnKhachHang = makeMenuBtn("Khách hàng",  btnColor);
        btnThongKe   = makeMenuBtn("Thống kê",    btnColor);
        btnNhanVien  = makeMenuBtn("Nhân viên",   btnColor);

        menuButtons = new CustomButton[]{
            btnBanHang, btnHoaDon, btnSanPham, btnLoHang,
            btnKhachHang, btnThongKe, btnNhanVien
        };
        for (CustomButton btn : menuButtons) {
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
        }

        // Đẩy thông tin user xuống dưới
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(makeSeparator());
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        String displayName = (currentUser != null)
                ? "👤  " + currentUser.getTenNV() + "  |  " + currentUser.getVaiTro()
                : "👤  Admin  |  Nhân viên";
        JLabel user = new JLabel(displayName);
        user.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        user.setForeground(new Color(150, 180, 200));
        user.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(user);

        return sidebar;
    }

    // ---------- HEADER ----------
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(245, 248, 250));
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 215, 230)),
                new EmptyBorder(0, 20, 0, 20)
        ));

        // --- Thông tin người dùng (bên trái) ---
        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        userInfo.setOpaque(false);

        // Khung avatar trống (để tự thêm ảnh sau)
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(200, 215, 230));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(150, 175, 200));
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(1, 1, getWidth() - 2, getHeight() - 2);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(40, 40));

        // Tên và vai trò – lấy từ currentUser sau khi đăng nhập thành công
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);

        String tenHV  = (currentUser != null) ? currentUser.getTenNV()   : "Admin";
        String chucVu = (currentUser != null) ? currentUser.getVaiTro() : "Nhân viên";

        JLabel lblName = new JLabel(tenHV);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(new Color(33, 47, 61));

        JLabel lblRole = new JLabel(chucVu);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(new Color(120, 140, 160));

        namePanel.add(lblName);
        namePanel.add(lblRole);

        userInfo.add(avatar);
        userInfo.add(namePanel);

        // --- Đồng hồ (bên phải) ---
        JLabel clock = new JLabel();
        clock.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        clock.setForeground(new Color(120, 140, 160));

        Timer timer = new Timer(1000, e -> {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            clock.setText(String.format("%02d:%02d:%02d   %02d/%02d/%04d",
                    now.getHour(), now.getMinute(), now.getSecond(),
                    now.getDayOfMonth(), now.getMonthValue(), now.getYear()));
        });
        timer.setInitialDelay(0);
        timer.start();

        header.add(userInfo, BorderLayout.WEST);
        header.add(clock,    BorderLayout.EAST);
        return header;
    }

    // ---------- CONTENT ----------
    private JPanel buildContent() {
         cardLayout  = new CardLayout();
         mainContent = new JPanel(cardLayout);
         mainContent.setBackground(new Color(240, 244, 248));

        
         FrmBanHang banHangPanel = new FrmBanHang();
         if (currentUser != null) banHangPanel.maNVHienTai = currentUser.getMaNV();
         mainContent.add(banHangPanel, "BAN_HANG");
         mainContent.add(new HoaDonPanel(), "HOA_DON");
      
         mainContent.add(new HangHoaPanel(), "SAN_PHAM");
         mainContent.add(makePage("Quản lý Lô hàng"),   "LO_HANG");
         mainContent.add(new KhachHangPanel(),"KHACH_HANG");
         
         mainContent.add(makePage("Báo cáo Thống kê"),  "THONG_KE");
         mainContent.add(new QuanLyNhanVienPanel(),      "NHAN_VIEN");

         return mainContent;
     }

    // ---------- EVENTS ----------
    private void setupMenuEvents() {
        String[][] map = {
            {"BAN_HANG",  "Bán hàng"},    {"HOA_DON",   "Hóa đơn"},
            {"SAN_PHAM",  "Sản phẩm"},    {"LO_HANG",   "Lô hàng"},
            {"KHACH_HANG","Khách hàng"},  {"THONG_KE",  "Thống kê"},
            {"NHAN_VIEN", "Nhân viên"},
        };
        for (int i = 0; i < menuButtons.length; i++) {
            final int idx = i;
            menuButtons[i].addActionListener(e ->
                showPage(menuButtons[idx], map[idx][0], map[idx][1])
            );
        }
    }

    private void showPage(CustomButton active, String key, String title) {
        for (CustomButton b : menuButtons) b.setActive(false);
        active.setActive(true);
        cardLayout.show(mainContent, key);
    }

    // ---------- HELPERS ----------
    /** Tạo nút menu – chiều rộng tối đa để lấp đầy sidebar */
    private CustomButton makeMenuBtn(String text, Color bg) {
        CustomButton btn = new CustomButton(text, bg, Color.WHITE);
        // Integer.MAX_VALUE cho phép BoxLayout kéo dãn nút đến hết chiều rộng
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setPreferredSize(new Dimension(196, 42));
        return btn;
    }

    /** Đường kẻ ngang trong sidebar */
    private JSeparator makeSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 40));
        sep.setBackground(new Color(255, 255, 255, 40));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sep;
    }

    /** Trang nội dung placeholder đơn giản */
    private JPanel makePage(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(240, 244, 248));
        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lbl.setForeground(new Color(44, 62, 80));
        p.add(lbl);
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConvenienceStoreView().setVisible(true));
    }
}
