// Updated: Added ratingValues Map<String, Double> to store the actual star rating per food ID
// Updated: serialVersionUID bumped to 2L because a new serializable field was added
// Updated: markRated(foodID, stars) overload added to store the star value alongside the boolean flag
// Updated: markRated(foodID) legacy overload kept so existing call-sites still compile
// Updated: getRatingValue() returns stored stars (0.0 if not yet rated) for pre-filling the rating panel
// Updated: setRatingValue() updates an existing rating without duplicating the rated-list entry
// Updated: readObject() initialises ratingValues if missing for backward-compat with old saves

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order implements Serializable {

    private static final long serialVersionUID = 2L;

    private final String orderID;
    private String status;
    private final List<FoodItem> items;
    private final double totalAmount;
    private OrderTracking tracking;
    private double deliveryFee;
    private int redeemedPoints = 0;
    private String paymentMethod = "CASH"; // "CASH" or "CARD"
    private double discountApplied = 0;

    public double getDiscountApplied() { 
        return discountApplied; 
    }
    
    public void setDiscountApplied(double d) { 
        this.discountApplied = d; 
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getRedeemedPoints() { 
        return redeemedPoints; 
    }
    public void setRedeemedPoints(int pts) { 
        this.redeemedPoints = pts; 
    }

    private List<String> ratedFoodIDs = new ArrayList<>();
    private Map<String, Double> ratingValues = new HashMap<>();

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

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public void updateStatus(String newStatus) {
        this.status = newStatus;
        System.out.println("Order status updated to: " + status);
    }

    public void cancelOrder() {
        updateStatus("Cancelled");
    }

    public boolean hasRated(String foodID) {
        return ratedFoodIDs.contains(foodID);
    }

    public void markRated(String foodID, double stars) {
        if (!ratedFoodIDs.contains(foodID)) {
            ratedFoodIDs.add(foodID);
        }
        ratingValues.put(foodID, stars);
    }

    public void markRated(String foodID) {
        markRated(foodID, 0.0);
    }

    public double getRatingValue(String foodID) {
        Double val = ratingValues.get(foodID);
        return (val != null) ? val : 0.0;
    }

    public void setRatingValue(String foodID, double stars) {
        ratingValues.put(foodID, stars);
        if (!ratedFoodIDs.contains(foodID)) {
            ratedFoodIDs.add(foodID);
        }
    }

    public void proceedWithCashPayment(Restaurant restaurant, Customer customer, java.util.List<Rider> riders) {
        if ("Cancelled".equals(status)) {
            System.out.println("Cannot proceed to payment. Order is cancelled.");
            return;
        }
        CashPayment cashPayment = new CashPayment(orderID, totalAmount);
        if (cashPayment.processPayment()) {
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

    private void readObject(java.io.ObjectInputStream in)throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (ratedFoodIDs == null) {
            ratedFoodIDs = new ArrayList<>();
        }
        if (ratingValues == null) {
            ratingValues = new HashMap<>();
        }
        if (paymentMethod == null) {
            paymentMethod = "CASH";
        }
    }

    public double getGrandTotal() {
        return totalAmount + deliveryFee;
    }
}