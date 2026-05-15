import java.util.List;

// 10 point per 100 PKR
public class LoyaltyPoints {

    private String loyaltyID;
    private int pointsBalance;

    private static final int POINTS_PER_100_PKR = 10;

    private LoyaltyOfferManager offerManager;

    public LoyaltyPoints() {
        this.pointsBalance = 0;
        this.offerManager = new LoyaltyOfferManager();
    }

    public LoyaltyPoints(String loyaltyID, int pointsBalance) {
        this.loyaltyID = loyaltyID;
        this.pointsBalance = pointsBalance;
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
        int earned = (int) (orderTotalPKR / 100) * POINTS_PER_100_PKR;
        pointsBalance += earned;
        System.out.println("You earned " + earned + " points! Balance: " + pointsBalance + " pts.");
    }

    public List<LoyaltyOffer> getAvailableOffers(double cartTotalPKR) {
        return offerManager.getAvailableOffers(pointsBalance, cartTotalPKR);
    }

// Redeem COde Logic 

    public RedeemCode generateRedeemCode(LoyaltyOffer offer, double cartTotalPKR) {
        if (pointsBalance < offer.getPointsRequired()) {
            System.out.println("Not enough points. You have " + pointsBalance
                    + " pts but need " + offer.getPointsRequired() + " pts.");
            return null;
        }
        if (cartTotalPKR < offer.getMinOrderPKR()) {
            System.out.println("Minimum order for this offer is "
                    + offer.getMinOrderPKR() + " PKR. Your cart is " + cartTotalPKR + " PKR.");
            return null;
        }

        pointsBalance -= offer.getPointsRequired();
        RedeemCode code = new RedeemCode(offer);
        System.out.println("Code generated: " + code.getCode()
                + " | " + offer.getPointsRequired() + " pts deducted. Remaining: " + pointsBalance + " pts.");
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
            System.out.println("Your cart does not meet the minimum order requirement for this code.");
            return 0;
        }

        redeemCode.consume();
        double discount = redeemCode.getOffer().getDiscountPKR();
        System.out.println("Redeem code applied! You saved " + discount + " PKR.");
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