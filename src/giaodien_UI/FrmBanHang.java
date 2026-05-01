package giaodien_UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

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

        // Bảng danh sách hàng hóa từ DB (ĐÃ CẬP NHẬT CỘT HÌNH ẢNH)
        String[] colsSP = {"Hình ảnh", "Mã vạch", "Tên sản phẩm", "Đơn giá"};
        modelSanPham = new DefaultTableModel(colsSP, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSanPham = new JTable(modelSanPham);
        tblSanPham.setRowHeight(80); // Chỉnh cao lên để hiển thị ảnh
        tblSanPham.setFont(new Font("Arial", Font.PLAIN, 13));
        tblSanPham.setSelectionBackground(new Color(52, 152, 219));
        tblSanPham.setSelectionForeground(Color.WHITE);
        
        tblSanPham.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblSanPham.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblSanPham.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblSanPham.getColumnModel().getColumn(3).setPreferredWidth(80);
        
        // Render hình ảnh cho cột 0
        tblSanPham.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());

        // Double-click → thêm vào giỏ (gọi lại themVaoGioHang đã có sẵn)
        tblSanPham.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblSanPham.getSelectedRow();
                    if (row >= 0) {
                        // Cột 1 bây giờ là Mã vạch
                        String maVach = modelSanPham.getValueAt(row, 1).toString();
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
        btnHuyDon     = new CustomButton("Hủy đơn hàng (ESC)", new Color(153, 0, 0), Color.WHITE);
        btnXoaDong    = new CustomButton("Xóa dòng chọn (DEL)", new Color(0, 51, 102), Color.WHITE);
        
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
    	// 1. Theo dõi mọi thay đổi trong ô text (xem app điện thoại có bắn chữ vào không)
        txtMaVach.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                System.out.println("DEBUG - Đang nhận dữ liệu: " + txtMaVach.getText());
            }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) {}
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) {}
        });

        // 2. Bắt sự kiện lệnh ENTER từ app điện thoại gửi tới
        txtMaVach.addActionListener(e -> {
            String barcode = txtMaVach.getText().trim();
            System.out.println("========== BẮT ĐẦU QUÉT ==========");
            System.out.println("Mã chốt nhận được: [" + barcode + "]");
            
            if (!barcode.isEmpty()) {
                HangHoa sp = hhDAO.timTheoMaVach(barcode);
                if (sp != null) {
                    System.out.println("-> BINGO! Đã tìm thấy: " + sp.getTenHH());
                    themVaoGioHang(sp);
                    tinhTongTien();
                } else {
                    // Nếu không tìm thấy bằng mã vạch, thử tìm bằng Mã HH (backup)
                    sp = hhDAO.timTheoMaHH(barcode);
                    if (sp != null) {
                        System.out.println("-> BINGO! Đã tìm thấy theo Mã HH: " + sp.getTenHH());
                        themVaoGioHang(sp);
                        tinhTongTien();
                    } else {
                        System.out.println("-> LỖI: DB không có mã này!");
                        JOptionPane.showMessageDialog(this, 
                            "Không tìm thấy mã sản phẩm: " + barcode, 
                            "Lỗi quét mã", JOptionPane.ERROR_MESSAGE);
                    }
                }
                // Xóa trắng ô nhập và focus lại để sẵn sàng quét món tiếp theo
                txtMaVach.setText("");
                txtMaVach.requestFocus();
            }
        });

        txtSDT.addActionListener(e -> {
            String sdt = txtSDT.getText().trim();
            khachHangHienTai = khDAO.timKhachHangTheoSDT(sdt);
            if (khachHangHienTai != null) {
                lblTenKH.setText("Khách: " + khachHangHienTai.getTenKH());
                lblGiamGiaKH.setText("Chiết khấu: " + khachHangHienTai.getLoaiKhachHang().getGiamGia() + "%");
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
        
        btnHuyDon.addActionListener(e -> {
            if(modelGioHang.getRowCount() > 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn hủy đơn hàng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if(confirm == JOptionPane.YES_OPTION) {
                    modelGioHang.setRowCount(0);
                    txtSDT.setText("");
                    khachHangHienTai = null;
                    lblTenKH.setText("Khách: Vãng lai");
                    lblGiamGiaKH.setText("Chiết khấu: 0%");
                    tinhTongTien();
                    txtMaVach.requestFocus();
                }
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
                    txtMaVach.requestFocus();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Giỏ hàng trống!");
            }
        });
    }

    private void setupHotkeys() {
        // F1 -> Nhập mã vạch
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "focus");
        this.getActionMap().put("focus", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { txtMaVach.requestFocus(); }
        });
        
        // F12 -> Thanh toán
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "pay");
        this.getActionMap().put("pay", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { btnThanhToan.doClick(); }
        });
        
        // DELETE -> Xóa dòng
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteRow");
        this.getActionMap().put("deleteRow", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { btnXoaDong.doClick(); }
        });
        
        // ESCAPE -> Hủy đơn
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
        this.getActionMap().put("cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { btnHuyDon.doClick(); }
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
        double giam = (khachHangHienTai != null) ? tamTinh * (khachHangHienTai.getLoaiKhachHang().getGiamGia() / 100.0) : 0;
        lblTongTien.setText("Tổng tạm tính: " + String.format("%,.0f", tamTinh) + " VNĐ");
        lblThanhTien.setText("THÀNH TIỀN: " + String.format("%,.0f", (tamTinh - giam)) + " VNĐ");
    }

    private void capNhatSTT() {
        for (int i = 0; i < modelGioHang.getRowCount(); i++) modelGioHang.setValueAt(i + 1, i, 0);
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
        double giamGia = (khachHangHienTai != null) ? tamTinh * (khachHangHienTai.getLoaiKhachHang().getGiamGia() / 100.0) : 0;
        return tamTinh - giamGia;
    }
    
    public double tinhTienGiam() {
        double tamTinh = 0;
        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            Object v = modelGioHang.getValueAt(i, 5);
            if (v instanceof Number) tamTinh += ((Number) v).doubleValue();
            else {
                try { tamTinh += Double.parseDouble(v.toString()); } catch (Exception ex) { /* bỏ qua ngoại lệ nếu sai định dạng */ }
            }
        }
        return (khachHangHienTai != null) ? tamTinh * (khachHangHienTai.getLoaiKhachHang().getGiamGia() / 100.0) : 0;
    }

    // ── Tải danh sách hàng hóa từ DB và hiển thị kèm hình ảnh ──
    private void taiDanhSachSanPham() {
        modelSanPham.setRowCount(0);
        java.util.List<HangHoa> list = hhDAO.layTatCa();
        for (HangHoa h : list) {
            // Load hình ảnh thu nhỏ
            ImageIcon imageIcon = createImageIcon(h.getHinhAnh(), 60, 60);
            modelSanPham.addRow(new Object[]{
                imageIcon,
                h.getMaVach(),
                h.getTenHH(),
                String.format("%,.0f đ", h.getGiaSP())
            });
        }
    }
    
    // ── Hàm tiện ích hỗ trợ load ảnh ──
    private ImageIcon createImageIcon(String fileName, int width, int height) {
        try {
            if (fileName != null && !fileName.trim().isEmpty()) {
                URL imgURL = getClass().getResource("/Resource/HangHoa/" + fileName);
                if (imgURL != null) {
                    ImageIcon icon = new ImageIcon(imgURL);
                    Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaledImage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ── Lớp hỗ trợ Render Hình ảnh trong bảng ──
    class ImageRenderer extends JLabel implements TableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setIcon((value instanceof ImageIcon) ? (ImageIcon) value : null);
            setText((value instanceof ImageIcon) ? "" : "No Image");
            setHorizontalAlignment(JLabel.CENTER);
            if (isSelected) { 
                setBackground(table.getSelectionBackground()); 
                setOpaque(true); 
            } else {
                setOpaque(false);
            }
            return this;
        }
    }
}