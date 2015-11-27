package com.centerm.util.financial;

/*!
 * @brief 应用数据协议单元结构体类
 * @par 说明：
 * 			该类定义了应用数据协议结果体需要的参数
 */
public class APDUData {

	public APDUData() {
	}

	public byte[] Command = new byte[4];/*!<指令 */
	public byte[] DataIn = new byte[256];/*!<发送内容 */
	public byte[] DataOut = new byte[1024];/*!<接收内容 */
	public byte SW1;/*!<状态码 */
	public byte SW2;/*!<状态码 */
	public byte Lc; /*!<发送数据的长度 */
	public byte Le;/*!<期望接收到的长度，可以不传 */
	public int LengthOut;/*!<返回数据长度 */
}
