/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view;

import java.text.DecimalFormat;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import repository.DotGiamGiaRepository;
import repository.MaGiamGiaRepository;

/**
 *
 * @author LHH05
 */
public class GiamGiaView extends javax.swing.JPanel {

    private final DotGiamGiaRepository dotGiamGiaRepo = new DotGiamGiaRepository();
    private final MaGiamGiaRepository maGiamGiaRepo = new MaGiamGiaRepository();
    private final List<Map<String, Object>> listDotGiamGia = new ArrayList<>();
    private final List<Map<String, Object>> listSPSearch = new ArrayList<>();
    private final List<Map<String, Object>> listSPSearchVoucher = new ArrayList<>();
    private final List<Map<String, Object>> listMaGiamGia = new ArrayList<>();
    private Integer selectedSanPhamId;
    private Integer selectedDotGiamGiaId;
    private Integer selectedMaGiamGiaId;
    private Integer selectedSanPhamIdSearch;
    private Integer selectedSanPhamIdSearchVoucher;
    private final Timer searchTimer;
    private final Timer voucherSearchTimer;
    private final DecimalFormat df = new DecimalFormat("#,###");
    private boolean isSelectingSP = false;
    private boolean isSelectingSPVoucher = false;

    SpinnerNumberModel SpnModel = new SpinnerNumberModel();

