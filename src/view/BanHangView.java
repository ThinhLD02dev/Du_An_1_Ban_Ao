/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.table.DefaultTableModel;
import model.HoaDon;
import model.KhachHang;
import repository.HoaDonChiTietRepository;
import repository.HoaDonRepository;
import repository.KhachHangRepository;
import repository.NhanVienRepository;
import repository.SanPhamChiTietRepository;
import repository.SanPhamRepository;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.CardLayout;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 *
 * @author LHH05
 */
public class BanHangView extends javax.swing.JPanel {

    SanPhamChiTietRepository spctRepo = new SanPhamChiTietRepository();
    HoaDonRepository hdRepo = new HoaDonRepository();
    KhachHangRepository khRepo = new KhachHangRepository();
    HoaDonChiTietRepository hdctRepo = new HoaDonChiTietRepository();
    private int currentHoaDonId = -1;
    private List<Map<String, Object>> listSP = new ArrayList<>();
    private List<Map<String, Object>> listCart = new ArrayList<>();
    private List<Map<String, Object>> listHD = new ArrayList<>();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("ss:mm:HH dd/MM/yyyy");
    SpinnerNumberModel SpnModel = new SpinnerNumberModel(1, 1, 1000, 1);
    private final Integer idNhanVien;
    private CreateCustomerView customerDialog = null;
    // Biến cho tìm kiếm khách hàng
    private KhachHang kh = null;
    private List<KhachHang> Listkh = new ArrayList<>();
    private final Timer searchTimer; // Timer để debounce tìm kiếm
    private boolean isSelectingCustomer = false; // Flag khi đang chọn từ dropdown

