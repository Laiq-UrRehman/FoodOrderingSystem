// Update: Added Loyalty Points and Scheduled Order features to the Customer class.

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Customer extends Person implements Account{
    private String username;
    private String password;
    private LoyaltyPoints loyaltyPoints;
    private List<Order> orderHistory;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    private Cart cart;

    public Customer() {
        this.loyaltyPoints = new LoyaltyPoints();
        this.orderHistory  = new ArrayList<>();
        this.cart = new Cart();
    }

    public Customer(String personID, String name, String address, String phoneNumber, String username, String password) {
        super(personID, name, address, phoneNumber);
        this.username = username;
        this.password = password;
        this.loyaltyPoints = new LoyaltyPoints(personID + "-LP", 0);
        this.orderHistory  = new ArrayList<>();
    }

// Loyalty Points Logic
    public LoyaltyPoints getLoyaltyPoints() {
        return loyaltyPoints;
    }


    public int viewLoyaltyPoints() {
        return loyaltyPoints.getPointsBalance();
    }

// Order History Logic

    public void placeOrder(Order order) {
        if (order == null) {
            System.out.println("Cannot place a null order.");
            return;
        }
        orderHistory.add(order);
        System.out.println("Order placed: " + order.getOrderID()
                + " | Total: " + order.getTotalAmount() + " PKR");
    }

    public List<Order> viewOrderHistory() {
        return Collections.unmodifiableList(orderHistory);
    }

    public void cancelOrder(String orderID) {
        for (Order order : orderHistory) {
            if (order.getOrderID().equals(orderID)) {
                order.cancelOrder();
                return;
            }
        }
        System.out.println("Order not found: " + orderID);
    }

    public List<ScheduledOrder> viewScheduledOrders() {
        List<ScheduledOrder> scheduled = new ArrayList<>();
        for (Order order : orderHistory) {
            if (order instanceof ScheduledOrder) {
                scheduled.add((ScheduledOrder) order);
            }
        }
        return Collections.unmodifiableList(scheduled);
    }

// View Cart Logic

    public Cart getCart() {
        return cart;
    }

}