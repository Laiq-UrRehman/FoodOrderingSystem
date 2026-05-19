import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.List;

public class CustomerDashboardController {

    @FXML
    private Label customerNameLabel;
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
    }

    private void loadCustomerData() {
        customerNameLabel.setText(customer.getName());
        loyaltyPointsBadge.setText(customer.viewLoyaltyPoints() + " pts");
        totalOrdersLabel.setText(String.valueOf(customer.viewOrderHistory().size()));
        pointsStatLabel.setText(String.valueOf(customer.viewLoyaltyPoints()));
        String preferred = customer.getPreferredCategory();
        favouriteCategoryLabel.setText(preferred != null ? preferred : "None yet");
    }

    private void loadRestaurants() {
        FileHandler<Restaurant> fh = new FileHandler<>();
        restaurants = fh.loadArray("restaurants.dat");
        if (restaurants == null)
            return;

        restaurantsContainer.getChildren().clear();
        for (Restaurant r : restaurants) {
            restaurantsContainer.getChildren().add(createRestaurantCard(r));
        }
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
            suggestionsContainer.getChildren().add(createSuggestionCard(item));
        }
    }

    private VBox createRestaurantCard(Restaurant restaurant) {
        VBox card = new VBox();
        card.getStyleClass().add("dashboard-restaurant-card");
        card.setPrefWidth(280);

        VBox header = new VBox();
        header.getStyleClass().add("dashboard-restaurant-card-header");
        header.setPrefHeight(100);
        header.setAlignment(Pos.CENTER);
        Label emoji = new Label(getCuisineEmoji(restaurant.getCuisineType()));
        emoji.setStyle("-fx-font-size: 36px;");
        header.getChildren().add(emoji);

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

    private VBox createSuggestionCard(FoodItem item) {
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

        card.getChildren().addAll(nameLabel, detailLabel, priceLabel);
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
        SceneManager.getInstance().switchTo("CustomerDashboard");
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
    private void goTracking() {
        SceneManager.getInstance().switchTo("OrderTracking");
    }

    @FXML
    private void handleLogout() {
        FileHandler<Customer> fh = new FileHandler<>();
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
        SessionManager.getInstance().logout();
        SceneManager.getInstance().switchTo("Login");
    }
}