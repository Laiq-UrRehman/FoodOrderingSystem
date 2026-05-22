import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.List;

public class CartController {

    @FXML
    private Label itemCountLabel;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private Label offersAvailableLabel;
    @FXML
    private VBox cartItemsContainer;

    private Customer customer;
    private Cart cart;

    @FXML
    public void initialize() {
        customer = SessionManager.getInstance().getCurrentCustomer();
        if (customer == null) {
            SceneManager.getInstance().switchTo("Login");
            return;
        }
        cart = customer.getCart();
        loadCart();
    }

    // ── Load ────────────────────────────────────────────────────────────────

    private void loadCart() {
        cartItemsContainer.getChildren().clear();

        List<FoodItem> items = cart.getItems();
        int count = items.size();
        itemCountLabel.setText(count + (count == 1 ? " item" : " items"));

        if (items.isEmpty()) {
            cartItemsContainer.getChildren().add(buildEmptyState());
        } else {
            for (FoodItem item : items) {
                cartItemsContainer.getChildren().add(createCartItemRow(item));
            }
        }

        updateTotals();
        updateOffersInfo();
    }

    private VBox buildEmptyState() {
        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(80));

        Label icon = new Label("🛒");
        icon.setStyle("-fx-font-size: 52px;");

        Label msg = new Label("Your cart is empty");
        msg.setStyle("-fx-text-fill: #555555; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label sub = new Label("Browse restaurants and add items to get started");
        sub.setStyle("-fx-text-fill: #444444; -fx-font-size: 13px;");

        box.getChildren().addAll(icon, msg, sub);
        return box;
    }

    // ── Cart Item Row ────────────────────────────────────────────────────────

    private HBox createCartItemRow(FoodItem item) {
        HBox row = new HBox(12);
        row.getStyleClass().add("dashboard-cart-item-row");
        row.setAlignment(Pos.CENTER_LEFT);

        // Info
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label nameLabel = new Label(item.getName());
        nameLabel.getStyleClass().add("dashboard-menu-item-name");

        Label catLabel = new Label(item.getCategory());
        catLabel.getStyleClass().add("dashboard-menu-item-category");

        info.getChildren().addAll(nameLabel, catLabel);

        // Unit price
        Label unitPrice = new Label("Rs. " + (int) item.getPrice() + " each");
        unitPrice.getStyleClass().add("dashboard-menu-item-category");
        unitPrice.setMinWidth(110);

        // Item line total — defined before spinner so lambda can capture it
        Label itemTotal = new Label("Rs. " + (int) (item.getPrice() * item.getQuantity()));
        itemTotal.getStyleClass().add("dashboard-menu-item-price");
        itemTotal.setMinWidth(90);

        // Quantity spinner
        Spinner<Integer> qtySpinner = new Spinner<>(1, 10, item.getQuantity());
        qtySpinner.setPrefWidth(80);
        qtySpinner.setStyle(
                "-fx-background-color: #2a2a2a;" +
                        "-fx-border-color: #444444;" +
                        "-fx-border-radius: 6;");

        qtySpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            cart.updateQuantity(item.getFoodID(), newVal);
            itemTotal.setText("Rs. " + (int) (item.getPrice() * newVal));
            updateTotals();
            updateOffersInfo();
        });

        // Remove button
        Button removeBtn = new Button("✕");
        removeBtn.getStyleClass().add("dashboard-cart-remove-button");
        removeBtn.setOnAction(e -> {
            cart.removeItem(item);
            loadCart();
        });

        row.getChildren().addAll(info, unitPrice, qtySpinner, itemTotal, removeBtn);
        return row;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void updateTotals() {
        int total = (int) cart.getTotal();
        subtotalLabel.setText("Rs. " + total);
        totalLabel.setText("Rs. " + total);
    }

    private void updateOffersInfo() {
        List<LoyaltyOffer> offers = customer.getLoyaltyPoints().getAvailableOffers(cart.getTotal());

        if (cart.getItems().isEmpty()) {
            offersAvailableLabel.setText("Add items to see available offers.");
        } else if (offers.isEmpty()) {
            offersAvailableLabel.setText("No offers available right now.\nEarn more points by ordering!");
        } else {
            offersAvailableLabel.setText(offers.size()
                    + (offers.size() == 1 ? " offer" : " offers")
                    + " available — select at checkout.");
        }
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    @FXML
    private void goCheckout() {
        if (cart.getItems().isEmpty())
            return;
        SceneManager.getInstance().switchTo("Checkout");
    }

    @FXML
    private void goScheduledOrder() {
        if (cart.getItems().isEmpty())
            return;
        SceneManager.getInstance().switchTo("ScheduledOrder");
    }

    @FXML
    private void goBack() {
        SceneManager.getInstance().switchTo("CustomerDashboard");
    }

    @FXML
    private void goHome() {
        SceneManager.getInstance().switchTo("CustomerDashboard");
    }

    @FXML
    private void goBrowse() {
        SceneManager.getInstance().switchTo("CustomerDashboard");
    }

    @FXML
    private void goCart() {
        /* already here */ }

    @FXML
    private void goOrders() {
        SceneManager.getInstance().switchTo("CustomerDashboard");
    } // Phase 5

    @FXML
    private void goTracking() {
        SceneManager.getInstance().switchTo("CustomerDashboard"); // Phase 5
    }
}
