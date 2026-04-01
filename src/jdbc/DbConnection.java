/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author ngocp
 */
public class DbConnection {
    public static Connection getConnection() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=shop_quan_ao;encrypt=false";
            String user = "sa"; // user SQL Server
            String pass = "123456"; // mật khẩu
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("✅ Kết nối thành công!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
