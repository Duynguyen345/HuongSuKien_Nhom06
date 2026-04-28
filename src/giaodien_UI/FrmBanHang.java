package giaodien_UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

import dAO.HangHoa_DAO;
import dAO.KhachHang_DAO;
import dAO.HoaDon_DAO;
import model.*;

public class FrmBanHang extends JPanel {
    private JTextField txtMaVach, txtSDT;
    // ── Bảng danh sách sản phẩm (thêm mới, không ảnh hưởng code cũ) ──
    private JTable tblSanPham;
    private DefaultTableModel modelSanPham;
    private JTable tblGioHang;
    private DefaultTableModel modelGioHang;
    private JLabel lblTongTien, lblGiamGiaKH, lblTenKH, lblThanhTien;
    private CustomButton btnThanhToan, btnHuyDon, btnXoaDong;
    private JLabel lblIconBarcode, lblIconSearch;
    
    private HangHoa_DAO hhDAO  = new HangHoa_DAO();
    private KhachHang_DAO khDAO = new KhachHang_DAO();
    private HoaDon_DAO hdDAO   = new HoaDon_DAO();
    private KhachHang khachHangHienTai = null;
    /** Mã NV đăng nhập – thiết lập từ ConvenienceStoreView sau login */
    public String maNVHienTai = "NV001";

