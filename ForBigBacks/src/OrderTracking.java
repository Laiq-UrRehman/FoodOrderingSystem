import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OrderTracking implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int SECONDS_PER_MINUTE = 1;

    private String trackingID;
    private Order order;
    private Restaurant restaurant;
    private Customer customer;
    private Rider assignedRider;

    private double distanceRiderToRestaurant;
    private double distanceRestaurantToCustomer;
    private int estimatedDeliveryMinutes;

    private String currentStatus;
    private transient Timer statusTimer;

    public OrderTracking(String trackingID, Order order, Restaurant restaurant, Customer customer, List<Rider> riders) {
        this.trackingID = trackingID;
        this.order = order;
        this.restaurant = restaurant;
        this.customer = customer;
        this.currentStatus = "Confirmed";

        customer.setLocation(randomLocation());
        System.out.println("[Tracking] Customer location randomized to: " + customer.getLocation());

        for (Rider rider : riders) {
            rider.setLocation(randomLocation());
            System.out.println("[Tracking] Rider " + rider.getName() + " location randomized to: " + rider.getLocation());
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

            if (rider.getLocation() == null) {
                System.out.println("[Tracking] Skipping rider " + rider.getName() + " — no location set.");
                continue;
            }

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
        distanceRestaurantToCustomer = restaurant.getLocation().distanceTo(customer.getLocation());

        int prepTimeMinutes = order.getItems().size();
        int travelMinutes = (int) Math.ceil(distanceRestaurantToCustomer);

        this.estimatedDeliveryMinutes = prepTimeMinutes + travelMinutes;

        System.out.println("[Tracking] Restaurant → Customer distance: "
                + String.format("%.2f", distanceRestaurantToCustomer) + " units");

        System.out.println("[Tracking] Estimated delivery time: "
                + estimatedDeliveryMinutes + " minutes "
                + "(" + prepTimeMinutes + " min prep + "
                + travelMinutes + " min travel)");
    }

    private void startStatusUpdates() {
        statusTimer = new Timer(true);

        long prepMs = minutesToMs(order.getItems().size());
        long deliveryMs = minutesToMs(estimatedDeliveryMinutes);

        scheduleStatus("Preparing", 0);
        scheduleStatus("Out for Delivery", prepMs);
        scheduleStatus("Delivered", deliveryMs);
    }

    private void scheduleStatus(String newStatus, long delayMs) {
        statusTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                currentStatus = newStatus;
                order.updateStatus(newStatus);

                System.out.println("[Tracking " + trackingID + "] Status → " + newStatus);

                if ("Delivered".equals(newStatus) && assignedRider != null) {

                    assignedRider.setAvailable(true);
                    assignedRider.setAssigned(false);

                    updateRiderStatusInFile(true, false);

                    System.out.println("[Tracking] Rider "
                            + assignedRider.getName()
                            + " is now available again.");

                    statusTimer.cancel();
                }
            }
        }, delayMs);
    }

    private void updateRiderStatusInFile(boolean available, boolean assigned) {
        FileHandler<Rider> fileHandler = new FileHandler<>();

        Rider[] riders = fileHandler.loadArray("riders.dat");

        if (riders == null || assignedRider == null)
            return;

        for (Rider rider : riders) {
            if (rider.getPersonID().equals(assignedRider.getPersonID())) {
                rider.setAvailable(available);
                rider.setAssigned(assigned);
                rider.setLocation(assignedRider.getLocation());
                break;
            }
        }

        fileHandler.saveArray(riders, "riders.dat");
    }

    private long minutesToMs(int minutes) {
        return (long) minutes * SECONDS_PER_MINUTE * 1000L;
    }

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
                trackingID,
                order.getOrderID(),
                currentStatus,
                assignedRider != null ? assignedRider.getName() : "None",
                assignedRider != null ? assignedRider.getVehicleType() : "-",
                distanceRiderToRestaurant,
                distanceRestaurantToCustomer,
                estimatedDeliveryMinutes);
    }

    private Location randomLocation() {
        java.util.Random rand = new java.util.Random();
        double x = rand.nextDouble() * 100;
        double y = rand.nextDouble() * 100;

        return new Location(x, y);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {

        in.defaultReadObject();

        if (!"Delivered".equals(currentStatus)
                && !"Cancelled".equals(currentStatus)) {

            currentStatus = "Delivered";

            if (order != null) {
                order.updateStatus("Delivered");
            }

            if (assignedRider != null) {
                assignedRider.setAvailable(true);
                assignedRider.setAssigned(false);

                updateRiderStatusInFile(true, false);
            }
        }
    }
}