/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package view;

import java.awt.Dimension;
import java.awt.Frame;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import jdbc.DbConnection;
import java.util.logging.Logger;

/**
 *
 * @author LHH05
 */
public class SanPhamChiTietView extends javax.swing.JDialog {

    private static final Logger logger = Logger.getLogger(SanPhamChiTietView.class.getName());

    /**
     * Creates new form SanPhamChiTietView
     */
    private int sanPhamId;

    public void loadMaSp() {
        String sql = "SELECT ma_sp FROM quan_ao WHERE id=?";
        try {
            Connection con = DbConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, sanPhamId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                txtMasp.setText(rs.getString("ma_sp"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean validateForm() {

        if (txtSoLuong.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Số lượng không được để trống");
            txtMasp.requestFocus();
            return false;
        }

        if (txtGiaBan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giá bán không được để trống");
            txtMasp.requestFocus();
            return false;
        }

        if (cbKichThuoc.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chất liệu");
            return false;
        }

        if (cbMauSac.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thương hiệu");
            return false;
        }

        return true;
    }

    public void loadMauSac() {
        try {
            Connection con = DbConnection.getConnection();
            String sql = "SELECT ten_mau FROM mau_sac";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            cbMauSac.removeAllItems();

            while (rs.next()) {
                cbMauSac.addItem(rs.getString("ten_mau"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadKichThuoc() {
        try {
            Connection con = DbConnection.getConnection();
            String sql = "SELECT ten_kich_thuoc FROM kich_thuoc";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            cbKichThuoc.removeAllItems();

            while (rs.next()) {
                cbKichThuoc.addItem(rs.getString("ten_kich_thuoc"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTableSPCT() {

        DefaultTableModel model = (DefaultTableModel) tblSpct.getModel();
        model.setRowCount(0);

        String sql = """
    SELECT 
    spct.id,
    spct.ma_spct,
    spct.so_luong,
    ms.ten_mau,
    spct.gia_ban,
    kt.ten_kich_thuoc
    FROM quan_ao_chi_tiet spct
    JOIN quan_ao qa ON spct.quan_ao_id = qa.id
    JOIN mau_sac ms ON spct.mau_sac_id = ms.id
    JOIN kich_thuoc kt ON spct.kich_thuoc_id = kt.id
    WHERE spct.quan_ao_id = ?
    """;

        try {
            Connection con = DbConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, sanPhamId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("ma_spct"),
                    rs.getInt("so_luong"),
                    rs.getString("ten_mau"),
                    rs.getDouble("gia_ban"),
                    rs.getString("ten_kich_thuoc")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadForm() {
        String sql = """
            SELECT 
            spct.id,
            spct.ma_spct,
            spct.so_luong,
            ms.ten_mau,
            spct.gia_ban,
            kt.ten_kich_thuoc
            FROM quan_ao_chi_tiet spct
            JOIN quan_ao qa ON spct.quan_ao_id = qa.id
            JOIN mau_sac ms ON spct.mau_sac_id = ms.id
            JOIN kich_thuoc kt ON spct.kich_thuoc_id = kt.id
            WHERE spct.quan_ao_id = ?
            """;

        try {
            Connection con = DbConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, sanPhamId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                txtMasp.setText(rs.getString("ma_spct"));
                txtSoLuong.setText(rs.getString("so_luong"));
                txtGiaBan.setText(rs.getString("gia_ban"));
                cbKichThuoc.setSelectedItem(rs.getString("ten_kich_thuoc"));
                cbMauSac.setSelectedItem(rs.getString("ten_mau"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void suaSpct() {
        int row = tblSpct.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần sửa!");
            return;
        }

        int id = (int) tblSpct.getValueAt(row, 0);
        int soLuong = Integer.parseInt(txtSoLuong.getText());
        int mauSac = cbMauSac.getSelectedIndex() + 1;
        int giaBan = Integer.parseInt(txtGiaBan.getText());
        int kichThuoc = cbKichThuoc.getSelectedIndex() + 1;

        String sql = "UPDATE quan_ao_chi_tiet SET so_luong=?, mau_sac_id=?, gia_ban=?, kich_thuoc_id=? WHERE id=?";

        try (Connection con = DbConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, soLuong);
            ps.setInt(2, mauSac);
            ps.setInt(3, giaBan);
            ps.setInt(4, kichThuoc);
            ps.setInt(5, id);

            int kq = ps.executeUpdate();
            if (kq > 0) {
                JOptionPane.showMessageDialog(this, "Sửa thành công!");
                loadTableSPCT();
            } else {
                JOptionPane.showMessageDialog(this, "Sửa thất bại!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkTrungMaSP(String maSP) {
        try {
            Connection con = DbConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM quan_ao_chi_tiet WHERE ma_spct = ?";
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

    public boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean validateFormSpct() {

        if (checkTrungMaSP(txtMasp.getText())) {
            JOptionPane.showMessageDialog(this, "Mã sản phẩm đã tồn tại");
            return false;
        }
        if (txtGiaBan.getText() == null) {
            JOptionPane.showMessageDialog(this, "Không được để trống giá bán");
        }

        try {
            double giaBan = Double.parseDouble(txtGiaBan.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá bán phải là số, không được nhập chữ");
        }

        double gia = Double.parseDouble(txtGiaBan.getText());
        if (gia <= 0) {
            JOptionPane.showMessageDialog(this, "Giá phải lớn hơn 0!");
            txtGiaBan.requestFocus();
        }

        try {
            double soLuong = Double.parseDouble(txtSoLuong.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số, không được nhập chữ");
        }

        double sl = Double.parseDouble(txtSoLuong.getText());
        if (sl < 0) {
            JOptionPane.showMessageDialog(this, "Giá phải lớn hơn 0!");
            txtGiaBan.requestFocus();
        }

        if (cbMauSac.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn màu sắc");
            return false;
        }

        if (cbKichThuoc.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn kích thước");
            return false;
        }

        return true;
    }

    public SanPhamChiTietView(Frame parent, boolean modal, int id) {
        super(parent, modal);
        initComponents();
        this.sanPhamId = id;
        loadForm();
        loadTableSPCT();
        loadKichThuoc();
        loadMauSac();
        loadMaSp();
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
        pnLeft = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtMasp = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cbMauSac = new javax.swing.JComboBox<>();
        cbKichThuoc = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        txtGiaBan = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtSoLuong = new javax.swing.JTextField();
        btnThem = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        pnMain = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSpct = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pnHeader.setBackground(new java.awt.Color(74, 144, 226));
        pnHeader.setPreferredSize(new java.awt.Dimension(1002, 60));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("PRODUCT DETAILS");

        javax.swing.GroupLayout pnHeaderLayout = new javax.swing.GroupLayout(pnHeader);
        pnHeader.setLayout(pnHeaderLayout);
        pnHeaderLayout.setHorizontalGroup(
            pnHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnHeaderLayout.createSequentialGroup()
                .addGap(342, 342, 342)
                .addComponent(jLabel1)
                .addContainerGap(417, Short.MAX_VALUE))
        );
        pnHeaderLayout.setVerticalGroup(
            pnHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        getContentPane().add(pnHeader, java.awt.BorderLayout.PAGE_START);

        pnLeft.setBackground(new java.awt.Color(245, 247, 250));
        pnLeft.setPreferredSize(new java.awt.Dimension(250, 577));

        jLabel2.setText("Mã Sản Phẩm :");

        jLabel3.setText("Màu Sắc :");

        jLabel4.setText("Kích Thước :");

        cbMauSac.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbKichThuoc.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel5.setText("Giá Bán :");

        jLabel6.setText("Số Lượng :");

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

        javax.swing.GroupLayout pnLeftLayout = new javax.swing.GroupLayout(pnLeft);
        pnLeft.setLayout(pnLeftLayout);
        pnLeftLayout.setHorizontalGroup(
            pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnLeftLayout.createSequentialGroup()
                .addGroup(pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnLeftLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cbMauSac, javax.swing.GroupLayout.Alignment.TRAILING, 0, 210, Short.MAX_VALUE)
                            .addComponent(jLabel2)
                            .addComponent(txtMasp)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(cbKichThuoc, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5)
                            .addComponent(txtGiaBan)
                            .addComponent(jLabel6)
                            .addComponent(txtSoLuong))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnLeftLayout.createSequentialGroup()
                        .addGap(0, 16, Short.MAX_VALUE)
                        .addComponent(btnThem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnXoa)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSua)))
                .addContainerGap())
        );
        pnLeftLayout.setVerticalGroup(
            pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnLeftLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMasp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbMauSac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbKichThuoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtGiaBan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThem)
                    .addComponent(btnXoa)
                    .addComponent(btnSua))
                .addContainerGap(228, Short.MAX_VALUE))
        );

        getContentPane().add(pnLeft, java.awt.BorderLayout.LINE_START);

        tblSpct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Mã Sản Phẩm ", "Số Lượng", "Màu Sắc", "Giá Bán", "Kích Thước"
            }
        ));
        jScrollPane1.setViewportView(tblSpct);

        javax.swing.GroupLayout pnMainLayout = new javax.swing.GroupLayout(pnMain);
        pnMain.setLayout(pnMainLayout);
        pnMainLayout.setHorizontalGroup(
            pnMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 746, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnMainLayout.setVerticalGroup(
            pnMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
        );

        getContentPane().add(pnMain, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Bạn có chắc chắn muốn thêm sản phẩm này không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (!validateFormSpct()) {
                return;
            }
            try {
                Connection con = DbConnection.getConnection();

                String sql = "INSERT INTO quan_ao_chi_tiet(ma_spct,so_luong,mau_sac_id,gia_ban,kich_thuoc_id,quan_ao_id) VALUES (?,?,?,?,?,?)";

                PreparedStatement ps = con.prepareStatement(sql);

                ps.setString(1, txtMasp.getText());
                ps.setInt(2, Integer.parseInt(txtSoLuong.getText()));
                ps.setInt(3, cbMauSac.getSelectedIndex() + 1);
                ps.setDouble(4, Integer.parseInt(txtGiaBan.getText()));
                ps.setInt(5, cbKichThuoc.getSelectedIndex() + 1);
                ps.setInt(6, sanPhamId);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công");
                loadTableSPCT();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnThemActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Bạn có chắc muốn sửa sản phẩm này không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            suaSpct();
            JOptionPane.showMessageDialog(null, "Sửa sản phẩm thành công!");
        }
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        int row = tblSpct.getSelectedRow();

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
        int id = Integer.parseInt(tblSpct.getValueAt(row, 0).toString());

        try {

            Connection con = DbConnection.getConnection();
            String sql = "DELETE FROM quan_ao_chi_tiet WHERE id = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Xóa thành công");

            loadTableSPCT();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnXoaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SanPhamChiTietView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SanPhamChiTietView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SanPhamChiTietView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SanPhamChiTietView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SanPhamChiTietView dialog = new SanPhamChiTietView(new javax.swing.JFrame(), true, 0);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JComboBox<String> cbKichThuoc;
    private javax.swing.JComboBox<String> cbMauSac;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnHeader;
    private javax.swing.JPanel pnLeft;
    private javax.swing.JPanel pnMain;
    private javax.swing.JTable tblSpct;
    private javax.swing.JTextField txtGiaBan;
    private javax.swing.JTextField txtMasp;
    private javax.swing.JTextField txtSoLuong;
    // End of variables declaration//GEN-END:variables
}
