// New class: RestaurantOffer represents a restaurant-side promotional offer (no loyalty points involved)
// Stores a title, description, and discount percentage
// Serializable so it is persisted inside restaurants.dat as part of the Restaurant object

import java.io.Serializable;

public class RestaurantOffer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String offerID;
    private String title;
    private String description;
    private double discountPercent;

    public RestaurantOffer() {
    }

    public RestaurantOffer(String offerID, String title, String description, double discountPercent) {
        this.offerID = offerID;
        this.title = title;
        this.description = description;
        this.discountPercent = discountPercent;
    }

    public String getOfferID() {
        return offerID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    @Override
    public String toString() {
        return "[" + offerID + "] " + title
                + " — " + discountPercent + "% off  |  " + description;
    }
}