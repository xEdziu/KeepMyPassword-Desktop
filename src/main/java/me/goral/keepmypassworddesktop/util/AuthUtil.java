package me.goral.keepmypassworddesktop.util;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import static me.goral.keepmypassworddesktop.util.SHAUtil.hashSHA;

public class AuthUtil {
    public static String initialValue = "initial";//NON-NLS
    private static final String ALGORITHM_PADDING = "AES/CFB/PKCS5Padding";
    /**
     * Encrypt the initial value with AES/CBC/PKCS5Padding using the provided key and iv
     * 
     * @param key The secret key to use for encryption.
     * @param iv The initialization vector.
     * @return The encrypted initial value and the IV.
     */
    public static String encryptInitial(SecretKey key, IvParameterSpec iv) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String ivString = Base64.getEncoder().encodeToString(iv.getIV());
        
		String encryptedInitial = hashSHA(AESUtil.encrypt(ALGORITHM_PADDING, initialValue, key, iv));//NON-NLS

        return  encryptedInitial+":"+ivString;
    }

    /**
     * Authorize the user by checking the encrypted initial value against the hash of the initial value
     * 
     * @param encryptedInitial The encrypted initial value that was sent to the server.
     * @param ivString The IV used to encrypt the initial value.
     * @param key The key to use for encryption.
     * @return The encrypted initial value.
     */
    public static boolean authorize(String encryptedInitial, String ivString, SecretKey key) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(ivString));
        String encTest = hashSHA(AESUtil.encrypt(ALGORITHM_PADDING, initialValue, key, iv));//NON-NLS

        return encTest.equals(encryptedInitial);
    }
}
