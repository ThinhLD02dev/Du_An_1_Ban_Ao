package view;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
/**
 *
 * @author LHH05
 */
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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
        qa.id,
        qa.ma_sp,
        qa.ten_ao,
        qa.mo_ta,
        qa.ngay_tao,
        cl.ten_chat_lieu,
        th.ten_thuong_hieu,
        SUM(ct.so_luong) AS so_luong,
        qa.trang_thai
        FROM quan_ao qa
        LEFT JOIN quan_ao_chi_tiet ct 
            ON qa.id = ct.quan_ao_id
        LEFT JOIN chat_lieu cl
            ON qa.chat_lieu_id = cl.id
        LEFT JOIN thuong_hieu th
            ON qa.thuong_hieu_id = th.id
        
        GROUP BY 
        qa.id,
        qa.ma_sp,
        qa.ten_ao,
        qa.mo_ta,
        qa.ngay_tao,
        cl.ten_chat_lieu,
        th.ten_thuong_hieu,
        qa.trang_thai
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

        if (txtMoTa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mô tả không được để trống");
            txtMoTa.requestFocus();
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

        String sql = "UPDATE quan_ao SET ma_sp=?, ten_ao=?, mo_ta=?, ngay_tao=?, chat_lieu_id=?, thuong_hieu_id=? WHERE id=?";

        try (Connection con = DbConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maSP);
            ps.setString(2, tenAo);
            ps.setString(3, moTa);
            ps.setDate(4, sqlDate);
            ps.setInt(5, chatLieuId);
            ps.setInt(6, thuongHieuId);
            ps.setInt(7, id);

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
        loadTable();
    }

    private void setupUI() {
        // ===== HEADER =====
        pnHeader.setBackground(new java.awt.Color(74, 144, 226));
        jLabel1.setText("PRODUCT MANAGEMENT");
        jLabel1.setForeground(java.awt.Color.WHITE);
        jLabel1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        // ===== PANEL FORM =====
        pnThemSanPham.setBackground(new java.awt.Color(245, 247, 250));

        // ===== BUTTON STYLE =====
        styleButton(btnThem, new java.awt.Color(76, 175, 80));   // xanh lá
        styleButton(btnXoa, new java.awt.Color(244, 67, 54));    // đỏ
        styleButton(btnSua, new java.awt.Color(255, 193, 7));    // vàng
        styleButton(btnLamMoi, new java.awt.Color(158, 158, 158)); // xám
        styleButton(btnSanPhamChiTiet, new java.awt.Color(33, 150, 243)); // xanh dương

        // ===== FONT INPUT =====
        java.awt.Font font = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14);

        txtMasp.setFont(font);
        txtTensp.setFont(font);
        txtMoTa.setFont(font);
        cbChatLieu.setFont(font);
        cbThuongHieu.setFont(font);

        // ===== TABLE STYLE =====
        tblSanPham.setRowHeight(25);
        tblSanPham.setFont(font);
        tblSanPham.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));

        // ===== BORDER / SPACING =====
        txtMasp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));
        txtTensp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));
        txtMoTa.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));

        // ===== BUTTON PADDING =====
        btnThem.setFocusPainted(false);
        btnXoa.setFocusPainted(false);
        btnSua.setFocusPainted(false);
        btnLamMoi.setFocusPainted(false);
        btnSanPhamChiTiet.setFocusPainted(false);
    }

    private void styleButton(javax.swing.JButton btn, java.awt.Color color) {
        btn.setBackground(color);
        btn.setForeground(java.awt.Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
    }

    /**
     * Creates new form ProductView
     */
    java.util.Date date = java.sql.Date.valueOf(LocalDate.now());
    public SanPhamView() {
        initComponents();
        setupUI();
        loadChatLieu();
        loadThuongHieu();
        loadTable();
        dateNgayTao.setDate(date);
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
        pnTable = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSanPham = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        pnHeader.setBackground(new java.awt.Color(74, 144, 226));
        pnHeader.setPreferredSize(new java.awt.Dimension(1015, 60));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("PRODUCCT");

        javax.swing.GroupLayout pnHeaderLayout = new javax.swing.GroupLayout(pnHeader);
        pnHeader.setLayout(pnHeaderLayout);
        pnHeaderLayout.setHorizontalGroup(
            pnHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnHeaderLayout.createSequentialGroup()
                .addGap(379, 379, 379)
                .addComponent(jLabel1)
                .addContainerGap(489, Short.MAX_VALUE))
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

        javax.swing.GroupLayout pnThemSanPhamLayout = new javax.swing.GroupLayout(pnThemSanPham);
        pnThemSanPham.setLayout(pnThemSanPhamLayout);
        pnThemSanPhamLayout.setHorizontalGroup(
            pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnThemSanPhamLayout.createSequentialGroup()
                .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnThemSanPhamLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
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
                                .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnThem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnXoa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnLamMoi, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                                    .addComponent(btnSua, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(pnThemSanPhamLayout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addComponent(btnSanPhamChiTiet)))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        pnThemSanPhamLayout.setVerticalGroup(
            pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnThemSanPhamLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMasp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTensp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(dateNgayTao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(cbChatLieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(cbThuongHieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThem)
                    .addComponent(btnSua))
                .addGap(39, 39, 39)
                .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnXoa)
                    .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnSanPhamChiTiet)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        add(pnThemSanPham, java.awt.BorderLayout.LINE_START);

        tblSanPham.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Mã Sản Phẩm ", "Tên Sản Phẩm", "Mô tả", "Ngày Tạo", "Chất Liệu", "Thương Hiệu", "Số Lượng", "Trạng Thái"
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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 733, Short.MAX_VALUE)
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

                String sql = "INSERT INTO quan_ao(ma_sp, ten_ao, mo_ta, trang_thai, ngay_tao, chat_lieu_id, thuong_hieu_id) VALUES (?,?,?,?,?,?,?)";

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnSanPhamChiTiet;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel pnHeader;
    private javax.swing.JPanel pnTable;
    private javax.swing.JPanel pnThemSanPham;
    private javax.swing.JTable tblSanPham;
    private javax.swing.JTextField txtMasp;
    private javax.swing.JTextArea txtMoTa;
    private javax.swing.JTextField txtTensp;
    // End of variables declaration//GEN-END:variables
}
