package com.centerm.util.financial;

public class ICCard {
	
	
	static ICCard instance;
	
	private ICCard(){
	}
	
	public static ICCard getInstance()
	{
		if( instance == null )
		{
			instance = new ICCard();
		}
		return instance;
	}
	
	static
	{
		System.loadLibrary("newiccard");
	}
	
    /*!
     * �������ܣ����ؽ���IC����
     * ����˵����
     *    portType[in]������ģ�鴮�ںţ���Ҫ��COMX��ʾ��x��ʾ���ںţ���COM1��ʾ����1
     *    cardType[in]����Ƭ���ͣ�0x00--0x0F��ʾ�Ӵ���Ƭ�� 0xFF��ʾ�ǽӿ�Ƭ
     *    cardNumber[out]:�洢IC���ţ������С�����32�ֽ�
     * ����ֵ��
     *    0 -- �����ɹ���other -- ����ʧ�ܡ���Ƭ����λ����Ƭ�巴����������
     * ˵�������Կ�Ƭ�����ǽ���IC��   
     * */
	
	native public int getICCardNumber( String portType, int cardType, byte[] cardNumber );
    /*!
     * �������ܣ����Կ�Ƭ�Ƿ���뿨��
     * ����˵����
     *    portType[in]������ģ�鴮�ںţ���Ҫ��COMX��ʾ��x��ʾ���ںţ���COM1��ʾ����1
     *    cardType[in]����Ƭ���ͣ�0x00--0x0F��ʾ�Ӵ���Ƭ�� 0xFF��ʾ�ǽӿ�Ƭ
     * ����ֵ��
     *    0 -- ��Ƭ��λ��other -- ��Ƭ����λ����Ƭ�巴����������
     * */	
	native public int checkICCardStatus( String portType, int cardType );
	
	
	
	
}
