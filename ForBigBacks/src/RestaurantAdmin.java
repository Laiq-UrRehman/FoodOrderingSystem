// Updated: addOffer() and removeOffer() now reuse a single LoyaltyOfferManager instance instead of creating a new one each call
// Updated: persistRestaurant() added to save back changes to restaurants.dat after every mutation
// Updated: getOfferManager() getter added so AdminDashboardController can reuse the same instance

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
    public String getUsername() { return username; }

    @Override
    public String getPassword() { return password; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    public LoyaltyOfferManager getOfferManager() { return offerManager; }

    // ── Saves the current restaurant state back to restaurants.dat ──────────
    private void persistRestaurant() {
        FileHandler<Restaurant> fh = new FileHandler<>();
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
    }

    public void addFoodItem(FoodItem item) {
        if (restaurant != null) {
            restaurant.getMenu().addItem(item);
            persistRestaurant();
            System.out.println(item.getName() + " added successfully.");
        }
    }

    public void removeFoodItem(FoodItem item) {
        if (restaurant != null) {
            restaurant.getMenu().removeItem(item);
            persistRestaurant();
            System.out.println(item.getName() + " removed successfully.");
        }
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
        offerManager.addOffer(offer);
    }

    public void removeOffer(String offerCode) {
        offerManager.removeOffer(offerCode);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.offerManager = new LoyaltyOfferManager();
    }

    public void addCustomization(String foodID, CustomizationGroup group) {
        for (FoodItem item : restaurant.getMenu().getItems()) {
            if (item.getFoodID().equals(foodID)) {
                item.addCustomizationGroup(group);
                persistRestaurant();
                System.out.println("Customization added to " + item.getName());
                return;
            }
        }
        System.out.println("Food item not found.");
    }
    public void removeCustomization(String foodID, String groupName) {
        for (FoodItem item : restaurant.getMenu().getItems()) {
            if (item.getFoodID().equals(foodID)) {
                item.removeCustomizationGroup(groupName);
                persistRestaurant();
                System.out.println("Customization '" + groupName + "' removed from " + item.getName());
                return;
            }
        }
        System.out.println("Food item not found.");
    }
}