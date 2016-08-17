package util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class Encoder {

	private static String algorithm = "DESede";
	private static Key key = null;
	static private Encoder instance = new Encoder();
	
	static public Encoder getInstance() { return instance; }	
	/**
	 * we encode a queryparam
	 * @param param
	 * @return
	 */
	public String encodeQueryParam(String param){		
       try{ 
    	   byte[] paramAsByteArr = param.getBytes("UTF-8");  	   
		   byte[] encryptionBytes = encrypt(paramAsByteArr);
		   String s = StringUtilities.toHex(encryptionBytes); 
		   return s;
       }
       catch (Exception e){
    	   CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
    	   return null;
    	   }
	}
	
	/**
	 * decoding of a encrypted param (if not encrypted we return it as it is)
	 * @param param
	 * @return
	 */
	public String decodeQueryParam(String param){
		try{
			if(param==null || param.trim().equals("") || param.length()<=7 || param.contains("#")) //then param seems not to be encrypted!
				return param;
			
			byte[] b = true ? StringUtilities.hexToBytes(param) : param.getBytes((String) null);
			return decrypt(b);
		}
		catch(Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
			return null;
			}

	}
	
	private byte[] encrypt(byte[] inputBytes) throws Exception {
		try{
			if(key==null) createKey();
			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(inputBytes);
		}
		catch(Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e),  CRTLogger.LEVEL_ERROR);
			return null;
		}
	}
	
    private static void createKey(){
    	String hexKey = "85b310e3e50461f4976db962df1fae6e8fc8d66d2ada7f79";
		byte[]  fromHexKey = StringUtilities.hexToBytes(hexKey);
		Key myDesKey = new SecretKeySpec(fromHexKey, algorithm);
		System.out.println("myDesKey="+ myDesKey);
		key = myDesKey;
    } 
    
	
   /* private  String decrypt(byte[] encryptionBytes, String in_key) throws Exception {
    	try{
	        Cipher cipher = Cipher.getInstance(algorithm);
	        while (in_key.length()<24) {
				in_key = in_key + in_key;
			}
			in_key = in_key.substring(0, 24);
	        byte[]  fromHexKey = in_key.getBytes("UTF-8");
			Key myDesKey = new SecretKeySpec(fromHexKey, algorithm);
	    	cipher.init(Cipher.DECRYPT_MODE, myDesKey);
	        byte[] recoveredBytes =  cipher.doFinal(encryptionBytes);
	        String recovered =  new String(recoveredBytes,"UTF-8");
	        return recovered;
    	}
		catch(Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
			return null;
		}
    }*/
    
    private  String decrypt(byte[] encryptionBytes) throws Exception {
    	try{
			if(key==null) createKey();
	        Cipher cipher = Cipher.getInstance(algorithm);
	    	cipher.init(Cipher.DECRYPT_MODE, key);
	        byte[] recoveredBytes =  cipher.doFinal(encryptionBytes);
	        String recovered =  new String(recoveredBytes,"UTF-8");
	        return recovered;
    	}
		catch(Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
			return null;
		}
      }
}
