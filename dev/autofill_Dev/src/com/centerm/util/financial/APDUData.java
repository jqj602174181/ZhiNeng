package com.centerm.util.financial;

/*!
 * @brief Ӧ������Э�鵥Ԫ�ṹ����
 * @par ˵����
 * 			���ඨ����Ӧ������Э��������Ҫ�Ĳ���
 */
public class APDUData {

	public APDUData() {
	}

	public byte[] Command = new byte[4];/*!<ָ�� */
	public byte[] DataIn = new byte[256];/*!<�������� */
	public byte[] DataOut = new byte[1024];/*!<�������� */
	public byte SW1;/*!<״̬�� */
	public byte SW2;/*!<״̬�� */
	public byte Lc; /*!<�������ݵĳ��� */
	public byte Le;/*!<�������յ��ĳ��ȣ����Բ��� */
	public int LengthOut;/*!<�������ݳ��� */
}