    /**
     * Creates new form SaleManagementView
     */
    public BanHangView(Integer id) {
        initComponents();
        this.idNhanVien = id;
        initializeUI();

        loadTableProduct();
        loadTableUnpaid();
        loadTablePaid();

        btnAdd.setEnabled(false);
        btnSave.setEnabled(false);

        spnNumberAdd.setModel(SpnModel);
        spnNumberDelete.setModel(SpnModel);

        // Add DocumentListener to txtSearch for real-time search
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                performSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                performSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                performSearch();
            }
        });

        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
                loadTableProduct();
            }
        });

        // Khởi tạo Timer debounce (300ms)
        searchTimer = new Timer(100, e -> doCustomerSearch());
        searchTimer.setRepeats(false);

        // Sử dụng KeyAdapter thay vì DocumentListener để tránh lỗi
        JTextField editorField = (JTextField) cbbCustomer.getEditor().getEditorComponent();
        editorField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!isSelectingCustomer) {
                    // Reset timer mỗi khi gõ phím
                    searchTimer.restart();
                }
            }
        });

        // Xử lý khi chọn item từ dropdown
        cbbCustomer.addActionListener(e -> {
            if ("comboBoxChanged".equals(e.getActionCommand())) {
                int index = cbbCustomer.getSelectedIndex();
                if (index >= 0 && index < Listkh.size()) {
                    isSelectingCustomer = true;
                    kh = Listkh.get(index);
                    cbbCustomer.getEditor().setItem(kh.getTenKhachHang());
                    cbbCustomer.hidePopup();
                    isSelectingCustomer = false;
                }
            }
        });

        // Tự động tính tiền thừa khi nhập tiền khách đưa
        txtGiveMoney.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                calculateChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                calculateChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                calculateChange();
            }
        });
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSpinner2 = new javax.swing.JSpinner();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        pnLeft1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btnCreateInvoice = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        pnLeft2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtIdInvoice = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnAddCustomer = new javax.swing.JButton();
        txtTimeCreate = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtSumMoney = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtSale = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtMoneyPaid = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtGiveMoney = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtChange = new javax.swing.JTextField();
        cbbCustomer = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        txtUseCreate = new javax.swing.JTextField();
        pnLeft3 = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnPaid = new javax.swing.JButton();
        btnIssueIvoice = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCart = new javax.swing.JTable();
        btnDelete = new javax.swing.JButton();
        spnNumberDelete = new javax.swing.JSpinner();
        btnClear = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        spnNumberAdd = new javax.swing.JSpinner();
        btnAdd = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblProduct = new javax.swing.JTable();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblUnPaid = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPaid = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(74, 144, 226));
        jPanel1.setPreferredSize(new java.awt.Dimension(1078, 60));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("BÁN HÀNG");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(430, 430, 430)
                .addComponent(jLabel1)
                .addContainerGap(505, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(21, 21, 21))
        );

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setBackground(new java.awt.Color(245, 247, 250));
        jPanel2.setPreferredSize(new java.awt.Dimension(300, 658));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Tạo Hóa Đơn");

        btnCreateInvoice.setText("Tạo Hóa Đơn");
        btnCreateInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateInvoiceActionPerformed(evt);
            }
        });

        btnCancel.setText("Hủy Hóa Đơn");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnLeft1Layout = new javax.swing.GroupLayout(pnLeft1);
        pnLeft1.setLayout(pnLeft1Layout);
        pnLeft1Layout.setHorizontalGroup(
            pnLeft1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnLeft1Layout.createSequentialGroup()
                .addGroup(pnLeft1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnLeft1Layout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnLeft1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnCreateInvoice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                        .addComponent(btnCancel)))
                .addContainerGap())
        );
        pnLeft1Layout.setVerticalGroup(
            pnLeft1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnLeft1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnLeft1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreateInvoice)
                    .addComponent(btnCancel))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jLabel3.setText("Mã HĐ :");

        jLabel4.setText("Khách Hàng :");

        jLabel5.setText("Thời Gian Tạo :");

        btnAddCustomer.setText("+");
        btnAddCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCustomerActionPerformed(evt);
            }
        });

        jLabel6.setText("Tổng Tiền :");

        jLabel7.setText("Mã Giảm Giá :");

        jLabel8.setText("Tiền Cần Thanh Toán :");

        jLabel9.setText("Tiền Khách Đưa :");

        jLabel10.setText("Tiền Thừa :");

        cbbCustomer.setEditable(true);

        jLabel14.setText("Người Tạo :");

        javax.swing.GroupLayout pnLeft2Layout = new javax.swing.GroupLayout(pnLeft2);
        pnLeft2.setLayout(pnLeft2Layout);
        pnLeft2Layout.setHorizontalGroup(
            pnLeft2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnLeft2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnLeft2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTimeCreate)
                    .addGroup(pnLeft2Layout.createSequentialGroup()
                        .addComponent(cbbCustomer, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAddCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtIdInvoice, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtSumMoney)
                    .addComponent(txtSale)
                    .addComponent(txtMoneyPaid)
                    .addComponent(txtGiveMoney)
                    .addComponent(txtChange)
                    .addGroup(pnLeft2Layout.createSequentialGroup()
                        .addGroup(pnLeft2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(jLabel14))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtUseCreate))
                .addContainerGap())
        );
        pnLeft2Layout.setVerticalGroup(
            pnLeft2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnLeft2Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtIdInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnLeft2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbbCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddCustomer))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTimeCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUseCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addGap(4, 4, 4)
                .addComponent(txtSumMoney, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMoneyPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtGiveMoney, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(txtChange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        btnSave.setText("Tạm Lưu");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnRefresh.setText("Làm Mới");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        btnPaid.setText("Thanh Toán");
        btnPaid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPaidActionPerformed(evt);
            }
        });

        btnIssueIvoice.setText("Xuất Hóa Đơn");
        btnIssueIvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIssueIvoiceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnLeft3Layout = new javax.swing.GroupLayout(pnLeft3);
        pnLeft3.setLayout(pnLeft3Layout);
        pnLeft3Layout.setHorizontalGroup(
            pnLeft3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnLeft3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnLeft3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnPaid, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnLeft3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnIssueIvoice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnLeft3Layout.setVerticalGroup(
            pnLeft3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnLeft3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnLeft3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(btnRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(pnLeft3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPaid)
                    .addComponent(btnIssueIvoice))
                .addGap(17, 17, 17))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnLeft2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnLeft1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnLeft3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnLeft1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(pnLeft2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnLeft3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        add(jPanel2, java.awt.BorderLayout.LINE_START);

        jLabel11.setText("Giỏ Hàng");

        tblCart.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Tên", "Size", "Số Lượng Mua", "Màu Sắc", "Đơn Giá ", "Thành Tiền"
            }
        ));
        jScrollPane3.setViewportView(tblCart);

        btnDelete.setText("Xóa ");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnClear.setText("Xóa Hết");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel11)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 774, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(btnDelete)
                .addGap(18, 18, 18)
                .addComponent(spnNumberDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnClear))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDelete)
                    .addComponent(spnNumberDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClear)))
        );

        jLabel12.setText("Sản Phẩm ");

        jLabel13.setText("Tìm Kiếm :");

        btnAdd.setText("Thêm");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        tblProduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Tên", "Size", "Màu Sắc", "Số Lượng Tồn", "Đơn Giá"
            }
        ));
        jScrollPane4.setViewportView(tblProduct);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 404, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(spnNumberAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAdd)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jScrollPane4)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnNumberAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdd))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
        );

        tblUnPaid.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Mã Hóa Đơn", "Thời Gian Tạo", "Khách Hàng ", "Người Tạo"
            }
        ));
        tblUnPaid.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUnPaidMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tblUnPaid);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 774, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Chưa Thanh Toán", jPanel4);

        tblPaid.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Mã Hóa Đơn", "Ngày Thanh Toán", "Khách Hàng", "Người Tạo"
            }
        ));
        jScrollPane2.setViewportView(tblPaid);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 774, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Đã Thanh Toán", jPanel7);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel3, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void calculateChange() {
        try {
            String tienNhanStr = txtGiveMoney.getText().trim();
            String tienThanhToanStr = txtMoneyPaid.getText().trim();

            BigDecimal tienNhan = tienNhanStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(tienNhanStr);
            BigDecimal tienThanhToan = tienThanhToanStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(tienThanhToanStr);

            BigDecimal tienThua = tienNhan.subtract(tienThanhToan);

            // Nếu tiền thừa âm thì set về 0
            if (tienThua.compareTo(BigDecimal.ZERO) < 0) {
                tienThua = BigDecimal.ZERO;
            }

            txtChange.setText(String.valueOf(tienThua));
        } catch (NumberFormatException e) {
            // Nếu nhập không phải số, set tiền thừa về 0
            txtChange.setText("0");
        }
    }

    public void loadTableUnpaid() {
        DefaultTableModel model = (DefaultTableModel) tblUnPaid.getModel();
        tblUnPaid.setDefaultEditor(Object.class, null);
        model.setRowCount(0);

        listHD = hdRepo.getAllToUnpaid();
        for (Map<String, Object> hd : listHD) {
            LocalDateTime ngayTao = (LocalDateTime) hd.get("ngayTao");
            Object[] row = {
                hd.get("id"),
                ngayTao.format(format),
                hd.get("tenKhachHang"),
                hd.get("tenNhanVien")
            };
            model.addRow(row);
        }
    }

    public void loadTablePaid() {
        DefaultTableModel model = (DefaultTableModel) tblPaid.getModel();
        tblPaid.setDefaultEditor(Object.class, null);
        model.setRowCount(0);

        listHD = hdRepo.getAllToPaid();
        for (Map<String, Object> hd : listHD) {
            LocalDateTime ngayThanhToan = (LocalDateTime) hd.get("ngayThanhToan");
            Object[] row = {
                hd.get("id"),
                ngayThanhToan.format(format),
                hd.get("tenKhachHang"),
                hd.get("tenNhanVien")
            };
            model.addRow(row);
        }
    }

    public void loadTableCart(int hoaDonId) {
        DefaultTableModel model = (DefaultTableModel) tblCart.getModel();
        model.setRowCount(0);

        listCart = hdctRepo.getAllByCart(hoaDonId);

        for (Map<String, Object> item : listCart) {
            Object[] row = {
                item.get("tenAo"),
                item.get("tenKichThuoc"),
                item.get("tenMau"),
                item.get("soLuong"),
                item.get("donGia"),
                item.get("tongGia")
            };
            model.addRow(row);
        }
    }

    public void loadTableProduct() {
        DefaultTableModel model = (DefaultTableModel) tblProduct.getModel();
        tblProduct.setDefaultEditor(Object.class, null);
        model.setRowCount(0);

        try {
            listSP = spctRepo.getAllbySell();

            if (listSP == null || listSP.isEmpty()) {
                System.out.println("Không có sản phẩm");
                return;
            }

            for (Map<String, Object> sp : listSP) {

                String tenAo = String.valueOf(sp.getOrDefault("tenAo", ""));
                String size = String.valueOf(sp.getOrDefault("tenKichThuoc", ""));
                String mau = String.valueOf(sp.getOrDefault("tenMau", ""));
                String moTa = String.valueOf(sp.getOrDefault("moTa", ""));
                Object soLuong = sp.getOrDefault("soLuong", 0);
                Object giaBan = sp.getOrDefault("giaBan", 0);

                model.addRow(new Object[]{
                    tenAo,
                    size,
                    mau,
                    moTa,
                    soLuong,
                    giaBan
                });
            }

            tblProduct.revalidate();
            tblProduct.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi load sản phẩm");
        }
    }

    public void loadFormInvoice(int hoaDonId) {
        Map<String, Object> hd = hdRepo.findById(hoaDonId);
        if (hd != null) {
            txtIdInvoice.setText(String.valueOf(hd.get("id")));
            isSelectingCustomer = true;
            cbbCustomer.getEditor().setItem(hd.get("tenKhachHang"));
            isSelectingCustomer = false;

            // Format thời gian tạo
            Timestamp ngayTao = (Timestamp) hd.get("ngayTao");
            if (ngayTao != null) {
                LocalDateTime ldt = ngayTao.toLocalDateTime();
                txtTimeCreate.setText(ldt.format(format));
            } else {
                txtTimeCreate.setText("");
            }

            txtUseCreate.setText((String) hd.get("tenNhanVien"));

            // Handle null cho các field số
            BigDecimal tongTien = (BigDecimal) hd.get("tongTien");
            tongTien = tongTien != null ? tongTien : BigDecimal.ZERO;
            txtSumMoney.setText(String.valueOf(tongTien));

            String maGiamGia = (String) hd.get("maGiamGia");
            txtSale.setText(maGiamGia != null ? maGiamGia : "");

            // Tính tiền cần thanh toán: nếu không có mã giảm giá thì bằng tổng tiền
            BigDecimal tienThanhToan;
            if (maGiamGia == null || maGiamGia.trim().isEmpty()) {
                tienThanhToan = tongTien;
            } else {
                // Tạm thời chưa xử lý giảm giá, vẫn bằng tổng tiền
                tienThanhToan = tongTien;
            }
            txtMoneyPaid.setText(String.valueOf(tienThanhToan));

            // Tiền khách đưa
            BigDecimal tienNhan = (BigDecimal) hd.get("tienNhan");
            tienNhan = tienNhan != null ? tienNhan : BigDecimal.ZERO;
            txtGiveMoney.setText(String.valueOf(tienNhan));

            // Tính tiền thừa = tiền khách đưa - tiền cần thanh toán
            BigDecimal tienThua = tienNhan.subtract(tienThanhToan);
            // Nếu tiền thừa âm thì set về 0
            if (tienThua.compareTo(BigDecimal.ZERO) < 0) {
                tienThua = BigDecimal.ZERO;
            }
            txtChange.setText(String.valueOf(tienThua));
        } else {
            clearForm();
        }
    }

    private void performSearch() {
        String searchText = txtSearch.getText().trim().toLowerCase();
        DefaultTableModel model = (DefaultTableModel) tblProduct.getModel();
        model.setRowCount(0);

        if (searchText.isEmpty()) {
            // Nếu không có từ khóa tìm kiếm, load lại toàn bộ sản phẩm
            loadTableProduct();
        } else {
            // Tìm kiếm qua repository
            listSP = spctRepo.searchByKeyword(searchText);
            for (Map<String, Object> sp : listSP) {
                Object[] row = {
                    sp.get("tenAo"),
                    sp.get("tenKichThuoc"),
                    sp.get("tenMau"),
                    sp.get("moTa"),
                    sp.get("soLuong"),
                    sp.get("giaBan")
                };
                model.addRow(row);
            }
        }
    }

    /**
     * Thực hiện tìm kiếm khách hàng (được gọi sau khi Timer hết hạn)
     */
    private void doCustomerSearch() {
        JTextField editor = (JTextField) cbbCustomer.getEditor().getEditorComponent();
        String keyword = editor.getText().trim();

        if (keyword.isEmpty()) {
            cbbCustomer.hidePopup();
            Listkh.clear();
            kh = null;
            return;
        }

        // Tìm kiếm khách hàng theo tên
        Listkh = khRepo.search(keyword);

        if (Listkh.isEmpty()) {
            cbbCustomer.hidePopup();
            kh = null;
            return;
        }

        // Tạo danh sách hiển thị
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (KhachHang kh : Listkh) {
            String sdt = (kh.getSoDienThoai() != null && !kh.getSoDienThoai().isEmpty())
                    ? " - " + kh.getSoDienThoai() : "";
            model.addElement(kh.getTenKhachHang() + sdt);
        }

        // Lưu text và vị trí con trỏ
        String currentText = editor.getText();
        int caretPos = editor.getCaretPosition();

        // Cập nhật model
        isSelectingCustomer = true;
        cbbCustomer.setModel(model);
        cbbCustomer.setSelectedIndex(-1);
        editor.setText(currentText);
        if (caretPos <= currentText.length()) {
            editor.setCaretPosition(caretPos);
        }
        isSelectingCustomer = false;

        // Hiển thị dropdown
        cbbCustomer.showPopup();
    }

    private void clearForm() {
        txtIdInvoice.setText("");
        isSelectingCustomer = true;
        cbbCustomer.getEditor().setItem("");
        isSelectingCustomer = false;
        txtTimeCreate.setText("");
        txtSumMoney.setText("");
        txtSale.setText("");
        txtMoneyPaid.setText("");
        txtGiveMoney.setText("");
        txtChange.setText("");

        kh = null;
        cbbCustomer.setModel(new DefaultComboBoxModel<>());

        currentHoaDonId = -1;
        btnAdd.setEnabled(false);
        btnCreateInvoice.setEnabled(true);
        btnSave.setEnabled(false);
        btnPaid.setEnabled(false);

        loadTableCart(0);
        tblUnPaid.setSelectionMode(0);
    }

    private void initializeUI() {
        // 1. Cài đặt Font chung cho toàn bộ ứng dụng
        setupFonts();

        // 2. Cài đặt màu sắc và giao diện
        setupColors();

        // 3. Cài đặt border và padding
        setupBorders();

        // 4. Cài đặt các bảng (Table)
        setupTables();

        // 5. Cài đặt các nút (Button)
        setupButtons();

        // 6. Cài đặt các trường văn bản (TextField)
        setupTextFields();

        // 7. Cài đặt các Label
        setupLabels();
    }

    /**
     * Cài đặt Font cho các thành phần
     */
    private void setupFonts() {
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 11);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 12);
        Font smallFont = new Font("Segoe UI", Font.PLAIN, 10);

        // Cài đặt font cho các label
        jLabel2.setFont(titleFont);
        jLabel3.setFont(labelFont);
        jLabel4.setFont(labelFont);
        jLabel5.setFont(labelFont);
        jLabel6.setFont(labelFont);
        jLabel7.setFont(labelFont);
        jLabel8.setFont(labelFont);
        jLabel9.setFont(labelFont);
        jLabel10.setFont(labelFont);
        jLabel11.setFont(titleFont);
        jLabel12.setFont(titleFont);
        jLabel13.setFont(labelFont);
        jLabel14.setFont(labelFont);

        // Cài đặt font cho các text field
        txtIdInvoice.setFont(labelFont);
        txtTimeCreate.setFont(labelFont);
        txtSumMoney.setFont(labelFont);
        txtSale.setFont(labelFont);
        txtMoneyPaid.setFont(labelFont);
        txtGiveMoney.setFont(labelFont);
        txtChange.setFont(labelFont);
        txtUseCreate.setFont(labelFont);
        txtSearch.setFont(labelFont);
    }

    /**
     * Cài đặt màu sắc cho các thành phần
     */
    private void setupColors() {
        // Màu chủ đạo
        Color primaryColor = new Color(74, 144, 226);
        Color secondaryColor = new Color(52, 152, 219);
        Color accentColor = new Color(230, 126, 34);
        Color backgroundColor = new Color(245, 247, 250);
        Color borderColor = new Color(189, 195, 199);
        Color textColor = new Color(44, 62, 80);
        Color lightGray = new Color(236, 240, 241);

        // Nền panel
        jPanel2.setBackground(backgroundColor);
        jPanel3.setBackground(Color.WHITE);
        jPanel4.setBackground(Color.WHITE);
        jPanel5.setBackground(Color.WHITE);
        jPanel6.setBackground(Color.WHITE);
        jPanel7.setBackground(Color.WHITE);
        pnLeft1.setBackground(backgroundColor);
        pnLeft2.setBackground(backgroundColor);
        pnLeft3.setBackground(backgroundColor);

        // Màu chữ cho các label
        jLabel3.setForeground(textColor);
        jLabel4.setForeground(textColor);
        jLabel5.setForeground(textColor);
        jLabel6.setForeground(textColor);
        jLabel7.setForeground(textColor);
        jLabel8.setForeground(textColor);
        jLabel9.setForeground(textColor);
        jLabel10.setForeground(textColor);
        jLabel11.setForeground(primaryColor);
        jLabel12.setForeground(primaryColor);
        jLabel13.setForeground(textColor);
        jLabel14.setForeground(textColor);
        jLabel2.setForeground(primaryColor);
    }

    /**
     * Cài đặt Border cho các thành phần
     */
    private void setupBorders() {
        // Border chung
        Border lineBorder = BorderFactory.createLineBorder(new Color(189, 195, 199), 1);
        Border emptyBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border roundBorder = BorderFactory.createCompoundBorder(
                lineBorder,
                emptyBorder
        );

        // Panel border
        TitledBorder invoiceBorder = BorderFactory.createTitledBorder(
                lineBorder, "Thông Tin Hóa Đơn",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 11),
                new Color(74, 144, 226)
        );
        pnLeft2.setBorder(invoiceBorder);

        // TextField border
        Border textFieldBorder = BorderFactory.createCompoundBorder(
                lineBorder,
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        );

        txtIdInvoice.setBorder(textFieldBorder);
        txtTimeCreate.setBorder(textFieldBorder);
        txtSumMoney.setBorder(textFieldBorder);
        txtSale.setBorder(textFieldBorder);
        txtMoneyPaid.setBorder(textFieldBorder);
        txtGiveMoney.setBorder(textFieldBorder);
        txtChange.setBorder(textFieldBorder);
        txtUseCreate.setBorder(textFieldBorder);
        txtSearch.setBorder(textFieldBorder);

        // Combobox border
        cbbCustomer.setBorder(textFieldBorder);
    }

    /**
     * Cài đặt giao diện cho các bảng (Table)
     */
    private void setupTables() {
        // Cài đặt font chung
        Font tableFont = new Font("Segoe UI", Font.PLAIN, 11);
        Font headerFont = new Font("Segoe UI", Font.BOLD, 11);

        // Cài đặt cho bảng sản phẩm
        setupTable(tblProduct, tableFont, headerFont);

        // Cài đặt cho bảng giỏ hàng
        setupTable(tblCart, tableFont, headerFont);

        // Cài đặt cho bảng hóa đơn chưa thanh toán
        setupTable(tblUnPaid, tableFont, headerFont);

        // Cài đặt cho bảng hóa đơn đã thanh toán
        setupTable(tblPaid, tableFont, headerFont);
    }

    /**
     * Hàm helper cài đặt bảng
     */
    private void setupTable(JTable table, Font bodyFont, Font headerFont) {
        // Cài đặt font
        table.setFont(bodyFont);
        table.getTableHeader().setFont(headerFont);

        // Cài đặt màu
        Color headerColor = new Color(74, 144, 226);
        table.getTableHeader().setBackground(headerColor);
        table.getTableHeader().setForeground(Color.WHITE);

        // Cài đặt chiều cao hàng
        table.setRowHeight(25);
        table.getTableHeader().setPreferredSize(new Dimension(0, 30));

        // Cài đặt border
        table.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        table.setGridColor(new Color(220, 220, 220));

        // Cài đặt cách chọn hàng
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);
        table.setRowSelectionAllowed(true);
    }

    /**
     * Cài đặt giao diện cho các nút (Button)
     */
    private void setupButtons() {
        Color primaryColor = new Color(74, 144, 226);
        Color successColor = new Color(46, 204, 113);
        Color dangerColor = new Color(231, 76, 60);
        Color warningColor = new Color(230, 126, 34);

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 11);

        // Nút Tạo Hóa Đơn - Xanh dương
        styleButton(btnCreateInvoice, primaryColor, buttonFont);

        // Nút Hủy Hóa Đơn - Đỏ
        styleButton(btnCancel, dangerColor, buttonFont);

        // Nút Thêm - Xanh lá
        styleButton(btnAdd, successColor, buttonFont);

        // Nút Xóa - Đỏ
        styleButton(btnDelete, dangerColor, buttonFont);

        // Nút Xóa Hết - Đỏ đậm
        styleButton(btnClear, dangerColor, buttonFont);

        // Nút Tạm Lưu - Vàng
        styleButton(btnSave, warningColor, buttonFont);

        // Nút Làm Mới - Xanh lá
        styleButton(btnRefresh, successColor, buttonFont);

        // Nút Thanh Toán - Xanh lá
        styleButton(btnPaid, successColor, buttonFont);

        // Nút Xuất Hóa Đơn - Xanh dương
        styleButton(btnIssueIvoice, primaryColor, buttonFont);

        // Nút Thêm khách hàng - Nhỏ, xanh dương
        styleButton(btnAddCustomer, primaryColor, new Font("Segoe UI", Font.BOLD, 10));
    }

    /**
     * Hàm helper cài đặt style nút
     */
    private void styleButton(JButton button, Color backgroundColor, Font font) {
        button.setFont(font);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(brightenColor(backgroundColor));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
    }

    /**
     * Hàm tạo màu sáng hơn cho hiệu ứng hover
     */
    private Color brightenColor(Color color) {
        int red = Math.min(255, color.getRed() + 30);
        int green = Math.min(255, color.getGreen() + 30);
        int blue = Math.min(255, color.getBlue() + 30);
        return new Color(red, green, blue);
    }

    /**
     * Cài đặt giao diện cho các trường nhập liệu (TextField)
     */
    private void setupTextFields() {
        Border textFieldBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        );

        Font textFont = new Font("Segoe UI", Font.PLAIN, 11);

        JTextField[] textFields = {
            txtIdInvoice, txtTimeCreate, txtSumMoney, txtSale,
            txtMoneyPaid, txtGiveMoney, txtChange, txtUseCreate, txtSearch
        };

        for (JTextField tf : textFields) {
            tf.setFont(textFont);
            tf.setBorder(textFieldBorder);
            tf.setBackground(Color.WHITE);
            tf.setForeground(new Color(44, 62, 80));
        }

        // Các trường chỉ đọc
        txtIdInvoice.setEditable(false);
        txtTimeCreate.setEditable(false);
        txtSumMoney.setEditable(false);
        txtUseCreate.setEditable(false);
    }

    /**
     * Cài đặt giao diện cho các nhãn (Label)
     */
    private void setupLabels() {
        Color labelColor = new Color(44, 62, 80);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 11);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 12);

        JLabel[] labels = {
            jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8,
            jLabel9, jLabel10, jLabel13, jLabel14
        };

        for (JLabel label : labels) {
            label.setFont(labelFont);
            label.setForeground(labelColor);
        }

        // Title labels
        jLabel11.setFont(titleFont);
        jLabel12.setFont(titleFont);
        jLabel2.setFont(titleFont);
    }

    private void tblUnPaidMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUnPaidMouseClicked
        int row = tblUnPaid.getSelectedRow();
        if (row >= 0) {
            int hoaDonId = (int) tblUnPaid.getValueAt(row, 0);
            loadTableCart(hoaDonId);
            loadFormInvoice(hoaDonId);
            currentHoaDonId = hoaDonId;
            btnAdd.setEnabled(true);
            btnCreateInvoice.setEnabled(false);
            btnSave.setEnabled(true);
            btnPaid.setEnabled(true);
        }

    }//GEN-LAST:event_tblUnPaidMouseClicked

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int row = tblCart.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chưa chọn sản phẩm trong giỏ hàng!");
            return;
        }

        int hdctId = Integer.parseInt(listCart.get(row).get("id").toString());
        int currentQuantity = Integer.parseInt(listCart.get(row).get("soLuong").toString());
        int sanPhamChiTietId = (Integer) listCart.get(row).get("sanPhamChiTietId");

        int numberDelete = (Integer) spnNumberDelete.getValue();
        if (numberDelete <= 0) {
            JOptionPane.showMessageDialog(this, "Số lượng xoá phải lớn hơn 0!");
            return;
        }
        if (numberDelete >= currentQuantity) {
            hdctRepo.deleteCartItemByHdctId(hdctId);
            // Cộng lại toàn bộ số lượng đã xóa
            spctRepo.updateQuantity(sanPhamChiTietId, currentQuantity);
        } else {
            hdctRepo.updateCartDeleteByHdctId(hdctId, numberDelete);
            // Cộng lại số lượng đã xóa
            spctRepo.updateQuantity(sanPhamChiTietId, numberDelete);
        }
        loadTableCart(currentHoaDonId);
        loadTableProduct(); // Reload bảng sản phẩm để hiển thị số lượng mới
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        if (currentHoaDonId <= 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn trước!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xoá toàn bộ giỏ hàng?",
                "Xác nhận xoá",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Cộng lại số lượng cho tất cả sản phẩm trong giỏ trước khi xóa
            for (Map<String, Object> item : listCart) {
                int sanPhamChiTietId = (Integer) item.get("sanPhamChiTietId");
                int soLuong = (Integer) item.get("soLuong");
                spctRepo.updateQuantity(sanPhamChiTietId, soLuong);
            }
            hdctRepo.deleteAllCartItems(currentHoaDonId);
            loadTableCart(currentHoaDonId);

        }
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        if (currentHoaDonId <= 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn để hủy!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn hủy hóa đơn này? Tất cả dữ liệu sẽ bị xóa.",
                "Xác nhận hủy hóa đơn",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Cộng lại số lượng cho tất cả sản phẩm trong giỏ trước khi xóa
            for (Map<String, Object> item : listCart) {
                int sanPhamChiTietId = (Integer) item.get("sanPhamChiTietId");
                int soLuong = (Integer) item.get("soLuong");
                spctRepo.updateQuantity(sanPhamChiTietId, soLuong);
            }
            // Xóa chi tiết hóa đơn trước
            hdctRepo.deleteAllCartItems(currentHoaDonId);
            // Xóa hóa đơn
            boolean deleted = hdRepo.delete(currentHoaDonId);
            if (deleted) {
                JOptionPane.showMessageDialog(this, "Hủy hóa đơn thành công!");
                loadTableUnpaid();
                loadTableCart(0);
                loadTableProduct(); // Reload bảng sản phẩm để hiển thị số lượng mới
                // Reset form
                clearForm(); // Hoặc reset manual
                currentHoaDonId = -1;
                btnAdd.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(this, "Hủy hóa đơn thất bại!");
            }
        }
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // Kiểm tra xem có hoá đơn được chọn không
        if (currentHoaDonId <= 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hoá đơn để lưu!");
            return;
        }

        try {
            // Lấy giá trị từ các text field
            String maGiamGia = txtSale.getText().trim();
            String tienThanhToanStr = txtMoneyPaid.getText().trim();
            String tienNhanStr = txtGiveMoney.getText().trim();
            String tienThuaStr = txtChange.getText().trim();

            // Validate và chuyển đổi sang BigDecimal
            BigDecimal tienThanhToan = tienThanhToanStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(tienThanhToanStr);
            BigDecimal tienNhan = tienNhanStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(tienNhanStr);
            BigDecimal tienThua = tienThuaStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(tienThuaStr);

            // Gọi repository để cập nhật hoá đơn
            boolean success = hdRepo.updateInvoice(currentHoaDonId, maGiamGia.isEmpty() ? null : maGiamGia,
                    tienThanhToan, tienNhan, tienThua);

            if (success) {
                JOptionPane.showMessageDialog(this, "Lưu thông tin hoá đơn thành công!");
                // Reload form để hiển thị dữ liệu mới
                loadFormInvoice(currentHoaDonId);
            } else {
                JOptionPane.showMessageDialog(this, "Lưu thông tin hoá đơn thất bại!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho các trường tiền!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu thông tin: " + e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        clearForm();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnCreateInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateInvoiceActionPerformed
        Integer khachHangId = null;
        String tenKhachHang = ((javax.swing.JTextField) cbbCustomer.getEditor().getEditorComponent()).getText().trim();

        if (kh != null) {
            khachHangId = kh.getId();
            tenKhachHang = kh.getTenKhachHang();
        }

        HoaDon hd = new HoaDon();
        hd.setKhachHangId(khachHangId);
        hd.setNhanVienId(idNhanVien);

        int id = hdRepo.createInvoice(hd);
        if (id > 0) {
            JOptionPane.showMessageDialog(this, "Tạo hoá đơn thành công!");
            currentHoaDonId = id;
            loadTableUnpaid();
            loadFormInvoice(id);
            btnAdd.setEnabled(true);
            btnCreateInvoice.setEnabled(false);
            btnSave.setEnabled(true);
            btnPaid.setEnabled(true);

            if (tenKhachHang.isEmpty()) {
                tenKhachHang = "Khách lẻ";
                isSelectingCustomer = true;
                cbbCustomer.getEditor().setItem(tenKhachHang);
                isSelectingCustomer = false;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Tạo hoá đơn thất bại!");
        }
    }//GEN-LAST:event_btnCreateInvoiceActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        int row = tblProduct.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm trước khi thêm vào giỏ hàng!");
            return;
        }

        if (currentHoaDonId <= 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng tạo hoặc chọn một hóa đơn trước khi thêm sản phẩm!");
            return;
        }

        int sanPhamChiTietId = (Integer) listSP.get(row).get("id");
        int numberAdd = (Integer) spnNumberAdd.getValue();
        if (numberAdd <= 0) {
            JOptionPane.showMessageDialog(this, "Số lượng thêm phải lớn hơn 0!");
            return;
        }

        if (hdctRepo.existsInCart(currentHoaDonId, sanPhamChiTietId)) {
            hdctRepo.updateCart(currentHoaDonId, sanPhamChiTietId, numberAdd);
        } else {
            hdctRepo.insertToCart(currentHoaDonId, sanPhamChiTietId, numberAdd);
        }

        // Cập nhật số lượng sản phẩm trong kho
        spctRepo.updateQuantity(sanPhamChiTietId, -numberAdd);

        loadTableCart(currentHoaDonId);
        loadTableProduct();
        loadFormInvoice(currentHoaDonId);
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPaidActionPerformed
        // Kiểm tra xem có hoá đơn được chọn không
        if (currentHoaDonId <= 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hoá đơn để thanh toán!");
            return;
        }

        // Kiểm tra giỏ hàng có sản phẩm không
        if (listCart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống! Vui lòng thêm sản phẩm trước khi thanh toán.");
            return;
        }

        try {
            // Lấy giá trị từ các text field
            String maGiamGia = txtSale.getText().trim();
            String tongTienStr = txtSumMoney.getText().trim();
            String tienThanhToanStr = txtMoneyPaid.getText().trim();
            String tienNhanStr = txtGiveMoney.getText().trim();
            String tienThuaStr = txtChange.getText().trim();

            // Validate và chuyển đổi sang BigDecimal
            BigDecimal tongTien = tongTienStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(tongTienStr);
            BigDecimal tienThanhToan = tienThanhToanStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(tienThanhToanStr);
            BigDecimal tienNhan = tienNhanStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(tienNhanStr);
            BigDecimal tienThua = tienThuaStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(tienThuaStr);

            // Kiểm tra tiền khách đưa >= tiền cần thanh toán
            if (tienNhan.compareTo(tienThanhToan) < 0) {
                JOptionPane.showMessageDialog(this, "Tiền khách đưa phải lớn hơn hoặc bằng tiền cần thanh toán!");
                return;
            }

            // Xác nhận thanh toán
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xác nhận thanh toán hoá đơn #" + currentHoaDonId + "?\n"
                    + "Tổng tiền: " + tongTien + "\n"
                    + "Tiền thanh toán: " + tienThanhToan + "\n"
                    + "Tiền khách đưa: " + tienNhan + "\n"
                    + "Tiền thừa: " + tienThua,
                    "Xác nhận thanh toán",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Gọi repository để thanh toán hoá đơn
                boolean success = hdRepo.payInvoice(currentHoaDonId,
                        maGiamGia.isEmpty() ? null : maGiamGia,
                        tongTien, tienThanhToan, tienNhan, tienThua);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Thanh toán hoá đơn thành công!");

                    // Reload các bảng
                    loadTableUnpaid();
                    loadTablePaid();
                    loadTableCart(0);

                    // Reset form
                    txtIdInvoice.setText("");
                    isSelectingCustomer = true;
                    cbbCustomer.getEditor().setItem("");
                    isSelectingCustomer = false;
                    txtTimeCreate.setText("");
                    txtSumMoney.setText("");
                    txtSale.setText("");
                    txtMoneyPaid.setText("");
                    txtGiveMoney.setText("");
                    txtChange.setText("");

                    // Reset các biến và trạng thái nút
                    currentHoaDonId = -1;
                    kh = null;
                    cbbCustomer.setModel(new DefaultComboBoxModel<>());
                    btnAdd.setEnabled(false);
                    btnCreateInvoice.setEnabled(true);
                    btnSave.setEnabled(false);
                    btnPaid.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Thanh toán hoá đơn thất bại!");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho các trường tiền!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thanh toán: " + e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnPaidActionPerformed

    private void btnIssueIvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIssueIvoiceActionPerformed
        if (currentHoaDonId <= 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hoá đơn để xuất!");
            return;
        }

        // Kiểm tra giỏ hàng có sản phẩm không
        if (listCart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống! Vui lòng thêm sản phẩm trước khi xuất hóa đơn.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Xuất hoá đơn #" + currentHoaDonId + " thành công!");
    }//GEN-LAST:event_btnIssueIvoiceActionPerformed

    private void btnAddCustomerActionPerformed(java.awt.event.ActionEvent evt) {
        // Nếu dialog đã tồn tại, dispose nó trước
        if (customerDialog != null) {
            customerDialog.dispose();
        }

        // Tạo dialog mới
        customerDialog = new CreateCustomerView();

        // Set callback để cập nhật cbbCustomer và biến kh khi khách hàng được thêm
        customerDialog.setOnCustomerAddedListener(khachHangMoi -> {
            //  Cập nhật biến kh với object có ID chính xác từ database
            kh = khachHangMoi;

            //  Cập nhật Listkh để ActionListener có thể tìm thấy
            Listkh.clear();
            Listkh.add(khachHangMoi);

            //  Cập nhật text trong cbbCustomer
            isSelectingCustomer = true;
            JTextField editor = (JTextField) cbbCustomer.getEditor().getEditorComponent();
            if (khachHangMoi.getSoDienThoai() != null && !khachHangMoi.getSoDienThoai().isEmpty()) {
                editor.setText(khachHangMoi.getTenKhachHang() + " - " + khachHangMoi.getSoDienThoai());
            } else {
                editor.setText(khachHangMoi.getTenKhachHang());
            }
            isSelectingCustomer = false;
        });

        customerDialog.setLocationRelativeTo(null);
        customerDialog.setVisible(true);
    }

    private void tblPaidMouseClicked(java.awt.event.MouseEvent evt) {
        int row = tblPaid.getSelectedRow();
        if (row >= 0) {
            int hoaDonId = (int) tblPaid.getValueAt(row, 0);
            loadTableCart(hoaDonId);
            loadFormInvoice(hoaDonId);
            currentHoaDonId = hoaDonId;
            btnAdd.setEnabled(false);
            btnCreateInvoice.setEnabled(false);
            btnSave.setEnabled(false);
            btnPaid.setEnabled(false);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAddCustomer;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCreateInvoice;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnIssueIvoice;
    private javax.swing.JButton btnPaid;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cbbCustomer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel pnLeft1;
    private javax.swing.JPanel pnLeft2;
    private javax.swing.JPanel pnLeft3;
    private javax.swing.JSpinner spnNumberAdd;
    private javax.swing.JSpinner spnNumberDelete;
    private javax.swing.JTable tblCart;
    private javax.swing.JTable tblPaid;
    private javax.swing.JTable tblProduct;
    private javax.swing.JTable tblUnPaid;
    private javax.swing.JTextField txtChange;
    private javax.swing.JTextField txtGiveMoney;
    private javax.swing.JTextField txtIdInvoice;
    private javax.swing.JTextField txtMoneyPaid;
    private javax.swing.JTextField txtSale;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSumMoney;
    private javax.swing.JTextField txtTimeCreate;
    private javax.swing.JTextField txtUseCreate;
    // End of variables declaration//GEN-END:variables
}
