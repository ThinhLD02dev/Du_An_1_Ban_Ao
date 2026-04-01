/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import model.SanPhamChiTiet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import jdbc.DbConnection;


/**
 *
 * @author ngocp
 */
public class SanPhamChiTietRepository {
    public List<SanPhamChiTiet> getAll() {

        List<SanPhamChiTiet> list = new ArrayList<>();
        String sql = "SELECT * FROM quan_ao_chi_tiet";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                SanPhamChiTiet sp = new SanPhamChiTiet(
                        rs.getInt("id"),
                        rs.getInt("so_luong"),
                        rs.getInt("mau_sac_id"),
                        rs.getBigDecimal("gia_ban"),
                        rs.getInt("trang_thai"),
                        rs.getInt("kich_thuoc_id"),
                        rs.getInt("quan_ao_id")
                );

                list.add(sp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean add(SanPhamChiTiet sp) {

        String sql = """
                INSERT INTO quan_ao_chi_tiet
                (so_luong, mau_sac_id, gia_ban, trang_thai, kich_thuoc_id, quan_ao_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, sp.getSoLuong());
            ps.setInt(2, sp.getMauSacId());
            ps.setBigDecimal(3, sp.getGiaBan());
            ps.setInt(4, sp.getTrangThai());
            ps.setInt(5, sp.getKichThuocId());
            ps.setInt(6, sp.getQuanAoId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(SanPhamChiTiet sp) {

        String sql = """
                UPDATE quan_ao_chi_tiet
                SET so_luong=?, mau_sac_id=?, gia_ban=?, trang_thai=?,
                    kich_thuoc_id=?, quan_ao_id=?
                WHERE id=?
                """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, sp.getSoLuong());
            ps.setInt(2, sp.getMauSacId());
            ps.setBigDecimal(3, sp.getGiaBan());
            ps.setInt(4, sp.getTrangThai());
            ps.setInt(5, sp.getKichThuocId());
            ps.setInt(6, sp.getQuanAoId());
            ps.setInt(7, sp.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(int id) {

        String sql = "DELETE FROM quan_ao_chi_tiet WHERE id=?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public List<Map<String, Object>> getAllbySell() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = " SELECT qact.id, qa.ten_ao, kt.ten_kich_thuoc, ms.ten_mau, qa.mo_ta, qact.so_luong, qact.gia_ban "
                + " FROM quan_ao_chi_tiet qact "
                + " JOIN quan_ao qa ON qact.quan_ao_id = qa.id "
                + " JOIN kich_thuoc kt ON qact.kich_thuoc_id = kt.id "
                + " JOIN mau_sac ms ON qact.mau_sac_id = ms.id ";

        try (Connection con = DbConnection.getConnection(); 
                PreparedStatement ps = con.prepareStatement(sql); 
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));                        
                row.put("tenAo", rs.getString("ten_ao"));               
                row.put("tenKichThuoc",rs.getString("ten_kich_thuoc")); 
                row.put("tenMau", rs.getString("ten_mau"));             
                row.put("moTa", rs.getString("mo_ta"));
                row.put("soLuong", rs.getInt("so_luong"));              
                row.put("giaBan", rs.getBigDecimal("gia_ban"));         
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
