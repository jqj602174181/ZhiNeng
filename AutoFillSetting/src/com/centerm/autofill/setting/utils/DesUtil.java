package com.centerm.autofill.setting.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
* 通过DES算法，加密或解密数据
* @author sunlightcs
* 2011-4-29
* http://hi.juziku.com/sunlightcs/
*/

public class DesUtil {
	// 用于3des加密的24字节的密钥
	   public static final byte[] KEYBYTES  = { 0x11, 0x22, 0x4F, 0x58, (byte) 0x88, 0x10,0x40, 
	        		0x38, 0x28, 0x25, 0x79, 0x51, (byte) 0xCB, (byte) 0xDD, 0x55,
	        		0x66, 0x77, 0x29, 0x74, (byte) 0x98, 0x30, 0x40, 0x36, (byte) 0xE2 };
    private static final String Algorithm = "DESede";
	//private static final String Algorithm = "DES";

//	public static void main(String[] args) {
//		String test = "11111111";
//		String key = "00000000";
//		DesUtil d = new DesUtil();
//		
//		//DES加密
//		test = d.encrypt(test, key);
//		System.out.println("加密后的数据：" + test);
//		
//		//DES解密
//		test = d.decrypt(test, key);
//		System.out.println("解密后的数据：" + test);
//	}
    
	public DesUtil() {
		// TODO Auto-generated constructor stub
	}
	
