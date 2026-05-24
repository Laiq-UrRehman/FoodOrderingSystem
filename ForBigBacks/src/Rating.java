// Updated: rateFoodItem() throws IllegalArgumentException for null customer, order, or blank foodID
// Updated: updateRatingInFile() catches FileHandler.FileOperationException
// Updated: Star range check now throws IllegalArgumentException instead of printing

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

        if (!order.getStatus().equals("Delivered")) {
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

        if (order.hasRated(foodID)) {
            System.out.println("You already rated " + target.getName() + " for this order.");
            return;
        }

        target.rate(stars);
        order.markRated(foodID);
        updateRatingInFile(foodID, stars);
        System.out.println("Rated " + target.getName() + " → " + stars + " stars!");
    }

    private void updateRatingInFile(String foodID, double stars) {
        try {
            Restaurant[] restaurants = fileHandler.loadArray("restaurants.dat");
            if (restaurants == null)
                return;
            for (Restaurant r : restaurants) {
                for (FoodItem item : r.getMenu().getItems()) {
                    if (item.getFoodID().equals(foodID)) {
                        item.rate(stars);
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