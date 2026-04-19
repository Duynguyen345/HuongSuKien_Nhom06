package giaodien_UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomButton extends JButton {
    private Color originalColor;
    private Color hoverColor;

    public CustomButton(String text, Color bg, Color fg) {
        super(text);
        this.originalColor = bg;
        this.hoverColor = bg.brighter(); // Tự tạo màu sáng hơn để hover

        // Thiết kế basic cho nút
        setContentAreaFilled(true); // Cho phép hiển thị màu nền
        setBackground(originalColor);
        setForeground(fg);
        setFocusPainted(false); // Xóa cái khung viền khi click vào
        setFont(new Font("Arial", Font.BOLD, 14));

        // Xử lý hiệu ứng chuột (Hover effect)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor); // Chuột bay vào thì đổi màu sáng
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(originalColor); // Chuột bay ra thì về màu cũ
            }
        });
    }
}