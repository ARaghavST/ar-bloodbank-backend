
package com.ar.bloodbank.helpers;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


public class PasswordEncryptionWithAES {
    // Generate a secret key
    private static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // AES-128
        return keyGen.generateKey();
    }

    // Encrypt the password
    private static String encrypt(String password, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(password.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Decrypt the password
    private static String decrypt(String encryptedPassword, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
        return new String(decryptedBytes);
    }
    
    public static String doEncrypt(String password){
         String encryptedPassword="";
        try{
            SecretKey key = generateKey();
          encryptedPassword=encrypt(password,key);
        }catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        return encryptedPassword;
         
    }
}