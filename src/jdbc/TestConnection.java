/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author ngocp
 */
public class TestConnection {
    public static void main(String[] args) {
        try (Connection conn = DbConnection.getConnection()) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT TOP 5 * FROM chuc_vu");
                while (rs.next()) {
                    System.out.println(rs.getString("ma_cv") + " - " + rs.getString("ten_cv"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
