
// Updated: Added cuisineType, rating, totalRatings fields and rate() method for restaurant-level ratings
// Rating: Submits a new rating (1.0 - 5.0) and updates the running average.
import java.io.Serializable;

public class Restaurant implements Serializable {

    private static final long serialVersionUID = 1L;

    private String restaurantID;
    private String name;
    private String address;
    private String cuisineType;
    private Menu menu;
    private RestaurantAdmin admin;
    private Location location;
    private double rating;
    private int totalRatings;

    public Restaurant() {
    }

    public Restaurant(String restaurantID, String name, String address, String cuisineType, Menu menu,
            Location location) {
        this.restaurantID = restaurantID;
        this.name = name;
        this.address = address;
        this.cuisineType = cuisineType;
        this.menu = menu;
        this.location = location;
        this.rating = 0.0;
        this.totalRatings = 0;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public RestaurantAdmin getAdmin() {
        return admin;
    }

    public void setAdmin(RestaurantAdmin admin) {
        this.admin = admin;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getRating() {
        return rating;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public void rate(double newRating) {
        if (newRating < 1.0 || newRating > 5.0) {
            System.out.println("Rating must be between 1 and 5.");
            return;
        }
        rating = ((rating * totalRatings) + newRating) / (totalRatings + 1);
        totalRatings++;
    }

    public void updateMenu() {
        System.out.println("Menu updated.");
    }

    public void acceptOrder() {
        System.out.println("Order accepted.");
    }

    @Override
    public String toString() {
        return "[" + restaurantID + "] " + name + " | " + cuisineType
                + " | Rating: " + String.format("%.1f", rating)
                + " (" + totalRatings + ")";
    }
}