import java.util.ArrayList;
import java.util.List;

public class DataSeeder {

    public static void main(String[] args) {
        seedRestaurants();
    }

    public static void seedRestaurants() {

        FileHandler<Restaurant> fileHandler = new FileHandler<>();

        // ─── Restaurant 1: Burger Palace ──────────────────────────────
        Menu menu1 = new Menu("M001", new ArrayList<>());
        menu1.addItem(new FoodItem("F001", "Classic Burger",   350.0, "Burgers", 1));
        menu1.addItem(new FoodItem("F002", "Cheese Burger",    420.0, "Burgers", 1));
        menu1.addItem(new FoodItem("F003", "Crispy Fries",     180.0, "Sides",   1));
        menu1.addItem(new FoodItem("F004", "Chocolate Shake",  250.0, "Drinks",  1));
        menu1.addItem(new FoodItem("F005", "Onion Rings",      200.0, "Sides",   1));

        Restaurant burgerPalace = new Restaurant("R001", "Burger Palace", "Main Boulevard, Gulberg, Lahore", menu1, new Location(20.0, 35.0));

        RestaurantAdmin admin1 = new RestaurantAdmin(
                "A001", "Ahmed Raza", "Gulberg, Lahore", "03001112222",
                "ahmed_admin", "ahmed123", burgerPalace
        );
        burgerPalace.getAdmin();

        // ─── Restaurant 2: Pizza Hub ──────────────────────────────────
        Menu menu2 = new Menu("M002", new ArrayList<>());
        menu2.addItem(new FoodItem("F006", "Margherita Pizza",  650.0, "Pizza",  1));
        menu2.addItem(new FoodItem("F007", "BBQ Chicken Pizza", 850.0, "Pizza",  1));
        menu2.addItem(new FoodItem("F008", "Garlic Bread",      220.0, "Sides",  1));
        menu2.addItem(new FoodItem("F009", "Caesar Salad",      300.0, "Salads", 1));
        menu2.addItem(new FoodItem("F010", "Iced Tea",          150.0, "Drinks", 1));

        Restaurant pizzaHub = new Restaurant("R002", "Pizza Hub", "DHA Phase 5, Lahore", menu2, new Location(80.0, 15.0));

        RestaurantAdmin admin2 = new RestaurantAdmin(
                "A002", "Bilal Sheikh", "DHA Phase 5, Lahore", "03112223333",
                "bilal_admin", "bilal456", pizzaHub
        );

        // ─── Restaurant 3: Desi Dhaba ─────────────────────────────────
        Menu menu3 = new Menu("M003", new ArrayList<>());
        menu3.addItem(new FoodItem("F011", "Chicken Karahi",  900.0, "Main Course", 1));
        menu3.addItem(new FoodItem("F012", "Beef Nihari",     750.0, "Main Course", 1));
        menu3.addItem(new FoodItem("F013", "Plain Naan",       60.0, "Bread",       1));
        menu3.addItem(new FoodItem("F014", "Raita",            80.0, "Sides",       1));
        menu3.addItem(new FoodItem("F015", "Mango Lassi",     180.0, "Drinks",      1));

        Restaurant desiDhaba = new Restaurant("R003", "Desi Dhaba", "Johar Town, Lahore", menu3, new Location(55.0, 70.0));

        RestaurantAdmin admin3 = new RestaurantAdmin(
                "A003", "Nadia Malik", "Johar Town, Lahore", "03223334444",
                "nadia_admin", "nadia789", desiDhaba
        );

        // ─── Save Array to restaurants.dat ───────────────────────────
        Restaurant[] restaurants = { burgerPalace, pizzaHub, desiDhaba };
        fileHandler.saveArray(restaurants, "restaurants.dat");

        System.out.println("restaurants.dat seeded with " + restaurants.length + " restaurants.");

        // ─── Verify: Reload and print ─────────────────────────────────
        Restaurant[] loaded = fileHandler.loadArray("restaurants.dat");

        if (loaded == null) {
            System.out.println("Failed to load restaurants.dat");
            return;
        }

        System.out.println("\n===== Loaded Restaurants =====");
        for (Restaurant r : loaded) {
            System.out.println("\nID      : " + r.getRestaurantID());
            System.out.println("Name    : " + r.getName());
            System.out.println("Address : " + r.getAddress());
            System.out.println("Menu Items:");
            for (FoodItem item : r.getMenu().getItems()) {
                System.out.println("  - [" + item.getFoodID() + "] "
                        + item.getName() + " | " + item.getCategory()
                        + " | Rs." + item.getPrice());
            }
        }
    }
}