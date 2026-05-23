
// Updated: Added rating, totalRatings fields and rate() method for item-level ratings
// Updated: Added orderCount field and incrementOrderCount() for popularity tracking
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
        this.foodID = foodID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
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

    /**
     * Called after every checkout to track how many times this item was ordered.
     */
    public void incrementOrderCount() {
        orderCount++;
    }

    public void rate(double newRating) {
        if (newRating < 1.0 || newRating > 5.0) {
            System.out.println("Rating must be between 1 and 5.");
            return;
        }
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