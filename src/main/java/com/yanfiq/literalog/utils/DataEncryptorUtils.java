package com.yanfiq.literalog.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class DataEncryptorUtils {

    // Simulate a secret key
    private static final String SECRET_KEY = "mysecretkey";

    public static void main(String[] args) {
        // Serialize and encrypt data
        String serializedData = serializeDataToXML();
        String encryptedData = encryptData(serializedData);

        // Decrypt and deserialize data
        String decryptedData = decryptData(encryptedData);
        deserializeDataFromXML(decryptedData);
    }

    private static String serializeDataToXML() {
        // Serialize data to XML (JAXB or other serialization libraries)
        // For simplicity, using a dummy data representation
        return "<Book><item>Example</item></data>";
    }

    private static String encryptData(String data) {
        try {
            // Simulate encryption using a secret key
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, generateSecretKey());

            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Encode the encrypted bytes to a string for storage
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String decryptData(String encryptedData) {
        try {
            // Decode the string to get the encrypted bytes
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);

            // Simulate decryption using the same secret key
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, generateSecretKey());

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void deserializeDataFromXML(String data) {
        // Deserialize data from XML (JAXB or other serialization libraries)
        // For simplicity, just printing the deserialized data
        System.out.println("Deserialized Data: " + data);
    }

    private static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        // Simulate generating a secret key (in a more secure way in real scenarios)
        return new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
    }
}
