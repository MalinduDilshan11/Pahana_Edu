/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package Utils;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ItemDB
 * @author Malindu
 */
public class ItemDBTest {

    private ItemDB instance;
    private Item testItem;

    @BeforeEach
    public void setUp() {
        instance = new ItemDB();
        testItem = new Item();
        testItem.setName("JUnit Test Item");
        testItem.setCategory("Test Category");
        testItem.setPrice(99.99);
        testItem.setStockQuantity(10);
    }

    @AfterEach
    public void tearDown() {
        // Cleanup any test data
        if (testItem.getItemId() > 0) {
            instance.deleteItem(testItem.getItemId());
        }
    }

    @Test
    public void testAddAndGetItem() {
        System.out.println("addItem & getItem");
        
        // Step 1: Add item
        boolean added = instance.addItem(testItem);
        assertTrue(added, "Failed to add test item");

        // Fetch newly inserted ID for retrieval
        List<Item> allItems = instance.getItems();
        assertFalse(allItems.isEmpty(), "Item list should not be empty");
        testItem.setItemId(allItems.get(allItems.size() - 1).getItemId());

        // Step 2: Retrieve item
        Item result = instance.getItem(testItem.getItemId());
        assertNotNull(result, "Item not found");
        assertEquals(testItem.getName(), result.getName(), "Name mismatch");
        assertEquals(testItem.getCategory(), result.getCategory(), "Category mismatch");
        assertEquals(testItem.getPrice(), result.getPrice(), "Price mismatch");
        assertEquals(testItem.getStockQuantity(), result.getStockQuantity(), "Stock quantity mismatch");
    }

    @Test
    public void testUpdateItem() {
        System.out.println("updateItem");

        // Add test item
        assertTrue(instance.addItem(testItem));
        List<Item> allItems = instance.getItems();
        testItem.setItemId(allItems.get(allItems.size() - 1).getItemId());

        // Update details
        testItem.setName("Updated Name");
        testItem.setPrice(199.99);
        boolean updated = instance.updateItem(testItem);
        assertTrue(updated, "Failed to update item");

        // Retrieve updated item
        Item result = instance.getItem(testItem.getItemId());
        assertNotNull(result);
        assertEquals("Updated Name", result.getName(), "Name not updated");
        assertEquals(199.99, result.getPrice(), "Price not updated");
    }

    @Test
    public void testDeleteItem() {
        System.out.println("deleteItem");

        // Add test item
        assertTrue(instance.addItem(testItem));
        List<Item> allItems = instance.getItems();
        testItem.setItemId(allItems.get(allItems.size() - 1).getItemId());

        // Delete item
        boolean deleted = instance.deleteItem(testItem.getItemId());
        assertTrue(deleted, "Failed to delete item");

        // Confirm deletion
        Item result = instance.getItem(testItem.getItemId());
        assertNull(result, "Item should be deleted");
    }

    @Test
    public void testGetItems() {
        System.out.println("getItems");
        List<Item> result = instance.getItems();
        assertNotNull(result, "Item list should not be null");
        assertTrue(result.size() >= 0, "Item list size should be >= 0");
    }
}
