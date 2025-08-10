package Utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Bill {
    private int billId;
    private int customerId;
    private LocalDateTime billDate;
    private double totalAmount;
    private List<BillItem> billItems;

    public Bill() {
        this.billItems = new ArrayList<>();
        this.billDate = LocalDateTime.now();
    }

    public Bill(int customerId, List<BillItem> billItems) {
        this.customerId = customerId;
        this.billItems = billItems != null ? billItems : new ArrayList<>();
        this.billDate = LocalDateTime.now();
        calculateTotalAmount();
    }

    // Getters and Setters
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public LocalDateTime getBillDate() { return billDate; }
    public void setBillDate(LocalDateTime billDate) { this.billDate = billDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public List<BillItem> getBillItems() { return billItems; }
    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems != null ? billItems : new ArrayList<>();
        calculateTotalAmount();
    }

    public void addBillItem(BillItem billItem) {
        if (billItem != null) {
            this.billItems.add(billItem);
            calculateTotalAmount();
        }
    }

    private void calculateTotalAmount() {
        this.totalAmount = 0.0;
        for (BillItem item : billItems) {
            this.totalAmount += item.getSubtotal();
        }
    }

    public static class BillItem {
        private int itemId;
        private int quantity;
        private double price;
        private double subtotal;

        public BillItem() {}

        public BillItem(int itemId, int quantity, double price) {
            this.itemId = itemId;
            this.quantity = quantity;
            this.price = price;
            this.subtotal = quantity * price;
        }

        // Getters and Setters
        public int getItemId() { return itemId; }
        public void setItemId(int itemId) { this.itemId = itemId; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) {
            this.quantity = quantity;
            this.subtotal = quantity * price;
        }

        public double getPrice() { return price; }
        public void setPrice(double price) {
            this.price = price;
            this.subtotal = quantity * price;
        }

        public double getSubtotal() { return subtotal; }
    }
}