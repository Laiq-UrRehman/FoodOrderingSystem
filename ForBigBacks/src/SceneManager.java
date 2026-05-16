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
        this.stage.setTitle("ForBigBacks");
        this.stage.setResizable(true);
        this.stage.setWidth(1280);
        this.stage.setHeight(680);
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
            // global.css is applied here
            scene.getStylesheets().add(
                    getClass().getResource("/global.css").toExternalForm());
            // auth.css is applied here
            if (screenName.equals("Login") || screenName.equals("Signup")) {
                scene.getStylesheets().add(
                        getClass().getResource("/auth.css").toExternalForm());
            }

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error loading screen: " + screenName);
            e.printStackTrace();
        }
    }

    public Stage getStage() {
        return stage;
    }
}
