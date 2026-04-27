package giaodien_UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class QuanLyNhanVien_GUI extends JPanel implements ActionListener, MouseListener {

    // Components
    private JTextField txtMaNV, txtTenNV, txtDiaChi, txtSdt, txtNgayVaoLam, txtTimKiem;
    private JPasswordField txtMatKhau;
    private JComboBox<String> cbGioiTinh, cbVaiTro;
    private CustomButton btnThem, btnXoa, btnSua, btnXoaTrang, btnTimKiem;
    private JTable table;
    private DefaultTableModel tableModel;

    public QuanLyNhanVien_GUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 244, 248));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Tiêu đề
        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN VIÊN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(44, 62, 80));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // 2. Main Content (Top: Form + Buttons)
        JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
        pnlTop.setOpaque(false);

        // -- 2.1 Form Input bằng GridLayout đơn giản --
        JPanel pnlForm = new JPanel(new GridLayout(4, 4, 15, 15));
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 215, 230)),
                "Thông tin nhân viên", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(44, 62, 80)
            ),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Dòng 1
        pnlForm.add(new JLabel("Mã nhân viên:"));
        pnlForm.add(txtMaNV = new JTextField());
        pnlForm.add(new JLabel("Tên nhân viên:"));
        pnlForm.add(txtTenNV = new JTextField());

        // Dòng 2
        pnlForm.add(new JLabel("Giới tính:"));
        pnlForm.add(cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"}));
        cbGioiTinh.setBackground(Color.WHITE);
        pnlForm.add(new JLabel("Ngày vào làm:"));
        pnlForm.add(txtNgayVaoLam = new JTextField());

        // Dòng 3
        pnlForm.add(new JLabel("Số điện thoại:"));
        pnlForm.add(txtSdt = new JTextField());
        pnlForm.add(new JLabel("Vai trò:"));
        pnlForm.add(cbVaiTro = new JComboBox<>(new String[]{"Quản lý", "Nhân viên thu ngân"}));
        cbVaiTro.setBackground(Color.WHITE);

        // Dòng 4
        pnlForm.add(new JLabel("Mật khẩu:"));
        pnlForm.add(txtMatKhau = new JPasswordField());
        pnlForm.add(new JLabel("Địa chỉ:"));
        pnlForm.add(txtDiaChi = new JTextField());

        pnlTop.add(pnlForm, BorderLayout.CENTER);

        // -- 2.2 Buttons & Search --
        JPanel pnlAction = new JPanel(new BorderLayout(10, 10));
        pnlAction.setOpaque(false);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlButtons.setOpaque(false);
        
        btnThem = new CustomButton("Thêm", new Color(46, 204, 113), Color.WHITE);
        btnThem.setHorizontalAlignment(SwingConstants.CENTER);
        btnThem.setMargin(new Insets(0, 0, 0, 0));
        btnThem.setPreferredSize(new Dimension(100, 35));
        
        btnXoa = new CustomButton("Xóa", new Color(231, 76, 60), Color.WHITE);
        btnXoa.setHorizontalAlignment(SwingConstants.CENTER);
        btnXoa.setMargin(new Insets(0, 0, 0, 0));
        btnXoa.setPreferredSize(new Dimension(100, 35));
        
        btnSua = new CustomButton("Sửa", new Color(52, 152, 219), Color.WHITE);
        btnSua.setHorizontalAlignment(SwingConstants.CENTER);
        btnSua.setMargin(new Insets(0, 0, 0, 0));
        btnSua.setPreferredSize(new Dimension(100, 35));
        
        btnXoaTrang = new CustomButton("Xóa trắng", new Color(149, 165, 166), Color.WHITE);
        btnXoaTrang.setHorizontalAlignment(SwingConstants.CENTER);
        btnXoaTrang.setMargin(new Insets(0, 0, 0, 0));
        btnXoaTrang.setPreferredSize(new Dimension(100, 35));
        
        pnlButtons.add(btnThem);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnXoaTrang);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlSearch.setOpaque(false);
        JLabel lblSearch = new JLabel("Tìm theo mã:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlSearch.add(lblSearch);
        
        txtTimKiem = new JTextField(12);
        txtTimKiem.setPreferredSize(new Dimension(150, 35));
        pnlSearch.add(txtTimKiem);
        
        btnTimKiem = new CustomButton("Tìm kiếm", new Color(241, 196, 15), Color.WHITE);
        btnTimKiem.setHorizontalAlignment(SwingConstants.CENTER);
        btnTimKiem.setMargin(new Insets(0, 0, 0, 0));
        btnTimKiem.setPreferredSize(new Dimension(100, 35));
        pnlSearch.add(btnTimKiem);

        pnlAction.add(pnlButtons, BorderLayout.WEST);
        pnlAction.add(pnlSearch, BorderLayout.EAST);

        pnlTop.add(pnlAction, BorderLayout.SOUTH);

        // Wrap Top in another panel to add title
        JPanel pnlNorthWrap = new JPanel(new BorderLayout());
        pnlNorthWrap.setOpaque(false);
        pnlNorthWrap.add(lblTitle, BorderLayout.NORTH);
        pnlNorthWrap.add(pnlTop, BorderLayout.CENTER);

        add(pnlNorthWrap, BorderLayout.NORTH);

        // 3. Table
        String[] columns = {"Mã NV", "Tên NV", "Giới tính", "SĐT", "Ngày vào làm", "Vai trò", "Địa chỉ"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(200, 215, 230));
        table.getTableHeader().setOpaque(false);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 215, 230)),
            "Danh sách nhân viên", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14), new Color(44, 62, 80)
        ));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
        
        // Đăng ký sự kiện
        btnThem.addActionListener(this);
        btnXoa.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoaTrang.addActionListener(this);
        btnTimKiem.addActionListener(this);
        table.addMouseListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnThem)) {
            JOptionPane.showMessageDialog(this, "Chức năng Thêm đang được xây dựng!");
        } else if (o.equals(btnXoa)) {
            JOptionPane.showMessageDialog(this, "Chức năng Xóa đang được xây dựng!");
        } else if (o.equals(btnSua)) {
            JOptionPane.showMessageDialog(this, "Chức năng Sửa đang được xây dựng!");
        } else if (o.equals(btnXoaTrang)) {
            xoaTrang();
        } else if (o.equals(btnTimKiem)) {
            JOptionPane.showMessageDialog(this, "Chức năng Tìm kiếm đang được xây dựng!");
        }
    }

    private void xoaTrang() {
        txtMaNV.setText("");
        txtTenNV.setText("");
        txtDiaChi.setText("");
        txtSdt.setText("");
        txtNgayVaoLam.setText("");
        txtMatKhau.setText("");
        txtTimKiem.setText("");
        cbGioiTinh.setSelectedIndex(0);
        cbVaiTro.setSelectedIndex(0);
        txtMaNV.requestFocus();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtMaNV.setText(tableModel.getValueAt(row, 0).toString());
            txtTenNV.setText(tableModel.getValueAt(row, 1).toString());
            cbGioiTinh.setSelectedItem(tableModel.getValueAt(row, 2).toString());
            txtSdt.setText(tableModel.getValueAt(row, 3).toString());
            txtNgayVaoLam.setText(tableModel.getValueAt(row, 4).toString());
            cbVaiTro.setSelectedItem(tableModel.getValueAt(row, 5).toString());
            txtDiaChi.setText(tableModel.getValueAt(row, 6).toString());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
