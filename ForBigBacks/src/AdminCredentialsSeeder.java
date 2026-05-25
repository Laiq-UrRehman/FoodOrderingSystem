public class AdminCredentialsSeeder {

    public static void main(String[] args) {
        seedAdminCredentials();
    }

    public static void seedAdminCredentials() {

        FileHandler<Restaurant> restaurantFileHandler = new FileHandler<>();
        Restaurant[] restaurants;

        try {
            restaurants = restaurantFileHandler.loadArray("restaurants.dat");
        } catch (FileHandler.FileOperationException e) {
            System.out.println("[AdminCredentialsSeeder] Failed to load restaurants: " + e.getMessage());
            return;
        }

        if (restaurants == null) {
            System.out.println("Failed to load restaurants.dat. Run DataSeeder first.");
            return;
        }

        String[][] credentials = new String[restaurants.length][3];
        for (int i = 0; i < restaurants.length; i++) {
            RestaurantAdmin admin = restaurants[i].getAdmin();
            credentials[i][0] = admin.getUsername();
            credentials[i][1] = admin.getPassword();
            credentials[i][2] = restaurants[i].getRestaurantID();
        }

        FileHandler<String[][]> credFileHandler = new FileHandler<>();

        try {
            credFileHandler.saveObject(credentials, "admin_credentials.dat");
            System.out.println("admin_credentials.dat seeded with " + credentials.length + " entries.");
        } catch (FileHandler.FileOperationException e) {
            System.out.println("[AdminCredentialsSeeder] Failed to save credentials: " + e.getMessage());
            return;
        }

        String[][] loaded;

        try {
            loaded = credFileHandler.loadObject("admin_credentials.dat");
        } catch (FileHandler.FileOperationException e) {
            System.out.println("[AdminCredentialsSeeder] Failed to load credentials: " + e.getMessage());
            return;
        }

        if (loaded == null) {
            System.out.println("Failed to load admin_credentials.dat");
            return;
        }

        System.out.println("\n===== Loaded Admin Credentials =====");
        for (String[] cred : loaded) {
            System.out.println("Username: " + cred[0] + " | Password: " + cred[1] + " | RestaurantID: " + cred[2]);
        }
    }
}