import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class CheckoutController {

    @FXML
    private VBox orderItemsContainer;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label discountLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private VBox cardFieldsContainer;
    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField cardHolderField;
    @FXML
    private TextField expiryField;
    @FXML
    private VBox offersContainer;
    @FXML
    private Label offersStatusLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private Button cashButton;
    @FXML
    private Button cardButton;

    private Customer customer;
    private Restaurant restaurant;
    private Cart cart;

    private boolean isCardPayment = false;
    private LoyaltyOffer selectedOffer = null;
    private double discount = 0;

    @FXML
    public void initialize() {
        customer = SessionManager.getInstance().getCurrentCustomer();
        restaurant = SessionManager.getInstance().getSelectedRestaurant();

        if (customer == null) {
            SceneManager.getInstance().switchTo("Login");
            return;
        }
        cart = customer.getCart();
        if (cart.getItems().isEmpty()) {
            SceneManager.getInstance().switchTo("Cart");
            return;
        }

        // Card fields hidden until card payment is selected
        cardFieldsContainer.setVisible(false);
        cardFieldsContainer.setManaged(false);

        loadOrderSummary();
        loadOffers();
        updateTotalDisplay();
    }

    // ── Order Summary ────────────────────────────────────────────────────────

    private void loadOrderSummary() {
        orderItemsContainer.getChildren().clear();

        for (FoodItem item : cart.getItems()) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(4, 0, 4, 0));

            Label nameLabel = new Label(item.getName() + "  ×" + item.getQuantity());
            nameLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 13px;");
            HBox.setHgrow(nameLabel, Priority.ALWAYS);

            Label priceLabel = new Label("Rs. " + (int) (item.getPrice() * item.getQuantity()));
            priceLabel.getStyleClass().add("dashboard-menu-item-price");
            priceLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #8B5E3C; -fx-font-weight: bold;");

            row.getChildren().addAll(nameLabel, priceLabel);
            orderItemsContainer.getChildren().add(row);
        }
    }

    // ── Loyalty Offers ───────────────────────────────────────────────────────

    private void loadOffers() {
        List<LoyaltyOffer> offers = customer.getLoyaltyPoints().getAvailableOffers(cart.getTotal());

        offersContainer.getChildren().clear();

        if (offers.isEmpty()) {
            offersStatusLabel.setText(
                    "No offers available. Keep ordering to earn more points!");
            return;
        }

        offersStatusLabel.setText("Select an offer to apply:");

        ToggleGroup group = new ToggleGroup();

        // "No offer" option
        RadioButton noneRb = new RadioButton("No offer");
        noneRb.setToggleGroup(group);
        noneRb.setStyle("-fx-text-fill: #888888; -fx-font-size: 13px;");
        noneRb.setSelected(true);
        noneRb.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                selectedOffer = null;
                discount = 0;
                updateTotalDisplay();
                clearOfferCardSelection();
            }
        });
        offersContainer.getChildren().add(noneRb);

        // One card per offer
        for (LoyaltyOffer offer : offers) {
            VBox offerCard = new VBox(6);
            offerCard.getStyleClass().add("dashboard-offer-card");

            RadioButton rb = new RadioButton();
            rb.setToggleGroup(group);
            rb.setStyle("-fx-text-fill: white;");

            Label desc = new Label(offer.getDescription());
            desc.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");

            Label detail = new Label(
                    "Save Rs. " + (int) offer.getDiscountPKR()
                            + "   ·   " + offer.getPointsRequired() + " pts required"
                            + "   ·   Min Rs. " + (int) offer.getMinOrderPKR());
            detail.getStyleClass().add("dashboard-menu-item-category");

            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getChildren().addAll(rb, new VBox(3, desc, detail));

            offerCard.getChildren().add(row);

            rb.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    selectedOffer = offer;
                    discount = offer.getDiscountPKR();
                    updateTotalDisplay();
                    clearOfferCardSelection();
                    offerCard.getStyleClass().add("dashboard-offer-card-selected");
                }
            });

            // Clicking the card itself also selects the radio button
            offerCard.setOnMouseClicked(e -> rb.setSelected(true));

            offersContainer.getChildren().add(offerCard);
        }
    }

    private void clearOfferCardSelection() {
        offersContainer.getChildren().forEach(node -> node.getStyleClass().remove("dashboard-offer-card-selected"));
    }

    // ── Totals Display ───────────────────────────────────────────────────────

    private void updateTotalDisplay() {
        int subtotal = (int) cart.getTotal();
        int total = (int) Math.max(0, subtotal - discount);
        subtotalLabel.setText("Rs. " + subtotal);
        discountLabel.setText("- Rs. " + (int) discount);
        totalLabel.setText("Rs. " + total);
    }

    // ── Payment Toggle ───────────────────────────────────────────────────────

    @FXML
    private void selectCash() {
        isCardPayment = false;
        cashButton.getStyleClass().setAll("dashboard-payment-toggle-active");
        cardButton.getStyleClass().setAll("dashboard-payment-toggle");
        cardFieldsContainer.setVisible(false);
        cardFieldsContainer.setManaged(false);
        errorLabel.setText("");
    }

    @FXML
    private void selectCard() {
        isCardPayment = true;
        cardButton.getStyleClass().setAll("dashboard-payment-toggle-active");
        cashButton.getStyleClass().setAll("dashboard-payment-toggle");
        cardFieldsContainer.setVisible(true);
        cardFieldsContainer.setManaged(true);
        errorLabel.setText("");
    }

    // ── Place Order ──────────────────────────────────────────────────────────

    @FXML
    private void placeOrder() {
        errorLabel.setText("");

        // Validate card fields if card payment selected
        if (isCardPayment) {
            if (cardNumberField.getText().trim().isEmpty()
                    || cardHolderField.getText().trim().isEmpty()
                    || expiryField.getText().trim().isEmpty()) {
                errorLabel.setText("Please fill in all card details.");
                return;
            }
        }

        // Checkout — generates redeem code + clears cart
        Order order;
        if (selectedOffer != null) {
            RedeemCode code = cart.selectOffer(customer, selectedOffer);
            if (code == null) {
                errorLabel.setText(
                        "Could not generate redeem code. Check your points balance.");
                return;
            }
            order = (restaurant != null)
                    ? cart.checkOut(customer, code, restaurant)
                    : cart.checkOut();
        } else {
            order = (restaurant != null)
                    ? cart.checkOut(customer, restaurant)
                    : cart.checkOut();
        }

        // Add to customer's order history
        customer.placeOrder(order);

        // Process payment + start tracking
        FileHandler<Rider> riderFH = new FileHandler<>();
        Rider[] ridersArr = riderFH.loadArray("riders.dat");
        List<Rider> riders = (ridersArr != null)
                ? Arrays.asList(ridersArr)
                : new ArrayList<>();

        if (restaurant != null) {
            if (isCardPayment) {
                order.proceedWithCardPayment(
                        cardNumberField.getText().trim(),
                        cardHolderField.getText().trim(),
                        expiryField.getText().trim(),
                        restaurant, customer, riders);
            } else {
                order.proceedWithCashPayment(restaurant, customer, riders);
            }
        }

        // Persist updated customer
        saveCustomer();

        SceneManager.getInstance().switchTo("CustomerDashboard"); // Phase 5: change to OrderHistory
    }

    // ── Persistence ──────────────────────────────────────────────────────────

    private void saveCustomer() {
        FileHandler<Customer> fh = new FileHandler<>();
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
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    @FXML
    private void goBack() {
        SceneManager.getInstance().switchTo("Cart");
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
        SceneManager.getInstance().switchTo("Cart");
    }

    @FXML
    private void goOrders() {
        SceneManager.getInstance().switchTo("CustomerDashboard");
    } // Phase 5

    @FXML
    private void goTracking() {
        SceneManager.getInstance().switchTo("CustomerDashboard"); // Phase 5 }
    }
}