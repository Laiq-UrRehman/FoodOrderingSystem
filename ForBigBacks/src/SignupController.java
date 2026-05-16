import javafx.fxml.FXML;
import javafx.scene.control.*;

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
    private void handleSignup() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validate all fields filled
        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()
                || username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        // Load existing customers
        FileHandler<Customer> fh = new FileHandler<>();
        Customer[] existing = fh.loadArray("customers.dat");
        int count = (existing == null) ? 0 : existing.length;

        // Check username not taken
        if (existing != null) {
            for (Customer c : existing) {
                if (c.getUsername().equals(username)) {
                    errorLabel.setText("Username already taken.");
                    return;
                }
            }
        }

        // Create new customer
        String newID = String.format("C%03d", count + 1);
        Customer newCustomer = new Customer(newID, name, address, phone, username, password, new Location(0, 0));

        // Save
        Customer[] updated = new Customer[count + 1];
        if (existing != null)
            System.arraycopy(existing, 0, updated, 0, count);
        updated[count] = newCustomer;
        fh.saveArray(updated, "customers.dat");

        // Auto login and go to dashboard
        SessionManager.getInstance().setCurrentCustomer(newCustomer);
        SceneManager.getInstance().switchTo("CustomerDashboard");
    }

    @FXML
    private void goToLogin() {
        SceneManager.getInstance().switchTo("Login");
    }
}
