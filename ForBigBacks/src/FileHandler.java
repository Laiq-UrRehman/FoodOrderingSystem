// Updated: Added FileOperationException (checked) for all save/load failures
// Updated: Null and blank argument validation added to every public method
// Updated: FileNotFoundException separated from general IOException in load methods for clearer error messages

import java.io.*;
import java.util.ArrayList;

public class FileHandler<T extends Serializable> {

    public static class FileOperationException extends Exception {
        public FileOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public void saveObject(T obj, String fileName) throws FileOperationException {
        if (obj == null)
            throw new IllegalArgumentException("Cannot save null object to " + fileName);
        if (fileName == null || fileName.isBlank())
            throw new IllegalArgumentException("File name cannot be null or empty");

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            throw new FileOperationException("Failed to save object to " + fileName, e);
        }
    }

    @SuppressWarnings("unchecked")
    public T loadObject(String fileName) throws FileOperationException {
        if (fileName == null || fileName.isBlank())
            throw new IllegalArgumentException("File name cannot be null or empty");

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (T) ois.readObject();
        } catch (FileNotFoundException e) {
            throw new FileOperationException("File not found: " + fileName, e);
        } catch (ClassNotFoundException e) {
            throw new FileOperationException("Class definition mismatch for data in " + fileName, e);
        } catch (IOException e) {
            throw new FileOperationException("Failed to read object from " + fileName, e);
        }
    }

    public void saveArray(T[] arr, String fileName) throws FileOperationException {
        if (arr == null)
            throw new IllegalArgumentException("Cannot save null array to " + fileName);
        if (fileName == null || fileName.isBlank())
            throw new IllegalArgumentException("File name cannot be null or empty");

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(arr);
        } catch (IOException e) {
            throw new FileOperationException("Failed to save array to " + fileName, e);
        }
    }

    @SuppressWarnings("unchecked")
    public T[] loadArray(String fileName) throws FileOperationException {
        if (fileName == null || fileName.isBlank())
            throw new IllegalArgumentException("File name cannot be null or empty");

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (T[]) ois.readObject();
        } catch (FileNotFoundException e) {
            throw new FileOperationException("File not found: " + fileName, e);
        } catch (ClassNotFoundException e) {
            throw new FileOperationException("Class definition mismatch for data in " + fileName, e);
        } catch (IOException e) {
            throw new FileOperationException("Failed to read array from " + fileName, e);
        }
    }

    public void saveText(String data, String fileName) throws FileOperationException {
        if (data == null)
            throw new IllegalArgumentException("Cannot save null text to " + fileName);
        if (fileName == null || fileName.isBlank())
            throw new IllegalArgumentException("File name cannot be null or empty");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            bw.write(data);
            bw.newLine();
        } catch (IOException e) {
            throw new FileOperationException("Failed to write text to " + fileName, e);
        }
    }

    public ArrayList<String> loadText(String fileName) throws FileOperationException {
        if (fileName == null || fileName.isBlank())
            throw new IllegalArgumentException("File name cannot be null or empty");

        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            throw new FileOperationException("File not found: " + fileName, e);
        } catch (IOException e) {
            throw new FileOperationException("Failed to read text from " + fileName, e);
        }
        return lines;
    }
}