import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SignupController {

    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleSignup() {
        String name     = nameField.getText().trim();
        String address  = addressField.getText().trim();
        String phone    = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // ── Empty check ──────────────────────────────────────────────────
        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()
                || username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        // ── Name: letters and spaces only ────────────────────────────────
        if (!name.matches("[a-zA-Z ]{2,50}")) {
            errorLabel.setText("Name must be 2–50 letters only.");
            return;
        }

        // ── Phone: 11 digits, starts with 03 ────────────────────────────
        if (!phone.matches("03[0-9]{9}")) {
            errorLabel.setText("Phone must be 11 digits starting with 03 (e.g. 03001234567).");
            return;
        }

        // ── Username: letters/numbers/underscores, 3–20 chars ────────────
        if (!username.matches("[a-zA-Z0-9_]{3,20}")) {
            errorLabel.setText("Username must be 3–20 characters, letters, numbers or _ only.");
            return;
        }

        // ── Password: min 8 chars, must have uppercase, lowercase, digit, special char
        if (password.length() < 8) {
            errorLabel.setText("Password must be at least 8 characters.");
            return;
        }
        if (!password.matches(".*[A-Z].*")) {
            errorLabel.setText("Password must contain at least one uppercase letter.");
            return;
        }
        if (!password.matches(".*[a-z].*")) {
            errorLabel.setText("Password must contain at least one lowercase letter.");
            return;
        }
        if (!password.matches(".*[0-9].*")) {
            errorLabel.setText("Password must contain at least one number.");
            return;
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            errorLabel.setText("Password must contain at least one special character.");
            return;
        }

        // ── Duplicate username check ─────────────────────────────────────
        FileHandler<Customer> fh = new FileHandler<>();
        Customer[] existing = fh.loadArray("customers.dat");
        int count = (existing == null) ? 0 : existing.length;

        if (existing != null) {
            for (Customer c : existing) {
                if (c.getUsername().equals(username)) {
                    errorLabel.setText("Username already taken.");
                    return;
                }
            }
        }

        // ── Create and save ──────────────────────────────────────────────
        String newID = String.format("C%03d", count + 1);
        Customer newCustomer = new Customer(newID, name, address, phone, username, password, new Location(0, 0));

        Customer[] updated = new Customer[count + 1];
        if (existing != null)
            System.arraycopy(existing, 0, updated, 0, count);
        updated[count] = newCustomer;
        fh.saveArray(updated, "customers.dat");

        SessionManager.getInstance().setCurrentCustomer(newCustomer);
        SceneManager.getInstance().switchTo("CustomerDashboard");
    }

    @FXML
    private void goToLogin() {
        SceneManager.getInstance().switchTo("Login");
    }
}