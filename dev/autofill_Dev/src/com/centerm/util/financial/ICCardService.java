package com.centerm.util.financial;

/*!
 * @brief IC��������
 * @par ˵��:
 * 			�����ṩ��IC���Ļ���������
 */
public class ICCardService {
	static ICCardService instance;
	
	private ICCardService()
	{
	}
	
	/*!
	 * @brief ��ȡIC��serviceʵ��
	 * @return serviceʵ��
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
		//���ض�̬��
    	System.loadLibrary( "iccard" );
    }
	
	/*!
	 * \brief  ReSetCardSlot
	 * \par ˵��        	�����豸�Ŀ��ۺ�
	 * \param[in]         	
	 * \param[in]          
	 * \param[in]          
	 * \param[out]         
	 * \param[out]         
	 * \retval              0 -- �ɹ�   -1 ʧ��; 
	 * \note                  
	 */
	public int ReSetCardSlot( )
	{
		return ReaderReSetCardSlot();
	}
	private native int ReaderReSetCardSlot( );

	/*!
	 * \brief               instr_ReaderSetCardSlot
	 * \par ˵��        	�����豸�Ŀ��ۺ�
	 * \param[in]         	
	 * \param[in]          
	 * \param[in]          
	 * \param[out]         
	 * \param[out]         
	 * \retval              0 -- �ɹ�   -1 ʧ��; 
	 * \note                  
	 */
	public int SetCardSlot( int  nCardSlot  )
	{
		return ReaderSetCardSlot( nCardSlot );
	}
	private native int  ReaderSetCardSlot(   int  nCardSlot  );
	/*!
	 * \brief               instr_ReaderGetCardSlot
	 * \par ˵��        	��ȡ�豸�Ŀ��ۺ�
	 * \param[in]         	
	 * \param[in]          
	 * \param[in]          
	 * \param[out]         
	 * \param[out]         
	 * \retval              0 -- �ɹ�   -1 ʧ��; 
	 * \note                  
	 */
	public int GetCardSlot()
	{
		return ReaderGetCardSlot();
	}
	private native int ReaderGetCardSlot(  );
	
	/*!
	 * @brief ��ȡ����Ϣ
	 * @param [in] portType - �˿�����(C-����, U-USB)
	 * @param [in] portNo - �˿ں�
	 * @param [in] tagList - ����־�б�
	 * @param [out] AIDList - Ӧ���б�
	 * @param [out] userInfo - ������Ϣ
	 * @param [out] icType - IC����־
	 * @param [out] lpicappdata - ȫ��״̬����
	 * @param [out] errMsg - �������������Ϣ
	 * @return ״̬�룬�������ģ��״̬�붨����
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
	 * @brief ��IC����ȡARQC
	 * @param [in] portType - �˿�����(C-����, U-USB)
	 * @param [in] portNo - �˿ں�
	 * @param [in] txData - ��������
	 * @param [in] lpicappdata - ȫ��״̬����
	 * @param [out] ARQCLen - ARQC����
	 * @param [out] ARQC - ARQC
	 * @param [out] errMsg - �������������Ϣ
	 * @return ״̬�룬�������ģ��״̬�붨����
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
	 * @brief ��IC������ARPC������д���ű�
	 * @param [in] portType - �˿�����(C-����, U-USB)
	 * @param [in] portNo - �˿ں�
	 * @param [in] txData - ��������
	 * @param [in] lpicappdata - ȫ��״̬����
	 * @param [in] ARPC - ARPC
	 * @param [out] TC - ����֤��
	 * @param [out] ScriptResult - �ű�ִ�н��
	 * @param [out] errMsg - �������������Ϣ
	 * @return ״̬�룬�������ģ��״̬�붨����
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
	 * @brief ��ȡ������ϸ��Ϣ
	 * @param [in] portType - �˿�����(C-����, U-USB)
	 * @param [in] portNo - �˿ں�
	 * @param [in] AIDName - Ӧ��AID
	 * @param [out] txDetail - ������ϸ
	 * @param [out] errMsg - �������������Ϣ
	 * @return ״̬�룬�������ģ��״̬�붨����
	 */
	public  int getTranDetail( String portType, String portNo, 
			String AIDName, byte[] txDetail, byte[] errMsg )
	{
		return GetTranDetail( portType, portNo, AIDName, txDetail, errMsg );
	}
	private native int GetTranDetail( String portType, String portNo, 
			String AIDName, byte[] txDetail, byte[] errMsg );
	
	/*!
	 * @brief ��IC���µ�
	 * @param [in] portType - �˿�����(C-����, U-USB)
	 * @param [in] portNo - �˿ں�
	 * @param [out] errMsg - �������������Ϣ
	 * @return ״̬�룬�������ģ��״̬�붨����
	 */
	public int powerOff( String portType, String portNo, byte[] errMsg )
	{
		return PowerOff( portType, portNo, errMsg);
	}
	private native int PowerOff( String portType, String portNo, byte[] errMsg );

	/*!
	 * @brief ��IC���ϵ�
	 * @param [in] portType - �˿�����(C-����, U-USB)
	 * @param [in] portNo - �˿ں�
	 * @param [out] errMsg - �������������Ϣ
	 * @return ״̬�룬�������ģ��״̬�붨����
	 */
	public int powerOn( int cardNo, String portType, String portNo, byte[] errMsg )
	{
		return PowerOn( cardNo,portType, portNo, errMsg);
	}
	private native int PowerOn( int cardNo,String portType, String portNo, byte[] errMsg );

	/*!
	 * @brief ����PBOC��ǩ��Tag����ȡ����
	 * @param [in] portType - �˿�����(C-����, U-USB)
	 * @param [in] portNo - �˿ں�
	 * @param [in] szTagName - PBOC��ǩ
	 * @param [in] szAppData - ȫ��״̬����
	 * @param [out] szTagData - ��ǩ����
	 * @param [out] errMsg - �������������Ϣ
	 * @return ״̬�룬�������ģ��״̬�붨����
	 */
	public  int getDataByPBOCTag(String portType, String portNo, 
			String szTagName, String szAppData, byte[] szTagData, byte[] errMsg )
	{
		return getTagData( portType, portNo, szTagName, szAppData, szTagData, errMsg );

	}
	private native int getTagData(String portType, String portNo, 
			String szTagName, String szAppData, byte[] szTagData, byte[] errMsg );


	/*!
	 * @brief ִ��APDU����
	 * @param [in] portType - �˿�����(C-����, U-USB)
	 * @param [in] portNo - �˿ں�
	 * @param [out] inAPDU - APDU�������������
	 * @param [out] errMsg - �������������Ϣ
	 * @return ״̬�룬�������ģ��״̬�붨����
	 */
	public int doAPDU(String portType, String portNo, 
			APDUData inAPDU, byte[] errMsg )
	{
		return  DoAPDU( portType, portNo, inAPDU, errMsg );
	}
	private native int DoAPDU(String portType, String portNo, 
			APDUData inAPDU, byte[] errMsg );
}
