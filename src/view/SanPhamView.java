package view;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
/**
 *
 * @author LHH05
 */
import java.text.DecimalFormat;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import jdbc.DbConnection;
import model.ChatLieu;
import model.MauSac;
import model.SanPham;
import model.ThuongHieu;
import repository.ChatLieuRepository;
import repository.SanPhamChiTietRepository;
import repository.SanPhamRepository;
import repository.ThuongHieuRepository;

public class SanPhamView extends javax.swing.JPanel {

    SanPhamRepository spRepo = new SanPhamRepository();
    SanPhamChiTietRepository spctRepo = new SanPhamChiTietRepository();
    ChatLieuRepository clRepo = new ChatLieuRepository();
    ThuongHieuRepository thRepo = new ThuongHieuRepository();
    DecimalFormat df = new DecimalFormat("#,###");

    public void loadChatLieu() {
        try {
            Connection con = DbConnection.getConnection();
            String sql = "SELECT ten_chat_lieu FROM chat_lieu";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            cbChatLieu.removeAllItems();

            while (rs.next()) {
                cbChatLieu.addItem(rs.getString("ten_chat_lieu"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadThuongHieu() {
        try {
            Connection con = DbConnection.getConnection();
            String sql = "SELECT ten_thuong_hieu FROM thuong_hieu";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            cbThuongHieu.removeAllItems();

            while (rs.next()) {
                cbThuongHieu.addItem(rs.getString("ten_thuong_hieu"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadTable() {

        DefaultTableModel model = (DefaultTableModel) tblSanPham.getModel();
        model.setRowCount(0);

        String sql = """
        SELECT 
        v.id,
        v.ma_sp,
        v.ten_ao,
        v.mo_ta,
        v.ngay_tao,
        cl.ten_chat_lieu,
        th.ten_thuong_hieu,
        SUM(ct.so_luong) AS so_luong,
        v.gia_goc,
        v.gia_thuc_te,
        v.ten_dot
        FROM v_san_pham_ban_hang v
        LEFT JOIN quan_ao_chi_tiet ct ON v.id = ct.quan_ao_id
        LEFT JOIN chat_lieu cl
            ON v.chat_lieu_id = cl.id
        LEFT JOIN thuong_hieu th
            ON v.thuong_hieu_id = th.id
        GROUP BY v.id, v.ma_sp, v.ten_ao, v.mo_ta, v.ngay_tao, cl.ten_chat_lieu, th.ten_thuong_hieu, v.gia_goc, v.gia_thuc_te, v.ten_dot
        """;

        try (
                Connection con = DbConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery();) {

            while (rs.next()) {
                String tt = rs.getInt("so_luong") > 0 ? "Còn hàng" : "Hết hàng";

                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("ma_sp"),
                    rs.getString("ten_ao"),
                    rs.getString("mo_ta"),
                    rs.getDate("ngay_tao"),
                    rs.getString("ten_chat_lieu"),
                    rs.getString("ten_thuong_hieu"),
                    rs.getInt("so_luong"),
                    df.format(rs.getBigDecimal("gia_thuc_te")),
                    rs.getString("ten_dot") == null ? "Không có" : rs.getString("ten_dot"),
                    tt
                };

                model.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean checkTrungMaSP(String maSP) {
        try {
            Connection con = DbConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM quan_ao WHERE ma_sp = ?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, maSP);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                if (rs.getInt(1) > 0) {
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean validateForm() {

        if (checkTrungMaSP(txtMasp.getText())) {
            JOptionPane.showMessageDialog(this, "Mã sản phẩm đã tồn tại");
            txtMasp.requestFocus();
            return false;
        }

        if (txtMasp.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã sản phẩm không được để trống");
            txtMasp.requestFocus();
            return false;
        }

        if (txtTensp.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên sản phẩm không được để trống");
            txtTensp.requestFocus();
            return false;
        }

        if (dateNgayTao.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày tạo");
            return false;
        }

        if (cbChatLieu.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chất liệu");
            return false;
        }

        if (cbThuongHieu.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thương hiệu");
            return false;
        }

        if (txtGiaBan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giá bán không được để trống");
            txtGiaBan.requestFocus();
            return false;
        }

        try {
            double giaBan = Double.parseDouble(txtGiaBan.getText().replace(",", ""));
            if (giaBan <= 0) {
                JOptionPane.showMessageDialog(this, "Giá phải lớn hơn 0!");
                txtGiaBan.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá bán phải là số, không được nhập chữ");
            return false;
        }

        return true;
    }

    private void suaQuanAo() {
        int row = tblSanPham.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần sửa!");
            return;
        }

        int id = (int) tblSanPham.getValueAt(row, 0);
        String maSP = txtMasp.getText();
        String tenAo = txtTensp.getText();
        String moTa = txtMoTa.getText();
        java.util.Date utilDate = dateNgayTao.getDate();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        int chatLieuId = cbChatLieu.getSelectedIndex() + 1;
        int thuongHieuId = cbThuongHieu.getSelectedIndex() + 1;
        double giaBan = Double.parseDouble(txtGiaBan.getText().replace(",", ""));

        String sql = "UPDATE quan_ao SET ma_sp=?, ten_ao=?, mo_ta=?, ngay_tao=?, chat_lieu_id=?, thuong_hieu_id=? , gia_ban=? WHERE id=?";

        try (Connection con = DbConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maSP);
            ps.setString(2, tenAo);
            ps.setString(3, moTa);
            ps.setDate(4, sqlDate);
            ps.setInt(5, chatLieuId);
            ps.setInt(6, thuongHieuId);
            ps.setDouble(7, giaBan);
            ps.setInt(8, id);

            int kq = ps.executeUpdate();
            if (kq > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadTable();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void timTheoTen() {
        DefaultTableModel model = (DefaultTableModel) tblSanPham.getModel();
        model.setRowCount(0);

        String ten = txtTimKiem.getText();

        try {
            Connection con = DbConnection.getConnection();

            String sql = """
        SELECT 
                 qa.id,
                 qa.ma_sp,
                 qa.ten_ao,
                 qa.mo_ta,
                 qa.ngay_tao,
                 cl.ten_chat_lieu,
                 th.ten_thuong_hieu,
                 SUM(ct.so_luong) AS so_luong,
                 qa.gia_ban,
                 qa.trang_thai
                 FROM quan_ao qa
                 LEFT JOIN quan_ao_chi_tiet ct 
                     ON qa.id = ct.quan_ao_id
                 LEFT JOIN chat_lieu cl
                     ON qa.chat_lieu_id = cl.id
                 LEFT JOIN thuong_hieu th
                     ON qa.thuong_hieu_id = th.id
                 
                 WHERE qa.ten_ao COLLATE Vietnamese_CI_AI LIKE ?
                 
                 GROUP BY 
                 qa.id,
                 qa.ma_sp,
                 qa.ten_ao,
                 qa.mo_ta,
                 qa.ngay_tao,
                 cl.ten_chat_lieu,
                 th.ten_thuong_hieu,
                 qa.gia_ban,
                 qa.trang_thai
        """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + ten + "%");

            ResultSet rs = ps.executeQuery();
            boolean coDuLieu = false;

            while (rs.next()) {
                coDuLieu = true;
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("ma_sp"),
                    rs.getString("ten_ao"),
                    rs.getString("mo_ta"),
                    rs.getDate("ngay_tao"),
                    rs.getString("ten_chat_lieu"),
                    rs.getString("ten_thuong_hieu"),
                    rs.getInt("so_luong"),
                    df.format(rs.getDouble("gia_ban"))
                });
            }
            if (!coDuLieu) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void timTheoMa() {
        DefaultTableModel model = (DefaultTableModel) tblSanPham.getModel();
        model.setRowCount(0);

        String ma = txtTimKiem.getText();

        try {
            Connection con = DbConnection.getConnection();
            String sql = """
        SELECT 
        qa.id,
        qa.ma_sp,
        qa.ten_ao,
        qa.mo_ta,
        qa.ngay_tao,
        cl.ten_chat_lieu,
        th.ten_thuong_hieu,
        SUM(ct.so_luong) AS so_luong,
        qa.gia_ban,
        qa.trang_thai
        FROM quan_ao qa
        LEFT JOIN quan_ao_chi_tiet ct 
            ON qa.id = ct.quan_ao_id
        LEFT JOIN chat_lieu cl
            ON qa.chat_lieu_id = cl.id
        LEFT JOIN thuong_hieu th
            ON qa.thuong_hieu_id = th.id

        WHERE qa.ma_sp LIKE ?

        GROUP BY 
        qa.id,
        qa.ma_sp,
        qa.ten_ao,
        qa.mo_ta,
        qa.ngay_tao,
        cl.ten_chat_lieu,
        th.ten_thuong_hieu,
        qa.gia_ban,
        qa.trang_thai
        """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + ma + "%");

            ResultSet rs = ps.executeQuery();
            boolean coDuLieu = false;

            while (rs.next()) {
                coDuLieu = true;
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("ma_sp"),
                    rs.getString("ten_ao"),
                    rs.getString("mo_ta"),
                    rs.getDate("ngay_tao"),
                    rs.getString("ten_chat_lieu"),
                    rs.getString("ten_thuong_hieu"),
                    rs.getInt("so_luong"),
                    df.format(rs.getDouble("gia_ban"))
                });
            }
            if (!coDuLieu) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMaSp() {
        String maSp = txtMasp.getText();
        return maSp;
    }

    public void resetForm() {
        txtMasp.setText("");
        txtMoTa.setText("");
        txtTensp.setText("");
        dateNgayTao.setDate(null);
        cbChatLieu.setSelectedIndex(0);
        cbChatLieu.setSelectedIndex(0);
        txtGiaBan.setText("");
        txtTimKiem.setText("");
        loadTable();
    }

    private void UI1() {
        // Đổi layout header thành BorderLayout
        pnHeader.setLayout(new java.awt.BorderLayout());

        // Căn giữa label
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        // Xóa hết component cũ (nếu có)
        pnHeader.removeAll();

        // Thêm lại label vào giữa
        pnHeader.add(jLabel1, java.awt.BorderLayout.CENTER);

        // Cập nhật UI
        pnHeader.revalidate();
        pnHeader.repaint();
    }

    /**
     * Creates new form ProductView
     */
    Date date = Date.valueOf(LocalDate.now());

    public SanPhamView() {
        initComponents();
        loadChatLieu();
        loadThuongHieu();
        loadTable();
        dateNgayTao.setDate(date);
        UI1();
        dateNgayTao.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnHeader = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        pnThemSanPham = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtMasp = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtTensp = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        dateNgayTao = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        cbChatLieu = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        cbThuongHieu = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtMoTa = new javax.swing.JTextArea();
        btnThem = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnLamMoi = new javax.swing.JButton();
        btnSanPhamChiTiet = new javax.swing.JButton();
        txtTimKiem = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtGiaBan = new javax.swing.JTextField();
        btnTimTen = new javax.swing.JButton();
        btnTimMa = new javax.swing.JButton();
        pnTable = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSanPham = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        pnHeader.setBackground(new java.awt.Color(74, 144, 226));
        pnHeader.setPreferredSize(new java.awt.Dimension(1015, 60));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("QUẢN LÝ SẢN PHẨM");

        javax.swing.GroupLayout pnHeaderLayout = new javax.swing.GroupLayout(pnHeader);
        pnHeader.setLayout(pnHeaderLayout);
        pnHeaderLayout.setHorizontalGroup(
            pnHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnHeaderLayout.createSequentialGroup()
                .addGap(363, 363, 363)
                .addComponent(jLabel1)
                .addContainerGap(433, Short.MAX_VALUE))
        );
        pnHeaderLayout.setVerticalGroup(
            pnHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        add(pnHeader, java.awt.BorderLayout.PAGE_START);

        pnThemSanPham.setBackground(new java.awt.Color(245, 247, 250));
        pnThemSanPham.setPreferredSize(new java.awt.Dimension(270, 550));

        jLabel2.setText("Mã Sản Phẩm :");

        jLabel3.setText("Tên Sản Phẩm :");

        jLabel4.setText("Ngày Tạo:");

        jLabel5.setText("Chất Liệu :");

        cbChatLieu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel6.setText("Thương Hiệu :");

        cbThuongHieu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel7.setText("Mô Tả :");

        txtMoTa.setColumns(20);
        txtMoTa.setRows(5);
        jScrollPane1.setViewportView(txtMoTa);

        btnThem.setText("Thêm");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        btnXoa.setText("Xóa");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        btnSua.setText("Sửa");
        btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaActionPerformed(evt);
            }
        });

        btnLamMoi.setText("Làm Mới");
        btnLamMoi.setMaximumSize(new java.awt.Dimension(80, 23));
        btnLamMoi.setMinimumSize(new java.awt.Dimension(80, 23));
        btnLamMoi.setPreferredSize(new java.awt.Dimension(80, 23));
        btnLamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoiActionPerformed(evt);
            }
        });

        btnSanPhamChiTiet.setText("Sản Phẩm Chi Tiết");
        btnSanPhamChiTiet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSanPhamChiTietActionPerformed(evt);
            }
        });

        jLabel8.setText("Giá bán:");

        btnTimTen.setText("Tìm theo tên");
        btnTimTen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimTenActionPerformed(evt);
            }
        });

        btnTimMa.setText("Tìm theo mã");
        btnTimMa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimMaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnThemSanPhamLayout = new javax.swing.GroupLayout(pnThemSanPham);
        pnThemSanPham.setLayout(pnThemSanPhamLayout);
        pnThemSanPhamLayout.setHorizontalGroup(
            pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnThemSanPhamLayout.createSequentialGroup()
                .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnThemSanPhamLayout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(btnSanPhamChiTiet))
                    .addGroup(pnThemSanPhamLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel8)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addComponent(txtMasp)
                            .addComponent(jLabel2)
                            .addComponent(txtTensp)
                            .addComponent(dateNgayTao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbChatLieu, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbThuongHieu, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnThemSanPhamLayout.createSequentialGroup()
                                .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtTimKiem)
                            .addComponent(txtGiaBan)
                            .addGroup(pnThemSanPhamLayout.createSequentialGroup()
                                .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnThem, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                    .addComponent(btnTimTen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnTimMa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnSua, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE))))))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        pnThemSanPhamLayout.setVerticalGroup(
            pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnThemSanPhamLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMasp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTensp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateNgayTao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbChatLieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbThuongHieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtGiaBan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnThemSanPhamLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnTimTen))
                    .addComponent(btnTimMa))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSua, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnThem, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnXoa)
                    .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSanPhamChiTiet)
                .addContainerGap())
        );

        add(pnThemSanPham, java.awt.BorderLayout.LINE_START);

        tblSanPham.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Mã Sản Phẩm ", "Tên Sản Phẩm", "Mô tả", "Ngày Tạo", "Chất Liệu", "Thương Hiệu", "Số Lượng", "Giá Bán", "Đợt giảm giá", "Trạng Thái"
            }
        ));
        tblSanPham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSanPhamMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblSanPham);

        javax.swing.GroupLayout pnTableLayout = new javax.swing.GroupLayout(pnTable);
        pnTable.setLayout(pnTableLayout);
        pnTableLayout.setHorizontalGroup(
            pnTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 795, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnTableLayout.setVerticalGroup(
            pnTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(pnTable, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Bạn có chắc chắn muốn thêm sản phẩm này không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (!validateForm()) {
                return;
            }
            try {
                Connection con = DbConnection.getConnection();

                String sql = "INSERT INTO quan_ao(ma_sp, ten_ao, mo_ta, trang_thai, ngay_tao, chat_lieu_id, thuong_hieu_id, gia_ban) VALUES (?,?,?,?,?,?,?,?)";

                PreparedStatement ps = con.prepareStatement(sql);

                ps.setString(1, txtMasp.getText());
                ps.setString(2, txtTensp.getText());
                ps.setString(3, txtMoTa.getText());

                ps.setInt(4, 2);

                java.util.Date utilDate = dateNgayTao.getDate();
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                ps.setDate(5, sqlDate);

                ps.setInt(6, cbChatLieu.getSelectedIndex() + 1);
                ps.setInt(7, cbThuongHieu.getSelectedIndex() + 1);
                ps.setDouble(8, Double.parseDouble(txtGiaBan.getText().replace(",", "")));

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công");
                loadTable();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }//GEN-LAST:event_btnThemActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        int row = tblSanPham.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        int id = Integer.parseInt(tblSanPham.getValueAt(row, 0).toString());

        try {

            Connection con = DbConnection.getConnection();
            String sql = "DELETE FROM quan_ao WHERE id = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Xóa thành công");

            loadTable();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnXoaActionPerformed

    private void tblSanPhamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSanPhamMouseClicked
        int row = tblSanPham.getSelectedRow();

        txtMasp.setText(tblSanPham.getValueAt(row, 1).toString());
        txtTensp.setText(tblSanPham.getValueAt(row, 2).toString());
        txtMoTa.setText(tblSanPham.getValueAt(row, 3).toString());
        Date ngay = (Date) tblSanPham.getValueAt(row, 4);
        dateNgayTao.setDate(ngay);

        cbChatLieu.setSelectedItem(tblSanPham.getValueAt(row, 5).toString());
        cbThuongHieu.setSelectedItem(tblSanPham.getValueAt(row, 6).toString());
        txtGiaBan.setText(tblSanPham.getValueAt(row, 8).toString());
    }//GEN-LAST:event_tblSanPhamMouseClicked

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Bạn có chắc muốn sửa sản phẩm này không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            suaQuanAo();
            JOptionPane.showMessageDialog(null, "Sửa sản phẩm thành công!");
        }
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnSanPhamChiTietActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSanPhamChiTietActionPerformed
        int row = tblSanPham.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!");
            return;
        }

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);

        int id = Integer.parseInt(tblSanPham.getValueAt(row, 0).toString());

        SanPhamChiTietView nj = new SanPhamChiTietView(parent, true, id);
        nj.setLocationRelativeTo(null);
        nj.setVisible(true);
        loadTable();
    }//GEN-LAST:event_btnSanPhamChiTietActionPerformed

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        resetForm();
    }//GEN-LAST:event_btnLamMoiActionPerformed

    private void btnTimTenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimTenActionPerformed
        timTheoTen();
    }//GEN-LAST:event_btnTimTenActionPerformed

    private void btnTimMaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimMaActionPerformed
        timTheoMa();
    }//GEN-LAST:event_btnTimMaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnSanPhamChiTiet;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnTimMa;
    private javax.swing.JButton btnTimTen;
    private javax.swing.JButton btnXoa;
    private javax.swing.JComboBox<String> cbChatLieu;
    private javax.swing.JComboBox<String> cbThuongHieu;
    private com.toedter.calendar.JDateChooser dateNgayTao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel pnHeader;
    private javax.swing.JPanel pnTable;
    private javax.swing.JPanel pnThemSanPham;
    private javax.swing.JTable tblSanPham;
    private javax.swing.JTextField txtGiaBan;
    private javax.swing.JTextField txtMasp;
    private javax.swing.JTextArea txtMoTa;
    private javax.swing.JTextField txtTensp;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables
}
