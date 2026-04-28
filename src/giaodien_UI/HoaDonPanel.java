package giaodien_UI;

import dAO.HoaDon_DAO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.util.List;

/**
 * HoaDonPanel – Trang quản lý hóa đơn đã thanh toán.
 *
 * Tính năng:
 *  - Danh sách hóa đơn load từ DB
 *  - Tìm kiếm theo mã HĐ / SĐT khách hàng
 *  - Lọc theo: hình thức thanh toán, khoảng ngày, khoảng tiền
 *  - Nút Làm mới bộ lọc
 *  - Click vào hóa đơn → xem chi tiết ở bảng dưới
 *  - Nút In lại hóa đơn (PrinterJob)
 */
public class HoaDonPanel extends JPanel {

    private final HoaDon_DAO dao = new HoaDon_DAO();

    // ── Bộ lọc / tìm kiếm ───────────────────────────────────
    private JTextField txtTimKiem, txtTuNgay, txtDenNgay, txtMinTien, txtMaxTien;
    private JComboBox<String> cbHinhThuc;

    // ── Bảng danh sách hóa đơn ──────────────────────────────
    private JTable tblHoaDon;
    private DefaultTableModel modelHoaDon;

    // ── Bảng chi tiết hóa đơn ───────────────────────────────
    private JTable tblChiTiet;
    private DefaultTableModel modelChiTiet;

    // ── Label tóm tắt hóa đơn đang chọn ────────────────────
    private JLabel lblTomTat;

    // ── Hóa đơn đang được chọn ──────────────────────────────
    private String maHDDangChon = null;

