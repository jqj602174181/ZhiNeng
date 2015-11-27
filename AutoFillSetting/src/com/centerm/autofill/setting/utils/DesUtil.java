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
* ͨ��DES�㷨�����ܻ��������
* @author sunlightcs
* 2011-4-29
* http://hi.juziku.com/sunlightcs/
*/

public class DesUtil {
	// ����3des���ܵ�24�ֽڵ���Կ
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
//		//DES����
//		test = d.encrypt(test, key);
//		System.out.println("���ܺ�����ݣ�" + test);
//		
//		//DES����
//		test = d.decrypt(test, key);
//		System.out.println("���ܺ�����ݣ�" + test);
//	}
    
	public DesUtil() {
		// TODO Auto-generated constructor stub
	}
	
    /**
     * ����
     * @param src ����Դ
     * @param key ��Կ�����ȱ�����8�ı���
     * @return    ���ؼ��ܺ������
     * @throws Exception
     */
    public  static byte[] encrypt(byte[] src, byte[] key) throws RuntimeException {
        //DES�㷨Ҫ����һ�������ε������Դ
        try{
            //SecureRandom sr = new SecureRandom();
            // ��ԭʼ�ܳ����ݴ���DESKeySpec����
            //DESKeySpec dks = new DESKeySpec(key);
            // ����һ���ܳ׹�����Ȼ��������DESKeySpecת����
            // һ��SecretKey����
            //SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(Algorithm);
        	SecretKey securekey = new SecretKeySpec(key, Algorithm);
            // Cipher����ʵ����ɼ��ܲ���
            Cipher cipher = Cipher.getInstance(Algorithm + "/ECB/NoPadding");
            // ���ܳ׳�ʼ��Cipher����
            cipher.init(Cipher.ENCRYPT_MODE, securekey);
            // ���ڣ���ȡ���ݲ�����
            // ��ʽִ�м��ܲ���
            return cipher.doFinal(src);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
   
    /**
     * ����
     * @param src   ����Դ
     * @param key   ��Կ�����ȱ�����8�ı���
     * @return      ���ؽ��ܺ��ԭʼ����
     * @throws Exception
     */
    public  static byte[] decrypt(byte[] src, byte[] key) throws RuntimeException {
        try{
            //      DES�㷨Ҫ����һ�������ε������Դ
            //SecureRandom sr = new SecureRandom();
            // ��ԭʼ�ܳ����ݴ���һ��DESKeySpec����
            //DESKeySpec dks = new DESKeySpec(key);
            // ����һ���ܳ׹�����Ȼ��������DESKeySpec����ת����
            // һ��SecretKey����
            //SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(Algorithm);
            //SecretKey securekey = keyFactory.generateSecret(dks);
        	SecretKey deskey = new SecretKeySpec(key, Algorithm);
            // Cipher����ʵ����ɽ��ܲ���
            Cipher cipher = Cipher.getInstance(Algorithm + "/ECB/NoPadding");
            // ���ܳ׳�ʼ��Cipher����
            cipher.init(Cipher.DECRYPT_MODE, deskey);
            // ���ڣ���ȡ���ݲ�����
            // ��ʽִ�н��ܲ���
            return cipher.doFinal(src);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * DES���ݽ���
     * @param data
     * @param key ��Կ
     * @return
     * @throws Exception
     */
    public final static String decrypt(String data, String key){
        return new String(decrypt(hex2byte(data.getBytes()),key.getBytes()));
    }
    /**
     * DES���ݼ���
     * @param data
     * @param key ��Կ
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
     * ������ת�ַ���
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
    
    
    //**************************�¼ӵ�,��������*********************************//
	private static String triDes = "DESede/ECB/NoPadding";	//3des�ӡ������㷨/ģʽ/���
    /**
	 * 3DES����
	 * @param key	��Կ
	 * @param data	����
	 * @return	����
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
	 * 3DES����
	 * @param key	��Կ
	 * @param data	����
	 * @return	����
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
	 * ʮ��������ʽ���byte[]
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
	 * ת��ʮ��������ʽ��byte[]
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
