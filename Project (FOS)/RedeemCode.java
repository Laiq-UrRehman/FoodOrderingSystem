import java.util.UUID;

public class RedeemCode {

    private final String code;
    private final LoyaltyOffer offer;
    private boolean used;

    public RedeemCode(LoyaltyOffer offer) {
        // e.g. "LOYAL-A-3F9C"
        this.code  = offer.getOfferCode() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        this.offer = offer;
        this.used  = false;
    }

    public String      getCode()  { 
        return code;  
    }
    public LoyaltyOffer getOffer(){ 
        return offer; 
    }
    public boolean     isUsed()   { 
        return used;  
    }


    public boolean consume() {
        if (used) return false;
        used = true;
        return true;
    }

    @Override
    public String toString() {
        return "Redeem Code : " + code
             + "\nDiscount    : " + offer.getDiscountPKR() + " PKR"
             + "\nOffer       : " + offer.getDescription()
             + "\nStatus      : " + (used ? "Used" : "Active");
    }
}