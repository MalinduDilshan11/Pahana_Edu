package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BillDB {

    public boolean createBill(Bill bill) {
        String billQuery = "INSERT INTO bills (customer_id, bill_date, total_amount) VALUES (?, ?, ?)";
        String billItemQuery = "INSERT INTO bill_items (bill_id, item_id, quantity, price, subtotal) VALUES (?, ?, ?, ?, ?)";
        String updateStockQuery = "UPDATE items SET stock_quantity = stock_quantity - ? WHERE item_id = ? AND stock_quantity >= ?";
        
        Connection conn = null;
        PreparedStatement billStmt = null;
        PreparedStatement billItemStmt = null;
        PreparedStatement updateStockStmt = null;
        ResultSet generatedKeys = null;
        
        try {
            conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS);
            conn.setAutoCommit(false); // Start transaction

            // Insert bill
            billStmt = conn.prepareStatement(billQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            billStmt.setInt(1, bill.getCustomerId());
            billStmt.setTimestamp(2, Timestamp.valueOf(bill.getBillDate()));
            billStmt.setDouble(3, bill.getTotalAmount());
            int rowsAffected = billStmt.executeUpdate();

            if (rowsAffected == 0) {
                conn.rollback();
                return false;
            }

            // Get generated bill ID
            generatedKeys = billStmt.getGeneratedKeys();
            if (!generatedKeys.next()) {
                conn.rollback();
                return false;
            }
            int billId = generatedKeys.getInt(1);
            bill.setBillId(billId);

            // Insert bill items and update stock
            billItemStmt = conn.prepareStatement(billItemQuery);
            updateStockStmt = conn.prepareStatement(updateStockQuery);
            for (Bill.BillItem billItem : bill.getBillItems()) {
                // Insert bill item
                billItemStmt.setInt(1, billId);
                billItemStmt.setInt(2, billItem.getItemId());
                billItemStmt.setInt(3, billItem.getQuantity());
                billItemStmt.setDouble(4, billItem.getPrice());
                billItemStmt.setDouble(5, billItem.getSubtotal());
                billItemStmt.addBatch();

                // Update stock quantity
                updateStockStmt.setInt(1, billItem.getQuantity());
                updateStockStmt.setInt(2, billItem.getItemId());
                updateStockStmt.setInt(3, billItem.getQuantity());
                updateStockStmt.addBatch();
            }

            // Execute batch for bill items
            int[] batchResults = billItemStmt.executeBatch();
            for (int result : batchResults) {
                if (result <= 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Execute batch for stock updates
            int[] stockUpdateResults = updateStockStmt.executeBatch();
            for (int result : stockUpdateResults) {
                if (result <= 0) {
                    conn.rollback();
                    System.out.println("Failed to update stock for an item");
                    return false;
                }
            }

            conn.commit();
            System.out.println("Bill created for customer ID: " + bill.getCustomerId() + " with total: " + bill.getTotalAmount());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (billStmt != null) billStmt.close();
                if (billItemStmt != null) billItemStmt.close();
                if (updateStockStmt != null) updateStockStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Bill getBill(int billId) {
        String billQuery = "SELECT * FROM bills WHERE bill_id = ?";
        String billItemsQuery = "SELECT * FROM bill_items WHERE bill_id = ?";
        
        try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS);
             PreparedStatement billStmt = conn.prepareStatement(billQuery);
             PreparedStatement billItemsStmt = conn.prepareStatement(billItemsQuery)) {
            
            // Fetch bill details
            billStmt.setInt(1, billId);
            try (ResultSet billRs = billStmt.executeQuery()) {
                if (billRs.next()) {
                    Bill bill = new Bill();
                    bill.setBillId(billRs.getInt("bill_id"));
                    bill.setCustomerId(billRs.getInt("customer_id"));
                    bill.setBillDate(billRs.getTimestamp("bill_date").toLocalDateTime());
                    bill.setTotalAmount(billRs.getDouble("total_amount"));

                    // Fetch bill items
                    billItemsStmt.setInt(1, billId);
                    try (ResultSet itemsRs = billItemsStmt.executeQuery()) {
                        List<Bill.BillItem> billItems = new ArrayList<>();
                        while (itemsRs.next()) {
                            Bill.BillItem billItem = new Bill.BillItem();
                            billItem.setItemId(itemsRs.getInt("item_id"));
                            billItem.setQuantity(itemsRs.getInt("quantity"));
                            billItem.setPrice(itemsRs.getDouble("price"));
                            billItems.add(billItem);
                        }
                        bill.setBillItems(billItems);
                    }
                    System.out.println("Bill found: ID " + billId);
                    return bill;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("No bill found for ID: " + billId);
        return null;
    }

    public List<Bill> getBillsByCustomer(int customerId) {
        List<Bill> bills = new ArrayList<>();
        String billQuery = "SELECT * FROM bills WHERE customer_id = ?";
        String billItemsQuery = "SELECT * FROM bill_items WHERE bill_id = ?";
        
        try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS);
             PreparedStatement billStmt = conn.prepareStatement(billQuery);
             PreparedStatement billItemsStmt = conn.prepareStatement(billItemsQuery)) {
            
            billStmt.setInt(1, customerId);
            try (ResultSet billRs = billStmt.executeQuery()) {
                while (billRs.next()) {
                    Bill bill = new Bill();
                    bill.setBillId(billRs.getInt("bill_id"));
                    bill.setCustomerId(billRs.getInt("customer_id"));
                    bill.setBillDate(billRs.getTimestamp("bill_date").toLocalDateTime());
                    bill.setTotalAmount(billRs.getDouble("total_amount"));

                    // Fetch bill items
                    billItemsStmt.setInt(1, bill.getBillId());
                    try (ResultSet itemsRs = billItemsStmt.executeQuery()) {
                        List<Bill.BillItem> billItems = new ArrayList<>();
                        while (itemsRs.next()) {
                            Bill.BillItem billItem = new Bill.BillItem();
                            billItem.setItemId(itemsRs.getInt("item_id"));
                            billItem.setQuantity(itemsRs.getInt("quantity"));
                            billItem.setPrice(itemsRs.getDouble("price"));
                            billItems.add(billItem);
                        }
                        bill.setBillItems(billItems);
                    }
                    bills.add(bill);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }
}