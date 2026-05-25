public class SignUpManager {

    public boolean signup(Customer account, String fileName) {
        FileHandler<Customer> fileHandler = new FileHandler<>();
        Customer[] existing;

        try {
            existing = fileHandler.loadArray(fileName);
        } catch (FileHandler.FileOperationException e) {
            System.out.println("[SignUpManager] Failed to load existing customers: " + e.getMessage());
            return false;
        }

        if (existing != null) {
            for (Customer c : existing) {
                if (c.getUsername().equals(account.getUsername())) {
                    System.out.println("Username already exists.");
                    return false;
                }
            }
        }

        int oldLen = (existing != null) ? existing.length : 0;
        Customer[] updated = new Customer[oldLen + 1];
        if (existing != null) {
            System.arraycopy(existing, 0, updated, 0, oldLen);
        }
        updated[oldLen] = account;

        try {
            fileHandler.saveArray(updated, fileName);
            System.out.println("Signup successful.");
            return true;
        } catch (FileHandler.FileOperationException e) {
            System.out.println("[SignUpManager] Failed to save customer: " + e.getMessage());
            return false;
        }
    }
}