    public HoaDonPanel() {
        setLayout(new BorderLayout(6, 6));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 244, 248));

        add(buildFilter(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);

        taiDanhSach(null, null, null, null, 0, 0);
    }

    // ════════════════════════════════════════════════════════════
    //  PHẦN BỘ LỌC (NORTH)
    // ════════════════════════════════════════════════════════════
    private JPanel buildFilter() {
        JPanel outer = new JPanel(new BorderLayout(4, 4));
        outer.setOpaque(false);

        // --- Hàng 1: Tìm kiếm + nút ---
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        row1.setOpaque(false);

        txtTimKiem = new JTextField(20);
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtTimKiem.putClientProperty("JTextField.placeholderText", "Mã hóa đơn / SĐT khách...");

        JButton btnTimKiem = styled("🔍  Tìm kiếm", new Color(52, 152, 219));
        JButton btnLamMoi  = styled("↺  Làm mới",   new Color(127, 140, 141));

        row1.add(new JLabel("Tìm kiếm:"));
        row1.add(txtTimKiem);
        row1.add(btnTimKiem);
        row1.add(btnLamMoi);

        // --- Hàng 2: Các bộ lọc ---
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        row2.setOpaque(false);

        cbHinhThuc = new JComboBox<>(new String[]{"Tất cả", "Tiền mặt", "Chuyển khoản (Mã QR)"});
        cbHinhThuc.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        txtTuNgay  = field(10, "yyyy-MM-dd");
        txtDenNgay = field(10, "yyyy-MM-dd");
        txtMinTien = field(10, "VD: 50000");
        txtMaxTien = field(10, "VD: 500000");

        JButton btnLoc = styled("▼  Lọc", new Color(39, 174, 96));

        row2.add(new JLabel("Hình thức:")); row2.add(cbHinhThuc);
        row2.add(new JLabel("Từ ngày:"));  row2.add(txtTuNgay);
        row2.add(new JLabel("Đến ngày:")); row2.add(txtDenNgay);
        row2.add(new JLabel("Min (đ):"));  row2.add(txtMinTien);
        row2.add(new JLabel("Max (đ):"));  row2.add(txtMaxTien);
        row2.add(btnLoc);

        JPanel rows = new JPanel(new GridLayout(2, 1));
        rows.setOpaque(false);
        rows.add(row1);
        rows.add(row2);

        outer.add(rows, BorderLayout.CENTER);
        outer.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 215, 230)),
            "Tìm kiếm & Lọc hóa đơn",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), new Color(44, 62, 80)
        ));

        // ── Sự kiện ──
        btnTimKiem.addActionListener(e -> applyFilter());
        txtTimKiem.addActionListener(e -> applyFilter());
        btnLoc.addActionListener(e -> applyFilter());
        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText(""); txtTuNgay.setText(""); txtDenNgay.setText("");
            txtMinTien.setText(""); txtMaxTien.setText("");
            cbHinhThuc.setSelectedIndex(0);
            taiDanhSach(null, null, null, null, 0, 0);
        });

        return outer;
    }

    // ════════════════════════════════════════════════════════════
    //  PHẦN CHÍNH (CENTER): Bảng HĐ trên | Chi tiết dưới
    // ════════════════════════════════════════════════════════════
    private JSplitPane buildCenter() {
        // Bảng danh sách hóa đơn
        String[] colsHD = {"Mã HĐ", "Ngày lập", "Nhân viên", "Khách hàng (SĐT)", "Hình thức TT", "Tổng tiền gốc", "Giảm giá", "Thực thu"};
        modelHoaDon = new DefaultTableModel(colsHD, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblHoaDon = new JTable(modelHoaDon);
        styleTable(tblHoaDon, new int[]{90, 140, 80, 120, 120, 110, 80, 100});

        JScrollPane scrollHD = new JScrollPane(tblHoaDon);
        scrollHD.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200,215,230)),
            "Danh sách hóa đơn", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), new Color(44, 62, 80)
        ));

        // Bảng chi tiết
        String[] colsCT = {"Mã hàng", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"};
        modelChiTiet = new DefaultTableModel(colsCT, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblChiTiet = new JTable(modelChiTiet);
        styleTable(tblChiTiet, new int[]{80, 250, 60, 90, 100});

        JScrollPane scrollCT = new JScrollPane(tblChiTiet);
        scrollCT.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200,215,230)),
            "Chi tiết hóa đơn", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), new Color(44, 62, 80)
        ));

        // Click vào hóa đơn → load chi tiết
        tblHoaDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblHoaDon.getSelectedRow();
                if (row >= 0) {
                    maHDDangChon = modelHoaDon.getValueAt(row, 0).toString();
                    loadChiTiet(maHDDangChon, row);
                }
            }
        });

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollHD, scrollCT);
        split.setDividerLocation(300);
        split.setResizeWeight(0.6);
        split.setBorder(null);
        return split;
    }

    // ════════════════════════════════════════════════════════════
    //  PHẦN DƯỚI (SOUTH): Tóm tắt + Nút In lại
    // ════════════════════════════════════════════════════════════
    private JPanel buildBottom() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);

        lblTomTat = new JLabel("← Chọn một hóa đơn để xem chi tiết và in lại");
        lblTomTat.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblTomTat.setForeground(Color.GRAY);

        JButton btnIn = styled("🖨  In lại hóa đơn", new Color(142, 68, 173));
        btnIn.addActionListener(e -> inLaiHoaDon());

        p.add(lblTomTat, BorderLayout.CENTER);
        p.add(btnIn,     BorderLayout.EAST);
        return p;
    }

    // ════════════════════════════════════════════════════════════
    //  LOGIC
    // ════════════════════════════════════════════════════════════

    private void applyFilter() {
        String kw       = txtTimKiem.getText().trim();
        String hinhThuc = cbHinhThuc.getSelectedItem().toString();
        String tuNgay   = txtTuNgay.getText().trim();
        String denNgay  = txtDenNgay.getText().trim();
        double min = 0, max = 0;
        try { min = Double.parseDouble(txtMinTien.getText().trim()); } catch (Exception ignored) {}
        try { max = Double.parseDouble(txtMaxTien.getText().trim()); } catch (Exception ignored) {}
        taiDanhSach(kw.isEmpty() ? null : kw, hinhThuc, tuNgay.isEmpty() ? null : tuNgay,
                    denNgay.isEmpty() ? null : denNgay, min, max);
    }

    public void taiDanhSach(String tuKhoa, String hinhThuc, String tuNgay,
                             String denNgay, double min, double max) {
        modelHoaDon.setRowCount(0);
        modelChiTiet.setRowCount(0);
        maHDDangChon = null;
        lblTomTat.setText("← Chọn một hóa đơn để xem chi tiết và in lại");
        try {
            List<Object[]> list = dao.layDanhSach(tuKhoa, hinhThuc, tuNgay, denNgay, min, max);
            for (Object[] row : list) {
                Object ngay = row[1];
                String ngayStr = (ngay != null) ? ngay.toString().substring(0, 16) : "";
                modelHoaDon.addRow(new Object[]{
                    row[0], ngayStr, row[2], row[3], row[4],
                    String.format("%,.0f đ", row[5]),
                    String.format("%,.0f đ", row[6]),
                    String.format("%,.0f đ", row[7])
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách hóa đơn:\n" + e.getMessage());
        }
    }

    private void loadChiTiet(String maHD, int row) {
        modelChiTiet.setRowCount(0);
        try {
            List<Object[]> ct = dao.layChiTiet(maHD);
            for (Object[] r : ct) {
                modelChiTiet.addRow(new Object[]{
                    r[0], r[1], r[2],
                    String.format("%,.0f đ", r[3]),
                    String.format("%,.0f đ", r[4])
                });
            }
            // Cập nhật label tóm tắt
            String thucThu = modelHoaDon.getValueAt(row, 7).toString();
            String ngay    = modelHoaDon.getValueAt(row, 1).toString();
            String kh      = modelHoaDon.getValueAt(row, 3).toString();
            lblTomTat.setText(String.format(
                "📄  %s  |  Ngày: %s  |  Khách: %s  |  Thực thu: %s  |  %d sản phẩm",
                maHD, ngay, kh, thucThu, ct.size()
            ));
            lblTomTat.setForeground(new Color(44, 62, 80));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải chi tiết:\n" + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════
    //  IN LẠI HÓA ĐƠN (PrinterJob)
    // ════════════════════════════════════════════════════════════
    private void inLaiHoaDon() {
        if (maHDDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn trước.");
            return;
        }
        try {
            Object[] header = dao.layHeaderHD(maHDDangChon);
            List<Object[]> ct = dao.layChiTiet(maHDDangChon);
            if (header == null) { JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn."); return; }

            // Tạo text hóa đơn
            StringBuilder sb = new StringBuilder();
            sb.append("==============================\n");
            sb.append("       CỬA HÀNG TIỆN LỢI\n");
            sb.append("==============================\n");
            sb.append("Mã HĐ  : ").append(header[0]).append("\n");
            sb.append("Ngày   : ").append(header[1]).append("\n");
            sb.append("NV     : ").append(header[2]).append("\n");
            String tenKH = header[3] != null ? header[3].toString() : "Vãng lai";
            String sdt   = header[4] != null ? header[4].toString() : "";
            sb.append("Khách  : ").append(tenKH).append(sdt.isEmpty() ? "" : " - " + sdt).append("\n");
            sb.append("HT TT  : ").append(header[5]).append("\n");
            sb.append("------------------------------\n");
            sb.append(String.format("%-20s %4s %10s\n", "Sản phẩm", "SL", "Tiền"));
            sb.append("------------------------------\n");
            for (Object[] r : ct) {
                String ten = r[1].toString();
                if (ten.length() > 20) ten = ten.substring(0, 18) + "..";
                sb.append(String.format("%-20s %4d %10.0f\n", ten, r[2], r[4]));
            }
            sb.append("------------------------------\n");
            sb.append(String.format("Tổng gốc : %,.0f đ\n", header[6]));
            sb.append(String.format("Giảm giá : %,.0f đ\n", header[7]));
            sb.append(String.format("THỰC THU : %,.0f đ\n", header[8]));
            sb.append("==============================\n");
            sb.append("    Cảm ơn quý khách!\n");

            // Hiển thị preview rồi in
            JTextArea ta = new JTextArea(sb.toString());
            ta.setFont(new Font("Monospaced", Font.PLAIN, 11));
            ta.setEditable(false);
            JScrollPane sp = new JScrollPane(ta);
            sp.setPreferredSize(new Dimension(340, 480));

            int opt = JOptionPane.showConfirmDialog(this, sp, "Preview hóa đơn – " + maHDDangChon,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opt == JOptionPane.OK_OPTION) {
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setPrintable((graphics, pageFormat, pageIndex) -> {
                    if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
                    graphics.setFont(new Font("Monospaced", Font.PLAIN, 10));
                    String[] lines = sb.toString().split("\n");
                    int y = (int) pageFormat.getImageableY() + 12;
                    for (String line : lines) {
                        graphics.drawString(line, (int) pageFormat.getImageableX(), y);
                        y += 14;
                    }
                    return Printable.PAGE_EXISTS;
                });
                if (job.printDialog()) job.print();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi in hóa đơn:\n" + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════
    private JButton styled(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JTextField field(int cols, String placeholder) {
        JTextField tf = new JTextField(cols);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setToolTipText(placeholder);
        return tf;
    }

    private void styleTable(JTable t, int[] widths) {
        t.setRowHeight(26);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setSelectionBackground(new Color(52, 152, 219));
        t.setSelectionForeground(Color.WHITE);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.getTableHeader().setBackground(new Color(44, 62, 80));
        t.getTableHeader().setForeground(Color.WHITE);
        for (int i = 0; i < widths.length && i < t.getColumnCount(); i++)
            t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }
}
