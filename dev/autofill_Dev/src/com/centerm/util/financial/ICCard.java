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
     * 函数功能：返回金融IC卡号
     * 参数说明：
     *    portType[in]：金融模块串口号，需要以COMX表示，x表示串口号，如COM1表示串口1
     *    cardType[in]：卡片类型，0x00--0x0F表示接触卡片， 0xFF表示非接卡片
     *    cardNumber[out]:存储IC卡号，缓存大小需大于32字节
     * 返回值：
     *    0 -- 读卡成功，other -- 读卡失败、卡片不在位、卡片插反、参数错误
     * 说明：测试卡片必须是金融IC卡   
     * */
	
	native public int getICCardNumber( String portType, int cardType, byte[] cardNumber );
    /*!
     * 函数功能：测试卡片是否放入卡槽
     * 参数说明：
     *    portType[in]：金融模块串口号，需要以COMX表示，x表示串口号，如COM1表示串口1
     *    cardType[in]：卡片类型，0x00--0x0F表示接触卡片， 0xFF表示非接卡片
     * 返回值：
     *    0 -- 卡片在位，other -- 卡片不在位、卡片插反、参数错误
     * */	
	native public int checkICCardStatus( String portType, int cardType );
	
	
	
	
}
