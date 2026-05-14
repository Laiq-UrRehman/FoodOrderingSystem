
import java.util.ArrayList;

public class LoginManager<T extends Account> {

    private FileHandler<T> fileHandler = new FileHandler<>();

    public boolean login(String username,String password,String fileName) {

        ArrayList<String> accounts = fileHandler.loadAccounts(fileName);

        for (String line : accounts) {
            String[] data = line.split(",");
            String savedUsername = data[0];
            String savedPassword = data[1];

            if (savedUsername.equals(username) && savedPassword.equals(password)) {
                System.out.println("Login successful.");
                return true;
            }
        }

        System.out.println("Invalid username or password.");
        return false;
    }
}
