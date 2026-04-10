/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdbc.DbConnection;
import java.math.BigDecimal;
import model.HoaDon;

/**
 *
 * @author nhocx
 */
public class HoaDonRepository {
    private static final Logger logger = Logger.getLogger(HoaDonRepository.class.getName());
    private static final int UNPAID = 0;
    private static final int PAID = 1;

    public List<Map<String, Object>> getAllToUnpaid() {
        return getHoaDonList(UNPAID, "ngay_tao", "ngayTao");
    }
    
    public List<Map<String, Object>> getAllToPaid() {
        return getHoaDonList(PAID, "ngay_thanh_toan", "ngayThanhToan");
    }

    private List<Map<String, Object>> getHoaDonList(int status, String dateColumn, String dateKey) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT hd.id, hd." + dateColumn + ", kh.ten_khach_hang, nv.ten_nhan_vien "
                + " FROM hoa_don hd "
                + " JOIN khach_hang kh ON hd.khach_hang_id = kh.id "
                + " JOIN nhan_vien nv ON hd.nhan_vien_id = nv.id "
                + " WHERE hd.trang_thai = ? "
                + " AND CAST(hd."+ dateColumn +" AS DATE) = CAST(GETDATE() AS DATE)";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp(dateColumn);
                    LocalDateTime dateTime = ts.toLocalDateTime();
                    
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));                        
                    row.put(dateKey, dateTime);               
                    row.put("tenKhachHang", rs.getString("ten_khach_hang")); 
                    row.put("tenNhanVien", rs.getString("ten_nhan_vien"));       
                    list.add(row);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in getHoaDonList", e);
        }

        return list;
    }
    
    public Map<String, Object> findById(int hoaDonId) {
        String sql = "SELECT hd.id, hd.ngay_tao, kh.ten_khach_hang, nv.ten_nhan_vien, hd.tong_tien, gg.ma_giam_gia, hd.tien_thanh_toan, hd.tien_nhan, hd.tien_thua "
                + " FROM hoa_don hd "
                + " JOIN khach_hang kh ON hd.khach_hang_id = kh.id "
                + " JOIN nhan_vien nv ON hd.nhan_vien_id = nv.id "
                + " LEFT JOIN giam_gia gg ON hd.khuyen_mai_id = gg.id "
                + " WHERE hd.id = ? ";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             ps.setInt(1, hoaDonId);
             try(ResultSet rs = ps.executeQuery()){
                 if (rs.next()) {
                     Map<String, Object> row = new HashMap<>();
                     row.put("id", rs.getInt("id"));
                     row.put("tenKhachHang", rs.getString("ten_khach_hang"));
                     row.put("ngayTao", rs.getTimestamp("ngay_tao"));
                     row.put("tenNhanVien", rs.getString("ten_nhan_vien"));
                     row.put("tongTien", rs.getBigDecimal("tong_tien"));
                     row.put("maGiamGia", rs.getString("ma_giam_gia"));
                     row.put("tienThanhToan", rs.getBigDecimal("tien_thanh_toan"));
                     row.put("tienNhan", rs.getBigDecimal("tien_nhan"));
                     row.put("tienThua", rs.getBigDecimal("tien_thua"));
                     return row;
                 }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in findById", e);
        }

        return null;
    }
    
    public int createHoaDon(int khacHangId, int nguoiTaoId){
        String sql = "INSERT INTO hoa_don(khach_hang_id, ngay_tao, nguoi_tao, trang_thai)"+
                     "VALUES (?, GETDATE(), ?, 0); SELECT SCOPE_IDENTITY();";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)         
            ){
            ps.setInt(1, khacHangId);
            ps.setInt(2, nguoiTaoId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in createHoaDon", e);
        }
        return -1;
    }  
    
    public int createInvoice(HoaDon hd) {
    String sql = """
            INSERT INTO hoa_don
            (khach_hang_id, nhan_vien_id, ngay_tao, trang_thai)
            VALUES (?, ?, ?, ?); SELECT SCOPE_IDENTITY();
            """;

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, hd.getKhachHangId());
        ps.setInt(2, hd.getNhanVienId());
        ps.setTimestamp(3, java.sql.Timestamp.valueOf(LocalDateTime.now()));
        ps.setInt(4, UNPAID); // mặc định "Chưa thanh toán"

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }

    } catch (Exception e) {
        logger.log(Level.SEVERE, "Error in createInvoice", e);
    }

    return -1;
}

    public boolean delete(int hoaDonId) {
        String sql = "DELETE FROM hoa_don WHERE id = ?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hoaDonId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in delete", e);
        }

        return false;
    }
    
    public boolean updateInvoice(int hoaDonId, String maGiamGia, java.math.BigDecimal tienThanhToan,
                                  java.math.BigDecimal tienNhan, java.math.BigDecimal tienThua) {
        String sql = "UPDATE hoa_don SET khuyen_mai_id = CASE WHEN ? IS NOT NULL THEN (SELECT id FROM giam_gia WHERE ma_giam_gia = ?) ELSE NULL END, "
                   + "tien_thanh_toan = ?, tien_nhan = ?, tien_thua = ? WHERE id = ?";
        
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maGiamGia);
            ps.setString(2, maGiamGia);
            ps.setBigDecimal(3, tienThanhToan);
            ps.setBigDecimal(4, tienNhan);
            ps.setBigDecimal(5, tienThua);
            ps.setInt(6, hoaDonId);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in updateInvoice", e);
        }

        return false;
    }

    /**
     * Thanh toán hoá đơn - cập nhật tất cả thông tin và đổi trạng thái sang đã thanh toán
     */
    public boolean payInvoice(int hoaDonId, String maGiamGia, BigDecimal tongTien,
                              BigDecimal tienThanhToan, BigDecimal tienNhan,
                              BigDecimal tienThua) {
        String sql = "UPDATE hoa_don SET "
                   + "khuyen_mai_id = CASE WHEN ? IS NOT NULL AND ? != '' THEN (SELECT id FROM giam_gia WHERE ma_giam_gia = ?) ELSE NULL END, "
                   + "tong_tien = ?, "
                   + "tien_thanh_toan = ?, "
                   + "tien_nhan = ?, "
                   + "tien_thua = ?, "
                   + "ngay_thanh_toan = GETDATE(), "
                   + "trang_thai = ? "
                   + "WHERE id = ?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maGiamGia);
            ps.setString(2, maGiamGia);
            ps.setString(3, maGiamGia);
            ps.setBigDecimal(4, tongTien);
            ps.setBigDecimal(5, tienThanhToan);
            ps.setBigDecimal(6, tienNhan);
            ps.setBigDecimal(7, tienThua);
            ps.setInt(8, PAID); // trạng thái = 1 (đã thanh toán)
            ps.setInt(9, hoaDonId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in payInvoice", e);
        }

        return false;
    }
}
