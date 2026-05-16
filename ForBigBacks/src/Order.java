// Order class should not be final to allow inheritance by ScheduledOrder.

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String orderID;
    private String status;
    private final List<FoodItem> items;
    private final double totalAmount;
    private OrderTracking tracking;

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

    public void proceedWithCashPayment(Restaurant restaurant, Customer customer, java.util.List<Rider> riders) {
        if ("Cancelled".equals(status)) {
            System.out.println("Cannot proceed to payment. Order is cancelled.");
            return;
        }
        CashPayment cashPayment = new CashPayment(orderID, totalAmount);
        if (cashPayment.processPayment()) {           // always true for cash
            initTracking(restaurant, customer, riders);
        }
    }

    public void proceedWithCardPayment(String cardNumber, String cardHolderName, String expiryDate, Restaurant restaurant, Customer customer, java.util.List<Rider> riders) {
        if ("Cancelled".equals(status)) {
            System.out.println("Cannot proceed to payment. Order is cancelled.");
            return;
        }
        CardPayment cardPayment = new CardPayment(orderID, totalAmount);
        cardPayment.setCardNumber(cardNumber);
        cardPayment.setCardHolderName(cardHolderName);
        cardPayment.setExpiryDate(expiryDate);
 
        if (cardPayment.processPayment()) {
            initTracking(restaurant, customer, riders);
        } else {
            System.out.println("Card payment failed. Tracking not started.");
        }
    }

    private void initTracking(Restaurant restaurant, Customer customer, java.util.List<Rider> riders) {
        String trackingID = "TRK-" + orderID;
        this.tracking = new OrderTracking(trackingID, this, restaurant, customer, riders);
        System.out.println("[Order] Tracking started: " + trackingID);
    }

    public OrderTracking getTracking() {
        return tracking;
    }
}