package com.centerm.util.exception;

/*!
 * @brief �Զ����쳣��
 * @par ˵����
 * 			����̳���Exception
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
