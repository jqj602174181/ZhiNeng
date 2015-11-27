package com.centerm.util.financial;

/*!
 * @brief IC卡服务类
 * @par 说明:
 * 			该类提供对IC卡的基本操作。
 */
public class ICCardService {
	static ICCardService instance;
	
	private ICCardService()
	{
	}
	
	/*!
	 * @brief 获取IC卡service实例
	 * @return service实例
	 */
	public static ICCardService getInstance()
	{
		if( instance == null )
		{
			instance = new ICCardService();
		}
		return instance;
	}
	
	static 
    {
		//加载动态库
    	System.loadLibrary( "iccard" );
    }
	
	/*!
	 * \brief  ReSetCardSlot
	 * \par 说明        	重置设备的卡槽号
	 * \param[in]         	
	 * \param[in]          
	 * \param[in]          
	 * \param[out]         
	 * \param[out]         
	 * \retval              0 -- 成功   -1 失败; 
	 * \note                  
	 */
	public int ReSetCardSlot( )
	{
		return ReaderReSetCardSlot();
	}
	private native int ReaderReSetCardSlot( );

	/*!
	 * \brief               instr_ReaderSetCardSlot
	 * \par 说明        	设置设备的卡槽号
	 * \param[in]         	
	 * \param[in]          
	 * \param[in]          
	 * \param[out]         
	 * \param[out]         
	 * \retval              0 -- 成功   -1 失败; 
	 * \note                  
	 */
	public int SetCardSlot( int  nCardSlot  )
	{
		return ReaderSetCardSlot( nCardSlot );
	}
	private native int  ReaderSetCardSlot(   int  nCardSlot  );
	/*!
	 * \brief               instr_ReaderGetCardSlot
	 * \par 说明        	获取设备的卡槽号
	 * \param[in]         	
	 * \param[in]          
	 * \param[in]          
	 * \param[out]         
	 * \param[out]         
	 * \retval              0 -- 成功   -1 失败; 
	 * \note                  
	 */
	public int GetCardSlot()
	{
		return ReaderGetCardSlot();
	}
	private native int ReaderGetCardSlot(  );
	
	/*!
	 * @brief 获取卡信息
	 * @param [in] portType - 端口类型(C-串口, U-USB)
	 * @param [in] portNo - 端口号
	 * @param [in] tagList - 卡标志列表
	 * @param [out] AIDList - 应用列表
	 * @param [out] userInfo - 卡内信息
	 * @param [out] icType - IC卡标志
	 * @param [out] lpicappdata - 全局状态参数
	 * @param [out] errMsg - 外设操作错误信息
	 * @return 状态码，详见金融模块状态码定义类
	 */
	public int getIcDetailInfo( String portType, String portNo, 
			String tagList, byte[] AIDList, byte[] userInfo, 
			byte[] icType, byte[] lpicappdata, byte[] errMsg)
	{
		return getIcInfo( portType, portNo, tagList,
							AIDList, userInfo, icType, 
							lpicappdata, errMsg);
	}
	private native int getIcInfo( String portType, String portNo, 
			String tagList, byte[] AIDList, byte[] userInfo, 
			byte[] icType, byte[] lpicappdata, byte[] errMsg);
	
	/*!
	 * @brief 从IC卡获取ARQC
	 * @param [in] portType - 端口类型(C-串口, U-USB)
	 * @param [in] portNo - 端口号
	 * @param [in] txData - 交易数据
	 * @param [in] lpicappdata - 全局状态参数
	 * @param [out] ARQCLen - ARQC长度
	 * @param [out] ARQC - ARQC
	 * @param [out] errMsg - 外设操作错误信息
	 * @return 状态码，详见金融模块状态码定义类
	 */
	public int getARQC( String portType, String portNo, 
			String txData, String lpicappdata, 
			byte[] ARQCLen, byte[] ARQC, byte[] errMsg)
	{
		return genARQC( portType, portNo, txData, lpicappdata, ARQCLen, ARQC, errMsg);
	}
	private native int genARQC( String portType, String portNo, 
			String txData, String lpicappdata, 
			byte[] ARQCLen, byte[] ARQC, byte[] errMsg);
	
