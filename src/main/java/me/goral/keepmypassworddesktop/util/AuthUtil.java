package me.goral.keepmypassworddesktop.util;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import static me.goral.keepmypassworddesktop.util.SHAUtil.hashSHA;

public class AuthUtil {
    public static String initialValue = "initial";

    public static String encryptInitial(SecretKey key, IvParameterSpec iv)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
            InvalidKeyException {
        String ivString = Base64.getEncoder().encodeToString(iv.getIV());
        String encryptedInitial = hashSHA(AESUtil.encrypt("AES/CBC/PKCS5Padding", initialValue, key, iv));

        return  initialValue+":"+encryptedInitial+":"+ivString;
    }

    public static boolean authorize(String encryptedInitial, String ivString, SecretKey key)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException {
        IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(ivString));
        String encTest = hashSHA(AESUtil.encrypt("AES/CBC/PKCS5Padding", initialValue, key, iv));

        int res = Arrays.compare(encTest.getBytes(), encryptedInitial.getBytes());
        return res == 0;
        //return encTest.equals(encryptedInitial);

    }
}
