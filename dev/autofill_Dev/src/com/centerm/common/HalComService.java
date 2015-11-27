package com.centerm.common;

public class HalComService 
{	
	static 
    {
    	System.loadLibrary( "halcomservice" );
    }
	
	
	public HalComService()
	{
		
	}
	
	
	/*!
	 * @brief �򿪴���
	 * @param nComNo - ���ںţ�1��ʾ����1��2��ʾ����2
	 * @return ����������
	 */
	public int openCom( int nComNo)
	{
		return openUsart( nComNo );
	}
	private native int openUsart( int nComNo );
	
	
	/*!
	 * @brief �رմ���ͨѶ������ִ�гɹ�
	 * @param fd - ����������
	 */
	 public void closeCom( int fd )
	 {
		 closeUsart(fd);
	 }
	 private native void closeUsart( int fd );
    
    /*!
     * @brief �Ӵ��ڶ�ȡ����
     * @param fd - ����������
     * @param nDataMaxLen - ������󳤶�
     * @return ��ȡ���ֽ���
     */
    public  byte[] readCom( int fd, int nMaxLen )
    {
    	return readUsart( fd, nMaxLen );
    }
    private native byte[] readUsart( int fd, int nMaxLen );
    
    /*!
     * @brief �򴮿�д����
     * @param fd - ����������
     * @param dataArray -��������
     * @param len - ���ݳ���
     * @return д�ɹ����ֽ���
     */
    public int writeCom( int fd, byte[] data, int len )
    {
    	return writeUsart( fd, data, len );
    }
    private native int writeUsart( int fd, byte[] data, int len );
    
    /*!
     * @brief ���SD��
     * @return 0��ʾδ���룬���򷵻�SD����С����λM
     */
    public int detectMemCard()
    {
    	return detectMMC();
    }
    private native int detectMMC();
    
    /*!
     * @brief ����Ի�оƬ
     * @return 0��ʾʧ�ܣ�1��ʾ��ȷ
     */
    public int detectDestory()
    {
    	return detectdestory();
    }
    private native int detectdestory();
    
    /*!
     * @brief ���nand
     * @return 0��ʾ���ʧ�ܣ����򷵻�nand��С����λM
     */
    public  int getNandFlashSize()
    {
    	return getNandFlash();
    }
    private native int getNandFlash();
    
    /*!
     * @brief ���HIDͨ������
     * @return 0��ʾʧ�ܣ�1��ʾ�ɹ�
     */
    public int detectHid()
    {
    	return detectHID();
    }
    private native int detectHID();
    
    /*!
     * @brief ����ڴ�
     * @return ��һ�ֽ�0x30��ʾʧ�ܣ� ��һ�ֽ�0x31��ʾ�ɹ�
     */
    public byte[] detectMemery()
    {
    	return detectMEM();
    }
    private native byte[] detectMEM();

    /*!
     * @brief ��ȡCPUʹ��
     * @return CPUʹ����
     */
    public int getCPURt()
    {
    	return getCPURate();
    }
    private native int getCPURate();

}
