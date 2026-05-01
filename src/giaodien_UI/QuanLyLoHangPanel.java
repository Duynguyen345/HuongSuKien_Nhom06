package giaodien_UI;

import dAO.LoHang_DAO;
import model.LoHang;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class QuanLyLoHangPanel extends JPanel {

    private JTable table;
    private DefaultTableModel modelTable;
    private LoHang_DAO dao = new LoHang_DAO();

    // Form nhập liệu
    private JTextField txtMaLo, txtMaHH, txtNgayNhap, txtHanSuDung, txtSLNhap, txtSLTon, txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnCanhBao;

    public QuanLyLoHangPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 250));

        add(taoTieuDe(), BorderLayout.NORTH);
        add(taoBang(), BorderLayout.CENTER);
        add(taoFormNhapLieu(), BorderLayout.EAST);

        loadData();
        canhBaoTuDong();
    }

    // ===== TIÊU ĐỀ + THANH TÌM KIẾM =====
    private JPanel taoTieuDe() {
        JPanel pnl = new JPanel(new BorderLayout(10, 0));
        pnl.setOpaque(false);
        pnl.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JLabel lbl = new JLabel("QUẢN LÝ LÔ HÀNG");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(new Color(33, 47, 61));
        pnl.add(lbl, BorderLayout.WEST);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlSearch.setOpaque(false);
        pnlSearch.add(new JLabel("🔍 Tìm kiếm:"));
        txtTimKiem = new JTextField(16);
        txtTimKiem.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { timKiem(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { timKiem(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { timKiem(); }
        });
        pnlSearch.add(txtTimKiem);
        pnl.add(pnlSearch, BorderLayout.EAST);
        return pnl;
    }

    // ===== BẢNG DỮ LIỆU =====
    private JScrollPane taoBang() {
        String[] cols = {"Mã Lô", "Mã Hàng Hóa", "Ngày Nhập", "Hạn Sử Dụng", "SL Nhập", "SL Tồn", "Trạng thái"};
        modelTable = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(modelTable);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(33, 47, 61));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(174, 214, 241));

        // Tô màu đỏ dòng hết hạn
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                String tt = (String) modelTable.getValueAt(row, 6);
                if ("⚠ HẾT HẠN".equals(tt))      c.setBackground(new Color(255, 200, 200));
                else if ("⏰ SẮP HẾT HẠN".equals(tt)) c.setBackground(new Color(255, 243, 176));
                else c.setBackground(sel ? new Color(174, 214, 241) : Color.WHITE);
                return c;
            }
        });

        // Click dòng → điền vào form
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            txtMaLo.setText(modelTable.getValueAt(row, 0).toString());
            txtMaHH.setText(modelTable.getValueAt(row, 1).toString());
            txtNgayNhap.setText(modelTable.getValueAt(row, 2).toString());
            txtHanSuDung.setText(modelTable.getValueAt(row, 3).toString());
            txtSLNhap.setText(modelTable.getValueAt(row, 4).toString());
            txtSLTon.setText(modelTable.getValueAt(row, 5).toString());
            txtMaLo.setEditable(false); // Không cho sửa mã lô khi đang sửa
        });

        return new JScrollPane(table);
    }

    // ===== FORM NHẬP LIỆU =====
    private JPanel taoFormNhapLieu() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(33,47,61), 1),
                "Thông tin lô hàng", 0, 0,
                new Font("Segoe UI", Font.BOLD, 13), new Color(33, 47, 61)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        form.setPreferredSize(new Dimension(260, 0));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 4, 4, 4);

        txtMaLo      = new JTextField();
        txtMaHH      = new JTextField();
        txtNgayNhap  = new JTextField("yyyy-MM-dd");
        txtHanSuDung = new JTextField("yyyy-MM-dd");
        txtSLNhap    = new JTextField("0");
        txtSLTon     = new JTextField("0");

        String[] labels = {"Mã lô:", "Mã hàng hóa:", "Ngày nhập:", "Hạn sử dụng:", "SL nhập:", "SL tồn:"};
        JTextField[] fields = {txtMaLo, txtMaHH, txtNgayNhap, txtHanSuDung, txtSLNhap, txtSLTon};

        for (int i = 0; i < labels.length; i++) {
            g.gridx = 0; g.gridy = i; g.gridwidth = 1;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            form.add(lbl, g);
            g.gridx = 1;
            fields[i].setFont(new Font("Segoe UI", Font.PLAIN, 12));
            form.add(fields[i], g);
        }

        // Nút bấm
        btnThem    = taoNut("+ Thêm",     new Color(39, 174, 96));
        btnSua     = taoNut("✎ Sửa",      new Color(243, 156, 18));
        btnXoa     = taoNut("✕ Xóa",      new Color(231, 76, 60));
        btnLamMoi  = taoNut("↺ Làm mới",  new Color(108, 117, 125));
        btnCanhBao = taoNut("⚠ Sắp hết hạn", new Color(142, 68, 173));

        JPanel pnlBtn = new JPanel(new GridLayout(5, 1, 4, 6));
        pnlBtn.setOpaque(false);
        pnlBtn.add(btnThem); pnlBtn.add(btnSua); pnlBtn.add(btnXoa);
        pnlBtn.add(btnLamMoi); pnlBtn.add(btnCanhBao);

        g.gridx = 0; g.gridy = labels.length; g.gridwidth = 2;
        g.insets = new Insets(12, 4, 4, 4);
        form.add(pnlBtn, g);

        // Gắn sự kiện
        btnThem.addActionListener(e -> them());
        btnSua.addActionListener(e -> sua());
        btnXoa.addActionListener(e -> xoa());
        btnLamMoi.addActionListener(e -> { xoaForm(); loadData(); });
        btnCanhBao.addActionListener(e -> xemSapHetHan());

        return form;
    }

    // ===== LOAD DỮ LIỆU =====
    public void loadData() {
        modelTable.setRowCount(0);
        for (LoHang lh : dao.getAllLoHang()) {
            String tt;
            if (lh.isHetHan()) tt = "⚠ HẾT HẠN";
            else if (lh.isSapHetHan(30)) tt = "⏰ SẮP HẾT HẠN";
            else tt = "✓ Còn hạn";
            modelTable.addRow(new Object[]{
                lh.getMaLo(), lh.getMaHH(), lh.getNgayNhap(),
                lh.getHanSuDung(), lh.getSoLuongNhap(), lh.getSoLuongTon(), tt
            });
        }
    }

    // ===== TÌM KIẾM (ĐÃ ĐƯỢC FIX LỖI NULL) =====
    private void timKiem() {
        String kw = txtTimKiem.getText().trim().toLowerCase();
        modelTable.setRowCount(0);
        for (LoHang lh : dao.getAllLoHang()) {
            // FIX: Tránh lỗi NullPointerException khi lấy mã bị rỗng
            String maLo = lh.getMaLo() != null ? lh.getMaLo().toLowerCase() : "";
            String maHH = lh.getMaHH() != null ? lh.getMaHH().toLowerCase() : "";
            
            if (maLo.contains(kw) || maHH.contains(kw)) {
                String tt;
                if (lh.isHetHan()) tt = "⚠ HẾT HẠN";
                else if (lh.isSapHetHan(30)) tt = "⏰ SẮP HẾT HẠN";
                else tt = "✓ Còn hạn";
                modelTable.addRow(new Object[]{
                    lh.getMaLo(), lh.getMaHH(), lh.getNgayNhap(),
                    lh.getHanSuDung(), lh.getSoLuongNhap(), lh.getSoLuongTon(), tt
                });
            }
        }
    }

    // ===== THÊM =====
    private void them() {
        try {
            LoHang lh = docForm();
            if (dao.themLoHang(lh)) {
                JOptionPane.showMessageDialog(this, "✓ Thêm lô hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                xoaForm(); loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại! Kiểm tra lại mã lô.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== SỬA =====
    private void sua() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lô cần sửa!", "Chú ý", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            LoHang lh = docForm();
            if (dao.suaLoHang(lh)) {
                JOptionPane.showMessageDialog(this, "✓ Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                xoaForm(); loadData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== XÓA =====
    private void xoa() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn lô cần xóa!"); return; }
        String maLo = modelTable.getValueAt(row, 0).toString();
        int xn = JOptionPane.showConfirmDialog(this,
            "Xóa lô hàng [" + maLo + "]?\nThao tác này không thể hoàn tác!",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (xn == JOptionPane.YES_OPTION && dao.xoaLoHang(maLo)) {
            JOptionPane.showMessageDialog(this, "✓ Đã xóa lô " + maLo);
            xoaForm(); loadData();
        }
    }

    // ===== CẢNH BÁO SẮP HẾT HẠN =====
    private void xemSapHetHan() {
        ArrayList<LoHang> ds = dao.getLoHangSapHetHan(30);
        if (ds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có lô nào sắp hết hạn trong 30 ngày tới.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder("Các lô SẮP HẾT HẠN (trong 30 ngày):\n\n");
        for (LoHang lh : ds)
            sb.append("• ").append(lh.getMaLo())
              .append(" | HSD: ").append(lh.getHanSuDung())
              .append(" | Tồn: ").append(lh.getSoLuongTon()).append("\n");
        JOptionPane.showMessageDialog(this, sb.toString(), "⏰ Cảnh báo sắp hết hạn", JOptionPane.WARNING_MESSAGE);
    }

    // ===== CẢNH BÁO TỰ ĐỘNG KHI MỞ =====
    private void canhBaoTuDong() {
        ArrayList<LoHang> hetHan = dao.getLoHangHetHan();
        ArrayList<LoHang> sapHet = dao.getLoHangSapHetHan(7);
        if (!hetHan.isEmpty() || !sapHet.isEmpty()) {
            String msg = "";
            if (!hetHan.isEmpty()) msg += "⚠ Có " + hetHan.size() + " lô đã HẾT HẠN!\n";
            if (!sapHet.isEmpty()) msg += "⏰ Có " + sapHet.size() + " lô sắp hết hạn trong 7 ngày!";
            JOptionPane.showMessageDialog(this, msg, "Cảnh báo tồn kho", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ===== HELPER =====
    private LoHang docForm() {
        if (txtMaLo.getText().trim().isEmpty()) throw new RuntimeException("Mã lô không được trống!");
        if (txtMaHH.getText().trim().isEmpty()) throw new RuntimeException("Mã hàng hóa không được trống!");
        return new LoHang(
            txtMaLo.getText().trim(),
            txtMaHH.getText().trim(),
            LocalDate.parse(txtNgayNhap.getText().trim()),
            LocalDate.parse(txtHanSuDung.getText().trim()),
            Integer.parseInt(txtSLNhap.getText().trim()),
            Integer.parseInt(txtSLTon.getText().trim())
        );
    }

    private void xoaForm() {
        txtMaLo.setText(""); txtMaLo.setEditable(true);
        txtMaHH.setText(""); txtNgayNhap.setText("yyyy-MM-dd");
        txtHanSuDung.setText("yyyy-MM-dd");
        txtSLNhap.setText("0"); txtSLTon.setText("0");
        table.clearSelection();
    }

    private JButton taoNut(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("TEST - Quản lý lô hàng");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(1000, 600);
            f.setLocationRelativeTo(null);
            f.add(new QuanLyLoHangPanel());
            f.setVisible(true);
        });
    }
}