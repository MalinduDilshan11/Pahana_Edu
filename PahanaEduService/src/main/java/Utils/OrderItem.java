package Utils;

public class OrderItem {
    private int itemId;
    private int unitsConsumed;

    public OrderItem() {}

    public OrderItem(int itemId, int unitsConsumed) {
        this.itemId = itemId;
        this.unitsConsumed = unitsConsumed;
    }

    // Getters and Setters
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getUnitsConsumed() { return unitsConsumed; }
    public void setUnitsConsumed(int unitsConsumed) { this.unitsConsumed = unitsConsumed; }
}