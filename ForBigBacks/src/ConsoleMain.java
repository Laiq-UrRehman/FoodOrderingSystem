// Updated: checkoutNormal() and checkoutScheduled() now pass selectedRestaurant into cart.checkOut() variants
// Updated: Added full Search & Browse menu with SearchManager integration

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class ConsoleMain {

    static Scanner scanner = new Scanner(System.in);
    static LoginManager loginManager = new LoginManager();
    static SearchManager searchManager = new SearchManager();
    static Restaurant selectedRestaurant = null;

    public static void main(String[] args) {

        seedIfNeeded();

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
        java.io.File riderFile = new java.io.File("riders.dat");
        java.io.File adminCredFile = new java.io.File("admin_credentials.dat");

        if (!restaurantFile.exists()) {
            System.out.println("Seeding restaurant data...");
            DataSeeder.seedRestaurants();
        }
        if (!customerFile.exists()) {
            System.out.println("Seeding customer data...");
            CustomerSeeder.seedCustomers();
        }
        if (!riderFile.exists()) {
            System.out.println("Seeding rider data...");
            RiderSeeder.seedRiders();
        }
        if (!adminCredFile.exists()) {
            System.out.println("Seeding admin credentials...");
            AdminCredentialsSeeder.seedAdminCredentials();
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

        FileHandler<Customer> fh = new FileHandler<>();
        Customer[] existing = fh.loadArray("customers.dat");
        int count = (existing == null) ? 0 : existing.length;

        if (existing != null) {
            for (Customer c : existing) {
                if (c.getUsername().equals(username)) {
                    System.out.println("Username already taken. Please choose another.");
                    return;
                }
            }
        }

        String newID = String.format("C%03d", count + 1);
        Customer newCustomer = new Customer(newID, name, address, phone, username, password, new Location(0, 0));

        Customer[] updated = new Customer[count + 1];
        if (existing != null)
            System.arraycopy(existing, 0, updated, 0, count);
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
            System.out.println("  1.  Search & Browse");
            System.out.println("  2.  Add to Cart from Selected Restaurant");
            System.out.println("  3.  View Cart");
            System.out.println("  4.  Checkout (Normal Order)");
            System.out.println("  5.  Checkout (Scheduled Order)");
            System.out.println("  6.  View Order History");
            System.out.println("  7.  Cancel an Order");
            System.out.println("  8.  View Loyalty Points");
            System.out.println("  9.  View Available Loyalty Offers");
            System.out.println("  10. View Scheduled Orders");
            System.out.println("  11. Track Order");
            System.out.println("  0.  Logout");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> searchAndBrowseMenu(customer);
                case "2" -> addToCartFromSelected(customer);
                case "3" -> customer.getCart().viewCart();
                case "4" -> checkoutNormal(customer);
                case "5" -> checkoutScheduled(customer);
                case "6" -> viewOrderHistory(customer);
                case "7" -> cancelOrder(customer);
                case "8" -> customer.getLoyaltyPoints().printBalance();
                case "9" -> customer.getLoyaltyPoints().printAvailableOffers(customer.getCart().getTotal());
                case "10" -> viewScheduledOrders(customer);
                case "11" -> trackOrder(customer);
                case "0" -> {
                    saveCustomer(customer);
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SEARCH & BROWSE
    // ─────────────────────────────────────────────────────────────────────────

    static void searchAndBrowseMenu(Customer customer) {
        Restaurant[] all = searchManager.getAllRestaurants();

        while (true) {
            System.out.println("\n--- Search & Browse ---");
            System.out.println("  1. Search restaurants by name");
            System.out.println("  2. Filter restaurants by cuisine");
            System.out.println("  3. View top rated restaurants");
            System.out.println("  4. Browse a restaurant's menu");
            System.out.println("  5. Search items in a restaurant");
            System.out.println("  6. Filter items by category");
            System.out.println("  7. View top rated items in a restaurant");
            System.out.println("  8. View trending categories");
            System.out.println("  9. Smart suggestions for you");
            System.out.println("  0. Back");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.print("Search restaurants: ");
                    String query = scanner.nextLine().trim();
                    List<Restaurant> results = searchManager.searchRestaurants(query, all);
                    System.out.println("\n--- Results ---");
                    searchManager.printRestaurants(results);
                    selectRestaurantFromList(results);
                }
                case "2" -> {
                    System.out.print("Enter cuisine (e.g. Pakistani, Italian, American): ");
                    String cuisine = scanner.nextLine().trim();
                    List<Restaurant> results = searchManager.filterByCuisine(cuisine, all);
                    System.out.println("\n--- " + cuisine + " Restaurants ---");
                    searchManager.printRestaurants(results);
                    selectRestaurantFromList(results);
                }
                case "3" -> {
                    List<Restaurant> topRated = searchManager.getTopRatedRestaurants(all);
                    System.out.println("\n--- Top Rated Restaurants ---");
                    searchManager.printRestaurants(topRated);
                    selectRestaurantFromList(topRated);
                }
                case "4" -> {
                    Restaurant r = pickRestaurant(all);
                    if (r != null) {
                        selectedRestaurant = r;
                        printMenu(r);
                    }
                }
                case "5" -> {
                    Restaurant r = pickRestaurant(all);
                    if (r != null) {
                        selectedRestaurant = r;
                        System.out.print("Search items: ");
                        String query = scanner.nextLine().trim();
                        List<FoodItem> results = searchManager.searchMenuItems(query, r);
                        System.out.println("\n--- Results in " + r.getName() + " ---");
                        searchManager.printItems(results);
                    }
                }
                case "6" -> {
                    Restaurant r = pickRestaurant(all);
                    if (r != null) {
                        selectedRestaurant = r;
                        System.out.print("Enter category (e.g. Burgers, Pizza, Drinks): ");
                        String category = scanner.nextLine().trim();
                        List<FoodItem> results = searchManager.filterByCategory(category, r);
                        System.out.println("\n--- " + category + " in " + r.getName() + " ---");
                        searchManager.printItems(results);
                    }
                }
                case "7" -> {
                    Restaurant r = pickRestaurant(all);
                    if (r != null) {
                        selectedRestaurant = r;
                        List<FoodItem> topItems = searchManager.getTopRatedItems(r);
                        System.out.println("\n--- Top Rated Items in " + r.getName() + " ---");
                        searchManager.printItems(topItems);
                    }
                }
                case "8" -> {
                    List<String> trending = searchManager.getTrendingCategories(all);
                    searchManager.printCategories(trending);
                }
                case "9" -> {
                    List<FoodItem> suggestions = searchManager.getSuggestedItems(customer, all, 5);
                    System.out.println("\n--- Suggested For You ---");
                    searchManager.printItems(suggestions);
                }
                case "0" -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Lets the customer pick a restaurant from a filtered/searched list
     * and sets it as selectedRestaurant.
     */
    static void selectRestaurantFromList(List<Restaurant> list) {
        if (list.isEmpty())
            return;
        System.out.print("Select a restaurant by number to browse (or 0 to skip): ");
        int pick;
        try {
            pick = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (NumberFormatException e) {
            return;
        }
        if (pick < 0 || pick >= list.size())
            return;
        selectedRestaurant = list.get(pick);
        System.out.println("Selected: " + selectedRestaurant.getName());
        printMenu(selectedRestaurant);
    }

    /**
     * Shows all restaurants and lets the customer pick one by number.
     */
    static Restaurant pickRestaurant(Restaurant[] all) {
        System.out.println("\n--- All Restaurants ---");
        for (int i = 0; i < all.length; i++) {
            System.out.println("  " + (i + 1) + ". " + all[i].getName()
                    + " | " + all[i].getCuisineType()
                    + " | Rating: " + String.format("%.1f", all[i].getRating()));
        }
        System.out.print("Select restaurant (number): ");
        int pick;
        try {
            pick = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return null;
        }
        if (pick < 0 || pick >= all.length) {
            System.out.println("Invalid selection.");
            return null;
        }
        return all[pick];
    }

    static void printMenu(Restaurant r) {
        System.out.println("\n--- Menu: " + r.getName() + " ---");
        List<FoodItem> items = r.getMenu().getItems();
        for (int i = 0; i < items.size(); i++) {
            FoodItem item = items.get(i);
            System.out.println("  " + (i + 1) + ". [" + item.getFoodID() + "] "
                    + item.getName() + " | " + item.getCategory()
                    + " | Rs." + item.getPrice()
                    + " | Rating: " + String.format("%.1f", item.getRating()));
        }
    }

    static void addToCartFromSelected(Customer customer) {
        if (selectedRestaurant == null) {
            System.out.println("No restaurant selected. Please use Search & Browse first.");
            return;
        }

        printMenu(selectedRestaurant);
        List<FoodItem> menuItems = selectedRestaurant.getMenu().getItems();

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

    // ─────────────────────────────────────────────────────────────────────────
    // CHECKOUT
    // ─────────────────────────────────────────────────────────────────────────

    static void checkoutNormal(Customer customer) {
        Cart cart = customer.getCart();
        if (cart.getItems().isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        if (selectedRestaurant == null) {
            System.out.println("Please browse a restaurant before checking out.");
            return;
        }

        cart.viewCart();
        RedeemCode redeemCode = offerSelectionFlow(customer, cart);

        System.out.println("\nSelect Payment Method:");
        System.out.println("  1. Cash");
        System.out.println("  2. Card");
        System.out.print("Choice: ");
        String payChoice = scanner.nextLine().trim();

        Order order;
        if (redeemCode != null) {
            order = cart.checkOut(customer, redeemCode, selectedRestaurant);
        } else {
            order = cart.checkOut(customer, selectedRestaurant);
        }

        FileHandler<Rider> riderFH = new FileHandler<>();
        Rider[] ridersArr = riderFH.loadArray("riders.dat");
        java.util.List<Rider> riders = (ridersArr != null)
                ? new java.util.ArrayList<>(java.util.Arrays.asList(ridersArr))
                : new java.util.ArrayList<>();

        if (riders.isEmpty()) {
            System.out.println("Cannot start tracking: no rider data.");
            customer.placeOrder(order);
            return;
        }

        if (payChoice.equals("2")) {
            System.out.print("Card Number: ");
            String cardNum = scanner.nextLine().trim();
            System.out.print("Card Holder Name: ");
            String holderName = scanner.nextLine().trim();
            System.out.print("Expiry Date (MM/YY): ");
            String expiry = scanner.nextLine().trim();
            order.proceedWithCardPayment(cardNum, holderName, expiry, selectedRestaurant, customer, riders);
        } else {
            order.proceedWithCashPayment(selectedRestaurant, customer, riders);
        }

        customer.placeOrder(order);
        System.out.println("Order placed successfully! Total paid: Rs." + order.getTotalAmount());
        if (order.getTracking() != null)
            System.out.println(order.getTracking().getSummary());

        riderFH.saveArray(riders.toArray(new Rider[0]), "riders.dat");
    }

    static void checkoutScheduled(Customer customer) {
        Cart cart = customer.getCart();
        if (cart.getItems().isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        if (selectedRestaurant == null) {
            System.out.println("Please browse a restaurant before checking out.");
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
            order = cart.checkOutScheduled(customer, redeemCode, scheduledTime, selectedRestaurant);
        } else {
            order = cart.checkOutScheduled(customer, scheduledTime, selectedRestaurant);
        }

        if (order == null)
            return;

        order.confirm();
        customer.placeOrder(order);
        System.out.println("Scheduled order placed! Total: Rs." + order.getTotalAmount());
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

    // ─────────────────────────────────────────────────────────────────────────
    // ORDER MANAGEMENT
    // ─────────────────────────────────────────────────────────────────────────

    static void viewOrderHistory(Customer customer) {
        List<Order> history = customer.viewOrderHistory();
        if (history.isEmpty()) {
            System.out.println("No orders yet.");
            return;
        }
        System.out.println("\n--- Order History ---");
        for (Order o : history) {
            System.out.println("  [" + o.getOrderID() + "] Status: " + o.getStatus() + " | Rs." + o.getTotalAmount());
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
        for (ScheduledOrder so : scheduled)
            System.out.println("  " + so);
    }

    static void trackOrder(Customer customer) {
        java.util.List<Order> history = customer.viewOrderHistory();
        if (history.isEmpty()) {
            System.out.println("No orders to track.");
            return;
        }

        System.out.print("Enter Order ID to track: ");
        String orderID = scanner.nextLine().trim();

        for (Order o : history) {
            if (o.getOrderID().equals(orderID)) {
                OrderTracking tracking = o.getTracking();
                if (tracking == null) {
                    System.out.println("No tracking available for this order.");
                    return;
                }
                System.out.println("\n--- Tracking Info ---");
                System.out.println(tracking.getSummary());
                return;
            }
        }
        System.out.println("Order ID not found.");
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