package giaodien_UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import connectDB.ConnectDB;
import dAO.HangHoa_DAO;
import dAO.LoaiHangHoa_DAO;
import model.HangHoa;
import model.LoaiHangHoa;

public class HangHoaPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTable table;
	private DefaultTableModel tableModel;
	private JTextField txtMaHH, txtMaVach, txtTenHH, txtGiaSP, txtTimKiem, txtHinhAnhPath; 
	private JLabel lblHinhAnh;
	private JComboBox<LoaiHangHoa> cbLoaiHH; 
	private JComboBox<String> cbLocLoaiHH;
	private JButton btnThem, btnSua, btnXoa, btnXoaTrang;
	
	private HangHoa_DAO hangHoaDAO;
	private LoaiHangHoa_DAO loaiHangHoaDAO;
	private List<HangHoa> dsHH;

	public HangHoaPanel() {
		try {
			ConnectDB.getInstance().connect();
		} catch (Exception e) {
			e.printStackTrace();
		}

		hangHoaDAO = new HangHoa_DAO();
		loaiHangHoaDAO = new LoaiHangHoa_DAO();

		initUI();
		loadData();
	}

	private void initUI() {
		setLayout(new BorderLayout());

	//bảng dữ liệu
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBackground(new Color(240, 242, 245));
		
		JLabel lblTitle = new JLabel("QUẢN LÝ HÀNG HÓA", JLabel.CENTER);
		lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
		lblTitle.setForeground(new Color(0, 51, 102));
		lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
		tablePanel.add(lblTitle, BorderLayout.NORTH);

		String[] columns = { "Mã HH", "Mã Vạch", "Tên HH", "Hình ảnh", "Giá SP", "Loại HH" };
		tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		
		
		table.setRowHeight(100); // Giúp dòng cao lên để chứa ảnh to
		table.getColumnModel().getColumn(3).setCellRenderer(new ImageRenderer()); // Để nó biết cột 3 là để vẽ hình
		

		Font tableFont = new Font("Arial", Font.PLAIN, 14);
		table.setFont(tableFont);
		
	    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
	    centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		
	    JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Arial", Font.BOLD, 14));
		header.setBackground(new Color(0, 51, 102));
		header.setForeground(Color.WHITE);

		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); 
		table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); 
		table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); 
		table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); 

		table.getColumnModel().getColumn(3).setCellRenderer(new ImageRenderer());

		table.getColumnModel().getColumn(0).setPreferredWidth(60);  
		table.getColumnModel().getColumn(1).setPreferredWidth(100); 
		table.getColumnModel().getColumn(2).setPreferredWidth(180); 
		table.getColumnModel().getColumn(3).setPreferredWidth(90);  
		table.getColumnModel().getColumn(4).setPreferredWidth(90);  
		table.getColumnModel().getColumn(5).setPreferredWidth(100); 

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.getSelectedRow();
				if (row >= 0) {
					txtMaHH.setText(tableModel.getValueAt(row, 0).toString());
					txtMaHH.setEditable(false); 
					txtMaVach.setText(tableModel.getValueAt(row, 1).toString());
					txtTenHH.setText(tableModel.getValueAt(row, 2).toString());
					
					String hinhAnh = hangHoaDAO.getHinhAnhByMa(txtMaHH.getText());
					txtHinhAnhPath.setText(hinhAnh != null ? hinhAnh : "");
					
					ImageIcon icon = createImageIcon(hinhAnh, 150, 100);
					if(icon != null) {
						lblHinhAnh.setIcon(icon);
						lblHinhAnh.setText("");
					} else {
						lblHinhAnh.setIcon(null);
						lblHinhAnh.setText("No Image");
					}

					txtGiaSP.setText(tableModel.getValueAt(row, 4).toString());
					
					String tenLH = tableModel.getValueAt(row, 5).toString();
					for(int i = 0; i < cbLoaiHH.getItemCount(); i++) {
						if(cbLoaiHH.getItemAt(i).getTenLoaiHang().equals(tenLH)) {
							cbLoaiHH.setSelectedIndex(i);
							break;
						}
					}
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(table);
		tablePanel.add(scrollPane, BorderLayout.CENTER);
		add(tablePanel, BorderLayout.CENTER);

		// ================= PHẦN EAST: FORM NHẬP LIỆU =================
		JPanel inputPanel = new JPanel(new BorderLayout());
		inputPanel.setPreferredSize(new java.awt.Dimension(320, 600));
		inputPanel.setBackground(new Color(230, 230, 230)); 
		
		JLabel lblDetailTitle = new JLabel("Thông tin chi tiết", JLabel.CENTER);
		lblDetailTitle.setFont(new Font("Arial", Font.BOLD, 18));
		lblDetailTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		inputPanel.add(lblDetailTitle, BorderLayout.NORTH);

		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBackground(Color.WHITE);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 5, 8, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		Font formFont = new Font("Arial", Font.PLAIN, 13); 

		gbc.gridx = 0; gbc.gridy = 0;
		formPanel.add(new JLabel("Mã HH:"), gbc);
		txtMaHH = new JTextField(12); 
		gbc.gridx = 1; formPanel.add(txtMaHH, gbc);
		
		gbc.gridx = 0; gbc.gridy = 1;
		formPanel.add(new JLabel("Mã vạch:"), gbc);
		txtMaVach = new JTextField(12); 
		gbc.gridx = 1; formPanel.add(txtMaVach, gbc);

		gbc.gridx = 0; gbc.gridy = 2;
		formPanel.add(new JLabel("Tên HH:"), gbc);
		txtTenHH = new JTextField(12); 
		gbc.gridx = 1; formPanel.add(txtTenHH, gbc);

		gbc.gridx = 0; gbc.gridy = 3;
		formPanel.add(new JLabel("Hình ảnh:"), gbc);
		lblHinhAnh = new JLabel("No Image", JLabel.CENTER);
		lblHinhAnh.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		lblHinhAnh.setPreferredSize(new java.awt.Dimension(120, 100));
		gbc.gridx = 1; formPanel.add(lblHinhAnh, gbc);

		gbc.gridx = 0; gbc.gridy = 4;
		formPanel.add(new JLabel("Tên file ảnh:"), gbc);
		txtHinhAnhPath = new JTextField(12); 
		gbc.gridx = 1; formPanel.add(txtHinhAnhPath, gbc);

		gbc.gridx = 0; gbc.gridy = 5;
		formPanel.add(new JLabel("Giá SP:"), gbc);
		txtGiaSP = new JTextField(12); 
		gbc.gridx = 1; formPanel.add(txtGiaSP, gbc);

		gbc.gridx = 0; gbc.gridy = 6;
		formPanel.add(new JLabel("Loại HH:"), gbc);
		cbLoaiHH = new JComboBox<>();
		List<LoaiHangHoa> listLHH = loaiHangHoaDAO.getAllLoaiHangHoa();
		if(listLHH != null) {
			for (LoaiHangHoa lhh : listLHH) cbLoaiHH.addItem(lhh);
		}
		gbc.gridx = 1; formPanel.add(cbLoaiHH, gbc);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		buttonPanel.setBackground(Color.WHITE);
		btnThem = new JButton("Thêm");
		btnSua = new JButton("Sửa");
		btnXoa = new JButton("Xóa");
		btnXoaTrang = new JButton("Làm mới");
		buttonPanel.add(btnThem); buttonPanel.add(btnSua);
		buttonPanel.add(btnXoa); buttonPanel.add(btnXoaTrang);

		gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
		formPanel.add(buttonPanel, gbc);

		txtTimKiem = new JTextField(15);
		JPanel pnlSearch = new JPanel(new BorderLayout());
		pnlSearch.setBorder(BorderFactory.createTitledBorder("Tìm kiếm theo tên"));
		pnlSearch.add(txtTimKiem);
		gbc.gridy = 8; formPanel.add(pnlSearch, gbc);

		cbLocLoaiHH = new JComboBox<>();
		cbLocLoaiHH.addItem("Tất cả");
		if(listLHH != null) {
			for (LoaiHangHoa lhh : listLHH) cbLocLoaiHH.addItem(lhh.getTenLoaiHang());
		}
		JPanel pnlFilter = new JPanel(new BorderLayout());
		pnlFilter.setBorder(BorderFactory.createTitledBorder("Lọc theo loại hàng"));
		pnlFilter.add(cbLocLoaiHH);
		gbc.gridy = 9; formPanel.add(pnlFilter, gbc);

		inputPanel.add(formPanel, BorderLayout.CENTER);
		add(inputPanel, BorderLayout.EAST);

		// Listeners
		btnThem.addActionListener(e -> themHangHoa());
		btnXoa.addActionListener(e -> xoaHangHoa());
		btnSua.addActionListener(e -> suaHangHoa());
		btnXoaTrang.addActionListener(e -> clearForm());
		txtTimKiem.getDocument().addDocumentListener(timKiemDong());
		cbLocLoaiHH.addItemListener(e -> locLoaiHang());
	}

	private void loadData() {
	
		dsHH = hangHoaDAO.getAllHangHoaForSanPhamPanel();
		reloadData();
	}
	
	private void reloadData() {
		tableModel.setRowCount(0);
		for (HangHoa hh : dsHH) {
			ImageIcon imageIcon = createImageIcon(hh.getHinhAnh(), 60, 60); 
			tableModel.addRow(new Object[] { 
				hh.getMaHH(), hh.getMaVach(), hh.getTenHH(), 
				imageIcon, hh.getGiaSP(), hh.getLoaiHangHoa().getTenLoaiHang() 
			});
		}
	}

	private void locLoaiHang() {
        String loaiHang = cbLocLoaiHH.getSelectedItem().toString();
        tableModel.setRowCount(0); 
        for (HangHoa hh : dsHH) {
            if (loaiHang.equals("Tất cả") || hh.getLoaiHangHoa().getTenLoaiHang().equalsIgnoreCase(loaiHang)) {
                ImageIcon imageIcon = createImageIcon(hh.getHinhAnh(), 60, 60);
                tableModel.addRow(new Object[] {
                    hh.getMaHH(), hh.getMaVach(), hh.getTenHH(), imageIcon, hh.getGiaSP(), hh.getLoaiHangHoa().getTenLoaiHang()
                });
            }
        }
    }

	public void themHangHoa() {
		try {
			String maHH = txtMaHH.getText().trim();
			String maVach = txtMaVach.getText().trim();
			String tenHH = txtTenHH.getText().trim();
			String hinhAnh = txtHinhAnhPath.getText().trim();
			double giaSP = Double.parseDouble(txtGiaSP.getText().trim());
			LoaiHangHoa lhh = (LoaiHangHoa) cbLoaiHH.getSelectedItem();
			
			HangHoa hh = new HangHoa(maHH, maVach, tenHH, giaSP, hinhAnh, lhh, true);
			if (hangHoaDAO.themHangHoa(hh)) {
				JOptionPane.showMessageDialog(this, "Thêm thành công!");
				clearForm();
			} else JOptionPane.showMessageDialog(this, "Thêm thất bại!");
		} catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi nhập liệu!"); }
	}

	public void suaHangHoa() {
		try {
			HangHoa hh = new HangHoa(txtMaHH.getText(), txtMaVach.getText(), txtTenHH.getText(), 
					Double.parseDouble(txtGiaSP.getText()), txtHinhAnhPath.getText(), (LoaiHangHoa)cbLoaiHH.getSelectedItem(), true);
			if (hangHoaDAO.capNhatHangHoa(hh)) {
				JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
				loadData();
			}
		} catch (Exception ex) { ex.printStackTrace(); }
	}

	private void xoaHangHoa() {
		if (JOptionPane.showConfirmDialog(this, "Ngừng kinh doanh sản phẩm này?") == JOptionPane.YES_OPTION) {
			if (hangHoaDAO.xoaHangHoa(txtMaHH.getText())) {
				JOptionPane.showMessageDialog(this, "Đã cập nhật!");
				clearForm();
			}
		}
	}

    private DocumentListener timKiemDong() {
        return new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { thucHienTimKiem(); }
            @Override public void removeUpdate(DocumentEvent e) { thucHienTimKiem(); }
            @Override public void changedUpdate(DocumentEvent e) { thucHienTimKiem(); }
            private void thucHienTimKiem() {
            	dsHH = hangHoaDAO.timKiemHangHoa(txtTimKiem.getText().trim());
    			reloadData();
            }
        };
    }

	private void clearForm() {
		txtMaHH.setEditable(true); txtMaHH.setText(""); txtMaVach.setText("");
		txtTenHH.setText(""); txtGiaSP.setText(""); txtHinhAnhPath.setText("");
		lblHinhAnh.setIcon(null); lblHinhAnh.setText("No Image");
		loadData();
	}

