package com.centerm.util.financial;

import android.util.Log;

//���ڶ�ic������
public class MsgCardService {

	private int cardType =0x00;//0x00���ڶ�ic��,0xff����ˢ�ſ�
	private ICCard icCard;
	private String portType = "COM2";
	public MsgCardService(int style)
	{
		this.cardType = style;
		icCard = ICCard.getInstance();
	}
	public MsgCardService()
	{
		icCard = ICCard.getInstance();
	}
	
	//���Ƿ��д���
	public boolean isCardexist()
	{
		if(icCard.checkICCardStatus(portType, cardType)==0)return true;
		return false;
	}
	
	public String readCard()
	{
		byte [] cardNumber = new byte[32];
		int ret = icCard.getICCardNumber(portType, cardType, cardNumber);
		if(ret==0){//�����ɹ�
			int flag = 0;
			StringBuilder stringBuilder = new StringBuilder();
			for(int i=0;i<32;i++){
				if(cardNumber[i]==0){
					flag = i;
					break;
				}
				stringBuilder.append((char)cardNumber[i]);
			}
			if(stringBuilder.length()==0)
				return null;
			return stringBuilder.toString(); 
		}
		return null;
	}
}
