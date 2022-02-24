package me.goral.keepmypassworddesktop.util;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class ArgonUtil {

    static Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    public static String encrypt(String password){
        char[] pwd = password.toCharArray();
        return argon2.hash(22, 65536, 1, pwd);
    }

    public static boolean verify(String hash, String pwd){
        return argon2.verify(hash, pwd.toCharArray());
    }

}
