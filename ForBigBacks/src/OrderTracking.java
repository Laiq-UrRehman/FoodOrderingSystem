import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * OrderTracking.java  (NEW CLASS)
 *
 * Manages the full lifecycle of an order after successful payment:
 *
 *  Step 1 – Find the closest available rider to the restaurant.
 *  Step 2 – Assign that rider; mark them unavailable.
 *  Step 3 – Simulate the preparation phase (10 minutes fixed).
 *  Step 4 – Calculate restaurant → customer distance and estimate travel time.
 *  Step 5 – Fire a Timer to advance order status automatically:
 *              "Confirmed" → "Preparing" → "Out for Delivery" → "Delivered"
 *
 * TIME SCALE (adjustable via SECONDS_PER_UNIT):
 *   We map each "distance unit" to a number of real seconds so you can
 *   demo the whole flow without waiting real minutes.
 *   Set SECONDS_PER_UNIT = 60 for a 1 unit ≈ 1 minute experience.
 *   Currently set to 5 seconds per unit for quick testing.
 *
 * DISTANCE → TIME FORMULA:
 *   Assumed average speed = 1 unit/minute on the simulated grid.
 *   Estimated delivery time (minutes) = distance (units) × 1.
 *   e.g. distance of 8 units → 8 minutes travel after preparation.
 */
public class OrderTracking implements Serializable {

    private static final long serialVersionUID = 1L;

    // ── Tuneable constants ────────────────────────────────────────────────────

    /** Fixed preparation time in minutes. */
    private static final int PREP_TIME_MINUTES = 10;

    /**
     * How many real-world seconds represent 1 simulated minute.
     * Set to 5 for quick demos; set to 60 for realistic timing.
     */
    private static final int SECONDS_PER_MINUTE = 1;

    // ─────────────────────────────────────────────────────────────────────────

    private String     trackingID;
    private Order      order;
    private Restaurant restaurant;
    private Customer   customer;
    private Rider      assignedRider;

    private double distanceRiderToRestaurant;    // units
    private double distanceRestaurantToCustomer; // units
    private int    estimatedDeliveryMinutes;     // prep + travel

    private String currentStatus;

    // Non-serializable Timer — transient so serialisation doesn't break.
    private transient Timer statusTimer;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * @param trackingID  Unique ID for this tracking record.
     * @param order       The order being tracked.
     * @param restaurant  The restaurant fulfilling the order.
     * @param customer    The customer who placed the order.
     * @param riders      Full list of registered riders to search through.
     */
    public OrderTracking(String trackingID, Order order, Restaurant restaurant,
                         Customer customer, List<Rider> riders) {
        this.trackingID  = trackingID;
        this.order       = order;
        this.restaurant  = restaurant;
        this.customer    = customer;
        this.currentStatus = "Confirmed";

        // Randomize customer and rider locations on a 0-100 grid.
        // The restaurant location stays fixed (set externally when the Restaurant
        // object is created). Only the moving actors get fresh random positions
        // each time an order is tracked.
        customer.setLocation(randomLocation());
        System.out.println("[Tracking] Customer location randomized to: " + customer.getLocation());

        for (Rider rider : riders) {
            rider.setLocation(randomLocation());
            System.out.println("[Tracking] Rider " + rider.getName()
                    + " location randomized to: " + rider.getLocation());
        }

        // Step 1 & 2 – find + assign closest rider
        assignClosestRider(riders);

        // Step 3 & 4 – calculate distances and estimate time
        calculateDistancesAndTime();

        // Step 5 – begin automatic status updates
        startStatusUpdates();
    }

    // ── Step 1 & 2: Rider Assignment ──────────────────────────────────────────

    /**
     * Iterates over all riders, filters to those that are available,
     * measures each one's distance to the restaurant, and picks the nearest.
     *
     * WHY a loop instead of Streams: keeps it accessible for OOP-level Java.
     */
    private void assignClosestRider(List<Rider> riders) {
        Rider  closest     = null;
        double minDistance = Double.MAX_VALUE;

        // Guard: if restaurant has no location, tracking cannot work
        if (restaurant.getLocation() == null) {
            System.out.println("[Tracking] ERROR: Restaurant has no location set.");
            return;
        }

        for (Rider rider : riders) {
            // Only consider riders that are currently available
            if (!rider.getStatus()) continue;

            // Skip riders with no location (stale data from before Location was added)
            if (rider.getLocation() == null) {
                System.out.println("[Tracking] Skipping rider " + rider.getName() + " — no location set.");
                continue;
            }

            double dist = rider.getLocation()
                               .distanceTo(restaurant.getLocation());

            if (dist < minDistance) {
                minDistance = dist;
                closest     = rider;
            }
        }

        if (closest == null) {
            System.out.println("[Tracking] No available riders found.");
            return;
        }

        // Mark the rider as assigned and unavailable for other orders
        closest.assignOrder();          // sets isAssigned=true, isAvailable=false
        this.assignedRider = closest;
        this.distanceRiderToRestaurant = minDistance;

        System.out.println("[Tracking] Rider assigned: " + closest.getName()
                + " | Distance to restaurant: "
                + String.format("%.2f", minDistance) + " units");
    }

