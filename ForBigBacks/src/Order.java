// Order class should not be final to allow inheritance by ScheduledOrder.

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {

    private final String orderID;
    private String status;
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

    public void updateStatus(String newStatus) {
        this.status = newStatus;
        System.out.println("Order status updated to: " + status);
    }

    public void cancelOrder() {
        updateStatus("Cancelled");
    }

    public void proceedWithCashPayment() {
        if ("Cancelled".equals(status)) {
            System.out.println("Cannot proceed to payment. Order is cancelled.");
            return;
        }
        CashPayment cashPayment = new CashPayment(orderID, totalAmount);

    }
    public void proceedWithCardPayment() {
        if ("Cancelled".equals(status)) {
            System.out.println("Cannot proceed to payment. Order is cancelled.");
            return;
        }
        CardPayment cardPayment = new CardPayment(orderID, totalAmount);
    }
}