/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/System/Templates/Classes/Class.java to edit this template
 */
package repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdbc.DbConnection;

/**
 *
 * @author nhocx
 */
public class DotGiamGiaRepository {
    private static final Logger logger = Logger.getLogger(DotGiamGiaRepository.class.getName());

    public List<Map<String, Object>> getAllWithProductName() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT qa.id AS san_pham_id, qa.ten_ao, dg.id AS dot_giam_id, dg.ten_dot, dg.gia_tri, dg.ngay_bat_dau, dg.ngay_ket_thuc, dg.trang_thai "
                + "FROM quan_ao qa "
                + "LEFT JOIN dot_giam_gia dg ON qa.dot_giam_id = dg.id";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("sanPhamId", rs.getInt("san_pham_id"));
                row.put("tenSanPham", rs.getString("ten_ao"));
                Integer dotGiamId = rs.getObject("dot_giam_id") != null ? rs.getInt("dot_giam_id") : null;
                row.put("dotGiamId", dotGiamId);
                row.put("tenDot", rs.getString("ten_dot"));
                row.put("giaTri", rs.getObject("gia_tri") != null ? rs.getInt("gia_tri") : null);
                row.put("ngayBatDau", rs.getDate("ngay_bat_dau"));
                row.put("ngayKetThuc", rs.getDate("ngay_ket_thuc"));
                row.put("trangThai", rs.getObject("trang_thai") != null ? rs.getBoolean("trang_thai") : null);
                list.add(row);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading product discount data", e);
        }

        return list;
    }

    public int insert(int sanPhamId, String tenDot, int giaTri, Date ngayBatDau, Date ngayKetThuc, boolean trangThoai) {
        String sql = "INSERT INTO dot_giam_gia (ten_dot, gia_tri, ngay_bat_dau, ngay_ket_thuc, trang_thai) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tenDot);
            ps.setInt(2, giaTri);
            ps.setDate(3, ngayBatDau);
            ps.setDate(4, ngayKetThuc);
            ps.setBoolean(5, trangThoai);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                return -1;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    return generatedId;
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inserting discount period", e);
        }
        return -1;
    }

    public boolean attachDiscountToProduct(int sanPhamId, int dotGiamId) {
        String sql = "UPDATE quan_ao SET dot_giam_id = ? WHERE id = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dotGiamId);
            ps.setInt(2, sanPhamId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error attaching discount to product", e);
        }
        return false;
    }
}
