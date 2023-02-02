package me.goral.keepmypassworddesktop.util;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public class AESUtil {

    //use "AES/CFB/PKCS5Padding" algorithm

    /**
     * Encrypts the input string using the specified algorithm, key, and initialization vector
     * 
     * @param algorithm The algorithm to use for encryption.
     * @param input The string to be encrypted.
     * @param key The key to use for encryption.
     * @param iv The initialization vector (IV) is a set of bytes that the algorithm uses for input to
     * the cipher.
     * @return The encrypted string.
     */
    public static String encrypt(String algorithm, String input, SecretKey key, IvParameterSpec iv) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder()
                    .encodeToString(cipherText);
    }

    /**
     * Decrypts the cipherText using the algorithm, key, and iv parameters
     * 
     * @param algorithm The algorithm to use for encryption.
     * @param cipherText The encrypted text that you want to decrypt.
     * @param key The key to use for the encryption.
     * @param iv The initialization vector (IV) is a set of bytes that the algorithm uses for the
     * encryption process. The IV is used in conjunction with the key to encrypt the data. The IV is
     * not encrypted or decrypted with the key. The IV is typically stored with the encrypted data.
     * @return The decrypted string.
     */
    public static String decrypt(String algorithm, String cipherText, SecretKey key, IvParameterSpec iv) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] plainText = cipher.doFinal(Base64.getDecoder()
                    .decode(cipherText));
            return new String(plainText);
        } catch (Exception e){
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
        return null;
    }

    /**
     * It takes the argon hash and extracts the key from it.
     * 
     * @param argon The argon hash to use to generate the key.
     * @return The SecretKeySpec is a key that can be used for encryption and decryption.
     */
    public static SecretKeySpec generateKey(String argon) {
        byte[] extracted = extractArgon(argon);
        return new SecretKeySpec(extracted, "AES");
    }

    /**
     * Generate a random initialization vector (IV) of 16 bytes
     * 
     * @return The IvParameterSpec is a class that is used to specify the initialization vector (IV)
     * for a block cipher.
     */
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /**
     * It takes the full argon hash and extracts the base64 encoded hash.
     * 
     * @param argonFull The full string of the argon hash.
     * @return The decoded argon hash.
     */
    private static byte[] extractArgon(String argonFull){
        String[] arr = argonFull.split("\\$");
        String toCode = arr[arr.length-1];
        return Base64.getDecoder().decode(toCode);
    }
}
