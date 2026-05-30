import java.io.Serializable;
import java.util.List;

// 10 points per 100 PKR
// FIX (Critical): Points are no longer deducted inside generateRedeemCode().
//   Deduction now happens only inside applyRedeemCode(), which is called at
//   checkout after the order is confirmed. This prevents points being lost
//   when payment fails or the order is never placed.
public class LoyaltyPoints implements Serializable {

    private static final long serialVersionUID = 1L;

    private String loyaltyID;
    private int pointsBalance;

    private static final int POINTS_PER_100_PKR = 10;

    // transient: LoyaltyOfferManager does file I/O only, no need to serialize it
    private transient LoyaltyOfferManager offerManager;

    public LoyaltyPoints() {
        this.pointsBalance = 0;
        this.offerManager = new LoyaltyOfferManager();
    }

    public LoyaltyPoints(String loyaltyID, int pointsBalance) {
        this.loyaltyID = loyaltyID;
        this.pointsBalance = pointsBalance;
        this.offerManager = new LoyaltyOfferManager();
    }

    // Recreate transient field after deserialization
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

    public void deductPoints(double orderTotalPKR) {
        int toDeduct = (int) (orderTotalPKR / 100) * POINTS_PER_100_PKR;
        pointsBalance = Math.max(0, pointsBalance - toDeduct);
        System.out.println("Deducted " + toDeduct + " points due to cancellation. Balance: " + pointsBalance + " pts.");
    }

    public void refundPoints(int pts) {
        if (pts <= 0) return;
        pointsBalance += pts;
        System.out.println("Refunded " + pts + " points due to cancellation. Balance: " + pointsBalance + " pts.");
    }

    public void earnPoints(double orderTotalPKR) {
        int earned = (int) (orderTotalPKR / 100) * POINTS_PER_100_PKR;
        pointsBalance += earned;
        System.out.println("You earned " + earned +
                " points! Balance: " + pointsBalance + " pts.");
    }

    public List<LoyaltyOffer> getAvailableOffers(double cartTotalPKR) {
        return offerManager.getAvailableOffers(pointsBalance, cartTotalPKR);
    }

    // Redeem Code Logic
    // FIX: Points are NO LONGER deducted here. This method now only validates
    // eligibility and creates the code object. The actual deduction happens in
    // applyRedeemCode() at the moment the code is consumed at checkout.
    public RedeemCode generateRedeemCode(LoyaltyOffer offer, double cartTotalPKR) {

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

        // No deduction here — deduction happens in applyRedeemCode()
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

        // FIX: Points are deducted HERE, only when the code is actually consumed
        // at checkout — not when the code was generated.
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