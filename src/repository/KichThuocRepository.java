/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jdbc.DbConnection;
import model.KichThuoc;
/**
 *
 * @author ngocp
 */
public class KichThuocRepository {
    public List<KichThuoc> getAll() {

        List<KichThuoc> list = new ArrayList<>();
        String sql = "SELECT * FROM kich_thuoc";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                KichThuoc kt = new KichThuoc(
                        rs.getInt("id"),
                        rs.getString("ten_kich_thuoc")
                );

                list.add(kt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean add(String tenKichThuoc) {

        String sql = "INSERT INTO kich_thuoc(ten_kich_thuoc) VALUES (?)";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tenKichThuoc);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(KichThuoc kt) {

        String sql = "UPDATE kich_thuoc SET ten_kich_thuoc=? WHERE id=?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, kt.getTenKichThuoc());
            ps.setInt(2, kt.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(int id) {

        String sql = "DELETE FROM kich_thuoc WHERE id=?";

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
