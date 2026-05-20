public class LoginManager {

    // ── Customer Login ─────────────────────────────────────────────────────────

    public Customer loginCustomer(String username, String password) {
        FileHandler<Customer> fileHandler = new FileHandler<>();
        Customer[] customers = fileHandler.loadArray("customers.dat");

        if (customers == null) {
            System.out.println("No customer data found. Please seed data first.");
            return null;
        }

        for (Customer c : customers) {
            if (c.getUsername().equals(username) && c.getPassword().equals(password)) {
                System.out.println("Customer login successful. Welcome, " + c.getName() + "!");
                return c;
            }
        }

        System.out.println("Invalid username or password.");
        return null;
    }

    // ── Restaurant Admin Login ─────────────────────────────────────────────────

    public RestaurantAdmin loginAdmin(String username, String password) {

        FileHandler<String[][]> credFileHandler = new FileHandler<>();
        String[][] credentials = credFileHandler.loadObject("admin_credentials.dat");

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
        Restaurant[] restaurants = restaurantFileHandler.loadArray("restaurants.dat");

        if (restaurants == null) {
            System.out.println("No restaurant data found.");
            return null;
        }

        for (Restaurant r : restaurants) {
            if (r.getRestaurantID().equals(matchedRestaurantID)) {
                System.out.println("Admin login successful. Welcome, " + r.getAdmin().getName() + " (" + r.getName() + ")!");
                return r.getAdmin();
            }
        }

        System.out.println("Restaurant not found for admin.");
        return null;
    }
}