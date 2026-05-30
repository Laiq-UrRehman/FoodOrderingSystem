import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReceiptModal {

    /**
     * Shows a styled receipt modal for the given order.
     * Call this right after placeOrder() in CheckoutController,
     * or from a "View Receipt" button in OrderHistoryController.
     *
     * @param order      the placed order
     * @param customer   the logged-in customer
     * @param restaurant the restaurant (may be null for edge cases)
     */
    public static void show(Order order, Customer customer, Restaurant restaurant) {
        String receiptText = buildReceiptText(order, customer, restaurant);

        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);
        modal.setTitle("Receipt");

        // ── Receipt lines ─────────────────────────────────────────────────
        VBox lines = new VBox(3);
        lines.setPadding(new Insets(4, 0, 4, 0));

        for (String line : receiptText.split("\n")) {
            Label lbl = new Label(line);
            lbl.setStyle(
                "-fx-font-family: 'Courier New', monospace;" +
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #cccccc;"
            );
            // Highlight section dividers
            if (line.startsWith("=")) {
                lbl.setStyle(
                    "-fx-font-family: 'Courier New', monospace;" +
                    "-fx-font-size: 13px;" +
                    "-fx-text-fill: #8B5E3C;"
                );
            }
            // Highlight heading
            if (line.contains("FOOD ORDER RECEIPT") || line.contains("Thank you")) {
                lbl.setStyle(
                    "-fx-font-family: 'Courier New', monospace;" +
                    "-fx-font-size: 13px;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;"
                );
            }
            // Highlight total line
            if (line.startsWith("Total:")) {
                lbl.setStyle(
                    "-fx-font-family: 'Courier New', monospace;" +
                    "-fx-font-size: 13px;" +
                    "-fx-text-fill: #8B5E3C;" +
                    "-fx-font-weight: bold;"
                );
            }
            lines.getChildren().add(lbl);
        }

        ScrollPane scroll = new ScrollPane(lines);
        scroll.setFitToWidth(true);
        scroll.setStyle(
            "-fx-background: transparent;" +
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;"
        );
        scroll.setPrefHeight(420);

        // ── Buttons ───────────────────────────────────────────────────────
        Button saveBtn = new Button("💾  Save Receipt");
        saveBtn.setStyle(
            "-fx-background-color: #2a2a2a;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 8 20;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #8B5E3C;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        );
        saveBtn.setOnMouseEntered(e -> saveBtn.setStyle(
            "-fx-background-color: #333333;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 8 20;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #8B5E3C;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        saveBtn.setOnMouseExited(e -> saveBtn.setStyle(
            "-fx-background-color: #2a2a2a;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 8 20;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #8B5E3C;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        saveBtn.setOnAction(e -> saveToFile(receiptText, order.getOrderID(), modal));

        Button closeBtn = new Button("Close");
        closeBtn.setStyle(
            "-fx-background-color: #8B5E3C;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 28;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle(
            "-fx-background-color: #6F4A2F;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 28;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle(
            "-fx-background-color: #8B5E3C;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 28;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        closeBtn.setOnAction(e -> modal.close());

        HBox btnRow = new HBox(12, saveBtn, closeBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        VBox.setMargin(btnRow, new Insets(10, 0, 0, 0));

        // ── Card ──────────────────────────────────────────────────────────
        VBox card = new VBox(12);
        card.setStyle(
            "-fx-background-color: #1e1e1e;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: #3a3a3a;" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1;" +
            "-fx-padding: 24 28;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 28, 0, 0, 8);"
        );
        card.setPrefWidth(460);
        card.getChildren().addAll(scroll, btnRow);

        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane(card);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(16));

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);
        modal.showAndWait();
    }

    // ── Receipt text builder ──────────────────────────────────────────────────

    private static String buildReceiptText(Order order, Customer customer, Restaurant restaurant) {
        String divider  = "=================================";
        String thin     = "---------------------------------";
        StringBuilder sb = new StringBuilder();

        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy  HH:mm"));

        sb.append(divider).append("\n");
        sb.append("       FOOD ORDER RECEIPT\n");
        sb.append(divider).append("\n");
        sb.append(String.format("Order ID : %s%n", order.getOrderID()));
        sb.append(String.format("Date     : %s%n", date));
        sb.append(String.format("Customer : %s%n", customer.getName()));
        if (restaurant != null) {
            sb.append(String.format("Restaurant: %s%n", restaurant.getName()));
        }
        sb.append(thin).append("\n");
        sb.append("Items:\n");
        sb.append(thin).append("\n");

        for (FoodItem item : order.getItems()) {
            String itemLine = String.format("%-18s Rs. %d",
                item.getName() + " x" + item.getQuantity(),
                (int)(item.getPrice() * item.getQuantity()));
            sb.append(itemLine).append("\n");
        }

        sb.append(thin).append("\n");

        int subtotal = (int) order.getTotalAmount();
        int delivery = (int) order.getDeliveryFee();
        int grand    = (int) order.getGrandTotal();

        // Show loyalty discount only if points were redeemed
        int redeemed = order.getRedeemedPoints();

        sb.append(String.format("%-18s Rs. %d%n", "Subtotal:", subtotal));

        if (redeemed > 0) {
            int discountAmt = (int) order.getDiscountApplied();
            if (discountAmt > 0) {
                sb.append(String.format("%-18s Rs. -%d  (%d pts)%n",
                    "Loyalty Discount:", discountAmt, redeemed));
            }
        }

        sb.append(String.format("%-18s Rs. %d%n", "Delivery Fee:", delivery));
        sb.append(thin).append("\n");
        sb.append(String.format("%-18s Rs. %d%n", "Total:", grand));
        sb.append(thin).append("\n");

        // Payment method
        sb.append("Payment Method:\n");
        if ("CARD".equals(order.getPaymentMethod())) {
            sb.append("  Card Payment\n");
        } else {
            sb.append("  Cash on Delivery\n");
        }

        // ETA from tracking if available
        if (order.getTracking() != null) {
            int eta = order.getTracking().getEstimatedDeliveryMinutes();
            sb.append(thin).append("\n");
            sb.append("Estimated Delivery:\n");
            sb.append(String.format("  %d minutes%n", eta));
        }

        sb.append(divider).append("\n");
        sb.append("   Thank you for your order!\n");
        sb.append(divider).append("\n");

        return sb.toString();
    }

    // ── Save to file ──────────────────────────────────────────────────────────

    private static void saveToFile(String text, String orderID, Stage owner) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Receipt");
        chooser.setInitialFileName("Receipt_" + orderID + ".txt");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = chooser.showSaveDialog(owner);
        if (file == null) return;

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(text);
            showSaveConfirmation(file.getName(), owner);
        } catch (IOException e) {
            System.out.println("Could not save receipt: " + e.getMessage());
        }
    }

    private static void showSaveConfirmation(String fileName, Stage owner) {
        Stage toast = new Stage();
        toast.initModality(Modality.WINDOW_MODAL);
        toast.initOwner(owner);
        toast.initStyle(StageStyle.TRANSPARENT);

        Label msg = new Label("✓  Saved as " + fileName);
        msg.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 12 20;" +
            "-fx-background-color: #2a2a2a;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #8B5E3C;" +
            "-fx-border-radius: 8;"
        );

        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane(msg);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(8));

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        toast.setScene(scene);

        // Auto-close after 2 seconds
        javafx.animation.PauseTransition pause =
            new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
        pause.setOnFinished(e -> toast.close());
        pause.play();
        toast.show();
    }
}
