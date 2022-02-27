package me.goral.keepmypassworddesktop.util;

import de.mkammerer.argon2.Argon2Advanced;
import de.mkammerer.argon2.Argon2Factory;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Base64;

public class ArgonUtil {

    static Argon2Advanced argon2 = Argon2Factory.createAdvanced(Argon2Factory.Argon2Types.ARGON2id);

    public static String encrypt(String password, String salt){
        char[] pwd = password.toCharArray();
        byte[] s = Base64.getDecoder().decode(salt);
        return argon2.hash(22, 65536, 1, pwd, Charset.defaultCharset(), s);
    }

    public static byte[] generateSalt(){
        byte[] s = new byte[128];
        new SecureRandom().nextBytes(s);
        return s;
    }

}
