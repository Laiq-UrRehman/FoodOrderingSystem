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

    private transient Timer statusTimer;

    public OrderTracking(String trackingID, Order order, Restaurant restaurant, Customer customer, List<Rider> riders) {
        this.trackingID = trackingID;
        this.order = order;
        this.restaurant = restaurant;
        this.customer = customer;
        this.startTime = Instant.now();
        this.currentStatus = "Confirmed";

        for (Rider rider : riders) {
            rider.setLocation(randomLocation());
        }

        assignClosestRider(riders);
        calculateDistancesAndTime();
        startStatusUpdates();
    }

    private String computeStatus() {
        if (order != null && "Cancelled".equals(order.getStatus()))
            return "Cancelled";
        if (startTime == null)
            return "Delivered";

        long elapsedMs = Instant.now().toEpochMilli() - startTime.toEpochMilli();
        long prepMs = minutesToMs(prepMinutes);
        long deliveryMs = minutesToMs(estimatedDeliveryMinutes);

        if (elapsedMs >= deliveryMs)
            return "Delivered";
        if (elapsedMs >= prepMs)
            return "Out for Delivery";
        return "Preparing";
    }

    public String getCurrentStatus() {
        if (order != null && "Cancelled".equals(order.getStatus()))
            return "Cancelled";
        String computed = computeStatus();
        if (order != null && !computed.equals(order.getStatus())) {
            order.updateStatus(computed);
        }
        currentStatus = computed;
        return currentStatus;
    }

    private void startStatusUpdates() {
        statusTimer = new Timer(true);
        long prepMs = minutesToMs(prepMinutes);
        long deliveryMs = minutesToMs(estimatedDeliveryMinutes);

        statusTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (order != null && "Cancelled".equals(order.getStatus())) {
                    if (assignedRider != null) {
                        assignedRider.setAvailable(true);
                        assignedRider.setAssigned(false);
                        updateRiderStatusInFile(true, false);
                    }
                    statusTimer.cancel();
                    return;
                }

                String status = computeStatus();
                boolean changed = !status.equals(currentStatus);
                currentStatus = status;
                if (order != null)
                    order.updateStatus(status);

                if (changed) {
                    System.out.println("[Tracking " + trackingID + "] Status → " + status);
                    saveCustomerToDisk();

                    if ("Delivered".equals(status)) {
                        if (assignedRider != null) {
                            assignedRider.setAvailable(true);
                            assignedRider.setAssigned(false);
                            updateRiderStatusInFile(true, false);
                            System.out.println("[Tracking] Rider " + assignedRider.getName() + " available again.");
                        }
                        statusTimer.cancel();
                    }
                }
            }
        }, minutesToMs(0), 1000L); // tick every second
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

        System.out.println("[Tracking] Rider assigned: " + closest.getName() + " | Distance to restaurant: " + String.format("%.2f", minDistance) + " units");
    }

    private void calculateDistancesAndTime() {
        if (restaurant.getLocation() == null || customer.getLocation() == null) {
            System.out.println("[Tracking] Missing location.");
            return;
        }
        distanceRestaurantToCustomer = restaurant.getLocation().distanceTo(customer.getLocation());
        prepMinutes = order.getItems().size() * 2;
        int travelMinutes = (int) Math.ceil(distanceRestaurantToCustomer);
        this.estimatedDeliveryMinutes = prepMinutes + travelMinutes;

        System.out.println("[Tracking] ETA: " + estimatedDeliveryMinutes + " minutes (" + prepMinutes + " prep + " + travelMinutes + " travel)");
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
            System.out.println("[Tracking] Customer saved. Status: " + currentStatus);
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
            System.out.println("[Tracking] Failed to update rider: " + e.getMessage());
        }
    }

    public String getTrackingID() {
        return trackingID;
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

    public long getElapsedMs() {
        return startTime == null ? Long.MAX_VALUE : Instant.now().toEpochMilli() - startTime.toEpochMilli();
    }

    public long getPrepMs() {
        return minutesToMs(prepMinutes);
    }

    public long getDeliveryMs() {
        return minutesToMs(estimatedDeliveryMinutes);
    }

    public String getSummary() {
        return String.format(
                "Tracking ID  : %s%nOrder ID     : %s%nStatus       : %s%n"
                        + "Rider        : %s (%s)%nRider → Rest.: %.2f units%n"
                        + "Rest. → Cust.: %.2f units%nETA          : %d minutes",
                trackingID, order.getOrderID(), getCurrentStatus(),
                assignedRider != null ? assignedRider.getName() : "None",
                assignedRider != null ? assignedRider.getVehicleType() : "-",
                distanceRiderToRestaurant, distanceRestaurantToCustomer,
                estimatedDeliveryMinutes);
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (order != null && "Cancelled".equals(order.getStatus()))
            return;

        String correct = computeStatus();
        currentStatus = correct;
        if (order != null)
            order.updateStatus(correct);
    }

    private long minutesToMs(int minutes) {
        return (long) minutes * SECONDS_PER_MINUTE * 1000L;
    }

    private Location randomLocation() {
        java.util.Random rand = new java.util.Random();
        return new Location(rand.nextDouble() * 30, rand.nextDouble() * 30);
    }
}