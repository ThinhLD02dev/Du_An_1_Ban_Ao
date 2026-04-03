/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.KhachHang;
import repository.KhachHangRepository;

/**
 *
 * @author LHH05
 */
public class KhachHangView extends javax.swing.JPanel {

    KhachHangRepository khRepo = new KhachHangRepository();

    /**
     * Creates new form KhachHangView
     */
    public KhachHangView() {
        initComponents();
        // ===== GẮN SỰ KIỆN CÒN THIẾU =====
        btnSua.addActionListener(evt -> btnSuaActionPerformed(evt));
        btnXoa.addActionListener(evt -> btnXoaActionPerformed(evt));
        btnLamMoi.addActionListener(evt -> btnLamMoiActionPerformed(evt));
        btnTimKiem.addActionListener(evt -> btnTimKiemActionPerformed(evt));
        txtTimKiem.addActionListener(evt -> btnTimKiemActionPerformed(evt));

        // ===== STYLE UI =====
        pnMain.setBackground(new Color(245, 247, 250));

        jPanel1.setBackground(Color.WHITE); // search
        jPanel2.setBackground(Color.WHITE); // table

        pnMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel2.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // ===== STYLE SEARCH =====
        txtTimKiem.setPreferredSize(new Dimension(250, 30));
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTimKiem.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        btnTimKiem.setBackground(new Color(0, 123, 255));
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setFocusPainted(false);
        btnTimKiem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ===== STYLE TABLE =====
        tblKhachHang.setRowHeight(28);
        tblKhachHang.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tblKhachHang.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblKhachHang.getTableHeader().setBackground(new Color(240, 240, 240));
        tblKhachHang.getTableHeader().setReorderingAllowed(false);

        tblKhachHang.setSelectionBackground(new Color(0, 123, 255));
        tblKhachHang.setSelectionForeground(Color.WHITE);

        tblKhachHang.setShowGrid(false);
        tblKhachHang.setIntercellSpacing(new Dimension(0, 0));

        // căn giữa header
        ((DefaultTableCellRenderer) tblKhachHang.getTableHeader()
                .getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        loadTable();
    }

    public void loadTable() {
        DefaultTableModel model = (DefaultTableModel) tblKhachHang.getModel();
        model.setRowCount(0);
        List<KhachHang> list = khRepo.getAll();
        for (KhachHang kh : list) {
            Object[] row = {kh.getId(), kh.getTenKhachHang(), kh.getSoDienThoai(), kh.getEmail(), kh.getDiaChi()};
            model.addRow(row);
        }
    }

    public void loadTableBySearch(String keyword) {
        DefaultTableModel model = (DefaultTableModel) tblKhachHang.getModel();
        model.setRowCount(0);
        List<KhachHang> list = khRepo.search(keyword);
        for (KhachHang kh : list) {
            Object[] row = {kh.getId(), kh.getTenKhachHang(), kh.getSoDienThoai(), kh.getEmail(), kh.getDiaChi()};
            model.addRow(row);
        }
    }

    public boolean validateForm() {
        if (txtTenKH.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên khách hàng không được để trống");
            txtTenKH.requestFocus();
            return false;
        }
        if (txtSDT.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không được để trống");
            txtSDT.requestFocus();
            return false;
        }
        if (!txtSDT.getText().trim().matches("\\d{10,11}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ (10-11 chữ số)");
            txtSDT.requestFocus();
            return false;
        }
        if (txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email không được để trống");
            txtEmail.requestFocus();
            return false;
        }
        if (!txtEmail.getText().trim().contains("@")) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ");
            txtEmail.requestFocus();
            return false;
        }
        if (txtDiaChi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Địa chỉ không được để trống");
            txtDiaChi.requestFocus();
            return false;
        }
        return true;
    }

