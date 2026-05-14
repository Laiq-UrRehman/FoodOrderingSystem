public class CardPayment extends Payment {
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;

    public CardPayment() {
    }

    public CardPayment(String paymentID, double amount, String status, String cardNumber, String cardHolderName, String expiryDate) {
        super(paymentID, amount, status);
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean validateCard() {
        return cardNumber != null && expiryDate != null;
    }

    @Override
    public boolean processPayment() {
        if (validateCard()) {
            setStatus("Paid");
            return true;
        }

        setStatus("Failed");
        return false;
    }
}