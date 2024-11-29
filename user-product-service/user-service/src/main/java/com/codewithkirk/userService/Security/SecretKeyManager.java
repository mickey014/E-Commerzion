package com.codewithkirk.userService.Security;

import org.springframework.stereotype.Component;

import java.io.*;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class SecretKeyManager {
    private static final String SECRET_KEY_FILE = "ssssh!.txt";

    public SecretKeyManager() {
        // Generate and save the initial secret key if the file does not exist
        generateAndSaveSecretKey();
    }

    public void generateAndSaveSecretKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[32]; // 256 bits
        secureRandom.nextBytes(key);
        String secretKey = Base64.getEncoder().encodeToString(key);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SECRET_KEY_FILE))) {
            writer.write(secretKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadSecretKey() {
        // Load the secret key from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(SECRET_KEY_FILE))) {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void overwriteSecretKey(String newSecretKey) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SECRET_KEY_FILE))) {
            writer.write(newSecretKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generateNewSecretKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[32]; // 256 bits
        secureRandom.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

}
