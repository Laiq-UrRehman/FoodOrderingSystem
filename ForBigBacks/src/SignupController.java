
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SignupController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField passwordVisible;
    @FXML
    private Button togglePasswordBtn;
    @FXML
    private ImageView logoImageView;

    @FXML
    private void initialize() {
        java.io.File imgFile = new java.io.File("src/FORBIGBACKS1 Logo.png");
        if (imgFile.exists()) {
            logoImageView.setImage(new Image(imgFile.toURI().toString()));
        }
    }

    @FXML
    private void handleSignup() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.isVisible()
                ? passwordField.getText().trim()
                : passwordVisible.getText().trim();

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()
                || username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        if (!name.matches("[a-zA-Z ]{2,50}")) {
            errorLabel.setText("Name must be 2–50 letters only.");
            return;
        }

        if (address.length() < 5) {
            errorLabel.setText("Please enter a valid address (at least 5 characters).");
            return;
        }

        if (!phone.matches("03[0-9]{9}")) {
            errorLabel.setText("Phone must be 11 digits starting with 03 (e.g. 03001234567).");
            return;
        }

        if (!username.matches("[a-zA-Z0-9_]{3,20}")) {
            errorLabel.setText("Username must be 3–20 characters, letters, numbers or _ only.");
            return;
        }

        if (password.length() < 6) {
            errorLabel.setText("Password must be at least 6 characters.");
            return;
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            errorLabel.setText("Password must contain at least one special character.");
            return;
        }

        FileHandler<Customer> fh = new FileHandler<>();
        Customer[] existing = null;

        try {
            existing = fh.loadArray("customers.dat");
        } catch (FileHandler.FileOperationException e) {
            if (!e.getMessage().toLowerCase().contains("file not found")
                    && !e.getMessage().toLowerCase().contains("not found")) {
                errorLabel.setText("Could not load customer data. Please try again.");
                System.out.println("Signup load error: " + e.getMessage());
                return;
            }
        }

        int count = (existing == null) ? 0 : existing.length;

        if (existing != null) {
            for (Customer c : existing) {
                if (c.getUsername().equals(username)) {
                    errorLabel.setText("Username already taken.");
                    return;
                }
            }
        }

        String newID = String.format("C%03d", count + 1);
        Customer newCustomer = new Customer(
                newID, name, address, phone, username, password, null);

        Customer[] updated = new Customer[count + 1];
        if (existing != null)
            System.arraycopy(existing, 0, updated, 0, count);
        updated[count] = newCustomer;

        try {
            fh.saveArray(updated, "customers.dat");
        } catch (FileHandler.FileOperationException e) {
            errorLabel.setText("Could not save account. Please try again.");
            System.out.println("Signup save error: " + e.getMessage());
            return;
        }

        SessionManager.getInstance().setCurrentCustomer(newCustomer);
        SceneManager.getInstance().switchTo("CustomerDashboard");
    }

    @FXML
    private void goToLogin() {
        SceneManager.getInstance().switchTo("Login");
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