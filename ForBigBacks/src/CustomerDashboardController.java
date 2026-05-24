// Updated: loadRestaurants() and saveCustomer() and handleLogout() now catch FileHandler.FileOperationException

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.List;

public class CustomerDashboardController {

    @FXML
    private Label loyaltyPointsBadge;
    @FXML
    private Label totalOrdersLabel;
    @FXML
    private Label pointsStatLabel;
    @FXML
    private Label favouriteCategoryLabel;
    @FXML
    private FlowPane restaurantsContainer;
    @FXML
    private FlowPane suggestionsContainer;

    private Customer customer;
    private Restaurant[] restaurants;

    @FXML
    public void initialize() {
        customer = SessionManager.getInstance().getCurrentCustomer();
        if (customer == null) {
            SceneManager.getInstance().switchTo("Login");
            return;
        }
        loadCustomerData();
        loadRestaurants();
        loadSuggestions();
        saveCustomer();
    }

    private void loadCustomerData() {
        loyaltyPointsBadge.setText(customer.viewLoyaltyPoints() + " pts");
        totalOrdersLabel.setText(String.valueOf(customer.viewOrderHistory().size()));
        pointsStatLabel.setText(String.valueOf(customer.viewLoyaltyPoints()));
        String preferred = customer.getPreferredCategory();
        favouriteCategoryLabel.setText(preferred != null ? preferred : "None yet");
    }

    private void loadRestaurants() {
        FileHandler<Restaurant> fh = new FileHandler<>();
        try {
            restaurants = fh.loadArray("restaurants.dat");
        } catch (FileHandler.FileOperationException e) {
            System.out.println("Could not load restaurants: " + e.getMessage());
            return;
        }

        if (restaurants == null)
            return;

        java.util.Arrays.sort(restaurants,
                (a, b) -> Double.compare(b.getRating(), a.getRating()));

        showTopRestaurants();
    }

    private void showTopRestaurants() {
        restaurantsContainer.getChildren().clear();
        removeShowAllRow();

        int limit = Math.min(3, restaurants.length);
        for (int i = 0; i < limit; i++) {
            restaurantsContainer.getChildren().add(createRestaurantCard(restaurants[i]));
        }

        if (restaurants.length > 3) {
            insertShowAllRow();
        }
    }

    private void showAllRestaurants(Button trigger) {
        restaurantsContainer.getChildren().clear();
        removeShowAllRow();

        for (Restaurant r : restaurants) {
            restaurantsContainer.getChildren().add(createRestaurantCard(r));
        }

        insertShowLessRow();
    }

    private void removeShowAllRow() {
        javafx.scene.Parent parent = restaurantsContainer.getParent();
        if (parent instanceof VBox) {
            ((VBox) parent).getChildren().removeIf(n -> "show-all-row".equals(n.getId()));
        }
    }

    private void insertShowAllRow() {
        javafx.scene.Parent parent = restaurantsContainer.getParent();
        if (!(parent instanceof VBox))
            return;
        VBox contentVBox = (VBox) parent;

        HBox row = new HBox();
        row.setId("show-all-row");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setPadding(new Insets(2, 0, 0, 0));

        Button btn = buildToggleButton("Show all ");
        btn.setOnAction(e -> showAllRestaurants(btn));
        applyToggleHover(btn);

        row.getChildren().add(btn);

        int idx = contentVBox.getChildren().indexOf(restaurantsContainer);
        contentVBox.getChildren().add(idx + 1, row);
    }

    private void insertShowLessRow() {
        javafx.scene.Parent parent = restaurantsContainer.getParent();
        if (!(parent instanceof VBox))
            return;
        VBox contentVBox = (VBox) parent;

        HBox row = new HBox();
        row.setId("show-all-row");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setPadding(new Insets(2, 0, 0, 0));

        Button btn = buildToggleButton("Show less");
        btn.setOnAction(e -> {
            removeShowAllRow();
            showTopRestaurants();
        });
        applyToggleHover(btn);

        row.getChildren().add(btn);

        int idx = contentVBox.getChildren().indexOf(restaurantsContainer);
        contentVBox.getChildren().add(idx + 1, row);
    }

