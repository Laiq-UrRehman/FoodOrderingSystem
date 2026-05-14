abstract class Payment {
    private String paymentID;
    private double amount;
    private String status;
    public Payment(){}
    public Payment(String paymentID, double amount){
        this.paymentID = paymentID;
        this.amount = amount;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public abstract boolean processPayment();
}
