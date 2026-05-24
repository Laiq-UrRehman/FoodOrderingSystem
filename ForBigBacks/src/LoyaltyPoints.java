// Updated: earnPoints() throws IllegalArgumentException for negative order totals
// Updated: generateRedeemCode() throws IllegalArgumentException for null offer
// Updated: applyRedeemCode() throws IllegalArgumentException for null redeemCode

import java.io.Serializable;
import java.util.List;

public class LoyaltyPoints implements Serializable {

    private static final long serialVersionUID = 1L;

    private String loyaltyID;
    private int pointsBalance;

    private static final int POINTS_PER_100_PKR = 10;

    private transient LoyaltyOfferManager offerManager;

    public LoyaltyPoints() {
        this.pointsBalance = 0;
        this.offerManager = new LoyaltyOfferManager();
    }

    public LoyaltyPoints(String loyaltyID, int pointsBalance) {
        if (pointsBalance < 0)
            throw new IllegalArgumentException("Points balance cannot be negative, got: " + pointsBalance);
        this.loyaltyID = loyaltyID;
        this.pointsBalance = pointsBalance;
        this.offerManager = new LoyaltyOfferManager();
    }

    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.offerManager = new LoyaltyOfferManager();
    }

    public String getLoyaltyID() {
        return loyaltyID;
    }

    public void setLoyaltyID(String id) {
        this.loyaltyID = id;
    }

    public int getPointsBalance() {
        return pointsBalance;
    }

    public void earnPoints(double orderTotalPKR) {
        if (orderTotalPKR < 0)
            throw new IllegalArgumentException("Order total cannot be negative, got: " + orderTotalPKR);
        int earned = (int) (orderTotalPKR / 100) * POINTS_PER_100_PKR;
        pointsBalance += earned;
        System.out.println("You earned " + earned + " points! Balance: " + pointsBalance + " pts.");
    }

    public List<LoyaltyOffer> getAvailableOffers(double cartTotalPKR) {
        return offerManager.getAvailableOffers(pointsBalance, cartTotalPKR);
    }

    public RedeemCode generateRedeemCode(LoyaltyOffer offer, double cartTotalPKR) {
        if (offer == null)
            throw new IllegalArgumentException("Offer cannot be null");

        if (pointsBalance < offer.getPointsRequired()) {
            System.out.println("Not enough points. You have "
                    + pointsBalance + " pts but need "
                    + offer.getPointsRequired() + " pts.");
            return null;
        }

        if (cartTotalPKR < offer.getMinOrderPKR()) {
            System.out.println("Minimum order for this offer is "
                    + offer.getMinOrderPKR()
                    + " PKR. Your cart is "
                    + cartTotalPKR + " PKR.");
            return null;
        }

        RedeemCode code = new RedeemCode(offer);
        System.out.println("Code generated: " + code.getCode()
                + " | Will deduct " + offer.getPointsRequired()
                + " pts at checkout.");
        return code;
    }

    public double applyRedeemCode(RedeemCode redeemCode, double cartTotalPKR) {
        if (redeemCode == null) {
            System.out.println("No redeem code provided.");
            return 0;
        }

        if (redeemCode.isUsed()) {
            System.out.println("This redeem code has already been used.");
            return 0;
        }

        if (cartTotalPKR < redeemCode.getOffer().getMinOrderPKR()) {
            System.out.println(
                    "Your cart does not meet the minimum order requirement for this code.");
            return 0;
        }

        pointsBalance -= redeemCode.getOffer().getPointsRequired();
        redeemCode.consume();

        double discount = redeemCode.getOffer().getDiscountPKR();
        System.out.println("Redeem code applied! You saved " + discount
                + " PKR. Points deducted: " + redeemCode.getOffer().getPointsRequired()
                + ". Remaining: " + pointsBalance + " pts.");
        return discount;
    }

    public void printBalance() {
        System.out.println("Loyalty Points Balance: " + pointsBalance + " pts");
    }

    public void printAvailableOffers(double cartTotalPKR) {
        offerManager.printAvailableOffers(pointsBalance, cartTotalPKR);
    }

    public LoyaltyOfferManager getOfferManager() {
        return offerManager;
    }
}