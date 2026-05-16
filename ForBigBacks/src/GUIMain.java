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
        File restaurantFile = new File("restaurants.dat");
        File customerFile = new File("customers.dat");
        if (!restaurantFile.exists())
            DataSeeder.seedRestaurants();
        if (!customerFile.exists())
            CustomerSeeder.seedCustomers();
    }
}
