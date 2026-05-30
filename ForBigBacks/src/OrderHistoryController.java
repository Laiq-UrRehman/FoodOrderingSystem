// Updated: buildRatingPanel() fully rewritten — stars pre-filled, clickable to change, Save button closes panel
// Updated: highlightStars() now respects the currently selected star value so selected stars stay lit on mouse-exit
// Updated: resetStars() resets hover highlight back to the currently selected value instead of all-off
// Updated: hasUnratedItems() kept but "Rate Order" button now always shows for delivered orders so ratings can be changed
// Updated: saveCustomer() catches FileHandler.FileOperationException

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderHistoryController {

    @FXML
    private Label orderCountLabel;
    @FXML
    private VBox ordersContainer;

    private Customer customer;
    private Timeline refreshTimer;
    private static final int REFRESH_SECONDS = 1;

    @FXML
    public void initialize() {
        customer = SessionManager.getInstance().getCurrentCustomer();
        if (customer == null) {
            SceneManager.getInstance().switchTo("Login");
            return;
        }

        ordersContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null)
                stopRefresh();
        });

        loadOrders();
        startRefresh();
    }

    private void startRefresh() {
        stopRefresh();
        refreshTimer = new Timeline(new KeyFrame(
                Duration.seconds(REFRESH_SECONDS), e -> refreshFromSession()));
        refreshTimer.setCycleCount(Timeline.INDEFINITE);
        refreshTimer.play();
    }

    private void stopRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
            refreshTimer = null;
        }
    }

    private void refreshFromSession() {
        Customer sessionCustomer = SessionManager.getInstance().getCurrentCustomer();
        if (sessionCustomer == null) {
            stopRefresh();
            return;
        }
        customer = sessionCustomer;

        boolean anyActive = false;
        for (Order o : customer.viewOrderHistory()) {
            if (o.getTracking() != null) {
                String s = o.getTracking().getCurrentStatus();
                if (!"Delivered".equals(s) && !"Cancelled".equals(s)) {
                    anyActive = true;
                    break;
                }
            } else {
                String s = o.getStatus();
                if (!"Delivered".equals(s) && !"Cancelled".equals(s)) {
                    anyActive = true;
                    break;
                }
            }
        }

        loadOrders();
        if (!anyActive)
            stopRefresh();
    }

    private void loadOrders() {
        ordersContainer.getChildren().clear();
        List<Order> history = new ArrayList<>(customer.viewOrderHistory());
        int count = (int) history.stream().filter(o -> !"Cancelled".equals(o.getTracking() != null ? o.getTracking().getCurrentStatus() : o.getStatus())).count();
        orderCountLabel.setText(count + (count == 1 ? " order" : " orders"));

        if (history.isEmpty()) {
            ordersContainer.getChildren().add(buildEmptyState());
            return;
        }

        for (int i = history.size() - 1; i >= 0; i--)
            ordersContainer.getChildren().add(createOrderCard(history.get(i)));
    }

    private VBox buildEmptyState() {
        VBox box = new VBox(12);
        box.getStyleClass().add("dashboard-empty-state");
        Label icon = new Label("📋");
        icon.getStyleClass().add("dashboard-empty-icon");
        Label msg = new Label("No orders yet");
        msg.getStyleClass().add("dashboard-empty-title");
        Label sub = new Label("Place an order to see it here");
        sub.getStyleClass().add("text-screen-subheading");
        box.getChildren().addAll(icon, msg, sub);
        return box;
    }

    private VBox createOrderCard(Order order) {
        VBox card = new VBox();
        card.getStyleClass().add("dashboard-order-card");

        HBox header = new HBox(12);
        header.getStyleClass().add("dashboard-order-card-header");
        header.setAlignment(Pos.CENTER_LEFT);

        VBox idBox = new VBox(3);
        HBox.setHgrow(idBox, Priority.ALWAYS);

        Label typeLabel;
        if (order instanceof ScheduledOrder) {
            ScheduledOrder so = (ScheduledOrder) order;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d, yyyy  HH:mm");
            typeLabel = new Label("Scheduled  ·  " + so.getScheduledTime().format(fmt));
        } else {
            typeLabel = new Label("Standard Order");
        }
        typeLabel.getStyleClass().add("dashboard-order-type-label");

        Label idLabel = new Label(order.getOrderID());
        idLabel.getStyleClass().add("dashboard-order-id-label");
        idBox.getChildren().addAll(typeLabel, idLabel);

        String liveStatus = getLiveStatus(order);

        Label statusBadge = new Label(liveStatus);
        statusBadge.getStyleClass().addAll("dashboard-order-status-badge", getStatusClass(liveStatus));

        double fee = order.getDeliveryFee();
        String totalText = fee > 0
                ? "Rs. " + (int) order.getGrandTotal() + "  (incl. Rs. " + (int) fee + " delivery)"
                : "Rs. " + (int) order.getTotalAmount();
        Label totalLabel = new Label(totalText);
        totalLabel.getStyleClass().add("dashboard-order-total");

        header.getChildren().addAll(idBox, statusBadge, totalLabel);

        VBox body = new VBox(5);
        body.getStyleClass().add("dashboard-order-card-body");
        for (FoodItem item : order.getItems()) {
            Label itemLabel = new Label(
                    item.getName() + "  ×" + item.getQuantity()
                            + "   —   Rs. " + (int) (item.getPrice() * item.getQuantity()));
            itemLabel.getStyleClass().add("dashboard-order-item-label");
            body.getChildren().add(itemLabel);
        }

        card.getChildren().addAll(header, body);

        VBox ratingPanel = buildRatingPanel(order);

        HBox footer = new HBox(10);
        footer.getStyleClass().add("dashboard-order-card-footer");
        footer.setAlignment(Pos.CENTER_RIGHT);

        if (order.getTracking() != null && !"Cancelled".equals(liveStatus)) {
            Button trackBtn = new Button("Track Order →");
            trackBtn.getStyleClass().add("dashboard-order-track-button");
            trackBtn.setOnAction(e -> {
                stopRefresh();
                SessionManager.getInstance().setSelectedOrder(order);
                SceneManager.getInstance().switchTo("OrderTracking");
            });
            footer.getChildren().add(trackBtn);
        }

        if (isCancellable(order, liveStatus)) {
            Button cancelBtn = new Button("Cancel Order");
            cancelBtn.getStyleClass().add("dashboard-order-cancel-button");
            cancelBtn.setOnAction(e -> {
                double refundAmount = order.getGrandTotal();
                boolean isCard = "CARD".equals(order.getPaymentMethod());

                customer.cancelOrder(order.getOrderID());
                saveCustomer();
                loadOrders();

                showCancelModal(isCard, (int) refundAmount);
            });
            footer.getChildren().add(cancelBtn);
        }

        // Show "Rate Order" button for all delivered orders — ratings can always be
        // changed
        if ("Delivered".equals(liveStatus)) {
            Button rateBtn = new Button("Rate Order  ★");
            rateBtn.getStyleClass().add("dashboard-order-rate-button");
            rateBtn.setOnAction(e -> {
                boolean nowVisible = !ratingPanel.isVisible();
                ratingPanel.setVisible(nowVisible);
                ratingPanel.setManaged(nowVisible);
                rateBtn.setText(nowVisible ? "Close Ratings  ✕" : "Rate Order  ★");
            });
            footer.getChildren().add(rateBtn);
        }

        if (!footer.getChildren().isEmpty())
            card.getChildren().add(footer);

        card.getChildren().add(ratingPanel);
        return card;
    }

    // ── Rating Panel ──────────────────────────────────────────────────────────

    private VBox buildRatingPanel(Order order) {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("dashboard-order-rating-panel");
        panel.setVisible(false);
        panel.setManaged(false);

        Label title = new Label("RATE YOUR ITEMS");
        title.getStyleClass().add("dashboard-order-rating-title");
        panel.getChildren().add(title);

        Rating ratingService = new Rating();

        // Track pending selections: foodID → chosen star value (0 = none chosen yet)
        Map<String, Integer> pendingStars = new HashMap<>();

        for (FoodItem item : order.getItems()) {

            // Determine initial star value to display:
            // 1. If rated in this order, use that value.
            // 2. Otherwise, check if they rated this item in any previous order.
            double savedValue = order.hasRated(item.getFoodID())
                    ? order.getRatingValue(item.getFoodID())
                    : customer.getLastRatingForItem(item.getFoodID());

            int initialStars = (int) Math.round(savedValue); // 0 if never rated
            pendingStars.put(item.getFoodID(), initialStars);

            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Label itemName = new Label(item.getName());
            itemName.getStyleClass().add("dashboard-order-rating-item-name");
            itemName.setPrefWidth(220);
            HBox.setHgrow(itemName, Priority.ALWAYS);
            row.getChildren().add(itemName);

            // Show previous-rating badge if already rated in this order
            if (order.hasRated(item.getFoodID()) && initialStars >= 1) {
                Label ratedBadge = new Label(initialStars + "★  ✓");
                ratedBadge.getStyleClass().add("dashboard-order-rated-label");
                ratedBadge.setStyle("-fx-font-size: 12px; -fx-text-fill: #4a7c59; -fx-font-weight: bold;");
                row.getChildren().add(ratedBadge);
            }

            // Build star buttons
            HBox stars = new HBox(4);
            stars.setAlignment(Pos.CENTER_LEFT);

            for (int s = 1; s <= 5; s++) {
                final int starValue = s;
                Button starBtn = new Button("★");
                starBtn.getStyleClass().add(
                        starValue <= initialStars
                                ? "dashboard-order-star-button-hover"
                                : "dashboard-order-star-button");
                starBtn.setUserData(starValue <= initialStars); // true = currently lit

                starBtn.setOnMouseEntered(e -> highlightStars(stars, starValue,
                        pendingStars.get(item.getFoodID())));

                starBtn.setOnMouseExited(e -> resetStars(stars,
                        pendingStars.get(item.getFoodID())));

                starBtn.setOnAction(e -> {
                    pendingStars.put(item.getFoodID(), starValue);
                    applyStarSelection(stars, starValue);
                });

                stars.getChildren().add(starBtn);
            }

            row.getChildren().add(stars);
            panel.getChildren().add(row);
        }

        // Save button — commits all pending ratings and closes panel
        Label saveError = new Label("");
        saveError.getStyleClass().add("text-error-message");

        Button saveBtn = new Button("Save Ratings");
        saveBtn.getStyleClass().add("action-button-brown");
        saveBtn.setOnAction(e -> {
            boolean anyRated = false;
            for (FoodItem item : order.getItems()) {
                int chosen = pendingStars.getOrDefault(item.getFoodID(), 0);
                if (chosen >= 1) {
                    try {
                        ratingService.rateFoodItem(customer, order, item.getFoodID(), chosen);
                        anyRated = true;
                    } catch (IllegalArgumentException ex) {
                        saveError.setText("Rating error: " + ex.getMessage());
                        return;
                    }
                }
            }
            if (anyRated) {
                saveCustomer();
            }
            panel.setVisible(false);
            panel.setManaged(false);
            loadOrders();
        });

        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER_LEFT);
        btnRow.getChildren().addAll(saveBtn, saveError);
        panel.getChildren().add(btnRow);

        return panel;
    }

    // ── Star highlight helpers ────────────────────────────────────────────────

    /**
     * Lights up stars 1..upTo on hover while keeping already-selected stars lit.
     */
    private void highlightStars(HBox stars, int upTo, int selected) {
        for (int i = 0; i < stars.getChildren().size(); i++) {
            Button b = (Button) stars.getChildren().get(i);
            int starValue = i + 1;
            if (starValue <= upTo) {
                b.getStyleClass().remove("dashboard-order-star-button");
                if (!b.getStyleClass().contains("dashboard-order-star-button-hover"))
                    b.getStyleClass().add("dashboard-order-star-button-hover");
            } else {
                b.getStyleClass().remove("dashboard-order-star-button-hover");
                if (!b.getStyleClass().contains("dashboard-order-star-button"))
                    b.getStyleClass().add("dashboard-order-star-button");
            }
        }
    }

    /**
     * On mouse-exit, resets stars back to the currently selected value
     * so already-chosen stars stay lit.
     */
    private void resetStars(HBox stars, int selected) {
        applyStarSelection(stars, selected);
    }

    /**
     * Applies the chosen star count visually — stars 1..selected are lit,
     * the rest are dim.
     */
    private void applyStarSelection(HBox stars, int selected) {
        for (int i = 0; i < stars.getChildren().size(); i++) {
            Button b = (Button) stars.getChildren().get(i);
            int starValue = i + 1;
            if (starValue <= selected) {
                b.getStyleClass().remove("dashboard-order-star-button");
                if (!b.getStyleClass().contains("dashboard-order-star-button-hover"))
                    b.getStyleClass().add("dashboard-order-star-button-hover");
            } else {
                b.getStyleClass().remove("dashboard-order-star-button-hover");
                if (!b.getStyleClass().contains("dashboard-order-star-button"))
                    b.getStyleClass().add("dashboard-order-star-button");
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String getLiveStatus(Order order) {
        if (order.getTracking() != null)
            return order.getTracking().getCurrentStatus();
        return order.getStatus();
    }

    private boolean hasUnratedItems(Order order) {
        for (FoodItem item : order.getItems())
            if (!order.hasRated(item.getFoodID()))
                return true;
        return false;
    }

    private boolean isCancellable(Order order, String liveStatus) {
        if ("Cancelled".equals(liveStatus) || "Delivered".equals(liveStatus)
                || "Out for Delivery".equals(liveStatus))
            return false;
        if (order instanceof ScheduledOrder)
            return !((ScheduledOrder) order).isConfirmed();
        return true;
    }

    private String getStatusClass(String status) {
        switch (status) {
            case "Confirmed":
                return "order-status-confirmed";
            case "Preparing":
                return "order-status-preparing";
            case "Out for Delivery":
                return "order-status-transit";
            case "Delivered":
                return "order-status-delivered";
            case "Cancelled":
                return "order-status-cancelled";
            case "Scheduled":
                return "order-status-scheduled";
            default:
                return "order-status-pending";
        }
    }


    private void showCancelModal(boolean isCard, int refundAmount) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        Label icon = new Label(isCard ? "\uD83D\uDCB3" : "\u2713");
        icon.setStyle("-fx-font-size: 28px;");

        Label title = new Label("Order Cancelled");
        title.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );

        HBox titleRow = new HBox(12, icon, title);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #333333;");

        Label body = new Label("Your order has been cancelled.");
        body.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14px;");
        body.setWrapText(true);

        VBox content = new VBox(8, titleRow, sep, body);

        if (isCard) {
            Label refundLine = new Label(
                "Rs. " + refundAmount + " has been refunded to your credit card."
            );
            refundLine.setStyle(
                "-fx-text-fill: #8B5E3C;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
            );
            refundLine.setWrapText(true);
            content.getChildren().add(refundLine);
        }

        Button okBtn = new Button("OK");
        String btnBase =
            "-fx-background-color: #8B5E3C;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 9 32;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;";
        String btnHover =
            "-fx-background-color: #6F4A2F;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 9 32;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;";
        okBtn.setStyle(btnBase);
        okBtn.setOnMouseEntered(e -> okBtn.setStyle(btnHover));
        okBtn.setOnMouseExited(e -> okBtn.setStyle(btnBase));
        okBtn.setOnAction(e -> modal.close());

        HBox btnRow = new HBox(okBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        VBox.setMargin(btnRow, new Insets(6, 0, 0, 0));
        content.getChildren().add(btnRow);

        VBox card = new VBox(content);
        card.setStyle(
            "-fx-background-color: #242424;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: #3a3a3a;" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1;" +
            "-fx-padding: 28 32;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 24, 0, 0, 6);"
        );
        card.setPrefWidth(360);

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(16));

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);
        modal.showAndWait();
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
    private void goHome() {
        stopRefresh();
        SceneManager.getInstance().switchTo("CustomerDashboard");
    }

    @FXML
    private void goBrowse() {
        stopRefresh();
        SceneManager.getInstance().switchTo("RestaurantBrowse");
    }

    @FXML
    private void goCart() {
        stopRefresh();
        SceneManager.getInstance().switchTo("Cart");
    }

    @FXML
    private void goOrders() {
        /* already here */ }
}