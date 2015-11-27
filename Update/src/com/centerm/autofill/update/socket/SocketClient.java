package com.centerm.autofill.update.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.centerm.autofill.update.listener.Condition;
import com.centerm.autofill.update.utils.ErrorUtil;

public class SocketClient {

	private Socket client;
	InputStream in = null;
	OutputStream out = null;

	public SocketClient() {
		client = new Socket();

	}

	/**
	 * @Title: SocketConnect
	 * @Description: 连接服务器
	 * @param HostName
	 *            ：服务器主机名或IP
	 * @param iPort
	 *            ：服务器端口号
	 * @param iTimeOut
	 *            ：连接超时时间
	 * @return true：连接成功；false：连接异常
	 * @throws
	 */
	public boolean SocketConnect(String HostName, int iPort, int iTimeOut) {
		// 连接服务器
		SocketAddress socketAddress = new InetSocketAddress(HostName, iPort);
		try {
			client.connect(socketAddress, iTimeOut * 1000);
			System.out.println("Socket connect");
		} catch (IOException e) {
			System.out.println("SocketConnect IOException ");
			return false;
		} catch (IllegalArgumentException e1) {
			System.out.println("SocketConnect IllegalArgumentException ");
			return false;
		}

		// 获取socket输入输出流
		try {
			in = client.getInputStream();
			out = client.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * @Title: SocketClose
	 * @Description: 断开与服务器连接
	 * @throws
	 */
	public void SocketClose() {
		try {
			client.close();
			System.out.println("SocketClient close");
		} catch (IOException e) {
			System.out.println("socket close error" + e.getMessage());
		}
	}

	/**
	 * @Title: sendMessage
	 * @Description: 发送数据
	 * @param buf
	 *            : 数据
	 * @param len
	 *            ：数据长度
	 * @return 状态码
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ErrorUtil.ERR_OPEN;
		}

		return ErrorUtil.ERR_SUCCESS;
	}

	/**
	 * @Title: readMessage
	 * @Description: 接收数据
	 * @param buf
	 *            ：接收缓冲区
	 * @param len
	 *            ：缓冲区大小
	 * @param condition
	 *            ：结束条件
	 * @param timeout
	 *            ：超时
	 * @return >=0:收到的数据长度；<0：错误码
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
			// 超时
			if ((System.currentTimeMillis() - startTime) > timeout * 1000) {
				return ErrorUtil.ERR_TIMEOUT;
			}

			// 读数据
			try {
				nCurRead = in.read(buf, nHasRead, len - nHasRead);
				if( nCurRead == -1 )
					return ErrorUtil.ERR_READ;
				nHasRead += nCurRead;
				System.out.println("cur" + nCurRead + "has" + nHasRead);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return ErrorUtil.ERR_READ;
			}

			// 结束条件
			if (condition != null) {
				if (condition.reach(buf, nHasRead)) {
					System.out.println("recv msg " + nHasRead + " byte");
					return nHasRead;
				}
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("recv msg " + nHasRead + " byte");
		return nHasRead;
	}
}