    // ── Step 3 & 4: Distance & Time Calculation ───────────────────────────────

    /**
     * Calculates the restaurant → customer distance and combines it with
     * the fixed preparation time to produce the total estimated delivery time.
     *
     * Formula:
     *   Travel time (min) = restaurant-to-customer distance × 1 min/unit
     *   Total ETA (min)   = PREP_TIME_MINUTES + travel time
     */
    private void calculateDistancesAndTime() {
        distanceRestaurantToCustomer = restaurant.getLocation()
                                                 .distanceTo(customer.getLocation());

        int travelMinutes           = (int) Math.ceil(distanceRestaurantToCustomer);
        this.estimatedDeliveryMinutes = PREP_TIME_MINUTES + travelMinutes;

        System.out.println("[Tracking] Restaurant → Customer distance: "
                + String.format("%.2f", distanceRestaurantToCustomer) + " units");
        System.out.println("[Tracking] Estimated delivery time: "
                + estimatedDeliveryMinutes + " minutes "
                + "(10 min prep + " + travelMinutes + " min travel)");
    }

    // ── Step 5: Status Update Timeline ───────────────────────────────────────

    /**
     * Uses java.util.Timer to schedule status changes without blocking the
     * main thread.  Each milestone fires at the appropriate simulated time.
     *
     * Timeline:
     *   t = 0                      → "Confirmed"       (set in constructor)
     *   t = 0 (immediate)          → "Preparing"
     *   t = PREP_TIME_MINUTES      → "Out for Delivery"
     *   t = estimatedDeliveryMinutes → "Delivered"
     */
    private void startStatusUpdates() {
        statusTimer = new Timer(true);   // daemon=true: timer won't block JVM exit

        long prepMs     = minutesToMs(PREP_TIME_MINUTES);
        long deliveryMs = minutesToMs(estimatedDeliveryMinutes);

        // Immediately move to Preparing
        scheduleStatus("Preparing", 0);

        // After prep time → rider picks up and heads out
        scheduleStatus("Out for Delivery", prepMs);

        // After full estimated time → delivered
        scheduleStatus("Delivered", deliveryMs);
    }

    /**
     * Schedules a single status change after `delayMs` milliseconds.
     * Also releases the rider when the order is delivered.
     */
    private void scheduleStatus(String newStatus, long delayMs) {
        statusTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                currentStatus = newStatus;
                order.updateStatus(newStatus);  // keeps Order's own status in sync

                System.out.println("[Tracking " + trackingID + "] Status → " + newStatus);

                // When delivered, free the rider for new orders
                if ("Delivered".equals(newStatus) && assignedRider != null) {
                    assignedRider.setAvailable(true);
                    assignedRider.setAssigned(false);
                    System.out.println("[Tracking] Rider " + assignedRider.getName()
                            + " is now available again.");
                    statusTimer.cancel();
                }
            }
        }, delayMs);
    }

    /** Converts simulated minutes to real milliseconds using SECONDS_PER_MINUTE. */
    private long minutesToMs(int minutes) {
        return (long) minutes * SECONDS_PER_MINUTE * 1000L;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String  getTrackingID()                  { return trackingID; }
    public String  getCurrentStatus()               { return currentStatus; }
    public Rider   getAssignedRider()               { return assignedRider; }
    public int     getEstimatedDeliveryMinutes()    { return estimatedDeliveryMinutes; }
    public double  getDistanceRestaurantToCustomer(){ return distanceRestaurantToCustomer; }
    public double  getDistanceRiderToRestaurant()   { return distanceRiderToRestaurant; }

    /** Human-readable summary of this tracking record. */
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
            estimatedDeliveryMinutes
        );
    }

    /** Returns a random Location with x and y each in the range [0, 100]. */
    private Location randomLocation() {
        java.util.Random rand = new java.util.Random();
        double x = rand.nextDouble() * 100;
        double y = rand.nextDouble() * 100;
        return new Location(x, y);
    }
}