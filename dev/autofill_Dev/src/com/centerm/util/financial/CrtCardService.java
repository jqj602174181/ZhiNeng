package com.centerm.util.financial;

import com.Crt310r.Transmission;

//用于读crt卡的类
public class CrtCardService {

	private static CrtCardService instance;
	private Transmission tran;

	static
	{
		try {  
			System.loadLibrary("Crt310r");	
		} catch (UnsatisfiedLinkError e) { 
			e.printStackTrace();
		}  
	}

	private CrtCardService(){
		tran = new Transmission();
	}

	public static CrtCardService getInstance()
	{
		if( instance == null )
		{
			instance = new CrtCardService();
		}
		return instance;
	}

	//卡是否有存在
	public boolean isCardexist()
	{
		int rc=-1;
		byte [] OutBuf =new byte[300];
		int [] OutBufLen = new int[2];
		byte [] InBuf = null;
		int [] InBufLen = null;

		OutBufLen[0]=0x00;
		OutBufLen[1]=0x00;

		InBufLen = new int[2];
		InBufLen[0] =2;
		InBufLen[1] = 0;

		InBuf = new byte[2];
		InBuf[0] = 0x31;
		InBuf[1] = 0x30;

		String ComStr="/dev/ttyS2";
		rc = tran.ExeCution2(ComStr,InBufLen,InBuf,OutBufLen,OutBuf);
		if (rc==0)
		{
			switch (OutBuf[2])
			{
			case 0x46:
			case 0x47:
			case 0x48:
			case 0x49:
			case 0x4A:
			case 0x4B:
			case 0x4C:
			case 0x4D:
				return true;
			case 0x4E:
			default:
				return false;
			}
		}
		return false;
	}

	public String readCard()  //读卡， 退卡
	{
		String no = null;
		int rc = -1;
		byte [] OutBuf =new byte[300];
		int [] OutBufLen = new int[2];
		byte [] InBuf = null;
		int [] InBufLen = null;

		OutBufLen[0] = 0x00;
		OutBufLen[1] = 0x00;

		InBufLen = new int[2];
		InBufLen[0] =4;
		InBufLen[1] = 0;

		InBuf = new byte[4];
		InBuf[0] = 0x45;
		InBuf[1] = 0x30;
		InBuf[2] = 0x30;
		InBuf[3] = 0x37;

		String ComStr="/dev/ttyS2";
		rc = tran.ExeCution2(ComStr,InBufLen,InBuf,OutBufLen,OutBuf);
		if (rc==0)
		{
			int weizhi1,weizhi2,weizhi3;
			String Tra1Buf,Tra2Buf,Tra3Buf;
			Tra1Buf="";
			Tra2Buf="";
			Tra3Buf="";
			weizhi1=0;
			weizhi2=0;
			weizhi3=0;
			int l;
			if (OutBufLen[0] >= 13)
			{
				weizhi1 = 4;                                       //第一个1F所在位置
				for (l = 5 ;l< OutBufLen[0] ;l++)
				{
					if (OutBuf[l] == 31)
					{
						weizhi2 = l ;                                   //第二个1F所在位置
						break;
					}
				}
				for (l = weizhi2+1 ;l< OutBufLen[0];l++)
				{
					if (OutBuf[l] == 31)
					{
						weizhi3 = l ;                                   //第三个1F所在位置
						break;
					}
				}
				switch  (OutBuf[weizhi1 + 1])    //一轨数据
				{
				case 89:
					for (int M = weizhi1 + 2 ;M< weizhi2 ;M++)
						Tra1Buf = Tra1Buf + (char)(OutBuf[M]);
					System.out.println("一轨数据：" + Tra1Buf);
					break;
				case 78:
					System.out.println("一轨数据：" + 78);
					switch  (OutBuf[weizhi1 + 2]&0xFF)
					{
					case 225:
						break;
					case 226:
						break;
					case 227:
						break;
					case 228:
						break;
					case 229:
						break;
					}
					break;
				case 79:
					break;
				case 0:
					break;
				}
				switch  (OutBuf[weizhi2 + 1])    //二轨数据;
				{
				case 89:
					for (int M = weizhi2 + 2 ;M< weizhi3 ;M++)
						Tra2Buf = Tra2Buf + (char)(OutBuf[M]);
					System.out.println("二轨数据：" + Tra2Buf);//读取二轨数据，即为卡号
					
					int index = Tra2Buf.contains("=")? Tra2Buf.indexOf("="):-1;
					no = Tra2Buf.substring(0, index);
					break;
				case 78:
					System.out.println("二轨数据：" + 78);
					switch  (OutBuf[weizhi2 + 2]&0xFF)
					{
					case 225:
						break;
					case 226:
						break;
					case 227:
						break;
					case 228:
						break;
					case 229:
						break;
					}
					break;
				case 79:
					break;
				case 0:
					break;
				}
				switch  (OutBuf[weizhi3 + 1])     //三轨数据
				{
				case 89:
				{
					for (int M = weizhi3 + 2;M< OutBufLen[0];M++)
						Tra3Buf = Tra3Buf + (char)(OutBuf[M]);
					System.out.println("三轨数据：" + Tra3Buf);
					break;
				}
				case 78:
					System.out.println("三轨数据：" + 78);
					switch  (OutBuf[weizhi3 + 2]&0xFF)
					{
					case 225:
						break;
					case 226:
						break;
					case 227:
						break;
					case 228:
						break;
					case 229:
						break;
					}
					break;
				case 79:
					break;
				case 0:
					break;
				}
			}
		}
		
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//弹卡
		int rc2 = -1;
		OutBuf = new byte[300];
		OutBufLen = new int[2];
		InBuf = null;
		InBufLen = null;

		OutBufLen[0]=0x00;
		OutBufLen[1]=0x00;

		InBufLen = new int[2];
		InBufLen[0] =2;
		InBufLen[1] = 0;

		InBuf = new byte[2];
		InBuf[0] = 0x32;
		InBuf[1] = 0x31;

		rc2 = tran.ExeCution2(ComStr,InBufLen,InBuf,OutBufLen,OutBuf);
		if (rc2==0)
		{
			switch (OutBuf[2])
			{
			//		    	case 0x59:
			//		    		 Toast.makeText(Crt310r.this, "移动卡成功 ", 1).show();
			//		    	     break;
			//		    	case 0x4E:
			//		    		 Toast.makeText(Crt310r.this, "移动卡失败 ", 1).show();
			//	    	        break;
			//		    	case 0x45:
			//		    		 Toast.makeText(Crt310r.this, "读卡器中无卡 ", 1).show();
			//	    	        break;
			//		    	case 0x57:
			//		    		 Toast.makeText(Crt310r.this, "卡不在允许操作的位置上 ", 1).show();
			//	    	        break;
			//		    	default:
			//		    		 Toast.makeText(Crt310r.this, "其它错误 ", 1).show();
			//		    		 break;
			}
		}
		return no;
	}
}
