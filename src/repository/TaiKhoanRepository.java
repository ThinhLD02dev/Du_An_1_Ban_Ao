/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import jdbc.DbConnection;
import model.TaiKhoan;

public class TaiKhoanRepository {

    public List<TaiKhoan> getAll() {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT id, ten_dang_nhap AS ten, mat_khau AS matKhau FROM tai_khoan";

        try (Connection con = DbConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TaiKhoan tk = new TaiKhoan(
                        rs.getInt("id"),
                        rs.getString("ten"),
                        rs.getString("matKhau"),
                        rs.getInt("nhanVienId")
                );
                list.add(tk);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean add(TaiKhoan tk) {
        String sql = "INSERT INTO tai_khoan(ten_dang_nhap, mat_khau, trang_thai, vai_tro_id, nhan_vien_id) VALUES (?, ?, 1, 2, ?)";
        try (Connection con = DbConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tk.getTen());
            ps.setString(2, tk.getMatKhau());
            ps.setInt(3, tk.getNhanVienId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM tai_khoan WHERE id=?";

        try (Connection con = DbConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Object[]> getAllWithNhanVien() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT tk.id, tk.ten_dang_nhap, tk.mat_khau, tk.trang_thai, nv.ten_nhan_vien "
                + "FROM tai_khoan tk "
                + "LEFT JOIN nhan_vien nv ON tk.nhan_vien_id = nv.id";

        try (Connection con = DbConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String trangThai = rs.getInt("trang_thai") == 1 ? "Đang làm" : "Đã nghỉ";
                list.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("ten_dang_nhap"),
                    rs.getString("mat_khau"),
                    trangThai,
                    rs.getString("ten_nhan_vien")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}
