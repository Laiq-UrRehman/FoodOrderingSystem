public class LoyaltyOffer {

    private String offerCode;
    private String description;
    private int pointsRequired;
    private double discountPKR;
    private double minOrderPKR;

    public LoyaltyOffer() {}

    public LoyaltyOffer(String offerCode, String description, int pointsRequired, double discountPKR, double minOrderPKR) {
        this.offerCode      = offerCode;
        this.description    = description;
        this.pointsRequired = pointsRequired;
        this.discountPKR    = discountPKR;
        this.minOrderPKR    = minOrderPKR;
    }

    public String getOfferCode()      { 
        return offerCode;
        }
    public String getDescription()    { 
        return description;    
    }
    public int    getPointsRequired() { 
        return pointsRequired; 
    }
    public double getDiscountPKR()    { 
        return discountPKR;    
    }
    public double getMinOrderPKR()    { 
        return minOrderPKR;    
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Points: %d | Min Order: %.0f PKR",
                offerCode, description, pointsRequired, minOrderPKR);
    }
}