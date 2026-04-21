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

public class ThongKeView extends JPanel {

    private JLabel lblRevenue, lblOrders, lblAvg, lblProducts;
    private JTable table;
    private JPanel chartPanel;

    private DecimalFormat df = new DecimalFormat("#,###");

    public ThongKeView() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        add(createTop(), BorderLayout.NORTH);
        add(createCenter(), BorderLayout.CENTER);

        loadData();
    }

    // ================= TOP =================
    private JPanel createTop() {
        JPanel pn = new JPanel(new BorderLayout());
        pn.setBorder(new EmptyBorder(10,10,10,10));
        pn.setBackground(getBackground());

        JPanel cards = new JPanel(new GridLayout(1,4,10,0));
        cards.setBackground(getBackground());

        cards.add(createCard("💰 Doanh thu", c -> lblRevenue = c));
        cards.add(createCard("🧾 Đơn hàng", c -> lblOrders = c));
        cards.add(createCard("📊 Trung bình", c -> lblAvg = c));
        cards.add(createCard("📦 Sản phẩm", c -> lblProducts = c));

        pn.add(cards, BorderLayout.CENTER);
        return pn;
    }

    private JPanel createCard(String title, java.util.function.Consumer<JLabel> set) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220,220,220)),
                new EmptyBorder(10,15,10,15)
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
        JPanel main = new JPanel(new BorderLayout(10,10));
        main.setBorder(new EmptyBorder(0,10,10,10));
        main.setBackground(getBackground());

        // Chart
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("Biểu đồ doanh thu"));
        chartPanel.setBackground(Color.WHITE);

        // Table
        table = new JTable();
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Chi tiết"));

        main.add(chartPanel, BorderLayout.NORTH);
        main.add(sp, BorderLayout.CENTER);

        return main;
    }

    // ================= LOAD =================
    private void loadData() {
        loadStats();
        loadTable();
        loadChart();
    }

    // ================= STATS =================
    private void loadStats() {
        try (Connection con = DbConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("""
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

                long avg = orders == 0 ? 0 : revenue/orders;

                lblRevenue.setText(df.format(revenue) + " đ");
                lblOrders.setText(String.valueOf(orders));
                lblAvg.setText(df.format(avg) + " đ");
                lblProducts.setText(String.valueOf(products));
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // ================= TABLE =================
    private void loadTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
            "Ngày","Mã HĐ","Khách","Số lượng","Tổng tiền"
        });

        try (Connection con = DbConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("""
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

            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getString(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getInt(4),
                        df.format(rs.getLong(5)) + " đ"
                });
            }

            table.setModel(model);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // ================= CHART =================
    private void loadChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (Connection con = DbConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("""
                SELECT 
                    CAST(hd.ngay_tao AS DATE) as ngay,
                    COALESCE(SUM(hdct.tong_gia),0) as doanh_thu
                FROM hoa_don hd
                LEFT JOIN hoa_don_chi_tiet hdct 
                ON hd.id = hdct.hoa_don_id
                GROUP BY CAST(hd.ngay_tao AS DATE)
                ORDER BY ngay
             """)) {

            while(rs.next()) {
                dataset.addValue(
                        rs.getLong("doanh_thu"),
                        "Doanh thu",
                        rs.getString("ngay")
                );
            }

        } catch(Exception e) {
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
        plot.setRangeGridlinePaint(new Color(230,230,230));

        // Trục Y
        NumberAxis axis = (NumberAxis) plot.getRangeAxis();
        axis.setNumberFormatOverride(new DecimalFormat("#,###"));

        // Renderer KHÔNG dùng anonymous class
        BarRenderer renderer = new BarRenderer();
        renderer.setSeriesPaint(0, new GradientPaint(
                0, 0, new Color(52,152,219),
                0, 300, new Color(41,128,185)
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
}