package kiwi.sofia.mail.common.security;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.NoSuchAlgorithmException;

public class KeyHandler {
    /**
     * @return a new 448 bits long Blowfish key
     */
    public static SecretKey generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("Blowfish");
            keyGen.init(448);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            System.out.printf("Failed to generate key: %s\n", e.getMessage());
            return null;
        }
    }

    /**
     * saveKeyToFile saves a key to a file. Insane.
     *
     * @param key      the key to save
     * @param fileName the file to save the key to... whoa
     */
    public static void saveKey(SecretKey key, String fileName) {
        try {
            // Saving the key as an object as per the assignment instructions
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
            out.writeObject(key);
            System.out.printf("Key saved to \"%s\"\n", fileName);
            out.close();

        } catch (IOException e) {
            System.out.printf("Failed to save key or to close the output stream: %s\n", e.getMessage());
        }
    }

    /**
     * Reads and parses a key from the specified path
     *
     * @param keyPath the path to the key
     * @return the key, or null if it couldn't be read
     */
    public static SecretKey readKey(String keyPath) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(keyPath));
            SecretKey key = (SecretKey) in.readObject();
            System.out.println("Key read from file.");
            in.close();
            return key;
        } catch (IOException e) {
            System.out.printf("Failed to read key from file or to close the input stream: %s\n", e.getMessage());
            return null;
        } catch (ClassNotFoundException e) {
            System.out.printf("Failed to read key from file: %s\n", e.getMessage());
            return null;
        }
    }
}
