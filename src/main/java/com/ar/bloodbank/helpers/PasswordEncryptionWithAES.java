package com.ar.bloodbank.helpers;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class PasswordEncryptionWithAES {

    // Generate a secret key
    public static SecretKey generateKey(String salt) {
        byte[] keyBytes = salt.getBytes();
        return new SecretKeySpec(keyBytes, 0, 16, "AES");
    }

    // Encrypt the password
    private String encrypt(String password, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(password.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Decrypt the password
    private String decrypt(String encryptedPassword, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
        return new String(decryptedBytes);
    }

    public String doEncrypt(String password, SecretKey key) {
        String encryptedPassword = "";
        try {

            encryptedPassword = this.encrypt(password, key);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        return encryptedPassword;

    }

    public String doDecrypt(String password, SecretKey key) {
        String decryptedPassword = "";
        try {
            decryptedPassword = this.decrypt(password, key);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        return decryptedPassword;

    }
}
