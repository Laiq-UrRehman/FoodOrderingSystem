// Combined: Pizza size animation + Fries size animation (File 1)
// Combined: Spice Level thermometer widget (File 2/3)
// Combined: Coffee Cup size widget (File 2/3)
// Combined: Extra sauce toggle added to any panel containing a size group (File 1)
// Combined: All pizza/fries visuals dark #111111 / #8B5E3C theme (File 1)

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MenuViewController {

    // ══════════════════════════════════════════════════════════════════════════
    // THERMOMETER CONSTANTS
    // ══════════════════════════════════════════════════════════════════════════
    private static final double T_STEM_W = 14;
    private static final double T_STEM_H = 120;
    private static final double T_STEM_X = 33;
    private static final double T_STEM_Y = 12;
    private static final double T_BULB_R = 16;
    private static final double T_BULB_CX = T_STEM_X + T_STEM_W / 2.0;
    private static final double T_BULB_CY = T_STEM_Y + T_STEM_H + T_BULB_R + 3;

    // ══════════════════════════════════════════════════════════════════════════
    // COFFEE CUP CONSTANTS & DATA
    // ══════════════════════════════════════════════════════════════════════════
    private static final double CUP_CX = 100;
    private static final double CUP_Y0 = 28;
    private static final double LID_H = 11;
    private static final double BASE_H = 6;

    private enum DrinkType {
        COFFEE, JUICE, SODA, MILKSHAKE, WATER
    }

    private enum CupSize {
        SMALL("Small", 88, 52, 36),
        MEDIUM("Medium", 114, 66, 44),
        LARGE("Large", 144, 80, 54);

        final String label;
        final double cupH, cupTopW, cupBotW;

        CupSize(String label, double cupH, double cupTopW, double cupBotW) {
            this.label = label;
            this.cupH = cupH;
            this.cupTopW = cupTopW;
            this.cupBotW = cupBotW;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SPICE LEVEL DATA
    // ══════════════════════════════════════════════════════════════════════════
    private enum SpiceLevel {
        MILD("Mild", 0.20, "#7cc83a", "Just a hint of warmth!"),
        MEDIUM("Medium", 0.45, "#f4a020", "A nice kick!"),
        HOT("Hot", 0.72, "#e84020", "Serious heat!"),
        HELL("Hell", 1.00, "#cc0000", "MAXIMUM BURN!!");

        final String label;
        final double fillPct;
        final Color color;
        final String message;

        SpiceLevel(String label, double fillPct, String hex, String message) {
            this.label = label;
            this.fillPct = fillPct;
            this.color = Color.web(hex);
            this.message = message;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // FXML FIELDS
    // ══════════════════════════════════════════════════════════════════════════
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

    // ─────────────────────────────────────────────────────────────────────────
    // FXML init
    // ─────────────────────────────────────────────────────────────────────────
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

    // ─────────────────────────────────────────────────────────────────────────
    // Menu loading
    // ─────────────────────────────────────────────────────────────────────────
    private void loadMenuItems() {
        menuContainer.getChildren().clear();
        for (FoodItem item : restaurant.getMenu().getItems())
            menuContainer.getChildren().add(createMenuItemRow(item));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // MENU ITEM ROW
    // ══════════════════════════════════════════════════════════════════════════
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

        // Updated: nameLabel first, then one offer label per restaurant offer, then
        // category and rating
        info.getChildren().add(nameLabel);
        for (RestaurantOffer offer : restaurant.getRestaurantOffers()) {
            Label offerLabel = new Label("🏷  " + offer.getTitle() + "  " + (int) offer.getDiscountPercent() + "% off");
            offerLabel.setStyle("-fx-text-fill: #8B5E3C; -fx-font-size: 11px; -fx-font-weight: bold;");
            info.getChildren().add(offerLabel);
        }
        info.getChildren().addAll(categoryLabel, itemRatingLabel);

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

    // ══════════════════════════════════════════════════════════════════════════
    // CUSTOMIZATION PANEL ROUTER
    // Dispatches each group to the appropriate widget:
    // • "spice level" → animated thermometer
    // • "size" / "coffee size" /
    // "cup size" on a coffee/
    // beverage item → animated coffee-cup widget
    // • "size" / "bucket size" on a fries item → animated fries-box widget
    // • "size" on a pizza item → animated pizza-slice widget
    // • anything else → default option buttons
    //
    // An Extra Sauce toggle is appended whenever any size group is present.
    // ══════════════════════════════════════════════════════════════════════════
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

        // Extra-sauce state (only relevant when a size group exists)
        final boolean[] sauceOn = { false };
        final double SAUCE_COST = 50.0;

        Label confirmError = new Label("");
        confirmError.getStyleClass().add("text-error-message");

        // Classify the item once so every size group below can branch correctly
        boolean isFriesItem = item.getName().toLowerCase().contains("fries")
                || item.getCategory().toLowerCase().contains("fries");
        String catLow = item.getCategory().toLowerCase();
        String nameLow = item.getName().toLowerCase();

        DrinkType detectedDrink = null;
        if (catLow.contains("coffee") || nameLow.contains("coffee")
                || nameLow.contains("cappuccino") || nameLow.contains("latte")
                || nameLow.contains("espresso") || nameLow.contains("americano"))
            detectedDrink = DrinkType.COFFEE;
        else if (catLow.contains("juice") || nameLow.contains("juice")
                || nameLow.contains("mango") || nameLow.contains("orange juice")
                || nameLow.contains("apple juice"))
            detectedDrink = DrinkType.JUICE;
        else if (catLow.contains("soda") || nameLow.contains("soda")
                || nameLow.contains("cola") || nameLow.contains("pepsi")
                || nameLow.contains("sprite") || nameLow.contains("7up")
                || nameLow.contains("fanta") || nameLow.contains("lemonade"))
            detectedDrink = DrinkType.SODA;
        else if (nameLow.contains("milkshake") || nameLow.contains("shake")
                || nameLow.contains("smoothie"))
            detectedDrink = DrinkType.MILKSHAKE;
        else if (catLow.contains("water") || nameLow.contains("water"))
            detectedDrink = DrinkType.WATER;
        else if (catLow.contains("beverage") || catLow.contains("drink"))
            detectedDrink = DrinkType.COFFEE; // fallback

        boolean isCoffeeItem = detectedDrink != null;
        final DrinkType finalDrink = detectedDrink;

        final boolean[] hasPizzaSizeGroup = { false };

        for (CustomizationGroup group : item.getCustomizationGroups()) {
            String gn = group.getGroupName().toLowerCase();

            // ── Spice level ───────────────────────────────────────────────────
            if (gn.equals("spice level")) {
                panel.getChildren().add(
                        buildSpiceThermometerWidget(group, selections, extraCharges,
                                priceLabel, item, confirmError));
                continue;
            }

            // ── Size group — branch by item type ─────────────────────────────
            // "bucket size" always routes to the fries widget regardless of item category
            if (gn.equals("size") || gn.equals("coffee size") || gn.equals("cup size")
                    || gn.equals("bucket size")) {

                if (isCoffeeItem || gn.equals("coffee size") || gn.equals("cup size")) {
                    panel.getChildren().add(
                            buildCoffeeCupWidget(group, selections, extraCharges,
                                    priceLabel, item, confirmError,
                                    finalDrink != null ? finalDrink : DrinkType.COFFEE));
                } else if (isFriesItem || gn.equals("bucket size")) {
                    // Fries / bucket → animated fries box
                    panel.getChildren().add(
                            buildFriesSelector(group, selections, extraCharges,
                                    item, priceLabel, confirmError));
                } else {
                    // Default size (pizza, burgers, etc.) → animated pizza slices
                    hasPizzaSizeGroup[0] = true;
                    panel.getChildren().add(
                            buildPizzaSelector(group, selections, extraCharges,
                                    item, priceLabel,
                                    sauceOn, SAUCE_COST, confirmError));
                }
                continue;
            }

            // ── Default option buttons ────────────────────────────────────────
            VBox groupSection = new VBox(8);
            Label groupLabel = new Label(group.getGroupName().toUpperCase());
            groupLabel.getStyleClass().add("menu-customization-group-label");

            FlowPane optionsFlow = new FlowPane(8, 8);
            optionsFlow.setAlignment(Pos.CENTER_LEFT);

            List<Button> optionBtns = new ArrayList<>();

            for (int i = 0; i < group.getOptions().size(); i++) {
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
                            .mapToDouble(Double::doubleValue).sum()
                            + (sauceOn[0] ? SAUCE_COST : 0);
                    priceLabel.setText("Rs. " + (int) (item.getPrice() + totalExtra));
                    confirmError.setText("");
                });

                optionBtns.add(optBtn);
                optionsFlow.getChildren().add(optBtn);
            }

            groupSection.getChildren().addAll(groupLabel, optionsFlow);
            panel.getChildren().add(groupSection);
        }

        // ── Extra Sauce toggle (shown whenever any size group is present) ─────
        if (hasPizzaSizeGroup[0]) {
            panel.getChildren().add(
                    buildSauceToggle(sauceOn, SAUCE_COST, item, priceLabel,
                            selections, extraCharges));
        }

        // ── Confirm button ────────────────────────────────────────────────────
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
                    .mapToDouble(Double::doubleValue).sum()
                    + (sauceOn[0] ? SAUCE_COST : 0);
            double finalPrice = item.getPrice() + totalExtra;

            Map<String, String> finalSelections = new LinkedHashMap<>(selections);
            if (sauceOn[0])
                finalSelections.put("Extra Sauce", "Yes (+Rs.50)");

            addToCart(item, qtySpinner.getValue(), finalSelections, triggerBtn, finalPrice);

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

    // ══════════════════════════════════════════════════════════════════════════
    // FRIES SELECTOR — animated fries box + option buttons
    // ══════════════════════════════════════════════════════════════════════════
    private VBox buildFriesSelector(CustomizationGroup group,
            Map<String, String> selections,
            Map<String, Double> extraCharges,
            FoodItem item, Label priceLabel,
            Label confirmError) {

        VBox section = new VBox(12);
        section.setAlignment(Pos.CENTER);
        section.setMaxWidth(Double.MAX_VALUE);

        Label groupLabel = new Label(group.getGroupName().toUpperCase());
        groupLabel.getStyleClass().add("menu-customization-group-label");
        groupLabel.setAlignment(Pos.CENTER);
        groupLabel.setMaxWidth(Double.MAX_VALUE);

        FriesAnimPane friesPane = new FriesAnimPane(group.getOptions().size());
        friesPane.setMaxWidth(Double.MAX_VALUE);
        friesPane.setAlignment(Pos.CENTER);

        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER);

        List<Button> optBtns = new ArrayList<>();

        for (int i = 0; i < group.getOptions().size(); i++) {
            final int idx = i;
            final String optName = group.getOptions().get(i);
            final double charge = group.getExtraCharges().get(i);

            VBox btnContent = new VBox(2);
            btnContent.setAlignment(Pos.CENTER);

            Label topLbl = new Label(optName.toUpperCase());
            topLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #888888;");

            Label chargeLbl = new Label(charge > 0 ? "+Rs." + (int) charge : "free");
            chargeLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #aaaaaa;");

            btnContent.getChildren().addAll(topLbl, chargeLbl);

            Button btn = new Button();
            btn.setGraphic(btnContent);
            btn.setPrefWidth(120);
            btn.setMinWidth(120);
            btn.setMaxWidth(120);
            btn.setPadding(new Insets(10, 8, 10, 8));
            btn.setCursor(javafx.scene.Cursor.HAND);
            styleOptionBtn(btn, false);

            btn.setOnAction(e -> {
                for (Button b : optBtns)
                    styleOptionBtn(b, false);
                styleOptionBtn(btn, true);

                selections.put(group.getGroupName(), optName);
                extraCharges.put(group.getGroupName(), charge);

                double totalExtra = extraCharges.values().stream()
                        .mapToDouble(Double::doubleValue).sum();
                priceLabel.setText("Rs. " + (int) (item.getPrice() + totalExtra));
                confirmError.setText("");

                friesPane.selectSize(idx);
            });

            optBtns.add(btn);
            btnRow.getChildren().add(btn);
        }

        // Pre-select first option
        if (!optBtns.isEmpty()) {
            styleOptionBtn(optBtns.get(0), true);
            selections.put(group.getGroupName(), group.getOptions().get(0));
            extraCharges.put(group.getGroupName(), group.getExtraCharges().get(0));
        }

        section.getChildren().addAll(groupLabel, friesPane, btnRow);
        return section;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PIZZA SELECTOR — animated pizza canvas + option buttons
    // ══════════════════════════════════════════════════════════════════════════
    private VBox buildPizzaSelector(CustomizationGroup group,
            Map<String, String> selections,
            Map<String, Double> extraCharges,
            FoodItem item, Label priceLabel,
            boolean[] sauceOn, double sauceCost,
            Label confirmError) {

        VBox section = new VBox(12);
        section.setAlignment(Pos.CENTER);

        Label groupLabel = new Label(group.getGroupName().toUpperCase());
        groupLabel.getStyleClass().add("menu-customization-group-label");
        groupLabel.setAlignment(Pos.CENTER_LEFT);
        groupLabel.setMaxWidth(Double.MAX_VALUE);

        PizzaAnimPane pizzaPane = new PizzaAnimPane(group.getOptions().size());
        pizzaPane.setMaxWidth(Double.MAX_VALUE);
        pizzaPane.setAlignment(Pos.CENTER);

        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER);

        List<Button> optBtns = new ArrayList<>();

        for (int i = 0; i < group.getOptions().size(); i++) {
            final int idx = i;
            final String optName = group.getOptions().get(i);
            final double charge = group.getExtraCharges().get(i);

            VBox btnContent = new VBox(2);
            btnContent.setAlignment(Pos.CENTER);

            Label topLbl = new Label(optName.toUpperCase());
            topLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #888888;");

            Label chargeLbl = new Label(charge > 0 ? "+Rs." + (int) charge : "free");
            chargeLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #aaaaaa;");

            btnContent.getChildren().addAll(topLbl, chargeLbl);

            Button btn = new Button();
            btn.setGraphic(btnContent);
            btn.setPrefWidth(120);
            btn.setMinWidth(120);
            btn.setMaxWidth(120);
            btn.setPadding(new Insets(10, 8, 10, 8));
            btn.setCursor(javafx.scene.Cursor.HAND);
            styleOptionBtn(btn, false);

            btn.setOnAction(e -> {
                for (Button b : optBtns)
                    styleOptionBtn(b, false);
                styleOptionBtn(btn, true);

                selections.put(group.getGroupName(), optName);
                extraCharges.put(group.getGroupName(), charge);

                double totalExtra = extraCharges.values().stream()
                        .mapToDouble(Double::doubleValue).sum()
                        + (sauceOn[0] ? sauceCost : 0);
                priceLabel.setText("Rs. " + (int) (item.getPrice() + totalExtra));
                confirmError.setText("");

                pizzaPane.selectSize(idx);
            });

            optBtns.add(btn);
            btnRow.getChildren().add(btn);
        }

        // Pre-select first option visually
        if (!optBtns.isEmpty()) {
            styleOptionBtn(optBtns.get(0), true);
            selections.put(group.getGroupName(), group.getOptions().get(0));
            extraCharges.put(group.getGroupName(), group.getExtraCharges().get(0));
        }

        section.getChildren().addAll(groupLabel, pizzaPane, btnRow);
        return section;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // EXTRA SAUCE TOGGLE
    // ══════════════════════════════════════════════════════════════════════════
    private VBox buildSauceToggle(boolean[] sauceOn, double sauceCost,
            FoodItem item, Label priceLabel,
            Map<String, String> selections,
            Map<String, Double> extraCharges) {

        VBox section = new VBox(8);

        Label sauceLabel = new Label("EXTRA SAUCE");
        sauceLabel.getStyleClass().add("menu-customization-group-label");

        HBox toggleRow = new HBox(12);
        toggleRow.setAlignment(Pos.CENTER_LEFT);

        Button sauceBtn = new Button("🍅  Add Extra Sauce  +Rs." + (int) sauceCost);
        sauceBtn.setPrefWidth(240);
        sauceBtn.setPadding(new Insets(9, 16, 9, 16));
        sauceBtn.setCursor(javafx.scene.Cursor.HAND);
        styleSauceBtn(sauceBtn, false);

        Label sauceNote = new Label("Rich tomato sauce on top");
        sauceNote.setStyle("-fx-text-fill: #444444; -fx-font-size: 11px;");

        sauceBtn.setOnAction(e -> {
            sauceOn[0] = !sauceOn[0];
            styleSauceBtn(sauceBtn, sauceOn[0]);
            sauceBtn.setText(sauceOn[0]
                    ? "🍅  Extra Sauce Added  ✓"
                    : "🍅  Add Extra Sauce  +Rs." + (int) sauceCost);

            double totalExtra = extraCharges.values().stream()
                    .mapToDouble(Double::doubleValue).sum()
                    + (sauceOn[0] ? sauceCost : 0);
            priceLabel.setText("Rs. " + (int) (item.getPrice() + totalExtra));
        });

        toggleRow.getChildren().addAll(sauceBtn, sauceNote);
        section.getChildren().addAll(sauceLabel, toggleRow);
        return section;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // COFFEE CUP WIDGET
    // ══════════════════════════════════════════════════════════════════════════
    private VBox buildCoffeeCupWidget(
            CustomizationGroup group,
            Map<String, String> selections,
            Map<String, Double> extraCharges,
            Label priceLabel,
            FoodItem item,
            Label confirmError,
            DrinkType drinkType) {

        // ── Per-drink visual config ───────────────────────────────────────────
        record DrinkConfig(
                Color liquidTop, Color liquidBot, // coffee body gradient
                Color espTop, Color espBot, // base layer
                Color foamTop, Color foamBot, // top layer
                Color foamBubble,
                Color streamCol,
                boolean hasFoam, boolean hasBubbles,
                String lidStyle // "paper" or "glass"
        ) {
        }

        DrinkConfig dc = switch (drinkType) {
            case JUICE -> new DrinkConfig(
                    Color.web("#e87820"), Color.web("#c05808"),
                    Color.web("#a03800"), Color.web("#802800"),
                    Color.web("#f0a040"), Color.web("#d08020"),
                    Color.web("#f8c060"),
                    Color.web("#c06010"),
                    false, false, "glass");
            case SODA -> new DrinkConfig(
                    Color.web("#1a1a2e"), Color.web("#0d0d1a"),
                    Color.web("#0a0a14"), Color.web("#050508"),
                    Color.web("#c8e0f0"), Color.web("#a0c8e0"),
                    Color.web("#e0f0ff"),
                    Color.web("#3080c0"),
                    true, true, "glass");
            case MILKSHAKE -> new DrinkConfig(
                    Color.web("#e8b0c8"), Color.web("#c88098"),
                    Color.web("#c06080"), Color.web("#a04860"),
                    Color.web("#fff0f8"), Color.web("#f0d0e8"),
                    Color.web("#ffffff"),
                    Color.web("#d080a8"),
                    true, false, "paper");
            case WATER -> new DrinkConfig(
                    Color.web("#a8d8f0"), Color.web("#70b8e0"),
                    Color.web("#50a0d0"), Color.web("#3080b8"),
                    Color.web("#e0f4ff"), Color.web("#c0e8f8"),
                    Color.web("#f0faff"),
                    Color.web("#80c0e8"),
                    false, true, "glass");
            default -> new DrinkConfig( // COFFEE
                    Color.web("#4a2810"), Color.web("#38190a"),
                    Color.web("#1a0a02"), Color.web("#180a02"),
                    Color.web("#d4b890"), Color.web("#cfb090"),
                    Color.web("#f0e0c8"),
                    Color.web("#5a3010"),
                    true, false, "paper");
        };

        VBox wrapper = new VBox(10);
        wrapper.setAlignment(Pos.TOP_LEFT);

        Label groupLabel = new Label(group.getGroupName().toUpperCase());
        groupLabel.getStyleClass().add("menu-customization-group-label");

        double paneW = 200, paneH = 210;
        Pane cupPane = new Pane();
        cupPane.setPrefSize(paneW, paneH);
        cupPane.setMaxWidth(paneW);
        cupPane.setStyle("-fx-background-color: transparent;");

        double[] curH = { CupSize.MEDIUM.cupH };
        double[] curTopW = { CupSize.MEDIUM.cupTopW };
        double[] curBotW = { CupSize.MEDIUM.cupBotW };
        double[] fill = { 0 };
        boolean[] pouring = { false };
        boolean[] busy = { false };
        CupSize[] current = { CupSize.MEDIUM };

        // ── Shapes ────────────────────────────────────────────────────────────
        Polygon cupBody = new Polygon();
        cupBody.setStroke(Color.web("#b09880"));
        cupBody.setStrokeWidth(1.2);

        Polygon sleeve = new Polygon();
        sleeve.setFill(Color.web("#c09c6a", 0.30));
        sleeve.setStroke(Color.web("#a07848", 0.55));
        sleeve.setStrokeWidth(0.7);

        Ellipse baseEllipse = new Ellipse();
        baseEllipse.setFill(Color.web("#987060", 0.40));

        Rectangle baseRect = new Rectangle();
        baseRect.setFill(Color.web("#b89878", 0.40));
        baseRect.setArcWidth(4);
        baseRect.setArcHeight(4);
        baseRect.setStroke(Color.web("#907060", 0.55));
        baseRect.setStrokeWidth(0.7);

        Rectangle espLayer = new Rectangle();
        espLayer.setFill(dc.espTop());

        Polygon coffeeLayer = new Polygon();
        coffeeLayer.setFill(dc.liquidTop());

        Rectangle foamLayer = new Rectangle();
        foamLayer.setArcWidth(3);
        foamLayer.setArcHeight(3);
        foamLayer.setFill(dc.foamTop());

        Circle[] bubbles = new Circle[7];
        for (int i = 0; i < bubbles.length; i++) {
            bubbles[i] = new Circle();
            bubbles[i].setFill(dc.foamBubble().deriveColor(0, 1, 1, 0.50));
        }

        Rectangle foamSheen = new Rectangle();
        foamSheen.setArcWidth(2);
        foamSheen.setArcHeight(2);
        foamSheen.setFill(Color.web("#ffffff", 0.14));

        Path pourStream = new Path();
        pourStream.setFill(Color.TRANSPARENT);
        pourStream.setStroke(dc.streamCol());
        pourStream.setStrokeWidth(3.2);
        pourStream.setStrokeLineCap(StrokeLineCap.ROUND);

        Path pourSheen = new Path();
        pourSheen.setFill(Color.TRANSPARENT);
        pourSheen.setStroke(Color.web("#c07840", 0.30));
        pourSheen.setStrokeWidth(1.0);
        pourSheen.setStrokeLineCap(StrokeLineCap.ROUND);

        Ellipse pourRipple = new Ellipse();
        pourRipple.setFill(Color.TRANSPARENT);
        pourRipple.setStroke(Color.web("#a06030", 0.45));
        pourRipple.setStrokeWidth(0.8);

        Rectangle lidRect = new Rectangle();
        lidRect.setArcWidth(8);
        lidRect.setArcHeight(8);
        lidRect.setFill(Color.web("#404040"));
        lidRect.setStroke(Color.web("#222222"));
        lidRect.setStrokeWidth(1.0);

        Ellipse lidTop = new Ellipse();
        lidTop.setFill(Color.web("#484848", 0.60));

        Rectangle sipHole = new Rectangle();
        sipHole.setArcWidth(4);
        sipHole.setArcHeight(4);
        sipHole.setFill(Color.web("#111111", 0.80));

        Rectangle lidSheen = new Rectangle();
        lidSheen.setArcWidth(2);
        lidSheen.setArcHeight(2);
        lidSheen.setFill(Color.web("#ffffff", 0.11));

        Line hiLine = new Line();
        hiLine.setStroke(Color.web("#ffffff", 0.14));
        hiLine.setStrokeWidth(2.5);
        hiLine.setStrokeLineCap(StrokeLineCap.ROUND);

        Line shadowLine = new Line();
        shadowLine.setStroke(Color.web("#7a6050", 0.18));
        shadowLine.setStrokeWidth(1.8);
        shadowLine.setStrokeLineCap(StrokeLineCap.ROUND);

        // Rising bubbles (soda / water)
        Canvas bubbleCanvas = new Canvas(paneW, paneH);
        GraphicsContext bgc = bubbleCanvas.getGraphicsContext2D();
        double[] bubbleY = new double[12];
        double[] bubbleX = new double[12];
        double[] bubbleSpd = new double[12];
        double[] bubbleR = new double[12];
        for (int i = 0; i < 12; i++) {
            bubbleX[i] = 20 + Math.random() * 160;
            bubbleY[i] = 40 + Math.random() * 140;
            bubbleSpd[i] = 0.4 + Math.random() * 0.7;
            bubbleR[i] = 1.2 + Math.random() * 1.8;
        }
        AnimationTimer bubbleTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!dc.hasBubbles() || fill[0] < 0.05)
                    return;
                bgc.clearRect(0, 0, paneW, paneH);
                double cupTop2 = CUP_Y0, cupBot2 = cupTop2 + curH[0];
                double maxFY = cupBot2 - BASE_H - 2;
                double liqY = maxFY - fill[0] * (maxFY - (cupTop2 + 4));
                for (int i = 0; i < 12; i++) {
                    bubbleY[i] -= bubbleSpd[i];
                    if (bubbleY[i] < liqY) {
                        bubbleY[i] = maxFY;
                        bubbleX[i] = CUP_CX - curBotW[0] / 2 + 4
                                + Math.random() * (curBotW[0] - 8);
                    }
                    bgc.setFill(Color.color(
                            dc.streamCol().getRed(),
                            dc.streamCol().getGreen(),
                            dc.streamCol().getBlue(), 0.35));
                    bgc.fillOval(bubbleX[i] - bubbleR[i], bubbleY[i] - bubbleR[i],
                            bubbleR[i] * 2, bubbleR[i] * 2);
                }
            }
        };
        bubbleTimer.start();

        cupPane.getChildren().addAll(
                bubbleCanvas,
                espLayer, coffeeLayer, foamLayer, foamSheen,
                pourStream, pourSheen, pourRipple,
                cupBody, sleeve, hiLine, shadowLine,
                baseEllipse, baseRect,
                lidRect, lidTop, sipHole, lidSheen);
        for (Circle b : bubbles)
            cupPane.getChildren().add(b);

        // ── Redraw lambda ─────────────────────────────────────────────────────
        Runnable redraw = () -> {
            double h = curH[0], topW = curTopW[0], botW = curBotW[0];
            double f = fill[0];
            boolean pour = pouring[0];

            double cupTop = CUP_Y0, cupBot = cupTop + h;
            double tl = CUP_CX - topW / 2, tr = CUP_CX + topW / 2;
            double bl = CUP_CX - botW / 2, br = CUP_CX + botW / 2;
            double lidW = topW + 8;

            double maxFillY = cupBot - BASE_H - 2;
            double minFillY = cupTop + 4;
            double fillRange = maxFillY - minFillY;

            double liquidY = maxFillY - f * fillRange;
            double tFill = (liquidY - cupTop) / (cupBot - cupTop);
            double fillW = topW + (botW - topW) * tFill;
            double fillL = CUP_CX - fillW / 2;

            double foamH = Math.min(fillRange * f * 0.15, 16);
            double foamY = liquidY;
            double coffeeY = foamY + foamH;
            double espH = Math.min(fillRange * f * 0.13, 14);
            double espY = maxFillY - espH;

            boolean showLiquid = f > 0.01;

            cupBody.getPoints().setAll(tl, cupTop, tr, cupTop, br, cupBot, bl, cupBot);
            cupBody.setFill(Color.web("#e8ddd2", 0.72));

            sleeve.getPoints().setAll(
                    tl + (tr - tl) * 0.10, cupTop + h * 0.34,
                    tr - (tr - tl) * 0.10, cupTop + h * 0.34,
                    tr - (tr - tl) * 0.10 - (tr - br) * 0.14, cupTop + h * 0.64,
                    tl + (tr - tl) * 0.10 + (bl - tl) * 0.14, cupTop + h * 0.64);

            hiLine.setStartX(tl + 5);
            hiLine.setStartY(cupTop + 5);
            hiLine.setEndX(bl + 4);
            hiLine.setEndY(cupBot - 7);
            shadowLine.setStartX(tr - 4);
            shadowLine.setStartY(cupTop + 5);
            shadowLine.setEndX(br - 3);
            shadowLine.setEndY(cupBot - 7);

            baseEllipse.setCenterX(CUP_CX);
            baseEllipse.setCenterY(cupBot);
            baseEllipse.setRadiusX(botW / 2 + 2);
            baseEllipse.setRadiusY(3);
            baseRect.setX(bl - 1);
            baseRect.setY(cupBot - 1);
            baseRect.setWidth(botW + 2);
            baseRect.setHeight(BASE_H);

            if (showLiquid) {
                double[] pts = { tl, cupTop, tr, cupTop, br, cupBot, bl, cupBot };

                espLayer.setX(bl - 1);
                espLayer.setY(espY);
                espLayer.setWidth(botW + 2);
                espLayer.setHeight(Math.max(0, espH + 3));
                espLayer.setVisible(espH > 1);
                espLayer.setClip(new Polygon(pts));

                coffeeLayer.getPoints().setAll(fillL - 1, coffeeY, fillL + fillW + 1, coffeeY, br + 1, cupBot, bl - 1,
                        cupBot);
                coffeeLayer.setVisible(true);
                coffeeLayer.setClip(new Polygon(pts));

                foamLayer.setX(fillL);
                foamLayer.setY(foamY);
                foamLayer.setWidth(fillW);
                foamLayer.setHeight(Math.max(0, foamH + 1));
                foamLayer.setVisible(dc.hasFoam() && foamH > 1);
                foamLayer.setClip(new Polygon(pts));

                if (dc.hasFoam() && foamH > 3) {
                    for (int i = 0; i < bubbles.length; i++) {
                        bubbles[i].setCenterX(fillL + fillW * (0.08 + i * 0.13));
                        bubbles[i].setCenterY(foamY + foamH * 0.48);
                        bubbles[i].setRadius(1.4 + Math.sin(i * 1.9) * 0.6);
                        bubbles[i].setVisible(true);
                        bubbles[i].setClip(new Polygon(pts));
                    }
                    foamSheen.setX(fillL + fillW * 0.08);
                    foamSheen.setY(foamY + 1);
                    foamSheen.setWidth(fillW * 0.40);
                    foamSheen.setHeight(1.5);
                    foamSheen.setVisible(true);
                    foamSheen.setClip(new Polygon(pts));
                } else {
                    for (Circle b : bubbles)
                        b.setVisible(false);
                    foamSheen.setVisible(false);
                }
            } else {
                espLayer.setVisible(false);
                coffeeLayer.setVisible(false);
                foamLayer.setVisible(false);
                foamSheen.setVisible(false);
                for (Circle b : bubbles)
                    b.setVisible(false);
            }

            if (pour && f > 0.02 && f < 0.999) {
                double pourX = CUP_CX + 5;
                double streamT = cupTop + LID_H + 1;
                double streamB = liquidY + 2;
                double m1y = streamT + (streamB - streamT) * 0.30;
                double m2y = streamT + (streamB - streamT) * 0.70;

                pourStream.getElements().setAll(
                        new MoveTo(pourX, streamT),
                        new CubicCurveTo(pourX + 1.2, m1y, pourX - 0.8, m2y, pourX + 0.4, streamB));
                pourSheen.getElements().setAll(
                        new MoveTo(pourX + 0.4, streamT + 3),
                        new CubicCurveTo(pourX + 1.8, m1y, pourX - 0.4, m2y, pourX + 1, streamB - 4));
                pourRipple.setCenterX(pourX);
                pourRipple.setCenterY(coffeeY);
                pourRipple.setRadiusX(fillW * 0.12);
                pourRipple.setRadiusY(2);
                pourStream.setVisible(true);
                pourSheen.setVisible(true);
                pourRipple.setVisible(true);
            } else {
                pourStream.setVisible(false);
                pourSheen.setVisible(false);
                pourRipple.setVisible(false);
            }

            lidRect.setX(CUP_CX - lidW / 2);
            lidRect.setY(cupTop - LID_H);
            lidRect.setWidth(lidW);
            lidRect.setHeight(LID_H);
            lidTop.setCenterX(CUP_CX);
            lidTop.setCenterY(cupTop - LID_H);
            lidTop.setRadiusX(lidW / 2 - 1);
            lidTop.setRadiusY(3.5);
            sipHole.setX(CUP_CX - 8);
            sipHole.setY(cupTop - LID_H + 2);
            sipHole.setWidth(16);
            sipHole.setHeight(4);
            lidSheen.setX(CUP_CX - lidW / 2 + 5);
            lidSheen.setY(cupTop - LID_H + 2);
            lidSheen.setWidth(lidW * 0.30);
            lidSheen.setHeight(2);
        };

        // ── AnimationTimer wrapper ────────────────────────────────────────────
        AnimationTimer[] timer = { null };

        class CupAnim {
            void run(double toH, double toTopW, double toBotW,
                    double toFill, long durationMs, boolean showPour,
                    Runnable onDone) {
                if (timer[0] != null) {
                    timer[0].stop();
                    timer[0] = null;
                }

                double fromH = curH[0], fromTopW = curTopW[0], fromBotW = curBotW[0];
                double fromFill = fill[0];

                pouring[0] = showPour;
                long[] start = { -1L };

                timer[0] = new AnimationTimer() {
                    @Override
                    public void handle(long now) {
                        if (start[0] < 0)
                            start[0] = now;
                        double raw = Math.min(1.0, (now - start[0]) / (durationMs * 1_000_000.0));

                        curH[0] = lerp(fromH, toH, easeInOut(raw));
                        curTopW[0] = lerp(fromTopW, toTopW, easeInOut(raw));
                        curBotW[0] = lerp(fromBotW, toBotW, easeInOut(raw));
                        fill[0] = lerp(fromFill, toFill, showPour ? easeOutCubic(raw) : easeInOut(raw));

                        redraw.run();

                        if (raw >= 1.0) {
                            stop();
                            timer[0] = null;
                            pouring[0] = false;
                            redraw.run();
                            if (onDone != null)
                                Platform.runLater(onDone);
                        }
                    }
                };
                timer[0].start();
            }
        }
        CupAnim anim = new CupAnim();

        Platform.runLater(() -> anim.run(CupSize.MEDIUM.cupH, CupSize.MEDIUM.cupTopW, CupSize.MEDIUM.cupBotW,
                1.0, 1400, true, null));

        // ── Size buttons ──────────────────────────────────────────────────────
        HBox btnRow = new HBox(8);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        for (CupSize cs : CupSize.values()) {
            int optIdx = group.getOptions().indexOf(cs.label);
            final double charge = (optIdx >= 0) ? group.getExtraCharges().get(optIdx) : 0.0;

            Button btn = new Button(cs.label.toUpperCase());
            btn.setStyle(buildCupButtonStyle(false));

            btn.setOnMouseEntered(e -> {
                btn.setScaleX(1.05);
                btn.setScaleY(1.05);
            });
            btn.setOnMouseExited(e -> {
                btn.setScaleX(1.0);
                btn.setScaleY(1.0);
            });

            btn.setOnAction(e -> {
                if (busy[0] || current[0] == cs)
                    return;

                btnRow.getChildren().forEach(n -> {
                    if (n instanceof Button)
                        ((Button) n).setStyle(buildCupButtonStyle(false));
                });
                btn.setStyle(buildCupButtonStyle(true));

                busy[0] = true;
                CupSize prev = current[0];
                current[0] = cs;

                boolean growing = cs.ordinal() > prev.ordinal();

                if (growing) {
                    double scaledStart = (prev.cupH / cs.cupH) * 0.88;
                    fill[0] = scaledStart;
                    anim.run(cs.cupH, cs.cupTopW, cs.cupBotW, 1.0, 1200, true,
                            () -> busy[0] = false);
                } else {
                    double scaledEnd = Math.min((prev.cupH / cs.cupH) * 0.60, 1.0);
                    anim.run(cs.cupH, cs.cupTopW, cs.cupBotW, scaledEnd, 900, false,
                            () -> anim.run(cs.cupH, cs.cupTopW, cs.cupBotW, 1.0, 650, true,
                                    () -> busy[0] = false));
                }

                selections.put(group.getGroupName(), cs.label);
                extraCharges.put(group.getGroupName(), charge);
                double totalExtra = extraCharges.values().stream()
                        .mapToDouble(Double::doubleValue).sum();
                priceLabel.setText("Rs. " + (int) (item.getPrice() + totalExtra));
                confirmError.setText("");
            });

            btnRow.getChildren().add(btn);
        }

        HBox contentRow = new HBox(16);
        contentRow.setAlignment(Pos.CENTER_LEFT);
        VBox rightSide = new VBox(10);
        rightSide.setAlignment(Pos.CENTER_LEFT);
        rightSide.getChildren().add(btnRow);

        contentRow.getChildren().addAll(cupPane, rightSide);
        wrapper.getChildren().addAll(groupLabel, contentRow);

        redraw.run();
        return wrapper;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SPICE THERMOMETER WIDGET
    // ══════════════════════════════════════════════════════════════════════════
    private VBox buildSpiceThermometerWidget(
            CustomizationGroup group,
            Map<String, String> selections,
            Map<String, Double> extraCharges,
            Label priceLabel,
            FoodItem item,
            Label confirmError) {

        VBox wrapper = new VBox(12);
        wrapper.setAlignment(Pos.TOP_LEFT);

        Label groupLabel = new Label("SPICE LEVEL");
        groupLabel.getStyleClass().add("menu-customization-group-label");

        Pane thermoPane = new Pane();
        thermoPane.setPrefSize(80, T_BULB_CY + T_BULB_R + 16);
        thermoPane.setMaxWidth(80);

        Rectangle stemBg = new Rectangle(T_STEM_X, T_STEM_Y, T_STEM_W, T_STEM_H);
        stemBg.setArcWidth(T_STEM_W);
        stemBg.setArcHeight(T_STEM_W);
        stemBg.setFill(Color.web("#2a2a2a"));

        Circle bulbBg = new Circle(T_BULB_CX, T_BULB_CY, T_BULB_R);
        bulbBg.setFill(Color.web("#2a2a2a"));

        Circle bulbBase = new Circle(T_BULB_CX, T_BULB_CY, T_BULB_R - 4);
        bulbBase.setFill(Color.web("#1a1a1a"));

        Rectangle fillRect = new Rectangle(T_STEM_X + 2, T_STEM_Y + T_STEM_H, T_STEM_W - 4, 0);
        fillRect.setArcWidth(6);
        fillRect.setArcHeight(6);
        fillRect.setFill(Color.web("#444444"));
        fillRect.setClip(new Rectangle(T_STEM_X + 2, T_STEM_Y, T_STEM_W - 4, T_STEM_H));

        Circle bulbFill = new Circle(T_BULB_CX, T_BULB_CY, T_BULB_R - 4);
        bulbFill.setFill(Color.web("#444444"));

        Circle bulbRing = new Circle(T_BULB_CX, T_BULB_CY, T_BULB_R);
        bulbRing.setFill(Color.TRANSPARENT);
        bulbRing.setStroke(Color.web("#444444"));
        bulbRing.setStrokeWidth(2.5);

        for (double t : new double[] { 0.25, 0.5, 0.75, 1.0 }) {
            double ty = T_STEM_Y + T_STEM_H - (T_STEM_H * t);
            Line tL = new Line(T_STEM_X - 3, ty, T_STEM_X + 4, ty);
            tL.setStroke(Color.web("#3a3a3a"));
            tL.setStrokeWidth(1.2);
            Line tR = new Line(T_STEM_X + T_STEM_W - 4, ty, T_STEM_X + T_STEM_W + 3, ty);
            tR.setStroke(Color.web("#3a3a3a"));
            tR.setStrokeWidth(1.2);
            thermoPane.getChildren().addAll(tL, tR);
        }

        Path outerFlame = buildThermometerOuterFlame();
        Path innerFlame = buildThermometerInnerFlame();
        thermoPane.getChildren().addAll(stemBg, bulbBg, bulbBase, fillRect, bulbFill, bulbRing,
                outerFlame, innerFlame);

        Text messageLabel = new Text("Pick your heat");
        messageLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        messageLabel.setFill(Color.web("#555555"));

        HBox btnRow = new HBox(8);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        final Timeline[] tl = { null };

        for (SpiceLevel lv : SpiceLevel.values()) {
            int optIdx = group.getOptions().indexOf(lv.label);
            final double charge = (optIdx >= 0) ? group.getExtraCharges().get(optIdx) : 0.0;

            Button btn = new Button(lv.label.toUpperCase());
            btn.setStyle(buildSpiceButtonStyle(lv.color, false));
            btn.setOnMouseEntered(e -> {
                btn.setScaleX(1.05);
                btn.setScaleY(1.05);
            });
            btn.setOnMouseExited(e -> {
                btn.setScaleX(1.0);
                btn.setScaleY(1.0);
            });

            btn.setOnAction(e -> {
                btnRow.getChildren().forEach(n -> {
                    if (n instanceof Button) {
                        SpiceLevel s = spiceLevelFromLabel(((Button) n).getText());
                        if (s != null)
                            ((Button) n).setStyle(buildSpiceButtonStyle(s.color, false));
                    }
                });
                btn.setStyle(buildSpiceButtonStyle(lv.color, true));

                double targetH = T_STEM_H * lv.fillPct;
                double targetY = T_STEM_Y + T_STEM_H - targetH;

                if (tl[0] != null)
                    tl[0].stop();
                tl[0] = new Timeline(
                        new KeyFrame(Duration.ZERO,
                                new KeyValue(fillRect.heightProperty(), fillRect.getHeight()),
                                new KeyValue(fillRect.yProperty(), fillRect.getY())),
                        new KeyFrame(Duration.millis(450),
                                new KeyValue(fillRect.heightProperty(), targetH),
                                new KeyValue(fillRect.yProperty(), targetY)));
                tl[0].play();

                fillRect.setFill(lv.color);
                bulbFill.setFill(lv.color);
                bulbRing.setStroke(lv.color);
                outerFlame.setFill(lv.color);
                messageLabel.setFill(lv.color);
                messageLabel.setText(lv.message);
                bulbRing.setEffect(new DropShadow(12, lv.color));

                selections.put(group.getGroupName(), lv.label);
                extraCharges.put(group.getGroupName(), charge);
                double totalExtra = extraCharges.values().stream()
                        .mapToDouble(Double::doubleValue).sum();
                priceLabel.setText("Rs. " + (int) (item.getPrice() + totalExtra));
                confirmError.setText("");
            });

            btnRow.getChildren().add(btn);
        }

        HBox contentRow = new HBox(16);
        contentRow.setAlignment(Pos.TOP_LEFT);
        VBox rightSide = new VBox(10);
        rightSide.setAlignment(Pos.CENTER_LEFT);
        rightSide.getChildren().addAll(btnRow, messageLabel);

        contentRow.getChildren().addAll(thermoPane, rightSide);
        wrapper.getChildren().addAll(groupLabel, contentRow);
        return wrapper;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // FLAME PATHS (thermometer)
    // ══════════════════════════════════════════════════════════════════════════
    private Path buildThermometerOuterFlame() {
        double cx = T_BULB_CX, cy = T_BULB_CY;
        Path f = new Path();
        f.getElements().addAll(
                new MoveTo(cx, cy + 7),
                new CubicCurveTo(cx - 5, cy + 5, cx - 5, cy + 1, cx - 3, cy - 2),
                new CubicCurveTo(cx - 3, cy, cx - 2, cy + 1, cx - 2, cy + 1),
                new CubicCurveTo(cx - 2, cy - 2, cx - 1, cy - 5, cx, cy - 7),
                new CubicCurveTo(cx + 1, cy - 5, cx + 3, cy - 3, cx + 3, cy),
                new CubicCurveTo(cx + 3, cy, cx + 4, cy - 1, cx + 4, cy - 3),
                new CubicCurveTo(cx + 6, cy - 1, cx + 6, cy + 3, cx + 5, cy + 5),
                new CubicCurveTo(cx + 4, cy + 7, cx + 2, cy + 7, cx, cy + 7),
                new ClosePath());
        f.setFill(Color.web("#555555"));
        f.setStroke(Color.TRANSPARENT);
        return f;
    }

    private Path buildThermometerInnerFlame() {
        double cx = T_BULB_CX, cy = T_BULB_CY + 1;
        Path f = new Path();
        f.getElements().addAll(
                new MoveTo(cx, cy + 5),
                new CubicCurveTo(cx - 2, cy + 3, cx - 2, cy + 1, cx - 1, cy - 1),
                new CubicCurveTo(cx - 1, cy, cx, cy + 1, cx, cy + 1),
                new CubicCurveTo(cx, cy - 1, cx, cy - 3, cx + 1, cy - 4),
                new CubicCurveTo(cx + 1, cy - 3, cx + 2, cy - 1, cx + 2, cy + 1),
                new CubicCurveTo(cx + 3, cy, cx + 3, cy + 2, cx + 2, cy + 4),
                new CubicCurveTo(cx + 1, cy + 5, cx, cy + 5, cx, cy + 5),
                new ClosePath());
        f.setFill(Color.WHITE);
        f.setOpacity(0.25);
        f.setStroke(Color.TRANSPARENT);
        return f;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // BUTTON STYLE HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    /** Dark-themed option button used by pizza & fries selectors. */
    private void styleOptionBtn(Button btn, boolean active) {
        if (active) {
            btn.setStyle(
                    "-fx-background-color: #1f1510;" +
                            "-fx-text-fill: #8B5E3C;" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-color: #8B5E3C;" +
                            "-fx-border-radius: 8;" +
                            "-fx-border-width: 2;" +
                            "-fx-cursor: hand;");
            btn.setScaleX(1.07);
            btn.setScaleY(1.07);
            btn.setTranslateY(-2);
            btn.setEffect(new DropShadow(14, Color.web("#8B5E3C55")));
        } else {
            btn.setStyle(
                    "-fx-background-color: #2a2a2a;" +
                            "-fx-text-fill: #aaaaaa;" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-color: #3a3a3a;" +
                            "-fx-border-radius: 8;" +
                            "-fx-border-width: 1.5;" +
                            "-fx-cursor: hand;");
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
            btn.setTranslateY(0);
            btn.setEffect(null);
        }
    }

    /** Sauce toggle button style. */
    private void styleSauceBtn(Button btn, boolean active) {
        if (active) {
            btn.setStyle(
                    "-fx-background-color: #1f1510;" +
                            "-fx-text-fill: #8B5E3C;" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-color: #8B5E3C;" +
                            "-fx-border-radius: 8;" +
                            "-fx-border-width: 2;" +
                            "-fx-cursor: hand;");
            btn.setEffect(new DropShadow(10, Color.web("#8B5E3C44")));
        } else {
            btn.setStyle(
                    "-fx-background-color: #2a2a2a;" +
                            "-fx-text-fill: #aaaaaa;" +
                            "-fx-font-size: 13px;" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-color: #3a3a3a;" +
                            "-fx-border-radius: 8;" +
                            "-fx-border-width: 1.5;" +
                            "-fx-cursor: hand;");
            btn.setEffect(null);
        }
    }

    /** Coffee cup size button style. */
    private String buildCupButtonStyle(boolean selected) {
        if (selected)
            return "-fx-background-color: #8B5E3C; -fx-text-fill: white;" +
                    "-fx-font-weight: 900; -fx-font-size: 11;" +
                    "-fx-padding: 6 16 6 16; -fx-background-radius: 6;" +
                    "-fx-border-color: #8B5E3C; -fx-border-radius: 6; -fx-cursor: hand;";
        return "-fx-background-color: #2a2a2a; -fx-text-fill: #888888;" +
                "-fx-font-weight: 900; -fx-font-size: 11;" +
                "-fx-padding: 6 16 6 16; -fx-background-radius: 6;" +
                "-fx-border-color: #444444; -fx-border-radius: 6; -fx-cursor: hand;";
    }

    /** Spice level button style (pill shape, color-coded). */
    private String buildSpiceButtonStyle(Color color, boolean selected) {
        String hex = toHex(color);
        return "-fx-background-color: " + (selected ? hex + "22" : "transparent") + ";" +
                "-fx-border-color: " + hex + "; -fx-border-width: 2;" +
                "-fx-border-radius: 50; -fx-background-radius: 50;" +
                "-fx-text-fill: " + hex + "; -fx-font-weight: 900; -fx-font-size: 11;" +
                "-fx-padding: 6 14 6 14; -fx-cursor: hand;";
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SMALL HELPERS
    // ══════════════════════════════════════════════════════════════════════════
    private static double easeInOut(double t) {
        return t < .5 ? 2 * t * t : -1 + (4 - 2 * t) * t;
    }

    private static double easeOutCubic(double t) {
        return 1 - Math.pow(1 - t, 3);
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private SpiceLevel spiceLevelFromLabel(String text) {
        for (SpiceLevel lv : SpiceLevel.values())
            if (lv.label.equalsIgnoreCase(text))
                return lv;
        return null;
    }

    private String toHex(Color c) {
        return String.format("#%02x%02x%02x",
                (int) (c.getRed() * 255), (int) (c.getGreen() * 255), (int) (c.getBlue() * 255));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ADD TO CART
    // ══════════════════════════════════════════════════════════════════════════
    private void addToCart(FoodItem item, int qty,
            Map<String, String> selectedCustomizations,
            Button feedbackBtn, double finalPrice) {
        try {
            FoodItem toAdd = new FoodItem(
                    item.getFoodID(), item.getName(),
                    finalPrice, item.getCategory(), qty);

            if (selectedCustomizations != null)
                for (Map.Entry<String, String> e : selectedCustomizations.entrySet())
                    toAdd.setSelectedCustomization(e.getKey(), e.getValue());

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

    // ══════════════════════════════════════════════════════════════════════════
    // NAVIGATION
    // ══════════════════════════════════════════════════════════════════════════
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

    // =========================================================================
    // INNER CLASS: PizzaAnimPane
    // Self-contained animated pizza canvas, dark #111111 / #8B5E3C theme.
    // =========================================================================
    static class PizzaAnimPane extends VBox {

        private static final int[] SLICE_COUNTS = { 4, 6, 8, 8 };
        private static final double[] RADII = { 80, 105, 128, 150 };

        private static final double SPREAD_MS = 500;
        private static final double FLY_MS = 620;
        private static final double EXIT_MS = 480;
        private static final long STAGGER = 90_000_000L; // ns

        private static final double CX = 175, CY = 175;

        private static class Slice {
            int id, seed;
            double s0, s1, r;
            double opacity = 0;
            double flyFrac = 1;
        }

        private final int totalOptions;
        private final List<Slice> slices = new ArrayList<>();
        private int selectedIdx = 0;
        private boolean busy = false;

        private final Canvas canvas;
        private final GraphicsContext gc;
        private AnimationTimer timer;

        private enum Phase {
            IDLE, FLY_IN, SPREAD, EXIT, CLOSE
        }

        private Phase phase = Phase.IDLE;
        private long phaseStart = 0;
        private int nextSizeIdx;

        private double[] fromS0, fromS1, fromR;
        private double[] toS0, toS1, toR;
        private int spreadCount;

        private record FlyJob(int sliceId, long startNs) {
        }

        private List<FlyJob> flyJobs = new ArrayList<>();
        private List<FlyJob> exitJobs = new ArrayList<>();

        PizzaAnimPane(int optionCount) {
            this.totalOptions = Math.min(optionCount, 4);
            setAlignment(Pos.CENTER);
            setPadding(new Insets(8, 0, 4, 0));

            canvas = new Canvas(350, 350);
            gc = canvas.getGraphicsContext2D();
            getChildren().add(canvas);

            Platform.runLater(() -> {
                buildSlices(selectedIdx);
                startInitFlyIn();
                startRenderLoop();
            });
        }

        private int sliceCount(int idx) {
            return idx < totalOptions ? SLICE_COUNTS[Math.min(idx, SLICE_COUNTS.length - 1)] : 4;
        }

        private double radius(int idx) {
            return idx < totalOptions ? RADII[Math.min(idx, RADII.length - 1)] : RADII[0];
        }

        private static double[] polar(double deg, double r) {
            double rad = Math.toRadians(deg);
            return new double[] { CX + r * Math.cos(rad), CY + r * Math.sin(rad) };
        }

        private static double ease(double t) {
            return t < .5 ? 2 * t * t : -1 + (4 - 2 * t) * t;
        }

        private static double lerp(double a, double b, double t) {
            return a + (b - a) * t;
        }

        private double[] tS0(int n) {
            double[] r = new double[n];
            for (int i = 0; i < n; i++)
                r[i] = i * 360.0 / n - 90;
            return r;
        }

        private double[] tS1(int n) {
            double[] r = new double[n];
            for (int i = 0; i < n; i++)
                r[i] = (i + 1) * 360.0 / n - 90;
            return r;
        }

        private void buildSlices(int sIdx) {
            slices.clear();
            int n = sliceCount(sIdx);
            double[] s0 = tS0(n), s1 = tS1(n);
            for (int i = 0; i < n; i++) {
                Slice sl = new Slice();
                sl.id = i;
                sl.seed = i;
                sl.s0 = s0[i];
                sl.s1 = s1[i];
                sl.r = radius(sIdx);
                sl.opacity = 0;
                sl.flyFrac = 1;
                slices.add(sl);
            }
        }

        private void startInitFlyIn() {
            busy = true;
            flyJobs.clear();
            long now = System.nanoTime();
            for (int i = 0; i < slices.size(); i++)
                flyJobs.add(new FlyJob(i, now + i * STAGGER));
            phase = Phase.FLY_IN;
        }

        void selectSize(int nextIdx) {
            if (nextIdx == selectedIdx || busy)
                return;
            int prev = selectedIdx;
            selectedIdx = nextIdx;
            busy = true;

            int prevN = sliceCount(prev), nextN = sliceCount(nextIdx);
            boolean growing = nextIdx > prev;

            if (growing) {
                int kept = prevN;
                int added = nextN - kept;

                fromS0 = new double[kept];
                fromS1 = new double[kept];
                fromR = new double[kept];
                for (int i = 0; i < kept; i++) {
                    fromS0[i] = slices.get(i).s0;
                    fromS1[i] = slices.get(i).s1;
                    fromR[i] = slices.get(i).r;
                }
                double[] ns0 = tS0(nextN), ns1 = tS1(nextN);
                toS0 = new double[kept];
                toS1 = new double[kept];
                toR = new double[kept];
                for (int i = 0; i < kept; i++) {
                    toS0[i] = ns0[i];
                    toS1[i] = ns1[i];
                    toR[i] = radius(nextIdx);
                }
                spreadCount = kept;

                for (int j = 0; j < added; j++) {
                    Slice sl = new Slice();
                    sl.id = kept + j;
                    sl.seed = kept + j;
                    sl.s0 = ns0[kept + j];
                    sl.s1 = ns1[kept + j];
                    sl.r = radius(nextIdx);
                    sl.opacity = 0;
                    sl.flyFrac = 1;
                    slices.add(sl);
                }
                phaseStart = System.nanoTime();
                phase = Phase.SPREAD;

            } else {
                int keep = nextN;
                int remove = prevN - keep;

                exitJobs.clear();
                long now = System.nanoTime();
                for (int j = 0; j < remove; j++)
                    exitJobs.add(new FlyJob(keep + j, now + j * STAGGER));

                fromS0 = new double[keep];
                fromS1 = new double[keep];
                fromR = new double[keep];
                for (int i = 0; i < keep; i++) {
                    fromS0[i] = slices.get(i).s0;
                    fromS1[i] = slices.get(i).s1;
                    fromR[i] = slices.get(i).r;
                }
                double[] ns0 = tS0(nextN), ns1 = tS1(nextN);
                toS0 = new double[keep];
                toS1 = new double[keep];
                toR = new double[keep];
                for (int i = 0; i < keep; i++) {
                    toS0[i] = ns0[i];
                    toS1[i] = ns1[i];
                    toR[i] = radius(nextIdx);
                }
                spreadCount = keep;
                phaseStart = System.nanoTime();
                phase = Phase.EXIT;
            }
        }

        private void startRenderLoop() {
            timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    tick(now);
                    redraw();
                }
            };
            timer.start();
        }

        private void tick(long now) {
            switch (phase) {
                case FLY_IN -> {
                    boolean done = true;
                    for (FlyJob j : flyJobs) {
                        double el = (now - j.startNs()) / 1e6;
                        if (el < 0) {
                            done = false;
                            continue;
                        }
                        double t = Math.min(1.0, el / FLY_MS);
                        double e = ease(t);
                        Slice sl = find(j.sliceId());
                        if (sl != null) {
                            sl.flyFrac = 1 - e;
                            sl.opacity = e;
                        }
                        if (t < 1)
                            done = false;
                    }
                    if (done) {
                        phase = Phase.IDLE;
                        busy = false;
                        flyJobs.clear();
                    }
                }
                case SPREAD -> {
                    double t = Math.min(1.0, (now - phaseStart) / 1e6 / SPREAD_MS);
                    double e = ease(t);
                    for (int i = 0; i < spreadCount; i++) {
                        Slice sl = slices.get(i);
                        sl.s0 = lerp(fromS0[i], toS0[i], e);
                        sl.s1 = lerp(fromS1[i], toS1[i], e);
                        sl.r = lerp(fromR[i], toR[i], e);
                        sl.flyFrac = 0;
                        sl.opacity = 1;
                    }
                    if (t >= 1) {
                        if (slices.size() > spreadCount) {
                            flyJobs.clear();
                            int added = slices.size() - spreadCount;
                            for (int j = 0; j < added; j++)
                                flyJobs.add(new FlyJob(spreadCount + j, now + j * STAGGER));
                            phase = Phase.FLY_IN;
                        } else {
                            phase = Phase.IDLE;
                            busy = false;
                        }
                    }
                }
                case EXIT -> {
                    boolean done = true;
                    for (FlyJob j : exitJobs) {
                        double el = (now - j.startNs()) / 1e6;
                        if (el < 0) {
                            done = false;
                            continue;
                        }
                        double t = Math.min(1.0, el / EXIT_MS);
                        double e = ease(t);
                        Slice sl = find(j.sliceId());
                        if (sl != null) {
                            sl.flyFrac = e;
                            sl.opacity = 1 - e;
                        }
                        if (t < 1)
                            done = false;
                    }
                    if (done) {
                        slices.removeIf(sl -> sl.flyFrac >= 0.999);
                        exitJobs.clear();
                        phaseStart = now;
                        phase = Phase.CLOSE;
                    }
                }
                case CLOSE -> {
                    double t = Math.min(1.0, (now - phaseStart) / 1e6 / SPREAD_MS);
                    double e = ease(t);
                    for (int i = 0; i < spreadCount && i < slices.size(); i++) {
                        Slice sl = slices.get(i);
                        sl.s0 = lerp(fromS0[i], toS0[i], e);
                        sl.s1 = lerp(fromS1[i], toS1[i], e);
                        sl.r = lerp(fromR[i], toR[i], e);
                        sl.flyFrac = 0;
                        sl.opacity = 1;
                    }
                    if (t >= 1) {
                        phase = Phase.IDLE;
                        busy = false;
                    }
                }
                default -> {
                }
            }
        }

        private Slice find(int id) {
            for (Slice sl : slices)
                if (sl.id == id)
                    return sl;
            return null;
        }

        private void redraw() {
            gc.clearRect(0, 0, 350, 350);
            gc.setFill(Color.web("#1a1a1a"));
            gc.fillOval(CX - 178, CY - 178, 356, 356);
            gc.setStroke(Color.web("#2a2a2a"));
            gc.setLineWidth(1.5);
            gc.strokeOval(CX - 178, CY - 178, 356, 356);
            for (Slice sl : slices)
                drawSlice(sl);
        }

        private void drawSlice(Slice sl) {
            if (sl.opacity < 0.005)
                return;
            gc.save();

            if (sl.flyFrac > 0.001) {
                double mid = Math.toRadians((sl.s0 + sl.s1) / 2.0);
                double dist = 420 * sl.flyFrac;
                double scale = 1.0 - 0.5 * sl.flyFrac;
                gc.translate(CX, CY);
                gc.scale(scale, scale);
                gc.translate(-CX + dist * Math.cos(mid) / scale,
                        -CY + dist * Math.sin(mid) / scale);
            }

            gc.setGlobalAlpha(sl.opacity);

            // 1. Cheese wedge
            gc.setFill(Color.web("#D4A020"));
            gc.setStroke(Color.web("#8B6010"));
            gc.setLineWidth(2.0);
            fillWedge(sl.s0, sl.s1, sl.r);
            strokeWedge(sl.s0, sl.s1, sl.r);

            // 2. Cheese blob highlight
            gc.setFill(Color.web("#E8B840"));
            gc.setGlobalAlpha(sl.opacity * 0.38);
            fillCheesePath(sl.s0, sl.s1, sl.r, sl.seed);
            gc.setGlobalAlpha(sl.opacity);

            // 3. Sauce blobs
            double[] sauceF = { 0.22, 0.52, 0.78 };
            for (int i = 0; i < 3; i++) {
                double span = sl.s1 - sl.s0;
                double a = sl.s0 + span * sauceF[i];
                double rr = sl.r * (0.28 + i * 0.15);
                double[] p = polar(a, rr);
                double rx = sl.r * (0.086 + Math.sin(sl.seed * 1.3 + i) * 0.014);
                double ry = sl.r * (0.070 + Math.cos(sl.seed * 2.1 + i) * 0.012);
                gc.save();
                gc.translate(p[0], p[1]);
                gc.rotate(sl.seed * 35 + i * 55);
                gc.setFill(Color.web("#CC3322"));
                gc.setGlobalAlpha(sl.opacity * 0.72);
                gc.fillOval(-rx, -ry, rx * 2, ry * 2);
                gc.restore();
                gc.setGlobalAlpha(sl.opacity);
            }

            // 4. Shine streaks
            double[] shineF = { 0.36, 0.64 };
            for (int i = 0; i < 2; i++) {
                double a = sl.s0 + (sl.s1 - sl.s0) * shineF[i];
                double[] p = polar(a, sl.r * 0.40);
                double rx = sl.r * 0.050, ry = sl.r * 0.034;
                gc.save();
                gc.translate(p[0], p[1]);
                gc.rotate(a + 20);
                gc.setFill(Color.web("#FFE880"));
                gc.setGlobalAlpha(sl.opacity * 0.32);
                gc.fillOval(-rx, -ry, rx * 2, ry * 2);
                gc.restore();
                gc.setGlobalAlpha(sl.opacity);
            }

            // 5. Toppings
            drawToppings(sl);

            // 6. Crust
            gc.setFill(Color.web("#8B5E3C"));
            gc.setStroke(Color.web("#5a3820"));
            gc.setLineWidth(2.0);
            fillCrustPath(sl.s0, sl.s1, sl.r, sl.seed);
            strokeCrustPath(sl.s0, sl.s1, sl.r, sl.seed);

            // 7. Crust sheen
            gc.setStroke(Color.web("#C4884A"));
            gc.setLineWidth(3.0);
            gc.setGlobalAlpha(sl.opacity * 0.22);
            strokeCrustPath(sl.s0, sl.s1, sl.r, sl.seed);
            gc.setGlobalAlpha(sl.opacity);

            // 8. Crust burn spots
            double cr = sl.r + 18;
            double[] burnF = { 0.18, 0.50, 0.82 };
            gc.setFill(Color.web("#4a2800"));
            gc.setGlobalAlpha(sl.opacity * 0.35);
            for (double f : burnF) {
                double a = sl.s0 + (sl.s1 - sl.s0) * f;
                double[] p = polar(a, cr + 3);
                double br = sl.r * 0.022;
                gc.fillOval(p[0] - br, p[1] - br, br * 2, br * 2);
            }
            gc.setGlobalAlpha(sl.opacity);

            // 9. Slice divider
            double[] lineEnd = polar(sl.s0, cr);
            gc.setStroke(Color.web("#6F4A2F"));
            gc.setLineWidth(1.8);
            gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
            gc.setGlobalAlpha(sl.opacity * 0.60);
            gc.strokeLine(CX, CY, lineEnd[0], lineEnd[1]);
            gc.setGlobalAlpha(sl.opacity);

            gc.restore();
        }

        // ── Path helpers ──────────────────────────────────────────────────────
        private void fillWedge(double s0, double s1, double r) {
            double span = s1 - s0;
            double[] A = polar(s0, r);
            int steps = Math.max(12, (int) (span / 3));
            gc.beginPath();
            gc.moveTo(CX, CY);
            gc.lineTo(A[0], A[1]);
            for (int i = 1; i <= steps; i++) {
                double[] p = polar(s0 + span * i / steps, r);
                gc.lineTo(p[0], p[1]);
            }
            gc.closePath();
            gc.fill();
        }

        private void strokeWedge(double s0, double s1, double r) {
            double span = s1 - s0;
            double[] A = polar(s0, r);
            int steps = Math.max(12, (int) (span / 3));
            gc.beginPath();
            gc.moveTo(CX, CY);
            gc.lineTo(A[0], A[1]);
            for (int i = 1; i <= steps; i++) {
                double[] p = polar(s0 + span * i / steps, r);
                gc.lineTo(p[0], p[1]);
            }
            gc.closePath();
            gc.stroke();
        }

        private void fillCheesePath(double s0, double s1, double r, int seed) {
            double span = s1 - s0;
            int N = 11;
            double[][] pts = new double[N + 1][2];
            for (int i = 0; i < N; i++) {
                double t = i / (N - 1.0);
                double a = s0 + span * (0.09 + t * 0.82);
                double rr = r * Math.min(0.86,
                        0.25 + 0.55 * Math.abs(Math.sin(seed * 1.7 + i * 2.3))
                                + Math.sin(seed * 3.1 + i * 1.9) * 0.09);
                pts[i] = polar(a, rr);
            }
            pts[N] = polar(s0 + span * 0.5, r * 0.09);
            gc.beginPath();
            gc.moveTo(pts[0][0], pts[0][1]);
            for (int i = 1; i < pts.length; i++) {
                double mx = (pts[i - 1][0] + pts[i][0]) / 2, my = (pts[i - 1][1] + pts[i][1]) / 2;
                gc.quadraticCurveTo(pts[i - 1][0], pts[i - 1][1], mx, my);
            }
            gc.closePath();
            gc.fill();
        }

        private double[][] crustOuter(double s0, double s1, double r, int seed) {
            double cr = r + 18;
            double span = s1 - s0;
            int steps = Math.max(6, (int) Math.round(span / 10));
            double[][] pts = new double[steps + 1][2];
            for (int i = 0; i <= steps; i++) {
                double a = s0 + span * i / steps;
                double w = cr + Math.sin(seed * 2.3 + i * 3.1) * 3.5 + Math.cos(seed * 1.8 + i * 2.5) * 2.5;
                pts[i] = polar(a, w);
            }
            return pts;
        }

        private void fillCrustPath(double s0, double s1, double r, int seed) {
            double span = s1 - s0;
            double[] A = polar(s0, r);
            double[][] outer = crustOuter(s0, s1, r, seed);
            int steps = Math.max(12, (int) (span / 3));
            gc.beginPath();
            gc.moveTo(A[0], A[1]);
            for (int i = 1; i <= steps; i++) {
                double[] p = polar(s0 + span * i / steps, r);
                gc.lineTo(p[0], p[1]);
            }
            gc.lineTo(outer[outer.length - 1][0], outer[outer.length - 1][1]);
            for (int i = outer.length - 2; i >= 0; i--) {
                double mx = (outer[i + 1][0] + outer[i][0]) / 2, my = (outer[i + 1][1] + outer[i][1]) / 2;
                gc.quadraticCurveTo(outer[i + 1][0], outer[i + 1][1], mx, my);
            }
            gc.closePath();
            gc.fill();
        }

        private void strokeCrustPath(double s0, double s1, double r, int seed) {
            double span = s1 - s0;
            double[] A = polar(s0, r);
            double[][] outer = crustOuter(s0, s1, r, seed);
            int steps = Math.max(12, (int) (span / 3));
            gc.beginPath();
            gc.moveTo(A[0], A[1]);
            for (int i = 1; i <= steps; i++) {
                double[] p = polar(s0 + span * i / steps, r);
                gc.lineTo(p[0], p[1]);
            }
            gc.lineTo(outer[outer.length - 1][0], outer[outer.length - 1][1]);
            for (int i = outer.length - 2; i >= 0; i--) {
                double mx = (outer[i + 1][0] + outer[i][0]) / 2, my = (outer[i + 1][1] + outer[i][1]) / 2;
                gc.quadraticCurveTo(outer[i + 1][0], outer[i + 1][1], mx, my);
            }
            gc.closePath();
            gc.stroke();
        }

        // ── Toppings ──────────────────────────────────────────────────────────
        private record TDef(String type, double af, double rf) {
        }

        private static final TDef[] TOPS = {
                new TDef("pepperoni", 0.25, 0.47), new TDef("pepperoni", 0.70, 0.61),
                new TDef("pepperoni", 0.45, 0.29), new TDef("mushroom", 0.15, 0.64),
                new TDef("mushroom", 0.80, 0.39), new TDef("olive", 0.55, 0.71),
                new TDef("olive", 0.30, 0.69),
        };

        private void drawToppings(Slice sl) {
            double span = sl.s1 - sl.s0;
            for (TDef t : TOPS) {
                double[] p = polar(sl.s0 + t.af() * span, t.rf() * sl.r);
                double tr = sl.r * 0.088;
                switch (t.type()) {
                    case "pepperoni" -> drawPepperoni(p[0], p[1], tr * 1.1);
                    case "mushroom" -> drawMushroom(p[0], p[1], tr * 1.0);
                    case "olive" -> drawOlive(p[0], p[1], tr * 0.85);
                }
            }
        }

        private void drawPepperoni(double x, double y, double r) {
            gc.setFill(Color.web("#AA2820"));
            gc.setStroke(Color.web("#661810"));
            gc.setLineWidth(1.8);
            gc.fillOval(x - r, y - r, r * 2, r * 2);
            gc.strokeOval(x - r, y - r, r * 2, r * 2);
            gc.setFill(Color.web("#CC3828"));
            gc.fillOval(x - r * .76, y - r * .76, r * .76 * 2, r * .76 * 2);
            dot(x - r * .22, y - r * .28, r * .12, "#FFFFFF", 0.16);
        }

        private void drawMushroom(double x, double y, double r) {
            gc.setFill(Color.web("#C8B898"));
            gc.setStroke(Color.web("#7a5830"));
            gc.setLineWidth(1.6);
            gc.fillOval(x - r, y - r * .08 - r * .58, r * 2, r * .58 * 2);
            gc.strokeOval(x - r, y - r * .08 - r * .58, r * 2, r * .58 * 2);
            gc.setFill(Color.web("#D8CCA8"));
            gc.fillOval(x - r * .76, y - r * .10 - r * .42, r * .76 * 2, r * .42 * 2);
            gc.setFill(Color.web("#C8B898"));
            gc.setStroke(Color.web("#7a5830"));
            gc.setLineWidth(1.4);
            gc.fillRoundRect(x - r * .25, y + r * .20, r * .50, r * .54, r * .10 * 2, r * .10 * 2);
            gc.strokeRoundRect(x - r * .25, y + r * .20, r * .50, r * .54, r * .10 * 2, r * .10 * 2);
        }

        private void drawOlive(double x, double y, double r) {
            gc.setFill(Color.web("#2a3010"));
            gc.setStroke(Color.web("#181e08"));
            gc.setLineWidth(1.6);
            gc.fillOval(x - r, y - r, r * 2, r * 2);
            gc.strokeOval(x - r, y - r, r * 2, r * 2);
            gc.setFill(Color.web("#C09020"));
            gc.fillOval(x - r * .40, y - r * .36, r * .40 * 2, r * .36 * 2);
            dot(x - r * .16, y - r * .16, r * .13, "#FFFFFF", 0.12);
        }

        private void dot(double x, double y, double r, String hex, double a) {
            double saved = gc.getGlobalAlpha();
            gc.setGlobalAlpha(saved * a);
            gc.setFill(Color.web(hex));
            gc.fillOval(x - r, y - r, r * 2, r * 2);
            gc.setGlobalAlpha(saved);
        }
    }

    // =========================================================================
    // INNER CLASS: FriesAnimPane
    // Self-contained animated fries box, dark #111111 / #8B5E3C theme.
    // =========================================================================
    static class FriesAnimPane extends VBox {

        static final int[] FRY_COUNTS = { 12, 22, 35 };
        static final double[] BOX_W = { 110, 150, 200 };
        static final double[] BOX_H = { 90, 122, 158 };
        static final String[] LETTERS = { "S", "M", "L" };

        static final Color[] FRY_COLS = {
                Color.web("#D4A020"), Color.web("#C89010"), Color.web("#DCA828"),
                Color.web("#C08808"), Color.web("#D8AA30"), Color.web("#C49018"),
                Color.web("#E0B038")
        };
        static final Color[] FRY_DARK = {
                Color.web("#8B6010"), Color.web("#7A5008"), Color.web("#906810"),
                Color.web("#684008"), Color.web("#887018"), Color.web("#7A5810"),
                Color.web("#9A7018")
        };

        static final double SW = 300, SH = 280;
        static final double MX = SW / 2, MY = SH / 2 + 40;

        private final int totalOptions;
        private int selectedIdx = 0;
        private boolean busy = false;

        private double curBoxW, curBoxH;

        static class FryItem {
            int id;
            double x, w, h, rot, popFrac;
            int ci;
            double yFrac = 1.0;
            double opacity = 0.0;
            javafx.scene.Group node;
        }

        private final List<FryItem> fries = new ArrayList<>();
        private final javafx.scene.layout.Pane fryPane = new javafx.scene.layout.Pane();
        private final javafx.scene.layout.Pane boxPane = new javafx.scene.layout.Pane();
        private final javafx.scene.layout.Pane rootPane = new javafx.scene.layout.Pane();
        private final javafx.scene.shape.Ellipse shadow;

        FriesAnimPane(int optionCount) {
            this.totalOptions = Math.min(optionCount, 3);
            setAlignment(Pos.CENTER);
            setPadding(new Insets(8, 0, 4, 0));

            curBoxW = BOX_W[0];
            curBoxH = BOX_H[0];

            // ── Canvas wrapper centred inside the VBox ────────────────────────
            javafx.scene.layout.StackPane canvasWrapper = new javafx.scene.layout.StackPane();
            canvasWrapper.setAlignment(Pos.CENTER);
            canvasWrapper.setMaxWidth(Double.MAX_VALUE);

            javafx.scene.layout.Pane canvas = new javafx.scene.layout.Pane();
            canvas.setPrefSize(SW, SH);
            canvas.setMaxWidth(SW);

            shadow = new javafx.scene.shape.Ellipse(0, 7, curBoxW * 0.44, 6);
            shadow.setFill(Color.color(0, 0, 0, 0.25));
            shadow.setTranslateX(MX);
            shadow.setTranslateY(MY);

            rootPane.getChildren().addAll(fryPane, boxPane);
            rootPane.setTranslateX(MX);
            rootPane.setTranslateY(MY);

            canvas.getChildren().addAll(shadow, rootPane);
            canvasWrapper.getChildren().add(canvas);
            getChildren().add(canvasWrapper);

            Platform.runLater(() -> {
                buildFries(0);
                renderBox();
                redrawFries();
                for (int i = 0; i < fries.size(); i++) {
                    final int idx = i;
                    PauseTransition delay = new PauseTransition(Duration.millis(80 + idx * 40));
                    delay.setOnFinished(e -> popFry(idx));
                    delay.play();
                }
            });
        }

        private void buildFries(int sIdx) {
            fries.clear();
            int total = FRY_COUNTS[Math.min(sIdx, FRY_COUNTS.length - 1)];
            double bw = BOX_W[Math.min(sIdx, BOX_W.length - 1)];
            for (int i = 0; i < total; i++)
                fries.add(makeFry(i, bw, total));
        }

        private FryItem makeFry(int id, double boxW, int total) {
            FryItem fi = new FryItem();
            fi.id = id;
            int cols = (int) Math.ceil(Math.sqrt(total * 1.6));
            int col = id % cols;
            double usableW = boxW * 0.88;
            double colW = usableW / cols;
            double xBase = -usableW / 2 + (col + 0.5) * colW;
            double xOff = Math.sin(id * 2.7 + col * 1.1) * colW * 0.28;
            fi.x = xBase + xOff;
            fi.w = Math.max(5, colW * 0.62 + Math.sin(id * 1.7) * 1.2);
            fi.h = 48 + Math.sin(id * 3.1 + 0.8) * 14 + (id % 4 == 0 ? 12 : 0);
            fi.ci = id % FRY_COLS.length;
            fi.rot = Math.sin(id * 2.1 + col * 0.9) * 13;
            fi.popFrac = 0.38 + Math.abs(Math.sin(id * 1.4 + 0.7)) * 0.38;
            fi.yFrac = 1.0;
            fi.opacity = 0.0;
            fi.node = null;
            return fi;
        }

        private static double easeOutBounce(double t) {
            if (t < 1 / 2.75)
                return 7.5625 * t * t;
            if (t < 2 / 2.75) {
                t -= 1.5 / 2.75;
                return 7.5625 * t * t + 0.75;
            }
            if (t < 2.5 / 2.75) {
                t -= 2.25 / 2.75;
                return 7.5625 * t * t + 0.9375;
            }
            t -= 2.625 / 2.75;
            return 7.5625 * t * t + 0.984375;
        }

        private static double easeInOut(double t) {
            return t < .5 ? 2 * t * t : -1 + (4 - 2 * t) * t;
        }

        private static double easeInCubic(double t) {
            return t * t * t;
        }

        private static double lerp(double a, double b, double t) {
            return a + (b - a) * t;
        }

        private javafx.scene.Group buildFryNode(FryItem fi) {
            javafx.scene.Group g = new javafx.scene.Group();

            javafx.scene.shape.Rectangle body = new javafx.scene.shape.Rectangle(-fi.w / 2, -fi.h / 2, fi.w, fi.h);
            body.setArcWidth(fi.w * 0.76);
            body.setArcHeight(fi.w * 0.76);
            body.setFill(FRY_COLS[fi.ci]);

            javafx.scene.shape.Rectangle side = new javafx.scene.shape.Rectangle(-fi.w / 2, -fi.h / 2, fi.w * 0.28,
                    fi.h);
            side.setArcWidth(fi.w * 0.76);
            side.setArcHeight(fi.w * 0.76);
            side.setFill(FRY_DARK[fi.ci]);
            side.setOpacity(0.42);

            javafx.scene.shape.Rectangle gloss = new javafx.scene.shape.Rectangle(fi.w * 0.10, -fi.h / 2 + fi.h * 0.07,
                    fi.w * 0.16, fi.h * 0.5);
            gloss.setArcWidth(fi.w * 0.2);
            gloss.setArcHeight(fi.w * 0.2);
            gloss.setFill(Color.WHITE);
            gloss.setOpacity(0.18);

            javafx.scene.shape.Rectangle topEdge = new javafx.scene.shape.Rectangle(-fi.w / 2, -fi.h / 2, fi.w,
                    fi.h * 0.10);
            topEdge.setArcWidth(fi.w * 0.76);
            topEdge.setArcHeight(fi.w * 0.76);
            topEdge.setFill(FRY_DARK[fi.ci]);
            topEdge.setOpacity(0.52);

            g.getChildren().addAll(body, side, gloss, topEdge);
            g.setRotate(fi.rot);
            return g;
        }

        private void positionFry(FryItem fi) {
            double restY = -curBoxH + fi.h * (0.5 - fi.popFrac);
            double hiddenY = -curBoxH * 0.38;
            double y = lerp(restY, hiddenY, fi.yFrac);
            fi.node.setTranslateX(fi.x);
            fi.node.setTranslateY(y);
            fi.node.setOpacity(fi.opacity);
        }

        private void redrawFries() {
            fryPane.getChildren().clear();
            for (FryItem fi : fries) {
                if (fi.node == null)
                    fi.node = buildFryNode(fi);
                positionFry(fi);
                fryPane.getChildren().add(fi.node);
            }
        }

        private void renderBox() {
            boxPane.getChildren().clear();
            double bw = curBoxW, bh = curBoxH;
            double flare = bw * 0.12;
            String letter = selectedIdx < LETTERS.length ? LETTERS[selectedIdx] : "S";

            javafx.scene.shape.Polygon body = new javafx.scene.shape.Polygon(
                    -bw / 2 - flare, -bh, bw / 2 + flare, -bh, bw / 2, 0, -bw / 2, 0);
            body.setFill(Color.web("#8B5E3C"));
            body.setStroke(Color.web("#5a3820"));
            body.setStrokeWidth(2.5);

            javafx.scene.shape.Polygon shade1 = new javafx.scene.shape.Polygon(
                    -bw / 2 - flare * 0.4, -bh, -bw / 2 - flare * 0.4 + bw * 0.17, -bh,
                    bw * 0.17 - bw / 2, 0, -bw / 2, 0);
            shade1.setFill(Color.web("#6F4A2F"));
            shade1.setOpacity(0.45);

            javafx.scene.shape.Polygon shade2 = new javafx.scene.shape.Polygon(
                    bw * 0.10, -bh, bw * 0.10 + bw * 0.13, -bh, bw * 0.12, 0, bw * 0.07, 0);
            shade2.setFill(Color.web("#6F4A2F"));
            shade2.setOpacity(0.28);

            javafx.scene.text.Text lbl = new javafx.scene.text.Text(letter);
            lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, bh * 0.32));
            lbl.setFill(Color.WHITE);
            lbl.setOpacity(0.90);
            lbl.setX(-lbl.getLayoutBounds().getWidth() / 2);
            lbl.setY(-bh * 0.48 + lbl.getLayoutBounds().getHeight() / 4);

            javafx.scene.shape.Polygon gloss = new javafx.scene.shape.Polygon(
                    -bw / 2 + 5, -6, bw / 2 - 5, -6, bw / 2, 0, -bw / 2, 0);
            gloss.setFill(Color.WHITE);
            gloss.setOpacity(0.06);

            boxPane.getChildren().addAll(body, shade1, shade2, lbl, gloss);
            shadow.setRadiusX(bw * 0.44);
            shadow.setTranslateX(MX);
        }

        private void popFry(int id) {
            if (id >= fries.size())
                return;
            FryItem fi = fries.get(id);
            final long startNs = System.nanoTime();
            new AnimationTimer() {
                @Override
                public void handle(long now) {
                    double t = Math.min(1.0, (now - startNs) / 540_000_000.0);
                    fi.yFrac = 1 - easeOutBounce(t);
                    fi.opacity = Math.min(1.0, t * 5);
                    positionFry(fi);
                    if (t >= 1.0)
                        stop();
                }
            }.start();
        }

        private void hideFry(int id, long delayMs, Runnable onDone) {
            PauseTransition delay = new PauseTransition(Duration.millis(delayMs));
            delay.setOnFinished(ev -> {
                if (id >= fries.size()) {
                    if (onDone != null)
                        onDone.run();
                    return;
                }
                FryItem fi = fries.get(id);
                final long startNs = System.nanoTime();
                new AnimationTimer() {
                    @Override
                    public void handle(long now) {
                        double t = Math.min(1.0, (now - startNs) / 300_000_000.0);
                        fi.yFrac = easeInCubic(t);
                        fi.opacity = 1 - t * t;
                        positionFry(fi);
                        if (t >= 1.0) {
                            stop();
                            if (onDone != null)
                                onDone.run();
                        }
                    }
                }.start();
            });
            delay.play();
        }

        private void shakeBox(Runnable onDone) {
            double[] keys = { 0, 9, -8, 7, -5, 4, -2, 1, 0 };
            final long startNs = System.nanoTime();
            new AnimationTimer() {
                @Override
                public void handle(long now) {
                    double t = Math.min(1.0, (now - startNs) / 380_000_000.0);
                    int ki = Math.min(keys.length - 1, (int) (t * keys.length));
                    rootPane.setTranslateX(MX + keys[ki]);
                    if (t >= 1.0) {
                        stop();
                        rootPane.setTranslateX(MX);
                        if (onDone != null)
                            onDone.run();
                    }
                }
            }.start();
        }

        private void resizeBox(double fromW, double fromH, double toW, double toH, Runnable onDone) {
            final long startNs = System.nanoTime();
            new AnimationTimer() {
                @Override
                public void handle(long now) {
                    double t = Math.min(1.0, (now - startNs) / 480_000_000.0);
                    double e = easeInOut(t);
                    curBoxW = lerp(fromW, toW, e);
                    curBoxH = lerp(fromH, toH, e);
                    renderBox();
                    redrawFries();
                    if (t >= 1.0) {
                        stop();
                        if (onDone != null)
                            onDone.run();
                    }
                }
            }.start();
        }

        void selectSize(int nextIdx) {
            if (nextIdx == selectedIdx || busy)
                return;
            busy = true;

            int prevIdx = selectedIdx;
            selectedIdx = nextIdx;

            int prevCount = FRY_COUNTS[Math.min(prevIdx, FRY_COUNTS.length - 1)];
            int nextCount = FRY_COUNTS[Math.min(nextIdx, FRY_COUNTS.length - 1)];
            double prevW = BOX_W[Math.min(prevIdx, BOX_W.length - 1)];
            double prevH = BOX_H[Math.min(prevIdx, BOX_H.length - 1)];
            double nextW = BOX_W[Math.min(nextIdx, BOX_W.length - 1)];
            double nextH = BOX_H[Math.min(nextIdx, BOX_H.length - 1)];
            boolean growing = nextIdx > prevIdx;

            if (growing) {
                shakeBox(() -> resizeBox(prevW, prevH, nextW, nextH, () -> {
                    int addCount = nextCount - prevCount;
                    for (int i = 0; i < prevCount; i++)
                        fries.get(i).x = makeFry(i, nextW, nextCount).x;
                    for (int i = 0; i < addCount; i++) {
                        FryItem fi = makeFry(prevCount + i, nextW, nextCount);
                        fi.node = buildFryNode(fi);
                        fries.add(fi);
                    }
                    redrawFries();
                    for (int i = 0; i < addCount; i++) {
                        final int idx = prevCount + i;
                        PauseTransition d = new PauseTransition(Duration.millis((long) (i * 38)));
                        d.setOnFinished(ev -> popFry(idx));
                        d.play();
                    }
                    PauseTransition wait = new PauseTransition(Duration.millis(addCount * 38L + 560));
                    wait.setOnFinished(ev -> busy = false);
                    wait.play();
                }));
            } else {
                shakeBox(() -> {
                    int removeCount = prevCount - nextCount;
                    int[] remaining = { removeCount };
                    for (int i = 0; i < removeCount; i++) {
                        final int id = prevCount - 1 - i;
                        hideFry(id, (long) (i * 30), () -> {
                            remaining[0]--;
                            if (remaining[0] == 0) {
                                while (fries.size() > nextCount)
                                    fries.remove(fries.size() - 1);
                                resizeBox(prevW, prevH, nextW, nextH, () -> {
                                    for (int j = 0; j < fries.size(); j++) {
                                        FryItem fi2 = fries.get(j);
                                        FryItem desc = makeFry(j, nextW, nextCount);
                                        fi2.x = desc.x;
                                        fi2.w = desc.w;
                                        fi2.node = buildFryNode(fi2);
                                    }
                                    redrawFries();
                                    busy = false;
                                });
                            }
                        });
                    }
                });
            }
        }
    }
}