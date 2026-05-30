// Updated: getLastRatingForItem() added to scan order history and return the most recent star value for a food ID
// Updated: getPassword() still throws UnsupportedOperationException — passwords are hashed, use verifyPassword()

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Customer extends Person implements Account {

    private static final long serialVersionUID = 2L;

    private String username;
    private String passwordSalt;
    private String passwordHash;

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
        throw new UnsupportedOperationException(
                "Plain-text password access is disabled. Use Customer.verifyPassword().");
    }

    public boolean verifyPassword(String plainText) {
        return PasswordUtils.verify(plainText, passwordSalt, passwordHash);
    }

    public Customer() {
        this.loyaltyPoints = new LoyaltyPoints();
        this.orderHistory = new ArrayList<>();
        this.cart = new Cart();
        this.categoryOrderCounts = new HashMap<>();
    }

    public Customer(String personID, String name, String address, String phoneNumber,
            String username, String password, Location location) {
        super(personID, name, address, phoneNumber);
        this.username = username;
        String[] hashed = PasswordUtils.hashPassword(password);
        this.passwordSalt = hashed[0];
        this.passwordHash = hashed[1];
        this.location = location;
        this.loyaltyPoints = new LoyaltyPoints(personID + "-LP", 0);
        this.orderHistory = new ArrayList<>();
        this.cart = new Cart();
        this.categoryOrderCounts = new HashMap<>();
    }

    // ── Loyalty Points ────────────────────────────────────────────────────────

    public LoyaltyPoints getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public int viewLoyaltyPoints() {
        return loyaltyPoints.getPointsBalance();
    }

    // ── Order History ─────────────────────────────────────────────────────────

    public void placeOrder(Order order) {
        if (order == null) {
            System.out.println("Cannot place a null order.");
            return;
        }
        orderHistory.add(order);
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

    // ── Ratings ───────────────────────────────────────────────────────────────

    /**
     * Scans order history from newest to oldest and returns the most recent
     * star value this customer gave the specified food item, or 0.0 if they
     * have never rated it. Used to pre-fill stars when the same item appears
     * in a new order.
     */
    public double getLastRatingForItem(String foodID) {
        if (foodID == null || foodID.isBlank())
            return 0.0;
        for (int i = orderHistory.size() - 1; i >= 0; i--) {
            Order order = orderHistory.get(i);
            if (order.hasRated(foodID)) {
                double val = order.getRatingValue(foodID);
                if (val >= 1.0)
                    return val;
            }
        }
        return 0.0;
    }

    // ── Smart Suggestions ─────────────────────────────────────────────────────

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

    // ── Cart and Location ─────────────────────────────────────────────────────

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