package giaodien_UI;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ThanhToanPanel extends JDialog {
    private boolean isThanhToanThanhCong = false;
    private String hinhThuc = "TIEN_MAT"; // mã DB mặc định

    public ThanhToanPanel(Frame parent, double tongTien, String maHoaDon) {
        super(parent, "Xác nhận thanh toán", true); // true = Bắt buộc phải tắt dialog này mới bấm được phần khác
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // --- Panel Chọn phương thức ---
        JPanel pnlTop = new JPanel();
        pnlTop.add(new JLabel("Phương thức: "));
        JComboBox<String> cbPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản (Mã QR)"});
        pnlTop.add(cbPhuongThuc);
        add(pnlTop, BorderLayout.NORTH);

        // --- Panel Hiển thị QR hoặc Tiền mặt ---
        JPanel pnlCenter = new JPanel(new CardLayout());
        
        // Card 1: Tiền mặt
        JPanel pnlTienMat = new JPanel(new GridLayout(3, 1));
        pnlTienMat.add(new JLabel("Cần thanh toán: " + String.format("%,.0f VNĐ", tongTien), SwingConstants.CENTER));
        pnlTienMat.add(new JLabel("Vui lòng nhận tiền mặt từ khách.", SwingConstants.CENTER));
        pnlCenter.add(pnlTienMat, "TIEN_MAT");

        // Card 2: Mã QR (Dùng API VietQR)
        JPanel pnlQR = new JPanel(new BorderLayout());
        JLabel lblQR = new JLabel("Đang tải mã QR...", SwingConstants.CENTER);
        pnlQR.add(new JLabel("Quét mã qua app Ngân hàng", SwingConstants.CENTER), BorderLayout.NORTH);
        pnlQR.add(lblQR, BorderLayout.CENTER);
        pnlCenter.add(pnlQR, "MA_QR");

        add(pnlCenter, BorderLayout.CENTER);

        // --- Panel Nút bấm ---
        JPanel pnlBottom = new JPanel();
        JButton btnXacNhan = new JButton("ĐÃ NHẬN TIỀN (XONG)");
        btnXacNhan.setBackground(new Color(0, 153, 51));
        btnXacNhan.setForeground(Color.WHITE);
        JButton btnHuy = new JButton("Hủy bỏ");
        pnlBottom.add(btnXacNhan);
        pnlBottom.add(btnHuy);
        add(pnlBottom, BorderLayout.SOUTH);

        // --- Xử lý sự kiện chuyển đổi ---
        CardLayout cl = (CardLayout) (pnlCenter.getLayout());
        cbPhuongThuc.addActionListener(e -> {
            // Map text hiển thị → mã DB
            switch (cbPhuongThuc.getSelectedIndex()) {
                case 0: hinhThuc = "TIEN_MAT";     break;
                case 1: hinhThuc = "CHUYEN_KHOAN"; break;
                default: hinhThuc = "TIEN_MAT";
            }
            if (cbPhuongThuc.getSelectedIndex() == 0) {
                cl.show(pnlCenter, "TIEN_MAT");
            } else {
                cl.show(pnlCenter, "MA_QR");
                // Giả sử dùng MB Bank (Mã BIN MB là 970422), STK: 0123456789
                //  có thể thay bằng STK thật của quán nếu muốn, nhưng nên để số ngẫu nhiên cho bảo mật
                long tien = (long) tongTien;
                String qrUrl = "https://img.vietqr.io/image/970422-0123456789-compact2.png?amount=" + tien + "&addInfo=ThanhToan" + maHoaDon;
                try {
                    // Tải ảnh từ web về và hiển thị
                    ImageIcon iconQR = new ImageIcon(new URL(qrUrl));
                    // Scale ảnh cho vừa khung
                    Image img = iconQR.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
                    lblQR.setText("");
                    lblQR.setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    lblQR.setText("Lỗi mạng: Không tải được mã QR");
                }
            }
        });

        // Xử lý nút bấm
        btnXacNhan.addActionListener(e -> {
            isThanhToanThanhCong = true;
            dispose(); // Đóng cửa sổ
        });
        
        btnHuy.addActionListener(e -> dispose());
    }

    public boolean isThanhToanThanhCong() { return isThanhToanThanhCong; }
    public String getHinhThuc()           { return hinhThuc; }
}