//	private ImageIcon createImageIcon(String fileName, int width, int height) {
//		try {
//			if (fileName != null && !fileName.trim().isEmpty()) {
//				java.net.URL imgURL = getClass().getResource("/Resource/HangHoa/" + fileName);
//				if (imgURL != null) {
//					return new ImageIcon(new ImageIcon(imgURL).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
//				}
//			}
//		} catch (Exception e) {}
//		return null;
//	}
//	
	
	private ImageIcon createImageIcon(String fileName, int width, int height) {
	    try {
	        if (fileName != null && !fileName.trim().isEmpty()) {
	        
	            java.net.URL imgURL = getClass().getResource("/Resource/HangHoa/" + fileName);
	            if (imgURL != null) {
	                ImageIcon icon = new ImageIcon(imgURL);
	             
	                Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
	                return new ImageIcon(scaledImage);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	class ImageRenderer extends JLabel implements TableCellRenderer {
		private static final long serialVersionUID = 1L;
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			setIcon((value instanceof ImageIcon) ? (ImageIcon) value : null);
			setText((value instanceof ImageIcon) ? "" : "No Image");
			setHorizontalAlignment(JLabel.CENTER);
			if (isSelected) { setBackground(table.getSelectionBackground()); setOpaque(true); } else setOpaque(false);
			return this;
		}
	}
}