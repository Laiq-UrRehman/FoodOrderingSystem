import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button customerTab;
    @FXML
    private Button adminTab;

    private boolean isCustomerLogin = true;
    private final LoginManager loginManager = new LoginManager();

    @FXML
    private void switchToCustomer() {
        isCustomerLogin = true;
        customerTab.getStyleClass().setAll("login-signup-tab-selected");
        adminTab.getStyleClass().setAll("login-signup-tab-unselected");
        errorLabel.setText("");
    }

    @FXML
    private void switchToAdmin() {
        isCustomerLogin = false;
        adminTab.getStyleClass().setAll("login-signup-tab-selected");
        customerTab.getStyleClass().setAll("login-signup-tab-unselected");
        errorLabel.setText("");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        if (isCustomerLogin) {
            Customer customer = loginManager.loginCustomer(username, password);
            if (customer != null) {
                SessionManager.getInstance().setCurrentCustomer(customer);
                SceneManager.getInstance().switchTo("CustomerDashboard");
            } else {
                errorLabel.setText("Invalid username or password.");
            }
        } else {
            RestaurantAdmin admin = loginManager.loginAdmin(username, password);
            if (admin != null) {
                SessionManager.getInstance().setCurrentAdmin(admin);
                SceneManager.getInstance().switchTo("AdminDashboard");
            } else {
                errorLabel.setText("Invalid admin username or password.");
            }
        }
    }

    @FXML
    private void goToSignup() {
        SceneManager.getInstance().switchTo("Signup");
    }
}