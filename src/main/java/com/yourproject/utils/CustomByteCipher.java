package com.yourproject.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class CustomByteCipher {
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 128;

    private static byte[] generateKey(int initialKey) throws Exception {
        byte[] key = new byte[KEY_SIZE / 8];
        System.arraycopy(String.valueOf(initialKey).getBytes(), 0, key, 0, key.length);
        return key;
    }

    public static byte[] encrypt(byte[] data, int initialKey) throws Exception {
        SecretKey secretKey = new SecretKeySpec(generateKey(initialKey), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] encryptedData, int initialKey) throws Exception {
        SecretKey secretKey = new SecretKeySpec(generateKey(initialKey), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedData);
    }
}
