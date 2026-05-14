import java.io.*;
import java.util.ArrayList;

public class FileHandler<T extends Serializable> {

    // ─── Serializable Object Save/Load ───────────────────────────

    public void saveObject(T obj, String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(obj);
            System.out.println("Object saved to " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving object: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public T loadObject(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading object: " + e.getMessage());
            return null;
        }
    }

    // ─── Serializable Array Save/Load ────────────────────────────

    public void saveArray(T[] arr, String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(arr);
            System.out.println(arr.length + " item(s) saved to " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving array: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public T[] loadArray(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (T[]) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading array: " + e.getMessage());
            return null;
        }
    }

    // ─── Text File Save/Load (CSV style) ─────────────────────────

    public void saveText(String data, String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            bw.write(data);
            bw.newLine();
            System.out.println("Data written to " + fileName);
        } catch (IOException e) {
            System.out.println("Error writing text: " + e.getMessage());
        }
    }

    public ArrayList<String> loadText(String fileName) {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading text: " + e.getMessage());
        }
        return lines;
    }
}