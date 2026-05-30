// Updated: persistRestaurant() now catches FileHandler.FileOperationException instead of checking null return
// Updated: addFoodItem() and removeFoodItem() throw IllegalArgumentException for null items
// Updated: addCustomization() and removeCustomization() throw IllegalArgumentException for null or blank arguments
// Updated: addOffer() and removeOffer() throw IllegalArgumentException for null or blank arguments
// Updated: addRestaurantOffer() and removeRestaurantOffer() delegate to restaurant and persist

public class RestaurantAdmin extends Person implements Account {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private Restaurant restaurant;
    private transient LoyaltyOfferManager offerManager = new LoyaltyOfferManager();

    public RestaurantAdmin() {
    }

    public RestaurantAdmin(String personID, String name, String address, String phoneNumber, String username,
            String password, Restaurant restaurant) {
        super(personID, name, address, phoneNumber);
        this.username = username;
        this.password = password;
        this.restaurant = restaurant;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public LoyaltyOfferManager getOfferManager() {
        return offerManager;
    }

    private void persistRestaurant() {
        FileHandler<Restaurant> fh = new FileHandler<>();
        try {
            Restaurant[] restaurants = fh.loadArray("restaurants.dat");
            if (restaurants == null) {
                System.out.println("Could not load restaurants.dat for saving.");
                return;
            }
            for (int i = 0; i < restaurants.length; i++) {
                if (restaurants[i].getRestaurantID().equals(restaurant.getRestaurantID())) {
                    restaurants[i] = restaurant;
                    break;
                }
            }
            fh.saveArray(restaurants, "restaurants.dat");
        } catch (FileHandler.FileOperationException e) {
            System.out.println("Warning: Could not persist restaurant changes: " + e.getMessage());
        }
    }

    public void addFoodItem(FoodItem item) {
        if (item == null)
            throw new IllegalArgumentException("Food item cannot be null");
        if (restaurant == null)
            throw new IllegalStateException("No restaurant assigned to this admin");
        restaurant.getMenu().addItem(item);
        persistRestaurant();
        System.out.println(item.getName() + " added successfully.");
    }

    public void removeFoodItem(FoodItem item) {
        if (item == null)
            throw new IllegalArgumentException("Food item cannot be null");
        if (restaurant == null)
            throw new IllegalStateException("No restaurant assigned to this admin");
        restaurant.getMenu().removeItem(item);
        persistRestaurant();
        System.out.println(item.getName() + " removed successfully.");
    }

    public void updateFoodItem(String foodID, String newName, String newCategory,
            double newPrice, int newQuantity) {
        if (foodID == null || foodID.isBlank())
            throw new IllegalArgumentException("Food ID cannot be null or empty");
        if (newName == null || newName.isBlank())
            throw new IllegalArgumentException("Item name cannot be null or empty");
        if (newCategory == null || newCategory.isBlank())
            throw new IllegalArgumentException("Category cannot be null or empty");
        if (newPrice <= 0)
            throw new IllegalArgumentException("Price must be positive");
        if (newQuantity < 0)
            throw new IllegalArgumentException("Quantity cannot be negative");
        if (restaurant == null)
            throw new IllegalStateException("No restaurant assigned to this admin");

        for (FoodItem item : restaurant.getMenu().getItems()) {
            if (item.getFoodID().equals(foodID)) {
                item.setName(newName);
                item.setCategory(newCategory);
                item.setPrice(newPrice);
                item.setQuantity(newQuantity);
                persistRestaurant();
                System.out.println(newName + " updated successfully.");
                return;
            }
        }
        System.out.println("Food item not found: " + foodID);
    }

    public void viewMenu() {
        if (restaurant == null) {
            System.out.println("No restaurant assigned.");
            return;
        }
        System.out.println("===== MENU =====");
        for (FoodItem item : restaurant.getMenu().getItems()) {
            System.out.println(
                    item.getFoodID() + " | " + item.getName() + " | " + item.getCategory() + " | " + item.getPrice());
        }
    }

    public void addOffer(LoyaltyOffer offer) {
        if (offer == null)
            throw new IllegalArgumentException("Offer cannot be null");
        offerManager.addOffer(offer);
    }

    public void removeOffer(String offerCode) {
        if (offerCode == null || offerCode.isBlank())
            throw new IllegalArgumentException("Offer code cannot be null or empty");
        offerManager.removeOffer(offerCode);
    }

    // ── Restaurant Offers ─────────────────────────────────────────────────────

    public void addRestaurantOffer(RestaurantOffer offer) {
        restaurant.addRestaurantOffer(offer);
        persistRestaurant();
    }

    public void removeRestaurantOffer(String offerID) {
        restaurant.removeRestaurantOffer(offerID);
        persistRestaurant();
    }

    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.offerManager = new LoyaltyOfferManager();
    }

    public void addCustomization(String foodID, CustomizationGroup group) {
        if (foodID == null || foodID.isBlank())
            throw new IllegalArgumentException("Food ID cannot be null or empty");
        if (group == null)
            throw new IllegalArgumentException("Customization group cannot be null");
        if (restaurant == null)
            throw new IllegalStateException("No restaurant assigned to this admin");

        for (FoodItem item : restaurant.getMenu().getItems()) {
            if (item.getFoodID().equals(foodID)) {
                item.addCustomizationGroup(group);
                persistRestaurant();
                System.out.println("Customization added to " + item.getName());
                return;
            }
        }
        System.out.println("Food item not found: " + foodID);
    }

    public void removeCustomization(String foodID, String groupName) {
        if (foodID == null || foodID.isBlank())
            throw new IllegalArgumentException("Food ID cannot be null or empty");
        if (groupName == null || groupName.isBlank())
            throw new IllegalArgumentException("Group name cannot be null or empty");
        if (restaurant == null)
            throw new IllegalStateException("No restaurant assigned to this admin");

        for (FoodItem item : restaurant.getMenu().getItems()) {
            if (item.getFoodID().equals(foodID)) {
                item.removeCustomizationGroup(groupName);
                persistRestaurant();
                System.out.println("Customization '" + groupName + "' removed from " + item.getName());
                return;
            }
        }
        System.out.println("Food item not found: " + foodID);
    }

    public void updateCustomization(String foodID, String oldGroupName, CustomizationGroup newGroup) {
        if (foodID == null || foodID.isBlank())
            throw new IllegalArgumentException("Food ID cannot be null or empty");
        if (oldGroupName == null || oldGroupName.isBlank())
            throw new IllegalArgumentException("Old group name cannot be null or empty");
        if (newGroup == null)
            throw new IllegalArgumentException("New customization group cannot be null");
        if (restaurant == null)
            throw new IllegalStateException("No restaurant assigned to this admin");

        for (FoodItem item : restaurant.getMenu().getItems()) {
            if (item.getFoodID().equals(foodID)) {
                item.removeCustomizationGroup(oldGroupName);
                item.addCustomizationGroup(newGroup);
                persistRestaurant();
                System.out.println("Customization '" + oldGroupName + "' updated on " + item.getName());
                return;
            }
        }
        System.out.println("Food item not found: " + foodID);
    }
}