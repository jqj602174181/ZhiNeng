package com.centerm.util.exception;

/*!
 * @brief 自定义异常类
 * @par 说明：
 * 			该类继承于Exception
 */
public class UtilException extends Exception{
	private static final long serialVersionUID = -7513714207781156743L;
	
	public UtilException() 
	{
		super();		
	}
	
	public UtilException(String errMsg) {
		super(errMsg);
	}
}