    public GiamGiaView() {
        initComponents();
        UI1();
        fixMaGiamGiaLayout();
        dcNgayBatDau.getJCalendar().setMinSelectableDate(new Date());
        dcNgayKetThuc.getJCalendar().setMinSelectableDate(new Date());
        dcNgayMaBD.getJCalendar().setMinSelectableDate(new Date());
        dcNgayMaKT.getJCalendar().setMinSelectableDate(new Date());

        // Cấu hình ban đầu cho Spinner giá trị
        spnGiaTri1.setModel(new SpinnerNumberModel(1, 0, 100, 1));

        // Khởi tạo Timer debounce cho tìm kiếm sản phẩm (300ms)
        searchTimer = new Timer(300, e -> doProductSearch());
        searchTimer.setRepeats(false);

        JTextField editorField = (JTextField) cbbSanPham.getEditor().getEditorComponent();
        editorField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!isSelectingSP) {
                    searchTimer.restart();
                }
            }
        });

        cbbSanPham.addActionListener(e -> {
            if ("comboBoxChanged".equals(e.getActionCommand())) {
                int index = cbbSanPham.getSelectedIndex();
                if (index >= 0 && index < listSPSearch.size()) {
                    isSelectingSP = true;
                    Map<String, Object> sp = listSPSearch.get(index);
                    selectedSanPhamIdSearch = (Integer) sp.get("id");
                    cbbSanPham.getEditor().setItem(sp.get("ten_ao"));
                    cbbSanPham.hidePopup();
                    isSelectingSP = false;
                }
            }
        });

        // Khởi tạo Timer debounce cho tìm kiếm SP Voucher (300ms)
        voucherSearchTimer = new Timer(300, e -> doProductSearchForVoucher());
        voucherSearchTimer.setRepeats(false);

        JTextField voucherEditorField = (JTextField) cbbSanPhamApDung.getEditor().getEditorComponent();
        voucherEditorField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!isSelectingSPVoucher) {
                    voucherSearchTimer.restart();
                }
            }
        });

        cbbSanPhamApDung.addActionListener(e -> {
            if ("comboBoxChanged".equals(e.getActionCommand())) {
                int index = cbbSanPhamApDung.getSelectedIndex();
                if (index >= 0 && index < listSPSearchVoucher.size()) {
                    isSelectingSPVoucher = true;
                    Map<String, Object> sp = listSPSearchVoucher.get(index);
                    selectedSanPhamIdSearchVoucher = (Integer) sp.get("id");
                    cbbSanPhamApDung.getEditor().setItem(sp.get("ten_ao"));
                    cbbSanPhamApDung.hidePopup();
                    isSelectingSPVoucher = false;
                }
            }
        });

        loadDotGiamGiaTable();
        loadTableSanPhamGiamGia();
        loadTableMaGiamGia();
        loadTableMaGiamGiaSanPham();

        rdbPhanTram.addActionListener(e -> {
            // Nếu chọn "Phần Trăm" thì giới hạn từ 1 đến 100
            spnGiaTri2.setModel(new SpinnerNumberModel(1, 1, 100, 1));
        });

        rdbCoDinh.addActionListener(e -> {
            // Nếu chọn "Cố Định" thì có thể để giới hạn khác, ví dụ từ 1 đến 100_000_000
            spnGiaTri2.setModel(new SpinnerNumberModel(100_000, 1, 100_000_000, 100_000));
        });
        rdbSanPham.addActionListener(e -> {
            // Nếu chọn "Sản phẩm" thì cbbSanPhamApDung enabled
            cbbSanPhamApDung.setEnabled(true);
        });

        rdbHoaDon.addActionListener(e -> {
            // Nếu chọn "Hoá đơn" thì cbbSanPhamApDung disabled
            cbbSanPhamApDung.setEnabled(false);
        });

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

    private void fixMaGiamGiaLayout() {
        jPanel6.removeAll();
        jPanel6.setLayout(new java.awt.BorderLayout());

        // Tạo JSplitPane để chia form và table
        javax.swing.JSplitPane splitPane = new javax.swing.JSplitPane(
                javax.swing.JSplitPane.VERTICAL_SPLIT,
                jPanel3, // Component trên
                jScrollPane2 // Component dưới
        );

        // Cài đặt vị trí chia ban đầu (400px cho form)
        splitPane.setDividerLocation(400);

        // Cho phép resize linh hoạt (60% form, 40% table)
        splitPane.setResizeWeight(0.6);

        // Thêm vào jPanel6
        jPanel6.add(splitPane, java.awt.BorderLayout.CENTER);

        jPanel6.revalidate();
        jPanel6.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        pnHeader = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        pnMain = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtTenDotGiam = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        spnGiaTri1 = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        rdbDotKhaDung = new javax.swing.JRadioButton();
        rdbDotKhongKhaDung = new javax.swing.JRadioButton();
        btnTaoDot = new javax.swing.JButton();
        btnXoaDot = new javax.swing.JButton();
        btnLamMoiDot = new javax.swing.JButton();
        dcNgayBatDau = new com.toedter.calendar.JDateChooser();
        dcNgayKetThuc = new com.toedter.calendar.JDateChooser();
        cbbSanPham = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        btnThemDot = new javax.swing.JButton();
        btnHuyDot = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDotGiamGia = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblSanPhamGiamGia = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblMaGiamGia = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtMa = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        rdbPhanTram = new javax.swing.JRadioButton();
        rdbCoDinh = new javax.swing.JRadioButton();
        spnGiaTri2 = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        rdbSanPham = new javax.swing.JRadioButton();
        rdbHoaDon = new javax.swing.JRadioButton();
        jLabel12 = new javax.swing.JLabel();
        dcNgayMaBD = new com.toedter.calendar.JDateChooser();
        jLabel13 = new javax.swing.JLabel();
        dcNgayMaKT = new com.toedter.calendar.JDateChooser();
        jLabel14 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        rdbMaKhaDung = new javax.swing.JRadioButton();
        rdbMaKhongKhaDung = new javax.swing.JRadioButton();
        btnTaoMa = new javax.swing.JButton();
        btnHuyMa = new javax.swing.JButton();
        btnLamMoiMa = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        txtGiaTriToiDa = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtGiaTriDon = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        cbbSanPhamApDung = new javax.swing.JComboBox<>();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblGiamGiaSanPham = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        pnHeader.setBackground(new java.awt.Color(74, 144, 226));
        pnHeader.setPreferredSize(new java.awt.Dimension(957, 60));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("QUẢN LÝ MÃ GIẢM GIÁ");

        javax.swing.GroupLayout pnHeaderLayout = new javax.swing.GroupLayout(pnHeader);
        pnHeader.setLayout(pnHeaderLayout);
        pnHeaderLayout.setHorizontalGroup(
            pnHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnHeaderLayout.createSequentialGroup()
                .addGap(255, 255, 255)
                .addComponent(jLabel1)
                .addContainerGap(384, Short.MAX_VALUE))
        );
        pnHeaderLayout.setVerticalGroup(
            pnHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        add(pnHeader, java.awt.BorderLayout.PAGE_START);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel4.setBackground(new java.awt.Color(245, 247, 250));
        jPanel4.setPreferredSize(new java.awt.Dimension(300, 503));

        jLabel3.setText("Tên Đợt Giảm :");

        jLabel4.setText("Giá Trị :");

        jLabel5.setText("Ngày Kết Thúc :");

        jLabel6.setText("Ngày Bắt Đầu :");

        jLabel7.setText("Trạng Thái :");

        buttonGroup1.add(rdbDotKhaDung);
        rdbDotKhaDung.setSelected(true);
        rdbDotKhaDung.setText("Khả Dụng");
        rdbDotKhaDung.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        rdbDotKhaDung.setPreferredSize(new java.awt.Dimension(113, 21));

        buttonGroup1.add(rdbDotKhongKhaDung);
        rdbDotKhongKhaDung.setText("Không Khả Dụng");

        btnTaoDot.setText("Tạo");
        btnTaoDot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTaoDotActionPerformed(evt);
            }
        });

        btnXoaDot.setText("Xoá");
        btnXoaDot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaDotActionPerformed(evt);
            }
        });

        btnLamMoiDot.setText("Làm Mới");
        btnLamMoiDot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoiDotActionPerformed(evt);
            }
        });

        cbbSanPham.setEditable(true);

        jLabel2.setText("Sản phẩm:");

        btnThemDot.setText("Thêm đợt giảm giá");
        btnThemDot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemDotActionPerformed(evt);
            }
        });

        btnHuyDot.setText("Huỷ đợt giảm giá");
        btnHuyDot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHuyDotActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTenDotGiam)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnGiaTri1)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(rdbDotKhaDung, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rdbDotKhongKhaDung))
                    .addComponent(dcNgayBatDau, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dcNgayKetThuc, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnTaoDot)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnXoaDot, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLamMoiDot, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbbSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnThemDot)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnHuyDot)))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTenDotGiam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnGiaTri1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dcNgayBatDau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dcNgayKetThuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbDotKhaDung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbDotKhongKhaDung))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTaoDot)
                    .addComponent(btnXoaDot)
                    .addComponent(btnLamMoiDot))
                .addGap(33, 33, 33)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbbSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThemDot)
                    .addComponent(btnHuyDot))
                .addContainerGap(79, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel4, java.awt.BorderLayout.LINE_START);

        tblDotGiamGia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Tên Đợt Giảm Giá", "Giá Trị ", "Ngày Bắt Đầu", "Ngày Kết Thúc", "Trạng Thái"
            }
        ));
        tblDotGiamGia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDotGiamGiaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblDotGiamGia);

        tblSanPhamGiamGia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Sản phẩm", "Đợt giảm giá được áp dụng", "Giá bán ", "Giá thực tế"
            }
        ));
        jScrollPane3.setViewportView(tblSanPhamGiamGia);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE)
                    .addComponent(jScrollPane3)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel5, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("ĐỢT GIẢM GIÁ", jPanel1);

        jPanel2.setLayout(new java.awt.BorderLayout());

        tblMaGiamGia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã", "Giá Trị", "Đang Giảm", "Loại Áp Dụng", "Start Date", "End Date", "Số Lần Dùng", "Trạng Thái"
            }
        ));
        tblMaGiamGia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMaGiamGiaMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblMaGiamGia);

        jPanel3.setBackground(new java.awt.Color(245, 247, 250));
        jPanel3.setMinimumSize(new java.awt.Dimension(100, 100));
        jPanel3.setPreferredSize(new java.awt.Dimension(300, 503));

        jLabel8.setText("Mã :");

        jLabel9.setText("Giá Trị :");

        jLabel10.setText("Đang Giảm :");

        buttonGroup2.add(rdbPhanTram);
        rdbPhanTram.setSelected(true);
        rdbPhanTram.setText("Phần Trăm");

        buttonGroup2.add(rdbCoDinh);
        rdbCoDinh.setText("Cố Định");

        jLabel11.setText("Loại áp dụng :");

        buttonGroup3.add(rdbSanPham);
        rdbSanPham.setText("Sản Phẩm");

        buttonGroup3.add(rdbHoaDon);
        rdbHoaDon.setText("Hóa Đơn");

        jLabel12.setText("Start Date :");

        jLabel13.setText("End Date :");

        jLabel14.setText("Số Lần Dùng :");

        jLabel15.setText("Trạng Thái :");

        buttonGroup4.add(rdbMaKhaDung);
        rdbMaKhaDung.setSelected(true);
        rdbMaKhaDung.setText("Khả Dụng");

        buttonGroup4.add(rdbMaKhongKhaDung);
        rdbMaKhongKhaDung.setText("Không Khả Dụng");

        btnTaoMa.setText("Tạo");
        btnTaoMa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTaoMaActionPerformed(evt);
            }
        });

        btnHuyMa.setText("Hủy");
        btnHuyMa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHuyMaActionPerformed(evt);
            }
        });

        btnLamMoiMa.setText("Làm Mới");
        btnLamMoiMa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoiMaActionPerformed(evt);
            }
        });

        jLabel16.setText("Giá trị giảm tối đa:");

        jLabel17.setText("Giá trị đơn tối thiểu:");

        jLabel18.setText("Sản phẩm áp dụng:");

        cbbSanPhamApDung.setEditable(true);
        cbbSanPhamApDung.setEnabled(false);

        tblGiamGiaSanPham.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Mã", "Sản phẩm"
            }
        ));
        tblGiamGiaSanPham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblGiamGiaSanPhamMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblGiamGiaSanPham);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtGiaTriDon, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtGiaTriToiDa, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 206, Short.MAX_VALUE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel8)
                                            .addComponent(jLabel9)
                                            .addComponent(txtMa, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                                            .addComponent(spnGiaTri2)))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(43, 43, 43)
                                        .addComponent(rdbPhanTram)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(rdbCoDinh)
                                        .addGap(37, 37, 37)))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jLabel16))))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel17))))
                .addGap(24, 24, 24)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(48, 48, 48)
                                .addComponent(rdbHoaDon)))
                        .addGap(55, 55, 55)
                        .addComponent(rdbSanPham))
                    .addComponent(jLabel18)
                    .addComponent(cbbSanPhamApDung, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnTaoMa)
                        .addGap(18, 18, 18)
                        .addComponent(btnHuyMa, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnLamMoiMa))
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(rdbMaKhaDung)
                        .addGap(92, 92, 92)
                        .addComponent(rdbMaKhongKhaDung)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(dcNgayMaKT, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(dcNgayMaBD, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dcNgayMaBD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dcNgayMaKT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel8)
                                                .addGap(6, 6, 6)
                                                .addComponent(txtMa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel11)
                                                .addGap(6, 6, 6)
                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(rdbSanPham)
                                                    .addComponent(rdbHoaDon))))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel10)
                                            .addComponent(jLabel18))
                                        .addGap(5, 5, 5)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(rdbPhanTram)
                                            .addComponent(rdbCoDinh)
                                            .addComponent(cbbSanPhamApDung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel9)
                                            .addComponent(jLabel14))
                                        .addGap(6, 6, 6)
                                        .addComponent(spnGiaTri2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtGiaTriToiDa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(6, 6, 6)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(rdbMaKhaDung)
                                    .addComponent(rdbMaKhongKhaDung))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtGiaTriDon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnTaoMa)
                            .addComponent(btnHuyMa)
                            .addComponent(btnLamMoiMa))))
                .addGap(279, 279, 279))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 939, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel6, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("MÃ GIẢM GIÁ", jPanel2);

        javax.swing.GroupLayout pnMainLayout = new javax.swing.GroupLayout(pnMain);
        pnMain.setLayout(pnMainLayout);
        pnMainLayout.setHorizontalGroup(
            pnMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        pnMainLayout.setVerticalGroup(
            pnMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnMainLayout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        add(pnMain, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void tblDotGiamGiaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDotGiamGiaMouseClicked
        int row = tblDotGiamGia.getSelectedRow();
        if (row < 0 || row >= listDotGiamGia.size()) {
            return;
        }

        Map<String, Object> data = listDotGiamGia.get(row);
        selectedDotGiamGiaId = (Integer) data.get("dotGiamId");
        // Lưu ý: selectedSanPhamId sẽ không còn quan trọng nếu bạn áp dụng cho nhiều SP

        txtTenDotGiam.setText(data.get("tenDot") != null ? (String) data.get("tenDot") : "");
        Integer giaTriValue = (Integer) data.get("giaTri");
        spnGiaTri1.setValue(giaTriValue != null ? giaTriValue : 1);
        dcNgayBatDau.setDate((Date) data.get("ngayBatDau"));
        dcNgayKetThuc.setDate((Date) data.get("ngayKetThuc"));
        Object tt = data.get("trangThai");
        if (tt instanceof Integer && (Integer) tt == 1) {
            rdbDotKhaDung.setSelected(true);
        } else {
            rdbDotKhongKhaDung.setSelected(true);
        }
    }//GEN-LAST:event_tblDotGiamGiaMouseClicked

    private void tblMaGiamGiaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMaGiamGiaMouseClicked
        int row = tblMaGiamGia.getSelectedRow();
        if (row < 0 || row >= listMaGiamGia.size()) {
            return;
        }

        Map<String, Object> data = listMaGiamGia.get(row);
        selectedMaGiamGiaId = (Integer) data.get("id");

        txtMa.setText(data.get("ma") != null ? (String) data.get("ma") : "");

        Integer loaiGiam = (Integer) data.get("loaiGiam");
        if (loaiGiam != null && loaiGiam == 1) { // 1 là Phần trăm
            rdbPhanTram.setSelected(true);
            spnGiaTri2.setModel(new SpinnerNumberModel(1, 1, 100, 1));
        } else {
            rdbCoDinh.setSelected(true);
            spnGiaTri2.setModel(new SpinnerNumberModel(100_000, 1, 100_000_000, 100_000));
        }

        Object giaTriObj = data.get("giaTri");
        if (giaTriObj != null) {
            // Chuyển đổi giá trị về kiểu int để set vào Spinner
            spnGiaTri2.setValue(Double.valueOf(giaTriObj.toString()).intValue());
        }

        Integer loaiApDung = (Integer) data.get("loaiApDung");
        if (loaiApDung != null && loaiApDung == 1) {
            rdbSanPham.setSelected(true);
            cbbSanPhamApDung.setEnabled(true);
        } else {
            rdbHoaDon.setSelected(true);
            cbbSanPhamApDung.setEnabled(false);
        }

        dcNgayMaBD.setDate((Date) data.get("ngayBatDau"));
        dcNgayMaKT.setDate((Date) data.get("ngayKetThuc"));
        jTextField6.setText(data.get("soLanDung") != null ? data.get("soLanDung").toString() : "");

        Integer trangThai = (Integer) data.get("trangThai");
        if (trangThai != null && trangThai == 1) {
            rdbMaKhaDung.setSelected(true);
        } else {
            rdbMaKhongKhaDung.setSelected(true);
        }

        txtGiaTriToiDa.setText(data.get("giaTriToiDa") != null ? data.get("giaTriToiDa").toString() : "0");
        txtGiaTriDon.setText(data.get("donToiThieu") != null ? data.get("donToiThieu").toString() : "0");
    }//GEN-LAST:event_tblMaGiamGiaMouseClicked

    private void btnThemDotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemDotActionPerformed
        if (selectedDotGiamGiaId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đợt giảm giá từ bảng bên phải!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedSanPhamIdSearch == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần áp dụng giảm giá!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (dotGiamGiaRepo.attachDiscountToProduct(selectedSanPhamIdSearch, selectedDotGiamGiaId)) {
            JOptionPane.showMessageDialog(this, "Áp dụng giảm giá thành công cho sản phẩm.");
            loadTableSanPhamGiamGia();
        } else {
            JOptionPane.showMessageDialog(this, "Thao tác thất bại. Vui lòng kiểm tra lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnThemDotActionPerformed

    private void btnLamMoiDotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiDotActionPerformed
        clearDotGiamGiaForm();
        loadDotGiamGiaTable();
    }//GEN-LAST:event_btnLamMoiDotActionPerformed

    private void btnTaoDotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTaoDotActionPerformed
        onCreateDotGiamGia();
    }//GEN-LAST:event_btnTaoDotActionPerformed

    private void btnTaoMaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTaoMaActionPerformed
        onCreateMaGiamGia();
    }//GEN-LAST:event_btnTaoMaActionPerformed

    private void btnHuyMaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHuyMaActionPerformed
        onHuyMaGiamGia();
    }//GEN-LAST:event_btnHuyMaActionPerformed

    private void btnLamMoiMaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiMaActionPerformed
        clearMaGiamGiaForm();
    }//GEN-LAST:event_btnLamMoiMaActionPerformed

    private void btnXoaDotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaDotActionPerformed
        onDeleteDotGiamGia();
    }//GEN-LAST:event_btnXoaDotActionPerformed

    private void tblGiamGiaSanPhamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblGiamGiaSanPhamMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblGiamGiaSanPhamMouseClicked

    private void btnHuyDotActionPerformed(java.awt.event.ActionEvent evt) {
        if (selectedSanPhamIdSearch == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần huỷ đợt giảm giá!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn huỷ áp dụng giảm giá cho sản phẩm này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dotGiamGiaRepo.detachDiscountFromProduct(selectedSanPhamIdSearch)) {
                JOptionPane.showMessageDialog(this, "Đã huỷ áp dụng giảm giá.");
                loadTableSanPhamGiamGia();
            } else {
                JOptionPane.showMessageDialog(this, "Thao tác thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void doProductSearch() {
        JTextField editor = (JTextField) cbbSanPham.getEditor().getEditorComponent();
        String keyword = editor.getText().trim();

        if (keyword.isEmpty()) {
            cbbSanPham.hidePopup();
            listSPSearch.clear();
            selectedSanPhamIdSearch = null;
            return;
        }

        listSPSearch.clear();
        listSPSearch.addAll(dotGiamGiaRepo.searchSanPham(keyword));

        if (listSPSearch.isEmpty()) {
            cbbSanPham.hidePopup();
            selectedSanPhamIdSearch = null;
            return;
        }

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (Map<String, Object> sp : listSPSearch) {
            model.addElement((String) sp.get("ten_ao"));
        }

        String currentText = editor.getText();
        int caretPos = editor.getCaretPosition();

        isSelectingSP = true;
        cbbSanPham.setModel(model);
        cbbSanPham.setSelectedIndex(-1);
        editor.setText(currentText);
        if (caretPos <= currentText.length()) {
            editor.setCaretPosition(caretPos);
        }
        isSelectingSP = false;
        cbbSanPham.showPopup();
    }

    private void doProductSearchForVoucher() {
        JTextField editor = (JTextField) cbbSanPhamApDung.getEditor().getEditorComponent();
        String keyword = editor.getText().trim();

        if (keyword.isEmpty()) {
            cbbSanPhamApDung.hidePopup();
            listSPSearchVoucher.clear();
            selectedSanPhamIdSearchVoucher = null;
            return;
        }

        listSPSearchVoucher.clear();
        listSPSearchVoucher.addAll(dotGiamGiaRepo.searchSanPham(keyword));

        if (listSPSearchVoucher.isEmpty()) {
            cbbSanPhamApDung.hidePopup();
            selectedSanPhamIdSearchVoucher = null;
            return;
        }

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (Map<String, Object> sp : listSPSearchVoucher) {
            model.addElement((String) sp.get("ten_ao"));
        }

        String currentText = editor.getText();
        int caretPos = editor.getCaretPosition();

        isSelectingSPVoucher = true;
        cbbSanPhamApDung.setModel(model);
        cbbSanPhamApDung.setSelectedIndex(-1);
        editor.setText(currentText);
        if (caretPos <= currentText.length()) {
            editor.setCaretPosition(caretPos);
        }
        isSelectingSPVoucher = false;
        cbbSanPhamApDung.showPopup();
    }

    private void loadTableMaGiamGiaSanPham() {
        DefaultTableModel model = (DefaultTableModel) tblGiamGiaSanPham.getModel();
        model.setRowCount(0);
        List<Map<String, String>> list = maGiamGiaRepo.getVoucherProductMappings();
        for (Map<String, String> m : list) {
            model.addRow(new Object[]{
                m.get("ma"),
                m.get("tenAo")
            });
        }
    }

    private void loadTableMaGiamGia() {
        DefaultTableModel model = (DefaultTableModel) tblMaGiamGia.getModel();
        model.setRowCount(0);

        listMaGiamGia.clear();
        listMaGiamGia.addAll(maGiamGiaRepo.getAll());

        for (Map<String, Object> m : listMaGiamGia) {
            Integer loaiGiamVal = (Integer) m.get("loaiGiam");
            int dangGiam = loaiGiamVal != null ? loaiGiamVal : 0;
            String loaiGiamStr = (dangGiam == 1) ? "%" : "VNĐ";

            String loaiAp = (int) m.get("loaiApDung") == 1 ? "Sản phẩm" : "Hóa đơn";
            String status = (int) m.get("trangThai") == 1 ? "Khả dụng" : "Ngừng";

            // Format hiển thị giá trị: Nếu là tiền thì dùng df, nếu % thì giữ nguyên
            String hienThiGiaTri = (dangGiam == 1)
                    ? (m.get("giaTri") != null ? m.get("giaTri").toString() + "%" : "0%")
                    : (m.get("giaTri") != null ? df.format(m.get("giaTri")) : "0");

            model.addRow(new Object[]{
                m.get("ma"),
                hienThiGiaTri,
                loaiGiamStr,
                loaiAp,
                m.get("ngayBatDau"),
                m.get("ngayKetThuc"),
                m.get("soLanDung"),
                status
            });
        }
    }

    private void loadTableSanPhamGiamGia() {
        DefaultTableModel model = (DefaultTableModel) tblSanPhamGiamGia.getModel();
        model.setRowCount(0);
        List<Map<String, Object>> list = dotGiamGiaRepo.getProductsWithDiscounts();
        for (Map<String, Object> item : list) {
            model.addRow(new Object[]{
                item.get("tenSanPham"),
                item.get("tenDot"),
                df.format(item.get("giaBan")),
                df.format(item.get("giaThucTe"))
            });
        }
    }

    private void loadDotGiamGiaTable() {
        DefaultTableModel model = (DefaultTableModel) tblDotGiamGia.getModel();
        model.setRowCount(0);
        listDotGiamGia.clear();
        listDotGiamGia.addAll(dotGiamGiaRepo.getAllWithProductName());

        for (Map<String, Object> rowData : listDotGiamGia) {
            Object trangThai = rowData.get("trangThai");
            String status = "";
            if (rowData.get("dotGiamId") != null) {
                status = "Không Khả Dụng";
                if (trangThai instanceof Integer && (Integer) trangThai == 1) {
                    status = "Khả Dụng";
                }
            }
            model.addRow(new Object[]{
                rowData.get("tenDot"),
                rowData.get("giaTri"),
                rowData.get("ngayBatDau"),
                rowData.get("ngayKetThuc"),
                status
            });
        }
    }

    private void clearDotGiamGiaForm() {
        selectedDotGiamGiaId = null;
        selectedSanPhamId = null;
        txtTenDotGiam.setText("");
        spnGiaTri1.setValue(1);
        dcNgayBatDau.setDate(null);
        dcNgayKetThuc.setDate(null);
        rdbDotKhaDung.setSelected(true);
        tblDotGiamGia.clearSelection();
        selectedSanPhamIdSearch = null;
        isSelectingSP = true;
        cbbSanPham.setSelectedIndex(-1);
        ((JTextField) cbbSanPham.getEditor().getEditorComponent()).setText("");
        isSelectingSP = false;
    }

    private void onHuyMaGiamGia() {
        if (selectedMaGiamGiaId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn mã giảm giá cần xoá từ bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Kiểm tra xem mã đã từng được sử dụng trong bất kỳ hóa đơn nào chưa
        if (maGiamGiaRepo.isUsedInAnyInvoice(selectedMaGiamGiaId)) {
            JOptionPane.showMessageDialog(this,
                    "Không thể xoá vĩnh viễn! Mã giảm giá này đã có lịch sử sử dụng trong hóa đơn.\n"
                    + "Để đảm bảo tính toàn vẹn dữ liệu, bạn chỉ nên chuyển trạng thái sang 'Ngừng kích hoạt'.",
                    "Cảnh báo", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Xác nhận xoá
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn XOÁ VĨNH VIỄN mã giảm giá này không?", "Xác nhận xoá", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (maGiamGiaRepo.delete(selectedMaGiamGiaId)) {
                JOptionPane.showMessageDialog(this, "Đã xoá mã giảm giá thành công.");
                clearMaGiamGiaForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xoá mã giảm giá thất bại. Vui lòng thử lại sau.");
            }
        }
    }

    private void clearMaGiamGiaForm() {
        selectedMaGiamGiaId = null;
        txtMa.setText("");
        // Mặc định chọn phần trăm và reset spinner về range 1-100
        rdbPhanTram.setSelected(true);
        spnGiaTri2.setModel(new SpinnerNumberModel(1, 1, 100, 1));
        rdbSanPham.setSelected(true);
        dcNgayMaBD.setDate(null);
        dcNgayMaKT.setDate(null);
        jTextField6.setText("");
        txtGiaTriToiDa.setText("");
        txtGiaTriDon.setText("");
        rdbMaKhaDung.setSelected(true);
        tblMaGiamGia.clearSelection();
        selectedSanPhamIdSearchVoucher = null;
        ((JTextField) cbbSanPhamApDung.getEditor().getEditorComponent()).setText("");
        loadTableMaGiamGia();
        loadTableMaGiamGiaSanPham();
    }

    private void onDeleteDotGiamGia() {
        if (selectedDotGiamGiaId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đợt giảm giá cần xoá từ bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Kiểm tra xem đợt giảm giá có đang được áp dụng cho sản phẩm nào không
        if (dotGiamGiaRepo.isCampaignUsed(selectedDotGiamGiaId)) {
            JOptionPane.showMessageDialog(this,
                    "Không thể xoá! Đợt giảm giá này đang được áp dụng cho một hoặc nhiều sản phẩm.\n"
                    + "Vui lòng huỷ áp dụng (gỡ bỏ) khỏi các sản phẩm đó trước khi xoá.",
                    "Cảnh báo", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Xác nhận và thực hiện xoá
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xoá đợt giảm giá này?", "Xác nhận xoá", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dotGiamGiaRepo.deleteCampaign(selectedDotGiamGiaId)) {
                JOptionPane.showMessageDialog(this, "Đã xoá đợt giảm giá thành công.");
                loadDotGiamGiaTable();
                clearDotGiamGiaForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xoá thất bại. Vui lòng thử lại sau.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onCreateMaGiamGia() {
        String ma = txtMa.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã không được để trống!");
            return;
        }

        // Kiểm tra mã đã tồn tại
        if (maGiamGiaRepo.isCodeExists(ma)) {
            JOptionPane.showMessageDialog(this, "Mã giảm giá đã tồn tại! Vui lòng chọn mã khác.");
            return;
        }

        double giaTri = Double.parseDouble(spnGiaTri2.getValue().toString());
        int loaiGiam = rdbPhanTram.isSelected() ? 1 : 0; // 1: %, 0: VNĐ

        // Validation cho giá trị giảm
        if (loaiGiam == 1 && (giaTri <= 0 || giaTri > 100)) {
            JOptionPane.showMessageDialog(this, "Giá trị giảm phần trăm phải từ 1% đến 100%!");
            return;
        }
        if (loaiGiam == 0 && giaTri <= 0) {
            JOptionPane.showMessageDialog(this, "Giá trị giảm cố định phải lớn hơn 0!");
            return;
        }

        int loaiAp = rdbSanPham.isSelected() ? 1 : 0;

        Date start = dcNgayMaBD.getDate();
        Date end = dcNgayMaKT.getDate();

        if (start == null || end == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày hiệu lực!");
            return;
        }

        if (end.before(start)) {
            JOptionPane.showMessageDialog(this, "Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu!");
            return;
        }

        int soLuong = 0;
        try {
            soLuong = Integer.parseInt(jTextField6.getText().trim());
            if (soLuong <= 0) {
                JOptionPane.showMessageDialog(this, "Số lần dùng phải lớn hơn 0!");
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Số lần dùng phải là số nguyên dương!");
            return;
        }

        double giaToiDa = 0;
        double donToiThieu = 0;
        try {
            giaToiDa = Double.parseDouble(txtGiaTriToiDa.getText().trim().isEmpty() ? "0" : txtGiaTriToiDa.getText().trim());
            donToiThieu = Double.parseDouble(txtGiaTriDon.getText().trim().isEmpty() ? "0" : txtGiaTriDon.getText().trim());
            if (giaToiDa < 0 || donToiThieu < 0) {
                JOptionPane.showMessageDialog(this, "Giá trị tối đa và đơn tối thiểu không được âm!");
                return;
            }

            // Logic nghiệp vụ: Nếu giảm theo cố định VNĐ
            if (loaiGiam == 0) {
                giaToiDa = giaTri; // Giảm tối đa chính là số tiền đó
            } else {
                // Nếu giảm theo % mà không nhập giá trị tối đa, có thể cảnh báo hoặc mặc định cực lớn
                if (giaToiDa == 0) {
                    giaToiDa = 100000000; // Mặc định không giới hạn nếu để 0
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá trị tối đa và đơn tối thiểu phải là số!");
            return;
        }

        int status = rdbMaKhaDung.isSelected() ? 1 : 0;

        // Nếu là loại SP, yêu cầu phải chọn SP trước
        if (loaiAp == 1 && selectedSanPhamIdSearchVoucher == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm áp dụng mã!");
            return;
        }

        int newVoucherId = maGiamGiaRepo.insert(ma, giaTri, loaiGiam, loaiAp,
                new java.sql.Date(start.getTime()),
                new java.sql.Date(end.getTime()),
                soLuong, status, giaToiDa, donToiThieu);

        if (newVoucherId > 0) {
            // Nếu áp dụng cho SP cụ thể thì lưu vào bảng trung gian
            if (loaiAp == 1) {
                maGiamGiaRepo.attachProductToVoucher(newVoucherId, selectedSanPhamIdSearchVoucher);
            }
            JOptionPane.showMessageDialog(this, "Thêm mã giảm giá thành công!");
            clearMaGiamGiaForm();
            loadTableMaGiamGia();
            loadTableMaGiamGiaSanPham();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại! Vui lòng thử lại.");
        }
    }

    private void onCreateDotGiamGia() {
        // 1. Thu thập dữ liệu từ Form
        String tenDot = txtTenDotGiam.getText().trim();
        if (tenDot.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đợt giảm không được để trống.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date ngayBatDau = dcNgayBatDau.getDate();
        Date ngayKetThuc = dcNgayKetThuc.getDate();
        if (ngayBatDau == null || ngayKetThuc == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày bắt đầu và ngày kết thúc.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (ngayKetThuc.before(ngayBatDau)) {
            JOptionPane.showMessageDialog(this, "Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int giaTri = (Integer) spnGiaTri1.getValue();
        if (giaTri <= 0 || giaTri > 100) {
            JOptionPane.showMessageDialog(this, "Giá trị giảm giá phải từ 1% đến 100%.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean trangThai = rdbDotKhaDung.isSelected();

        // 2. Kiểm tra overlap với các đợt giảm giá khác
        if (trangThai && dotGiamGiaRepo.hasOverlappingCampaign(ngayBatDau, ngayKetThuc)) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Đợt giảm giá này có thể trùng thời gian với các đợt khác đang active.\nBạn có muốn tiếp tục tạo?",
                    "Cảnh báo trùng lặp", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // 2. Gọi Repository để tạo Chiến dịch giảm giá (Campaign)
        // Bạn cần tạo phương thức insertCampaign trong DotGiamGiaRepository trả về ID vừa tạo
        int newDotGiamId = dotGiamGiaRepo.insertCampaign(
                tenDot,
                giaTri,
                new java.sql.Date(ngayBatDau.getTime()),
                new java.sql.Date(ngayKetThuc.getTime()),
                trangThai
        );

        if (newDotGiamId > 0) {
            // 3. Hiển thị dialog chọn sản phẩm để áp dụng campaign
            showProductSelectionDialog(newDotGiamId);

            JOptionPane.showMessageDialog(this, "Tạo đợt giảm giá thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

            loadDotGiamGiaTable();
            loadTableSanPhamGiamGia();
            clearDotGiamGiaForm();
        } else {
            JOptionPane.showMessageDialog(this, "Tạo đợt giảm giá thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showProductSelectionDialog(int campaignId) {
        // Tạo dialog để chọn sản phẩm
        javax.swing.JDialog dialog = new javax.swing.JDialog((java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this), "Chọn sản phẩm áp dụng giảm giá", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);

        // Panel chính
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout());

        // Label hướng dẫn
        javax.swing.JLabel lblTitle = new javax.swing.JLabel("Chọn các sản phẩm để áp dụng đợt giảm giá này:");
        lblTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(lblTitle, java.awt.BorderLayout.NORTH);

        // Bảng sản phẩm với checkbox
        String[] columnNames = {"Chọn", "Tên sản phẩm", "Giá bán", "Đợt giảm hiện tại"};
        List<Map<String, Object>> products = dotGiamGiaRepo.getAllProducts();
        Object[][] data = new Object[products.size()][4];

        List<javax.swing.JCheckBox> checkBoxes = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            Map<String, Object> product = products.get(i);
            javax.swing.JCheckBox checkBox = new javax.swing.JCheckBox();
            checkBoxes.add(checkBox);

            data[i][0] = checkBox;
            data[i][1] = product.get("ten_ao");
            data[i][2] = df.format(product.get("gia_ban"));
            data[i][3] = product.get("dot_giam_id") != null ? "Có" : "Không";
        }

        javax.swing.JTable table = new javax.swing.JTable(data, columnNames) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Chỉ cho phép edit cột checkbox
            }
        };

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);

        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(table);
        panel.add(scrollPane, java.awt.BorderLayout.CENTER);

        // Panel buttons
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.FlowLayout());

        javax.swing.JButton btnSelectAll = new javax.swing.JButton("Chọn tất cả");
        btnSelectAll.addActionListener(e -> {
            for (javax.swing.JCheckBox cb : checkBoxes) {
                cb.setSelected(true);
            }
        });

        javax.swing.JButton btnDeselectAll = new javax.swing.JButton("Bỏ chọn tất cả");
        btnDeselectAll.addActionListener(e -> {
            for (javax.swing.JCheckBox cb : checkBoxes) {
                cb.setSelected(false);
            }
        });

        javax.swing.JButton btnApply = new javax.swing.JButton("Áp dụng");
        btnApply.addActionListener(e -> {
            List<Integer> selectedProductIds = new ArrayList<>();
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    selectedProductIds.add((Integer) products.get(i).get("id"));
                }
            }

            if (selectedProductIds.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn ít nhất một sản phẩm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (dotGiamGiaRepo.attachDiscountToMultipleProducts(selectedProductIds, campaignId)) {
                JOptionPane.showMessageDialog(dialog, "Áp dụng giảm giá thành công cho " + selectedProductIds.size() + " sản phẩm!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Áp dụng thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        javax.swing.JButton btnCancel = new javax.swing.JButton("Hủy");
        btnCancel.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSelectAll);
        buttonPanel.add(btnDeselectAll);
        buttonPanel.add(btnApply);
        buttonPanel.add(btnCancel);

        panel.add(buttonPanel, java.awt.BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHuyDot;
    private javax.swing.JButton btnHuyMa;
    private javax.swing.JButton btnLamMoiDot;
    private javax.swing.JButton btnLamMoiMa;
    private javax.swing.JButton btnTaoDot;
    private javax.swing.JButton btnTaoMa;
    private javax.swing.JButton btnThemDot;
    private javax.swing.JButton btnXoaDot;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.JComboBox<String> cbbSanPham;
    private javax.swing.JComboBox<String> cbbSanPhamApDung;
    private com.toedter.calendar.JDateChooser dcNgayBatDau;
    private com.toedter.calendar.JDateChooser dcNgayKetThuc;
    private com.toedter.calendar.JDateChooser dcNgayMaBD;
    private com.toedter.calendar.JDateChooser dcNgayMaKT;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JPanel pnHeader;
    private javax.swing.JPanel pnMain;
    private javax.swing.JRadioButton rdbCoDinh;
    private javax.swing.JRadioButton rdbDotKhaDung;
    private javax.swing.JRadioButton rdbDotKhongKhaDung;
    private javax.swing.JRadioButton rdbHoaDon;
    private javax.swing.JRadioButton rdbMaKhaDung;
    private javax.swing.JRadioButton rdbMaKhongKhaDung;
    private javax.swing.JRadioButton rdbPhanTram;
    private javax.swing.JRadioButton rdbSanPham;
    private javax.swing.JSpinner spnGiaTri1;
    private javax.swing.JSpinner spnGiaTri2;
    private javax.swing.JTable tblDotGiamGia;
    private javax.swing.JTable tblGiamGiaSanPham;
    private javax.swing.JTable tblMaGiamGia;
    private javax.swing.JTable tblSanPhamGiamGia;
    private javax.swing.JTextField txtGiaTriDon;
    private javax.swing.JTextField txtGiaTriToiDa;
    private javax.swing.JTextField txtMa;
    private javax.swing.JTextField txtTenDotGiam;
    // End of variables declaration//GEN-END:variables
}
