package giaodien_UI;

import dAO.NHANVIEN_DAO;
import model.NhanVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class QuanLyNhanVienPanel extends JPanel implements ActionListener, MouseListener {

    // ── DAO & định dạng ngày ────────────────────────────────────
    private final NHANVIEN_DAO dao = new NHANVIEN_DAO();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ── Components ───────────────────────────────────────────────
    private JTextField     txtMaNV, txtTenNV, txtDiaChi, txtSdt, txtNgayVaoLam, txtTimKiem;
    private JPasswordField txtMatKhau;
    private JComboBox<String> cbGioiTinh, cbVaiTro;
    private CustomButton   btnThem, btnXoa, btnSua, btnXoaTrang, btnTimKiem;
    private JTable         table;
    private DefaultTableModel tableModel;

    public QuanLyNhanVienPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 244, 248));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ─── Tiêu đề ──────────────────────────────────────────
        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN VIÊN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(44, 62, 80));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // ─── Form nhập liệu ────────────────────────────────────
        JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
        pnlTop.setOpaque(false);

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

        // Dòng 1: Mã NV | Tên NV
        pnlForm.add(lbl("Mã nhân viên:"));
        pnlForm.add(txtMaNV = new JTextField());
        pnlForm.add(lbl("Tên nhân viên:"));
        pnlForm.add(txtTenNV = new JTextField());

        // Dòng 2: Giới tính | Ngày vào làm
        pnlForm.add(lbl("Giới tính:"));
        pnlForm.add(cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"}));
        cbGioiTinh.setBackground(Color.WHITE);
        pnlForm.add(lbl("Ngày vào làm (yyyy-MM-dd):"));
        pnlForm.add(txtNgayVaoLam = new JTextField());

        // Dòng 3: SĐT | Vai trò
        pnlForm.add(lbl("Số điện thoại:"));
        pnlForm.add(txtSdt = new JTextField());
        pnlForm.add(lbl("Vai trò:"));
        pnlForm.add(cbVaiTro = new JComboBox<>(new String[]{"Quản lý", "Nhân viên thu ngân"}));
        cbVaiTro.setBackground(Color.WHITE);

        // Dòng 4: Mật khẩu | Địa chỉ
        pnlForm.add(lbl("Mật khẩu:"));
        pnlForm.add(txtMatKhau = new JPasswordField());
        pnlForm.add(lbl("Địa chỉ:"));
        pnlForm.add(txtDiaChi = new JTextField());

        pnlTop.add(pnlForm, BorderLayout.CENTER);

        // ─── Thanh nút + Tìm kiếm ─────────────────────────────
        JPanel pnlAction = new JPanel(new BorderLayout(10, 10));
        pnlAction.setOpaque(false);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlButtons.setOpaque(false);
        btnThem     = makeBtn("Thêm",      new Color(46, 204, 113));
        btnXoa      = makeBtn("Xóa",       new Color(231, 76, 60));
        btnSua      = makeBtn("Sửa",       new Color(52, 152, 219));
        btnXoaTrang = makeBtn("Xóa trắng", new Color(149, 165, 166));
        pnlButtons.add(btnThem);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnXoaTrang);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlSearch.setOpaque(false);
        JLabel lblSearch = new JLabel("Tìm theo mã / tên:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlSearch.add(lblSearch);
        txtTimKiem = new JTextField(12);
        txtTimKiem.setPreferredSize(new Dimension(150, 35));
        pnlSearch.add(txtTimKiem);
        btnTimKiem = makeBtn("Tìm kiếm", new Color(241, 196, 15));
        pnlSearch.add(btnTimKiem);

        pnlAction.add(pnlButtons, BorderLayout.WEST);
        pnlAction.add(pnlSearch,  BorderLayout.EAST);
        pnlTop.add(pnlAction, BorderLayout.SOUTH);

        JPanel pnlNorthWrap = new JPanel(new BorderLayout());
        pnlNorthWrap.setOpaque(false);
        pnlNorthWrap.add(lblTitle, BorderLayout.NORTH);
        pnlNorthWrap.add(pnlTop,   BorderLayout.CENTER);
        add(pnlNorthWrap, BorderLayout.NORTH);

        // ─── Bảng danh sách ───────────────────────────────────
        String[] cols = {"Mã NV", "Tên NV", "Giới tính", "SĐT", "Ngày vào làm", "Vai trò", "Địa chỉ"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

        // ─── Đăng ký sự kiện ──────────────────────────────────
        btnThem.addActionListener(this);
        btnXoa.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoaTrang.addActionListener(this);
        btnTimKiem.addActionListener(this);
        table.addMouseListener(this);

        // ─── Tải dữ liệu ban đầu ──────────────────────────────
        taiDanhSach(null);
    }

    // ════════════════════════════════════════════════════════════
    //  XỬ LÝ SỰ KIỆN BUTTON
    // ════════════════════════════════════════════════════════════

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if      (src == btnThem)     them();
        else if (src == btnXoa)      xoa();
        else if (src == btnSua)      sua();
        else if (src == btnXoaTrang) xoaTrang();
        else if (src == btnTimKiem)  timKiem();
    }

    // ── THÊM ────────────────────────────────────────────────────
    private void them() {
        NhanVien nv = docForm();
        if (nv == null) return;

        String mk = new String(txtMatKhau.getPassword()).trim();
        if (mk.isEmpty()) { showErr("Mật khẩu không được để trống khi thêm mới!"); return; }

        try {
            if (dao.tonTai(nv.getMaNV())) {
                showErr("Mã nhân viên \"" + nv.getMaNV() + "\" đã tồn tại!");
                return;
            }
            if (dao.them(nv)) {
                showInfo("Thêm nhân viên thành công!");
                xoaTrang();
                taiDanhSach(null);
            } else {
                showErr("Thêm thất bại, vui lòng thử lại.");
            }
        } catch (SQLException ex) {
            showErr("Lỗi cơ sở dữ liệu:\n" + ex.getMessage());
        }
    }

    // ── XÓA ─────────────────────────────────────────────────────
    private void xoa() {
        String maNV = txtMaNV.getText().trim();
        if (maNV.isEmpty()) {
            showErr("Vui lòng chọn một nhân viên từ bảng để xóa!");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
            "Xác nhận xóa nhân viên \"" + maNV + "\"?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            if (dao.xoa(maNV)) {
                showInfo("Đã xóa nhân viên \"" + maNV + "\".");
                xoaTrang();
                taiDanhSach(null);
            } else {
                showErr("Không tìm thấy nhân viên để xóa.");
            }
        } catch (SQLException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("FK_"))
                showErr("Không thể xóa! Nhân viên đã có hóa đơn liên kết.");
            else
                showErr("Lỗi cơ sở dữ liệu:\n" + ex.getMessage());
        }
    }

    // ── SỬA ─────────────────────────────────────────────────────
    private void sua() {
        NhanVien nv = docForm();
        if (nv == null) return;

        try {
            if (!dao.tonTai(nv.getMaNV())) {
                showErr("Không tìm thấy nhân viên \"" + nv.getMaNV() + "\"!");
                return;
            }
            // Nếu để trống mật khẩu → giữ nguyên mật khẩu cũ
            String mk = new String(txtMatKhau.getPassword()).trim();
            if (mk.isEmpty()) {
                NhanVien cu = dao.timTheoMa(nv.getMaNV());
                if (cu != null) nv.setMatKhau(cu.getMatKhau());
            }
            if (dao.capNhat(nv)) {
                showInfo("Cập nhật thành công!");
                xoaTrang();
                taiDanhSach(null);
            } else {
                showErr("Cập nhật thất bại, vui lòng thử lại.");
            }
        } catch (SQLException ex) {
            showErr("Lỗi cơ sở dữ liệu:\n" + ex.getMessage());
        }
    }

    // ── TÌM KIẾM ────────────────────────────────────────────────
    private void timKiem() {
        taiDanhSach(txtTimKiem.getText().trim());
    }

    // ════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════

    /** Đọc & validate form → NhanVien. Trả null nếu lỗi. */
    private NhanVien docForm() {
        String maNV   = txtMaNV.getText().trim();
        String tenNV  = txtTenNV.getText().trim();
        String sdt    = txtSdt.getText().trim();
        String ngay   = txtNgayVaoLam.getText().trim();
        String mk     = new String(txtMatKhau.getPassword()).trim();
        String diaChi = txtDiaChi.getText().trim();

        if (maNV.isEmpty())  { showErr("Mã nhân viên không được để trống!");   return null; }
        if (tenNV.isEmpty()) { showErr("Tên nhân viên không được để trống!");   return null; }
        if (sdt.isEmpty())   { showErr("Số điện thoại không được để trống!");  return null; }
        if (!sdt.matches("\\d{9,15}")) {
            showErr("Số điện thoại chỉ gồm chữ số (9-15 ký tự)!"); return null;
        }
        if (ngay.isEmpty())  { showErr("Ngày vào làm không được để trống!");   return null; }

        LocalDate ngayVaoLam;
        try {
            ngayVaoLam = LocalDate.parse(ngay, DATE_FMT);
        } catch (DateTimeParseException ex) {
            showErr("Ngày không hợp lệ!\nVui lòng nhập theo dạng: yyyy-MM-dd  (VD: 2024-01-15)");
            return null;
        }
        if (ngayVaoLam.isAfter(LocalDate.now())) {
            showErr("Ngày vào làm không được lớn hơn ngày hôm nay!"); return null;
        }

        String  gioiTinh = (String) cbGioiTinh.getSelectedItem();
        boolean quanLy   = "Quản lý".equals(cbVaiTro.getSelectedItem());
        return new NhanVien(maNV, tenNV, diaChi, ngayVaoLam, gioiTinh, sdt, mk, quanLy);
    }

    /** Load danh sách lên bảng. keyword = null → lấy tất cả. */
    private void taiDanhSach(String keyword) {
        tableModel.setRowCount(0);
        try {
            List<NhanVien> list = (keyword == null || keyword.isEmpty())
                    ? dao.layTatCa()
                    : dao.timKiem(keyword);
            for (NhanVien nv : list) {
                tableModel.addRow(new Object[]{
                    nv.getMaNV(),
                    nv.getTenNV(),
                    nv.getGioiTinh(),
                    nv.getSdt(),
                    nv.getNgayVaoLam() != null ? nv.getNgayVaoLam().format(DATE_FMT) : "",
                    nv.getVaiTro(),
                    nv.getDiaChi() != null ? nv.getDiaChi() : ""
                });
            }
        } catch (SQLException ex) {
            showErr("Không thể tải dữ liệu:\n" + ex.getMessage());
        }
    }

    /** Xóa trắng toàn bộ form. */
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
        table.clearSelection();
        txtMaNV.requestFocus();
    }

    private CustomButton makeBtn(String text, Color bg) {
        CustomButton btn = new CustomButton(text, bg, Color.WHITE);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setPreferredSize(new Dimension(110, 35));
        return btn;
    }

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    private void showErr(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    // ════════════════════════════════════════════════════════════
    //  MOUSE – click hàng bảng → điền form
    // ════════════════════════════════════════════════════════════

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtMaNV.setText(tableModel.getValueAt(row, 0).toString());
        txtTenNV.setText(tableModel.getValueAt(row, 1).toString());
        cbGioiTinh.setSelectedItem(tableModel.getValueAt(row, 2).toString());
        txtSdt.setText(tableModel.getValueAt(row, 3).toString());
        txtNgayVaoLam.setText(tableModel.getValueAt(row, 4).toString());
        cbVaiTro.setSelectedItem(tableModel.getValueAt(row, 5).toString());
        txtDiaChi.setText(tableModel.getValueAt(row, 6).toString());
        txtMatKhau.setText(""); // không hiện mật khẩu cũ
    }

    @Override public void mousePressed(MouseEvent e)  {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e)  {}
    @Override public void mouseExited(MouseEvent e)   {}
}
