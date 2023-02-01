package keepmypassworddesktop.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.junit.jupiter.api.Test;

import me.goral.keepmypassworddesktop.util.AESUtil;
import me.goral.keepmypassworddesktop.util.ArgonUtil;

class AESUtilTest {
	 private static final String ALGORITHM_PADDING = "AES/CFB/PKCS5Padding";
	
	private final String PASSWORD = "KeepMyPassword";
	private final String SALT = "SALT1234SALT";
	private final String TEST_STRING = "EncryptMe";

	@Test
	void test_generateIVHas16Bytes() {
		IvParameterSpec iv = AESUtil.generateIv();
		assertEquals(16, iv.getIV().length);
	}

	@Test
	void testEncryptAndDecrypt() throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchPaddingException,
			NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
		String argon = ArgonUtil.encrypt(PASSWORD, SALT);
		SecretKey key = AESUtil.generateKey(argon);
		IvParameterSpec iv = AESUtil.generateIv();

		String encrypted = AESUtil.encrypt(ALGORITHM_PADDING, TEST_STRING, key, iv);
		String decrypted = AESUtil.decrypt(ALGORITHM_PADDING, encrypted, key, iv);
		assertEquals(TEST_STRING, decrypted);
	}

}