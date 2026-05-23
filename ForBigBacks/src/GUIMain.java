import javafx.application.Application;
import javafx.stage.Stage;
import java.io.*;

public class GUIMain extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        SceneManager.getInstance().init(stage);
        SceneManager.getInstance().getStage().show();
        SceneManager.getInstance().switchTo("Login");
    }

    public static void main(String[] args) {
        seedIfNeeded();
        launch(args);
    }

    static void seedIfNeeded() {
        if (!new File("restaurants.dat").exists())
            DataSeeder.seedRestaurants();
        if (!new File("customers.dat").exists())
            CustomerSeeder.seedCustomers();
        if (!new File("riders.dat").exists())
            RiderSeeder.seedRiders();
        if (!new File("admin_credentials.dat").exists())
            AdminCredentialsSeeder.seedAdminCredentials();
    }
}