// FIX (Critical): The old signup() called fileHandler.loadText() on customers.dat,
//   which is a binary serialised object array — not a CSV file. Reading binary
//   data as text lines produces garbage or exceptions, so the duplicate-username
//   check never worked. Fixed by loading the array with loadArray() and comparing
//   usernames against the actual Customer objects.

public class SignUpManager {

    public boolean signup(Customer account, String fileName) {
        FileHandler<Customer> fileHandler = new FileHandler<>();
        Customer[] existing = fileHandler.loadArray(fileName);

        if (existing != null) {
            for (Customer c : existing) {
                if (c.getUsername().equals(account.getUsername())) {
                    System.out.println("Username already exists.");
                    return false;
                }
            }
        }

        // Build updated array and save
        int oldLen = (existing != null) ? existing.length : 0;
        Customer[] updated = new Customer[oldLen + 1];
        if (existing != null) {
            System.arraycopy(existing, 0, updated, 0, oldLen);
        }
        updated[oldLen] = account;
        fileHandler.saveArray(updated, fileName);

        System.out.println("Signup successful.");
        return true;
    }
}