// Updated: saveCustomer() now catches FileHandler.FileOperationException
// Updated: placeScheduledOrder() catches IllegalStateException from Cart.checkOutScheduled()

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    private void loadOffers() {
        List<LoyaltyOffer> offers = customer.getLoyaltyPoints().getAvailableOffers(cart.getTotal());

        offersContainer.getChildren().clear();

        if (offers.isEmpty()) {
            offersStatusLabel.setText("No offers available. Keep ordering to earn more points!");
            return;
        }

        offersStatusLabel.setText("Select an offer to apply:");

        ToggleGroup group = new ToggleGroup();

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

    private boolean validateCardFields() {
        String cardNumber = cardNumberField.getText().trim();
        String cardHolder = cardHolderField.getText().trim();
        String expiry = expiryField.getText().trim();

        if (cardNumber.isEmpty() || cardHolder.isEmpty() || expiry.isEmpty()) {
            errorLabel.setText("Please fill in all card details.");
            return false;
        }
        if (!cardNumber.matches("\\d{16}")) {
            errorLabel.setText("Card number must be exactly 16 digits.");
            return false;
        }
        if (!cardHolder.matches("[a-zA-Z ]{2,50}")) {
            errorLabel.setText("Card holder name must be letters only.");
            return false;
        }
        if (!expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            errorLabel.setText("Expiry must be in MM/YY format (e.g. 08/27).");
            return false;
        }
        try {
            java.time.YearMonth cardExpiry = java.time.YearMonth.parse(
                    expiry, java.time.format.DateTimeFormatter.ofPattern("MM/yy"));
            if (cardExpiry.isBefore(java.time.YearMonth.now())) {
                errorLabel.setText("This card has expired.");
                return false;
            }
        } catch (java.time.format.DateTimeParseException e) {
            errorLabel.setText("Expiry must be in MM/YY format (e.g. 08/27).");
            return false;
        }
        return true;
    }

    @FXML
    private void placeScheduledOrder() {
        scheduleErrorLabel.setText("");
        errorLabel.setText("");

        LocalDateTime scheduledTime = parseScheduledTime();
        if (scheduledTime == null)
            return;

        long minutesAhead = java.time.Duration.between(
                LocalDateTime.now(), scheduledTime).toMinutes();
        if (minutesAhead < 30) {
            scheduleErrorLabel.setText("Scheduled time must be at least 30 minutes from now.");
            return;
        }

        if (isCardPayment && !validateCardFields())
            return;

        ScheduledOrder order;
        try {
            if (selectedOffer != null) {
                RedeemCode code = cart.selectOffer(customer, selectedOffer);
                if (code == null) {
                    errorLabel.setText("Could not generate redeem code. Check your points balance.");
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
        } catch (IllegalStateException e) {
            scheduleErrorLabel.setText(e.getMessage());
            return;
        }

        if (order == null) {
            scheduleErrorLabel.setText("Could not schedule order. Please check the time and try again.");
            return;
        }

        customer.placeOrder(order);
        saveCustomer();

        SceneManager.getInstance().switchTo("OrderHistory");
    }

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
}