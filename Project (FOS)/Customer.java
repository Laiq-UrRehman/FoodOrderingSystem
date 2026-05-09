import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Customer extends Person {

    private LoyaltyPoints loyaltyPoints;
    private List<Order> orderHistory;

    public Customer() {
        this.loyaltyPoints = new LoyaltyPoints();
        this.orderHistory  = new ArrayList<>();
    }

    public Customer(String personID, String name, String address, String phoneNumber) {
        super(personID, name, address, phoneNumber);
        this.loyaltyPoints = new LoyaltyPoints(personID + "-LP", 0);
        this.orderHistory  = new ArrayList<>();
    }

    // ── Loyalty Points ────────────────────────────────────────────────────────

    public LoyaltyPoints getLoyaltyPoints() {
        return loyaltyPoints;
    }

    /**
     * Returns the customer's current points balance.
     */
    public int viewLoyaltyPoints() {
        return loyaltyPoints.getPointsBalance();
    }

    // ── Order Management ──────────────────────────────────────────────────────

    /**
     * Records a placed order into the customer's history.
     * Called after Cart.checkOut() returns an Order.
     */
    public void placeOrder(Order order) {
        if (order == null) {
            System.out.println("Cannot place a null order.");
            return;
        }
        orderHistory.add(order);
        System.out.println("Order placed: " + order.getOrderID()
                + " | Total: " + order.getTotalAmount() + " PKR");
    }

    /**
     * Returns an unmodifiable view of all past orders.
     */
    public List<Order> viewOrderHistory() {
        return Collections.unmodifiableList(orderHistory);
    }

    /**
     * Cancels an order by ID if it exists in the customer's history.
     * Delegates the cancellation message to Order itself.
     */
    public void cancelOrder(String orderID) {
        for (Order order : orderHistory) {
            if (order.getOrderID().equals(orderID)) {
                order.cancelOrder();
                return;
            }
        }
        System.out.println("Order not found: " + orderID);
    }
}
