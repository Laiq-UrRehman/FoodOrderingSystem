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
        FileHandler<Restaurant> fileHandler = new FileHandler<>();
        Restaurant[] restaurants = fileHandler.loadArray("restaurants.dat");

        if (restaurants == null) {
            System.out.println("No restaurant data found. Please seed data first.");
            return null;
        }

        for (Restaurant r : restaurants) {
            RestaurantAdmin admin = r.getAdmin();
            if (admin != null
                    && admin.getUsername().equals(username)
                    && admin.getPassword().equals(password)) {
                System.out.println("Admin login successful. Welcome, " + admin.getName()
                        + " (" + r.getName() + ")!");
                return admin;
            }
        }

        System.out.println("Invalid admin username or password.");
        return null;
    }
}