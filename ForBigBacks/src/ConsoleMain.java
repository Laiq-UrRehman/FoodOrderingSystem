import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class ConsoleMain {

    static Scanner scanner = new Scanner(System.in);
    static LoginManager loginManager = new LoginManager();

    public static void main(String[] args) {

        // ── Seed data if .dat files don't exist ───────────────────────────────
        seedIfNeeded();

        // ── Main Menu ─────────────────────────────────────────────────────────
        while (true) {
            System.out.println("\n========================================");
            System.out.println("     WELCOME TO FOOD ORDERING SYSTEM    ");
            System.out.println("========================================");
            System.out.println("  1. Login as Customer");
            System.out.println("  2. Sign Up as Customer");
            System.out.println("  3. Login as Restaurant Admin");
            System.out.println("  4. Exit");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> customerLoginFlow();
                case "2" -> customerSignupFlow();
                case "3" -> adminLoginFlow();
                case "4" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SEEDING
    // ─────────────────────────────────────────────────────────────────────────

    static void seedIfNeeded() {
        java.io.File restaurantFile = new java.io.File("restaurants.dat");
        java.io.File customerFile = new java.io.File("customers.dat");

        if (!restaurantFile.exists()) {
            System.out.println("Seeding restaurant data...");
            DataSeeder.seedRestaurants();
        }
        if (!customerFile.exists()) {
            System.out.println("Seeding customer data...");
            CustomerSeeder.seedCustomers();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CUSTOMER FLOW
    // ─────────────────────────────────────────────────────────────────────────

    static void customerLoginFlow() {
        System.out.println("\n--- Customer Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        Customer customer = loginManager.loginCustomer(username, password);
        if (customer == null)
            return;

        customerMenu(customer);
    }

    static void customerSignupFlow() {
        System.out.println("\n--- Customer Sign Up ---");
        System.out.print("Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Address: ");
        String address = scanner.nextLine().trim();
        System.out.print("Phone Number: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        // Load existing customers to check username and generate next ID
        FileHandler<Customer> fh = new FileHandler<>();
        Customer[] existing = fh.loadArray("customers.dat");
        int count = (existing == null) ? 0 : existing.length;

        // Check username not already taken
        if (existing != null) {
            for (Customer c : existing) {
                if (c.getUsername().equals(username)) {
                    System.out.println("Username already taken. Please choose another.");
                    return;
                }
            }
        }

        // Create new customer with auto-generated ID
        String newID = String.format("C%03d", count + 1);
        Customer newCustomer = new Customer(newID, name, address, phone, username, password);

        // Build updated array and save back to customers.dat
        Customer[] updated = new Customer[count + 1];
        if (existing != null) {
            System.arraycopy(existing, 0, updated, 0, count);
        }
        updated[count] = newCustomer;
        fh.saveArray(updated, "customers.dat");

        System.out.println("Sign up successful! You can now log in as: " + username);
    }

    static void saveCustomer(Customer customer) {
        FileHandler<Customer> fh = new FileHandler<>();
        Customer[] existing = fh.loadArray("customers.dat");

        if (existing == null) {
            System.out.println("Warning: could not load customers.dat to save.");
            return;
        }

        // Find and replace the matching customer by username
        for (int i = 0; i < existing.length; i++) {
            if (existing[i].getUsername().equals(customer.getUsername())) {
                existing[i] = customer;
                break;
            }
        }

        fh.saveArray(existing, "customers.dat");
        System.out.println("Progress saved.");
    }

    static void customerMenu(Customer customer) {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("  CUSTOMER MENU - " + customer.getName());
            System.out.println("========================================");
            System.out.println("  1.  Browse Restaurants & Add to Cart");
            System.out.println("  2.  View Cart");
            System.out.println("  3.  Checkout (Normal Order)");
            System.out.println("  4.  Checkout (Scheduled Order)");
            System.out.println("  5.  View Order History");
            System.out.println("  6.  Cancel an Order");
            System.out.println("  7.  View Loyalty Points");
            System.out.println("  8.  View Available Loyalty Offers");
            System.out.println("  9.  View Scheduled Orders");
            System.out.println("  10. Track Order");
            System.out.println("  0.  Logout");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> browseAndAddToCart(customer);
                case "2" -> customer.getCart().viewCart();
                case "3" -> checkoutNormal(customer);
                case "4" -> checkoutScheduled(customer);
                case "5" -> viewOrderHistory(customer);
                case "6" -> cancelOrder(customer);
                case "7" -> customer.getLoyaltyPoints().printBalance();
                case "8" -> customer.getLoyaltyPoints().printAvailableOffers(customer.getCart().getTotal());
                case "9" -> viewScheduledOrders(customer);
                case "10" -> trackOrder();
                case "0" -> {
                    saveCustomer(customer);
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void browseAndAddToCart(Customer customer) {
        FileHandler<Restaurant> fh = new FileHandler<>();
        Restaurant[] restaurants = fh.loadArray("restaurants.dat");

        if (restaurants == null || restaurants.length == 0) {
            System.out.println("No restaurants available.");
            return;
        }

        System.out.println("\n--- Available Restaurants ---");
        for (int i = 0; i < restaurants.length; i++) {
            System.out.println("  " + (i + 1) + ". " + restaurants[i].getName()
                    + " | " + restaurants[i].getAddress());
        }
        System.out.print("Select restaurant (number): ");
        int rChoice;
        try {
            rChoice = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        if (rChoice < 0 || rChoice >= restaurants.length) {
            System.out.println("Invalid selection.");
            return;
        }

        Restaurant selected = restaurants[rChoice];
        System.out.println("\n--- Menu: " + selected.getName() + " ---");
        List<FoodItem> menuItems = selected.getMenu().getItems();
        for (int i = 0; i < menuItems.size(); i++) {
            FoodItem item = menuItems.get(i);
            System.out.println("  " + (i + 1) + ". [" + item.getFoodID() + "] "
                    + item.getName() + " | " + item.getCategory() + " | Rs." + item.getPrice());
        }

        System.out.print("Select item to add (number): ");
        int iChoice;
        try {
            iChoice = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        if (iChoice < 0 || iChoice >= menuItems.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        System.out.print("Enter quantity: ");
        int qty;
        try {
            qty = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity.");
            return;
        }

        FoodItem chosen = menuItems.get(iChoice);
        FoodItem toAdd = new FoodItem(chosen.getFoodID(), chosen.getName(),
                chosen.getPrice(), chosen.getCategory(), qty);
        customer.getCart().addItem(toAdd);
        System.out.println(qty + "x " + chosen.getName() + " added to cart. Cart total: Rs."
                + customer.getCart().getTotal());
    }

    static void checkoutNormal(Customer customer) {
        Cart cart = customer.getCart();
        if (cart.getItems().isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        cart.viewCart();

        // Loyalty offer
        RedeemCode redeemCode = offerSelectionFlow(customer, cart);

        // Payment method
        System.out.println("\nSelect Payment Method:");
        System.out.println("  1. Cash");
        System.out.println("  2. Card");
        System.out.print("Choice: ");
        String payChoice = scanner.nextLine().trim();

        Order order;
        if (redeemCode != null) {
            order = cart.checkOut(customer, redeemCode);
        } else {
            order = cart.checkOut(customer);
        }

        if (payChoice.equals("2")) {
            System.out.print("Card Number: ");
            String cardNum = scanner.nextLine().trim();
            System.out.print("Card Holder Name: ");
            String holderName = scanner.nextLine().trim();
            System.out.print("Expiry Date (MM/YY): ");
            String expiry = scanner.nextLine().trim();

            CardPayment card = new CardPayment(order.getOrderID(), order.getTotalAmount());
            card.setCardNumber(cardNum);
            card.setCardHolderName(holderName);
            card.setExpiryDate(expiry);
            card.processPayment();
            System.out.println("Card Payment Status: " + card.getStatus());
        } else {
            CashPayment cash = new CashPayment(order.getOrderID(), order.getTotalAmount());
            cash.processPayment();
            System.out.println("Cash Payment Status: " + cash.getStatus());
        }

        customer.placeOrder(order);
        System.out.println("Order placed successfully! Total paid: Rs." + order.getTotalAmount());

        // Clear cart
        cart.clearCart();
    }

    static void checkoutScheduled(Customer customer) {
        Cart cart = customer.getCart();
        if (cart.getItems().isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        cart.viewCart();

        System.out.print("Enter scheduled date-time (yyyy-MM-ddTHH:mm) e.g. 2026-05-16T14:30 : ");
        LocalDateTime scheduledTime;
        try {
            scheduledTime = LocalDateTime.parse(scanner.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Invalid date-time format.");
            return;
        }

        RedeemCode redeemCode = offerSelectionFlow(customer, cart);

        ScheduledOrder order;
        if (redeemCode != null) {
            order = cart.checkOutScheduled(customer, redeemCode, scheduledTime);
        } else {
            order = cart.checkOutScheduled(customer, scheduledTime);
        }

        if (order == null)
            return; // invalid schedule time

        order.confirm();
        customer.placeOrder(order);
        System.out.println("Scheduled order placed! Total: Rs." + order.getTotalAmount());

        // Clear cart
        cart.clearCart();
    }

    static RedeemCode offerSelectionFlow(Customer customer, Cart cart) {
        List<LoyaltyOffer> offers = cart.showLoyaltyOffers(customer);
        if (offers.isEmpty())
            return null;

        System.out.print("Apply a loyalty offer? (y/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("y"))
            return null;

        System.out.print("Enter offer code (e.g. LOYAL-A): ");
        String code = scanner.nextLine().trim();

        LoyaltyOfferManager manager = customer.getLoyaltyPoints().getOfferManager();
        LoyaltyOffer offer = manager.findByCode(code);
        if (offer == null) {
            System.out.println("Offer not found.");
            return null;
        }

        return cart.selectOffer(customer, offer);
    }

    static void viewOrderHistory(Customer customer) {
        List<Order> history = customer.viewOrderHistory();
        if (history.isEmpty()) {
            System.out.println("No orders yet.");
            return;
        }
        System.out.println("\n--- Order History ---");
        for (Order o : history) {
            System.out.println("  [" + o.getOrderID() + "] Status: " + o.getStatus()
                    + " | Rs." + o.getTotalAmount());
            for (FoodItem item : o.getItems()) {
                System.out.println("    - " + item.getName() + " x" + item.getQuantity());
            }
        }
    }

    static void cancelOrder(Customer customer) {
        viewOrderHistory(customer);
        System.out.print("Enter Order ID to cancel: ");
        String orderID = scanner.nextLine().trim();
        customer.cancelOrder(orderID);
    }

    static void viewScheduledOrders(Customer customer) {
        List<ScheduledOrder> scheduled = customer.viewScheduledOrders();
        if (scheduled.isEmpty()) {
            System.out.println("No scheduled orders.");
            return;
        }
        System.out.println("\n--- Scheduled Orders ---");
        for (ScheduledOrder so : scheduled) {
            System.out.println("  " + so);
        }
    }

    static void trackOrder() {
        System.out.print("Enter Order ID to track: ");
        String orderID = scanner.nextLine().trim();
        // Simulated tracking
        Tracking tracking = new Tracking(orderID, "30 minutes", "Out for Delivery");
        System.out.println("\n--- Tracking Info ---");
        System.out.println("  Order ID : " + tracking.getTrackID());
        System.out.println("  Status   : " + tracking.getStatus());
        System.out.println("  ETA      : " + tracking.getEstimatedETA());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ADMIN FLOW
    // ─────────────────────────────────────────────────────────────────────────

    static void adminLoginFlow() {
        System.out.println("\n--- Restaurant Admin Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        RestaurantAdmin admin = loginManager.loginAdmin(username, password);
        if (admin == null)
            return;

        adminMenu(admin);
    }

    static void saveRestaurant(Restaurant restaurant) {
        FileHandler<Restaurant> fh = new FileHandler<>();
        Restaurant[] existing = fh.loadArray("restaurants.dat");

        if (existing == null) {
            System.out.println("Warning: could not load restaurants.dat to save.");
            return;
        }

        // Find and replace the matching restaurant by ID
        for (int i = 0; i < existing.length; i++) {
            if (existing[i].getRestaurantID().equals(restaurant.getRestaurantID())) {
                existing[i] = restaurant;
                break;
            }
        }

        fh.saveArray(existing, "restaurants.dat");
        System.out.println("Restaurant data saved.");
    }

    static void adminMenu(RestaurantAdmin admin) {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("  ADMIN MENU - " + admin.getName());
            System.out.println("  Restaurant: " + admin.getRestaurant().getName());
            System.out.println("========================================");
            System.out.println("  1. View Menu");
            System.out.println("  2. Add Food Item");
            System.out.println("  3. Remove Food Item");
            System.out.println("  4. View All Loyalty Offers");
            System.out.println("  5. Add Loyalty Offer");
            System.out.println("  6. Remove Loyalty Offer");
            System.out.println("  0. Logout");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> admin.viewMenu();
                case "2" -> addFoodItem(admin);
                case "3" -> removeFoodItem(admin);
                case "4" -> {
                    LoyaltyOfferManager mgr = new LoyaltyOfferManager();
                    mgr.printAllOffers();
                }
                case "5" -> addLoyaltyOffer(admin);
                case "6" -> removeLoyaltyOffer(admin);
                case "0" -> {
                    saveRestaurant(admin.getRestaurant());
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void addFoodItem(RestaurantAdmin admin) {
        System.out.print("Food ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Price (PKR): ");
        double price;
        try {
            price = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid price.");
            return;
        }
        System.out.print("Category: ");
        String category = scanner.nextLine().trim();

        FoodItem item = new FoodItem(id, name, price, category, 1);
        admin.addFoodItem(item);
        saveRestaurant(admin.getRestaurant());
    }

    static void removeFoodItem(RestaurantAdmin admin) {
        admin.viewMenu();
        System.out.print("Enter Food ID to remove: ");
        String id = scanner.nextLine().trim();

        List<FoodItem> items = admin.getRestaurant().getMenu().getItems();
        FoodItem toRemove = null;
        for (FoodItem item : items) {
            if (item.getFoodID().equals(id)) {
                toRemove = item;
                break;
            }
        }
        if (toRemove == null) {
            System.out.println("Food item not found.");
            return;
        }
        admin.removeFoodItem(toRemove);
        saveRestaurant(admin.getRestaurant());
    }

    static void addLoyaltyOffer(RestaurantAdmin admin) {
        System.out.print("Offer Code (e.g. LOYAL-Z): ");
        String code = scanner.nextLine().trim();
        System.out.print("Description: ");
        String desc = scanner.nextLine().trim();
        System.out.print("Points Required: ");
        int points;
        try {
            points = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid points.");
            return;
        }
        System.out.print("Discount (PKR): ");
        double discount;
        try {
            discount = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid discount.");
            return;
        }
        System.out.print("Minimum Order (PKR): ");
        double minOrder;
        try {
            minOrder = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid minimum order.");
            return;
        }

        LoyaltyOffer offer = new LoyaltyOffer(code, desc, points, discount, minOrder);
        admin.addOffer(offer);
    }

    static void removeLoyaltyOffer(RestaurantAdmin admin) {
        LoyaltyOfferManager manager = new LoyaltyOfferManager();
        manager.printAllOffers();
        System.out.print("Enter Offer Code to remove: ");
        String code = scanner.nextLine().trim();
        admin.removeOffer(code);
    }
}