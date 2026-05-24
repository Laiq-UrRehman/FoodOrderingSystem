// Updated: addToCart() now catches IllegalArgumentException from Cart.addItem()

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        for (FoodItem item : restaurant.getMenu().getItems())
            menuContainer.getChildren().add(createMenuItemRow(item));
    }

    private VBox createMenuItemRow(FoodItem item) {
        VBox wrapper = new VBox(0);

        HBox row = new HBox(12);
        row.getStyleClass().add("dashboard-menu-item-row");
        row.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(3);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label nameLabel = new Label(item.getName());
        nameLabel.getStyleClass().add("dashboard-menu-item-name");

        Label categoryLabel = new Label(item.getCategory());
        categoryLabel.getStyleClass().add("dashboard-menu-item-category");

        Label itemRatingLabel = new Label(String.format("★ %.1f  ·  %d orders",
                item.getRating(), item.getOrderCount()));
        itemRatingLabel.getStyleClass().add("dashboard-menu-item-rating");

        info.getChildren().addAll(nameLabel, categoryLabel, itemRatingLabel);

        Label priceLabel = new Label("Rs. " + (int) item.getPrice());
        priceLabel.getStyleClass().add("dashboard-menu-item-price");
        priceLabel.setMinWidth(90);

        Spinner<Integer> qtySpinner = new Spinner<>(1, 10, 1);
        qtySpinner.setPrefWidth(75);
        qtySpinner.setStyle(
                "-fx-background-color: #2a2a2a;" +
                        "-fx-border-color: #444444;" +
                        "-fx-border-radius: 6;");

        boolean hasGroups = !item.getCustomizationGroups().isEmpty();

        if (hasGroups) {
            Button customizeBtn = new Button("Customize & Add");
            customizeBtn.getStyleClass().add("dashboard-add-button");

            VBox customPanel = buildCustomizationSelector(item, qtySpinner, priceLabel, customizeBtn);
            customPanel.setVisible(false);
            customPanel.setManaged(false);

            customizeBtn.setOnAction(e -> {
                boolean show = !customPanel.isVisible();
                customPanel.setVisible(show);
                customPanel.setManaged(show);
                if (show) {
                    customizeBtn.setText("Cancel  ✕");
                    customizeBtn.setStyle(
                            "-fx-background-color: #2a2a2a; -fx-text-fill: #888888;" +
                                    "-fx-font-size: 13px; -fx-font-weight: bold;" +
                                    "-fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
                } else {
                    customizeBtn.setText("Customize & Add");
                    customizeBtn.setStyle("");
                    customizeBtn.getStyleClass().setAll("dashboard-add-button");
                    priceLabel.setText("Rs. " + (int) item.getPrice());
                }
            });

            row.getChildren().addAll(info, priceLabel, qtySpinner, customizeBtn);
            wrapper.getChildren().addAll(row, customPanel);

        } else {
            Button addBtn = new Button("Add to Cart");
            addBtn.getStyleClass().add("dashboard-add-button");
            addBtn.setOnAction(e -> addToCart(item, qtySpinner.getValue(), null, addBtn, item.getPrice()));

            row.getChildren().addAll(info, priceLabel, qtySpinner, addBtn);
            wrapper.getChildren().add(row);
        }

        return wrapper;
    }

    private VBox buildCustomizationSelector(FoodItem item, Spinner<Integer> qtySpinner,
            Label priceLabel, Button triggerBtn) {

        VBox panel = new VBox(14);
        panel.getStyleClass().add("menu-customization-panel");

        Map<String, String> selections = new LinkedHashMap<>();
        Map<String, Double> extraCharges = new HashMap<>();

        for (CustomizationGroup group : item.getCustomizationGroups()) {
            selections.put(group.getGroupName(), null);
            extraCharges.put(group.getGroupName(), 0.0);
        }

        Label confirmError = new Label("");
        confirmError.getStyleClass().add("text-error-message");

        for (CustomizationGroup group : item.getCustomizationGroups()) {
            VBox groupSection = new VBox(8);

            Label groupLabel = new Label(group.getGroupName().toUpperCase());
            groupLabel.getStyleClass().add("menu-customization-group-label");

            FlowPane optionsFlow = new FlowPane(8, 8);
            optionsFlow.setAlignment(Pos.CENTER_LEFT);

            List<Button> optionBtns = new ArrayList<>();

            for (int i = 0; i < group.getOptions().size(); i++) {
                final int idx = i;
                final String optName = group.getOptions().get(i);
                final double charge = group.getExtraCharges().get(i);

                String btnLabel = optName + (charge > 0 ? "  +Rs." + (int) charge : "  free");

                Button optBtn = new Button(btnLabel);
                optBtn.getStyleClass().add("menu-customization-option-btn");

                optBtn.setOnAction(e -> {
                    for (Button b : optionBtns) {
                        b.getStyleClass().remove("menu-customization-option-btn-selected");
                        if (!b.getStyleClass().contains("menu-customization-option-btn"))
                            b.getStyleClass().add("menu-customization-option-btn");
                    }
                    optBtn.getStyleClass().remove("menu-customization-option-btn");
                    optBtn.getStyleClass().add("menu-customization-option-btn-selected");

                    selections.put(group.getGroupName(), optName);
                    extraCharges.put(group.getGroupName(), charge);

                    double totalExtra = extraCharges.values().stream()
                            .mapToDouble(Double::doubleValue).sum();
                    priceLabel.setText("Rs. " + (int) (item.getPrice() + totalExtra));

                    confirmError.setText("");
                });

                optionBtns.add(optBtn);
                optionsFlow.getChildren().add(optBtn);
            }

            groupSection.getChildren().addAll(groupLabel, optionsFlow);
            panel.getChildren().add(groupSection);
        }

        Button confirmBtn = new Button("Confirm & Add to Cart");
        confirmBtn.getStyleClass().add("menu-customization-confirm-btn");

        confirmBtn.setOnAction(e -> {
            for (CustomizationGroup group : item.getCustomizationGroups()) {
                if (selections.get(group.getGroupName()) == null) {
                    confirmError.setText("Please select an option for: " + group.getGroupName());
                    return;
                }
            }

            double totalExtra = extraCharges.values().stream()
                    .mapToDouble(Double::doubleValue).sum();
            double finalPrice = item.getPrice() + totalExtra;

            addToCart(item, qtySpinner.getValue(), selections, triggerBtn, finalPrice);

            panel.setVisible(false);
            panel.setManaged(false);
            priceLabel.setText("Rs. " + (int) item.getPrice());
            triggerBtn.setText("Customize & Add");
            triggerBtn.setStyle("");
            triggerBtn.getStyleClass().setAll("dashboard-add-button");
        });

        HBox confirmRow = new HBox(12);
        confirmRow.setAlignment(Pos.CENTER_LEFT);
        confirmRow.getChildren().addAll(confirmBtn, confirmError);
        panel.getChildren().add(confirmRow);

        return panel;
    }

    private void addToCart(FoodItem item, int qty,
            Map<String, String> selectedCustomizations,
            Button feedbackBtn, double finalPrice) {

        try {
            FoodItem toAdd = new FoodItem(
                    item.getFoodID(), item.getName(),
                    finalPrice, item.getCategory(), qty);

            if (selectedCustomizations != null) {
                for (Map.Entry<String, String> entry : selectedCustomizations.entrySet())
                    toAdd.setSelectedCustomization(entry.getKey(), entry.getValue());
            }

            customer.getCart().addItem(toAdd);
            updateCartTotal();

            feedbackBtn.setText("Added ✓");
            feedbackBtn.setStyle(
                    "-fx-background-color: #4a7c59; -fx-text-fill: white;" +
                            "-fx-font-size: 12px; -fx-font-weight: bold;" +
                            "-fx-padding: 6 14; -fx-background-radius: 6; -fx-cursor: hand;");

            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ex) {
                }
                Platform.runLater(() -> {
                    feedbackBtn.setText(selectedCustomizations != null
                            ? "Customize & Add"
                            : "Add to Cart");
                    feedbackBtn.setStyle("");
                    feedbackBtn.getStyleClass().setAll("dashboard-add-button");
                });
            }).start();

        } catch (IllegalArgumentException e) {
            System.out.println("Add to cart error: " + e.getMessage());
        }
    }

    private void updateCartTotal() {
        cartTotalLabel.setText("Cart: Rs." + (int) customer.getCart().getTotal());
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