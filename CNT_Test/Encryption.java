import java.util.*;
import java.io.*;
import java.net.*;
import java.math.BigInteger;
import java.security.*;
import javax.crypto.*;
import java.security.spec.*;
import java.security.interfaces.*;
import javax.crypto.spec.*;
import javax.crypto.interfaces.*;


public class Encryption {
	private SecretKeySpec SharedKey;
	private byte[] encParams;
	
	public Encryption() {
        
    }
	
	public String encrypt(SecretKeySpec SharedKey, String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
		byte[] plaintext = message.getBytes();
		Cipher enc = Cipher.getInstance("AES/CBC/PKCS5Padding");
		enc.init(Cipher.ENCRYPT_MODE, SharedKey);
		byte[] ciphertext = enc.doFinal(plaintext);
		encParams = enc.getParameters().getEncoded();
		return ciphertext.toString();
	}
	
	public String decrypt(SecretKeySpec SharedKey, String ciphertext) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		AlgorithmParameters aesParams = AlgorithmParameters.getInstance("AES");
		aesParams.init(encParams);
		Cipher dec = Cipher.getInstance("AES/CBC/PKCS5Padding");
		dec.init(Cipher.DECRYPT_MODE, SharedKey, aesParams);
        byte[] plaintext = dec.doFinal(ciphertext.getBytes());
		return plaintext.toString();
	}
	
	public SecretKeySpec KeyExchange() throws Exception {
        KeyPairGenerator User1pairGen = KeyPairGenerator.getInstance("DH");
        User1pairGen.initialize(2048);
        KeyPair User1Kpair = User1pairGen.generateKeyPair();
        
        // User 1 creates key agreement object
        KeyAgreement User1KeyAgree = KeyAgreement.getInstance("DH");
        User1KeyAgree.init(User1Kpair.getPrivate());
        
        // User 1 encodes public key and sends to user 2
        byte[] User1PubKeyEnc = User1Kpair.getPublic().getEncoded();
        
        //User 2
        KeyFactory User2KeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(User1PubKeyEnc);

        PublicKey User1PubKey = User2KeyFac.generatePublic(x509KeySpec);

        //User 2 gets User 1's DH public key
        DHParameterSpec DHUser1PubKey = ((DHPublicKey)User1PubKey).getParams();

        //User 2 key pair 
        KeyPairGenerator User2pairGen = KeyPairGenerator.getInstance("DH");
        User2pairGen.initialize(DHUser1PubKey);
        KeyPair User2Kpair = User2pairGen.generateKeyPair();

        // User 2 key agreement
        KeyAgreement User2KeyAgree = KeyAgreement.getInstance("DH");
        User2KeyAgree.init(User2Kpair.getPrivate());

        // User 2 sends back to user 1
        byte[] User2PubKeyEnc = User2Kpair.getPublic().getEncoded();

        //User 1
        KeyFactory User1KeyFac = KeyFactory.getInstance("DH");
        x509KeySpec = new X509EncodedKeySpec(User2PubKeyEnc);
        PublicKey User2PubKey = User1KeyFac.generatePublic(x509KeySpec);
        User1KeyAgree.doPhase(User2PubKey, true);

        //User 2
        User2KeyAgree.doPhase(User1PubKey, true);

       //Shared key block
        try {
            byte[] User1SharedSecret = User1KeyAgree.generateSecret();
            int User1Len = User1SharedSecret.length;
            byte[] User2SharedSecret = new byte[User1Len];
            int User2Len;
            User2Len = User2KeyAgree.generateSecret(User2SharedSecret, 0);
            System.out.println("User1 Key: " +
                    toHexString(User1SharedSecret));
            System.out.println("User2 Key: " +
                    toHexString(User2SharedSecret));
            if (!java.util.Arrays.equals(User1SharedSecret, User2SharedSecret))
                throw new Exception("Shared secrets differ");
            System.out.println("Shared secrets are the same");
            
            
            SecretKeySpec User2AesKey = new SecretKeySpec(User2SharedSecret, 0, 16, "AES");
            SecretKeySpec User1AesKey = new SecretKeySpec(User1SharedSecret, 0, 16, "AES");
            return User1AesKey;
        } catch (ShortBufferException e) {
            System.out.println(e.getMessage());
        }
        byte[] test = "".getBytes();
        return new SecretKeySpec(test, 0,16, "AES");
	}
	
	
    private static void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

    
    private static String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();
        int len = block.length;
        for (int i = 0; i < len; i++) {
            byte2hex(block[i], buf);
            if (i < len-1) {
                buf.append(":");
            }
        }
        return buf.toString();
	}
	
    public static void main(String[] args) {
        Encryption enc = new Encryption();
         
        try {
			enc.KeyExchange();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	

}
