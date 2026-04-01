/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.math.BigDecimal;
import java.sql.Date;
import jdbc.DbConnection;
import model.GiamGia;

/**
 *
 * @author nhocx
 */
public class GiamGiaRepository {
    public GiamGia findByCode(String maGiamGia) {
        String sql = "SELECT gg.id, gg.ma_giam_gia, gg.ngay_hieu_luc, gg.ngay_ket_thuc, gg.trang_thai, gg.so_lan_ap_dung, " +
                     "kg.kieu_giam_gia " +
                     "FROM giam_gia gg " +
                     "JOIN kieu_giam kg ON gg.kieu_giam_id = kg.id " +
                     "WHERE gg.ma_giam_gia = ?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maGiamGia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    GiamGia gg = new GiamGia();
                    gg.setId(rs.getInt("id"));
                    gg.setMaGiamGia(rs.getString("ma_giam_gia"));
                    gg.setNgayHieuLuc(rs.getDate("ngay_hieu_luc").toLocalDate());
                    gg.setNgayKetThuc(rs.getDate("ngay_ket_thuc").toLocalDate());
                    gg.setTrangThai(rs.getBoolean("trang_thai"));
                    gg.setSoLanApDung(rs.getInt("so_lan_ap_dung"));
                    gg.setKieuGiamId(rs.getInt("kieu_giam_id"));
                    return gg;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(GiamGia gg) {
        String sql = "UPDATE giam_gia SET trang_thai = ?, so_lan_ap_dung = ? WHERE id = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, gg.isTrangThai());
            ps.setInt(2, gg.getSoLanApDung());
            ps.setInt(3, gg.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insert(GiamGia gg) {
        String sql = "INSERT INTO giam_gia (ma_giam_gia, ngay_hieu_luc, ngay_ket_thuc, trang_thai, so_lan_ap_dung, kieu_giam_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, gg.getMaGiamGia());
            ps.setDate(2, Date.valueOf(gg.getNgayHieuLuc()));
            ps.setDate(3, Date.valueOf(gg.getNgayKetThuc()));
            ps.setBoolean(4, gg.isTrangThai());
            ps.setInt(5, gg.getSoLanApDung());
            ps.setInt(6, gg.getKieuGiamId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
