package FoodOrderingSystem;

import java.io.*;
import java.util.ArrayList;

public class FileHandler<T extends Account> {

    public void saveAccount(T account, String fileName) {
        try{
            FileWriter fw = new FileWriter(fileName, true);

            BufferedWriter bw = new BufferedWriter(fw);
            bw.write( account.getUsername() + "," + account.getPassword());
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            System.out.println("Error saving account.");
        }
    }

    public ArrayList<String> loadAccounts(String fileName) {
        ArrayList<String> accounts = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {
                accounts.add(line);
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error loading accounts.");
        }
        return accounts;
    }
}