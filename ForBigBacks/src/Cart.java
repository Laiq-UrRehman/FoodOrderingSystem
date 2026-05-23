// FIX (Critical): checkOutScheduled() variants now validate the schedule BEFORE
//   calling persistOrderCounts() or clearCart(). Previously the cart was wiped
//   and order-counts incremented even when the method returned null.

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cartID;
    private List<FoodItem> items;
    private double totalAmount;

    public Cart() {
        items = new ArrayList<>();
    }

    public Cart(String cartID, List<FoodItem> items, double totalAmount) {
        this.cartID = cartID;
        this.items = items;
        this.totalAmount = totalAmount;
    }

    public String getCartID() { return cartID; }
    public void setCartID(String cartID) { this.cartID = cartID; }

    public List<FoodItem> getItems() { return items; }
    public void setItems(List<FoodItem> items) { this.items = items; }

    public double getTotal() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public void addItem(FoodItem item) {
        items.add(item);
        totalAmount += item.getPrice() * item.getQuantity();
    }

    public void removeItem(FoodItem item) {
        items.remove(item);
        totalAmount -= item.getPrice() * item.getQuantity();
    }

    public void updateQuantity(String foodID, int quantity) {
        for (FoodItem item : items) {
            if (item.getFoodID().equals(foodID)) {
                totalAmount -= item.getPrice() * item.getQuantity();
                item.setQuantity(quantity);
                totalAmount += item.getPrice() * item.getQuantity();
                break; // FIX (Medium): stop after first match to avoid double-updating
            }
        }
    }

    // Loyalty and Redeem Code Logic

    public List<LoyaltyOffer> showLoyaltyOffers(Customer customer) {
        List<LoyaltyOffer> available = customer.getLoyaltyPoints().getAvailableOffers(totalAmount);
        if (available.isEmpty()) {
            System.out.println("No loyalty offers available for your current points or cart total.");
        } else {
            System.out.println("=== Available Loyalty Offers ===");
            for (LoyaltyOffer offer : available) {
                System.out.println(offer);
            }
        }
        return available;
    }

    public RedeemCode selectOffer(Customer customer, LoyaltyOffer offer) {
        return customer.getLoyaltyPoints().generateRedeemCode(offer, totalAmount);
    }

    private String generateOrderID() {
        return "ORD-" + System.currentTimeMillis();
    }

    private void persistOrderCounts(Restaurant restaurant) {
        for (FoodItem cartItem : items) {
            for (FoodItem menuItem : restaurant.getMenu().getItems()) {
                if (menuItem.getFoodID().equals(cartItem.getFoodID())) {
                    menuItem.incrementOrderCount();
                }
            }
        }
        FileHandler<Restaurant> fileHandler = new FileHandler<>();
        Restaurant[] all = fileHandler.loadArray("restaurants.dat");
        if (all != null) {
            for (int i = 0; i < all.length; i++) {
                if (all[i].getRestaurantID().equals(restaurant.getRestaurantID())) {
                    all[i] = restaurant;
                }
            }
            fileHandler.saveArray(all, "restaurants.dat");
        }
    }

    // Checkout Logic

    public Order checkOut(Customer customer, RedeemCode redeemCode, Restaurant restaurant) {
        double discount = customer.getLoyaltyPoints().applyRedeemCode(redeemCode, totalAmount);
        double amountPaid = Math.max(0, totalAmount - discount);
        customer.getLoyaltyPoints().earnPoints(amountPaid);
        Order order = new Order(generateOrderID(), "Pending", items, amountPaid);
        persistOrderCounts(restaurant);
        clearCart();
        return order;
    }

    public Order checkOut(Customer customer, Restaurant restaurant) {
        customer.getLoyaltyPoints().earnPoints(totalAmount);
        Order order = new Order(generateOrderID(), "Pending", items, totalAmount);
        persistOrderCounts(restaurant);
        clearCart();
        return order;
    }

    public Order checkOut() {
        Order order = new Order(generateOrderID(), "Pending", items, totalAmount);
        clearCart();
        return order;
    }

    // Scheduled Order Logic
    // FIX: Validate the schedule FIRST. Only persist counts and clear the cart
    // after we are certain an order object will be returned.

    public ScheduledOrder checkOutScheduled(Customer customer, LocalDateTime scheduledTime,
            Restaurant restaurant) {
        ScheduledOrder order = new ScheduledOrder(generateOrderID(), items, totalAmount, scheduledTime);
        if (!order.isValidSchedule()) {
            System.out.println("Scheduled time must be at least 30 minutes from now.");
            return null; // Cart is NOT cleared — nothing has changed yet
        }
        customer.getLoyaltyPoints().earnPoints(totalAmount);
        persistOrderCounts(restaurant);
        clearCart();
        return order;
    }

    public ScheduledOrder checkOutScheduled(Customer customer, RedeemCode redeemCode,
            LocalDateTime scheduledTime, Restaurant restaurant) {
        // Validate schedule before touching anything
        ScheduledOrder probe = new ScheduledOrder("probe", items, totalAmount, scheduledTime);
        if (!probe.isValidSchedule()) {
            System.out.println("Scheduled time must be at least 30 minutes from now.");
            return null; // Points not deducted, cart not cleared
        }
        double discount = customer.getLoyaltyPoints().applyRedeemCode(redeemCode, totalAmount);
        double amountPaid = Math.max(0, totalAmount - discount);
        ScheduledOrder order = new ScheduledOrder(generateOrderID(), items, amountPaid, scheduledTime);
        customer.getLoyaltyPoints().earnPoints(amountPaid);
        persistOrderCounts(restaurant);
        clearCart();
        return order;
    }

    public void clearCart() {
        items.clear();
        totalAmount = 0;
    }

    public void viewCart() {
        if (items.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        System.out.println("=== Your Cart ===");
        for (FoodItem item : items) {
            System.out.println(item);
        }
        System.out.println("Total: " + totalAmount + " PKR");
    }
}