// Updated: Removed loyalty offer fields, offerManager, showOffersPanel(), loadOffers(), buildOfferRow(), handleAddOffer()
// Updated: Added restaurant offers section — newOfferTitle, newOfferDescription, newOfferDiscount, restaurantOffersContainer, offerCountLabel
// Updated: Added loadRestaurantOffers(), handleAddRestaurantOffer()
// Updated: initialize() now calls loadRestaurantOffers() instead of loadOffers()

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
    private TextField newOfferTitle;
    @FXML
    private TextField newOfferDescription;
    @FXML
    private TextField newOfferDiscount;
    @FXML
    private Label offerFormError;
    @FXML
    private VBox restaurantOffersContainer;
    @FXML
    private Label offerCountLabel;

    private RestaurantAdmin admin;

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

        loadMenuItems();
        loadRestaurantOffers();
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

        int groupCount = item.getCustomizationGroups().size();
        Button customBtn = new Button(
                groupCount > 0 ? "Customizations (" + groupCount + ")" : "Customizations");
        customBtn.getStyleClass().add("admin-customization-toggle");

        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("admin-customization-toggle");

        Button removeBtn = new Button("Remove");
        removeBtn.getStyleClass().add("admin-remove-button");
        removeBtn.setOnAction(e -> {
            admin.removeFoodItem(item);
            loadMenuItems();
        });

        // ── Edit form (hidden by default) ──
        VBox editForm = buildItemEditForm(item, nameLabel, detailLabel, priceLabel, wrapper);
        editForm.setVisible(false);
        editForm.setManaged(false);

        editBtn.setOnAction(e -> {
            // collapse customization panel if open
            if (wrapper.getChildren().size() > 2) {
                javafx.scene.Node customPanel = wrapper.getChildren().get(1);
                if (customPanel.isVisible() && customPanel != editForm) {
                    customPanel.setVisible(false);
                    customPanel.setManaged(false);
                    customBtn.setText(groupCount > 0
                            ? "Customizations (" + item.getCustomizationGroups().size() + ")"
                            : "Customizations");
                    customBtn.setStyle("");
                    customBtn.getStyleClass().setAll("admin-customization-toggle");
                }
            }
            boolean show = !editForm.isVisible();
            editForm.setVisible(show);
            editForm.setManaged(show);
            editBtn.setText(show ? "Cancel  ✕" : "Edit");
            if (show) {
                editBtn.setStyle(
                        "-fx-background-color: #2a2a2a; -fx-text-fill: #888888;" +
                                "-fx-font-size: 12px; -fx-font-weight: bold;" +
                                "-fx-padding: 7 14; -fx-background-radius: 6; -fx-cursor: hand;" +
                                "-fx-border-color: #3a3a3a; -fx-border-radius: 6;");
            } else {
                editBtn.setStyle("");
                editBtn.getStyleClass().setAll("admin-customization-toggle");
            }
        });

        // ── Customization panel (hidden by default) ──
        VBox customPanel = buildCustomizationPanel(item);
        customPanel.setVisible(false);
        customPanel.setManaged(false);

        customBtn.setOnAction(e -> {
            // collapse edit form if open
            if (editForm.isVisible()) {
                editForm.setVisible(false);
                editForm.setManaged(false);
                editBtn.setText("Edit");
                editBtn.setStyle("");
                editBtn.getStyleClass().setAll("admin-customization-toggle");
            }
            boolean show = !customPanel.isVisible();
            customPanel.setVisible(show);
            customPanel.setManaged(show);
            int cnt = item.getCustomizationGroups().size();
            customBtn.setText(show ? "Hide Customizations"
                    : (cnt > 0 ? "Customizations (" + cnt + ")" : "Customizations"));
            if (!show) {
                customBtn.setStyle("");
                customBtn.getStyleClass().setAll("admin-customization-toggle");
            }
        });

        row.getChildren().addAll(info, priceLabel, customBtn, editBtn, removeBtn);
        wrapper.getChildren().addAll(row, editForm, customPanel);
        return wrapper;
    }

    private VBox buildItemEditForm(FoodItem item, Label nameLabel,
            Label detailLabel, Label priceLabel, VBox wrapper) {

        VBox form = new VBox(12);
        form.setStyle(
                "-fx-background-color: #141414;" +
                        "-fx-border-color: #2a2a2a transparent transparent transparent;" +
                        "-fx-border-width: 1 0 0 0;" +
                        "-fx-padding: 16;");

        Label formTitle = new Label("EDIT ITEM");
        formTitle.getStyleClass().add("admin-form-label");

        // Row 1: Name + Category
        HBox row1 = new HBox(12);

        VBox nameBox = new VBox(6);
        HBox.setHgrow(nameBox, Priority.ALWAYS);
        Label nameFieldLabel = new Label("ITEM NAME");
        nameFieldLabel.getStyleClass().add("admin-form-label");
        TextField nameField = new TextField(item.getName());
        nameField.getStyleClass().add("input-text-field");
        nameField.setMaxWidth(Double.MAX_VALUE);
        nameBox.getChildren().addAll(nameFieldLabel, nameField);

        VBox catBox = new VBox(6);
        HBox.setHgrow(catBox, Priority.ALWAYS);
        Label catFieldLabel = new Label("CATEGORY");
        catFieldLabel.getStyleClass().add("admin-form-label");
        TextField categoryField = new TextField(item.getCategory());
        categoryField.getStyleClass().add("input-text-field");
        categoryField.setMaxWidth(Double.MAX_VALUE);
        catBox.getChildren().addAll(catFieldLabel, categoryField);

        row1.getChildren().addAll(nameBox, catBox);

        // Row 2: Price + Quantity
        HBox row2 = new HBox(12);

        VBox priceBox = new VBox(6);
        HBox.setHgrow(priceBox, Priority.ALWAYS);
        Label priceFieldLabel = new Label("PRICE (PKR)");
        priceFieldLabel.getStyleClass().add("admin-form-label");
        TextField priceField = new TextField(String.valueOf((int) item.getPrice()));
        priceField.getStyleClass().add("input-text-field");
        priceField.setMaxWidth(Double.MAX_VALUE);
        priceBox.getChildren().addAll(priceFieldLabel, priceField);

        VBox qtyBox = new VBox(6);
        HBox.setHgrow(qtyBox, Priority.ALWAYS);
        Label qtyFieldLabel = new Label("STOCK QUANTITY");
        qtyFieldLabel.getStyleClass().add("admin-form-label");
        TextField qtyField = new TextField(String.valueOf(item.getQuantity()));
        qtyField.getStyleClass().add("input-text-field");
        qtyField.setMaxWidth(Double.MAX_VALUE);
        qtyBox.getChildren().addAll(qtyFieldLabel, qtyField);

        row2.getChildren().addAll(priceBox, qtyBox);

        // Error + Save button
        Label editError = new Label("");
        editError.getStyleClass().add("text-error-message");

        Button saveBtn = new Button("Save Changes");
        saveBtn.getStyleClass().add("action-button-brown");
        saveBtn.setOnAction(e -> {
            editError.setText("");

            String newName = nameField.getText().trim();
            String newCategory = categoryField.getText().trim();
            String priceStr = priceField.getText().trim();
            String qtyStr = qtyField.getText().trim();

            if (newName.isEmpty() || newCategory.isEmpty()
                    || priceStr.isEmpty() || qtyStr.isEmpty()) {
                editError.setText("All fields are required.");
                return;
            }
            if (!newName.matches("[a-zA-Z][a-zA-Z0-9 '',.-]{1,49}")) {
                editError.setText("Item name must start with a letter, 2–50 characters.");
                return;
            }
            if (!newCategory.matches("[a-zA-Z ]{2,30}")) {
                editError.setText("Category must be 2–30 letters only.");
                return;
            }

            double newPrice;
            int newQty;
            try {
                newPrice = Double.parseDouble(priceStr);
                newQty = Integer.parseInt(qtyStr);
            } catch (NumberFormatException ex) {
                editError.setText("Price and quantity must be valid numbers.");
                return;
            }
            if (newPrice <= 0 || newPrice > 100000) {
                editError.setText("Price must be between 1 and 100,000 PKR.");
                return;
            }
            if (newQty < 0 || newQty > 9999) {
                editError.setText("Quantity must be between 0 and 9999.");
                return;
            }

            admin.updateFoodItem(item.getFoodID(), newName, newCategory, newPrice, newQty);
            loadMenuItems();
        });

        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER_LEFT);
        btnRow.getChildren().addAll(saveBtn, editError);

        form.getChildren().addAll(formTitle, row1, row2, btnRow);
        return form;
    }

    private VBox buildCustomizationPanel(FoodItem item) {
        VBox panel = new VBox(12);
        panel.getStyleClass().add("admin-customization-panel");

        List<CustomizationGroup> groups = item.getCustomizationGroups();
        if (!groups.isEmpty()) {
            Label existingTitle = new Label("EXISTING GROUPS");
            existingTitle.getStyleClass().add("admin-form-label");
            panel.getChildren().add(existingTitle);

            for (CustomizationGroup group : new ArrayList<>(groups)) {
                panel.getChildren().add(buildGroupRow(item, group, panel));
            }
        }

        // ── Add new group form ──
        Label addTitle = new Label("ADD NEW GROUP");
        addTitle.getStyleClass().add("admin-form-label");

        TextField groupNameField = new TextField();
        groupNameField.setPromptText("Group name  (e.g. Size, Spice Level, Add-ons)");
        groupNameField.getStyleClass().add("input-text-field");

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

    private VBox buildGroupRow(FoodItem item, CustomizationGroup group, VBox parentPanel) {
        VBox groupWrapper = new VBox(0);

        // ── Display row ──
        HBox groupRow = new HBox(10);
        groupRow.getStyleClass().add("admin-customization-group-row");
        groupRow.setAlignment(Pos.CENTER_LEFT);

        VBox groupInfo = new VBox(3);
        HBox.setHgrow(groupInfo, Priority.ALWAYS);

        Label groupNameLabel = new Label(group.getGroupName());
        groupNameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #cccccc;");

        List<String> optionParts = new ArrayList<>();
        for (int i = 0; i < group.getOptions().size(); i++) {
            double charge = group.getExtraCharges().get(i);
            optionParts.add(group.getOptions().get(i)
                    + (charge > 0 ? " (+Rs." + (int) charge + ")" : " (free)"));
        }
        Label optionsLabel = new Label(String.join("  ·  ", optionParts));
        optionsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");

        groupInfo.getChildren().addAll(groupNameLabel, optionsLabel);

        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("admin-customization-toggle");

        Button removeGroupBtn = new Button("Remove");
        removeGroupBtn.getStyleClass().add("admin-remove-button");
        removeGroupBtn.setOnAction(e -> {
            admin.removeCustomization(item.getFoodID(), group.getGroupName());
            loadMenuItems();
        });

        groupRow.getChildren().addAll(groupInfo, editBtn, removeGroupBtn);

        // ── Inline edit form (hidden by default) ──
        VBox editForm = buildGroupEditForm(item, group, groupWrapper, groupNameLabel, optionsLabel);
        editForm.setVisible(false);
        editForm.setManaged(false);

        editBtn.setOnAction(e -> {
            boolean show = !editForm.isVisible();
            editForm.setVisible(show);
            editForm.setManaged(show);
            editBtn.setText(show ? "Cancel  ✕" : "Edit");
            if (show) {
                editBtn.setStyle(
                        "-fx-background-color: #2a2a2a; -fx-text-fill: #888888;" +
                                "-fx-font-size: 12px; -fx-font-weight: bold;" +
                                "-fx-padding: 7 14; -fx-background-radius: 6; -fx-cursor: hand;" +
                                "-fx-border-color: #3a3a3a; -fx-border-radius: 6;");
            } else {
                editBtn.setStyle("");
                editBtn.getStyleClass().setAll("admin-customization-toggle");
            }
        });

        groupWrapper.getChildren().addAll(groupRow, editForm);
        return groupWrapper;
    }

    private VBox buildGroupEditForm(FoodItem item, CustomizationGroup group,
            VBox groupWrapper, Label groupNameLabel, Label optionsLabel) {

        VBox form = new VBox(10);
        form.getStyleClass().add("admin-customization-panel");
        form.setStyle("-fx-background-color: #111111; -fx-border-color: #2a2a2a; " +
                "-fx-border-width: 0 0 1 0; -fx-padding: 12 16;");

        Label editTitle = new Label("EDIT GROUP: " + group.getGroupName().toUpperCase());
        editTitle.getStyleClass().add("admin-form-label");

        TextField groupNameField = new TextField(group.getGroupName());
        groupNameField.getStyleClass().add("input-text-field");
        groupNameField.setPromptText("Group name");

        Label optionsTitle = new Label("OPTIONS");
        optionsTitle.getStyleClass().add("admin-form-label");

        VBox optionRowsContainer = new VBox(8);
        for (int i = 0; i < group.getOptions().size(); i++) {
            HBox row = buildOptionInputRow();
            ((TextField) row.getChildren().get(0)).setText(group.getOptions().get(i));
            double charge = group.getExtraCharges().get(i);
            ((TextField) row.getChildren().get(1)).setText(charge > 0 ? String.valueOf((int) charge) : "");
            optionRowsContainer.getChildren().add(row);
        }
        optionRowsContainer.getChildren().add(buildOptionInputRow());

        Button addOptionRowBtn = new Button("+ Add Another Option");
        addOptionRowBtn.getStyleClass().add("admin-customization-add-option-btn");
        addOptionRowBtn.setOnAction(e -> optionRowsContainer.getChildren().add(buildOptionInputRow()));

        Label editError = new Label("");
        editError.getStyleClass().add("text-error-message");

        Button saveBtn = new Button("Save Changes");
        saveBtn.getStyleClass().add("action-button-brown");
        saveBtn.setOnAction(e -> {
            editError.setText("");
            String newName = groupNameField.getText().trim();
            if (newName.isEmpty()) {
                editError.setText("Group name is required.");
                return;
            }
            for (CustomizationGroup existing : item.getCustomizationGroups()) {
                if (existing.getGroupName().equalsIgnoreCase(newName)
                        && !existing.getGroupName().equalsIgnoreCase(group.getGroupName())) {
                    editError.setText("Another group named '" + newName + "' already exists.");
                    return;
                }
            }
            CustomizationGroup updated = new CustomizationGroup(newName);
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
                    editError.setText("Extra charge must be a valid number.");
                    return;
                }
                if (charge < 0) {
                    editError.setText("Extra charge cannot be negative.");
                    return;
                }
                updated.addOption(nameText, charge);
                hasOptions = true;
            }
            if (!hasOptions) {
                editError.setText("At least one option is required.");
                return;
            }
            admin.updateCustomization(item.getFoodID(), group.getGroupName(), updated);
            loadMenuItems();
        });

        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER_LEFT);
        btnRow.getChildren().addAll(saveBtn, addOptionRowBtn, editError);

        form.getChildren().addAll(editTitle, groupNameField, optionsTitle,
                optionRowsContainer, btnRow);
        return form;
    }

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
    // Restaurant Offers
    // ═════════════════════════════════════════════════════════════════════

    private void loadRestaurantOffers() {
        restaurantOffersContainer.getChildren().clear();

        List<RestaurantOffer> offers = admin.getRestaurant().getRestaurantOffers();
        offerCountLabel.setText("(" + offers.size() + " offer" + (offers.size() == 1 ? "" : "s") + ")");

        if (offers.isEmpty()) {
            restaurantOffersContainer.getChildren().add(makeEmptyLabel("No offers yet. Add one above."));
            return;
        }

        for (RestaurantOffer offer : offers)
            restaurantOffersContainer.getChildren().add(buildRestaurantOfferRow(offer));
    }

    private HBox buildRestaurantOfferRow(RestaurantOffer offer) {
        HBox row = new HBox(12);
        row.getStyleClass().add("admin-item-row");
        row.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label titleLabel = new Label(offer.getTitle());
        titleLabel.getStyleClass().add("admin-offer-code");

        Label descLabel = new Label(offer.getDescription());
        descLabel.getStyleClass().add("admin-offer-description");

        Label discountLabel = new Label((int) offer.getDiscountPercent() + "% off");
        discountLabel.getStyleClass().add("admin-offer-meta");

        info.getChildren().addAll(titleLabel, descLabel, discountLabel);

        Button removeBtn = new Button("Remove");
        removeBtn.getStyleClass().add("admin-remove-button");
        removeBtn.setOnAction(e -> {
            admin.removeRestaurantOffer(offer.getOfferID());
            loadRestaurantOffers();
        });

        row.getChildren().addAll(info, removeBtn);
        return row;
    }

    @FXML
    private void handleAddRestaurantOffer() {
        offerFormError.setText("");

        String title = newOfferTitle.getText().trim();
        String desc = newOfferDescription.getText().trim();
        String discountStr = newOfferDiscount.getText().trim();

        if (title.isEmpty() || desc.isEmpty() || discountStr.isEmpty()) {
            offerFormError.setText("All fields are required.");
            return;
        }

        double discount;
        try {
            discount = Double.parseDouble(discountStr);
        } catch (NumberFormatException e) {
            offerFormError.setText("Discount must be a valid number.");
            return;
        }

        if (discount <= 0) {
            offerFormError.setText("Discount must be a positive number.");
            return;
        }
        if (discount > 100) {
            offerFormError.setText("Discount cannot exceed 100%.");
            return;
        }

        admin.addRestaurantOffer(new RestaurantOffer(
                "RO-" + System.currentTimeMillis(), title, desc, discount));

        newOfferTitle.clear();
        newOfferDescription.clear();
        newOfferDiscount.clear();
        loadRestaurantOffers();
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