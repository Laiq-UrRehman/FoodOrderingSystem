// Updated: setAdmin() called for all three restaurants (was missing)
// Updated: Restaurant constructor now includes cuisineType
// Updated: Seeded initial ratings for restaurants and food items
// Updated: verify print now shows admin name and cuisine type

import java.util.ArrayList;

public class DataSeeder {

        public static void main(String[] args) {
                seedRestaurants();
        }

        public static void seedRestaurants() {

                FileHandler<Restaurant> fileHandler = new FileHandler<>();

                // ─── Restaurant 1: Burger Palace ──────────────────────────────
                Menu menu1 = new Menu("M001", new ArrayList<>());
                FoodItem f001 = new FoodItem("F001", "Classic Burger", 350.0, "Burgers", 1);
                FoodItem f002 = new FoodItem("F002", "Cheese Burger", 420.0, "Burgers", 1);
                FoodItem f003 = new FoodItem("F003", "Crispy Fries", 180.0, "Sides", 1);
                FoodItem f004 = new FoodItem("F004", "Chocolate Shake", 250.0, "Drinks", 1);
                FoodItem f005 = new FoodItem("F005", "Onion Rings", 200.0, "Sides", 1);

                f001.rate(4.5);
                f001.rate(4.0);
                f001.rate(5.0);
                f002.rate(4.2);
                f002.rate(3.8);
                f002.rate(4.5);
                f003.rate(3.5);
                f003.rate(4.0);
                f003.rate(3.0);
                f004.rate(4.8);
                f004.rate(4.5);
                f004.rate(5.0);
                f005.rate(3.8);
                f005.rate(4.0);
                f005.rate(3.5);

                menu1.addItem(f001);
                menu1.addItem(f002);
                menu1.addItem(f003);
                menu1.addItem(f004);
                menu1.addItem(f005);

                Restaurant burgerPalace = new Restaurant("R001", "Burger Palace", "Main Boulevard, Gulberg, Lahore",
                                "American",
                                menu1, new Location(20.0, 35.0));
                burgerPalace.rate(4.5);
                burgerPalace.rate(4.0);
                burgerPalace.rate(4.8);

                RestaurantAdmin admin1 = new RestaurantAdmin(
                                "A001", "Ahmed Raza", "Gulberg, Lahore", "03001112222",
                                "ahmed_admin", "ahmed123", burgerPalace);
                burgerPalace.setAdmin(admin1);

                // ─── Restaurant 2: Pizza Hub ──────────────────────────────────
                Menu menu2 = new Menu("M002", new ArrayList<>());
                FoodItem f006 = new FoodItem("F006", "Margherita Pizza", 650.0, "Pizza", 1);
                FoodItem f007 = new FoodItem("F007", "BBQ Chicken Pizza", 850.0, "Pizza", 1);
                FoodItem f008 = new FoodItem("F008", "Garlic Bread", 220.0, "Sides", 1);
                FoodItem f009 = new FoodItem("F009", "Caesar Salad", 300.0, "Salads", 1);
                FoodItem f010 = new FoodItem("F010", "Iced Tea", 150.0, "Drinks", 1);

                f006.rate(4.0);
                f006.rate(4.2);
                f006.rate(3.8);
                f007.rate(4.9);
                f007.rate(5.0);
                f007.rate(4.7);
                f008.rate(3.5);
                f008.rate(3.8);
                f008.rate(4.0);
                f009.rate(4.1);
                f009.rate(3.9);
                f009.rate(4.3);
                f010.rate(3.2);
                f010.rate(3.5);
                f010.rate(3.0);

                menu2.addItem(f006);
                menu2.addItem(f007);
                menu2.addItem(f008);
                menu2.addItem(f009);
                menu2.addItem(f010);

                Restaurant pizzaHub = new Restaurant("R002", "Pizza Hub", "DHA Phase 5, Lahore", "Italian", menu2,
                                new Location(80.0, 15.0));
                pizzaHub.rate(4.2);
                pizzaHub.rate(4.5);
                pizzaHub.rate(4.0);

                RestaurantAdmin admin2 = new RestaurantAdmin(
                                "A002", "Bilal Sheikh", "DHA Phase 5, Lahore", "03112223333",
                                "bilal_admin", "bilal456", pizzaHub);
                pizzaHub.setAdmin(admin2);

                // ─── Restaurant 3: Desi Dhaba ─────────────────────────────────
                Menu menu3 = new Menu("M003", new ArrayList<>());
                FoodItem f011 = new FoodItem("F011", "Chicken Karahi", 900.0, "Main Course", 1);
                FoodItem f012 = new FoodItem("F012", "Beef Nihari", 750.0, "Main Course", 1);
                FoodItem f013 = new FoodItem("F013", "Plain Naan", 60.0, "Bread", 1);
                FoodItem f014 = new FoodItem("F014", "Raita", 80.0, "Sides", 1);
                FoodItem f015 = new FoodItem("F015", "Mango Lassi", 180.0, "Drinks", 1);

                f011.rate(5.0);
                f011.rate(4.8);
                f011.rate(4.9);
                f012.rate(4.7);
                f012.rate(4.5);
                f012.rate(4.8);
                f013.rate(4.0);
                f013.rate(3.8);
                f013.rate(4.2);
                f014.rate(3.5);
                f014.rate(3.8);
                f014.rate(3.2);
                f015.rate(4.6);
                f015.rate(4.4);
                f015.rate(4.8);

                menu3.addItem(f011);
                menu3.addItem(f012);
                menu3.addItem(f013);
                menu3.addItem(f014);
                menu3.addItem(f015);

                Restaurant desiDhaba = new Restaurant("R003", "Desi Dhaba", "Johar Town, Lahore", "Pakistani", menu3,
                                new Location(55.0, 70.0));
                desiDhaba.rate(4.8);
                desiDhaba.rate(4.9);
                desiDhaba.rate(5.0);

                RestaurantAdmin admin3 = new RestaurantAdmin(
                                "A003", "Nadia Malik", "Johar Town, Lahore", "03223334444",
                                "nadia_admin", "nadia789", desiDhaba);
                desiDhaba.setAdmin(admin3);

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
                        System.out.println("Cuisine : " + r.getCuisineType());
                        System.out.println("Address : " + r.getAddress());
                        System.out.println("Admin   : " + (r.getAdmin() != null ? r.getAdmin().getName() : "None"));
                        System.out.println(
                                        "Rating  : " + String.format("%.1f", r.getRating()) + " (" + r.getTotalRatings()
                                                        + " ratings)");
                        System.out.println("Menu Items:");
                        for (FoodItem item : r.getMenu().getItems()) {
                                System.out.println("  - [" + item.getFoodID() + "] "
                                                + item.getName() + " | " + item.getCategory()
                                                + " | Rs." + item.getPrice()
                                                + " | Rating: " + String.format("%.1f", item.getRating()));
                        }
                }
        }
}