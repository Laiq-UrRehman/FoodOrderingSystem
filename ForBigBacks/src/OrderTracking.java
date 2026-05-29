import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OrderTracking implements Serializable {
    private static final long serialVersionUID = 2L;
    private static final int SECONDS_PER_MINUTE = 1;

    private String trackingID;
    private Order order;
    private Restaurant restaurant;
    private Customer customer;
    private Rider assignedRider;

    private double distanceRiderToRestaurant;
    private double distanceRestaurantToCustomer;
    private int estimatedDeliveryMinutes;
    private int prepMinutes;
    private Instant startTime;
    private String currentStatus;

    // Both transient — never serialized, never restarted on deserialization
    private transient Timer statusTimer;
    private transient boolean isLiveInstance;

    public OrderTracking(String trackingID, Order order, Restaurant restaurant,
            Customer customer, List<Rider> riders) {
        this.trackingID = trackingID;
        this.order = order;
        this.restaurant = restaurant;
        this.customer = customer;
        this.currentStatus = "Confirmed";
        this.startTime = Instant.now();
        this.isLiveInstance = true; // only constructor-created instances run timers

        customer.setLocation(randomLocation());
        System.out.println("[Tracking] Customer location randomized to: " + customer.getLocation());

        for (Rider rider : riders) {
            rider.setLocation(randomLocation());
            System.out.println("[Tracking] Rider " + rider.getName()
                    + " location randomized to: " + rider.getLocation());
        }

        assignClosestRider(riders);
        calculateDistancesAndTime();
        startStatusUpdates();
    }

    private void assignClosestRider(List<Rider> riders) {
        Rider closest = null;
        double minDistance = Double.MAX_VALUE;

        if (restaurant.getLocation() == null) {
            System.out.println("[Tracking] ERROR: Restaurant has no location set.");
            return;
        }

        for (Rider rider : riders) {
            if (!rider.getStatus())
                continue;
            if (rider.getLocation() == null)
                continue;
            double dist = rider.getLocation().distanceTo(restaurant.getLocation());
            if (dist < minDistance) {
                minDistance = dist;
                closest = rider;
            }
        }

        if (closest == null) {
            System.out.println("[Tracking] No available riders found.");
            return;
        }

        closest.assignOrder();
        this.assignedRider = closest;
        this.distanceRiderToRestaurant = minDistance;
        updateRiderStatusInFile(false, true);

        System.out.println("[Tracking] Rider assigned: " + closest.getName()
                + " | Distance to restaurant: "
                + String.format("%.2f", minDistance) + " units");
    }

    private void calculateDistancesAndTime() {
        if (restaurant.getLocation() == null || customer.getLocation() == null) {
            System.out.println("[Tracking] Missing location.");
            return;
        }
        distanceRestaurantToCustomer = restaurant.getLocation().distanceTo(customer.getLocation());
        prepMinutes = order.getItems().size();
        int travelMinutes = (int) Math.ceil(distanceRestaurantToCustomer);
        this.estimatedDeliveryMinutes = prepMinutes + travelMinutes;

        System.out.println("[Tracking] Restaurant → Customer distance: "
                + String.format("%.2f", distanceRestaurantToCustomer) + " units");
        System.out.println("[Tracking] Estimated delivery time: "
                + estimatedDeliveryMinutes + " minutes ("
                + prepMinutes + " min prep + " + travelMinutes + " min travel)");
    }

    private void startStatusUpdates() {
        statusTimer = new Timer(true);
        long prepMs = minutesToMs(prepMinutes);
        long deliveryMs = minutesToMs(estimatedDeliveryMinutes);

        scheduleStatusAfter("Preparing", 0);
        scheduleStatusAfter("Out for Delivery", prepMs);
        scheduleStatusAfter("Delivered", deliveryMs);
    }

    private void scheduleStatusAfter(String newStatus, long delayMs) {
        statusTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if ("Cancelled".equals(order.getStatus())) {
                    statusTimer.cancel();
                    if (assignedRider != null) {
                        assignedRider.setAvailable(true);
                        assignedRider.setAssigned(false);
                        updateRiderStatusInFile(true, false);
                    }
                    System.out.println("[Tracking] Order was cancelled.");
                    return;
                }

                currentStatus = newStatus;
                order.updateStatus(newStatus);
                System.out.println("[Tracking " + trackingID + "] Status → " + newStatus);

                // Save the live customer object to disk so re-login sees correct status
                saveCustomerToDisk();

                if ("Delivered".equals(newStatus)) {
                    if (assignedRider != null) {
                        assignedRider.setAvailable(true);
                        assignedRider.setAssigned(false);
                        updateRiderStatusInFile(true, false);
                        System.out.println("[Tracking] Rider "
                                + assignedRider.getName() + " is now available again.");
                    }
                    statusTimer.cancel();
                }
            }
        }, Math.max(0, delayMs));
    }

    private void saveCustomerToDisk() {
        if (customer == null)
            return;
        FileHandler<Customer> fh = new FileHandler<>();
        try {
            Customer[] all = fh.loadArray("customers.dat");
            if (all == null)
                return;
            for (int i = 0; i < all.length; i++) {
                if (all[i].getUsername().equals(customer.getUsername())) {
                    all[i] = customer;
                    break;
                }
            }
            fh.saveArray(all, "customers.dat");
            System.out.println("[Tracking] Customer saved to disk. Status: " + currentStatus);
        } catch (FileHandler.FileOperationException e) {
            System.out.println("[Tracking] Could not save customer: " + e.getMessage());
        }
    }

    private void updateRiderStatusInFile(boolean available, boolean assigned) {
        if (assignedRider == null)
            return;
        FileHandler<Rider> fh = new FileHandler<>();
        try {
            Rider[] riders = fh.loadArray("riders.dat");
            if (riders == null)
                return;
            for (Rider rider : riders) {
                if (rider.getPersonID().equals(assignedRider.getPersonID())) {
                    rider.setAvailable(available);
                    rider.setAssigned(assigned);
                    rider.setLocation(assignedRider.getLocation());
                    break;
                }
            }
            fh.saveArray(riders, "riders.dat");
        } catch (FileHandler.FileOperationException e) {
            System.out.println("[Tracking] Failed to update rider in file: " + e.getMessage());
        }
    }

    private long minutesToMs(int minutes) {
        return (long) minutes * SECONDS_PER_MINUTE * 1000L;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String getTrackingID() {
        return trackingID;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public Rider getAssignedRider() {
        return assignedRider;
    }

    public int getEstimatedDeliveryMinutes() {
        return estimatedDeliveryMinutes;
    }

    public double getDistanceRestaurantToCustomer() {
        return distanceRestaurantToCustomer;
    }

    public double getDistanceRiderToRestaurant() {
        return distanceRiderToRestaurant;
    }

    public String getSummary() {
        return String.format(
                "Tracking ID  : %s%n" +
                        "Order ID     : %s%n" +
                        "Status       : %s%n" +
                        "Rider        : %s (%s)%n" +
                        "Rider → Rest.: %.2f units%n" +
                        "Rest. → Cust.: %.2f units%n" +
                        "ETA          : %d minutes",
                trackingID, order.getOrderID(), currentStatus,
                assignedRider != null ? assignedRider.getName() : "None",
                assignedRider != null ? assignedRider.getVehicleType() : "-",
                distanceRiderToRestaurant, distanceRestaurantToCustomer,
                estimatedDeliveryMinutes);
    }

    private Location randomLocation() {
        java.util.Random rand = new java.util.Random();
        return new Location(rand.nextDouble() * 100, rand.nextDouble() * 100);
    }

    // ── readObject: calculate status from elapsed time, NO timers ever ────────
    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();

        // isLiveInstance stays false (transient) — this is a deserialized snapshot,
        // never runs timers. Status is calculated purely from startTime vs now.

        if ("Delivered".equals(currentStatus) || "Cancelled".equals(currentStatus))
            return;

        if (startTime == null) {
            // Old save format — mark delivered conservatively
            currentStatus = "Delivered";
            if (order != null)
                order.updateStatus("Delivered");
            return;
        }

        long elapsedMs = Instant.now().toEpochMilli() - startTime.toEpochMilli();
        long prepMs = minutesToMs(prepMinutes);
        long deliveryMs = minutesToMs(estimatedDeliveryMinutes);

        String correctStatus;
        if (elapsedMs >= deliveryMs)
            correctStatus = "Delivered";
        else if (elapsedMs >= prepMs)
            correctStatus = "Out for Delivery";
        else
            correctStatus = "Preparing";

        currentStatus = correctStatus;
        if (order != null)
            order.updateStatus(correctStatus);

        // No timer started — deserialized copies are read-only snapshots.
        // The live timer in the session customer object handles all updates.
    }
}