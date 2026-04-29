package giaodien_UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

// Đảm bảo import đúng đường dẫn package Model và DAO của bạn
import dAO.KhachHang_DAO;
import model.KhachHang;

public class KhachHangPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // Khởi tạo đối tượng DAO để tương tác CSDL
    private KhachHang_DAO khDAO = new KhachHang_DAO();
    private ArrayList<KhachHang> dskh;
    
    // Khai báo các component UI
    private JTextField txtMaKH, txtTenKH, txtSoDienThoai, txtDiemTL, txtLoaiKH;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiem;

    public KhachHangPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Load danh sách khách hàng ban đầu
        dskh = khDAO.getAllKhachHang();
        
        createUI();
    }

    private void createUI() {
        // Tiêu đề
        JLabel titleLabel = new JLabel("Quản Lý Khách Hàng", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- PANEL NHẬP LIỆU ---
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 8, 90));
        inputPanel.setBackground(new Color(245, 245, 245));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Font labelFont = new Font("Arial", Font.PLAIN, 14);
        Font textFieldFont = new Font("Arial", Font.PLAIN, 14);

        // Mã Khách Hàng
        inputPanel.add(createLabel("Mã Khách Hàng:", labelFont));
        txtMaKH = createTextField(textFieldFont, false);
        inputPanel.add(txtMaKH);

        // Tên Khách Hàng
        inputPanel.add(createLabel("Tên Khách Hàng:", labelFont));
        txtTenKH = createTextField(textFieldFont, true);
        inputPanel.add(txtTenKH);

        // Số Điện Thoại
        inputPanel.add(createLabel("Số Điện Thoại:", labelFont));
        txtSoDienThoai = createTextField(textFieldFont, true);
        inputPanel.add(txtSoDienThoai);

        // Điểm Tích Lũy
        inputPanel.add(createLabel("Điểm Tích Lũy:", labelFont));
        txtDiemTL = createTextField(textFieldFont, true);
        inputPanel.add(txtDiemTL);

        // Loại Khách Hàng
        inputPanel.add(createLabel("Loại Khách Hàng:", labelFont));
        txtLoaiKH = createTextField(textFieldFont, false);
        inputPanel.add(txtLoaiKH);

        JPanel inputWrapperBorder = new JPanel(new BorderLayout());
        inputWrapperBorder.setBackground(new Color(245, 245, 245));
        inputWrapperBorder.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), "Thông Tin Khách Hàng", TitledBorder.LEFT,
                TitledBorder.TOP, new Font("Arial", Font.BOLD, 12), Color.DARK_GRAY));
        inputWrapperBorder.add(inputPanel, BorderLayout.CENTER);

        JPanel inputWrapper = new JPanel(new BorderLayout());
        inputWrapper.setBackground(new Color(245, 245, 245));
        inputWrapper.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        inputWrapper.add(inputWrapperBorder, BorderLayout.CENTER);

        // --- BẢNG HIỂN THỊ (JTABLE) ---
        String[] columnNames = {"Mã Khách Hàng", "Tên Khách Hàng", "Số Điện Thoại", "Điểm Tích Lũy", "Loại Khách Hàng"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setRowHeight(30);
        table.setGridColor(new Color(200, 200, 200));
        table.setShowGrid(true);
        
        // Căn giữa tiêu đề và nội dung cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getTableHeader().setDefaultRenderer(centerRenderer);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    txtMaKH.setText(table.getValueAt(selectedRow, 0).toString());
                    txtTenKH.setText(table.getValueAt(selectedRow, 1).toString());
                    txtSoDienThoai.setText(table.getValueAt(selectedRow, 2).toString());
                    txtDiemTL.setText(table.getValueAt(selectedRow, 3).toString());
                    txtLoaiKH.setText(table.getValueAt(selectedRow, 4).toString());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(new Color(245, 245, 245));
        tableWrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), "Danh Sách Khách Hàng", TitledBorder.LEFT,
                TitledBorder.TOP, new Font("Arial", Font.BOLD, 12), Color.DARK_GRAY));
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(245, 245, 245));
        centerPanel.add(inputWrapper, BorderLayout.EAST);
        centerPanel.add(tableWrapper, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // --- CÁC NÚT CHỨC NĂNG ---
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        leftPanel.setBackground(new Color(245, 245, 245));
        leftPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), "Chức Năng Chính", TitledBorder.LEFT,
                TitledBorder.TOP, new Font("Arial", Font.BOLD, 12), Color.DARK_GRAY));
        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        JButton btnThem = createButton("Thêm", buttonFont);
        btnThem.addActionListener(e -> xuLySuKienThemKhachHang());
        leftPanel.add(btnThem);

        JButton btnXoa = createButton("Xóa", buttonFont);
        btnXoa.addActionListener(e -> xuLySuKienXoaKhachHang());
        leftPanel.add(btnXoa);

        JButton btnSua = createButton("Sửa", buttonFont);
        btnSua.addActionListener(e -> xuLySuKienSuaKhachHang());
        leftPanel.add(btnSua);

        JButton btnXoaTrang = createButton("Xóa Trắng", buttonFont);
        btnXoaTrang.addActionListener(e -> xuLySuKienLamMoi());
        leftPanel.add(btnXoaTrang);

        // --- TÌM KIẾM ---
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        rightPanel.setBackground(new Color(245, 245, 245));
        rightPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), "Tìm Kiếm Khách Hàng", TitledBorder.LEFT,
                TitledBorder.TOP, new Font("Arial", Font.BOLD, 12), Color.DARK_GRAY));

        JLabel lblTimKiem = new JLabel("Số Điện Thoại:");
        lblTimKiem.setFont(new Font("Arial", Font.PLAIN, 13));
        txtTimKiem = new JTextField(15);
        txtTimKiem.setFont(new Font("Arial", Font.PLAIN, 13));
        txtTimKiem.getDocument().addDocumentListener(timKiemDong());
        rightPanel.add(lblTimKiem);
        rightPanel.add(txtTimKiem);

        JSplitPane buttonSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        buttonSplitPane.setDividerLocation(850);
        buttonSplitPane.setBorder(null);

        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        bottomWrapper.add(buttonSplitPane, BorderLayout.CENTER);
        add(bottomWrapper, BorderLayout.SOUTH);

        loadDataToTable();
    }

    // --- CÁC HÀM TIỆN ÍCH TẠO UI ---
    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    private JTextField createTextField(Font font, boolean isEnabled) {
        JTextField textField = new JTextField();
        textField.setFont(font);
        textField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        textField.setPreferredSize(new Dimension(120, 10));
        textField.setEnabled(isEnabled);
        return textField;
    }

    private JButton createButton(String text, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(new Color(220, 220, 220));
        button.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
        button.setPreferredSize(new Dimension(100, 40));
        return button;
    }

    // --- CÁC HÀM XỬ LÝ NGHIỆP VỤ ---
    private void loadDataToTable() {
        tableModel.setRowCount(0);
        for (KhachHang kh : dskh) {
            Object[] row = {
                kh.getMaKH(),
                kh.getTenKH(),
                kh.getSoDienThoai(),
                kh.getDiemTL(),
                kh.getLoaiKhachHang() != null ? kh.getLoaiKhachHang().getTenLKH() : "Thường"
            };
            tableModel.addRow(row);
        }
    }

    private void updateTable() {
        dskh = khDAO.getAllKhachHang();
        loadDataToTable();
    }

    private void xuLySuKienThemKhachHang() {
        try {
            String tenKH = txtTenKH.getText().trim();
            String soDienThoai = txtSoDienThoai.getText().trim();

            if (tenKH.isEmpty() || soDienThoai.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên và số điện thoại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int diemTL = 0;
            if (!txtDiemTL.getText().trim().isEmpty()) {
                try {
                    diemTL = Integer.parseInt(txtDiemTL.getText().trim());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Điểm tích lũy phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

       
            String maMoi = "KH001"; 
            if (dskh != null && !dskh.isEmpty()) {
             
                String maCuoiCung = dskh.get(dskh.size() - 1).getMaKH();
             
                int soCuoi = Integer.parseInt(maCuoiCung.substring(2));
              
                maMoi = String.format("KH%03d", soCuoi + 1);
            }
      

      
            KhachHang newKH = new KhachHang(maMoi, tenKH, soDienThoai, diemTL);
            
            newKH.tuDongPhanHang();
            
            boolean result = khDAO.themKhachHang(newKH);
            
            if (result) {
                updateTable();
                lamRong();
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!\nMã khách hàng mới: " + maMoi, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại! Số điện thoại có thể đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLySuKienXoaKhachHang() {
        String maKH = txtMaKH.getText().trim();
        if (maKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa khách hàng này không?", "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean daXoa = khDAO.xoaKhachHang(maKH);
            if (daXoa) {
                updateTable();
                lamRong();
                JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa khách hàng vì đã có hóa đơn liên quan!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLySuKienSuaKhachHang() {
        try {
            String maKH = txtMaKH.getText().trim();
            if (maKH.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String tenKH = txtTenKH.getText().trim();
            String soDienThoai = txtSoDienThoai.getText().trim();

            if (tenKH.isEmpty() || soDienThoai.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên và số điện thoại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int diemTL;
            try {
                diemTL = Integer.parseInt(txtDiemTL.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Điểm tích lũy phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn cập nhật thông tin khách hàng này không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            // Tìm lại khách hàng cũ trong danh sách để giữ nguyên Loại Khách Hàng
            KhachHang khachHangCu = null;
            for (KhachHang kh : dskh) {
                if (kh.getMaKH().equals(maKH)) {
                    khachHangCu = kh;
                    break;
                }
            }

            if (khachHangCu != null) {
                KhachHang newKH = new KhachHang(maKH, tenKH, soDienThoai, diemTL, khachHangCu.getLoaiKhachHang());
                
                
                newKH.tuDongPhanHang();
                boolean ok = khDAO.suaKhachHang(newKH);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công!");
                    updateTable();
                    lamRong();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thất bại!");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLySuKienLamMoi() {
        updateTable();
        txtTimKiem.setText("");
        lamRong();
    }

    private DocumentListener timKiemDong() {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { thucHienTimKiem(); }
            @Override
            public void removeUpdate(DocumentEvent e) { thucHienTimKiem(); }
            @Override
            public void changedUpdate(DocumentEvent e) { thucHienTimKiem(); }

            private void thucHienTimKiem() {
                String sdt = txtTimKiem.getText().trim();
                if (sdt.isEmpty()) {
                    dskh = khDAO.getAllKhachHang();
                } else {
                    dskh = khDAO.timDanhSachKhachHangTheoSDT(sdt);
                }
                loadDataToTable();
            }
        };
    }

    private void lamRong() {
        txtMaKH.setText("");
        txtTenKH.setText("");
        txtSoDienThoai.setText("");
        txtDiemTL.setText("");
        txtLoaiKH.setText("");
        table.clearSelection();
    }
}