package giaodien_UI;

import dAO.ThongKe_DAO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ThongKePanel extends JPanel {

    private ThongKe_DAO dao;
    private int selectedMonth;
    private int selectedYear;

    public ThongKePanel() {
        Calendar cal = Calendar.getInstance();
        this.selectedMonth = cal.get(Calendar.MONTH) + 1;
        this.selectedYear = cal.get(Calendar.YEAR);
        initUI();
    }

    public ThongKePanel(int month, int year) {
        this.selectedMonth = month;
        this.selectedYear = year;
        initUI();
    }

    private void initUI() {
        try {
            dao = new ThongKe_DAO();
        } catch (Exception e) {
            dao = null;
            System.err.println("[ThongKePanel] Loi khoi tao DAO: " + e.getMessage());
        }

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setBackground(new Color(245, 245, 250));

        try {
            add(taoHeader(), BorderLayout.NORTH);
            add(taoBieuDo(), BorderLayout.CENTER);
            add(taoFooter(), BorderLayout.SOUTH);
        } catch (Exception e) {
            removeAll();
            JLabel lbl = new JLabel("Lỗi tải dữ liệu thống kê: " + e.getMessage(), SwingConstants.CENTER);
            lbl.setForeground(Color.RED);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            add(lbl, BorderLayout.CENTER);
            e.printStackTrace();
        }
    }

    // ===== HEADER: tiêu đề + 4 thẻ số liệu + Lọc thời gian =====
    private JPanel taoHeader() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setOpaque(false);

        // Header Top: Title + Filter
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel title = new JLabel("THỐNG KÊ KINH DOANH", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(33, 47, 61));
        topPanel.add(title, BorderLayout.CENTER);

        topPanel.add(taoFilterPanel(), BorderLayout.SOUTH);
        pnl.add(topPanel, BorderLayout.NORTH);

        // 4 Thẻ số liệu
        JPanel cards = new JPanel(new GridLayout(1, 4, 10, 0));
        cards.setOpaque(false);
        cards.setPreferredSize(new Dimension(0, 85));

        double dtThang = safeDouble(() -> dao != null ? dao.getDoanhThuTheoThangNam(selectedMonth, selectedYear) : 0);
        int    hdThang = safeInt   (() -> dao != null ? dao.getSoHoaDonTheoThangNam(selectedMonth, selectedYear) : 0);
        double dtNam   = safeDouble(() -> dao != null ? dao.getDoanhThuTheoNam(selectedYear) : 0);
        int    hdNam   = safeInt   (() -> dao != null ? dao.getSoHoaDonTheoNam(selectedYear) : 0);

        cards.add(taoCard("Doanh thu tháng " + selectedMonth, String.format("%,.0f VND", dtThang),   new Color(39, 174, 96)));
        cards.add(taoCard("Hóa đơn tháng " + selectedMonth,   hdThang + " đơn",                    new Color(52, 152, 219)));
        cards.add(taoCard("Doanh thu năm " + selectedYear,    String.format("%,.0f VND", dtNam),  new Color(155, 89, 182)));
        cards.add(taoCard("Hóa đơn năm " + selectedYear,      hdNam + " đơn",                  new Color(230, 126, 34)));

        pnl.add(cards, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel taoFilterPanel() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        pnl.setOpaque(false);

        JLabel lblThang = new JLabel("Tháng:");
        lblThang.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnl.add(lblThang);

        JComboBox<Integer> cboThang = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            cboThang.addItem(i);
        }
        cboThang.setSelectedItem(selectedMonth);
        pnl.add(cboThang);

        JLabel lblNam = new JLabel("Năm:");
        lblNam.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnl.add(lblNam);

        JComboBox<Integer> cboNam = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2020; i <= currentYear; i++) {
            cboNam.addItem(i);
        }
        cboNam.setSelectedItem(selectedYear);
        pnl.add(cboNam);

        JButton btnLoc = new JButton("Lọc Dữ Liệu");
        btnLoc.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLoc.setBackground(new Color(52, 73, 94));
        btnLoc.setForeground(Color.WHITE);
        btnLoc.setFocusPainted(false);
        btnLoc.addActionListener(e -> {
            int m = (Integer) cboThang.getSelectedItem();
            int y = (Integer) cboNam.getSelectedItem();
            lamMoi(m, y);
        });
        pnl.add(btnLoc);

        return pnl;
    }

    // ===== 2 BIỂU ĐỒ (TỰ VẼ BẰNG GRAPHICS 2D) =====
    private JSplitPane taoBieuDo() {
        // Lấy dữ liệu từ DB 1 lần theo tháng/năm được chọn
        Map<String, Double> dataDoanhThu = null;
        List<Object[]> dataTopSP = null;
        try {
            if (dao != null) {
                dataDoanhThu = dao.getDoanhThuTheoNgayTrongThang(selectedMonth, selectedYear);
                dataTopSP = dao.getTopSanPhamTheoThang(selectedMonth, selectedYear, 5);
            }
        } catch (Exception e) {
            System.err.println("Loi load data bieu do: " + e.getMessage());
        }

        JPanel chartCot   = new CustomBarChart(dataDoanhThu, selectedMonth, selectedYear);
        JPanel chartTron  = new CustomPieChart(dataTopSP, selectedMonth, selectedYear);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chartCot, chartTron);
        split.setResizeWeight(0.6);
        split.setDividerSize(6);
        split.setBorder(null);
        return split;
    }

    // ===== FOOTER =====
    private JPanel taoFooter() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnl.setOpaque(false);

        JButton btn = new JButton("Làm mới toàn bộ (Về tháng hiện tại)");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(33, 47, 61));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(300, 36));
        btn.addActionListener(e -> lamMoi(Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.YEAR)));
        pnl.add(btn);
        return pnl;
    }

    private void lamMoi(int month, int year) {
        Container parent = this.getParent();
        if (parent == null) return;
        parent.remove(this);
        ThongKePanel newPanel = new ThongKePanel(month, year);
        parent.add(newPanel, "THONG_KE"); // Thay đổi String này nếu form chính của bạn xài tên khác
        if (parent.getLayout() instanceof CardLayout) {
            ((CardLayout) parent.getLayout()).show(parent, "THONG_KE");
        }
        parent.revalidate();
        parent.repaint();
    }

    private JPanel taoCard(String label, String value, Color color) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 4));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel lbL = new JLabel(label);
        lbL.setForeground(new Color(220, 240, 220));
        lbL.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lbV = new JLabel(value);
        lbV.setForeground(Color.WHITE);
        lbV.setFont(new Font("Segoe UI", Font.BOLD, 18));

        card.add(lbL);
        card.add(lbV);
        return card;
    }

    private double safeDouble(java.util.function.Supplier<Double> fn) {
        try { return fn.get(); } catch (Exception e) { return 0; }
    }

    private int safeInt(java.util.function.Supplier<Integer> fn) {
        try { return fn.get(); } catch (Exception e) { return 0; }
    }

    // =====================================================================
    // LỚP VẼ BIỂU ĐỒ CỘT THỦ CÔNG THEO NGÀY TRONG THÁNG
    // =====================================================================
    class CustomBarChart extends JPanel {
        private Map<String, Double> data;
        private int thang;
        private int nam;

        public CustomBarChart(Map<String, Double> data, int thang, int nam) {
            this.data = data;
            this.thang = thang;
            this.nam = nam;
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createTitledBorder("Doanh thu theo ngày (Nghìn VNĐ) - Tháng " + thang + "/" + nam));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 40;
            int labelPadding = 20;

            // Tìm giá trị lớn nhất để căn tỷ lệ cột
            double maxScore = 0;
            for (Double score : data.values()) {
                maxScore = Math.max(maxScore, score);
            }
            if (maxScore == 0) maxScore = 1; // Tránh chia cho 0

            // Vẽ trục X và Y
            g2.setColor(Color.BLACK);
            g2.drawLine(padding, height - padding - labelPadding, padding, padding); // Y
            g2.drawLine(padding, height - padding - labelPadding, width - padding, height - padding - labelPadding); // X

            // Vẽ các cột
            int numBars = data.size();
            int barWidth = (width - 2 * padding) / numBars - 4;
            if (barWidth < 2) barWidth = 2; // tối thiểu
            int xOffset = padding + 4;
            g2.setFont(new Font("Arial", Font.PLAIN, 9));

            for (Map.Entry<String, Double> entry : data.entrySet()) {
                String ngay = entry.getKey();
                double value = entry.getValue() / 1000.0; // Đổi ra nghìn VNĐ

                int barHeight = (int) ((value / (maxScore / 1000.0)) * (height - 2 * padding - labelPadding));
                int x = xOffset;
                int y = height - padding - labelPadding - barHeight;

                // Vẽ cột
                g2.setColor(new Color(41, 128, 185)); // Màu xanh dương
                if (value > 0) {
                    g2.fillRect(x, y, barWidth, barHeight);
                }

                // Viết nhãn (Ngày) - chỉ hiện ngày lẻ để tránh đè chữ nếu số lượng ngày nhiều
                g2.setColor(Color.BLACK);
                if (numBars <= 15 || Integer.parseInt(ngay) % 2 != 0) {
                    g2.drawString(ngay, x + (barWidth / 4), height - padding);
                }

                xOffset += barWidth + 4;
            }
        }
    }

    // =====================================================================
    // LỚP VẼ BIỂU ĐỒ TRÒN THỦ CÔNG
    // =====================================================================
    class CustomPieChart extends JPanel {
        private List<Object[]> data;
        private Color[] colors = {
            new Color(231, 76, 60),  // Đỏ
            new Color(46, 204, 113), // Xanh lá
            new Color(52, 152, 219), // Xanh dương
            new Color(241, 196, 15), // Vàng
            new Color(155, 89, 182)  // Tím
        };

        public CustomPieChart(List<Object[]> data, int thang, int nam) {
            this.data = data;
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createTitledBorder("Top 5 sản phẩm bán chạy nhất - Tháng " + thang + "/" + nam));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (data == null || data.isEmpty()) {
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                g2.drawString("Không có giao dịch trong tháng", getWidth() / 2 - 100, getHeight() / 2);
                return;
            }

            // Tính tổng số lượng
            double total = 0;
            for (Object[] row : data) {
                total += ((Number) row[1]).doubleValue();
            }

            int width = getWidth();
            int height = getHeight();
            
            // Dành 160px bên phải cho phần chú thích (Legend)
            int legendWidth = 160;
            int pieSize = Math.min(width - legendWidth - 40, height - 60);
            if (pieSize < 50) pieSize = 50; // Đảm bảo biểu đồ không bị âm
            
            int x = 20; // Cách lề trái 20px
            int y = (height - pieSize) / 2;

            double startAngle = 0;
            int colorIndex = 0;
            int legendY = y + 20; // Dịch chú thích xuống một chút cho cân đối

            g2.setFont(new Font("Arial", Font.PLAIN, 12));

            // Vẽ từng lát cắt và chú thích
            for (Object[] row : data) {
                String name = (String) row[0];
                double value = ((Number) row[1]).doubleValue();
                double arcAngle = (value / total) * 360;

                // Vẽ lát cắt
                g2.setColor(colors[colorIndex % colors.length]);
                g2.fill(new Arc2D.Double(x, y, pieSize, pieSize, startAngle, arcAngle, Arc2D.PIE));
                startAngle += arcAngle;

                // Vẽ chú thích (Legend) bên phải
                int legendX = x + pieSize + 30; // Cách biểu đồ 30px
                g2.fillRect(legendX, legendY, 15, 15);
                g2.setColor(Color.BLACK);
                
                // Giới hạn tên SP dài
                String displayName = name.length() > 18 ? name.substring(0, 18) + "..." : name;
                g2.drawString(displayName + " (" + (int)value + ")", legendX + 25, legendY + 12);
                
                legendY += 28; // Tăng khoảng cách giữa các dòng chú thích
                colorIndex++;
            }
        }
    }
}