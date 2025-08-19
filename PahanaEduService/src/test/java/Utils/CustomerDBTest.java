/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package Utils;

import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Malindu Dilshan
 */
public class CustomerDBTest {

    private CustomerDB instance;

    public CustomerDBTest() {
    }

    @BeforeEach
    public void setUp() {
        instance = new CustomerDB();
    }

    @AfterEach
    public void tearDown() {
        // Cleanup any test data if necessary
    }

    @Test
    public void testAddCustomer() {
        System.out.println("addCustomer");
        Customer customer = new Customer();
        customer.setName("Test User");
        customer.setAddress("123 Test Street");
        customer.setAccountNumber("ACC123456");
        customer.setTelephoneNumber("0771234567");

        boolean result = instance.addCustomer(customer);
        assertTrue(result, "Failed to add test customer");

        // Verify customer was added
        Customer retrieved = instance.getCustomerByAccountNumber("ACC123456");
        assertNotNull(retrieved, "Customer not found");
        assertEquals(customer.getName(), retrieved.getName(), "Name mismatch");
        assertEquals(customer.getAddress(), retrieved.getAddress(), "Address mismatch");
        assertEquals(customer.getAccountNumber(), retrieved.getAccountNumber(), "Account number mismatch");
        assertEquals(customer.getTelephoneNumber(), retrieved.getTelephoneNumber(), "Telephone number mismatch");

        // Cleanup
        instance.deleteCustomer(retrieved.getCustomerId());
    }

    @Test
    public void testUpdateCustomer() {
        System.out.println("updateCustomer");
        Customer customer = new Customer();
        customer.setName("Old Name");
        customer.setAddress("456 Old Street");
        customer.setAccountNumber("ACC654321");
        customer.setTelephoneNumber("0779876543");

        // Add customer first
        instance.addCustomer(customer);

        // Retrieve to get customer ID
        Customer addedCustomer = instance.getCustomerByAccountNumber("ACC654321");
        assertNotNull(addedCustomer, "Customer not found for update");

        // Update customer
        addedCustomer.setName("New Name");
        addedCustomer.setAddress("789 New Street");
        boolean result = instance.updateCustomer(addedCustomer);
        assertTrue(result, "Failed to update customer");

        // Verify update
        Customer updatedCustomer = instance.getCustomer(addedCustomer.getCustomerId());
        assertNotNull(updatedCustomer, "Updated customer not found");
        assertEquals("New Name", updatedCustomer.getName(), "Name not updated");
        assertEquals("789 New Street", updatedCustomer.getAddress(), "Address not updated");

        // Cleanup
        instance.deleteCustomer(addedCustomer.getCustomerId());
    }

    @Test
    public void testDeleteCustomer() throws SQLException {
        System.out.println("deleteCustomer");
        Customer customer = new Customer();
        customer.setName("Delete Me");
        customer.setAddress("999 Delete Street");
        customer.setAccountNumber("ACC999999");
        customer.setTelephoneNumber("0775555555");

        // Add customer first
        instance.addCustomer(customer);

        // Retrieve to get customer ID
        Customer addedCustomer = instance.getCustomerByAccountNumber("ACC999999");
        assertNotNull(addedCustomer, "Customer not found for deletion");

        // Delete customer
        boolean result = instance.deleteCustomer(addedCustomer.getCustomerId());
        assertTrue(result, "Failed to delete customer");

        // Verify deletion
        Customer check = instance.getCustomer(addedCustomer.getCustomerId());
        assertNull(check, "Customer should be deleted");
    }

    @Test
    public void testGetCustomer() {
        System.out.println("getCustomer");
        Customer customer = new Customer();
        customer.setName("Test User");
        customer.setAddress("123 Test Street");
        customer.setAccountNumber("ACC112233");
        customer.setTelephoneNumber("0771122334");

        // Add customer first
        instance.addCustomer(customer);

        // Retrieve to get customer ID
        Customer addedCustomer = instance.getCustomerByAccountNumber("ACC112233");
        assertNotNull(addedCustomer, "Customer not found");

        // Test getCustomer by ID
        Customer result = instance.getCustomer(addedCustomer.getCustomerId());
        assertNotNull(result, "Customer not found by ID");
        assertEquals(customer.getName(), result.getName(), "Name mismatch");
        assertEquals(customer.getAddress(), result.getAddress(), "Address mismatch");
        assertEquals(customer.getAccountNumber(), result.getAccountNumber(), "Account number mismatch");
        assertEquals(customer.getTelephoneNumber(), result.getTelephoneNumber(), "Telephone number mismatch");

        // Cleanup
        instance.deleteCustomer(addedCustomer.getCustomerId());
    }

    @Test
    public void testGetCustomers() {
        System.out.println("getCustomers");
        List<Customer> result = instance.getCustomers();
        assertNotNull(result, "Customer list should not be null");
        assertTrue(result.size() >= 0, "Customer list size should be non-negative");
    }

    @Test
    public void testGetCustomerByAccountNumber() {
        System.out.println("getCustomerByAccountNumber");
        Customer customer = new Customer();
        customer.setName("Test User");
        customer.setAddress("123 Test Street");
        customer.setAccountNumber("ACC445566");
        customer.setTelephoneNumber("0774455667");

        // Add customer first
        instance.addCustomer(customer);

        // Test getCustomerByAccountNumber
        Customer result = instance.getCustomerByAccountNumber("ACC445566");
        assertNotNull(result, "Customer not found by account number");
        assertEquals(customer.getName(), result.getName(), "Name mismatch");
        assertEquals(customer.getAddress(), result.getAddress(), "Address mismatch");
        assertEquals(customer.getAccountNumber(), result.getAccountNumber(), "Account number mismatch");
        assertEquals(customer.getTelephoneNumber(), result.getTelephoneNumber(), "Telephone number mismatch");

        // Cleanup
        instance.deleteCustomer(result.getCustomerId());
    }
}