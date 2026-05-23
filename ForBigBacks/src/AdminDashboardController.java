import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.List;

public class AdminDashboardController {

    // ── Top bar ───────────────────────────────────────────────────────────
    @FXML
    private Label restaurantBadge;

    // ── Sidebar nav ───────────────────────────────────────────────────────
    @FXML
    private Button menuNavButton;
    @FXML
    private Button offersNavButton;

    // ── Panels ────────────────────────────────────────────────────────────
    @FXML
    private VBox menuPanel;
    @FXML
    private VBox offersPanel;

    // ── Menu form ─────────────────────────────────────────────────────────
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

    // ── Offer form ────────────────────────────────────────────────────────
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

    // ── State ─────────────────────────────────────────────────────────────
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

        if (admin.getRestaurant() != null) {
            restaurantBadge.setText(admin.getRestaurant().getName());
        }

        // Reuse the admin's own offerManager so adds/removes are immediately visible
        offerManager = admin.getOfferManager();

        loadMenuItems();
        loadOffers();

        setNavActive(menuNavButton);
        setNavInactive(offersNavButton);
    }

    // ═════════════════════════════════════════════════════════════════════
    // Nav switching
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
    // Menu Items — load + add + remove
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

        for (FoodItem item : items) {
            menuItemsContainer.getChildren().add(buildMenuItemRow(item));
        }
    }

    private HBox buildMenuItemRow(FoodItem item) {
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

        Button removeBtn = new Button("Remove");
        removeBtn.getStyleClass().add("admin-remove-button");
        removeBtn.setOnAction(e -> {
            // removeFoodItem() inside RestaurantAdmin now calls persistRestaurant() internally
            admin.removeFoodItem(item);
            loadMenuItems();
        });

        row.getChildren().addAll(info, priceLabel, removeBtn);
        return row;
    }

    @FXML
    private void handleAddMenuItem() {
        menuFormError.setText("");

        String name     = newItemName.getText().trim();
        String category = newItemCategory.getText().trim();
        String priceStr = newItemPrice.getText().trim();
        String qtyStr   = newItemQuantity.getText().trim();

        if (name.isEmpty() || category.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
            menuFormError.setText("All fields are required.");
            return;
        }

        double price;
        int qty;
        try {
            price = Double.parseDouble(priceStr);
            qty   = Integer.parseInt(qtyStr);
        } catch (NumberFormatException e) {
            menuFormError.setText("Price and quantity must be valid numbers.");
            return;
        }

        if (price <= 0 || qty < 0) {
            menuFormError.setText("Price must be positive; quantity cannot be negative.");
            return;
        }

        String foodID = "FI" + System.currentTimeMillis();
        FoodItem newItem = new FoodItem(foodID, name, price, category, qty);

        // addFoodItem() inside RestaurantAdmin now calls persistRestaurant() internally
        admin.addFoodItem(newItem);

        newItemName.clear();
        newItemCategory.clear();
        newItemPrice.clear();
        newItemQuantity.clear();

        loadMenuItems();
    }

    // ═════════════════════════════════════════════════════════════════════
    // Loyalty Offers — load + add + remove
    // ═════════════════════════════════════════════════════════════════════

    private void loadOffers() {
        offersContainer.getChildren().clear();

        // refresh() re-reads loyalty_offers.txt so the list is always current
        offerManager.refresh();

        List<LoyaltyOffer> offers = offerManager.getAllOffers();
        offerCountLabel.setText("(" + offers.size() + " offer" + (offers.size() == 1 ? "" : "s") + ")");

        if (offers.isEmpty()) {
            offersContainer.getChildren().add(makeEmptyLabel("No loyalty offers yet. Add one above."));
            return;
        }

        for (LoyaltyOffer offer : offers) {
            offersContainer.getChildren().add(buildOfferRow(offer));
        }
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
            // removeOffer() saves to loyalty_offers.txt automatically via LoyaltyOfferManager
            offerManager.removeOffer(offer.getOfferCode());
            loadOffers();
        });

        row.getChildren().addAll(info, removeBtn);
        return row;
    }

    @FXML
    private void handleAddOffer() {
        offerFormError.setText("");

        String code        = newOfferCode.getText().trim();
        String desc        = newOfferDescription.getText().trim();
        String pointsStr   = newOfferPoints.getText().trim();
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
            points   = Integer.parseInt(pointsStr);
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

        // addOffer() saves to loyalty_offers.txt automatically via LoyaltyOfferManager
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