    private void clearForm() {
        txtTenKH.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
        txtDiaChi.setText("");
    }

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {
        if (!validateForm()) {
            return;
        }
        KhachHang kh = new KhachHang(0, txtTenKH.getText().trim(), txtSDT.getText().trim(), txtEmail.getText().trim(), txtDiaChi.getText().trim());
        if (khRepo.add(kh)) {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!");
            loadTable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại!");
        }
    }

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tblKhachHang.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa!");
            return;
        }
        if (!validateForm()) {
            return;
        }
        int id = (int) tblKhachHang.getValueAt(row, 0);
        KhachHang kh = new KhachHang(id, txtTenKH.getText().trim(), txtSDT.getText().trim(), txtEmail.getText().trim(), txtDiaChi.getText().trim());
        if (khRepo.update(kh)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadTable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
        }
    }

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tblKhachHang.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa khách hàng này không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        int id = (int) tblKhachHang.getValueAt(row, 0);
        if (khRepo.delete(id)) {
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            loadTable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa thất bại! Khách hàng có thể đang liên kết với hóa đơn.");
        }
    }

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {
        clearForm();
        loadTable();
        tblKhachHang.clearSelection();
    }

    private void btnTimKiemActionPerformed(java.awt.event.ActionEvent evt) {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập từ khóa tìm kiếm!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            txtTimKiem.requestFocus();
            return;
        }
        loadTableBySearch(keyword);

        // Thông báo nếu không tìm thấy kết quả
        if (tblKhachHang.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy khách hàng với từ khóa: \"" + keyword + "\"",
                    "Không có kết quả",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void tblKhachHangMouseClicked(java.awt.event.MouseEvent evt) {
        int row = tblKhachHang.getSelectedRow();
        if (row < 0) {
            return;
        }
        txtTenKH.setText(tblKhachHang.getValueAt(row, 1).toString());
        txtSDT.setText(tblKhachHang.getValueAt(row, 2).toString());
        txtEmail.setText(tblKhachHang.getValueAt(row, 3).toString());
        txtDiaChi.setText(tblKhachHang.getValueAt(row, 4).toString());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnNorth = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        pnLeft = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtTenKH = new javax.swing.JTextField();
        txtSDT = new javax.swing.JTextField();
        txtDiaChi = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        btnThem = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnLamMoi = new javax.swing.JButton();
        pnMain = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtTimKiem = new javax.swing.JTextField();
        btnTimKiem = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblKhachHang = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        pnNorth.setBackground(new java.awt.Color(74, 144, 226));
        pnNorth.setMinimumSize(new java.awt.Dimension(100, 60));
        pnNorth.setPreferredSize(new java.awt.Dimension(957, 60));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("CUSTOMER MANAGEMENT");

        javax.swing.GroupLayout pnNorthLayout = new javax.swing.GroupLayout(pnNorth);
        pnNorth.setLayout(pnNorthLayout);
        pnNorthLayout.setHorizontalGroup(
            pnNorthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnNorthLayout.createSequentialGroup()
                .addGap(279, 279, 279)
                .addComponent(jLabel1)
                .addContainerGap(316, Short.MAX_VALUE))
        );
        pnNorthLayout.setVerticalGroup(
            pnNorthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnNorthLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        add(pnNorth, java.awt.BorderLayout.PAGE_START);

        pnLeft.setBackground(new java.awt.Color(245, 247, 250));
        pnLeft.setPreferredSize(new java.awt.Dimension(300, 539));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Tên Khách Hàng :");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Số Điện Thoại ;");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Địa Chỉ");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Email :");

        txtTenKH.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        txtSDT.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        txtDiaChi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        txtEmail.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        btnThem.setBackground(new java.awt.Color(40, 167, 69));
        btnThem.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnThem.setText("Thêm");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        btnXoa.setBackground(new java.awt.Color(220, 53, 69));
        btnXoa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnXoa.setText("Xóa");

        btnSua.setBackground(new java.awt.Color(255, 193, 7));
        btnSua.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSua.setText("Sửa");

        btnLamMoi.setBackground(new java.awt.Color(108, 117, 125));
        btnLamMoi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnLamMoi.setText("Làm Mới");

        javax.swing.GroupLayout pnLeftLayout = new javax.swing.GroupLayout(pnLeft);
        pnLeft.setLayout(pnLeftLayout);
        pnLeftLayout.setHorizontalGroup(
            pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnLeftLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnLeftLayout.createSequentialGroup()
                        .addGroup(pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(txtTenKH, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(29, Short.MAX_VALUE))
                    .addGroup(pnLeftLayout.createSequentialGroup()
                        .addGroup(pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnLeftLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLamMoi))
                        .addGap(46, 46, 46))))
        );
        pnLeftLayout.setVerticalGroup(
            pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnLeftLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTenKH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThem)
                    .addComponent(btnXoa))
                .addGap(18, 18, 18)
                .addGroup(pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSua)
                    .addComponent(btnLamMoi))
                .addContainerGap(108, Short.MAX_VALUE))
        );

        add(pnLeft, java.awt.BorderLayout.LINE_START);

        pnMain.setLayout(new java.awt.BorderLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(657, 50));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Tìm kIếm :");

        txtTimKiem.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        btnTimKiem.setBackground(new java.awt.Color(40, 167, 69));
        btnTimKiem.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnTimKiem.setText("Tìm Kiếm");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnTimKiem)
                .addContainerGap(95, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTimKiem))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnMain.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        tblKhachHang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Phone", "Address", "Email"
            }
        ));
        tblKhachHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblKhachHangMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblKhachHang);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE))
        );

        pnMain.add(jPanel2, java.awt.BorderLayout.CENTER);

        add(pnMain, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
/**
    private void tblKhachHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblKhachHangMouseClicked
        //
     * TODO add your handling code here:
    }//GEN-LAST:event_tblKhachHangMouseClicked

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
       
    }//GEN-LAST:event_btnThemActionPerformed
*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnTimKiem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel pnLeft;
    private javax.swing.JPanel pnMain;
    private javax.swing.JPanel pnNorth;
    private javax.swing.JTable tblKhachHang;
    private javax.swing.JTextField txtDiaChi;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtSDT;
    private javax.swing.JTextField txtTenKH;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables
}
