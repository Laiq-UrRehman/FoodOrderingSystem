import java.io.Serializable;

public class Offers implements Serializable {

    private static final long serialVersionUID = 1L;

    private String offerID;
    private String description;
    private double discount;
    private String expiry;
    private boolean isActive;

    public Offers() {
    }

    public Offers(String offerID, String description, double discount, String expiry, boolean isActive) {
        this.offerID = offerID;
        this.description = description;
        this.discount = discount;
        this.expiry = expiry;
        this.isActive = isActive;
    }

    public String getOfferID() {
        return offerID;
    }

    public void setOfferID(String offerID) {
        this.offerID = offerID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void applyOffer() {
        if (isActive) {
            System.out.println("Offer Applied");
        }
    }

    public boolean isValid() {
        return isActive;
    }
}