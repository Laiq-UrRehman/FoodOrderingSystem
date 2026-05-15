public class RestaurantAdmin extends Person implements Account {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private Restaurant restaurant;

    public RestaurantAdmin() {
    }

    public RestaurantAdmin(String personID, String name, String address, String phoneNumber, String username, String password, Restaurant restaurant) {
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

    public void addFoodItem(FoodItem item) {
        if (restaurant != null) {
            restaurant.getMenu().addItem(item);
            System.out.println(item.getName() + " added successfully.");
        }
    }

    public void removeFoodItem(FoodItem item) {
        if (restaurant != null) {
            restaurant.getMenu().removeItem(item);
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
                    item.getFoodID() + " | " + item.getName() + " | " + item.getCategory() + " | " + item.getPrice()
            );
        }
    }

    public void addOffer(LoyaltyOffer offer) {
        LoyaltyOfferManager manager = new LoyaltyOfferManager();
        manager.addOffer(offer);
    }

    public void removeOffer(String offerCode) {
        LoyaltyOfferManager manager = new LoyaltyOfferManager();
        manager.removeOffer(offerCode);
    }
}