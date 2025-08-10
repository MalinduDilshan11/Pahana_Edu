package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDB {

    public boolean addCustomer(Customer customer) {
        String query = "INSERT INTO customers (name, address, account_number, telephone_number) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getAddress());
            stmt.setString(3, customer.getAccountNumber());
            stmt.setString(4, customer.getTelephoneNumber());
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Customer added: " + customer.getName() + " (Rows affected: " + rowsAffected + ")");
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCustomer(Customer customer) {
        String query = "UPDATE customers SET name = ?, address = ?, account_number = ?, telephone_number = ? WHERE customer_id = ?";
        try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getAddress());
            stmt.setString(3, customer.getAccountNumber());
            stmt.setString(4, customer.getTelephoneNumber());
            stmt.setInt(5, customer.getCustomerId());
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Customer updated: " + customer.getName() + " (Rows affected: " + rowsAffected + ")");
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCustomer(int customerId) {
    String query = "DELETE FROM customers WHERE customer_id = ?";
    try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS);
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, customerId);
        int rowsAffected = stmt.executeUpdate();
        System.out.println("Customer deleted with ID: " + customerId + " (Rows affected: " + rowsAffected + ")");
        return rowsAffected > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    public Customer getCustomer(int customerId) {
        String query = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("customer_id"));
                    customer.setName(rs.getString("name"));
                    customer.setAddress(rs.getString("address"));
                    customer.setAccountNumber(rs.getString("account_number"));
                    customer.setTelephoneNumber(rs.getString("telephone_number"));
                    System.out.println("Customer found: " + customer.getName());
                    return customer;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("No customer found for ID: " + customerId);
        return null;
    }

    public List<Customer> getCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customers";
        try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setName(rs.getString("name"));
                customer.setAddress(rs.getString("address"));
                customer.setAccountNumber(rs.getString("account_number"));
                customer.setTelephoneNumber(rs.getString("telephone_number"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public Customer getCustomerByAccountNumber(String accountNumber) {
        String query = "SELECT * FROM customers WHERE account_number = ?";
        try (Connection conn = DriverManager.getConnection(Utils.DB_URL, Utils.USER, Utils.PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("customer_id"));
                    customer.setName(rs.getString("name"));
                    customer.setAddress(rs.getString("address"));
                    customer.setAccountNumber(rs.getString("account_number"));
                    customer.setTelephoneNumber(rs.getString("telephone_number"));
                    System.out.println("Customer found by account number: " + customer.getName());
                    return customer;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("No customer found for account number: " + accountNumber);
        return null;
    }
}