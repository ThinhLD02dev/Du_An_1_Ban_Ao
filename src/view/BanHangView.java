/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import jdbc.DbConnection;
import model.HoaDon;
import model.KhachHang;
import repository.HoaDonChiTietRepository;
import repository.HoaDonRepository;
import repository.KhachHangRepository;
import repository.NhanVienRepository;
import repository.SanPhamChiTietRepository;
import repository.SanPhamRepository;

/**
 *
 * @author ngocp
 */
public class BanHangView extends javax.swing.JPanel {

    SanPhamRepository spRepo = new SanPhamRepository();
    SanPhamChiTietRepository spctRepo = new SanPhamChiTietRepository();
    HoaDonRepository hdRepo = new HoaDonRepository();
    KhachHangRepository khRepo = new KhachHangRepository();
    HoaDonChiTietRepository hdctRepo = new HoaDonChiTietRepository();
    NhanVienRepository nvRepo = new NhanVienRepository();
    private int currentHoaDonId = -1;
    private List<Map<String, Object>> listSP = new ArrayList<>();
    private List<Map<String, Object>> listCart = new ArrayList<>();
    private List<Map<String, Object>> listHD = new ArrayList<>();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("ss:mm:HH dd/MM/yyyy");
    private Integer idNhanVien;
    
    // Biến cho tìm kiếm khách hàng
    private KhachHang kh = null;
    private List<KhachHang> Listkh = new ArrayList<>();
    private Timer searchTimer; // Timer để debounce tìm kiếm
    private boolean isSelectingCustomer = false; // Flag khi đang chọn từ dropdown

    /**
     * Creates new form BanHangView
     */
    public BanHangView(Integer id) {
        initComponents();
        loadTableProduct();
        this.idNhanVien = id;
        loadTableUnpaid();
        btnAdd.setEnabled(false);
        btnSave.setEnabled(false);

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
        
        // Khởi tạo Timer debounce (300ms)
        searchTimer = new Timer(300, e -> doCustomerSearch());
        searchTimer.setRepeats(false);

        // Sử dụng KeyAdapter thay vì DocumentListener để tránh lỗi
        javax.swing.JTextField editorField = (javax.swing.JTextField) cbbCustomer.getEditor().getEditorComponent();
        editorField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // Bỏ qua các phím điều hướng
                if (e.getKeyCode() == KeyEvent.VK_UP ||
                    e.getKeyCode() == KeyEvent.VK_DOWN ||
                    e.getKeyCode() == KeyEvent.VK_ENTER ||
                    e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    return;
                }

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
    }
    
    public void loadTableUnpaid() {
        DefaultTableModel model = (DefaultTableModel) tblUnPaid.getModel();
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
        model.setRowCount(0);

        listSP = spctRepo.getAllbySell();

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

    public void loadFormInvoice(int hoaDonId) {
        Map<String, Object> hd = hdRepo.findById(hoaDonId);
        if (hd != null) {
            txtIdInvoice.setText(String.valueOf(hd.get("id")));
            isSelectingCustomer = true;
            cbbCustomer.getEditor().setItem((String) hd.get("tenKhachHang"));
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
            txtSumMoney.setText(tongTien != null ? String.valueOf(tongTien) : "0");
            
            txtSale.setText((String) hd.get("maGiamGia"));
            
            BigDecimal tienThanhToan = (BigDecimal) hd.get("tienThanhToan");
            txtMoneyPaid.setText(tienThanhToan != null ? String.valueOf(tienThanhToan) : "0");
            
            BigDecimal tienNhan = (BigDecimal) hd.get("tienNhan");
            txtGiveMoney.setText(tienNhan != null ? String.valueOf(tienNhan) : "0");
            
            BigDecimal tienThua = (BigDecimal) hd.get("tienThua");
            txtChange.setText(tienThua != null ? String.valueOf(tienThua) : "0");
        } else {
            // Reset form nếu không tìm thấy
            txtIdInvoice.setText("");
            isSelectingCustomer = true;
            cbbCustomer.getEditor().setItem("");
            isSelectingCustomer = false;
            txtTimeCreate.setText("");
            txtUseCreate.setText("");
            txtSumMoney.setText("");
            txtSale.setText("");
            txtMoneyPaid.setText("");
            txtGiveMoney.setText("");
            txtChange.setText("");
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
        javax.swing.JTextField editor = (javax.swing.JTextField) cbbCustomer.getEditor().getEditorComponent();
        String keyword = editor.getText().trim();

        if (keyword.isEmpty() || keyword.length() < 1) {
            cbbCustomer.hidePopup();
            Listkh.clear();
            kh = null;
            return;
        }

        // Tìm kiếm khách hàng theo tên
        Listkh =khRepo.search(keyword);

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
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblProduct = new javax.swing.JTable();
        jPanel11 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        txtNumberAdd = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        btnCreateInvoice = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtIdInvoice = new javax.swing.JTextField();
        txtTimeCreate = new javax.swing.JTextField();
        txtUseCreate = new javax.swing.JTextField();
        txtSumMoney = new javax.swing.JTextField();
        txtSale = new javax.swing.JTextField();
        txtMoneyPaid = new javax.swing.JTextField();
        txtGiveMoney = new javax.swing.JTextField();
        txtChange = new javax.swing.JTextField();
        btnRefresh = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnAddCustomer = new javax.swing.JButton();
        cbbCustomer = new javax.swing.JComboBox<>();
        jPanel8 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        btnIssueIvoice = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCart = new javax.swing.JTable();
        jPanel12 = new javax.swing.JPanel();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        txtNumberDelete = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        tblPaid = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUnPaid = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        jPanel5.setBackground(new java.awt.Color(255, 255, 102));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Sản phẩm", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N

        tblProduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tên ", "Size", "Màu", "Mô tả", "Số lượng tồn", "Đơn giá"
            }
        ));
        jScrollPane4.setViewportView(tblProduct);

