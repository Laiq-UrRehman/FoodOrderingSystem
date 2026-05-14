import java.util.ArrayList;

public class SignUpManager<T extends Account> {

    private FileHandler<T> fileHandler = new FileHandler<T>();

    public boolean signup(T account, String fileName) {

        ArrayList<String> accounts = fileHandler.loadText(fileName); // ← loadAccounts → loadText

        for (String line : accounts) {
            String[] data = line.split(",");
            String savedUsername = data[0];

            if (savedUsername.equals(account.getUsername())) {
                System.out.println("Username already exists.");
                return false;
            }
        }

        fileHandler.saveText(account.getUsername() + "," + account.getPassword(), fileName); // ← saveAccount → saveText
        System.out.println("Signup successful.");
        return true;
    }
}