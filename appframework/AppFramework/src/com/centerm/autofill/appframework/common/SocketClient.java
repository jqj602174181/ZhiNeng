package com.centerm.autofill.appframework.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.centerm.autofill.appframework.listener.Condition;
import com.centerm.autofill.appframework.utils.ErrorUtil;

public class SocketClient {

	private Socket client;
	InputStream in = null;
	OutputStream out = null;

	public SocketClient() {
		client = new Socket();
	}

	/**
	 * @Title: SocketConnect
	 * @Description: ���ӷ�����
	 * @param HostName
	 *            ����������������IP
	 * @param iPort
	 *            ���������˿ں�
	 * @param iTimeOut
	 *            �����ӳ�ʱʱ��
	 * @return true�����ӳɹ���false�������쳣
	 * @throws
	 */
	public boolean SocketConnect(String HostName, int iPort, int iTimeOut) {
		// ���ӷ�����
		SocketAddress socketAddress = new InetSocketAddress(HostName, iPort);
		try {
			client.connect(socketAddress, iTimeOut * 1000);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalArgumentException e1) {
			System.out.println("SocketConnect IllegalArgumentException ");
			return false;
		}

		// ��ȡsocket���������
		try {
			in = client.getInputStream();
			out = client.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * @Title: SocketClose
	 * @Description: �Ͽ������������
	 * @throws
	 */
	public void SocketClose() {
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title: sendMessage
	 * @Description: ��������
	 * @param buf
	 *            : ����
	 * @param len
	 *            �����ݳ���
	 * @return ״̬��
	 * @throws
	 */
	public int sendMessage(byte[] buf, int len) {
		if (out == null) {
			return ErrorUtil.ERR_OPEN;
		}

		try {
			out.write(buf, 0, len);
			System.out.println("send msg " + len + " byte");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ErrorUtil.ERR_SUCCESS;
	}

	/**
	 * @Title: readMessage
	 * @Description: ��������
	 * @param buf
	 *            �����ջ�����
	 * @param len
	 *            ����������С
	 * @param condition
	 *            ����������
	 * @param timeout
	 *            ����ʱ
	 * @return >=0:�յ������ݳ��ȣ�<0��������
	 * @throws
	 */
	public int readMessage(byte[] buf, int len, Condition condition, int timeout) {
		if (out == null) {
			return ErrorUtil.ERR_OPEN;
		}

		long startTime = System.currentTimeMillis();
		int nHasRead = 0;
		int nCurRead = 0;

		while (nHasRead < len) {
			// ��ʱ
			if ((System.currentTimeMillis() - startTime) > timeout * 1000) {
				return ErrorUtil.ERR_TIMEOUT;
			}

			// ������
			try {
				nCurRead = in.read(buf, nHasRead, len - nHasRead);
				nHasRead += nCurRead;
			} catch (IOException e1) {
				e1.printStackTrace();
				return ErrorUtil.ERR_READ;
			}

			// ��������
			if (condition != null) {
				if (condition.reach(buf, nHasRead)) {
					System.out.println("recv msg " + nHasRead + " byte");
					return nHasRead;
				}
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("recv msg " + nHasRead + " byte");
		return nHasRead;
	}
}
