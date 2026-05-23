import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.List;

public class MenuViewController {

    @FXML
    private Label restaurantNameLabel;
    @FXML
    private Label cuisineLabel;
    @FXML
    private Label ratingLabel;
    @FXML
    private Label cartTotalLabel;
    @FXML
    private VBox menuContainer;

    private Customer customer;
    private Restaurant restaurant;

    @FXML
    public void initialize() {
        customer = SessionManager.getInstance().getCurrentCustomer();
        restaurant = SessionManager.getInstance().getSelectedRestaurant();

        if (customer == null || restaurant == null) {
            SceneManager.getInstance().switchTo("CustomerDashboard");
            return;
        }

        restaurantNameLabel.setText(restaurant.getName());
        cuisineLabel.setText("· " + restaurant.getCuisineType());
        ratingLabel.setText(String.format("★ %.1f", restaurant.getRating()));
        updateCartTotal();
        loadMenuItems();
    }

    private void loadMenuItems() {
        menuContainer.getChildren().clear();
        List<FoodItem> items = restaurant.getMenu().getItems();
        for (FoodItem item : items) {
            menuContainer.getChildren().add(createMenuItemRow(item));
        }
    }

    private HBox createMenuItemRow(FoodItem item) {
        HBox row = new HBox(12);
        row.getStyleClass().add("dashboard-menu-item-row");
        row.setAlignment(Pos.CENTER_LEFT);

        // Info section
        VBox info = new VBox(3);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label nameLabel = new Label(item.getName());
        nameLabel.getStyleClass().add("dashboard-menu-item-name");

        Label categoryLabel = new Label(item.getCategory());
        categoryLabel.getStyleClass().add("dashboard-menu-item-category");

        Label ratingLabel = new Label(String.format("★ %.1f  ·  %d orders",
                item.getRating(), item.getOrderCount()));
        ratingLabel.getStyleClass().add("dashboard-menu-item-rating");

        info.getChildren().addAll(nameLabel, categoryLabel, ratingLabel);

        // Price
        Label priceLabel = new Label("Rs. " + (int) item.getPrice());
        priceLabel.getStyleClass().add("dashboard-menu-item-price");
        priceLabel.setMinWidth(90);

        // Quantity spinner
        Spinner<Integer> qtySpinner = new Spinner<>(1, 10, 1);
        qtySpinner.setPrefWidth(75);
        qtySpinner.setStyle(
                "-fx-background-color: #2a2a2a;" +
                        "-fx-border-color: #444444;" +
                        "-fx-border-radius: 6;");

        // Add to cart button
        Button addBtn = new Button("Add to Cart");
        addBtn.getStyleClass().add("dashboard-add-button");
        addBtn.setOnAction(e -> {
            int qty = qtySpinner.getValue();
            FoodItem toAdd = new FoodItem(
                    item.getFoodID(), item.getName(),
                    item.getPrice(), item.getCategory(), qty);
            customer.getCart().addItem(toAdd);
            updateCartTotal();

            // Visual feedback
            addBtn.setText("Added ✓");
            addBtn.setStyle(
                    "-fx-background-color: #4a7c59;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 6 14;" +
                            "-fx-background-radius: 6;" +
                            "-fx-cursor: hand;");

            // Reset button after 1.5 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ex) {
                }
                Platform.runLater(() -> {
                    addBtn.setText("Add to Cart");
                    addBtn.setStyle("");
                    addBtn.getStyleClass().setAll("dashboard-add-button");
                });
            }).start();
        });

        row.getChildren().addAll(info, priceLabel, qtySpinner, addBtn);
        return row;
    }

    private void updateCartTotal() {
        cartTotalLabel.setText("Cart: Rs." + (int) customer.getCart().getTotal());
    }

    @FXML
    private void goBack() {
        SceneManager.getInstance().switchTo("RestaurantBrowse");
    }

    @FXML
    private void goHome() {
        SceneManager.getInstance().switchTo("CustomerDashboard");
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
    private void goTracking() {
        SceneManager.getInstance().switchTo("OrderTracking");
    }
}