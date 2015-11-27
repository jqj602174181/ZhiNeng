package com.centerm.util.financial;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.sam.sdticreader.WltDec;
import com.sdt.Sdtapi;

//应用程序需要"android.permission.WRITE_EXTERNAL_STORAGE"的权限方可正常运行

public class IDCardService {

	public static final int MSG_READIDSUCCESS = 1; //读卡成功
	public static final int MSG_READIDFAIL = 2; //读卡失败
	private OnReadCardListener rlistener = null;
	static IDCardService instance;

	private Sdtapi sdta;
	public boolean findloop = true;
	Thread t2;
	
//	private Activity activity;

	/*民族列表*/
	String [] nation ={"汉","蒙古","回","藏","维吾尔","苗","彝","壮","布依","朝鲜",
			"满","侗","瑶","白","土家","哈尼","哈萨克","傣","黎","傈僳",
			"佤","畲","高山","拉祜","水","东乡","纳西","景颇","克尔克孜","土",
			"达斡尔","仫佬","羌","布朗","撒拉","毛南","仡佬","锡伯","阿昌","普米",
			"塔吉克","怒","乌兹别克","俄罗斯","鄂温克","德昂","保安","裕固","京","塔塔尔",
			"独龙","鄂伦春","赫哲","门巴","珞巴","基诺"};

	public IDCardService()
	{	
		
	}
	
	public void init(Activity activity){
		try {
			sdta= new Sdtapi(activity); 
		} catch (Exception e1) {//捕获异常，
			if(e1.getCause()==null) //USB设备异常或无连接，应用程序即将关闭。
			{
				Toast.makeText(activity, "USB设备异常或无连接", Toast.LENGTH_SHORT).show();
			}else {	//USB设备未授权，需要确认授权
				Toast.makeText(activity, "USB设备未授权", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/*!
	 * \brief 获取二代证service实例
	 * \return service实例
	 */
	public static IDCardService getInstance()
	{
		if( instance == null )
		{
			instance = new IDCardService();
		}
		return instance;
	}

	public void readCard(OnReadCardListener rls)
	{
		this.rlistener = rls;
		findloop = true;
		t2 = new Thread(){
			public void run()
			{
				while(findloop)
				{
					if(sdta == null){
						break;
					}
					int ret =sdta.SDT_StartFindIDCard();
					if(ret==0x9f)//找卡成功
					{
						ret =sdta.SDT_SelectIDCard();
						if(ret==0x90) //选卡成功
						{
							IdCardMsg cardmsg = new IdCardMsg();
							ret =ReadBaseMsgToStr(cardmsg);
							
							Message msg = new Message();
							if(ret==0x90)
							{
								msg.what = MSG_READIDSUCCESS; 
								msg.obj = cardmsg;
							}else{
								msg.what = MSG_READIDFAIL; 
								msg.obj = "读取基本信息失败:" + String.format("0x%02x", ret);
							}
							mHandler.sendMessage(msg);	
							findloop = false;
							break;
						}
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}								
				}
			}
		};
		t2.start();
	}

	public void cancelRead(){
		if(rlistener == null)
		{
			Log.d("IDCardService", "rlistener is null");
			return;
		}
		if(t2 != null && t2.isAlive()){
			findloop=false;
		}
		rlistener.onCancel();
	}

	public void stopRead(){
		if(rlistener == null)
		{
			Log.d("IDCardService", "rlistener is null");
			return;
		}
		if(t2 != null && t2.isAlive()){
			findloop = false;
		}
		rlistener.onError("超时");
	}

	//读取身份证中的文字信息（可阅读格式的）
	public int ReadBaseMsgToStr(IdCardMsg msg)
	{
		int ret;
		int []puiCHMsgLen=new int[1];
		int []puiPHMsgLen=new int[1];

		byte [] pucCHMsg = new byte[256];
		byte [] pucPHMsg = new byte[1024];

		//sdtapi中标准接口，输出字节格式的信息。
		ret =sdta.SDT_ReadBaseMsg( pucCHMsg, puiCHMsgLen, pucPHMsg, puiPHMsgLen);

		if(ret==0x90)
		{
			try {
				char [] pucCHMsgStr = new char[128];
				WltDec decoder = new WltDec();
				byte[] tmpPhone = decoder.decodeToBitmap(pucPHMsg); //先获取照片
				msg.photo = tmpPhone;

				DecodeByte(pucCHMsg, pucCHMsgStr);//将读取的身份证中的信息字节，解码成可阅读的文字
				PareseItem(pucCHMsgStr, msg); //将信息解析到msg中
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	//分段信息提取
	void  PareseItem(char []pucCHMsgStr, IdCardMsg msg)
	{
		msg.name = String.copyValueOf(pucCHMsgStr, 0, 15);
		String sex_code = String.copyValueOf(pucCHMsgStr, 15, 1);

		if(sex_code.equals("1")) msg.sex="男";
		else if(sex_code.equals("2"))msg.sex="女";
		else if(sex_code.equals("0"))msg.sex="未知";
		else if (sex_code.equals("9"))msg.sex="未说明";

		String nation_code = String.copyValueOf(pucCHMsgStr, 16, 2);
		msg.nation_str = nation[Integer.valueOf(nation_code)-1];

		msg.birth_year = String.copyValueOf(pucCHMsgStr, 18, 4);
		msg.birth_month = String.copyValueOf(pucCHMsgStr, 22, 2);
		msg.birth_day = String.copyValueOf(pucCHMsgStr, 24, 2);
		msg.address = String.copyValueOf(pucCHMsgStr, 26, 35);
		msg.id_num = String.copyValueOf(pucCHMsgStr, 61, 18);
		msg.sign_office = String.copyValueOf(pucCHMsgStr, 79, 15);

		msg.useful_s_date_year = String.copyValueOf(pucCHMsgStr, 94, 4);
		msg.useful_s_date_month = String.copyValueOf(pucCHMsgStr, 98, 2);
		msg.useful_s_date_day = String.copyValueOf(pucCHMsgStr, 100, 2);

		msg.useful_e_date_year = String.copyValueOf(pucCHMsgStr, 102, 4);
		msg.useful_e_date_month = String.copyValueOf(pucCHMsgStr, 106, 2);
		msg.useful_e_date_day = String.copyValueOf(pucCHMsgStr, 108, 2);			
	}   

	//字节解码函数
	void DecodeByte(byte[] msg,char []msg_str) throws Exception
	{
		byte[] newmsg = new byte[msg.length+2];

		newmsg[0]=(byte) 0xff;
		newmsg[1] =(byte) 0xfe;

		for(int i=0;i<msg.length;i++)
			newmsg[i+2]= msg[i];

		String s=new String(newmsg,"UTF-16");
		for(int i=0;i<s.toCharArray().length;i++)
			msg_str[i]=s.toCharArray()[i];
	}

	/*********************内部私有方法声明*************************/
	//消息处理
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_READIDSUCCESS:
				if(rlistener == null)
				{
					Log.d("IDCardService", "rlistener is null");
					break;
				}
				IdCardMsg idCardMsg = (IdCardMsg)msg.obj;
				Log.d("IDCardService", "read card result:" + idCardMsg);
				rlistener.onComplete(idCardMsg);
				break;
			case MSG_READIDFAIL:
				if(rlistener == null)
				{
					Log.d("IDCardService", "rlistener is null");
					break;
				}
				String result = (String)msg.obj;
				rlistener.onError(result);
				break;
			default:
				break;
			}
		}
	};

	public interface OnReadCardListener {
		public void onComplete(IdCardMsg idCardMsg);
		public void onError(String strMsg);
		public void onCancel();
	}
}
