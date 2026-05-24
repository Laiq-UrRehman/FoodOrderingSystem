// Updated: Constructor throws IllegalArgumentException for null/blank IDs, names, non-positive price, negative quantity
// Updated: rate() throws IllegalArgumentException instead of printing when rating is out of range
// Updated: setPrice() and setQuantity() validate their arguments

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private String foodID;
    private String name;
    private double price;
    private String category;
    private int quantity;
    private double rating;
    private int totalRatings;
    private int orderCount;

    public FoodItem() {
    }

    public FoodItem(String foodID, String name, double price, String category, int quantity) {
        if (foodID == null || foodID.isBlank())
            throw new IllegalArgumentException("Food ID cannot be null or empty");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Food name cannot be null or empty");
        if (price <= 0)
            throw new IllegalArgumentException("Price must be positive, got: " + price);
        if (quantity < 0)
            throw new IllegalArgumentException("Quantity cannot be negative, got: " + quantity);

        this.foodID = foodID;
        this.name = name;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
        this.rating = 0.0;
        this.totalRatings = 0;
        this.orderCount = 0;
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        if (foodID == null || foodID.isBlank())
            throw new IllegalArgumentException("Food ID cannot be null or empty");
        this.foodID = foodID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Food name cannot be null or empty");
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price <= 0)
            throw new IllegalArgumentException("Price must be positive, got: " + price);
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0)
            throw new IllegalArgumentException("Quantity cannot be negative, got: " + quantity);
        this.quantity = quantity;
    }

    public double getRating() {
        return rating;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void incrementOrderCount() {
        orderCount++;
    }

    public void rate(double newRating) {
        if (newRating < 1.0 || newRating > 5.0)
            throw new IllegalArgumentException("Rating must be between 1.0 and 5.0, got: " + newRating);
        rating = ((rating * totalRatings) + newRating) / (totalRatings + 1);
        totalRatings++;
    }

    public String getDetails() {
        return foodID + " " + name + " " + category + " " + price;
    }

    @Override
    public String toString() {
        return "[" + foodID + "] " + name + " | " + category + " | Rs." + price
                + " x" + quantity + " | Rating: " + String.format("%.1f", rating)
                + " (" + totalRatings + ") | Orders: " + orderCount;
    }

    private List<CustomizationGroup> customizationGroups = new ArrayList<>();

    private Map<String, String> selectedCustomizations = new HashMap<>();

    public List<CustomizationGroup> getCustomizationGroups() {
        return customizationGroups;
    }

    public void addCustomizationGroup(CustomizationGroup group) {
        if (group == null)
            throw new IllegalArgumentException("Customization group cannot be null");
        customizationGroups.add(group);
    }

    public void removeCustomizationGroup(String groupName) {
        customizationGroups.removeIf(g -> g.getGroupName().equalsIgnoreCase(groupName));
    }

    public Map<String, String> getSelectedCustomizations() {
        return selectedCustomizations;
    }

    public void setSelectedCustomization(String groupName, String selectedOption) {
        selectedCustomizations.put(groupName, selectedOption);
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (customizationGroups == null)
            customizationGroups = new ArrayList<>();
        if (selectedCustomizations == null)
            selectedCustomizations = new HashMap<>();
    }
}