	/*!
	 * @brief 向IC卡发送ARPC，发送写卡脚本
	 * @param [in] portType - 端口类型(C-串口, U-USB)
	 * @param [in] portNo - 端口号
	 * @param [in] txData - 交易数据
	 * @param [in] lpicappdata - 全局状态参数
	 * @param [in] ARPC - ARPC
	 * @param [out] TC - 交易证书
	 * @param [out] ScriptResult - 脚本执行结果
	 * @param [out] errMsg - 外设操作错误信息
	 * @return 状态码，详见金融模块状态码定义类
	 */
	public int ctrScriptData( String portType, String portNo, 
			String txData, String lpicappdata, String ARPC, 
			byte[] TC, byte[] ScriptResult, byte[] errMsg )
	{
		return CtrScriptData( portType, portNo, txData, lpicappdata, ARPC, TC, ScriptResult, errMsg );
	}
	private native int CtrScriptData( String portType, String portNo, 
			String txData, String lpicappdata, String ARPC, 
			byte[] TC, byte[] ScriptResult, byte[] errMsg );
	
	/*!
	 * @brief 读取交易详细信息
	 * @param [in] portType - 端口类型(C-串口, U-USB)
	 * @param [in] portNo - 端口号
	 * @param [in] AIDName - 应用AID
	 * @param [out] txDetail - 交易明细
	 * @param [out] errMsg - 外设操作错误信息
	 * @return 状态码，详见金融模块状态码定义类
	 */
	public  int getTranDetail( String portType, String portNo, 
			String AIDName, byte[] txDetail, byte[] errMsg )
	{
		return GetTranDetail( portType, portNo, AIDName, txDetail, errMsg );
	}
	private native int GetTranDetail( String portType, String portNo, 
			String AIDName, byte[] txDetail, byte[] errMsg );
	
	/*!
	 * @brief 向IC卡下电
	 * @param [in] portType - 端口类型(C-串口, U-USB)
	 * @param [in] portNo - 端口号
	 * @param [out] errMsg - 外设操作错误信息
	 * @return 状态码，详见金融模块状态码定义类
	 */
	public int powerOff( String portType, String portNo, byte[] errMsg )
	{
		return PowerOff( portType, portNo, errMsg);
	}
	private native int PowerOff( String portType, String portNo, byte[] errMsg );

	/*!
	 * @brief 向IC卡上电
	 * @param [in] portType - 端口类型(C-串口, U-USB)
	 * @param [in] portNo - 端口号
	 * @param [out] errMsg - 外设操作错误信息
	 * @return 状态码，详见金融模块状态码定义类
	 */
	public int powerOn( int cardNo, String portType, String portNo, byte[] errMsg )
	{
		return PowerOn( cardNo,portType, portNo, errMsg);
	}
	private native int PowerOn( int cardNo,String portType, String portNo, byte[] errMsg );

	/*!
	 * @brief 根据PBOC标签（Tag）获取数据
	 * @param [in] portType - 端口类型(C-串口, U-USB)
	 * @param [in] portNo - 端口号
	 * @param [in] szTagName - PBOC标签
	 * @param [in] szAppData - 全局状态参数
	 * @param [out] szTagData - 标签数据
	 * @param [out] errMsg - 外设操作错误信息
	 * @return 状态码，详见金融模块状态码定义类
	 */
	public  int getDataByPBOCTag(String portType, String portNo, 
			String szTagName, String szAppData, byte[] szTagData, byte[] errMsg )
	{
		return getTagData( portType, portNo, szTagName, szAppData, szTagData, errMsg );

	}
	private native int getTagData(String portType, String portNo, 
			String szTagName, String szAppData, byte[] szTagData, byte[] errMsg );


	/*!
	 * @brief 执行APDU命令
	 * @param [in] portType - 端口类型(C-串口, U-USB)
	 * @param [in] portNo - 端口号
	 * @param [out] inAPDU - APDU的输入输出数据
	 * @param [out] errMsg - 外设操作错误信息
	 * @return 状态码，详见金融模块状态码定义类
	 */
	public int doAPDU(String portType, String portNo, 
			APDUData inAPDU, byte[] errMsg )
	{
		return  DoAPDU( portType, portNo, inAPDU, errMsg );
	}
	private native int DoAPDU(String portType, String portNo, 
			APDUData inAPDU, byte[] errMsg );
}
