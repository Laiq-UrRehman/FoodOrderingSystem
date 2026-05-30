// Updated: loadArray() and loadObject() FileOperationException now checked — "file not found" treated as empty data instead of hard failure
// Updated: loginCustomer() returns null with clear message when customers.dat is missing rather than crashing
// Updated: loginAdmin() returns null with clear message when admin_credentials.dat or restaurants.dat is missing

public class LoginManager {

    public Customer loginCustomer(String username, String password) {
        FileHandler<Customer> fileHandler = new FileHandler<>();
        Customer[] customers = null;

        try {
            customers = fileHandler.loadArray("customers.dat");
        } catch (FileHandler.FileOperationException e) {
            if (!e.getMessage().toLowerCase().contains("file not found")
                    && !e.getMessage().toLowerCase().contains("not found")) {
                System.out.println("Could not load customer data: " + e.getMessage());
            } else {
                System.out.println("No customer data found yet. Please sign up first.");
            }
            return null;
        }

        if (customers == null) {
            System.out.println("No customer data found. Please sign up first.");
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
        FileHandler<String[][]> credFileHandler = new FileHandler<>();
        String[][] credentials = null;

        try {
            credentials = credFileHandler.loadObject("admin_credentials.dat");
        } catch (FileHandler.FileOperationException e) {
            if (!e.getMessage().toLowerCase().contains("file not found")
                    && !e.getMessage().toLowerCase().contains("not found")) {
                System.out.println("Could not load admin credentials: " + e.getMessage());
            } else {
                System.out.println("No admin credential data found. Please seed data first.");
            }
            return null;
        }

        if (credentials == null) {
            System.out.println("No admin credential data found. Please seed data first.");
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
        Restaurant[] restaurants = null;

        try {
            restaurants = restaurantFileHandler.loadArray("restaurants.dat");
        } catch (FileHandler.FileOperationException e) {
            if (!e.getMessage().toLowerCase().contains("file not found")
                    && !e.getMessage().toLowerCase().contains("not found")) {
                System.out.println("Could not load restaurant data: " + e.getMessage());
            } else {
                System.out.println("No restaurant data found. Please seed data first.");
            }
            return null;
        }

        if (restaurants == null) {
            System.out.println("No restaurant data found.");
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