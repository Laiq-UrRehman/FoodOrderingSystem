// Updated: rateFoodItem() now allows re-rating — previously rated items can be updated
// Updated: markRated(foodID, stars) used instead of markRated(foodID) so the value is stored in Order
// Updated: updateRatingInFile() corrects the running average when a previous rating is replaced
// Updated: updateRatingInFile() now catches FileHandler.FileOperationException

public class Rating {
    private FileHandler<Restaurant> fileHandler = new FileHandler<>();

    public void rateFoodItem(Customer customer, Order order, String foodID, double stars) {
        if (customer == null)
            throw new IllegalArgumentException("Customer cannot be null");
        if (order == null)
            throw new IllegalArgumentException("Order cannot be null");
        if (foodID == null || foodID.isBlank())
            throw new IllegalArgumentException("Food ID cannot be null or empty");
        if (stars < 1.0 || stars > 5.0)
            throw new IllegalArgumentException("Rating must be between 1.0 and 5.0, got: " + stars);

        // Sync the live status first — after a restart, order.getStatus() may be stale
        // (e.g. still "Preparing") even though delivery is long done. getCurrentStatus()
        // recomputes from startTime and updates order.status in place.
        String effectiveStatus = (order.getTracking() != null)
                ? order.getTracking().getCurrentStatus()
                : order.getStatus();

        if (!effectiveStatus.equals("Delivered")) {
            System.out.println("You can only rate after the order is delivered.");
            return;
        }

        FoodItem target = null;
        for (FoodItem item : order.getItems()) {
            if (item.getFoodID().equals(foodID)) {
                target = item;
                break;
            }
        }

        if (target == null) {
            System.out.println("This item was not in this order.");
            return;
        }

        double previousStars = order.getRatingValue(foodID);
        boolean wasRated = order.hasRated(foodID);

        order.markRated(foodID, stars);
        updateRatingInFile(foodID, stars, wasRated ? previousStars : -1);
        System.out.println("Rated " + target.getName() + " → " + stars + " stars!");
    }

    private void updateRatingInFile(String foodID, double newStars, double previousStars) {
        try {
            Restaurant[] restaurants = fileHandler.loadArray("restaurants.dat");
            if (restaurants == null)
                return;
            for (Restaurant r : restaurants) {
                for (FoodItem item : r.getMenu().getItems()) {
                    if (item.getFoodID().equals(foodID)) {
                        if (previousStars >= 1.0 && item.getTotalRatings() > 0) {
                            // Remove old rating from running average, then add new one
                            double correctedSum = (item.getRating() * item.getTotalRatings()) - previousStars;
                            double correctedAvg = correctedSum / (item.getTotalRatings() - 1);
                            item.overwriteRating(correctedAvg, item.getTotalRatings() - 1);
                        }
                        item.rate(newStars);
                        break;
                    }
                }
            }
            fileHandler.saveArray(restaurants, "restaurants.dat");
        } catch (FileHandler.FileOperationException e) {
            System.out.println("Warning: Could not persist rating to file: " + e.getMessage());
        }
    }
}