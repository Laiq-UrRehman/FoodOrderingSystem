// Updated: validateCard() now performs real format and expiry checks instead of null-only checks.
//   - cardNumber must be exactly 16 digits
//   - cardHolderName must not be blank
//   - expiryDate must be MM/YY format and must not be in the past

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CardPayment extends Payment {
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;

    public CardPayment() {
    }

    public CardPayment(String paymentID, double amount) {
        super(paymentID, amount);
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

    /**
     * Returns the first validation error found, or null if card details are valid.
     *  - cardNumber: exactly 16 digits
     *  - cardHolderName: not blank
     *  - expiryDate: MM/YY format and not expired
     */
    public String getValidationError() {
        if (cardNumber == null || !cardNumber.matches("\\d{16}")) {
            return "Card number must be exactly 16 digits.";
        }
        if (cardHolderName == null || cardHolderName.isBlank()) {
            return "Cardholder name must not be empty.";
        }
        if (expiryDate == null || !expiryDate.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            return "Expiry date must be in MM/YY format (e.g. 08/27).";
        }
        try {
            YearMonth expiry = YearMonth.parse(expiryDate, DateTimeFormatter.ofPattern("MM/yy"));
            if (expiry.isBefore(YearMonth.now())) {
                return "Card has expired.";
            }
        } catch (DateTimeParseException e) {
            return "Invalid expiry date.";
        }
        return null;
    }

    public boolean validateCard() {
        return getValidationError() == null;
    }

    @Override
    public boolean processPayment() {
        String error = getValidationError();
        if (error != null) {
            System.out.println("Card payment failed: " + error);
            setStatus("Failed");
            return false;
        }
        setStatus("Paid");
        System.out.println("Card payment successful.");
        return true;
    }
}