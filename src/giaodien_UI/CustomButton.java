package giaodien_UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomButton extends JButton {

    private Color originalColor;
    private Color hoverColor;
    private boolean isActive = false;

    public CustomButton(String text, Color bg, Color fg) {
        super(text);
        this.originalColor = bg;
        this.hoverColor    = bg.brighter();

        setBackground(originalColor);
        setForeground(fg);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setHorizontalAlignment(SwingConstants.LEFT);
        setMargin(new Insets(0, 16, 0, 0));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if (!isActive) setBackground(hoverColor); }
            @Override public void mouseExited(MouseEvent e)  { if (!isActive) setBackground(originalColor); }
        });
    }

    public void setActive(boolean active) {
        this.isActive = active;
        setBackground(active ? hoverColor : originalColor);
    }
}