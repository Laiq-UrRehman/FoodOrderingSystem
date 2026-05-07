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

    public Order checkOut() {
        return new Order("ORD001", "Pending", items, totalAmount);
    }
}