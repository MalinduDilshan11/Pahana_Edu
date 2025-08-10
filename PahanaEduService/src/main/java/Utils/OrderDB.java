package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDB {

    public boolean createOrder(Order order, Customer customer, List<Item> items) {
        String insertOrder = "INSERT INTO orders (customer_id, order_date) VALUES (?, CURRENT_TIMESTAMP)";
        String insertOrderItem = "INSERT INTO order_items (order_id, item_id, units_consumed) VALUES (?, ?, ?)";
        String updateStock = "UPDATE items SET stock_quantity = stock_quantity - ? WHERE item_id = ? AND stock_quantity >= ?";
        
        try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS);
             PreparedStatement orderStmt = conn.prepareStatement(insertOrder, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement orderItemStmt = conn.prepareStatement(insertOrderItem);
             PreparedStatement updateStmt = conn.prepareStatement(updateStock)) {
            
            conn.setAutoCommit(false);

            // Validate customer exists
            if (customer == null) return false;

            // Validate items and stock
            for (int i = 0; i < order.getOrderItems().size(); i++) {
                OrderItem orderItem = order.getOrderItems().get(i);
                Item item = items.get(i);
                if (item == null || item.getStockQuantity() < orderItem.getUnitsConsumed()) {
                    conn.rollback();
                    return false;
                }
            }

            // Insert order
            orderStmt.setInt(1, order.getCustomerId());
            orderStmt.executeUpdate();
            
            // Get generated order ID
            ResultSet rs = orderStmt.getGeneratedKeys();
            if (!rs.next()) {
                conn.rollback();
                return false;
            }
            int orderId = rs.getInt(1);
            
            // Insert order items and update stock
            for (int i = 0; i < order.getOrderItems().size(); i++) {
                OrderItem orderItem = order.getOrderItems().get(i);
                Item item = items.get(i);
                
                // Insert order item
                orderItemStmt.setInt(1, orderId);
                orderItemStmt.setInt(2, orderItem.getItemId());
                orderItemStmt.setInt(3, orderItem.getUnitsConsumed());
                orderItemStmt.executeUpdate();
                
                // Update stock
                updateStmt.setInt(1, orderItem.getUnitsConsumed());
                updateStmt.setInt(2, orderItem.getItemId());
                updateStmt.setInt(3, orderItem.getUnitsConsumed());
                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Order> getOrdersByCustomerId(int customerId) {
        List<Order> orders = new ArrayList<>();
        String orderQuery = "SELECT * FROM orders WHERE customer_id = ?";
        String orderItemQuery = "SELECT * FROM order_items WHERE order_id = ?";
        
        try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS);
             PreparedStatement orderStmt = conn.prepareStatement(orderQuery);
             PreparedStatement orderItemStmt = conn.prepareStatement(orderItemQuery)) {
            
            orderStmt.setInt(1, customerId);
            try (ResultSet orderRs = orderStmt.executeQuery()) {
                while (orderRs.next()) {
                    Order order = new Order();
                    order.setOrderId(orderRs.getInt("order_id"));
                    order.setCustomerId(orderRs.getInt("customer_id"));
                    order.setOrderDate(orderRs.getTimestamp("order_date"));
                    
                    // Fetch order items
                    List<OrderItem> orderItems = new ArrayList<>();
                    orderItemStmt.setInt(1, order.getOrderId());
                    try (ResultSet itemRs = orderItemStmt.executeQuery()) {
                        while (itemRs.next()) {
                            OrderItem orderItem = new OrderItem();
                            orderItem.setItemId(itemRs.getInt("item_id"));
                            orderItem.setUnitsConsumed(itemRs.getInt("units_consumed"));
                            orderItems.add(orderItem);
                        }
                    }
                    order.setOrderItems(orderItems);
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}