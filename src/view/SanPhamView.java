/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        Connection con = DbConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
    ) {

        while (rs.next()) {
            String tt = rs.getInt("so_luong") >0 ? "Còn hàng" : "Hết hàng";

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
    java.util.Date utilDate =  dateNgayTao.getDate();
    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
    int chatLieuId = cbChatLieu.getSelectedIndex()+1;
    int thuongHieuId = cbThuongHieu.getSelectedIndex()+1;

    String sql = "UPDATE quan_ao SET ma_sp=?, ten_ao=?, mo_ta=?, ngay_tao=?, chat_lieu_id=?, thuong_hieu_id=? WHERE id=?";

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

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
    public String getMaSp(){
        String maSp = txtMasp.getText();
        return maSp;
    }
    
    public void resetForm(){
        txtMasp.setText("");
        txtMoTa.setText("");
        txtTensp.setText("");
        dateNgayTao.setDate(null);
        cbChatLieu.setSelectedIndex(0);
        cbChatLieu.setSelectedIndex(0);
        loadTable();
    }
    
    
    /**
     * Creates new form SanPham
     */
    public SanPhamView() {
        initComponents();
        loadChatLieu();
        loadThuongHieu();
        loadTable();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSanPham = new javax.swing.JTable();
        pnThemSanPham = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtMasp = new javax.swing.JTextField();
        txtTensp = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtMoTa = new javax.swing.JTextArea();
        cbChatLieu = new javax.swing.JComboBox<>();
        cbThuongHieu = new javax.swing.JComboBox<>();
        btnThem = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnSanPhamChiTiet = new javax.swing.JButton();
        dateNgayTao = new com.toedter.calendar.JDateChooser();
        btnLamMoi = new javax.swing.JButton();

        jPanel1.setPreferredSize(new java.awt.Dimension(900, 600));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setText("SẢN PHẨM");

        tblSanPham.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Mã sản phẩm", "Tên sản phẩm", "Mô tả", "Ngày tạo", "Chất Liệu", "Thương Hiệu", "Số Lượng", "Trạng Thái"
            }
        ));
        tblSanPham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSanPhamMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblSanPham);

        pnThemSanPham.setPreferredSize(new java.awt.Dimension(850, 270));

        jLabel2.setText("Mã Sản Phẩm");

        jLabel3.setText("Tên Sản Phẩm");

        jLabel4.setText("Mô Tả");

        jLabel5.setText("Ngày Tạo");

        jLabel6.setText("Chất Liệu");

        jLabel7.setText("Thương hiệu");

        txtMoTa.setColumns(20);
        txtMoTa.setRows(5);
        jScrollPane2.setViewportView(txtMoTa);

        cbChatLieu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbThuongHieu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnThem.setText("THÊM");
        btnThem.setPreferredSize(new java.awt.Dimension(70, 25));
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        btnSua.setText("SỬA");
        btnSua.setPreferredSize(new java.awt.Dimension(70, 25));
        btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaActionPerformed(evt);
            }
        });

        btnXoa.setText("XÓA");
        btnXoa.setPreferredSize(new java.awt.Dimension(70, 25));
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        btnSanPhamChiTiet.setText("Sản phẩm chi tiết");
        btnSanPhamChiTiet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSanPhamChiTietActionPerformed(evt);
            }
        });

        btnLamMoi.setText("LÀM MỚI");
        btnLamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnThemSanPhamLayout = new javax.swing.GroupLayout(pnThemSanPham);
        pnThemSanPham.setLayout(pnThemSanPhamLayout);
        pnThemSanPhamLayout.setHorizontalGroup(
            pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnThemSanPhamLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(35, 35, 35)
                .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTensp)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                    .addComponent(txtMasp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel5))
                .addGap(31, 31, 31)
                .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cbChatLieu, 0, 260, Short.MAX_VALUE)
                    .addComponent(cbThuongHieu, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dateNgayTao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(26, 26, 26))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnThemSanPhamLayout.createSequentialGroup()
                .addContainerGap(180, Short.MAX_VALUE)
                .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnThemSanPhamLayout.createSequentialGroup()
                        .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(109, 109, 109)
                        .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(91, 91, 91)
                        .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(80, 80, 80)
                        .addComponent(btnLamMoi)
                        .addGap(100, 100, 100))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnThemSanPhamLayout.createSequentialGroup()
                        .addComponent(btnSanPhamChiTiet)
                        .addGap(347, 347, 347))))
        );
        pnThemSanPhamLayout.setVerticalGroup(
            pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnThemSanPhamLayout.createSequentialGroup()
                .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnThemSanPhamLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtMasp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtTensp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnThemSanPhamLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(dateNgayTao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(cbChatLieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(cbThuongHieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(26, 26, 26)
                .addGroup(pnThemSanPhamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLamMoi))
                .addGap(18, 18, 18)
                .addComponent(btnSanPhamChiTiet)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(340, 340, 340))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(pnThemSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnThemSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        // TODO add your handling code here:
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

        java.util.Date utilDate =  dateNgayTao.getDate();
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
    }//GEN-LAST:event_btnThemActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        // TODO add your handling code here:
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
        // TODO add your handling code here:
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
        // TODO add your handling code here:
        suaQuanAo();
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnSanPhamChiTietActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSanPhamChiTietActionPerformed
        // TODO add your handling code here:
        // khởi tạo tableSanPham và dữ liệu
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(jLabel1);

    int row = tblSanPham.getSelectedRow();
    int id = Integer.parseInt(tblSanPham.getValueAt(row, 0).toString());

    SanPhamChiTietView nj = new SanPhamChiTietView(parent, true, id);
    nj.setLocationRelativeTo(null);
    nj.setVisible(true);
    }//GEN-LAST:event_btnSanPhamChiTietActionPerformed

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        // TODO add your handling code here:
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel pnThemSanPham;
    private javax.swing.JTable tblSanPham;
    private javax.swing.JTextField txtMasp;
    private javax.swing.JTextArea txtMoTa;
    private javax.swing.JTextField txtTensp;
    // End of variables declaration//GEN-END:variables
}
