public class CashPayment extends Payment {

    public CashPayment() {
    }

    public CashPayment(String paymentID, double amount) {
        super(paymentID, amount);
    }

    @Override
    public boolean processPayment() {
        setStatus("Paid");
        return true;
    }
}