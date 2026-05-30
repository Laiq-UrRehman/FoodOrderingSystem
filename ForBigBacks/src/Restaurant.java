// Updated: Added List<RestaurantOffer> field to store restaurant-side promotional offers
// Updated: addRestaurantOffer(), removeRestaurantOffer(), getRestaurantOffers() added
// Updated: readObject() initialises restaurantOffers if missing for backward-compat with old saves
// Updated: serialVersionUID bumped to 2L because a new serializable field was added

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Restaurant implements Serializable {

    private static final long serialVersionUID = 2L;

    private String restaurantID;
    private String name;
    private String address;
    private String cuisineType;
    private Menu menu;
    private RestaurantAdmin admin;
    private Location location;
    private double rating;
    private int totalRatings;

    // Restaurant-side promotional offers — persisted inside restaurants.dat
    private List<RestaurantOffer> restaurantOffers = new ArrayList<>();

    public Restaurant() {
    }

    public Restaurant(String restaurantID, String name, String address, String cuisineType,
            Menu menu, Location location) {
        this.restaurantID = restaurantID;
        this.name = name;
        this.address = address;
        this.cuisineType = cuisineType;
        this.menu = menu;
        this.location = location;
        this.rating = 0.0;
        this.totalRatings = 0;
        this.restaurantOffers = new ArrayList<>();
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

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

    public double getRating() {
        return rating;
    }

    public int getTotalRatings() {
        return totalRatings;
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

    // ── Rating ────────────────────────────────────────────────────────────────

    public void rate(double newRating) {
        if (newRating < 1.0 || newRating > 5.0) {
            System.out.println("Rating must be between 1 and 5.");
            return;
        }
        rating = ((rating * totalRatings) + newRating) / (totalRatings + 1);
        totalRatings++;
    }

    // ── Restaurant Offers ─────────────────────────────────────────────────────

    public List<RestaurantOffer> getRestaurantOffers() {
        return Collections.unmodifiableList(restaurantOffers);
    }

    public void addRestaurantOffer(RestaurantOffer offer) {
        if (offer == null)
            throw new IllegalArgumentException("Offer cannot be null");
        restaurantOffers.add(offer);
    }

    public void removeRestaurantOffer(String offerID) {
        if (offerID == null || offerID.isBlank())
            throw new IllegalArgumentException("Offer ID cannot be null or empty");
        restaurantOffers.removeIf(o -> o.getOfferID().equalsIgnoreCase(offerID));
    }

    // ── Misc ──────────────────────────────────────────────────────────────────

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

    // ── Deserialization guard ─────────────────────────────────────────────────

    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (restaurantOffers == null)
            restaurantOffers = new ArrayList<>();
    }
}