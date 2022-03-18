package me.goral.keepmypassworddesktop.util;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

public class SHAUtil {

    /**
     * It hashes the input string using SHA3-512.
     * 
     * @param input The input string to hash.
     * @return The SHA-512 hash of the input string.
     */
    public static String hashSHA(String input){
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
        byte[] digest = digestSHA3.digest(input.getBytes());
        return  Hex.toHexString(digest);
    }
}
