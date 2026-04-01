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
import jdbc.DbConnection;
import model.HoaDon;

/**
 *
 * @author nhocx
 */
public class HoaDonRepository {
    public List<Map<String, Object>> getAllToUnpaid() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT hd.id, hd.ngay_tao, kh.ten_khach_hang, nv.ten_nhan_vien "
                + " FROM hoa_don hd "
                + " JOIN khach_hang kh ON hd.khach_hang_id = kh.id "
                + " JOIN nhan_vien nv ON hd.nhan_vien_id = nv.id "
                + " WHERE hd.trang_thai = 0 ";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("ngay_tao");
                LocalDateTime ngayTao = ts.toLocalDateTime();
                
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));                        
                row.put("ngayTao", ngayTao);               
                row.put("tenKhachHang",rs.getString("ten_khach_hang")); 
                row.put("tenNhanVien", rs.getString("ten_nhan_vien"));       
                list.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public List<Map<String, Object>> getAllToPaid() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT hd.id, hd.ngay_thanh_toan, kh.ten_khach_hang, nv.ten_nhan_vien "
                + " FROM hoa_don hd "
                + " JOIN khach_hang kh ON hd.khach_hang_id = kh.id "
                + " JOIN nhan_vien nv ON hd.nhan_vien_id = nv.id "
                + " WHERE hd.trang_thai = 1 ";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("ngay_thanh_toan");
                LocalDateTime ngayThanhToan = ts.toLocalDateTime();
                
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));                        
                row.put("ngayThanhToan", ngayThanhToan);               
                row.put("tenKhachHang",rs.getString("ten_khach_hang")); 
                row.put("tenNhanVien", rs.getString("ten_nhan_vien"));       
                list.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public Map<String, Object> findById(int hoaDonId) {
        String sql = "SELECT hd.id, hd.ngay_tao, kh.ten_khach_hang, nv.ten_nhan_vien, hd.tong_tien, gg.ma_giam_gia, hd.tien_thanh_toan, hd.tien_nhan, hd.tien_thua "
                + " FROM hoa_don hd "
                + " JOIN khach_hang kh ON hd.khach_hang_id = kh.id "
                + " JOIN nhan_vien nv ON hd.nhan_vien_id = nv.id "
                + " JOIN giam_gia gg ON hd.khuyen_mai_id = gg.id "
                + " WHERE hd.id = ? ";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ){
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
                 }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return -1;
    }  
    
    public boolean createInvoice(HoaDon hd) {
    String sql = """
            INSERT INTO hoa_don
            (khach_hang_id, nhan_vien_id, ngay_tao, trang_thai)
            VALUES (?, ?, ?, ?)
            """;

    try (Connection con = DbConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, hd.getKhachHangId());
        ps.setInt(2, hd.getNhanVienId());
        ps.setTimestamp(3, java.sql.Timestamp.valueOf(LocalDateTime.now()));
        ps.setInt(4, 1); // ví dụ mặc định "Chưa thanh toán"

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}
}
