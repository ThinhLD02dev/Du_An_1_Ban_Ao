/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;


import java.util.ArrayList;
import model.VaiTro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jdbc.DbConnection;

/**
 *
 * @author ngocp
 */
public class VaiTroRepository {
    public ArrayList<VaiTro> getAll() {

        ArrayList<VaiTro> list = new ArrayList<>();

        String sql = "SELECT * FROM vai_tro";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                VaiTro vt = new VaiTro();

                vt.setId(rs.getInt("id"));
                vt.setTenVaiTro(rs.getString("ten_vai_tro"));

                list.add(vt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
