// Update: Added Loyalty Points and Scheduled Order features to the Customer class.

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Cart {
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

//Checkout Logic

    public Order checkOut(Customer customer, RedeemCode redeemCode) {
        double discount = customer.getLoyaltyPoints().applyRedeemCode(redeemCode, totalAmount);
        double amountPaid = Math.max(0, totalAmount - discount);
        customer.getLoyaltyPoints().earnPoints(amountPaid);
        return new Order("ORD001", "Pending", items, amountPaid);
    }


    public Order checkOut(Customer customer) {
        customer.getLoyaltyPoints().earnPoints(totalAmount);
        return new Order("ORD001", "Pending", items, totalAmount);
    }


    public Order checkOut() {
        return new Order("ORD001", "Pending", items, totalAmount);
    }

// Scheduled Order Logic
    public ScheduledOrder checkOutScheduled(Customer customer, LocalDateTime scheduledTime) {
        ScheduledOrder order = new ScheduledOrder("ORD001", items, totalAmount, scheduledTime);
        if (!order.isValidSchedule()) {
            System.out.println("Scheduled time must be at least 30 minutes from now.");
            return null;
        }
        customer.getLoyaltyPoints().earnPoints(totalAmount);
        return order;
    }

    public ScheduledOrder checkOutScheduled(Customer customer, RedeemCode redeemCode, LocalDateTime scheduledTime) {
        double discount = customer.getLoyaltyPoints().applyRedeemCode(redeemCode, totalAmount);
        double amountPaid = Math.max(0, totalAmount - discount);
        ScheduledOrder order = new ScheduledOrder("ORD001", items, amountPaid, scheduledTime);
        if (!order.isValidSchedule()) {
            System.out.println("Scheduled time must be at least 30 minutes from now.");
            return null;
        }
        customer.getLoyaltyPoints().earnPoints(amountPaid);
        return order;
    }
}