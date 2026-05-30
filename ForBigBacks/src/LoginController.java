import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    @FXML
    private TextField passwordVisible;
    @FXML
    private Button togglePasswordBtn;
    @FXML
    private ImageView logoImageView;

    private boolean isCustomerLogin = true;
    private final LoginManager loginManager = new LoginManager();

    @FXML
    private void initialize() {
        java.io.File imgFile = new java.io.File("src/FORBIGBACKS1 Logo.png");
        if (imgFile.exists()) {
            logoImageView.setImage(new Image(imgFile.toURI().toString()));
        }
    }

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
        String password = passwordField.isVisible()
                ? passwordField.getText().trim()
                : passwordVisible.getText().trim();

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

    @FXML
    private void togglePassword() {
        if (passwordField.isVisible()) {
            passwordVisible.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            passwordVisible.setVisible(true);
            passwordVisible.setManaged(true);
            togglePasswordBtn.setText("🙈");
        } else {
            passwordField.setText(passwordVisible.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordVisible.setVisible(false);
            passwordVisible.setManaged(false);
            togglePasswordBtn.setText("👁");
        }
    }
}