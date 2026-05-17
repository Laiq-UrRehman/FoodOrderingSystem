// Updated: unconfirm() added to reset isConfirmed if the scheduled time becomes invalid after confirmation

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class ScheduledOrder extends Order {

    private static final long serialVersionUID = 1L;

    private LocalDateTime scheduledTime;
    private boolean isConfirmed;
    private static final int MIN_ADVANCE_MINUTES = 30;

    public ScheduledOrder(String orderID, List<FoodItem> items, double totalAmount, LocalDateTime scheduledTime) {
        super(orderID, "Scheduled", items, totalAmount);
        this.scheduledTime = scheduledTime;
        this.isConfirmed = false;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void confirm() {
        if (!isValidSchedule()) {
            System.out.println("Cannot confirm — scheduled time is no longer valid.");
            return;
        }
        isConfirmed = true;
        System.out.println("Scheduled order " + getOrderID() + " confirmed for " + scheduledTime);
    }

    public void unconfirm() {
        if (!isValidSchedule()) {
            isConfirmed = false;
            System.out.println("Order " + getOrderID() + " unconfirmed — scheduled time is no longer valid.");
        }
    }

    public boolean isValidSchedule() {
        long minutesAhead = Duration.between(LocalDateTime.now(), scheduledTime).toMinutes();
        return minutesAhead >= MIN_ADVANCE_MINUTES;
    }

    @Override
    public void cancelOrder() {
        if (isConfirmed) {
            System.out.println("Cannot cancel — order already confirmed by restaurant.");
            return;
        }
        updateStatus("Cancelled");
    }

    @Override
    public String toString() {
        return "ScheduledOrder [" + getOrderID() + "] | Time: " + scheduledTime + " | Status: " + getStatus()
                + " | Confirmed: " + isConfirmed;
    }

    @Override
    public void proceedWithCashPayment(Restaurant restaurant, Customer customer, List<Rider> riders) {
        if (!isValidSchedule()) {
            System.out.println(
                    "Cannot pay — scheduled time must be at least " + MIN_ADVANCE_MINUTES + " minutes from now.");
            return;
        }
        super.proceedWithCashPayment(restaurant, customer, riders);
    }

    @Override
    public void proceedWithCardPayment(String cardNumber, String cardHolderName, String expiryDate,
            Restaurant restaurant, Customer customer, List<Rider> riders) {
        if (!isValidSchedule()) {
            System.out.println(
                    "Cannot pay — scheduled time must be at least " + MIN_ADVANCE_MINUTES + " minutes from now.");
            return;
        }
        super.proceedWithCardPayment(cardNumber, cardHolderName, expiryDate, restaurant, customer, riders);
    }
}