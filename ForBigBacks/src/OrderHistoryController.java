import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.util.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryController {

    @FXML
    private Label orderCountLabel;
    @FXML
    private VBox ordersContainer;

    private Customer customer;
    private Timeline refreshTimer;
    private static final int REFRESH_SECONDS = 2;

    @FXML
    public void initialize() {
        customer = SessionManager.getInstance().getCurrentCustomer();
        if (customer == null) {
            SceneManager.getInstance().switchTo("Login");
            return;
        }

        // Stop refresh when scene is torn down
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

    // Read directly from the session customer — it holds the live timer object.
    // No disk read here, so no deserialization, no duplicate timers.
    private void refreshFromSession() {
        Customer sessionCustomer = SessionManager.getInstance().getCurrentCustomer();
        if (sessionCustomer == null) {
            stopRefresh();
            return;
        }
        customer = sessionCustomer;

        boolean anyActive = false;
        for (Order o : customer.viewOrderHistory()) {
            String s = o.getStatus();
            if (!"Delivered".equals(s) && !"Cancelled".equals(s)) {
                anyActive = true;
                break;
            }
        }

        loadOrders();
        if (!anyActive)
            stopRefresh();
    }

    private void loadOrders() {
        ordersContainer.getChildren().clear();

        List<Order> history = new ArrayList<>(customer.viewOrderHistory());
        int count = history.size();
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

        Label statusBadge = new Label(order.getStatus());
        statusBadge.getStyleClass().addAll("dashboard-order-status-badge",
                getStatusClass(order.getStatus()));

        Label totalLabel = new Label("Rs. " + (int) order.getTotalAmount());
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

        if (order.getTracking() != null && !order.getStatus().equals("Cancelled")) {
            Button trackBtn = new Button("Track Order →");
            trackBtn.getStyleClass().add("dashboard-order-track-button");
            trackBtn.setOnAction(e -> {
                stopRefresh();
                SessionManager.getInstance().setSelectedOrder(order);
                SceneManager.getInstance().switchTo("OrderTracking");
            });
            footer.getChildren().add(trackBtn);
        }

        if (isCancellable(order)) {
            Button cancelBtn = new Button("Cancel Order");
            cancelBtn.getStyleClass().add("dashboard-order-cancel-button");
            cancelBtn.setOnAction(e -> {
                customer.cancelOrder(order.getOrderID());
                saveCustomer();
                loadOrders();
            });
            footer.getChildren().add(cancelBtn);
        }

        if (order.getStatus().equals("Delivered") && hasUnratedItems(order)) {
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

    private VBox buildRatingPanel(Order order) {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("dashboard-order-rating-panel");
        panel.setVisible(false);
        panel.setManaged(false);

        Label title = new Label("RATE YOUR ITEMS");
        title.getStyleClass().add("dashboard-order-rating-title");
        panel.getChildren().add(title);

        Rating ratingService = new Rating();

        for (FoodItem item : order.getItems()) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Label itemName = new Label(item.getName());
            itemName.getStyleClass().add("dashboard-order-rating-item-name");
            itemName.setPrefWidth(220);
            HBox.setHgrow(itemName, Priority.ALWAYS);
            row.getChildren().add(itemName);

            if (order.hasRated(item.getFoodID())) {
                Label ratedLabel = new Label("Rated  ✓");
                ratedLabel.getStyleClass().add("dashboard-order-rated-label");
                row.getChildren().add(ratedLabel);
            } else {
                HBox stars = new HBox(4);
                stars.setAlignment(Pos.CENTER_LEFT);
                for (int s = 1; s <= 5; s++) {
                    final int starValue = s;
                    Button starBtn = new Button("★");
                    starBtn.getStyleClass().add("dashboard-order-star-button");
                    starBtn.setOnMouseEntered(e -> highlightStars(stars, starValue));
                    starBtn.setOnMouseExited(e -> resetStars(stars));
                    starBtn.setOnAction(e -> {
                        try {
                            ratingService.rateFoodItem(customer, order,
                                    item.getFoodID(), starValue);
                        } catch (IllegalArgumentException ex) {
                            System.out.println("Rating error: " + ex.getMessage());
                        }
                        saveCustomer();
                        loadOrders();
                    });
                    stars.getChildren().add(starBtn);
                }
                row.getChildren().add(stars);
            }
            panel.getChildren().add(row);
        }
        return panel;
    }

    private void highlightStars(HBox stars, int upTo) {
        for (int i = 0; i < stars.getChildren().size(); i++) {
            Button b = (Button) stars.getChildren().get(i);
            if (i < upTo) {
                b.getStyleClass().remove("dashboard-order-star-button");
                b.getStyleClass().add("dashboard-order-star-button-hover");
            } else {
                b.getStyleClass().remove("dashboard-order-star-button-hover");
                b.getStyleClass().add("dashboard-order-star-button");
            }
        }
    }

    private void resetStars(HBox stars) {
        for (javafx.scene.Node n : stars.getChildren()) {
            Button b = (Button) n;
            b.getStyleClass().remove("dashboard-order-star-button-hover");
            if (!b.getStyleClass().contains("dashboard-order-star-button"))
                b.getStyleClass().add("dashboard-order-star-button");
        }
    }

    private boolean hasUnratedItems(Order order) {
        for (FoodItem item : order.getItems())
            if (!order.hasRated(item.getFoodID()))
                return true;
        return false;
    }

    private boolean isCancellable(Order order) {
        String s = order.getStatus();
        if (s.equals("Cancelled") || s.equals("Delivered")
                || s.equals("Out for Delivery"))
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
    }
}