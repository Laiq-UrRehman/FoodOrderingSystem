import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

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

    private Customer customer;

    @FXML
    public void initialize() {
        customer = SessionManager.getInstance().getCurrentCustomer();
        if (customer == null) {
            SceneManager.getInstance().switchTo("Login");
            return;
        }
        loadCustomerData();
        loadRestaurants();
    }

    private void loadCustomerData() {
        customerNameLabel.setText(customer.getName());
        loyaltyPointsBadge.setText(customer.viewLoyaltyPoints() + " pts");
        totalOrdersLabel.setText(String.valueOf(customer.viewOrderHistory().size()));
        pointsStatLabel.setText(String.valueOf(customer.viewLoyaltyPoints()));
        String preferred = customer.getPreferredCategory();
        favouriteCategoryLabel.setText(preferred != null ? preferred : "-");
    }

    private void loadRestaurants() {
        FileHandler<Restaurant> fh = new FileHandler<>();
        Restaurant[] restaurants = fh.loadArray("restaurants.dat");
        if (restaurants == null)
            return;

        restaurantsContainer.getChildren().clear();
        for (Restaurant r : restaurants) {
            restaurantsContainer.getChildren().add(createRestaurantCard(r));
        }
    }

    private VBox createRestaurantCard(Restaurant restaurant) {
        VBox card = new VBox();
        card.getStyleClass().add("dashboard-restaurant-card");
        card.setPrefWidth(220);

        // Header with emoji
        VBox header = new VBox();
        header.getStyleClass().add("dashboard-restaurant-card-header");
        header.setPrefHeight(80);
        header.setAlignment(Pos.CENTER);
        Label emoji = new Label(getCuisineEmoji(restaurant.getCuisineType()));
        emoji.setStyle("-fx-font-size: 30px;");
        header.getChildren().add(emoji);

        // Body
        VBox body = new VBox(4);
        body.setPadding(new Insets(10));

        Label nameLabel = new Label(restaurant.getName());
        nameLabel.getStyleClass().add("dashboard-restaurant-name");

        Label cuisineLabel = new Label(restaurant.getCuisineType());
        cuisineLabel.getStyleClass().add("dashboard-restaurant-cuisine");

        Label ratingLabel = new Label(String.format("★ %.1f", restaurant.getRating()));
        ratingLabel.getStyleClass().add("dashboard-restaurant-rating");

        body.getChildren().addAll(nameLabel, cuisineLabel, ratingLabel);
        card.getChildren().addAll(header, body);

        // Click — store selected restaurant and go to menu
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