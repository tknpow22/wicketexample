package tknpow22.wicketexample.app.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文字列の暗号化・復号化のユーティリティを定義する。
 */
public final class CipherUtils {

	private static final Logger logger = LoggerFactory.getLogger(CipherUtils.class);

	private static final byte[] cipherKey = "abcdefgh12345678".getBytes();	// 16 文字

	private static final String cipherAlgorithm = "AES";

	/**
	 * 暗号化した文字列を返す
	 *
	 * @param plainText 元の文字列
	 * @return 暗号化した文字列
	 */
	public static final String encrypt(String plainText) {
		String result = "";
		try {
			Cipher cipher = getCipher(true);

			byte[] encryptedData = cipher.doFinal(plainText.getBytes());
			result = new String(Base64.getEncoder().encode(encryptedData));
		} catch (Exception ex) {
			logger.error("{}", ex);
		}

		return result;
	}

	/**
	 * 復号化した文字列を返す
	 *
	 * @param encryptedText 暗号化した文字列
	 * @return 復号化した文字列
	 */
	public static final String decrypt(String encryptedText) {
		String result = "";
		try {
			Cipher cipher = getCipher(false);

			byte[] plainData = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
			result = new String(plainData);

		} catch (Exception ex) {
			logger.error("{}", ex);
		}

		return result;
	}

	/**
	 * 暗号・復号処理

	 * @param encryption 暗号化時: true、復号化時: false
	 * @return 暗号・復号した文字列
	 * @throws Exception
	 */
	private static final Cipher getCipher(boolean encryption) throws Exception {
		SecretKeySpec secretKeySpec = new SecretKeySpec(cipherKey, cipherAlgorithm);
		Cipher cipher = Cipher.getInstance(cipherAlgorithm);
		cipher.init((encryption) ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, secretKeySpec);
		return cipher;
	}
}
