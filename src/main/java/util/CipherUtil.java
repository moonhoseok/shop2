package util;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public class CipherUtil {
	private final static byte[] iv = new byte[] {
			(byte)0x8E,0x12,0x39,(byte)0x9,
				0x07,0x72,0x6F,(byte)0x5A,
			(byte)0x8E,0x12,0x39,(byte)0x9,
				0x07,0x72,0x6F,(byte)0x5A
	};
	static Cipher cipher;
	static {
		try {						//알고리즘/블러암호코드/패딩
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String makehash(String plain, String algo) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(algo);
		byte[] plainByte = plain.getBytes();
		byte[] hashByte = md.digest(plainByte);
		return byteToHex(hashByte);
	}
	private String byteToHex(byte[] hashByte) {
		if(hashByte == null) return null;
		String str = "";
		for(byte b : hashByte) str += String.format("%02X", b);
		return str;
	}
	private static byte[] hexToByte(String str) {
		// str : 074046B62... 두개의 데이터가 1byte
		if(str == null || str.length() < 2) return null; // 잘못된 데이터 
		int len =str.length() / 2; // 2개의 데이터가 1byte
		byte[] buf = new byte[len]; // 7,.. 
		for(int i =0; i<len; i++) {
			buf[i] = (byte)Integer.parseInt(str.substring(i*2,i*2+2),16);
		}
		return buf;
	}
	public String encrypt(String plain, String key) {
		byte[] cipherMsg = new byte[1024];
		try {								 // byte[] , 알고리즘
			Key genkey = new SecretKeySpec(makeKey(key), "AES");// 128비트 크기
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE,genkey,paramSpec); 
			cipherMsg = cipher.doFinal(plain.getBytes()); // 암호문
		} catch (Exception e) {
			e.printStackTrace();
		}
		return byteToHex(cipherMsg).trim(); // 문자열로 암호문 리턴
	}
	private byte[] makeKey(String key) {
		// key 값 : abc1234567
		int len = key.length(); //10자리
		char ch ='A';
		for(int i=len; i< 16; i++) {// 16바이트로 생성
			key += ch++; // abc1234567ABCDEF 
		}
		return key.substring(0,16).getBytes();
	}
	public String decrypt(String cipher1, String key) {
		byte[] plainMsg = new byte[1024];
		try {								 // byte[] , 알고리즘
			Key genkey = new SecretKeySpec(makeKey(key), "AES"); // 128비트 크기
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE,genkey,paramSpec); 
			plainMsg = cipher.doFinal(hexToByte(cipher1.trim()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(plainMsg).trim();
	}

	
	
	
	
	
	
	
	
	
	
	
}
