package com.Crt310r;

public class Transmission {

	public native int ExeCution(int ComNumber,int[] InBufLen,byte[] InBuf,int[] OutBufLen,byte[] OutBuf);
	public native int ExeCution2(String ComStr,int[] InBufLen,byte[] InBuf,int[] OutBufLen,byte[] OutBuf);
	static
	{
        try {  

        	//Log.i("JNI", "Trying to load so");  

        	System.loadLibrary("Crt310r");	
        }  

        catch (UnsatisfiedLinkError ule) {  

        	//Log.e("JNI", "WARNING: Could not load so");  

        }  
	}

}
