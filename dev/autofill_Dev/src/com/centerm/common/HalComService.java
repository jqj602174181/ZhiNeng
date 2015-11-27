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
	 * @brief 打开串口
	 * @param nComNo - 串口号，1表示串口1，2表示串口2
	 * @return 串口描述符
	 */
	public int openCom( int nComNo)
	{
		return openUsart( nComNo );
	}
	private native int openUsart( int nComNo );
	
	
	/*!
	 * @brief 关闭串口通讯，总是执行成功
	 * @param fd - 串口描述符
	 */
	 public void closeCom( int fd )
	 {
		 closeUsart(fd);
	 }
	 private native void closeUsart( int fd );
    
    /*!
     * @brief 从串口读取数据
     * @param fd - 串口描述符
     * @param nDataMaxLen - 数据最大长度
     * @return 读取的字节数
     */
    public  byte[] readCom( int fd, int nMaxLen )
    {
    	return readUsart( fd, nMaxLen );
    }
    private native byte[] readUsart( int fd, int nMaxLen );
    
    /*!
     * @brief 向串口写数据
     * @param fd - 串口描述符
     * @param dataArray -数据数组
     * @param len - 数据长度
     * @return 写成功的字节数
     */
    public int writeCom( int fd, byte[] data, int len )
    {
    	return writeUsart( fd, data, len );
    }
    private native int writeUsart( int fd, byte[] data, int len );
    
    /*!
     * @brief 检测SD卡
     * @return 0表示未插入，否则返回SD卡大小，单位M
     */
    public int detectMemCard()
    {
    	return detectMMC();
    }
    private native int detectMMC();
    
    /*!
     * @brief 检测自毁芯片
     * @return 0表示失败，1表示正确
     */
    public int detectDestory()
    {
    	return detectdestory();
    }
    private native int detectdestory();
    
    /*!
     * @brief 检测nand
     * @return 0表示检测失败，否则返回nand大小，单位M
     */
    public  int getNandFlashSize()
    {
    	return getNandFlash();
    }
    private native int getNandFlash();
    
    /*!
     * @brief 检测HID通信连接
     * @return 0表示失败，1表示成功
     */
    public int detectHid()
    {
    	return detectHID();
    }
    private native int detectHID();
    
    /*!
     * @brief 检测内存
     * @return 第一字节0x30表示失败， 第一字节0x31表示成功
     */
    public byte[] detectMemery()
    {
    	return detectMEM();
    }
    private native byte[] detectMEM();

    /*!
     * @brief 获取CPU使用
     * @return CPU使用率
     */
    public int getCPURt()
    {
    	return getCPURate();
    }
    private native int getCPURate();

}