        jLabel11.setText("Tìm kiếm");

        btnSearch.setText("Tìm");

        btnAdd.setText("Thêm");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtNumberAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAdd)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch)
                    .addComponent(btnAdd)
                    .addComponent(txtNumberAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(51, 255, 51));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tạo hoá đơn", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N

        btnCreateInvoice.setText("Tạo hoá đơn");
        btnCreateInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateInvoiceActionPerformed(evt);
            }
        });

        btnCancel.setText("Huỷ hoá đơn");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(btnCreateInvoice)
                .addGap(18, 18, 18)
                .addComponent(btnCancel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreateInvoice)
                    .addComponent(btnCancel))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jLabel1.setText("Mã HĐ");

        jLabel2.setText("Khách hàng");

        jLabel3.setText("Thời gian tạo");

        jLabel4.setText("Người tạo");

        jLabel5.setText("Tổng tiền");

        jLabel7.setText("Mã giảm giá");

        jLabel8.setText("Tiền cần thanh toán");

        jLabel9.setText("Tiền khách đưa");

        jLabel10.setText("Tiền thừa");

        txtIdInvoice.setEditable(false);
        txtIdInvoice.setEnabled(false);

        txtTimeCreate.setEditable(false);
        txtTimeCreate.setEnabled(false);

        txtUseCreate.setEditable(false);
        txtUseCreate.setEnabled(false);

        txtSumMoney.setEditable(false);
        txtSumMoney.setEnabled(false);

        txtMoneyPaid.setEditable(false);
        txtMoneyPaid.setEnabled(false);

        txtChange.setEditable(false);
        txtChange.setEnabled(false);

        btnRefresh.setText("Làm mới");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        jLabel6.setForeground(new java.awt.Color(255, 0, 0));
        jLabel6.setText("Chưa có mã!!!");

        btnSave.setText("Tạm lưu");

        btnAddCustomer.setText("+");

        cbbCustomer.setEditable(true);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnRefresh))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7)
                            .addComponent(jLabel9)
                            .addComponent(jLabel8)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(cbbCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAddCustomer, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE))
                            .addComponent(txtTimeCreate, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtUseCreate, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtSumMoney, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtGiveMoney, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtChange, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtMoneyPaid, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtSale, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtIdInvoice))))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtIdInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(btnAddCustomer)
                    .addComponent(cbbCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtTimeCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtUseCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtSumMoney, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtSale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtMoneyPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtGiveMoney, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtChange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRefresh)
                    .addComponent(btnSave))
                .addContainerGap())
        );

        jButton3.setText("Thanh toán");

        btnIssueIvoice.setText("Xuất hoá đơn");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addComponent(btnIssueIvoice)
                .addGap(27, 27, 27))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(btnIssueIvoice))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 0, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Giỏ hàng", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N

        tblCart.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tên ", "Size", "Màu sắc", "Số lượng mua", "Đơn giá", "Thành tiền"
            }
        ));
        jScrollPane3.setViewportView(tblCart);

        btnDelete.setText("Xoá");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnClear.setText("Xoá hết");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnDelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNumberDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnClear)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDelete)
                    .addComponent(btnClear)
                    .addComponent(txtNumberDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.setBackground(new java.awt.Color(102, 204, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Danh sách hoá đơn", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N

        tblUnPaid.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Mã hoá đơn", "Thời gian tạo", "Khách hàng", "Người tạo"
            }
        ));
        tblUnPaid.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUnPaidMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblUnPaid);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
        );

        tblPaid.addTab("Chưa thanh toán", jPanel9);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã hoá đơn", "Ngày thanh toán", "Khách hàng", "Người tạo"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
        );

        tblPaid.addTab("Đã thanh toán", jPanel10);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tblPaid)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tblPaid)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        int row = tblProduct.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm trước!");
            return;
        }

        int numberAdd = 1;
        String index = txtNumberAdd.getText().trim();
        if (!index.isEmpty()) {
            try {
                numberAdd = Integer.parseInt(index);
                if (numberAdd <= 0) {
                    JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!");
                return;
            }
        }

        int hoaDonId = currentHoaDonId;
        int sanPhamChiTietId = (Integer) listSP.get(row).get("id");

        if (hdctRepo.existsInCart(hoaDonId, sanPhamChiTietId)) {
            hdctRepo.updateCart(hoaDonId, sanPhamChiTietId, numberAdd);
            loadFormInvoice(hoaDonId);
        } else {
            hdctRepo.insertToCart(hoaDonId, sanPhamChiTietId, numberAdd);
            loadFormInvoice(hoaDonId);
        }

        loadTableCart(hoaDonId);
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnCreateInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateInvoiceActionPerformed
        // Ưu tiên lấy từ selectedCustomer nếu đã chọn
        int khachHangId;
        String tenKhachHang;

        if (kh != null) {
            khachHangId = kh.getId();
            tenKhachHang = kh.getTenKhachHang();
        } else {
            // Lấy text từ combobox
            tenKhachHang = ((javax.swing.JTextField) cbbCustomer.getEditor().getEditorComponent()).getText().trim();

            // Nếu trống, mặc định là "Khách lẻ"
            if (tenKhachHang.isEmpty()) {
                tenKhachHang = "Khách lẻ";
                isSelectingCustomer = true;
                cbbCustomer.getEditor().setItem(tenKhachHang);
                isSelectingCustomer = false;
            }

            // Tìm ID từ tên
            khachHangId = khRepo.getIdByName(tenKhachHang);
            if (khachHangId == -1) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng với tên: " + tenKhachHang + "\nVui lòng chọn khách hàng từ danh sách hoặc thêm khách hàng mới.");
                return;
            }
        }

        HoaDon hd = new HoaDon();
        hd.setKhachHangId(khachHangId);
        hd.setNhanVienId(idNhanVien);

        int id = hdRepo.createInvoice(hd);
        if (id > 0) {
            JOptionPane.showMessageDialog(this, "Tạo hóa đơn thành công!");
            currentHoaDonId = id;
            loadTableUnpaid();
            loadFormInvoice(id);
            btnAdd.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Tạo hóa đơn thất bại!");
        }
    }//GEN-LAST:event_btnCreateInvoiceActionPerformed

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
        }

    }//GEN-LAST:event_tblUnPaidMouseClicked

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int row = tblCart.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm trong giỏ hàng!");
            return;
        }

        int hdctId = Integer.parseInt(listCart.get(row).get("id").toString());
        int currentQuantity = Integer.parseInt(listCart.get(row).get("soLuong").toString());

        int numberDelete = 1;
        String index = txtNumberDelete.getText().trim();
        if (!index.isEmpty()) {
            try {
                numberDelete = Integer.parseInt(index);
                if (numberDelete <= 0) {
                    JOptionPane.showMessageDialog(this, "Số lượng xoá phải lớn hơn 0!");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số!");
                return;
            }
        }
        if (numberDelete >= currentQuantity) {
            hdctRepo.deleteCartItemByHdctId(hdctId);
        } else {
            hdctRepo.updateCartDeleteByHdctId(hdctId, numberDelete);
        }
        loadTableCart(currentHoaDonId);
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
            // Xóa chi tiết hóa đơn trước
            hdctRepo.deleteAllCartItems(currentHoaDonId);
            // Xóa hóa đơn
            boolean deleted = hdRepo.delete(currentHoaDonId);
            if (deleted) {
                JOptionPane.showMessageDialog(this, "Hủy hóa đơn thành công!");
                loadTableUnpaid();
                loadTableCart(0);
                // Reset form
                loadFormInvoice(-1); // Hoặc reset manual
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
            java.math.BigDecimal tienThanhToan = tienThanhToanStr.isEmpty() ? java.math.BigDecimal.ZERO : new java.math.BigDecimal(tienThanhToanStr);
            java.math.BigDecimal tienNhan = tienNhanStr.isEmpty() ? java.math.BigDecimal.ZERO : new java.math.BigDecimal(tienNhanStr);
            java.math.BigDecimal tienThua = tienThuaStr.isEmpty() ? java.math.BigDecimal.ZERO : new java.math.BigDecimal(tienThuaStr);

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
        
        loadTableCart(0);
        tblUnPaid.setSelectionMode(0);
    }//GEN-LAST:event_btnRefreshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAddCustomer;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCreateInvoice;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnIssueIvoice;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox<String> cbbCustomer;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable tblCart;
    private javax.swing.JTabbedPane tblPaid;
    private javax.swing.JTable tblProduct;
    private javax.swing.JTable tblUnPaid;
    private javax.swing.JTextField txtChange;
    private javax.swing.JTextField txtGiveMoney;
    private javax.swing.JTextField txtIdInvoice;
    private javax.swing.JTextField txtMoneyPaid;
    private javax.swing.JTextField txtNumberAdd;
    private javax.swing.JTextField txtNumberDelete;
    private javax.swing.JTextField txtSale;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSumMoney;
    private javax.swing.JTextField txtTimeCreate;
    private javax.swing.JTextField txtUseCreate;
    // End of variables declaration//GEN-END:variables
}
