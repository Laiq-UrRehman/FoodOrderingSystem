// Updated: Added LoyaltyPoints, placeOrder, viewOrderHistory, cancelOrder, viewScheduledOrders
// Updated: Added preferredCategory tracking based on most ordered category

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Customer extends Person implements Account {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private LoyaltyPoints loyaltyPoints;
    private List<Order> orderHistory;
    private Cart cart;
    private Location location;
    private Map<String, Integer> categoryOrderCounts;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public Customer() {
        this.loyaltyPoints = new LoyaltyPoints();
        this.orderHistory = new ArrayList<>();
        this.cart = new Cart();
        this.categoryOrderCounts = new HashMap<>();
    }

    public Customer(String personID, String name, String address, String phoneNumber, String username, String password,
            Location location) {
        super(personID, name, address, phoneNumber);
        this.username = username;
        this.password = password;
        this.location = location;
        this.loyaltyPoints = new LoyaltyPoints(personID + "-LP", 0);
        this.orderHistory = new ArrayList<>();
        this.cart = new Cart();
        this.categoryOrderCounts = new HashMap<>();
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
        // Track category counts for smart suggestions
        for (FoodItem item : order.getItems()) {
            String cat = item.getCategory();
            categoryOrderCounts.put(cat, categoryOrderCounts.getOrDefault(cat, 0) + 1);
        }
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

    // Smart Suggestions — preferred category

    /**
     * Returns the category this customer has ordered most.
     * Returns null if no orders have been placed yet.
     */
    public String getPreferredCategory() {
        if (categoryOrderCounts.isEmpty())
            return null;
        String preferred = null;
        int max = 0;
        for (Map.Entry<String, Integer> entry : categoryOrderCounts.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                preferred = entry.getKey();
            }
        }
        return preferred;
    }

    public Map<String, Integer> getCategoryOrderCounts() {
        return Collections.unmodifiableMap(categoryOrderCounts);
    }

    // Cart and Location

    public Cart getCart() {
        return cart;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}