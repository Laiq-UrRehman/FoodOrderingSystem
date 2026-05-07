import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public final class Order {

    private final String orderID;
    private final String status;
    private final List<FoodItem> items;
    private final double totalAmount;

    public Order(String orderID, String status, List<FoodItem> items, double totalAmount) {
        this.orderID = orderID;
        this.status = status;
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getStatus() {
        return status;
    }

    public List<FoodItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void getStatusInfo() {
        System.out.println("Order Status: " + status);
    }

    public void cancelOrder() {
        System.out.println("Order Cancelled.");
    }

}