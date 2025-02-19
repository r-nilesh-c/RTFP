package com.yourproject.utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class CustomByteCipher {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int KEY_INTERVAL = 128;
    private static final int KEY_DERIVATION_ROUNDS = 3;

    private static List<Integer> expandKey(int initialKey, int length) {
        List<Integer> expandedKeys = new ArrayList<>();
        int currentKey = initialKey;
        for (int i = 0; i < length; i++) {
            currentKey = (currentKey * 0x3498) % 0xff;
            currentKey = (currentKey ^ (i * 0x7621)) % 0xff;
            expandedKeys.add(currentKey);
        }
        return expandedKeys;
    }

    public static byte[] encrypt(byte[] data, int initialKey) throws Exception {
        byte[] encryptedData = new byte[data.length];
        List<Integer> expandedKey = expandKey(initialKey, data.length);

        for (int i = 0; i < data.length; i++) {
            int key = expandedKey.get(i);
            int encryptedByte = (data[i] + key) % 256;
            encryptedByte = (encryptedByte ^ (key * 0x23)) % 256;
            encryptedData[i] = (byte) encryptedByte;
        }

        return encryptedData;
    }

    public static byte[] decrypt(byte[] encryptedData, int initialKey) throws Exception {
        byte[] decryptedData = new byte[encryptedData.length];
        List<Integer> expandedKey = expandKey(initialKey, encryptedData.length);

        for (int i = 0; i < encryptedData.length; i++) {
            int key = expandedKey.get(i);
            int decryptedByte = (encryptedData[i] & 0xFF);
            decryptedByte = (decryptedByte ^ (key * 0x23)) % 256;
            decryptedByte = (decryptedByte - key + 256) % 256;
            decryptedData[i] = (byte) decryptedByte;
        }
        return decryptedData;
    }
}
