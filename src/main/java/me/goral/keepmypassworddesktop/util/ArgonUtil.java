package me.goral.keepmypassworddesktop.util;

import de.mkammerer.argon2.Argon2Advanced;
import de.mkammerer.argon2.Argon2Factory;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Base64;

public class ArgonUtil {

    static Argon2Advanced argon2 = Argon2Factory.createAdvanced(Argon2Factory.Argon2Types.ARGON2id);

    /**
     * It hashes the password using the Argon2 algorithm.
     * 
     * @param password The password to be hashed.
     * @param salt A random salt.
     * @return The hash of the password.
     */
    public static String encrypt(String password, String salt){
        char[] pwd = password.toCharArray();
        byte[] s = Base64.getDecoder().decode(salt);
        return argon2.hash(22, 65536, 1, pwd, Charset.defaultCharset(), s);
    }

    /**
     * Generate a 128-bit salt
     * 
     * @return A 128-byte random salt.
     */
    public static byte[] generateSalt(){
        byte[] s = new byte[128];
        new SecureRandom().nextBytes(s);
        return s;
    }

}
