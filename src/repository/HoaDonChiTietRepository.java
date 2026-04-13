/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdbc.DbConnection;
import model.HoaDonChiTiet;

/**
 *
 * @author nhocx
 */
public class HoaDonChiTietRepository {

    public List<Map<String, Object>> getAllByCart(int hoaDonId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = " SELECT hdct.id, hdct.quan_ao_chi_tiet_id, qa.ten_ao, kt.ten_kich_thuoc, ms.ten_mau, hdct.so_luong, qa.gia_ban, hdct.tong_gia "
                + " FROM hoa_don_chi_tiet hdct "
                + " JOIN quan_ao_chi_tiet qact ON hdct.quan_ao_chi_tiet_id = qact.id "
                + " JOIN quan_ao qa ON qact.quan_ao_id = qa.id "
                + " JOIN kich_thuoc kt ON qact.kich_thuoc_id = kt.id "
                + " JOIN mau_sac ms ON qact.mau_sac_id = ms.id "
                + " WHERE hdct.hoa_don_id = ? ";

        try (Connection con = DbConnection.getConnection(); 
                PreparedStatement ps = con.prepareStatement(sql)
                ){
            ps.setInt(1, hoaDonId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("sanPhamChiTietId", rs.getInt("quan_ao_chi_tiet_id"));
                    row.put("tenAo", rs.getString("ten_ao"));
                    row.put("tenKichThuoc", rs.getString("ten_kich_thuoc"));
                    row.put("tenMau", rs.getString("ten_mau"));
                    row.put("soLuong", rs.getInt("so_luong"));
                    row.put("donGia", rs.getBigDecimal("gia_ban"));
                    row.put("tongGia", rs.getBigDecimal("tong_gia"));
                    list.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean existsInCart(int hoaDonId, int sanPhamChiTietId) {
        String sql = "SELECT 1 FROM hoa_don_chi_tiet WHERE hoa_don_id = ? AND quan_ao_chi_tiet_id = ?";
        try (Connection con = DbConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hoaDonId);
            ps.setInt(2, sanPhamChiTietId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertToCart(int hoaDonId, int sanPhamChiTietId, int soLuong) {
        String sql = "INSERT INTO hoa_don_chi_tiet (hoa_don_id, quan_ao_chi_tiet_id, so_luong) VALUES (?, ?, ?)";
        try (Connection con = DbConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hoaDonId);
            ps.setInt(2, sanPhamChiTietId);
            ps.setInt(3, soLuong);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCart(int hoaDonId, int sanPhamChiTietId, int soLuongThem) {
        String sql = "UPDATE hoa_don_chi_tiet SET so_luong = so_luong + ? WHERE hoa_don_id = ? AND quan_ao_chi_tiet_id = ?";
        try (Connection con = DbConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, soLuongThem);
            ps.setInt(2, hoaDonId);
            ps.setInt(3, sanPhamChiTietId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCartDeleteByHdctId(int hdctId, int soLuongXoa) {
        String sql = "UPDATE hoa_don_chi_tiet SET so_luong = so_luong - ? WHERE id = ?";
        try (Connection con = DbConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, soLuongXoa);
            ps.setInt(2, hdctId);
            int affected = ps.executeUpdate();

            // Sau khi giảm, kiểm tra nếu số lượng <= 0 thì xoá hẳn
            if (affected > 0) {
                String checkSql = "SELECT so_luong FROM hoa_don_chi_tiet WHERE id = ?";
                try (PreparedStatement ps2 = con.prepareStatement(checkSql)) {
                    ps2.setInt(1, hdctId);
                    ResultSet rs = ps2.executeQuery();
                    if (rs.next()) {
                        int soLuong = rs.getInt("so_luong");
                        if (soLuong <= 0) {
                            deleteCartItemByHdctId(hdctId);
                        }
                    }
                }
            }
            return affected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteCartItemByHdctId(int hdctId) {
        String sql = "DELETE FROM hoa_don_chi_tiet WHERE id = ?";
        try (Connection con = DbConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hdctId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteAllCartItems(int hoaDonId) {
        String sql = "DELETE FROM hoa_don_chi_tiet WHERE hoa_don_id = ?";
        try (Connection con = DbConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hoaDonId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
