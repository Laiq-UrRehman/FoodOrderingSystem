// Updated: persistOrderCounts() now catches FileHandler.FileOperationException
// Updated: addItem() throws IllegalArgumentException for null items
// Updated: removeItem() throws IllegalArgumentException for null or items not in cart
// Updated: updateQuantity() throws IllegalArgumentException for null foodID or non-positive quantity
// Updated: checkOut() variants throw IllegalStateException when cart is empty

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

    public String getCartID() {
        return cartID;
    }

    public void setCartID(String cartID) {
        this.cartID = cartID;
    }

    public List<FoodItem> getItems() {
        return items;
    }

    public void setItems(List<FoodItem> items) {
        this.items = items;
    }

    public double getTotal() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void addItem(FoodItem item) {
        if (item == null)
            throw new IllegalArgumentException("Cannot add null item to cart");
        items.add(item);
        totalAmount += item.getPrice() * item.getQuantity();
    }

    public void removeItem(FoodItem item) {
        if (item == null)
            throw new IllegalArgumentException("Cannot remove null item from cart");
        if (!items.contains(item))
            throw new IllegalArgumentException("Item not found in cart: " + item.getName());
        items.remove(item);
        totalAmount -= item.getPrice() * item.getQuantity();
    }

    public void updateQuantity(String foodID, int quantity) {
        if (foodID == null || foodID.isBlank())
            throw new IllegalArgumentException("Food ID cannot be null or empty");
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive, got: " + quantity);

        for (FoodItem item : items) {
            if (item.getFoodID().equals(foodID)) {
                totalAmount -= item.getPrice() * item.getQuantity();
                item.setQuantity(quantity);
                totalAmount += item.getPrice() * item.getQuantity();
                break;
            }
        }
    }

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
        try {
            Restaurant[] all = fileHandler.loadArray("restaurants.dat");
            if (all != null) {
                for (int i = 0; i < all.length; i++) {
                    if (all[i].getRestaurantID().equals(restaurant.getRestaurantID())) {
                        all[i] = restaurant;
                    }
                }
                fileHandler.saveArray(all, "restaurants.dat");
            }
        } catch (FileHandler.FileOperationException e) {
            System.out.println("Warning: Could not persist order counts: " + e.getMessage());
        }
    }

    public Order checkOut(Customer customer, RedeemCode redeemCode, Restaurant restaurant) {
        if (items.isEmpty())
            throw new IllegalStateException("Cannot check out with an empty cart");
        double discount = customer.getLoyaltyPoints().applyRedeemCode(redeemCode, totalAmount);
        double amountPaid = Math.max(0, totalAmount - discount);
        customer.getLoyaltyPoints().earnPoints(amountPaid);
        Order order = new Order(generateOrderID(), "Pending", items, amountPaid);
        persistOrderCounts(restaurant);
        clearCart();
        return order;
    }

    public Order checkOut(Customer customer, Restaurant restaurant) {
        if (items.isEmpty())
            throw new IllegalStateException("Cannot check out with an empty cart");
        customer.getLoyaltyPoints().earnPoints(totalAmount);
        Order order = new Order(generateOrderID(), "Pending", items, totalAmount);
        persistOrderCounts(restaurant);
        clearCart();
        return order;
    }

    public Order checkOut() {
        if (items.isEmpty())
            throw new IllegalStateException("Cannot check out with an empty cart");
        Order order = new Order(generateOrderID(), "Pending", items, totalAmount);
        clearCart();
        return order;
    }

    public ScheduledOrder checkOutScheduled(Customer customer, LocalDateTime scheduledTime,
            Restaurant restaurant) {
        if (items.isEmpty())
            throw new IllegalStateException("Cannot check out with an empty cart");
        if (scheduledTime == null)
            throw new IllegalArgumentException("Scheduled time cannot be null");

        ScheduledOrder order = new ScheduledOrder(generateOrderID(), items, totalAmount, scheduledTime);
        if (!order.isValidSchedule()) {
            System.out.println("Scheduled time must be at least 30 minutes from now.");
            return null;
        }
        customer.getLoyaltyPoints().earnPoints(totalAmount);
        persistOrderCounts(restaurant);
        clearCart();
        return order;
    }

    public ScheduledOrder checkOutScheduled(Customer customer, RedeemCode redeemCode,
            LocalDateTime scheduledTime, Restaurant restaurant) {
        if (items.isEmpty())
            throw new IllegalStateException("Cannot check out with an empty cart");
        if (scheduledTime == null)
            throw new IllegalArgumentException("Scheduled time cannot be null");

        ScheduledOrder probe = new ScheduledOrder("probe", items, totalAmount, scheduledTime);
        if (!probe.isValidSchedule()) {
            System.out.println("Scheduled time must be at least 30 minutes from now.");
            return null;
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