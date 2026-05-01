package giaodien_UI;

import dAO.ThongKe_DAO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ThongKePanel extends JPanel {

    private ThongKe_DAO dao;

    public ThongKePanel() {
        // Khởi tạo DAO an toàn
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
            // Nếu lỗi, hiện thông báo thay vì crash
            removeAll();
            JLabel lbl = new JLabel("Lỗi tải dữ liệu thống kê: " + e.getMessage(), SwingConstants.CENTER);
            lbl.setForeground(Color.RED);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            add(lbl, BorderLayout.CENTER);
            e.printStackTrace();
        }
    }

    // ===== HEADER: tiêu đề + 4 thẻ số liệu =====
    private JPanel taoHeader() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setOpaque(false);

        JLabel title = new JLabel("THỐNG KÊ KINH DOANH", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(33, 47, 61));
        pnl.add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 4, 10, 0));
        cards.setOpaque(false);
        cards.setPreferredSize(new Dimension(0, 85));

        // Lấy số liệu an toàn — nếu lỗi thì hiện 0
        double dtHomNay   = safeDouble(() -> dao != null ? dao.getDoanhThuHomNay()   : 0);
        int    hdHomNay   = safeInt   (() -> dao != null ? dao.getSoHoaDonHomNay()   : 0);
        double dtThangNay = safeDouble(() -> dao != null ? dao.getDoanhThuThangNay() : 0);
        int    hdThangNay = safeInt   (() -> dao != null ? dao.getSoHoaDonThangNay() : 0);

        cards.add(taoCard("Doanh thu hom nay",
            String.format("%,.0f VND", dtHomNay),   new Color(39, 174, 96)));
        cards.add(taoCard("Hoa don hom nay",
            hdHomNay + " hoa don",                   new Color(52, 152, 219)));
        cards.add(taoCard("Doanh thu thang nay",
            String.format("%,.0f VND", dtThangNay),  new Color(155, 89, 182)));
        cards.add(taoCard("Hoa don thang nay",
            hdThangNay + " hoa don",                 new Color(230, 126, 34)));

        pnl.add(cards, BorderLayout.CENTER);
        return pnl;
    }

    // ===== 2 BIỂU ĐỒ =====
    private JSplitPane taoBieuDo() {
        int nam = Calendar.getInstance().get(Calendar.YEAR);
        ChartPanel chartCot   = taoChartDoanhThu(nam);
        ChartPanel chartTron  = taoChartSanPham();

        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT, chartCot, chartTron);
        split.setResizeWeight(0.6);
        split.setDividerSize(6);
        split.setBorder(null);
        return split;
    }

    // Biểu đồ cột doanh thu theo tháng
    private ChartPanel taoChartDoanhThu(int nam) {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        try {
            if (dao != null) {
                for (Map.Entry<String, Double> e : dao.getDoanhThuTheoThang(nam).entrySet())
                    ds.addValue(e.getValue() / 1000.0, "Nghin VND", e.getKey());
            }
        } catch (Exception e) {
            System.err.println("[Chart] Loi load doanh thu: " + e.getMessage());
        }
        // Nếu chưa có data, thêm 12 tháng = 0 để chart không bị lỗi trục
        if (ds.getRowCount() == 0) {
            for (int i = 1; i <= 12; i++)
                ds.addValue(0, "Nghin VND", "T" + i);
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Doanh thu theo thang - Nam " + nam,
            "Thang", "Doanh thu (Nghin VND)", ds);
        chart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(248, 249, 250));
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.getRenderer().setSeriesPaint(0, new Color(41, 128, 185));

        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        yAxis.setLowerBound(0);

        ChartPanel cp = new ChartPanel(chart);
        cp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return cp;
    }

    // Biểu đồ tròn top 5 sản phẩm
    private ChartPanel taoChartSanPham() {
        DefaultPieDataset ds = new DefaultPieDataset();
        try {
            if (dao != null) {
                List<Object[]> top = dao.getTopSanPham(5);
                if (!top.isEmpty()) {
                    for (Object[] row : top)
                        ds.setValue((String) row[0], (Number)(int) row[1]);
                }
            }
        } catch (Exception e) {
            System.err.println("[Chart] Loi load san pham: " + e.getMessage());
        }

        JFreeChart chart = ChartFactory.createPieChart(
            "Top 5 san pham ban chay", ds, true, true, false);
        chart.setBackgroundPaint(Color.WHITE);

        PiePlot pie = (PiePlot) chart.getPlot();
        pie.setSimpleLabels(false);
        pie.setLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        pie.setNoDataMessage("Chua co du lieu ban hang");
        pie.setNoDataMessageFont(new Font("Segoe UI", Font.ITALIC, 13));
        pie.setNoDataMessagePaint(new Color(120, 120, 120));
        pie.setBackgroundPaint(Color.WHITE);

        ChartPanel cp = new ChartPanel(chart);
        cp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return cp;
    }

    // ===== FOOTER =====
    private JPanel taoFooter() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnl.setOpaque(false);

        JButton btn = new JButton("Lam moi du lieu");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(33, 47, 61));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 36));
        btn.addActionListener(e -> lamMoi());
        pnl.add(btn);
        return pnl;
    }

    private void lamMoi() {
        Container parent = this.getParent();
        if (parent == null) return;
        // Tìm đúng key trong CardLayout và thay panel mới
        parent.remove(this);
        ThongKePanel newPanel = new ThongKePanel();
        parent.add(newPanel, "THONG_KE");
        ((CardLayout) parent.getLayout()).show(parent, "THONG_KE");
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

    // Helper: lấy double an toàn không crash
    private double safeDouble(java.util.function.Supplier<Double> fn) {
        try { return fn.get(); } catch (Exception e) { return 0; }
    }

    // Helper: lấy int an toàn không crash
    private int safeInt(java.util.function.Supplier<Integer> fn) {
        try { return fn.get(); } catch (Exception e) { return 0; }
    }
}