    /**
     * 加密
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return    返回加密后的数据
     * @throws Exception
     */
    public  static byte[] encrypt(byte[] src, byte[] key) throws RuntimeException {
        //DES算法要求有一个可信任的随机数源
        try{
            //SecureRandom sr = new SecureRandom();
            // 从原始密匙数据创建DESKeySpec对象
            //DESKeySpec dks = new DESKeySpec(key);
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            // 一个SecretKey对象
            //SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(Algorithm);
        	SecretKey securekey = new SecretKeySpec(key, Algorithm);
            // Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance(Algorithm + "/ECB/NoPadding");
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey);
            // 现在，获取数据并加密
            // 正式执行加密操作
            return cipher.doFinal(src);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
   
    /**
     * 解密
     * @param src   数据源
     * @param key   密钥，长度必须是8的倍数
     * @return      返回解密后的原始数据
     * @throws Exception
     */
    public  static byte[] decrypt(byte[] src, byte[] key) throws RuntimeException {
        try{
            //      DES算法要求有一个可信任的随机数源
            //SecureRandom sr = new SecureRandom();
            // 从原始密匙数据创建一个DESKeySpec对象
            //DESKeySpec dks = new DESKeySpec(key);
            // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
            // 一个SecretKey对象
            //SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(Algorithm);
            //SecretKey securekey = keyFactory.generateSecret(dks);
        	SecretKey deskey = new SecretKeySpec(key, Algorithm);
            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance(Algorithm + "/ECB/NoPadding");
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, deskey);
            // 现在，获取数据并解密
            // 正式执行解密操作
            return cipher.doFinal(src);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * DES数据解密
     * @param data
     * @param key 密钥
     * @return
     * @throws Exception
     */
    public final static String decrypt(String data, String key){
        return new String(decrypt(hex2byte(data.getBytes()),key.getBytes()));
    }
    /**
     * DES数据加密
     * @param data
     * @param key 密钥
     * @return
     * @throws Exception
     */
    public final static String encrypt(String data, String key){
        if(data!=null)
        try {
            return byte2hex(encrypt(data.getBytes(),key.getBytes()));
        }catch(Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }
  
    /**
     * 二行制转字符串
     * @param b
     * @return
     */
    private static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b!=null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }

        return hs.toString().toUpperCase();
    }

      
    private static byte[] hex2byte(byte[] b) {
        if((b.length%2)!=0)
            throw new IllegalArgumentException();
        byte[] b2 = new byte[b.length/2];
        for (int n = 0; n < b.length; n+=2) {
            String item = new String(b,n,2);
            b2[n/2] = (byte)Integer.parseInt(item,16);
        }
        return b2;
    }
    
    
    //**************************新加的,先做测试*********************************//
	private static String triDes = "DESede/ECB/NoPadding";	//3des加、解密算法/模式/填充
    /**
	 * 3DES加密
	 * @param key	密钥
	 * @param data	明文
	 * @return	密文
	 */
	public static byte[] trides_crypt(byte[] key, byte[] data)
	{
		byte[] triKey = new byte[24];
		byte[] triData = null;
		
		if ( key.length == 16 )
		{
			System.arraycopy(key, 0, triKey, 0, key.length);
			System.arraycopy(key, 0, triKey, key.length, triKey.length - key.length);
		}
		else
		{
			System.arraycopy(key, 0, triKey, 0, triKey.length);
		}
		
		int dataLen = data.length;
		if ( dataLen % 8 != 0 )
		{
			dataLen = dataLen - dataLen % 8 + 8;
		}
		if ( dataLen != 0 )
		{
			triData = new byte[dataLen];
		}
		for ( int i = 0; i < dataLen; i++ )
		{
			triData[i] = (byte)0x00;
		}
		System.arraycopy(data, 0, triData, 0, data.length);
		
		try
		{
			KeySpec keySpec = new DESedeKeySpec(triKey);
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DESede");
			SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
			
			Cipher cipher = Cipher.getInstance(triDes);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return cipher.doFinal(triData);
		}
		catch(InvalidKeyException ike)
		{
			
			return null;
		}
		catch(NoSuchAlgorithmException nsae)
		{
			
			return null;
		}
		catch(InvalidKeySpecException ikse)
		{
			
			return null;
		}
		catch(NoSuchPaddingException nspe)
		{
			
			return null;
		}
		catch(BadPaddingException bpe)
		{
			
			return null;
		}
		catch(IllegalBlockSizeException ibse)
		{
		
			return null;
		}
	}
	
	/**
	 * 3DES解密
	 * @param key	密钥
	 * @param data	密文
	 * @return	明文
	 */
	public static byte[] trides_decrypt(byte[] key, byte[] data)
	{
		byte[] triKey = new byte[24];
		byte[] triData = null;
		
		if ( key.length == 16 )
		{
			System.arraycopy(key, 0, triKey, 0, key.length);
			System.arraycopy(key, 0, triKey, key.length, triKey.length - key.length);
		}
		else
		{
			System.arraycopy(key, 0, triKey, 0, triKey.length);
		}
		
		int dataLen = data.length;
		if ( dataLen % 8 != 0 )
		{
			dataLen = dataLen - dataLen % 8 + 8;
		}
		if ( dataLen != 0 )
		{
			triData = new byte[dataLen];
		}
		for ( int i = 0; i < dataLen; i++ )
		{
			triData[i] = (byte)0x00;
		}
		System.arraycopy(data, 0, triData, 0, data.length);
		
		try
		{
			KeySpec keySpec = new DESedeKeySpec(triKey);
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DESede");
			SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
			
			Cipher cipher = Cipher.getInstance(triDes);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return cipher.doFinal(triData);
		}
		catch( InvalidKeyException ike )
		{
			
			return null;
		}
		catch(NoSuchAlgorithmException nsae)
		{
			
			return null;
		}
		catch(InvalidKeySpecException ikse)
		{
			
			return null;
		}
		catch(NoSuchPaddingException nspe)
		{
			
			return null;
		}
		catch(BadPaddingException bpe)
		{
		
			return null;
		}
		catch(IllegalBlockSizeException ibse)
		{
			
			return null;
		}
	}
	
	/**
	 * 十六进制形式输出byte[]
	 * 
	 * @param b
	 */
	public static String bytesToHexString(byte[] bytes) {
		if (bytes == null)
		{
			return null;
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < bytes.length; i++)
		{
			String hex = Integer.toHexString(bytes[i] & 0xff);
			if (hex.length() == 1)
			{
				hex = '0' + hex;
			}
			builder.append(hex.toUpperCase());
		}
		return builder.toString();
	}
	
	/**
	 * 转变十六进制形式的byte[]
	 * @param orign
	 * @return
	 */
	public static byte[] hexStringToBytes(String orign){
		int length = orign.length()/2;
		byte[] result = new byte[length];
		for(int i=0; i<length; i++){
			result[i] = (byte) Integer.parseInt(orign.substring(i*2, i*2+2),16);
		}
		return result;
	}

	
}
