import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScheduledOrderController {

    @FXML
    private VBox orderItemsContainer;
    @FXML
    private Label totalLabel;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField hourField;
    @FXML
    private TextField minuteField;
    @FXML
    private Label scheduleErrorLabel;
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

        cardFieldsContainer.setVisible(false);
        cardFieldsContainer.setManaged(false);

        // Restrict date picker to today or later
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
        datePicker.setValue(LocalDate.now());

        loadOrderSummary();
        loadOffers();
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
            priceLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #8B5E3C; -fx-font-weight: bold;");

            row.getChildren().addAll(nameLabel, priceLabel);
            orderItemsContainer.getChildren().add(row);
        }

        totalLabel.setText("Rs. " + (int) cart.getTotal());
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
                clearOfferCardSelection();
            }
        });
        offersContainer.getChildren().add(noneRb);

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
                    clearOfferCardSelection();
                    offerCard.getStyleClass().add("dashboard-offer-card-selected");
                }
            });

            offerCard.setOnMouseClicked(e -> rb.setSelected(true));
            offersContainer.getChildren().add(offerCard);
        }
    }

    private void clearOfferCardSelection() {
        offersContainer.getChildren().forEach(node -> node.getStyleClass().remove("dashboard-offer-card-selected"));
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

    // ── Place Scheduled Order ─────────────────────────────────────────────────

    @FXML
    private void placeScheduledOrder() {
        scheduleErrorLabel.setText("");
        errorLabel.setText("");

        // Build the LocalDateTime
        LocalDateTime scheduledTime = parseScheduledTime();
        if (scheduledTime == null)
            return; // scheduleErrorLabel already set

        // Validate 30 min rule
        long minutesAhead = java.time.Duration.between(
                LocalDateTime.now(), scheduledTime).toMinutes();
        if (minutesAhead < 30) {
            scheduleErrorLabel.setText(
                    "Scheduled time must be at least 30 minutes from now.");
            return;
        }

        // Validate card fields
        if (isCardPayment) {
            if (cardNumberField.getText().trim().isEmpty()
                    || cardHolderField.getText().trim().isEmpty()
                    || expiryField.getText().trim().isEmpty()) {
                errorLabel.setText("Please fill in all card details.");
                return;
            }
        }

        // Checkout
        ScheduledOrder order;
        if (selectedOffer != null) {
            RedeemCode code = cart.selectOffer(customer, selectedOffer);
            if (code == null) {
                errorLabel.setText(
                        "Could not generate redeem code. Check your points balance.");
                return;
            }
            order = (restaurant != null)
                    ? cart.checkOutScheduled(customer, code, scheduledTime, restaurant)
                    : null;
        } else {
            order = (restaurant != null)
                    ? cart.checkOutScheduled(customer, scheduledTime, restaurant)
                    : null;
        }

        if (order == null) {
            // Shouldn't happen since we already validated 30 min, but guard anyway
            scheduleErrorLabel.setText(
                    "Could not schedule order. Please check the time and try again.");
            return;
        }

        customer.placeOrder(order);
        saveCustomer();

        SceneManager.getInstance().switchTo("OrderHistory");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Parses the date picker + hour/minute fields into a LocalDateTime.
     * Sets scheduleErrorLabel and returns null on any parse failure.
     */
    private LocalDateTime parseScheduledTime() {
        LocalDate date = datePicker.getValue();
        if (date == null) {
            scheduleErrorLabel.setText("Please select a delivery date.");
            return null;
        }

        int hour;
        int minute;
        try {
            hour = Integer.parseInt(hourField.getText().trim());
        } catch (NumberFormatException e) {
            scheduleErrorLabel.setText("Enter a valid hour (0–23).");
            return null;
        }
        try {
            minute = Integer.parseInt(minuteField.getText().trim());
        } catch (NumberFormatException e) {
            scheduleErrorLabel.setText("Enter a valid minute (0–59).");
            return null;
        }

        if (hour < 0 || hour > 23) {
            scheduleErrorLabel.setText("Hour must be between 0 and 23.");
            return null;
        }
        if (minute < 0 || minute > 59) {
            scheduleErrorLabel.setText("Minute must be between 0 and 59.");
            return null;
        }

        return LocalDateTime.of(date, LocalTime.of(hour, minute));
    }

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
        SessionManager.getInstance().setSelectedOrder(null);
        SceneManager.getInstance().switchTo("OrderTracking");
    }
}