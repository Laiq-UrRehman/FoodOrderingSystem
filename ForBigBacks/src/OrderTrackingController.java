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

    // Auto-refresh every 2 seconds on the JavaFX thread — safe, no threading issues
    private Timeline autoRefresh;
    private static final int REFRESH_SECONDS = 2;

    @FXML
    public void initialize() {
        customer = SessionManager.getInstance().getCurrentCustomer();
        if (customer == null) {
            SceneManager.getInstance().switchTo("Login");
            return;
        }

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

    // ── Auto Refresh ──────────────────────────────────────────────────────────

    private void startAutoRefresh() {
        stopAutoRefresh(); // cancel any previous timeline

        autoRefresh = new Timeline(new KeyFrame(
                Duration.seconds(REFRESH_SECONDS),
                e -> refreshStatus()));
        autoRefresh.setCycleCount(Timeline.INDEFINITE);
        autoRefresh.play();
    }

    private void stopAutoRefresh() {
        if (autoRefresh != null) {
            autoRefresh.stop();
            autoRefresh = null;
        }
    }

    /**
     * Called every REFRESH_SECONDS by the Timeline — runs on the JavaFX thread.
     * Only rebuilds the status section (top part) so cards don't flicker.
     */
    private void refreshStatus() {
        if (tracking == null)
            return;

        // Replace the status section (always index 0 in trackingContent)
        if (!trackingContent.getChildren().isEmpty()) {
            trackingContent.getChildren().set(0, buildStatusSection());
        }

        // Stop the timer once the order is in a terminal state
        String status = tracking.getCurrentStatus();
        if ("Delivered".equals(status) || "Cancelled".equals(status)) {
            stopAutoRefresh();
        }
    }

    // ── Main UI ───────────────────────────────────────────────────────────────

    private void buildTrackingUI() {
        trackingContent.getChildren().clear();
        trackingContent.getChildren().addAll(
                buildStatusSection(), // index 0 — replaced by refreshStatus()
                buildInfoRow() // index 1 — static, never changes
        );
    }

    // ── Status Section ────────────────────────────────────────────────────────

    private VBox buildStatusSection() {
        VBox section = new VBox(16);

        Label sectionTitle = new Label("DELIVERY STATUS");
        sectionTitle.getStyleClass().add("dashboard-section-title");

        String current = tracking.getCurrentStatus();
        Label bigStatus = new Label(current);
        bigStatus.getStyleClass().add("dashboard-tracking-big-status");

        // ETA countdown label
        int etaMins = tracking.getEstimatedDeliveryMinutes();
        Label etaLabel = new Label(buildEtaText(current, etaMins));
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

        section.getChildren().addAll(sectionTitle, bigStatus, etaLabel, stepRow);
        return section;
    }

    private String buildEtaText(String status, int etaMins) {
        switch (status) {
            case "Confirmed":
                return "Your order has been received";
            case "Preparing":
                return "Estimated delivery in " + etaMins + " minutes";
            case "Out for Delivery":
                return "Your rider is on the way!";
            case "Delivered":
                return "Your order has been delivered ✓";
            case "Cancelled":
                return "This order was cancelled";
            default:
                return "";
        }
    }

    // ── Info Row ──────────────────────────────────────────────────────────────

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
        card.getChildren().add(makeInfoRow("Total: ", "Rs. " + (int) order.getTotalAmount()));
        card.getChildren().add(makeDivider());
        card.getChildren().add(makeCardTitle("ITEMS"));

        for (FoodItem item : order.getItems()) {
            Label itemLabel = new Label(
                    item.getName() + "  ×" + item.getQuantity()
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
                    String.format("%.1f units", tracking.getDistanceRiderToRestaurant())));
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
                String.format("%.1f units", tracking.getDistanceRestaurantToCustomer())));
        card.getChildren().add(makeInfoRow("Total ETA: ",
                tracking.getEstimatedDeliveryMinutes() + " mins"));

        return card;
    }

    // ── Empty State ───────────────────────────────────────────────────────────

    private void buildEmptyState() {
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

    // ── Helpers ───────────────────────────────────────────────────────────────

    private int stageIndex(String status, String[] stages) {
        for (int i = 0; i < stages.length; i++) {
            if (stages[i].equals(status))
                return i;
        }
        return 0;
    }

    private Order findMostRecentTrackedOrder() {
        List<Order> history = customer.viewOrderHistory();
        // Only return an order that is actively in progress
        for (int i = history.size() - 1; i >= 0; i--) {
            Order o = history.get(i);
            if (o.getTracking() != null
                    && !o.getStatus().equals("Delivered")
                    && !o.getStatus().equals("Cancelled")) {
                return o;
            }
        }
        // No active order — show empty state
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

    // ── Navigation — always stop the timeline before leaving ──────────────────

    @FXML
    private void refresh() {
        if (order != null && tracking != null) {
            refreshStatus();
        } else {
            order = findMostRecentTrackedOrder();
            if (order != null && order.getTracking() != null) {
                tracking = order.getTracking();
                buildTrackingUI();
                startAutoRefresh();
            } else {
                buildEmptyState();
            }
        }
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
        SceneManager.getInstance().switchTo("CustomerDashboard");
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

    @FXML
    private void goTracking() {
        /* already here */ }
}