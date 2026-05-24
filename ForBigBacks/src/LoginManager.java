// Updated: FileHandler calls now catch FileHandler.FileOperationException instead of relying on null returns
// Updated: Meaningful error messages distinguish file-not-found from class-mismatch failures

public class LoginManager {

    public Customer loginCustomer(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            System.out.println("Username and password cannot be empty.");
            return null;
        }

        FileHandler<Customer> fileHandler = new FileHandler<>();
        Customer[] customers;
        try {
            customers = fileHandler.loadArray("customers.dat");
        } catch (FileHandler.FileOperationException e) {
            System.out.println("Could not load customer data: " + e.getMessage());
            return null;
        }

        for (Customer c : customers) {
            if (c.getUsername().equals(username) && c.verifyPassword(password)) {
                System.out.println("Customer login successful. Welcome, " + c.getName() + "!");
                return c;
            }
        }

        System.out.println("Invalid username or password.");
        return null;
    }

    public RestaurantAdmin loginAdmin(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            System.out.println("Username and password cannot be empty.");
            return null;
        }

        FileHandler<String[][]> credFileHandler = new FileHandler<>();
        String[][] credentials;
        try {
            credentials = credFileHandler.loadObject("admin_credentials.dat");
        } catch (FileHandler.FileOperationException e) {
            System.out.println("Could not load admin credentials: " + e.getMessage());
            return null;
        }

        String matchedRestaurantID = null;
        for (String[] cred : credentials) {
            if (cred[0].equals(username) && cred[1].equals(password)) {
                matchedRestaurantID = cred[2];
                break;
            }
        }

        if (matchedRestaurantID == null) {
            System.out.println("Invalid admin username or password.");
            return null;
        }

        FileHandler<Restaurant> restaurantFileHandler = new FileHandler<>();
        Restaurant[] restaurants;
        try {
            restaurants = restaurantFileHandler.loadArray("restaurants.dat");
        } catch (FileHandler.FileOperationException e) {
            System.out.println("Could not load restaurant data: " + e.getMessage());
            return null;
        }

        for (Restaurant r : restaurants) {
            if (r.getRestaurantID().equals(matchedRestaurantID)) {
                System.out.println("Admin login successful. Welcome, "
                        + r.getAdmin().getName() + " (" + r.getName() + ")!");
                return r.getAdmin();
            }
        }

        System.out.println("Restaurant not found for admin.");
        return null;
    }
}