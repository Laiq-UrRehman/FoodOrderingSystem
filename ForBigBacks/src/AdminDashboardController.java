import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardController {

    @FXML
    private Label restaurantBadge;
    @FXML
    private Button menuNavButton;
    @FXML
    private Button offersNavButton;
    @FXML
    private VBox menuPanel;
    @FXML
    private VBox offersPanel;
    @FXML
    private TextField newItemName;
    @FXML
    private TextField newItemCategory;
    @FXML
    private TextField newItemPrice;
    @FXML
    private TextField newItemQuantity;
    @FXML
    private Label menuFormError;
    @FXML
    private VBox menuItemsContainer;
    @FXML
    private Label menuCountLabel;
    @FXML
    private TextField newOfferCode;
    @FXML
    private TextField newOfferDescription;
    @FXML
    private TextField newOfferPoints;
    @FXML
    private TextField newOfferDiscount;
    @FXML
    private TextField newOfferMinOrder;
    @FXML
    private Label offerFormError;
    @FXML
    private VBox offersContainer;
    @FXML
    private Label offerCountLabel;

    private RestaurantAdmin admin;
    private LoyaltyOfferManager offerManager;

    // ═════════════════════════════════════════════════════════════════════
    // Init
    // ═════════════════════════════════════════════════════════════════════

    @FXML
    public void initialize() {
        admin = SessionManager.getInstance().getCurrentAdmin();
        if (admin == null) {
            SceneManager.getInstance().switchTo("Login");
            return;
        }
        if (admin.getRestaurant() != null)
            restaurantBadge.setText(admin.getRestaurant().getName().toUpperCase());

        offerManager = admin.getOfferManager();
        loadMenuItems();
        loadOffers();
        setNavActive(menuNavButton);
        setNavInactive(offersNavButton);
    }

    // ═════════════════════════════════════════════════════════════════════
    // Nav
    // ═════════════════════════════════════════════════════════════════════

    @FXML
    private void showMenuPanel() {
        menuPanel.setVisible(true);
        menuPanel.setManaged(true);
        offersPanel.setVisible(false);
        offersPanel.setManaged(false);
        setNavActive(menuNavButton);
        setNavInactive(offersNavButton);
    }

    @FXML
    private void showOffersPanel() {
        offersPanel.setVisible(true);
        offersPanel.setManaged(true);
        menuPanel.setVisible(false);
        menuPanel.setManaged(false);
        setNavActive(offersNavButton);
        setNavInactive(menuNavButton);
    }

    private void setNavActive(Button btn) {
        btn.getStyleClass().remove("dashboard-nav-button");
        if (!btn.getStyleClass().contains("dashboard-nav-button-active"))
            btn.getStyleClass().add("dashboard-nav-button-active");
    }

    private void setNavInactive(Button btn) {
        btn.getStyleClass().remove("dashboard-nav-button-active");
        if (!btn.getStyleClass().contains("dashboard-nav-button"))
            btn.getStyleClass().add("dashboard-nav-button");
    }

    // ═════════════════════════════════════════════════════════════════════
    // Menu Items
    // ═════════════════════════════════════════════════════════════════════

    private void loadMenuItems() {
        menuItemsContainer.getChildren().clear();

        if (admin.getRestaurant() == null || admin.getRestaurant().getMenu() == null) {
            menuCountLabel.setText("(0 items)");
            menuItemsContainer.getChildren().add(makeEmptyLabel("No restaurant or menu found."));
            return;
        }

        List<FoodItem> items = admin.getRestaurant().getMenu().getItems();
        menuCountLabel.setText("(" + items.size() + " item" + (items.size() == 1 ? "" : "s") + ")");

        if (items.isEmpty()) {
            menuItemsContainer.getChildren().add(makeEmptyLabel("No menu items yet. Add one above."));
            return;
        }

        for (FoodItem item : items)
            menuItemsContainer.getChildren().add(buildMenuItemRow(item));
    }

    // Returns a VBox: the item row on top + collapsible customization panel below
    private VBox buildMenuItemRow(FoodItem item) {
        VBox wrapper = new VBox(0);

        // ── Main row ──
        HBox row = new HBox(12);
        row.getStyleClass().add("admin-item-row");
        row.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label nameLabel = new Label(item.getName());
        nameLabel.getStyleClass().add("admin-item-name");

        Label detailLabel = new Label(
                item.getCategory()
                        + "  ·  Qty: " + item.getQuantity()
                        + "  ·  ★ " + String.format("%.1f", item.getRating())
                        + "  ·  Orders: " + item.getOrderCount());
        detailLabel.getStyleClass().add("admin-item-detail");

        info.getChildren().addAll(nameLabel, detailLabel);

        Label priceLabel = new Label("Rs. " + (int) item.getPrice());
        priceLabel.getStyleClass().add("admin-item-price");

        // Customization toggle button
        int groupCount = item.getCustomizationGroups().size();
        Button customBtn = new Button(
                groupCount > 0 ? "Customizations (" + groupCount + ")" : "Customizations");
        customBtn.getStyleClass().add("admin-customization-toggle");

        Button removeBtn = new Button("Remove");
        removeBtn.getStyleClass().add("admin-remove-button");
        removeBtn.setOnAction(e -> {
            admin.removeFoodItem(item);
            loadMenuItems();
        });

        // ── Customization panel ──
        VBox customPanel = buildCustomizationPanel(item);
        customPanel.setVisible(false);
        customPanel.setManaged(false);

        customBtn.setOnAction(e -> {
            boolean show = !customPanel.isVisible();
            customPanel.setVisible(show);
            customPanel.setManaged(show);
            int cnt = item.getCustomizationGroups().size();
            customBtn.setText(show ? "Hide Customizations"
                    : (cnt > 0 ? "Customizations (" + cnt + ")" : "Customizations"));
        });

        row.getChildren().addAll(info, priceLabel, customBtn, removeBtn);
        wrapper.getChildren().addAll(row, customPanel);
        return wrapper;
    }

    private VBox buildCustomizationPanel(FoodItem item) {
        VBox panel = new VBox(12);
        panel.getStyleClass().add("admin-customization-panel");

        // ── Existing groups ──
        List<CustomizationGroup> groups = item.getCustomizationGroups();
        if (!groups.isEmpty()) {
            Label existingTitle = new Label("EXISTING GROUPS");
            existingTitle.getStyleClass().add("admin-form-label");
            panel.getChildren().add(existingTitle);

            for (CustomizationGroup group : groups) {
                HBox groupRow = new HBox(10);
                groupRow.getStyleClass().add("admin-customization-group-row");
                groupRow.setAlignment(Pos.CENTER_LEFT);

                VBox groupInfo = new VBox(3);
                HBox.setHgrow(groupInfo, Priority.ALWAYS);

                Label groupNameLabel = new Label(group.getGroupName());
                groupNameLabel.setStyle(
                        "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #cccccc;");

                List<String> optionParts = new ArrayList<>();
                for (int i = 0; i < group.getOptions().size(); i++) {
                    double charge = group.getExtraCharges().get(i);
                    optionParts.add(group.getOptions().get(i)
                            + (charge > 0 ? " (+Rs." + (int) charge + ")" : " (free)"));
                }
                Label optionsLabel = new Label(String.join("  ·  ", optionParts));
                optionsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");

                groupInfo.getChildren().addAll(groupNameLabel, optionsLabel);

                Button removeGroupBtn = new Button("Remove");
                removeGroupBtn.getStyleClass().add("admin-remove-button");
                removeGroupBtn.setOnAction(e -> {
                    admin.removeCustomization(item.getFoodID(), group.getGroupName());
                    loadMenuItems();
                });

                groupRow.getChildren().addAll(groupInfo, removeGroupBtn);
                panel.getChildren().add(groupRow);
            }
        }

        // ── Add new group form ──
        Label addTitle = new Label("ADD NEW GROUP");
        addTitle.getStyleClass().add("admin-form-label");

        TextField groupNameField = new TextField();
        groupNameField.setPromptText("Group name  (e.g. Size, Spice Level, Add-ons)");
        groupNameField.getStyleClass().add("input-text-field");

        // Dynamic option rows
        VBox optionRowsContainer = new VBox(8);
        optionRowsContainer.getChildren().add(buildOptionInputRow());

        Button addOptionRowBtn = new Button("+ Add Another Option");
        addOptionRowBtn.getStyleClass().add("admin-customization-add-option-btn");
        addOptionRowBtn.setOnAction(e -> optionRowsContainer.getChildren().add(buildOptionInputRow()));

        Label saveError = new Label("");
        saveError.getStyleClass().add("text-error-message");

        Button saveGroupBtn = new Button("Save Group");
        saveGroupBtn.getStyleClass().add("action-button-brown");
        saveGroupBtn.setOnAction(e -> {
            saveError.setText("");
            String gName = groupNameField.getText().trim();
            if (gName.isEmpty()) {
                saveError.setText("Group name is required.");
                return;
            }
            for (CustomizationGroup existing : item.getCustomizationGroups()) {
                if (existing.getGroupName().equalsIgnoreCase(gName)) {
                    saveError.setText("A group named '" + gName + "' already exists.");
                    return;
                }
            }
            CustomizationGroup newGroup = new CustomizationGroup(gName);
            boolean hasOptions = false;
            for (javafx.scene.Node node : optionRowsContainer.getChildren()) {
                if (!(node instanceof HBox))
                    continue;
                HBox optRow = (HBox) node;
                TextField optName = (TextField) optRow.getChildren().get(0);
                TextField optCharge = (TextField) optRow.getChildren().get(1);
                String nameText = optName.getText().trim();
                if (nameText.isEmpty())
                    continue;
                double charge = 0;
                try {
                    String chargeText = optCharge.getText().trim();
                    if (!chargeText.isEmpty())
                        charge = Double.parseDouble(chargeText);
                } catch (NumberFormatException ex) {
                    saveError.setText("Extra charge must be a valid number.");
                    return;
                }
                if (charge < 0) {
                    saveError.setText("Extra charge cannot be negative.");
                    return;
                }
                newGroup.addOption(nameText, charge);
                hasOptions = true;
            }
            if (!hasOptions) {
                saveError.setText("Add at least one option.");
                return;
            }
            admin.addCustomization(item.getFoodID(), newGroup);
            loadMenuItems();
        });

        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER_LEFT);
        btnRow.getChildren().addAll(saveGroupBtn, addOptionRowBtn, saveError);

        panel.getChildren().addAll(addTitle, groupNameField, optionRowsContainer, btnRow);
        return panel;
    }

    // One name + extra-charge pair row for the add-group form
    private HBox buildOptionInputRow() {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        TextField optName = new TextField();
        optName.setPromptText("Option name  (e.g. Large, Extra Spicy)");
        optName.getStyleClass().add("input-text-field");
        HBox.setHgrow(optName, Priority.ALWAYS);

        TextField optCharge = new TextField();
        optCharge.setPromptText("Extra charge Rs.  (0 = free)");
        optCharge.getStyleClass().add("input-text-field");
        optCharge.setPrefWidth(180);

        row.getChildren().addAll(optName, optCharge);
        return row;
    }

    @FXML
    private void handleAddMenuItem() {
        menuFormError.setText("");

        String name = newItemName.getText().trim();
        String category = newItemCategory.getText().trim();
        String priceStr = newItemPrice.getText().trim();
        String qtyStr = newItemQuantity.getText().trim();

        if (name.isEmpty() || category.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
            menuFormError.setText("All fields are required.");
            return;
        }
        if (!name.matches("[a-zA-Z][a-zA-Z0-9 '',.-]{1,49}")) {
            menuFormError.setText("Item name must start with a letter, 2–50 characters.");
            return;
        }
        if (!category.matches("[a-zA-Z ]{2,30}")) {
            menuFormError.setText("Category must be 2–30 letters only.");
            return;
        }

        double price;
        int qty;
        try {
            price = Double.parseDouble(priceStr);
            qty = Integer.parseInt(qtyStr);
        } catch (NumberFormatException e) {
            menuFormError.setText("Price and quantity must be valid numbers.");
            return;
        }
        if (price <= 0 || price > 100000) {
            menuFormError.setText("Price must be between 1 and 100,000 PKR.");
            return;
        }
        if (qty < 0 || qty > 9999) {
            menuFormError.setText("Quantity must be between 0 and 9999.");
            return;
        }

        FoodItem newItem = new FoodItem("FI" + System.currentTimeMillis(), name, price, category, qty);
        admin.addFoodItem(newItem);

        newItemName.clear();
        newItemCategory.clear();
        newItemPrice.clear();
        newItemQuantity.clear();
        loadMenuItems();
    }

    // ═════════════════════════════════════════════════════════════════════
    // Loyalty Offers
    // ═════════════════════════════════════════════════════════════════════

    private void loadOffers() {
        offersContainer.getChildren().clear();
        offerManager.refresh();

        List<LoyaltyOffer> offers = offerManager.getAllOffers();
        offerCountLabel.setText("(" + offers.size() + " offer" + (offers.size() == 1 ? "" : "s") + ")");

        if (offers.isEmpty()) {
            offersContainer.getChildren().add(makeEmptyLabel("No loyalty offers yet. Add one above."));
            return;
        }
        for (LoyaltyOffer offer : offers)
            offersContainer.getChildren().add(buildOfferRow(offer));
    }

    private HBox buildOfferRow(LoyaltyOffer offer) {
        HBox row = new HBox(12);
        row.getStyleClass().add("admin-item-row");
        row.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label codeLabel = new Label(offer.getOfferCode());
        codeLabel.getStyleClass().add("admin-offer-code");

        Label descLabel = new Label(offer.getDescription());
        descLabel.getStyleClass().add("admin-offer-description");

        Label metaLabel = new Label(
                offer.getPointsRequired() + " pts  ·  −Rs. "
                        + (int) offer.getDiscountPKR()
                        + "  ·  Min order Rs. " + (int) offer.getMinOrderPKR());
        metaLabel.getStyleClass().add("admin-offer-meta");

        info.getChildren().addAll(codeLabel, descLabel, metaLabel);

        Button removeBtn = new Button("Remove");
        removeBtn.getStyleClass().add("admin-remove-button");
        removeBtn.setOnAction(e -> {
            offerManager.removeOffer(offer.getOfferCode());
            loadOffers();
        });

        row.getChildren().addAll(info, removeBtn);
        return row;
    }

    @FXML
    private void handleAddOffer() {
        offerFormError.setText("");

        String code = newOfferCode.getText().trim();
        String desc = newOfferDescription.getText().trim();
        String pointsStr = newOfferPoints.getText().trim();
        String discountStr = newOfferDiscount.getText().trim();
        String minOrderStr = newOfferMinOrder.getText().trim();

        if (code.isEmpty() || desc.isEmpty() || pointsStr.isEmpty()
                || discountStr.isEmpty() || minOrderStr.isEmpty()) {
            offerFormError.setText("All fields are required.");
            return;
        }

        int points;
        double discount, minOrder;
        try {
            points = Integer.parseInt(pointsStr);
            discount = Double.parseDouble(discountStr);
            minOrder = Double.parseDouble(minOrderStr);
        } catch (NumberFormatException e) {
            offerFormError.setText("Points, discount, and min order must be valid numbers.");
            return;
        }
        if (points <= 0 || discount <= 0 || minOrder <= 0) {
            offerFormError.setText("All numeric values must be positive.");
            return;
        }
        if (offerManager.findByCode(code) != null) {
            offerFormError.setText("An offer with code " + code + " already exists.");
            return;
        }

        offerManager.addOffer(new LoyaltyOffer(code, desc, points, discount, minOrder));
        newOfferCode.clear();
        newOfferDescription.clear();
        newOfferPoints.clear();
        newOfferDiscount.clear();
        newOfferMinOrder.clear();
        loadOffers();
    }

    // ═════════════════════════════════════════════════════════════════════
    // Helpers
    // ═════════════════════════════════════════════════════════════════════

    private Label makeEmptyLabel(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("admin-empty-label");
        return lbl;
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneManager.getInstance().switchTo("Login");
    }
}