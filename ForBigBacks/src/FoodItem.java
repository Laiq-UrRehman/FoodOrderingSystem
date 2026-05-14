public class FoodItem {
    private String foodID;
    private String name;
    private double price;
    private String category;
    private int quantity;

    public FoodItem() {
    }

    public FoodItem(String foodID, String name, double price, String category, int quantity) {
        this.foodID = foodID;
        this.name = name;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
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

    public String getDetails() {
        return foodID + " " + name + " " + category + " " + price;
    }
}