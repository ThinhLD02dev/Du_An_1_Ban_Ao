/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import jdbc.DbConnection;
import model.SanPham;
import model.VaiTro;

/**
 *
 * @author ngocp
 */
public class SanPhamRepository {
//    public List<SanPham> getAll(){
//        List<SanPham> list = new ArrayList<>();
//        String sql = "SELECT * FROM quan_ao";
//
//        try (Connection con = DbConnection.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//
//            while (rs.next()) {
//
//                SanPham sp = new SanPham(
//                        rs.getInt("id"),
//                        rs.getString("ma_sp"),
//                        rs.getString("ten_ao"),
//                        rs.getString("mo_ta"),
//                        rs.getBigDecimal("gia_ban"),
//                        rs.getInt("trang_thai"),
//                        rs.getDate("ngay_tao"),
//                        rs.getInt("chat_lieu_id"),
//                        rs.getInt("thuong_hieu_id")
//                );
//
//                list.add(sp);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }

    public boolean add(SanPham sp) {

        String sql = """
                INSERT INTO quan_ao
                (ma_sp, ten_ao, mo_ta, trang_thai, ngay_tao,
                 loai_quan_ao_id, chat_lieu_id, thuong_hieu_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sp.getMaSP());
            ps.setString(2, sp.getTenAo());
            ps.setString(3, sp.getMoTa());
            ps.setInt(4, sp.getTrangThai());
            ps.setDate(5, (Date) sp.getNgayTao());
            ps.setInt(7, sp.getChatLieuId());
            ps.setInt(8, sp.getThuongHieuId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(SanPham sp) {

        String sql = """
                UPDATE quan_ao
                SET ma_sp=?, ten_ao=?, mo_ta=?, trang_thai=?, ngay_tao=?,
                    loai_quan_ao_id=?, chat_lieu_id=?, thuong_hieu_id=?
                WHERE id=?
                """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sp.getMaSP());
            ps.setString(2, sp.getTenAo());
            ps.setString(3, sp.getMoTa());
            ps.setInt(4, sp.getTrangThai());
            ps.setDate(5, (Date) sp.getNgayTao());
            ps.setInt(7, sp.getChatLieuId());
            ps.setInt(8, sp.getThuongHieuId());
            ps.setInt(9, sp.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(int id) {

        String sql = "DELETE FROM quan_ao WHERE id=?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
