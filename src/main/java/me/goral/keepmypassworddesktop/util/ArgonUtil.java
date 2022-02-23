package me.goral.keepmypassworddesktop.util;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

public class ArgonUtil {

    private static final int saltLength = 128 / 8; // 128 bits
    private static final int hashLength = 256 / 8; // 256 bits
    private static final int parallelism = 1;
    private static final int memoryInKb = 10 * 1024; // 10 MB
    private static final int iterations = 10;
    private static final Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memoryInKb, iterations);

    public static String encrypt(String plain){
        return passwordEncoder.encode(plain);
    }

    public static boolean verify(String hash, String plain){
        return passwordEncoder.matches(plain, hash);
    }

}
