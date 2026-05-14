public class CashPayment extends Payment {

    public CashPayment() {
    }

    public CashPayment(String paymentID, double amount, String status) {
        super(paymentID, amount, status);
    }

    @Override
    public boolean processPayment() {
        setStatus("Paid");
        return true;
    }
}