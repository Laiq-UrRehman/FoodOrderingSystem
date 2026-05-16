import java.util.ArrayList;
import java.util.List;

public class CustomerSeeder {

    public static void main(String[] args) {
        seedCustomers();
    }

    public static void seedCustomers() {

        FileHandler<Customer> fileHandler = new FileHandler<>();

        // ─── Customer 1: Ali Hassan ───────────────────────────────────
        Customer ali = new Customer("C001", "Ali Hassan", "Gulberg, Lahore", "03001234567", "ali_hassan", "ali123", new Location(0, 0));

        List<FoodItem> aliItems1 = new ArrayList<>();
        aliItems1.add(new FoodItem("F001", "Classic Burger", 350.0, "Burgers", 1));
        aliItems1.add(new FoodItem("F003", "Crispy Fries",   180.0, "Sides",   1));
        Order aliOrder1 = new Order("ORD001", "Delivered", aliItems1, 530.0);
        ali.placeOrder(aliOrder1);

        List<FoodItem> aliItems2 = new ArrayList<>();
        aliItems2.add(new FoodItem("F007", "BBQ Chicken Pizza", 850.0, "Pizza", 1));
        aliItems2.add(new FoodItem("F010", "Iced Tea",          150.0, "Drinks", 1));
        Order aliOrder2 = new Order("ORD002", "Delivered", aliItems2, 1000.0);
        ali.placeOrder(aliOrder2);

        ali.getLoyaltyPoints().earnPoints(530.0);
        ali.getLoyaltyPoints().earnPoints(1000.0);

        // ─── Customer 2: Sara Khan ────────────────────────────────────
        Customer sara = new Customer("C002", "Sara Khan", "DHA Phase 3, Lahore", "03111234567", "sara_khan", "sara456", new Location(0, 0));

        List<FoodItem> saraItems1 = new ArrayList<>();
        saraItems1.add(new FoodItem("F011", "Chicken Karahi", 900.0, "Main Course", 1));
        saraItems1.add(new FoodItem("F013", "Plain Naan",      60.0, "Bread",       2));
        saraItems1.add(new FoodItem("F015", "Mango Lassi",    180.0, "Drinks",      1));
        Order saraOrder1 = new Order("ORD003", "Delivered", saraItems1, 1200.0);
        sara.placeOrder(saraOrder1);

        sara.getLoyaltyPoints().earnPoints(1200.0);

        // ─── Customer 3: Usman Raza ───────────────────────────────────
        Customer usman = new Customer("C003", "Usman Raza", "Johar Town, Lahore", "03211234567", "usman_raza", "usman789", new Location(0, 0));

        List<FoodItem> usmanItems1 = new ArrayList<>();
        usmanItems1.add(new FoodItem("F006", "Margherita Pizza", 650.0, "Pizza",  1));
        usmanItems1.add(new FoodItem("F008", "Garlic Bread",     220.0, "Sides",  1));
        Order usmanOrder1 = new Order("ORD004", "Delivered", usmanItems1, 870.0);
        usman.placeOrder(usmanOrder1);

        List<FoodItem> usmanItems2 = new ArrayList<>();
        usmanItems2.add(new FoodItem("F002", "Cheese Burger", 420.0, "Burgers", 2));
        usmanItems2.add(new FoodItem("F004", "Chocolate Shake", 250.0, "Drinks", 1));
        Order usmanOrder2 = new Order("ORD005", "Pending", usmanItems2, 1090.0);
        usman.placeOrder(usmanOrder2);

        usman.getLoyaltyPoints().earnPoints(870.0);
        usman.getLoyaltyPoints().earnPoints(1090.0);

        // ─── Customer 4: Fatima Malik ─────────────────────────────────
        Customer fatima = new Customer("C004", "Fatima Malik", "Model Town, Lahore", "03321234567", "fatima_malik", "fatima321", new Location(0, 0));

        List<FoodItem> fatimaItems1 = new ArrayList<>();
        fatimaItems1.add(new FoodItem("F012", "Beef Nihari",  750.0, "Main Course", 1));
        fatimaItems1.add(new FoodItem("F014", "Raita",         80.0, "Sides",       1));
        fatimaItems1.add(new FoodItem("F013", "Plain Naan",    60.0, "Bread",       3));
        Order fatimaOrder1 = new Order("ORD006", "Delivered", fatimaItems1, 1010.0);
        fatima.placeOrder(fatimaOrder1);

        fatima.getLoyaltyPoints().earnPoints(1010.0);

        // ─── Save Array to customers.dat ──────────────────────────────
        Customer[] customers = { ali, sara, usman, fatima };
        fileHandler.saveArray(customers, "customers.dat");

        System.out.println("customers.dat seeded with " + customers.length + " customers.");

        // ─── Verify: Reload and print ─────────────────────────────────
        Customer[] loaded = fileHandler.loadArray("customers.dat");

        if (loaded == null) {
            System.out.println("Failed to load customers.dat");
            return;
        }

        System.out.println("\n===== Loaded Customers =====");
        for (Customer c : loaded) {
            System.out.println("\nID       : " + c.getPersonID());
            System.out.println("Name     : " + c.getName());
            System.out.println("Address  : " + c.getAddress());
            System.out.println("Phone    : " + c.getPhoneNumber());
            System.out.println("Username : " + c.getUsername());
            System.out.println("Points   : " + c.viewLoyaltyPoints());
            System.out.println("Orders   : " + c.viewOrderHistory().size());
            for (Order o : c.viewOrderHistory()) {
                System.out.println("  - [" + o.getOrderID() + "] "
                        + o.getStatus() + " | Rs." + o.getTotalAmount());
            }
        }
    }
}