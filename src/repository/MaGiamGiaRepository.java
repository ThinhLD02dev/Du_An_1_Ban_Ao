package repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdbc.DbConnection;

public class MaGiamGiaRepository {

    public List<Map<String, Object>> getAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT * FROM ma_giam_gia";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("ma", rs.getString("ma"));
                map.put("giaTri", rs.getBigDecimal("gia_tri"));
                map.put("loaiGiam", rs.getInt("dang_giam")); // 1: %, 0: VNĐ
                map.put("loaiApDung", rs.getInt("loai_ap_dung")); // 1: SP, 0: HĐ
                map.put("ngayBatDau", rs.getDate("ngay_bat_dau"));
                map.put("ngayKetThuc", rs.getDate("ngay_ket_thuc"));
                map.put("soLanDung", rs.getInt("gioi_han_su_dung"));
                map.put("trangThai", rs.getInt("trang_thai")); // 1: Active, 0: Disabled
                map.put("giaTriToiDa", rs.getBigDecimal("gia_tri_toi_da"));
                map.put("donToiThieu", rs.getBigDecimal("gia_tri_don_toi_thieu"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int insert(String ma, double giaTri, int loaiGiam, int loaiAp, 
                        Date start, Date end, int soLuong, int status, double giaToiDa, double donToiThieu) {
        String sql = "INSERT INTO ma_giam_gia (ma, gia_tri, dang_giam, loai_ap_dung, " +
                     "ngay_bat_dau, ngay_ket_thuc, gioi_han_su_dung, trang_thai, gia_tri_toi_da, gia_tri_don_toi_thieu) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ma);
            ps.setDouble(2, giaTri);
            ps.setInt(3, loaiGiam);
            ps.setInt(4, loaiAp);
            ps.setDate(5, start);
            ps.setDate(6, end);
            ps.setInt(7, soLuong);
            ps.setInt(8, status);
            ps.setDouble(9, giaToiDa);
            ps.setDouble(10, donToiThieu);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean attachProductToVoucher(int voucherId, int productId) {
        String sql = "INSERT INTO ma_giam_gia_san_pham (ma_id, quan_ao_id) VALUES (?, ?)";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, voucherId);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, String>> getVoucherProductMappings() {
        List<Map<String, String>> list = new ArrayList<>();
        String sql = "SELECT mgg.ma, qa.ten_ao FROM ma_giam_gia_san_pham mggsp " +
                     "JOIN ma_giam_gia mgg ON mggsp.ma_id = mgg.id " +
                     "JOIN quan_ao qa ON mggsp.quan_ao_id = qa.id";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, String> map = new HashMap<>();
                map.put("ma", rs.getString("ma"));
                map.put("tenAo", rs.getString("ten_ao"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean delete(int id) {
        Connection con = null;
        try {
            con = DbConnection.getConnection();
            con.setAutoCommit(false); // Bắt đầu transaction

            // 1. Xoá liên kết ở bảng trung gian trước để tránh lỗi khoá ngoại
            String sqlMapping = "DELETE FROM ma_giam_gia_san_pham WHERE ma_id = ?";
            try (PreparedStatement psMapping = con.prepareStatement(sqlMapping)) {
                psMapping.setInt(1, id);
                psMapping.executeUpdate();
            }

            // 2. Xoá mã giảm giá ở bảng chính
            String sqlVoucher = "DELETE FROM ma_giam_gia WHERE id = ?";
            try (PreparedStatement psVoucher = con.prepareStatement(sqlVoucher)) {
                psVoucher.setInt(1, id);
                int result = psVoucher.executeUpdate();
                con.commit(); // Hoàn tất
                return result > 0;
            }
        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (con != null) {
                try { con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    public boolean isUsedInAnyInvoice(int voucherId) {
        String sql = "SELECT COUNT(*) FROM hoa_don WHERE khuyen_mai_id = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, voucherId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isUsedInUnpaidInvoice(int voucherId) {
        // Trạng thái 0 là UNPAID theo HoaDonRepository
        String sql = "SELECT COUNT(*) FROM hoa_don WHERE khuyen_mai_id = ? AND trang_thai = 0";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, voucherId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isValidVoucher(String ma, Date currentDate) {
        String sql = "SELECT COUNT(*) FROM ma_giam_gia WHERE ma = ? AND trang_thai = 1 AND " +
                     "ngay_bat_dau <= ? AND ngay_ket_thuc >= ? AND gioi_han_su_dung > 0";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ma);
            ps.setDate(2, new java.sql.Date(currentDate.getTime()));
            ps.setDate(3, new java.sql.Date(currentDate.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean decreaseUsageLimit(String ma) {
        String sql = "UPDATE ma_giam_gia SET gioi_han_su_dung = gioi_han_su_dung - 1 WHERE ma = ? AND gioi_han_su_dung > 0";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ma);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, Object> getVoucherDetails(String ma) {
        String sql = "SELECT * FROM ma_giam_gia WHERE ma = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", rs.getInt("id"));
                    map.put("ma", rs.getString("ma"));
                    map.put("giaTri", rs.getBigDecimal("gia_tri"));
                    map.put("loaiGiam", rs.getInt("dang_giam"));
                    map.put("loaiApDung", rs.getInt("loai_ap_dung"));
                    map.put("ngayBatDau", rs.getDate("ngay_bat_dau"));
                    map.put("ngayKetThuc", rs.getDate("ngay_ket_thuc"));
                    map.put("soLanDung", rs.getInt("gioi_han_su_dung"));
                    map.put("trangThai", rs.getInt("trang_thai"));
                    map.put("giaTriToiDa", rs.getBigDecimal("gia_tri_toi_da"));
                    map.put("donToiThieu", rs.getBigDecimal("gia_tri_don_toi_thieu"));
                    return map;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isCodeExists(String ma) {
        String sql = "SELECT COUNT(*) FROM ma_giam_gia WHERE ma = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}