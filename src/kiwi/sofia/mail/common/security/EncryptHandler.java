package kiwi.sofia.mail.common.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class EncryptHandler {
    private final static String algorithm = "Blowfish/ECB/PKCS5Padding";

    /**
     * Encrypts a password using a key and stores the version on disk.
     *
     * @param password   the password to encrypt
     * @param key        the key to use for encryption
     * @param outputPath the path to save the encrypted password to
     * @see <a href="https://docs.oracle.com/javase/1.5.0/docs/guide/security/jce/JCERefGuide.html#Cipher">JavaTM Cryptography Extension (JCE) Reference Guide</a>
     */
    public static void encryptPassword(String password, SecretKey key, String outputPath) {
        if (key == null)
            return;

        try {
            System.out.println("Encrypting password...");

            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            FileOutputStream out = new FileOutputStream(outputPath);

            byte[] input = password.getBytes();
            byte[] output = cipher.doFinal(input);

            out.write(output);
            out.close();
            System.out.println("Password encrypted and saved");
        } catch (IOException e) {
            System.out.printf("Failed to read unencrypted file or save encrypted file: %s\n", e.getMessage());
        } catch (Exception e) {
            System.out.printf("Failed to encrypt file: %s\n", e.getMessage());
        }
    }

    /**
     * Decrypts a file using a key and stores the readable file on disk.
     *
     * @param encryptedPath the path to the encrypted file
     * @param key           the key to use for decryption
     * @return the decrypted file as a string
     */
    public static String decryptFile(String encryptedPath, SecretKey key) {
        if (key == null)
            return "";

        try {
            System.out.printf("Decrypting %s...\n", encryptedPath);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key);

            FileInputStream in = new FileInputStream(encryptedPath);

            byte[] input = in.readAllBytes();
            in.close();

            return new String(cipher.doFinal(input));
        } catch (IOException e) {
            System.out.printf("Failed to read encrypted file: %s\n", e.getMessage());
        } catch (Exception e) {
            System.out.printf("Failed to decrypt file: %s\n", e.getMessage());
        }

        return "";
    }
}
