package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdbc.DbConnection;

public class DotGiamGiaRepository {

    public List<Map<String, Object>> getAllWithProductName() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT id, ten_dot, gia_tri, ngay_bat_dau, ngay_ket_thuc, trang_thai FROM dot_giam_gia";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("dotGiamId", rs.getInt("id"));
                map.put("tenDot", rs.getString("ten_dot"));
                map.put("giaTri", rs.getInt("gia_tri"));
                map.put("ngayBatDau", rs.getDate("ngay_bat_dau"));
                map.put("ngayKetThuc", rs.getDate("ngay_ket_thuc"));
                map.put("trangThai", rs.getInt("trang_thai"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, Object>> getProductsWithDiscounts() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT qa.ten_ao, dgg.ten_dot, qa.gia_ban, " +
                     "(qa.gia_ban * (1 - CAST(dgg.gia_tri AS DECIMAL) / 100)) AS gia_thuc_te " +
                     "FROM quan_ao qa " +
                     "JOIN dot_giam_gia dgg ON qa.dot_giam_id = dgg.id";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("tenSanPham", rs.getString("ten_ao"));
                map.put("tenDot", rs.getString("ten_dot"));
                map.put("giaBan", rs.getBigDecimal("gia_ban"));
                map.put("giaThucTe", rs.getBigDecimal("gia_thuc_te"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, Object>> searchSanPham(String keyword) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT id, ten_ao FROM quan_ao WHERE ten_ao LIKE ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", rs.getInt("id"));
                    map.put("ten_ao", rs.getString("ten_ao"));
                    list.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int insertCampaign(String ten, int giaTri, java.sql.Date start, java.sql.Date end, boolean status) {
        String sql = "INSERT INTO dot_giam_gia (ten_dot, gia_tri, ngay_bat_dau, ngay_ket_thuc, trang_thai) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ten);
            ps.setInt(2, giaTri);
            ps.setDate(3, start);
            ps.setDate(4, end);
            ps.setInt(5, status ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean attachDiscountToProduct(int spId, int dotId) {
        String sql = "UPDATE quan_ao SET dot_giam_id = ? WHERE id = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dotId);
            ps.setInt(2, spId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean detachDiscountFromProduct(int spId) {
        String sql = "UPDATE quan_ao SET dot_giam_id = NULL WHERE id = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, spId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isCampaignUsed(int campaignId) {
        String sql = "SELECT COUNT(*) FROM quan_ao WHERE dot_giam_id = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, campaignId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteCampaign(int campaignId) {
        String sql = "DELETE FROM dot_giam_gia WHERE id = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, campaignId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasOverlappingCampaign(Date start, Date end) {
        String sql = "SELECT COUNT(*) FROM dot_giam_gia WHERE trang_thai = 1 AND " +
                     "((ngay_bat_dau <= ? AND ngay_ket_thuc >= ?) OR " +
                     "(ngay_bat_dau <= ? AND ngay_ket_thuc >= ?) OR " +
                     "(ngay_bat_dau >= ? AND ngay_ket_thuc <= ?))";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(start.getTime()));
            ps.setDate(2, new java.sql.Date(start.getTime()));
            ps.setDate(3, new java.sql.Date(end.getTime()));
            ps.setDate(4, new java.sql.Date(end.getTime()));
            ps.setDate(5, new java.sql.Date(start.getTime()));
            ps.setDate(6, new java.sql.Date(end.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Map<String, Object>> getAllProducts() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT id, ten_ao, gia_ban, dot_giam_id FROM quan_ao ORDER BY ten_ao";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("ten_ao", rs.getString("ten_ao"));
                map.put("gia_ban", rs.getBigDecimal("gia_ban"));
                map.put("dot_giam_id", rs.getObject("dot_giam_id"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean attachDiscountToMultipleProducts(List<Integer> productIds, int campaignId) {
        if (productIds.isEmpty()) return true;

        String sql = "UPDATE quan_ao SET dot_giam_id = ? WHERE id IN (" +
                     String.join(",", productIds.stream().map(id -> "?").toArray(String[]::new)) + ")";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, campaignId);
            for (int i = 0; i < productIds.size(); i++) {
                ps.setInt(i + 2, productIds.get(i));
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}