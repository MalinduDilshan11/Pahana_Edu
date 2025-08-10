package Utils;

import Utils.Utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDB {

    public boolean addItem(Item item) {
        String query = "INSERT INTO items (name, category, price, stock_quantity) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, item.getName());
            stmt.setString(2, item.getCategory());
            stmt.setDouble(3, item.getPrice());
            stmt.setInt(4, item.getStockQuantity());
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Item added: " + item.getName() + " (Rows affected: " + rowsAffected + ")");
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateItem(Item item) {
        String query = "UPDATE items SET name = ?, category = ?, price = ?, stock_quantity = ? WHERE item_id = ?";
        try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, item.getName());
            stmt.setString(2, item.getCategory());
            stmt.setDouble(3, item.getPrice());
            stmt.setInt(4, item.getStockQuantity());
            stmt.setInt(5, item.getItemId());
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Item updated: " + item.getName() + " (Rows affected: " + rowsAffected + ")");
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteItem(int itemId) {
        String query = "DELETE FROM items WHERE item_id = ?";
        try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, itemId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Item deleted with ID: " + itemId + " (Rows affected: " + rowsAffected + ")");
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Item getItem(int itemId) {
        String query = "SELECT * FROM items WHERE item_id = ?";
        try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, itemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Item item = new Item();
                    item.setItemId(rs.getInt("item_id"));
                    item.setName(rs.getString("name"));
                    item.setCategory(rs.getString("category"));
                    item.setPrice(rs.getDouble("price"));
                    item.setStockQuantity(rs.getInt("stock_quantity"));
                    System.out.println("Item found: " + item.getName());
                    return item;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("No item found for ID: " + itemId);
        return null;
    }

    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM items";
        try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Item item = new Item();
                item.setItemId(rs.getInt("item_id"));
                item.setName(rs.getString("name"));
                item.setCategory(rs.getString("category"));
                item.setPrice(rs.getDouble("price"));
                item.setStockQuantity(rs.getInt("stock_quantity"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public Item getItemById(int itemId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}