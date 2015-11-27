package com.centerm.util.financial;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.sam.sdticreader.WltDec;
import com.sdt.Sdtapi;

//Ӧ�ó�����Ҫ"android.permission.WRITE_EXTERNAL_STORAGE"��Ȩ�޷�����������

public class IDCardService {

	public static final int MSG_READIDSUCCESS = 1; //�����ɹ�
	public static final int MSG_READIDFAIL = 2; //����ʧ��
	private OnReadCardListener rlistener = null;
	static IDCardService instance;

	private Sdtapi sdta;
	public boolean findloop = true;
	Thread t2;
	
//	private Activity activity;

	/*�����б�*/
	String [] nation ={"��","�ɹ�","��","��","ά���","��","��","׳","����","����",
			"��","��","��","��","����","����","������","��","��","����",
			"��","�","��ɽ","����","ˮ","����","����","����","�˶�����","��",
			"���Ӷ�","����","Ǽ","����","����","ë��","����","����","����","����",
			"������","ŭ","���ȱ��","����˹","���¿�","�°�","����","ԣ��","��","������",
			"����","���״�","����","�Ű�","���","��ŵ"};

	public IDCardService()
	{	
		
	}
	
	public void init(Activity activity){
		try {
			sdta= new Sdtapi(activity); 
		} catch (Exception e1) {//�����쳣��
			if(e1.getCause()==null) //USB�豸�쳣�������ӣ�Ӧ�ó��򼴽��رա�
			{
				Toast.makeText(activity, "USB�豸�쳣��������", Toast.LENGTH_SHORT).show();
			}else {	//USB�豸δ��Ȩ����Ҫȷ����Ȩ
				Toast.makeText(activity, "USB�豸δ��Ȩ", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/*!
	 * \brief ��ȡ����֤serviceʵ��
	 * \return serviceʵ��
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
					if(ret==0x9f)//�ҿ��ɹ�
					{
						ret =sdta.SDT_SelectIDCard();
						if(ret==0x90) //ѡ���ɹ�
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
								msg.obj = "��ȡ������Ϣʧ��:" + String.format("0x%02x", ret);
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
		rlistener.onError("��ʱ");
	}

	//��ȡ���֤�е�������Ϣ�����Ķ���ʽ�ģ�
	public int ReadBaseMsgToStr(IdCardMsg msg)
	{
		int ret;
		int []puiCHMsgLen=new int[1];
		int []puiPHMsgLen=new int[1];

		byte [] pucCHMsg = new byte[256];
		byte [] pucPHMsg = new byte[1024];

		//sdtapi�б�׼�ӿڣ�����ֽڸ�ʽ����Ϣ��
		ret =sdta.SDT_ReadBaseMsg( pucCHMsg, puiCHMsgLen, pucPHMsg, puiPHMsgLen);

		if(ret==0x90)
		{
			try {
				char [] pucCHMsgStr = new char[128];
				WltDec decoder = new WltDec();
				byte[] tmpPhone = decoder.decodeToBitmap(pucPHMsg); //�Ȼ�ȡ��Ƭ
				msg.photo = tmpPhone;

				DecodeByte(pucCHMsg, pucCHMsgStr);//����ȡ�����֤�е���Ϣ�ֽڣ�����ɿ��Ķ�������
				PareseItem(pucCHMsgStr, msg); //����Ϣ������msg��
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	//�ֶ���Ϣ��ȡ
	void  PareseItem(char []pucCHMsgStr, IdCardMsg msg)
	{
		msg.name = String.copyValueOf(pucCHMsgStr, 0, 15);
		String sex_code = String.copyValueOf(pucCHMsgStr, 15, 1);

		if(sex_code.equals("1")) msg.sex="��";
		else if(sex_code.equals("2"))msg.sex="Ů";
		else if(sex_code.equals("0"))msg.sex="δ֪";
		else if (sex_code.equals("9"))msg.sex="δ˵��";

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

	//�ֽڽ��뺯��
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

	/*********************�ڲ�˽�з�������*************************/
	//��Ϣ����
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