    public FrmBanHang() {
        setLayout(new BorderLayout(10, 10));
        initIconsAndButtons();

        // --- PHÍA BẮC (NORTH): Khu vực Quét mã vạch ---
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        pnlTop.setBackground(Color.WHITE);
        pnlTop.add(lblIconBarcode);
        pnlTop.add(new JLabel("MÃ VẠCH (F1):"));
        
        txtMaVach = new JTextField(30);
        txtMaVach.setFont(new Font("Consolas", Font.BOLD, 22)); // Font to rõ cho dễ nhìn
        txtMaVach.setBorder(BorderFactory.createLineBorder(new Color(0, 51, 102), 2));
        pnlTop.add(txtMaVach);
        add(pnlTop, BorderLayout.NORTH);

        // --- TRUNG TÂM (CENTER): Danh sách SP (trái) | Giỏ hàng (phải) ---
        String[] columns = {"STT", "Mã hàng", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"};
        modelGioHang = new DefaultTableModel(columns, 0);
        tblGioHang = new JTable(modelGioHang);
        tblGioHang.setRowHeight(40);
        tblGioHang.setFont(new Font("Arial", Font.PLAIN, 15));

        // Bảng danh sách hàng hóa từ DB
        String[] colsSP = {"Mã hàng", "Tên sản phẩm", "Đơn giá"};
        modelSanPham = new DefaultTableModel(colsSP, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSanPham = new JTable(modelSanPham);
        tblSanPham.setRowHeight(30);
        tblSanPham.setFont(new Font("Arial", Font.PLAIN, 13));
        tblSanPham.setSelectionBackground(new Color(52, 152, 219));
        tblSanPham.setSelectionForeground(Color.WHITE);
        tblSanPham.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblSanPham.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblSanPham.getColumnModel().getColumn(2).setPreferredWidth(80);
        // Double-click → thêm vào giỏ (gọi lại themVaoGioHang đã có sẵn)
        tblSanPham.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblSanPham.getSelectedRow();
                    if (row >= 0) {
                        String maVach = modelSanPham.getValueAt(row, 0).toString();
                        HangHoa sp = hhDAO.timTheoMaVach(maVach);
                        if (sp == null) {
                            // Tìm theo maHH thay vì maVach
                            sp = hhDAO.timTheoMaHH(maVach);
                        }
                        if (sp != null) { themVaoGioHang(sp); tinhTongTien(); }
                    }
                }
            }
        });

        JScrollPane scrollSP = new JScrollPane(tblSanPham);
        scrollSP.setBorder(BorderFactory.createTitledBorder("Hàng hóa (double-click để thêm)"));

        JSplitPane splitCenter = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            scrollSP,
            new JScrollPane(tblGioHang)
        );
        splitCenter.setDividerLocation(380);
        splitCenter.setBorder(null);
        add(splitCenter, BorderLayout.CENTER);
        taiDanhSachSanPham();

        // --- PHÍA ĐÔNG (EAST): Khu vực Thanh toán (Nới rộng lên 400px để không mất chữ) ---
        JPanel pnlRight = new JPanel();
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setPreferredSize(new Dimension(400, 0)); 
        pnlRight.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Nhóm: Thông tin khách hàng
        JPanel pnlKH = new JPanel(new GridLayout(4, 1, 5, 5));
        pnlKH.setBorder(BorderFactory.createTitledBorder("THÔNG TIN KHÁCH HÀNG"));
        txtSDT = new JTextField();
        txtSDT.setFont(new Font("Arial", Font.BOLD, 18));
        
        JPanel pnlSearch = new JPanel(new BorderLayout(10, 0));
        pnlSearch.add(lblIconSearch, BorderLayout.WEST);
        pnlSearch.add(txtSDT, BorderLayout.CENTER);
        
        pnlKH.add(new JLabel("Số điện thoại (Enter):"));
        pnlKH.add(pnlSearch);
        lblTenKH = new JLabel("Khách: Vãng lai");
        lblGiamGiaKH = new JLabel("Chiết khấu: 0%");
        pnlKH.add(lblTenKH);
        pnlKH.add(lblGiamGiaKH);
        pnlRight.add(pnlKH);

        pnlRight.add(Box.createVerticalStrut(30));

        // Nhóm: Thông tin Tiền bạc
        lblTongTien = new JLabel("Tổng tạm tính: 0 VNĐ");
        lblTongTien.setFont(new Font("Arial", Font.PLAIN, 16));
        lblThanhTien = new JLabel("THÀNH TIỀN: 0 VNĐ");
        lblThanhTien.setForeground(new Color(204, 0, 0));
        lblThanhTien.setFont(new Font("Arial", Font.BOLD, 24));
        
        pnlRight.add(lblTongTien);
        pnlRight.add(Box.createVerticalStrut(10));
        pnlRight.add(lblThanhTien);
        pnlRight.add(Box.createVerticalGlue());

        // Nhóm: Các nút thao tác
        Dimension btnSize = new Dimension(360, 60); 
        
        // Đảm bảo các nút có không gian hiển thị tối đa để chữ không bị cắt khuất
        btnXoaDong.setPreferredSize(btnSize);
        btnXoaDong.setMaximumSize(new Dimension(Short.MAX_VALUE, btnSize.height));
        btnHuyDon.setPreferredSize(btnSize);
        btnHuyDon.setMaximumSize(new Dimension(Short.MAX_VALUE, btnSize.height));
        btnThanhToan.setPreferredSize(new Dimension(360, 80));
        btnThanhToan.setMaximumSize(new Dimension(Short.MAX_VALUE, 80));
        
        // Tinh chỉnh lại lề (margin) để chữ và icon thoáng hơn
        Insets btnInsets = new Insets(8, 14, 8, 14);
        btnXoaDong.setMargin(btnInsets);
        btnHuyDon.setMargin(btnInsets);
        btnThanhToan.setMargin(btnInsets);
        
        btnXoaDong.setIconTextGap(12);
        btnHuyDon.setIconTextGap(12);
        btnThanhToan.setIconTextGap(12);
        
        btnXoaDong.setHorizontalAlignment(SwingConstants.LEFT);
        btnHuyDon.setHorizontalAlignment(SwingConstants.LEFT);
        btnThanhToan.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Tăng kích thước font chữ để dễ thao tác trên màn hình bán hàng
        btnXoaDong.setFont(btnXoaDong.getFont().deriveFont(Font.BOLD, 16f));
        btnHuyDon.setFont(btnHuyDon.getFont().deriveFont(Font.BOLD, 16f));
        btnThanhToan.setFont(btnThanhToan.getFont().deriveFont(Font.BOLD, 18f));

        pnlRight.add(btnXoaDong);
        pnlRight.add(Box.createVerticalStrut(10));
        pnlRight.add(btnHuyDon);
        pnlRight.add(Box.createVerticalStrut(10));
        pnlRight.add(btnThanhToan);

        add(pnlRight, BorderLayout.EAST);

        xuLySuKien();
        setupHotkeys();
    }

    private void initIconsAndButtons() {
        // Khởi tạo các nút bấm
        btnThanhToan = new CustomButton("THANH TOÁN (F12)", new Color(255, 140, 0), Color.WHITE);
        btnHuyDon     = new CustomButton("Hủy đơn hàng", new Color(153, 0, 0), Color.WHITE);
        btnXoaDong    = new CustomButton("Xóa dòng chọn", new Color(0, 51, 102), Color.WHITE);
        
        // Hàm hỗ trợ: Tải và tự động thay đổi kích thước ảnh (Scale Icon)
        java.util.function.BiFunction<String, Integer, ImageIcon> loadScaled = (path, size) -> {
            try {
                // Thử tải dữ liệu từ classpath (áp dụng khi đã build ra file .jar)
                java.net.URL u = getClass().getResource(path);
                Image img = null;
                if (u != null) img = new ImageIcon(u).getImage();
                else {
                    // Dự phòng: Thử tải từ các thư mục file thông thường (áp dụng khi chạy trên IDE)
                    java.io.File f1 = new java.io.File("src/resources" + path.substring(path.lastIndexOf('/')));
                    java.io.File f2 = new java.io.File("resources" + path.substring(path.lastIndexOf('/')));
                    if (f1.exists()) img = new ImageIcon(f1.getAbsolutePath()).getImage();
                    else if (f2.exists()) img = new ImageIcon(f2.getAbsolutePath()).getImage();
                }
                if (img != null) {
                    Image scaled = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaled);
                }
            } catch (Exception ex) {
                System.err.println("Lỗi tải Icon tại: " + path + " -> " + ex.getMessage());
            }
            return null;
        };

        // Kích thước mong muốn: Icon trong nút nhỏ gọn, Icon trên nhãn lớn hơn
        ImageIcon icoPay = loadScaled.apply("/resources/payment-method.png", 28);
        ImageIcon icoCancel = loadScaled.apply("/resources/cancel.png", 24);
        ImageIcon icoDelete = loadScaled.apply("/resources/delete.png", 24);
        ImageIcon icoBarcode = loadScaled.apply("/resources/barcode.png", 36);
        ImageIcon icoSearch = loadScaled.apply("/resources/search.png", 20);

        if (icoPay != null) btnThanhToan.setIcon(icoPay); else System.err.println("Không tìm thấy payment-method.png");
        if (icoCancel != null) btnHuyDon.setIcon(icoCancel); else System.err.println("Không tìm thấy cancel.png");
        if (icoDelete != null) btnXoaDong.setIcon(icoDelete); else System.err.println("Không tìm thấy delete.png");
        lblIconBarcode = (icoBarcode != null) ? new JLabel(icoBarcode) : new JLabel();
        lblIconSearch  = (icoSearch != null) ? new JLabel(icoSearch) : new JLabel();

        // Đảm bảo phần văn bản (text) không bị đè lên bởi icon và được căn lề chuẩn
        btnThanhToan.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnHuyDon.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnXoaDong.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnThanhToan.setIconTextGap(10);
        btnHuyDon.setIconTextGap(8);
        btnXoaDong.setIconTextGap(8);
    }

    private void xuLySuKien() {
        txtMaVach.addActionListener(e -> {
            String barcode = txtMaVach.getText().trim();
            // TEST: Thử nghiệm quét mã vạch (ví dụ: 8934567890123)
            HangHoa sp = hhDAO.timTheoMaVach(barcode);
            if (sp != null) {
                themVaoGioHang(sp);
                tinhTongTien();
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy mã sản phẩm: " + barcode);
            }
            txtMaVach.setText("");
            txtMaVach.requestFocus();
        });

        txtSDT.addActionListener(e -> {
            String sdt = txtSDT.getText().trim();
            khachHangHienTai = khDAO.timKhachHangTheoSDT(sdt);
            if (khachHangHienTai != null) {
                lblTenKH.setText("Khách: " + khachHangHienTai.getTenKH());
                lblGiamGiaKH.setText("Chiết khấu: " + khachHangHienTai.getLoaiKH().getGiamGia() + "%");
            } else {
                lblTenKH.setText("Khách: Vãng lai");
                lblGiamGiaKH.setText("Chiết khấu: 0%");
            }
            tinhTongTien();
        });

        btnXoaDong.addActionListener(e -> {
            int row = tblGioHang.getSelectedRow();
            if (row != -1) {
                modelGioHang.removeRow(row);
                capNhatSTT();
                tinhTongTien();
            }
        });

        btnThanhToan.addActionListener(e -> {
            if (modelGioHang.getRowCount() > 0) {
                double tongGoc     = 0;
                for (int i = 0; i < modelGioHang.getRowCount(); i++)
                    tongGoc += ((Number) modelGioHang.getValueAt(i, 5)).doubleValue();
                double tienGiam    = tinhTienGiam();
                double tongThanhToan = tinhThanhTienSauGiam();

                String maHD = hdDAO.sinhMaHD();
                Window parentWindow = SwingUtilities.getWindowAncestor(this);
                ThanhToanPanel dialog = new ThanhToanPanel((Frame) parentWindow, tongThanhToan, maHD);
                dialog.setVisible(true);

                if (dialog.isThanhToanThanhCong()) {
                    // Xây dựng danh sách chi tiết
                    java.util.List<Object[]> chiTiet = new java.util.ArrayList<>();
                    for (int i = 0; i < modelGioHang.getRowCount(); i++) {
                        chiTiet.add(new Object[]{
                            modelGioHang.getValueAt(i, 1),  // maHH
                            modelGioHang.getValueAt(i, 3),  // soLuong
                            modelGioHang.getValueAt(i, 4),  // donGia
                            modelGioHang.getValueAt(i, 5)   // thanhTien
                        });
                    }
                    String maKH = (khachHangHienTai != null) ? khachHangHienTai.getMaKH() : null;
                    try {
                        hdDAO.luuHoaDon(maHD, maNVHienTai, maKH,
                            dialog.getHinhThuc(),
                            tongGoc, tienGiam, tongThanhToan, chiTiet);
                        JOptionPane.showMessageDialog(this,
                            "Hóa đơn " + maHD + " đã được lưu vào hệ thống!");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this,
                            "Lỗi lưu hóa đơn:\n" + ex.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                    modelGioHang.setRowCount(0);
                    txtSDT.setText("");
                    khachHangHienTai = null;
                    lblTenKH.setText("Khách: Vãng lai");
                    lblGiamGiaKH.setText("Chiết khấu: 0%");
                    tinhTongTien();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Giỏ hàng trống!");
            }
        });
    }

    private void setupHotkeys() {
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "focus");
        this.getActionMap().put("focus", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { txtMaVach.requestFocus(); }
        });
    }

    private void themVaoGioHang(HangHoa sp) {
        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            if (modelGioHang.getValueAt(i, 1).equals(sp.getMaHH())) {
                int sl = (int) modelGioHang.getValueAt(i, 3) + 1;
                modelGioHang.setValueAt(sl, i, 3);
                modelGioHang.setValueAt(sl * sp.getGiaSP(), i, 5);
                return;
            }
        }
        modelGioHang.addRow(new Object[]{modelGioHang.getRowCount() + 1, sp.getMaHH(), sp.getTenHH(), 1, sp.getGiaSP(), sp.getGiaSP()});
    }

    private void tinhTongTien() {
        double tamTinh = 0;
        for (int i = 0; i < modelGioHang.getRowCount(); i++) tamTinh += (double) modelGioHang.getValueAt(i, 5);
        double giam = (khachHangHienTai != null) ? tamTinh * (khachHangHienTai.getLoaiKH().getGiamGia() / 100.0) : 0;
        lblTongTien.setText("Tổng tạm tính: " + String.format("%,.0f", tamTinh) + " VNĐ");
        lblThanhTien.setText("THÀNH TIỀN: " + String.format("%,.0f", (tamTinh - giam)) + " VNĐ");
    }

    private void capNhatSTT() {
        for (int i = 0; i < modelGioHang.getRowCount(); i++) modelGioHang.setValueAt(i + 1, i, 0);
    }

    private void xuLyThanhToan() {
        JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
        modelGioHang.setRowCount(0);
        txtSDT.setText("");
        khachHangHienTai = null;
        tinhTongTien();
    }
    
    public double tinhThanhTienSauGiam() {
        double tamTinh = 0;
        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            Object v = modelGioHang.getValueAt(i, 5);
            if (v instanceof Number) tamTinh += ((Number) v).doubleValue();
            else {
                try { tamTinh += Double.parseDouble(v.toString()); } catch (Exception ex) { /* bỏ qua ngoại lệ nếu sai định dạng */ }
            }
        }
        double giamGia = (khachHangHienTai != null) ? tamTinh * (khachHangHienTai.getLoaiKH().getGiamGia() / 100.0) : 0;
        return tamTinh - giamGia;
    }
    
    // Phương thức hỗ trợ trả về riêng số tiền được giảm giá
    public double tinhTienGiam() {
        double tamTinh = 0;
        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            Object v = modelGioHang.getValueAt(i, 5);
            if (v instanceof Number) tamTinh += ((Number) v).doubleValue();
            else {
                try { tamTinh += Double.parseDouble(v.toString()); } catch (Exception ex) { /* bỏ qua ngoại lệ nếu sai định dạng */ }
            }
        }
        return (khachHangHienTai != null) ? tamTinh * (khachHangHienTai.getLoaiKH().getGiamGia() / 100.0) : 0;
    }

    // ── THÊM MỚI: Tải danh sách hàng hóa từ DB vào bảng bên trái ──
    private void taiDanhSachSanPham() {
        modelSanPham.setRowCount(0);
        java.util.List<HangHoa> list = hhDAO.layTatCa();
        for (HangHoa h : list) {
            modelSanPham.addRow(new Object[]{
                h.getMaHH(),
                h.getTenHH(),
                String.format("%,.0f đ", h.getGiaSP())
            });
        }
    }
}