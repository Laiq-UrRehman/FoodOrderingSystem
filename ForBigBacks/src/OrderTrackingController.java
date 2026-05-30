
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.util.Duration;
import java.util.List;

public class OrderTrackingController {

    @FXML
    private VBox trackingContent;

    private Customer customer;
    private Order order;
    private OrderTracking tracking;

    private boolean isTrackingMode = false;

    private Timeline autoRefresh;
    private static final int REFRESH_SECONDS = 1; // tick every second for smooth progress

    @FXML
    public void initialize() {
        customer = SessionManager.getInstance().getCurrentCustomer();
        if (customer == null) {
            SceneManager.getInstance().switchTo("Login");
            return;
        }

        trackingContent.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null)
                stopAutoRefresh();
        });

        order = SessionManager.getInstance().getSelectedOrder();
        if (order == null) {
            order = findMostRecentTrackedOrder();
        }

        if (order == null || order.getTracking() == null) {
            buildEmptyState();
            return;
        }

        tracking = order.getTracking();
        buildTrackingUI();
        startAutoRefresh();
    }

    private void startAutoRefresh() {
        stopAutoRefresh();
        autoRefresh = new Timeline(new KeyFrame(
                Duration.seconds(REFRESH_SECONDS), e -> refreshStatus()));
        autoRefresh.setCycleCount(Timeline.INDEFINITE);
        autoRefresh.play();
    }

    private void stopAutoRefresh() {
        if (autoRefresh != null) {
            autoRefresh.stop();
            autoRefresh = null;
        }
    }

    private void refreshStatus() {
        if (!isTrackingMode || tracking == null)
            return;
        if (trackingContent.getChildren().isEmpty())
            return;

        try {
            // getCurrentStatus() always recomputes from startTime — works after re-login
            String status = tracking.getCurrentStatus();
            trackingContent.getChildren().set(0, buildStatusSection());

            if ("Delivered".equals(status) || "Cancelled".equals(status)) {
                stopAutoRefresh();
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not refresh tracking status: " + e.getMessage());
        }
    }

    private void buildTrackingUI() {
        isTrackingMode = true;
        trackingContent.getChildren().clear();
        trackingContent.getChildren().addAll(
                buildStatusSection(),
                buildInfoRow());
    }

    private VBox buildStatusSection() {
        VBox section = new VBox(16);

        Label sectionTitle = new Label("DELIVERY STATUS");
        sectionTitle.getStyleClass().add("dashboard-section-title");

        // Always call getCurrentStatus() — it recomputes from elapsed time every call
        String current = tracking.getCurrentStatus();

        Label bigStatus = new Label(current);
        bigStatus.getStyleClass().add("dashboard-tracking-big-status");

        Label etaLabel = new Label(buildEtaText(current, tracking));
        etaLabel.getStyleClass().add("dashboard-tracking-eta");

        String[] stages = { "Confirmed", "Preparing", "Out for Delivery", "Delivered" };
        int activeIdx = stageIndex(current, stages);

        HBox stepRow = new HBox();
        stepRow.setAlignment(Pos.CENTER_LEFT);

        for (int i = 0; i < stages.length; i++) {
            boolean done = i < activeIdx;
            boolean active = i == activeIdx;

            VBox step = new VBox(6);
            step.setAlignment(Pos.CENTER);
            step.setPrefWidth(180);

            Label circle = new Label(done ? "✓" : String.valueOf(i + 1));
            if (done)
                circle.getStyleClass().add("dashboard-step-circle-done");
            else if (active)
                circle.getStyleClass().add("dashboard-step-circle-active");
            else
                circle.getStyleClass().add("dashboard-step-circle-idle");

            Label stepLabel = new Label(stages[i]);
            if (active)
                stepLabel.getStyleClass().add("dashboard-step-label-active");
            else if (done)
                stepLabel.getStyleClass().add("dashboard-step-label-done");
            else
                stepLabel.getStyleClass().add("dashboard-step-label-idle");

            step.getChildren().addAll(circle, stepLabel);
            stepRow.getChildren().add(step);

            if (i < stages.length - 1) {
                Region line = new Region();
                line.setPrefWidth(40);
                line.setPrefHeight(2);
                line.setMaxHeight(2);
                if (i < activeIdx)
                    line.getStyleClass().add("dashboard-step-connector-done");
                else
                    line.getStyleClass().add("dashboard-step-connector-idle");
                stepRow.getChildren().add(line);
            }
        }

        // Progress bar showing time elapsed within current phase
        HBox progressRow = buildProgressBar(current);

        section.getChildren().addAll(sectionTitle, bigStatus, etaLabel, stepRow, progressRow);
        return section;
    }

    /**
     * Shows a live time-remaining label derived purely from elapsed time.
     * Works after re-login because OrderTracking.getCurrentStatus() and
     * getElapsedMs() both compute from the serialized startTime field.
     */
    private String buildEtaText(String status, OrderTracking t) {
        long elapsed = t.getElapsedMs();
        long prepMs = t.getPrepMs();
        long delivMs = t.getDeliveryMs();

        switch (status) {
            case "Confirmed":
                return "Order confirmed — preparing will begin shortly";
            case "Preparing": {
                long remainingMins = Math.max(0, (prepMs - elapsed) / 1000);
                return "Preparing your food — ready in ~" + remainingMins + " minutes";
            }
            case "Out for Delivery": {
                long remainingMins = Math.max(0, (delivMs - elapsed) / 1000);
                return "Your rider is on the way! Arriving in ~" + remainingMins + " minutes";
            }
            case "Delivered":
                return "Your order has been delivered ✓";
            case "Cancelled":
                return "This order was cancelled";
            default:
                return "";
        }
    }

    /** A simple elapsed-time progress bar. */
    private HBox buildProgressBar(String status) {
        HBox row = new HBox();
        row.setMaxWidth(Double.MAX_VALUE);

        if ("Delivered".equals(status) || "Cancelled".equals(status))
            return row;

        long elapsed = tracking.getElapsedMs();
        long total = tracking.getDeliveryMs();
        double pct = total > 0 ? Math.min(1.0, (double) elapsed / total) : 0;

        Region fill = new Region();
        fill.setPrefHeight(4);
        fill.setPrefWidth(pct * 720); // approximate max width
        fill.setStyle("-fx-background-color: #8B5E3C; -fx-background-radius: 2;");

        Region track = new Region();
        track.setPrefHeight(4);
        HBox.setHgrow(track, Priority.ALWAYS);
        track.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 2;");

        HBox bar = new HBox();
        bar.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(bar, Priority.ALWAYS);
        bar.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 2; -fx-pref-height: 4;");
        bar.getChildren().add(fill);

        row.getChildren().add(bar);
        return row;
    }

    private HBox buildInfoRow() {
        HBox row = new HBox(20);
        row.getChildren().addAll(
                buildOrderDetailsCard(),
                buildRiderCard(),
                buildDeliveryCard());
        return row;
    }

    private VBox buildOrderDetailsCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("dashboard-tracking-card");
        HBox.setHgrow(card, Priority.ALWAYS);
        card.getChildren().add(makeCardTitle("ORDER DETAILS"));
        card.getChildren().add(makeDivider());
        card.getChildren().add(makeInfoRow("Order ID: ", order.getOrderID()));
        card.getChildren().add(makeInfoRow("Food Total: ", 
                "Rs. " + (int) order.getTotalAmount()));
        card.getChildren().add(makeInfoRow("Delivery Fee: ", 
                "Rs. " + (int) order.getDeliveryFee()));
        card.getChildren().add(makeInfoRow("Grand Total: ", 
                "Rs. " + (int) order.getGrandTotal()));
        card.getChildren().add(makeDivider());
        card.getChildren().add(makeCardTitle("ITEMS"));
        for (FoodItem item : order.getItems()) {
            Label itemLabel = new Label(item.getName() + "  ×" + item.getQuantity()
                    + "   —   Rs. " + (int) (item.getPrice() * item.getQuantity()));
            itemLabel.getStyleClass().add("dashboard-tracking-item");
            card.getChildren().add(itemLabel);
        }
        return card;
    }

    private VBox buildRiderCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("dashboard-tracking-card");
        card.setPrefWidth(240);
        card.getChildren().add(makeCardTitle("YOUR RIDER"));
        card.getChildren().add(makeDivider());
        Rider rider = tracking.getAssignedRider();
        if (rider == null) {
            Label none = new Label("Assigning a rider...");
            none.getStyleClass().add("dashboard-tracking-label");
            card.getChildren().add(none);
        } else {
            boolean isBike = rider.getVehicleType() != null
                    && rider.getVehicleType().toLowerCase().contains("bike");
            Label emoji = new Label(isBike ? "🛵" : "🚗");
            emoji.getStyleClass().add("dashboard-tracking-rider-emoji");
            card.getChildren().add(emoji);
            card.getChildren().add(makeInfoRow("Name: ", rider.getName()));
            card.getChildren().add(makeInfoRow("Vehicle: ", rider.getVehicleType()));
            card.getChildren().add(makeInfoRow("Rider → Restaurant: ",
                    String.format("%.1f km", tracking.getDistanceRiderToRestaurant())));
        }
        return card;
    }

    private VBox buildDeliveryCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("dashboard-tracking-card");
        card.setPrefWidth(240);
        card.getChildren().add(makeCardTitle("DELIVERY INFO"));
        card.getChildren().add(makeDivider());
        card.getChildren().add(makeInfoRow("Tracking ID: ", tracking.getTrackingID()));
        card.getChildren().add(makeInfoRow("Restaurant → You: ",
                String.format("%.1f km", tracking.getDistanceRestaurantToCustomer())));
        card.getChildren().add(makeInfoRow("Total ETA: ",
                tracking.getEstimatedDeliveryMinutes() + " mins"));
        card.getChildren().add(makeInfoRow("Delivery Fee: ",
                "Rs. " + (int) order.getDeliveryFee()));
        return card;
    }

    private void buildEmptyState() {
        isTrackingMode = false;
        trackingContent.getChildren().clear();
        VBox box = new VBox(12);
        box.getStyleClass().add("dashboard-empty-state");
        Label icon = new Label("📍");
        icon.getStyleClass().add("dashboard-empty-icon");
        Label msg = new Label("No active orders to track");
        msg.getStyleClass().add("dashboard-empty-title");
        Label sub = new Label("Place an order and come back here to track its delivery");
        sub.getStyleClass().add("text-screen-subheading");
        Button ordersBtn = new Button("View Order History");
        ordersBtn.getStyleClass().add("action-button-outline");
        ordersBtn.setOnAction(e -> {
            stopAutoRefresh();
            SceneManager.getInstance().switchTo("OrderHistory");
        });
        box.getChildren().addAll(icon, msg, sub, ordersBtn);
        trackingContent.getChildren().add(box);
    }

    private int stageIndex(String status, String[] stages) {
        for (int i = 0; i < stages.length; i++) {
            if (stages[i].equals(status))
                return i;
        }
        return 0;
    }

    private Order findMostRecentTrackedOrder() {
        List<Order> history = customer.viewOrderHistory();
        for (int i = history.size() - 1; i >= 0; i--) {
            Order o = history.get(i);
            if (o.getTracking() != null
                    && !"Delivered".equals(o.getStatus())
                    && !"Cancelled".equals(o.getStatus())) {
                return o;
            }
        }
        // Also return delivered orders if they have tracking (for viewing history)
        for (int i = history.size() - 1; i >= 0; i--) {
            if (history.get(i).getTracking() != null)
                return history.get(i);
        }
        return null;
    }

    private Label makeCardTitle(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("dashboard-section-title");
        return l;
    }

    private HBox makeInfoRow(String labelText, String valueText) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(labelText);
        label.getStyleClass().add("dashboard-tracking-label");
        HBox.setHgrow(label, Priority.ALWAYS);
        Label value = new Label(valueText);
        value.getStyleClass().add("dashboard-tracking-value");
        row.getChildren().addAll(label, value);
        return row;
    }

    private Region makeDivider() {
        Region div = new Region();
        div.getStyleClass().add("dashboard-tracking-divider");
        return div;
    }

    @FXML
    private void goBack() {
        stopAutoRefresh();
        SceneManager.getInstance().switchTo("OrderHistory");
    }

    @FXML
    private void goHome() {
        stopAutoRefresh();
        SceneManager.getInstance().switchTo("CustomerDashboard");
    }

    @FXML
    private void goBrowse() {
        stopAutoRefresh();
        SceneManager.getInstance().switchTo("RestaurantBrowse");
    }

    @FXML
    private void goCart() {
        stopAutoRefresh();
        SceneManager.getInstance().switchTo("Cart");
    }

    @FXML
    private void goOrders() {
        stopAutoRefresh();
        SceneManager.getInstance().switchTo("OrderHistory");
    }
}