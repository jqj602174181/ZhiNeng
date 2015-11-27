package com.centerm.util.financial;

/**
 * Created by ChenWenbin on 20151029.
 */
public class PeriDeviceService {


    static PeriDeviceService instance;

    private PeriDeviceService(){
    }

    public static PeriDeviceService getInstance()
    {
        if( instance == null )
        {
            instance = new PeriDeviceService();
        }
        return instance;
    }

    static
    {
        System.loadLibrary("PeriDevice");
    }


    native public int FrontPopCard( int nComNo, int dwBaud, int nTimeout);
	native public int GetBankCardNum( int nComNo, int dwBaud, int nTimeout, int nMaxBufLen, byte[] pszCardBuf );
	native public int PrintInfo( int nComNo, int dwBaud, int nTimeout, int nPrintBufLen, byte[] pszPrintBuf );
	native public int PopPaper( int nComNo, int dwBaud, int nTimeout);
}
