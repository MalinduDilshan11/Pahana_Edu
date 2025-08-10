package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDB {

    public Admin authenticateAdmin(String username, String password) {
        String query = "SELECT * FROM admins WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setAdminId(rs.getInt("admin_id"));
                    admin.setUsername(rs.getString("username"));
                    admin.setPassword(rs.getString("password"));
                    System.out.println("Admin authenticated: " + admin.getUsername());
                    return admin;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Authentication failed for username: " + username);
        return null;
    }
}