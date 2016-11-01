/** 
 * @company  Chengdu ZhiXiao planning consulting co., LTD.
 * @copyright  2015, Chengdu ZhiXiao planning consulting co., LTD.
 */
package com.weibuildus.smarttv.util;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密处理工具类
 * 
 * @ClassName: EncryptUtils
 * @author wumaojie.gmail.com
 * @date 2015-8-10 上午11:28:57
 */
public class EncryptUtils {

	/**
	 * 生成随机字符串
	 * 
	 * @Title getRandomString
	 * @param length
	 *            随机字符串长度
	 * @return 返回随机字符串
	 */
	public static String getRandomString(int length) {
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * 获取文件Base64编码, 读取错误时返回空
	 * 
	 * @Title getBase64
	 * @param filePath
	 *            文件路径
	 * @return 文件Base64编码字符串
	 */
	public static String getBase64(String filePath) {
		if (filePath == null) {
			throw new RuntimeException("can not null");
		}
		File file = new File(filePath);
		String base64Data = null;
		if (file.exists()) {
			InputStream in = null;
			try {
				in = new FileInputStream(file);
				byte[] data = new byte[in.available()];
				in.read(data);
				base64Data = Base64.encodeToString(data, Base64.DEFAULT);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return base64Data;
	}

	/**
	 * MD5加密，32位小写
	 * 
	 * @Title: MD5Encrypt
	 * @param str
	 *            明文
	 * @return String 密文
	 * @throws
	 */
	public static String MD5Encrypt(String str) {
		if (str == null) {
			throw new RuntimeException("can not null");
		}
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString().toLowerCase();
	}

	/**
	 * SHA1加密
	 * @Title  SHA1Encrypt 
	 * @param str
	 * @return
	 */
	public static String SHA1Encrypt(String str) {
		if (str == null) {
			throw new RuntimeException("can not null");
		}
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(str.getBytes());
			byte[] buf = digest.digest();
			return toHex(buf);

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * SHA 加密
	 * @Title  SHAEncrypt 
	 * @param str
	 * @return
	 */
	public static String SHAEncrypt(String str) {
		if (str == null) {
			throw new RuntimeException("can not null");
		}
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA");
			digest.update(str.getBytes());
			byte[] buf  = digest.digest();
			return toHex(buf);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * AES 加密
	 * 
	 * @Title: AESEncrypt
	 * @param seed
	 *            密码
	 * @param clearText
	 *            明文
	 * @return String 加密密文
	 * @throws
	 */
	public static String AESEncrypt(String seed, String clearText) {
		if (seed == null || clearText == null) {
			throw new RuntimeException("can not null");
		}
		byte[] result = null;
		try {
			byte[] rawkey = getRawKey(seed.getBytes());
			result = encrypt(rawkey, clearText.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		String content = toHex(result);
		return content;

	}

	/**
	 * AES 解密， 密码错误时返回空
	 * 
	 * @Title: decrypt
	 * @param seed
	 *            密码
	 * @param encrypted
	 *            加密密文
	 * @return String 界面明文
	 * @throws
	 */
	public static String AESDecrypt(String seed, String encrypted) {
		if (seed == null || encrypted == null) {
			throw new RuntimeException("can not null");
		}
		byte[] rawKey;
		try {
			rawKey = getRawKey(seed.getBytes());
			byte[] enc = toByte(encrypted);
			byte[] result = decrypt(rawKey, enc);
			String coentn = new String(result);
			return coentn;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * AES 加密 密码处理
	 * 
	 * @Title: getRawKey
	 * @param seed
	 *            密码Byte数组
	 * @return
	 * @throws Exception
	 *             byte[]
	 * @throws
	 */
	private static byte[] getRawKey(byte[] seed) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(seed);
		kgen.init(128, sr);
		SecretKey sKey = kgen.generateKey();
		byte[] raw = sKey.getEncoded();
		return raw;
	}

	/**
	 * AES加密算法
	 * 
	 * @Title: encrypt
	 * @param raw
	 *            密码
	 * @param clear
	 *            明文
	 * @return 密文
	 * @throws Exception
	 *             byte[]
	 * @throws
	 */
	private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(
				new byte[cipher.getBlockSize()]));
		byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	/**
	 * AES解密算法
	 * 
	 * @Title: decrypt
	 * @param raw
	 *            密码
	 * @param encrypted
	 *            密文
	 * @return 明文
	 * @throws Exception
	 *             byte[]
	 * @throws
	 */
	private static byte[] decrypt(byte[] raw, byte[] encrypted)
			throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(
				new byte[cipher.getBlockSize()]));
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}

	/**
	 * 字符串转16进制
	 * 
	 * @Title: toHex
	 * @param txt
	 * @return String
	 * @throws
	 */
	@SuppressWarnings("unused")
	private static String toHex(String txt) {
		return toHex(txt.getBytes());
	}

	/**
	 * 16进制字符串转字符串
	 * 
	 * @Title: fromHex
	 * @param hex
	 * @return String
	 * @throws
	 */
	@SuppressWarnings("unused")
	private static String fromHex(String hex) {
		return new String(toByte(hex));
	}

	/**
	 * 16进制转byte[]
	 * 
	 * @Title: toByte
	 * @param hexString
	 * @return byte[]
	 * @throws
	 */
	private static byte[] toByte(String hexString) {
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
					16).byteValue();
		return result;
	}

	/**
	 * byte[]转16进制
	 * 
	 * @Title: toHex
	 * @param buf
	 * @return String
	 * @throws
	 */
	private static String toHex(byte[] buf) {
		if (buf == null)
			return "";
		StringBuffer result = new StringBuffer(2 * buf.length);
		for (int i = 0; i < buf.length; i++) {
			appendHex(result, buf[i]);
		}
		return result.toString();
	}

	/**
	 * 16进制字母对应
	 * 
	 * @Title: appendHex
	 * @param sb
	 * @param b
	 *            void
	 * @throws
	 */
	private static void appendHex(StringBuffer sb, byte b) {
		final String HEX = "0123456789ABCDEF";
		sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
	}
}
