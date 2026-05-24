import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.List;

public class RestaurantBrowseController {

    @FXML
    private TextField searchField;
    @FXML
    private VBox suggestionsDropdown;
    @FXML
    private Label sectionLabel;
    @FXML
    private FlowPane resultsContainer;

    private Customer customer;
    private Restaurant[] restaurants;
    private SearchManager searchManager;

    @FXML
    public void initialize() {
        customer = SessionManager.getInstance().getCurrentCustomer();
        if (customer == null) {
            SceneManager.getInstance().switchTo("Login");
            return;
        }
        searchManager = new SearchManager();
        restaurants = searchManager.getAllRestaurants();
        loadSuggestions();
    }

    // ── Search ────────────────────────────────────────────────────────────────

    private void loadSuggestions() {
        sectionLabel.setText("SUGGESTED FOR YOU");
        resultsContainer.getChildren().clear();

        List<FoodItem> suggestions = searchManager.getSuggestedItems(customer, restaurants, 6);

        if (suggestions.isEmpty()) {
            Label empty = new Label("Order something to get personalised suggestions!");
            empty.setStyle("-fx-text-fill: #555555; -fx-font-size: 13px;");
            resultsContainer.getChildren().add(empty);
            return;
        }

        for (FoodItem item : suggestions) {
            Restaurant owner = findRestaurantForItem(item);
            resultsContainer.getChildren().add(createSuggestionCard(item, owner));
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

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            hideSuggestions();
            loadSuggestions();
            return;
        }

        suggestionsDropdown.getChildren().clear();

        List<Restaurant> matchedRestaurants = searchManager.searchRestaurants(query, restaurants);
        for (Restaurant r : matchedRestaurants) {
            suggestionsDropdown.getChildren().add(buildSuggestionRow(
                    "🍽  " + r.getName() + "  ·  " + r.getCuisineType(), () -> {
                        SessionManager.getInstance().setSelectedRestaurant(r);
                        SceneManager.getInstance().switchTo("MenuView");
                    }));
        }

        for (Restaurant r : restaurants) {
            List<FoodItem> items = searchManager.searchMenuItems(query, r);
            for (FoodItem item : items) {
                final Restaurant finalR = r;
                suggestionsDropdown.getChildren().add(buildSuggestionRow(
                        "🍴  " + item.getName() + "  ·  " + r.getName(), () -> {
                            SessionManager.getInstance().setSelectedRestaurant(finalR);
                            SceneManager.getInstance().switchTo("MenuView");
                        }));
            }
        }

        if (suggestionsDropdown.getChildren().isEmpty()) {
            Label none = new Label("No results for \"" + query + "\"");
            none.setStyle("-fx-text-fill: #555555; -fx-font-size: 13px; -fx-padding: 12 16;");
            suggestionsDropdown.getChildren().add(none);
        }

        suggestionsDropdown.setVisible(true);
        suggestionsDropdown.setManaged(true);
    }

    private Label buildSuggestionRow(String text, Runnable onClick) {
        Label row = new Label(text);
        row.setMaxWidth(Double.MAX_VALUE);
        row.setStyle(
                "-fx-text-fill: #cccccc; -fx-font-size: 13px;" +
                        "-fx-padding: 10 16; -fx-cursor: hand;" +
                        "-fx-border-color: transparent transparent #222222 transparent;" +
                        "-fx-border-width: 0 0 1 0;");
        row.setOnMouseEntered(e -> row.setStyle(
                "-fx-text-fill: white; -fx-font-size: 13px;" +
                        "-fx-padding: 10 16; -fx-cursor: hand;" +
                        "-fx-background-color: #222222;" +
                        "-fx-border-color: transparent transparent #222222 transparent;" +
                        "-fx-border-width: 0 0 1 0;"));
        row.setOnMouseExited(e -> row.setStyle(
                "-fx-text-fill: #cccccc; -fx-font-size: 13px;" +
                        "-fx-padding: 10 16; -fx-cursor: hand;" +
                        "-fx-border-color: transparent transparent #222222 transparent;" +
                        "-fx-border-width: 0 0 1 0;"));
        row.setOnMouseClicked(e -> onClick.run());
        return row;
    }

    private void hideSuggestions() {
        suggestionsDropdown.setVisible(false);
        suggestionsDropdown.setManaged(false);
        suggestionsDropdown.getChildren().clear();
    }

    // ── Restaurant Card ───────────────────────────────────────────────────────

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

    // ── Navigation ────────────────────────────────────────────────────────────

    @FXML
    private void goHome() {
        SceneManager.getInstance().switchTo("CustomerDashboard");
    }

    @FXML
    private void goBrowse() {
        /* already here */ }

    @FXML
    private void goCart() {
        SceneManager.getInstance().switchTo("Cart");
    }

    @FXML
    private void goOrders() {
        SceneManager.getInstance().switchTo("OrderHistory");
    }
}