    private Button buildToggleButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #8B5E3C;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 6 0;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: transparent;" +
                        "-fx-underline: true;");
        return btn;
    }

    private void applyToggleHover(Button btn) {
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #6F4A2F;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 6 0;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: transparent;" +
                        "-fx-underline: true;"));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #8B5E3C;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 6 0;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: transparent;" +
                        "-fx-underline: true;"));
    }

    private void loadSuggestions() {
        if (restaurants == null)
            return;

        SearchManager sm = new SearchManager();
        List<FoodItem> suggestions = sm.getSuggestedItems(customer, restaurants, 6);

        suggestionsContainer.getChildren().clear();

        if (suggestions.isEmpty()) {
            Label empty = new Label("Order something to get personalised suggestions!");
            empty.setStyle("-fx-text-fill: #555555; -fx-font-size: 13px;");
            suggestionsContainer.getChildren().add(empty);
            return;
        }

        for (FoodItem item : suggestions) {
            Restaurant owner = findRestaurantForItem(item);
            suggestionsContainer.getChildren().add(createSuggestionCard(item, owner));
        }
    }

    private Restaurant findRestaurantForItem(FoodItem item) {
        for (Restaurant r : restaurants) {
            for (FoodItem fi : r.getMenu().getItems()) {
                if (fi.getFoodID().equals(item.getFoodID())) {
                    return r;
                }
            }
        }
        return null;
    }

    private VBox createRestaurantCard(Restaurant restaurant) {
        VBox card = new VBox();
        card.getStyleClass().add("dashboard-restaurant-card");
        card.setPrefWidth(280);

        javafx.scene.layout.StackPane header = new javafx.scene.layout.StackPane();
        header.setPrefHeight(100);
        header.setAlignment(Pos.CENTER);

        String[] bannerColors = getBannerColors(restaurant.getName());
        header.setStyle("-fx-background-color: " + bannerColors[0] + "; -fx-background-radius: 10 10 0 0;");

        VBox bannerContent = new VBox(6);
        bannerContent.setAlignment(Pos.CENTER);

        Label icon = new Label(getCuisineEmoji(restaurant.getCuisineType()));
        icon.setStyle("-fx-font-size: 28px;");

        Label bannerName = new Label(restaurant.getName().toUpperCase());
        bannerName.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + bannerColors[1]
                + "; -fx-letter-spacing: 1px;");

        bannerContent.getChildren().addAll(icon, bannerName);
        header.getChildren().add(bannerContent);

        VBox body = new VBox(6);
        body.setPadding(new Insets(12));

        Label nameLabel = new Label(restaurant.getName());
        nameLabel.getStyleClass().add("dashboard-restaurant-name");

        Label cuisineLabel = new Label(restaurant.getCuisineType());
        cuisineLabel.getStyleClass().add("dashboard-restaurant-cuisine");

        Label ratingLabel = new Label(String.format("★ %.1f  ·  %d ratings",
                restaurant.getRating(), restaurant.getTotalRatings()));
        ratingLabel.getStyleClass().add("dashboard-restaurant-rating");

        body.getChildren().addAll(nameLabel, cuisineLabel, ratingLabel);
        card.getChildren().addAll(header, body);

        card.setOnMouseClicked(e -> {
            SessionManager.getInstance().setSelectedRestaurant(restaurant);
            SceneManager.getInstance().switchTo("MenuView");
        });

        return card;
    }

    private String[] getBannerColors(String name) {
        if (name == null)
            return new String[] { "#1a1a1a", "#8B5E3C" };
        switch (name) {
            case "Burger Palace":
                return new String[] { "#1a0d00", "#d4751a" };
            case "Pizza Hub":
                return new String[] { "#1a0000", "#d43a3a" };
            case "Desi Dhaba":
                return new String[] { "#0d1a00", "#7ab83a" };
            case "Sushi House":
                return new String[] { "#00101a", "#3aaed4" };
            case "Taco Fiesta":
                return new String[] { "#1a1000", "#d4b03a" };
            case "Spice Garden":
                return new String[] { "#1a0010", "#d43a9e" };
            case "BBQ Nation":
                return new String[] { "#1a0500", "#d45a1a" };
            case "Pasta Point":
                return new String[] { "#0a001a", "#8a3ad4" };
            case "Cafe Mocha":
                return new String[] { "#100a00", "#c4853a" };
            case "Shawarma Express":
                return new String[] { "#001a15", "#3ad4a0" };
            case "China Town":
                return new String[] { "#1a0000", "#d43a3a" };
            case "Turkish Grill":
                return new String[] { "#001015", "#3a8ad4" };
            case "Sea Food Bay":
                return new String[] { "#00101a", "#3aaed4" };
            case "Healthy Bites":
                return new String[] { "#051a00", "#4ad43a" };
            case "Hot n Spicy":
                return new String[] { "#1a0000", "#d43a3a" };
            default:
                return new String[] { "#1a1a1a", "#8B5E3C" };
        }
    }

    private VBox createSuggestionCard(FoodItem item, Restaurant owner) {
        VBox card = new VBox(8);
        card.getStyleClass().add("dashboard-suggestion-card");
        card.setPrefWidth(280);

        Label nameLabel = new Label(item.getName());
        nameLabel.getStyleClass().add("dashboard-suggestion-name");
        nameLabel.setWrapText(true);

        Label detailLabel = new Label(item.getCategory()
                + "  ·  ★ " + String.format("%.1f", item.getRating()));
        detailLabel.getStyleClass().add("dashboard-suggestion-detail");

        Label priceLabel = new Label("Rs. " + (int) item.getPrice());
        priceLabel.getStyleClass().add("dashboard-suggestion-price");

        if (owner != null) {
            Label restLabel = new Label(owner.getName());
            restLabel.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px;");
            card.getChildren().addAll(nameLabel, detailLabel, priceLabel, restLabel);

            card.setOnMouseClicked(e -> {
                SessionManager.getInstance().setSelectedRestaurant(owner);
                SceneManager.getInstance().switchTo("MenuView");
            });
        } else {
            card.getChildren().addAll(nameLabel, detailLabel, priceLabel);
        }

        return card;
    }

    private String getCuisineEmoji(String cuisine) {
        if (cuisine == null)
            return "🍽";
        switch (cuisine.toLowerCase()) {
            case "american":
                return "🍔";
            case "italian":
                return "🍕";
            case "pakistani":
                return "🍛";
            default:
                return "🍽";
        }
    }

    @FXML
    private void goHome() {
    }

    @FXML
    private void goBrowse() {
        SceneManager.getInstance().switchTo("RestaurantBrowse");
    }

    @FXML
    private void goCart() {
        SceneManager.getInstance().switchTo("Cart");
    }

    @FXML
    private void goOrders() {
        SceneManager.getInstance().switchTo("OrderHistory");
    }

    @FXML
    private void handleLogout() {
        FileHandler<Customer> fh = new FileHandler<>();
        try {
            Customer[] existing = fh.loadArray("customers.dat");
            if (existing != null) {
                for (int i = 0; i < existing.length; i++) {
                    if (existing[i].getUsername().equals(customer.getUsername())) {
                        existing[i] = customer;
                        break;
                    }
                }
                fh.saveArray(existing, "customers.dat");
            }
        } catch (FileHandler.FileOperationException e) {
            System.out.println("Warning: Could not save customer on logout: " + e.getMessage());
        }
        SessionManager.getInstance().logout();
        SceneManager.getInstance().switchTo("Login");
    }

    private void saveCustomer() {
        FileHandler<Customer> fh = new FileHandler<>();
        try {
            Customer[] all = fh.loadArray("customers.dat");
            if (all != null) {
                for (int i = 0; i < all.length; i++) {
                    if (all[i].getUsername().equals(customer.getUsername())) {
                        all[i] = customer;
                        break;
                    }
                }
                fh.saveArray(all, "customers.dat");
            }
        } catch (FileHandler.FileOperationException e) {
            System.out.println("Warning: Could not save customer data: " + e.getMessage());
        }
    }
}