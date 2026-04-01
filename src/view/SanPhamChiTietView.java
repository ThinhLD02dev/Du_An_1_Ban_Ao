/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package view;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import jdbc.DbConnection;

/**
 *
 * @author ngocp
 */
public class SanPhamChiTietView extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(SanPhamChiTietView.class.getName());

    /**
     * Creates new form NewJDialog
     */
    private int sanPhamId;
    
    public boolean validateForm() {
    

    if (txtSoLuong.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Số lượng không được để trống");
        txtMasp.requestFocus();
        return false;
    }

    if (txtGiaBan.getText().trim().isEmpty()){
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
    
    public void loadMauSac(){
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
    
    public void loadKichThuoc(){
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
    qa.ma_sp,
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
                rs.getString("ma_sp"),
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
    public void loadForm(){
        String sql = """
            SELECT 
            spct.id,
            qa.ma_sp,
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
            txtMasp.setText(rs.getString("ma_sp"));
            txtSoLuong.setText(rs.getString("so_luong"));
            txtGiaBan.setText(rs.getString("gia_ban"));
            cbKichThuoc.setSelectedItem(rs.getString("ten_kich_thuoc"));
            cbMauSac.setSelectedItem(rs.getString("ten_mau"));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    public SanPhamChiTietView(java.awt.Frame parent, boolean modal, int id) {
        super(parent, modal);
        initComponents();
        this.sanPhamId = id;
        loadForm();
        loadTableSPCT();
        loadKichThuoc();
        loadMauSac();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSpct = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtMasp = new javax.swing.JTextField();
        cbMauSac = new javax.swing.JComboBox<>();
        cbKichThuoc = new javax.swing.JComboBox<>();
        txtGiaBan = new javax.swing.JTextField();
        btnSua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        txtSoLuong = new javax.swing.JTextField();
        btnThem = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setText("SẢN PHẨM CHI TIẾT");

        tblSpct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Mã sản phẩm", "Số Lượng", "Màu sắc", "Giá Bán", "Kích thước"
            }
        ));
        jScrollPane1.setViewportView(tblSpct);

        jLabel2.setText("Mã Sản Phẩm");

        jLabel3.setText("Màu sắc");

        jLabel4.setText("Kích thước");

        jLabel5.setText("Giá bán");

        jLabel6.setText("Số Lượng");

        txtMasp.setEditable(false);

        cbMauSac.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbKichThuoc.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnSua.setText("SỬA");

        btnXoa.setText("Xóa");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        btnThem.setText("THÊM");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(38, 38, 38)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtMasp)
                            .addComponent(cbMauSac, 0, 240, Short.MAX_VALUE)
                            .addComponent(cbKichThuoc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnThem)
                        .addGap(129, 129, 129)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtGiaBan, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                            .addComponent(txtSoLuong))
                        .addGap(90, 90, 90))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(btnSua)
                        .addGap(116, 116, 116)
                        .addComponent(btnXoa)
                        .addGap(146, 146, 146))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtGiaBan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtMasp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbMauSac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbKichThuoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSua)
                        .addComponent(btnXoa))
                    .addComponent(btnThem))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(240, 240, 240))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 817, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel1)
                .addGap(8, 8, 8)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        // TODO add your handling code here:
        try {
        Connection con = DbConnection.getConnection();

        String sql = "INSERT INTO quan_ao_chi_tiet(so_luong,mau_sac_id,gia_ban,kich_thuoc_id,quan_ao_id) VALUES (?,?,?,?,?)";

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, Integer.parseInt(txtSoLuong.getText()));
        ps.setInt(2, cbMauSac.getSelectedIndex()+1);
        ps.setDouble(3, Integer.parseInt(txtGiaBan.getText()));
        ps.setInt(4, cbKichThuoc.getSelectedIndex()+1);
        ps.setInt(5, sanPhamId);

        ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công");
            loadTableSPCT();

    } catch (Exception e) {
        e.printStackTrace();
    }
    }//GEN-LAST:event_btnThemActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        // TODO add your handling code here:
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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
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
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblSpct;
    private javax.swing.JTextField txtGiaBan;
    private javax.swing.JTextField txtMasp;
    private javax.swing.JTextField txtSoLuong;
    // End of variables declaration//GEN-END:variables

}
