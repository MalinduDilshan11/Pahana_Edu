/*
 * Click nbfs://nbSystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbSystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Malindu Dilshan
 */
public class BillDBTest {

    private BillDB billDB;

    public BillDBTest() {
    }

    @BeforeEach
    public void setUp() {
        billDB = new BillDB();
        // Clean up database before each test
        try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS)) {
            conn.createStatement().execute("DELETE FROM bill_items");
            conn.createStatement().execute("DELETE FROM bills");
            // Reset item stock quantities for testing
            conn.createStatement().execute("UPDATE items SET stock_quantity = 100 WHERE item_id IN (1, 2)");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to set up database: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        // Clean up database after each test
        try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS)) {
            conn.createStatement().execute("DELETE FROM bill_items");
            conn.createStatement().execute("DELETE FROM bills");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to tear down database: " + e.getMessage());
        }
    }

    @Test
    public void testCreateBill() {
        System.out.println("createBill");
        // Create a sample bill
        Bill bill = new Bill();
        bill.setCustomerId(1);
        bill.setBillDate(LocalDateTime.now());
        bill.setTotalAmount(150.0);

        List<Bill.BillItem> billItems = new ArrayList<>();
        Bill.BillItem item1 = new Bill.BillItem();
        item1.setItemId(1);
        item1.setQuantity(2);
        item1.setPrice(50.0);
        billItems.add(item1);

        Bill.BillItem item2 = new Bill.BillItem();
        item2.setItemId(2);
        item2.setQuantity(1);
        item2.setPrice(50.0);
        billItems.add(item2);

        bill.setBillItems(billItems);

        // Test bill creation
        boolean result = billDB.createBill(bill);
        assertTrue(result, "Failed to create bill");

        // Verify bill exists
        Bill retrievedBill = billDB.getBill(bill.getBillId());
        assertNotNull(retrievedBill, "Bill not found after creation");
        assertEquals(bill.getCustomerId(), retrievedBill.getCustomerId(), "Customer ID mismatch");
        assertEquals(bill.getTotalAmount(), retrievedBill.getTotalAmount(), "Total amount mismatch");
        assertEquals(2, retrievedBill.getBillItems().size(), "Bill items count mismatch");

        // Verify stock updates
        try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT stock_quantity FROM items WHERE item_id = ?")) {
            stmt.setInt(1, 1);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    assertEquals(98, rs.getInt("stock_quantity"), "Stock quantity for item 1 incorrect");
                }
            }
            stmt.setInt(1, 2);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    assertEquals(99, rs.getInt("stock_quantity"), "Stock quantity for item 2 incorrect");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to verify stock quantities: " + e.getMessage());
        }
    }

    @Test
    public void testCreateBillInsufficientStock() {
        System.out.println("createBillInsufficientStock");
        // Set low stock for item
        try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS)) {
            conn.createStatement().execute("UPDATE items SET stock_quantity = 1 WHERE item_id = 1");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to set up stock: " + e.getMessage());
        }

        // Create a bill with quantity exceeding stock
        Bill bill = new Bill();
        bill.setCustomerId(1);
        bill.setBillDate(LocalDateTime.now());
        bill.setTotalAmount(100.0);

        List<Bill.BillItem> billItems = new ArrayList<>();
        Bill.BillItem item = new Bill.BillItem();
        item.setItemId(1);
        item.setQuantity(2); // Exceeds stock
        item.setPrice(50.0);
        billItems.add(item);
        bill.setBillItems(billItems);

        // Test bill creation failure
        boolean result = billDB.createBill(bill);
        assertFalse(result, "Bill creation should fail due to insufficient stock");

        // Verify no bill was created
        try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM bills")) {
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    assertEquals(0, rs.getInt(1), "No bills should be created");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to verify bill count: " + e.getMessage());
        }
    }

    @Test
    public void testGetBill() {
        System.out.println("getBill");
        // Create a bill first
        Bill bill = new Bill();
        bill.setCustomerId(1);
        bill.setBillDate(LocalDateTime.now());
        bill.setTotalAmount(100.0);

        List<Bill.BillItem> billItems = new ArrayList<>();
        Bill.BillItem item = new Bill.BillItem();
        item.setItemId(1);
        item.setQuantity(1);
        item.setPrice(100.0);
        billItems.add(item);
        bill.setBillItems(billItems);

        boolean created = billDB.createBill(bill);
        assertTrue(created, "Failed to create bill for getBill test");

        // Test retrieving the bill
        Bill result = billDB.getBill(bill.getBillId());
        assertNotNull(result, "Bill not found");
        assertEquals(bill.getCustomerId(), result.getCustomerId(), "Customer ID mismatch");
        assertEquals(bill.getTotalAmount(), result.getTotalAmount(), "Total amount mismatch");
        assertEquals(1, result.getBillItems().size(), "Bill items count mismatch");
        assertEquals(1, result.getBillItems().get(0).getQuantity(), "Item quantity mismatch");
    }

    @Test
    public void testGetBillNotFound() {
        System.out.println("getBillNotFound");
        Bill result = billDB.getBill(999); // Non-existent bill ID
        assertNull(result, "Should return null for non-existent bill");
    }

    @Test
    public void testGetBillsByCustomer() {
        System.out.println("getBillsByCustomer");
        // Create two bills for the same customer
        Bill bill1 = new Bill();
        bill1.setCustomerId(1);
        bill1.setBillDate(LocalDateTime.now());
        bill1.setTotalAmount(100.0);
        List<Bill.BillItem> billItems1 = new ArrayList<>();
        Bill.BillItem item1 = new Bill.BillItem();
        item1.setItemId(1);
        item1.setQuantity(1);
        item1.setPrice(100.0);
        billItems1.add(item1);
        bill1.setBillItems(billItems1);
        boolean created1 = billDB.createBill(bill1);
        assertTrue(created1, "Failed to create first bill");

        Bill bill2 = new Bill();
        bill2.setCustomerId(1);
        bill2.setBillDate(LocalDateTime.now());
        bill2.setTotalAmount(200.0);
        List<Bill.BillItem> billItems2 = new ArrayList<>();
        Bill.BillItem item2 = new Bill.BillItem();
        item2.setItemId(2);
        item2.setQuantity(2);
        item2.setPrice(100.0);
        billItems2.add(item2);
        bill2.setBillItems(billItems2);
        boolean created2 = billDB.createBill(bill2);
        assertTrue(created2, "Failed to create second bill");

        // Test retrieving bills by customer
        List<Bill> bills = billDB.getBillsByCustomer(1);
        assertNotNull(bills, "Bills list should not be null");
        assertEquals(2, bills.size(), "Should return 2 bills for customer");
        assertEquals(100.0, bills.get(0).getTotalAmount(), "First bill total amount mismatch");
        assertEquals(200.0, bills.get(1).getTotalAmount(), "Second bill total amount mismatch");
    }

    @Test
    public void testGetBillsByCustomerNoBills() {
        System.out.println("getBillsByCustomerNoBills");
        List<Bill> bills = billDB.getBillsByCustomer(999); // Non-existent customer
        assertNotNull(bills, "Bills list should not be null");
        assertTrue(bills.isEmpty(), "Should return empty list for customer with no bills");
    }
}