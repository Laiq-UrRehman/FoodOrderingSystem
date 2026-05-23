import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {

    private static SceneManager instance;
    private Stage stage;
    private final Map<String, String> scenes = new HashMap<>();

    private SceneManager() {
        scenes.put("Login", "/Login.fxml");
        scenes.put("Signup", "/Signup.fxml");
        scenes.put("CustomerDashboard", "/CustomerDashboard.fxml");
        scenes.put("RestaurantBrowse", "/RestaurantBrowse.fxml");
        scenes.put("MenuView", "/MenuView.fxml");
        scenes.put("Cart", "/Cart.fxml");
        scenes.put("Checkout", "/Checkout.fxml");
        scenes.put("OrderHistory", "/OrderHistory.fxml");
        scenes.put("OrderTracking", "/OrderTracking.fxml");
        scenes.put("ScheduledOrder", "/ScheduledOrder.fxml");
        scenes.put("AdminDashboard", "/AdminDashboard.fxml");
    }

    public static SceneManager getInstance() {
        if (instance == null)
            instance = new SceneManager();
        return instance;
    }

    public void init(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("FORBIGBACKS");
        this.stage.setResizable(true);
        this.stage.setWidth(1280);
        this.stage.setHeight(800);
        this.stage.centerOnScreen();
        String workingDir = new java.io.File(".").getAbsolutePath();
        System.out.println("Looking for icon in: " + workingDir);

        java.io.File iconFile = new java.io.File("FORBIGBACKS Logo.png");
        System.out.println("Icon exists: " + iconFile.exists() + " at " + iconFile.getAbsolutePath());

        if (iconFile.exists()) {
            String uri = iconFile.toURI().toString();
            this.stage.getIcons().addAll(
                    new javafx.scene.image.Image(uri, 16, 16, true, true),
                    new javafx.scene.image.Image(uri, 32, 32, true, true),
                    new javafx.scene.image.Image(uri, 64, 64, true, true),
                    new javafx.scene.image.Image(uri, 128, 128, true, true),
                    new javafx.scene.image.Image(uri, 256, 256, true, true));
        }
    }

    public void switchTo(String screenName) {
        String path = scenes.get(screenName);
        if (path == null) {
            System.out.println("Screen not found: " + screenName);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // global.css on every screen
            scene.getStylesheets().add(
                    getClass().getResource("/global.css").toExternalForm());

            // auth.css only for Login and Signup
            if (screenName.equals("Login") || screenName.equals("Signup")) {
                scene.getStylesheets().add(
                        getClass().getResource("/auth.css").toExternalForm());
            }

            // dashboard.css for all customer and admin screens
            if (screenName.equals("CustomerDashboard") || screenName.equals("MenuView")
                    || screenName.equals("Cart") || screenName.equals("Checkout")
                    || screenName.equals("OrderHistory") || screenName.equals("OrderTracking")
                    || screenName.equals("ScheduledOrder") || screenName.equals("AdminDashboard")
                    || screenName.equals("RestaurantBrowse")) {
                scene.getStylesheets().add(
                        getClass().getResource("/dashboard.css").toExternalForm());
            }

            // admin.css only for AdminDashboard (loaded on top of dashboard.css)
            if (screenName.equals("AdminDashboard")) {
                scene.getStylesheets().add(
                        getClass().getResource("/admin.css").toExternalForm());
            }

            stage.setScene(scene);
            stage.show();
            stage.setMaximized(true);
        } catch (IOException e) {
            System.out.println("Error loading screen: " + screenName);
            e.printStackTrace();
        }
    }

    public Stage getStage() {
        return stage;
    }
}