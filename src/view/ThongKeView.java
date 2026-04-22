package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;
import jdbc.DbConnection;

// JFreeChart
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;

// JDateChooser
import com.toedter.calendar.JDateChooser;

public class ThongKeView extends JPanel {

    private JLabel lblRevenue, lblOrders, lblAvg, lblProducts;
    private JTable table;
    private JPanel chartPanel;
    private JLabel lblMaxDay, lblMaxMonth, lblMaxInvoice;

    private JDateChooser dateChooser;
    private DecimalFormat df = new DecimalFormat("#,###");
    private JButton btnFilter, btnRefresh;
    private JPanel filterPanel;

    public ThongKeView() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        add(createTop(), BorderLayout.NORTH);
        add(createCenter(), BorderLayout.CENTER);
        UI();
        loadData();

    }

    // ================= TOP =================
    private JPanel createTop() {
        JPanel pn = new JPanel(new BorderLayout());
        pn.setBorder(new EmptyBorder(10, 10, 10, 10));
        pn.setBackground(getBackground());

        JPanel cards = new JPanel(new GridLayout(1, 4, 10, 0));
        cards.setBackground(getBackground());

        cards.add(createCard(" Doanh thu", c -> lblRevenue = c));
        cards.add(createCard(" Đơn hàng", c -> lblOrders = c));
        cards.add(createCard(" Trung bình", c -> lblAvg = c));
        cards.add(createCard(" Sản phẩm", c -> lblProducts = c));

        pn.add(cards, BorderLayout.CENTER);
        return pn;
    }

    private JPanel createCard(String title, java.util.function.Consumer<JLabel> set) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setForeground(Color.GRAY);

        JLabel v = new JLabel("0");
        v.setFont(new Font("Segoe UI", Font.BOLD, 20));

        set.accept(v);

        card.add(t);
        card.add(Box.createVerticalStrut(5));
        card.add(v);

        return card;
    }

    // ================= CENTER =================
    private JPanel createCenter() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(new EmptyBorder(0, 10, 10, 10));
        main.setBackground(getBackground());

        // Filter panel
        filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Lọc dữ liệu"));
        filterPanel.setBackground(Color.WHITE);

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setPreferredSize(new Dimension(150, 25));

        btnFilter = new JButton("Lọc");
        btnRefresh = new JButton("Làm mới");

        filterPanel.add(new JLabel("Chọn ngày:"));
        filterPanel.add(dateChooser);
        filterPanel.add(btnFilter);
        filterPanel.add(btnRefresh);

        btnFilter.addActionListener(e -> {
            java.util.Date selectedDate = dateChooser.getDate();
            if (selectedDate != null) {
                loadTableByDate(selectedDate);
                loadChartByDate(selectedDate);
                loadExtraStats();
            } else {
                loadData();
            }
        });
        btnRefresh.addActionListener(e -> {
            dateChooser.setDate(null);
            loadData();
        });

        // Chart
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("Biểu đồ doanh thu"));
        chartPanel.setBackground(Color.WHITE);

        // Table
        table = new JTable();
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Chi tiết"));

        // Extra stats
        JPanel extraStats = new JPanel(new GridLayout(3, 1, 5, 5));
        extraStats.setBorder(BorderFactory.createTitledBorder("Thống kê đặc biệt"));
        extraStats.setBackground(Color.WHITE);

        lblMaxDay = new JLabel("Ngày có doanh thu cao nhất: ...");
        lblMaxMonth = new JLabel("Tháng có doanh thu cao nhất: ...");
        lblMaxInvoice = new JLabel("Hóa đơn giá trị cao nhất: ...");

        extraStats.add(lblMaxDay);
        extraStats.add(lblMaxMonth);
        extraStats.add(lblMaxInvoice);

        main.add(filterPanel, BorderLayout.NORTH);
        main.add(chartPanel, BorderLayout.CENTER);
        main.add(sp, BorderLayout.SOUTH);
        main.add(extraStats, BorderLayout.EAST);

        return main;
    }

    // ================= LOAD =================
    private void loadData() {
        loadStats();
        loadTable();
        loadChart();
        loadExtraStats();
    }

    // ================= STATS =================
    private void loadStats() {
        try (Connection con = DbConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery("""
                SELECT 
                    COALESCE(SUM(hdct.tong_gia),0),
                    COUNT(DISTINCT hd.id),
                    COALESCE(SUM(hdct.so_luong),0)
                FROM hoa_don hd
                LEFT JOIN hoa_don_chi_tiet hdct 
                ON hd.id = hdct.hoa_don_id
             """)) {

            if (rs.next()) {
                long revenue = rs.getLong(1);
                long orders = rs.getLong(2);
                long products = rs.getLong(3);

                long avg = orders == 0 ? 0 : revenue / orders;

                lblRevenue.setText(df.format(revenue) + " đ");
                lblOrders.setText(String.valueOf(orders));
                lblAvg.setText(df.format(avg) + " đ");
                lblProducts.setText(String.valueOf(products));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= TABLE =================
    private void loadTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
            "Ngày", "Mã HĐ", "Khách", "Số lượng", "Tổng tiền"
        });

        try (Connection con = DbConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery("""
                SELECT 
                    CAST(hd.ngay_tao AS DATE),
                    hd.id,
                    kh.ten_khach_hang,
                    COALESCE(SUM(hdct.so_luong),0),
                    COALESCE(SUM(hdct.tong_gia),0)
                FROM hoa_don hd
                LEFT JOIN khach_hang kh ON hd.khach_hang_id = kh.id
                LEFT JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.hoa_don_id
                GROUP BY hd.id, hd.ngay_tao, kh.ten_khach_hang
                ORDER BY hd.ngay_tao DESC
             """)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString(1),
                    rs.getInt(2),
                    rs.getString(3),
                    rs.getInt(4),
                    df.format(rs.getLong(5)) + " đ"
                });
            }

            table.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= TABLE BY DATE =================
    private void loadTableByDate(java.util.Date date) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Ngày", "Mã HĐ", "Khách", "Số lượng", "Tổng tiền"});

        try (Connection con = DbConnection.getConnection(); PreparedStatement ps = con.prepareStatement("""
                SELECT CAST(hd.ngay_tao AS DATE), hd.id, kh.ten_khach_hang,
                       COALESCE(SUM(hdct.so_luong),0), COALESCE(SUM(hdct.tong_gia),0)
                FROM hoa_don hd
                LEFT JOIN khach_hang kh ON hd.khach_hang_id = kh.id
                LEFT JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.hoa_don_id
                WHERE CAST(hd.ngay_tao AS DATE) = ?
                GROUP BY hd.id, hd.ngay_tao, kh.ten_khach_hang
                ORDER BY hd.ngay_tao DESC
             """)) {
            ps.setDate(1, new java.sql.Date(date.getTime()));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString(1),
                    rs.getInt(2),
                    rs.getString(3),
                    rs.getInt(4),
                    df.format(rs.getLong(5)) + " đ"
                });
            }
            table.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= CHART BY DATE =================
    private void loadChartByDate(java.util.Date date) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (Connection con = DbConnection.getConnection(); PreparedStatement ps = con.prepareStatement("""
                SELECT CAST(hd.ngay_tao AS DATE) as ngay,
                       COALESCE(SUM(hdct.tong_gia),0) as doanh_thu
                FROM hoa_don hd
                LEFT JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.hoa_don_id
                WHERE CAST(hd.ngay_tao AS DATE) = ?
                GROUP BY CAST(hd.ngay_tao AS DATE)
                ORDER BY ngay
             """)) {
            ps.setDate(1, new java.sql.Date(date.getTime()));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                dataset.addValue(
                        rs.getLong("doanh_thu"),
                        "Doanh thu",
                        rs.getString("ngay")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "",
                "Ngày",
                "VNĐ",
                dataset
        );

        chart.setBackgroundPaint(Color.WHITE);
        chart.setTitle(new TextTitle("Doanh thu theo ngày",
                new Font("Segoe UI", Font.BOLD, 18)));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));

        NumberAxis axis = (NumberAxis) plot.getRangeAxis();
        axis.setNumberFormatOverride(new DecimalFormat("#,###"));

        BarRenderer renderer = new BarRenderer();
        renderer.setSeriesPaint(0, new GradientPaint(
                0, 0, new Color(52, 152, 219),
                0, 300, new Color(41, 128, 185)
        ));
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.08);
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator());

        plot.setRenderer(renderer);

        ChartPanel cp = new ChartPanel(chart);
        cp.setMouseWheelEnabled(true);

        chartPanel.removeAll();
        chartPanel.add(cp, BorderLayout.CENTER);
        chartPanel.revalidate();
    }

    // ================= EXTRA STATS =================
    private void loadExtraStats() {
        try (Connection con = DbConnection.getConnection()) {

            // Ngày có doanh thu cao nhất trong tháng hiện tại
            try (PreparedStatement ps = con.prepareStatement("""
                SELECT TOP 1 CAST(hd.ngay_tao AS DATE) AS Ngay, SUM(hdct.tong_gia) AS DoanhThu
                FROM hoa_don hd
                JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.hoa_don_id
                WHERE MONTH(hd.ngay_tao) = MONTH(GETDATE()) AND YEAR(hd.ngay_tao) = YEAR(GETDATE())
                GROUP BY CAST(hd.ngay_tao AS DATE)
                ORDER BY DoanhThu DESC
            """)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    lblMaxDay.setText("Ngày có doanh thu cao nhất: " + rs.getString("Ngay")
                            + " (" + df.format(rs.getLong("DoanhThu")) + " đ)");
                }
            }

            // Tháng có doanh thu cao nhất trong năm hiện tại
            try (PreparedStatement ps = con.prepareStatement("""
                SELECT TOP 1 MONTH(hd.ngay_tao) AS Thang, SUM(hdct.tong_gia) AS DoanhThu
                FROM hoa_don hd
                JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.hoa_don_id
                WHERE YEAR(hd.ngay_tao) = YEAR(GETDATE())
                GROUP BY MONTH(hd.ngay_tao)
                ORDER BY DoanhThu DESC
            """)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    lblMaxMonth.setText("Tháng có doanh thu cao nhất: " + rs.getInt("Thang")
                            + " (" + df.format(rs.getLong("DoanhThu")) + " đ)");
                }
            }

            // Hóa đơn có giá trị cao nhất
            try (PreparedStatement ps = con.prepareStatement("""
                SELECT TOP 1 hd.id, hd.tong_tien
                FROM hoa_don hd
                ORDER BY hd.tong_tien DESC
            """)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    lblMaxInvoice.setText("Hóa đơn giá trị cao nhất: Mã " + rs.getInt("id")
                            + " (" + df.format(rs.getLong("tong_tien")) + " đ)");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ================= CHART =================

    private void loadChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (Connection con = DbConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery("""
            SELECT 
                CAST(hd.ngay_tao AS DATE) as ngay,
                COALESCE(SUM(hdct.tong_gia),0) as doanh_thu
            FROM hoa_don hd
            LEFT JOIN hoa_don_chi_tiet hdct 
            ON hd.id = hdct.hoa_don_id
            GROUP BY CAST(hd.ngay_tao AS DATE)
            ORDER BY ngay
         """)) {

            while (rs.next()) {
                dataset.addValue(
                        rs.getLong("doanh_thu"),
                        "Doanh thu",
                        rs.getString("ngay")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "",
                "Ngày",
                "VNĐ",
                dataset
        );

        chart.setBackgroundPaint(Color.WHITE);
        chart.setTitle(new TextTitle("Doanh thu theo ngày",
                new Font("Segoe UI", Font.BOLD, 18)));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));

        // Trục Y
        NumberAxis axis = (NumberAxis) plot.getRangeAxis();
        axis.setNumberFormatOverride(new DecimalFormat("#,###"));

        // Renderer
        BarRenderer renderer = new BarRenderer();
        renderer.setSeriesPaint(0, new GradientPaint(
                0, 0, new Color(52, 152, 219),
                0, 300, new Color(41, 128, 185)
        ));
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.08);

        // Tooltip
        renderer.setDefaultToolTipGenerator(
                new StandardCategoryToolTipGenerator()
        );

        plot.setRenderer(renderer);

        ChartPanel cp = new ChartPanel(chart);
        cp.setMouseWheelEnabled(true);

        chartPanel.removeAll();
        chartPanel.add(cp, BorderLayout.CENTER);
        chartPanel.revalidate();
    }
// ================= UI STYLE =================

    private void UI() {
        // Style cho button Filter
        styleButton(btnFilter, new Color(52, 152, 219), Color.WHITE);

        // Style cho button Refresh
        styleButton(btnRefresh, new Color(230, 126, 34), Color.WHITE);

        // Style cho panel lọc
        filterPanel.setBackground(new Color(245, 247, 250));
        filterPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Lọc dữ liệu",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(52, 73, 94)
        ));

        // Style cho bảng
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(52, 152, 219));
        table.getTableHeader().setForeground(Color.WHITE);
    }

// Hàm phụ để style button
    private void styleButton(JButton btn, Color bgColor, Color fgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bgColor.darker(), 1, true),
                new EmptyBorder(5, 15, 5, 15)
        ));

        // Hiệu ứng hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });
    }

}
