package com.centerm.autofill.appframework.utils.printer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import com.centerm.autofill.appframework.exception.UtilException;

import android.util.Log;

/*!
 * @brief ��ӡ�������ࣨ��һʵ����
 * @par ˵����
 * 		�����ṩ�Դ�ӡ���Ļ�����������ӡͼƬ����ӡ���֡���ȡ�豸�汾��Ϣ������\��ȡ�������ڡ�����\��ȡ�豸��ŵȡ�
 */
public class PrinterService {
	static PrinterService service = null; // ��ӡ�������

	private PrinterService() {

	}

	/*
	 * !
	 * 
	 * @brief ��ӡ������һʵ��
	 * 
	 * @return ���ش�ӡ����һʵ��
	 */
	public static PrinterService getService() {
		if (service == null)
			service = new PrinterService();
		return service;
	}

	static {
		// ���ض�̬��
		System.loadLibrary("printerservice");
	}

	/********************* ��̬���з������� *************************/
	/*
	 * !
	 * 
	 * @brief ��ȡ�豸��Ϣ
	 * 
	 * @return �豸��Ϣ
	 */
	private native String GetDevinfo();

	/*
	 * !
	 * 
	 * @brief �����豸���
	 * 
	 * @param [in] num - �豸���
	 * 
	 * @retval 0 �ɹ�
	 * 
	 * @retval -1 ʧ��
	 */
	private native int SetDevNo(String num);

	/*
	 * !
	 * 
	 * @brief ��ȡ��ӡ״̬
	 * 
	 * @return ��ӡ��״̬
	 */
	private native int GetStatus();

	/*
	 * !
	 * 
	 * @brief ��ӡ�ַ�������Ӣ�ģ�
	 * 
	 * @param [in] dataArray - ��ӡ����
	 * 
	 * @param [in] size -��ӡ���ݴ�С
	 * 
	 * @param [in] step - ������
	 * 
	 * @retval 0 �ɹ�
	 * 
	 * @retval -1 ʧ��
	 */
	private native int PrintString(byte[] dataArray, int size, int step);

	/*
	 * !
	 * 
	 * @brief ��ӡͼƬ
	 * 
	 * @param [in] align - ���뷽ʽ
	 * 
	 * @param [in] imagewidth - ͼƬ���
	 * 
	 * @param [in] dataArray - ��ӡ���ݣ�����
	 * 
	 * @param [in] size - ��ӡ���ݴ�С
	 * 
	 * @param [in] step - ������
	 * 
	 * @return 0��ʾ��ӡ�ɹ���-1��ʾ��ӡʧ��,-2��ʾ��������-3��ʾȱֽ��-4��ʾ����
	 */
	private native int PrintImage(int align, int imagewidth, byte[] dataArray,
			int size, int step);

	/*
	 * !
	 * 
	 * @brief �����м��
	 * 
	 * @param [in] linespan - �м��
	 * 
	 * @retval 0 �ɹ�
	 * 
	 * @retval -1 ʧ��
	 */
	private native int SetLinespan(int linespan);

	/*
	 * !
	 * 
	 * @brief �������ұ߾�
	 * 
	 * @param [in] Ltn - ��߾�
	 * 
	 * @param [in] Rtn - �ұ߾�
	 * 
	 * @retval 0 �ɹ�
	 * 
	 * @retval -1 ʧ��
	 */
	private native int SetMargin(int Ltn, int Rtn);

	/*
	 * ! \brief ���ô�ӡ��ģʽ \param [in] bDoubleHeight - true�����ñ��ߣ�false��ȡ������ \param
	 * [in] bDoubleWidth - true�����ñ���false��ȡ������ \param [in] bUnderLine -
	 * true�������»��ߣ�false��ȡ���»��� \retval 0 �ɹ� \retval -1 ʧ��
	 */
	private native int SetMode(boolean bDoubleHeight, boolean bDoubleWidth,
			boolean bUnderLine);

	/*
	 * !
	 * 
	 * @brief ��ʼ����ӡ��
	 * 
	 * @retval 0 �ɹ�
	 * 
	 * @retval -1 ʧ��
	 */
	private native int InitPrinter();

	/*
	 * !
	 * 
	 * @brief ����
	 * 
	 * @param [in] path - �����ļ�·��
	 * 
	 * @retval 0 �ɹ�
	 * 
	 * @retval -1 ʧ��
	 * 
	 * @retval -2 ��ʾ�����������ϵ�ǰ�豸
	 * 
	 * @retval -3 �汾����
	 */
	private native int Update(String path);

	/*
	 * !
	 * 
	 * @brief ������������
	 * 
	 * @param [in] date - ��������
	 * 
	 * @return 0��ʾ���óɹ���-1��ʾ�޷��ظ�������������
	 */
	private native int SetDate(byte[] date);

	/*
	 * !
	 * 
	 * @brief ��ȡ������ӡ�����Ȱٷֱ�
	 * 
	 * @return ���Ȱٷֱȣ���50��ʾ������50%
	 */
	private native int GetUpdateProgress();

	/*
	 * !
	 * 
	 * @brief ��ȡ����������汾
	 * 
	 * @param[in] szPath - �����ļ�·��
	 * 
	 * @return �������汾�ַ���
	 */
	private native String GetUpdateVersion(String szPath);

	/********************* ����˽�к����ͱ��� *************************/
	private static final String errProtocol = "���豸ͨ��ʧ�ܣ���ͨ��Э����󣬻�δ֪����";
	private static final String errParameter = "��������";
	private static final String errSetProductionDate = "�޷��ظ�������������";
	private static final String errOutofPaper = "ȱֽ";
	private static final String errOverheated = "����";
	private static final String errFileNotMatch = "�����������ϵ�ǰ�豸";
	private static final String errVersionLow = "�汾����";
	private static final String errImagine = "�ļ���ʽ����ȷ";
	private static final String errResFile = "�ļ�������";
	private static final String CharMatrix1 = "16*16";
	private static final String CharMatrix2 = "24*24";
	private static final String CharMatrix3 = "32*32";

	// public static String
	/*
	 * !
	 * 
	 * @brief ��ȡ�豸��Ϣ
	 * 
	 * @return �ɹ������豸��Ϣ,ʧ�ܽ��׳��쳣
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	private String getAllDevInfo(String name) throws Exception {
		try {
			String info = GetDevinfo();
			if (info != null) {
				JSONObject o = new JSONObject(info);
				return o.getString(name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new UtilException(errProtocol);
	}

	/*
	 * !
	 * 
	 * @brief jni��������ֵת��
	 * 
	 * @return �ɹ�������Ӧboolean,ʧ�ܽ��׳��쳣
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	private boolean GetIntReturnToBoolean(int nRet) throws Exception {
		if (nRet == 0) {
			return true;
		} else if (nRet == 1) {
			return false;
		}
		throw new UtilException(errProtocol);
	}

	/*
	 * !
	 * 
	 * @brief ��ӡͼƬ
	 * 
	 * @param [in] is - ͼƬ��Ϣ������
	 * 
	 * @param [in] nAlign - ���䷽ʽ
	 * 
	 * @param [in] nStep - ����
	 * 
	 * @return 0��ʾ��ӡ�ɹ���-1��ʾ��ӡʧ��,-2��ʾ��������-3��ʾȱֽ��-4��ʾ���ȣ� -5 ͼƬ����bmpͼƬ
	 */
	public int printImageFromIs(InputStream is, int nAlign, int nStep) {
		int mImageOffset = 0, n = 0, nBitCount = 0, nCount = 0;
		int mImageWidth = 0, mImageHeight, mOneLinePixHoldByes = 0;
		int size = 0, nTemp = 0, i = 0, j = 0;
		int ret = -10;
		byte[] printByte;
		byte[] tmpByte;
		byte[] buffer = new byte[1024];
		int len = -1;
		int line = 0;
		int maxline = 1;
		int linesize = 0;
		try {
			ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
			while ((len = is.read(buffer)) != -1) {
				bytestream.write(buffer, 0, len);
			}

			byte imgData[] = bytestream.toByteArray();
			System.out.println("imgData.length="+imgData.length);
			is.close();
			bytestream.close();
			int mImageBitCount = (imgData[28] & 0xff)
					| ((imgData[29] & 0xff) << 8);
			// Log.i("mImageBitCount", String.valueOf(mImageBitCount));
			// System.out.println("imgData[0]="+imgData[0]+"  imgData[1]="+imgData[1]+"  mImageBitCount"+mImageBitCount);
			if (imgData[0] == 0x42 && imgData[1] == 0x4D && mImageBitCount == 1) {
				// ���
				mImageWidth = (imgData[18] & 0xff)
						| ((imgData[19] & 0xff) << 8)
						| ((imgData[20] & 0xff) << 16)
						| ((imgData[21] & 0xff) << 24);
				// Log.i("width", String.valueOf(mImageWidth));
				
				System.out.println( "mImageWidth="+mImageWidth);

				// �߶�
				mImageHeight = (imgData[22] & 0xff)
						| ((imgData[23] & 0xff) << 8)
						| ((imgData[24] & 0xff) << 16)
						| ((imgData[25] & 0xff) << 24);
				// Log.i("height", String.valueOf(mImageHeight));
				
				System.out.println( "mImageHeight="+mImageHeight);

				// ƫ����
				mImageOffset = (imgData[10] & 0xff)
						| ((imgData[11] & 0xff) << 8)
						| ((imgData[12] & 0xff) << 16)
						| ((imgData[13] & 0xff) << 24);
				
				System.out.println("mImageOffset="+mImageOffset);
				// Log.i("mImageOffset", String.valueOf(mImageOffset));

				// ���ͼƬ��С����Ϣ
				mOneLinePixHoldByes = (((mImageWidth + 7) / 8 + 3) / 4) * 4;// ��Ϊ���ı���
				// Log.i("mOneLinePixHoldByes",
				// String.valueOf(mOneLinePixHoldByes));

				size = mImageHeight * mOneLinePixHoldByes;

				// Log.i("size", String.valueOf(size));
				printByte = new byte[size];
				tmpByte = new byte[size];
				System.out.println( "size="+size);
				System.out.println( "mOneLinePixHoldByes="+mOneLinePixHoldByes);
				
				//System.out.println( "byData.length="+byData.length);
				nTemp += (mImageHeight - 1) * mOneLinePixHoldByes;
				for (i = 0; i < mImageHeight; i++) {
					/*
					 * for ( j = 0; j < mOneLinePixHoldByes; j++) {
					 * tmpByte[nTemp + j] = imgData[ mOneLinePixHoldByes * i + j
					 * + mImageOffset]; }
					 */
					//System.out.println("i="+i);
					//System.out.println("mOneLinePixHoldByes * i="+mOneLinePixHoldByes * i);
					System.arraycopy(imgData, mOneLinePixHoldByes * i
							+ mImageOffset, tmpByte, nTemp, mOneLinePixHoldByes);
					
					nBitCount = mOneLinePixHoldByes * 8 - mImageWidth;
					// Log.i("nBitCount", String.valueOf(nBitCount));
					nCount = 0;
					while (nBitCount > 0) {
						int k;
						// Log.i("nBitCount", String.valueOf(nBitCount));
						if (nBitCount < 8) {
							for (k = 0; k < nBitCount; k++) {
								tmpByte[nTemp + mOneLinePixHoldByes - nCount
										- 1] = (byte) (tmpByte[nTemp
										+ mOneLinePixHoldByes - nCount - 1] | (mImageBitCount << k));
							}
							nBitCount = nBitCount - k;
						} else {
							nCount++;
							for (k = 0; k < 8; k++) {
								tmpByte[nTemp + mOneLinePixHoldByes - nCount] = (byte) (tmpByte[nTemp
										+ mOneLinePixHoldByes - nCount] | (mImageBitCount << k));
							}
							nBitCount = nBitCount - 8;
						}
					}

					for (j = 0; j < mOneLinePixHoldByes; j++) {
						tmpByte[nTemp + j] = (byte) ~tmpByte[nTemp + j];
					}
					nTemp = nTemp - mOneLinePixHoldByes;
				}

				maxline = 256 / mOneLinePixHoldByes;// ���ȱ�����ָ���ƥ��
				Log.i("maxline", String.valueOf(maxline));
				while (line < mImageHeight) {
					maxline = line + maxline > mImageHeight ? mImageHeight
							- line : maxline;
					linesize = mOneLinePixHoldByes * maxline;
					for (j = 0; j < linesize; j++) {
						printByte[j] = tmpByte[n + j];
					}
					n += linesize;
					line += maxline;
					// ��������
					if (line == mImageHeight) {
						ret = PrintImage(nAlign, mOneLinePixHoldByes * 8,
								printByte, linesize, nStep);
					} else {
						ret = PrintImage(nAlign, mOneLinePixHoldByes * 8,
								printByte, linesize, 0);
					}

					if (ret != 0) {
						return ret;
					}
				}
			} else {
				return -5;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/*********************** JAR������ӿ� *************************/

	/*
	 * !
	 * 
	 * @brief ��ȡ��Ʒ����
	 * 
	 * @return �ɹ����ز�Ʒ����,ʧ�ܷ���null
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public String getDevName() throws Exception {
		return getAllDevInfo("name");
	}

	/*
	 * !
	 * 
	 * @brief ��ȡ�豸���
	 * 
	 * @return �豸���
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public String getDevNum() throws Exception {
		return getAllDevInfo("num");
	}

	/*
	 * !
	 * 
	 * @brief ��ȡ�豸����汾
	 * 
	 * @return �豸����汾
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public String getDevVersion() throws Exception {
		return getAllDevInfo("version");
	}

	/*
	 * !
	 * 
	 * @brief ��ȡ�豸�Ƿ�֧����������
	 * 
	 * @retval false ��֧����������
	 * 
	 * @retval true ֧����������
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public boolean canUpdate() throws Exception {
		int nRet = Integer.parseInt(getAllDevInfo("updateflag"));
		return GetIntReturnToBoolean(nRet);
	}

	/*
	 * !
	 * 
	 * @brief ��ȡ�豸��ǰģʽ
	 * 
	 * @retval 0 �û�ģʽ
	 * 
	 * @retval 1 ����ģʽ
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public int getDevMode() throws Exception {
		return Integer.parseInt(getAllDevInfo("mode"));
	}

	/*
	 * !
	 * 
	 * @brief ��ȡֽ�ſ��
	 * 
	 * @retval �ɹ�����ֽ�ſ��
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public int getPaperWidth() throws Exception {
		int nRet = -1;
		nRet = Integer.parseInt(getAllDevInfo("paperwidth"));
		return nRet;
	}

	/*
	 * !
	 * 
	 * @brief ��ȡ��Ч��ӡ���
	 * 
	 * @retval �ɹ�������Ч��ӡ���
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public int getPrintWidth() throws Exception {
		int nRet = -1;
		nRet = Integer.parseInt(getAllDevInfo("printwidth"));
		return nRet;
	}

	/*
	 * !
	 * 
	 * @brief ��ȡ��������
	 * 
	 * @retval �ɹ�������������
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public String getProductionDate() throws Exception {
		return getAllDevInfo("date");
	}

	/*
	 * !
	 * 
	 * @brief ��ȡ������Ч�ַ���
	 * 
	 * @retval �ɹ����ص�����Ч�ַ���
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public String getCharMatrix() throws Exception {
		int nRet = Integer.parseInt(getAllDevInfo("character"));
		if (nRet == 1) {
			return CharMatrix1;
		} else if (nRet == 2) {
			return CharMatrix2;
		} else if (nRet == 3) {
			return CharMatrix3;
		} else {
			throw new UtilException(errProtocol);
		}
	}

	/*
	 * !
	 * 
	 * @brief �����豸���
	 * 
	 * @param [in] strNum - �豸���
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public void setDevNum(String strNum) throws Exception {
		// Log.i("setDevNum Before ", strNum);
		int nRet = SetDevNo(strNum);
		// Log.i("setDevNum after ", strNum.getBytes("utf-8").toString());
		if (nRet == 0) {
			return;
		} else if (nRet == -2) {
			throw new UtilException(errParameter);
		} else
			throw new UtilException(errProtocol);
	}

	/*
	 * !
	 * 
	 * @brief ��ȡ��ӡ״̬
	 * 
	 * @return ��ӡ��״̬,����ֵ�������ڴ�ӡ��ģ��ָ�˵��V1.0��2.4�½�
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public int getPrintStatus() throws Exception {
		try {
			int nRet = GetStatus();
			if (nRet >= 0) {
				return nRet;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new UtilException(errProtocol);
	}

	/*
	 * !
	 * 
	 * @brief ��ȡ��ӡ״̬
	 * 
	 * @retval false ��ֽ
	 * 
	 * @retval true ��ֽ
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public boolean hasPaper() throws Exception {
		try {
			int nRet = GetStatus();
			if (nRet >= 0) {
				if ((nRet & 0x04) == 0x04) {
					return false;
				} else if ((nRet & 0x04) == 0x00) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new UtilException(errProtocol);
	}

	/*
	 * !
	 * 
	 * @brief ��ӡ�ַ�������Ӣ�ģ�
	 * 
	 * @param [in] strPrintData - ��ӡ����
	 * 
	 * @param [in] step - ������
	 * 
	 * @retval true ��ӡ�ɹ�
	 * 
	 * @retval false ʧ��
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public boolean printString(String strPrintData, int step) throws Exception {
		// String strTemp = new String(strPrintData.getBytes("GBK"));
		int nRet = PrintString(strPrintData.getBytes("GBK"),
				strPrintData.getBytes("GBK").length, step);
		if (nRet == 0) {
			return true;
		} else if (nRet == -2) {
			throw new UtilException(errParameter);
		} else if (nRet == -3) {
			throw new UtilException(errOutofPaper);
		} else if (nRet == -4) {
			throw new UtilException(errOverheated);
		} else
			throw new UtilException(errProtocol);
	}

	/*
	 * !
	 * 
	 * @brief ��ӡ�ַ�������Ӣ�ģ�
	 * 
	 * @param [in] strPrintData - ��ӡ����
	 * 
	 * @retval true ��ӡ�ɹ�
	 * 
	 * @retval false ʧ��
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public boolean printString(String strPrintData) throws Exception {
		return printString(strPrintData, 0);
	}

	/*
	 * !
	 * 
	 * @brief ��ӡͼƬ
	 * 
	 * @param [in] resFile - ��ӡͼƬ��·��
	 * 
	 * @param [in] nAlign - ���뷽ʽ
	 * 
	 * @param [in] nStep - ������
	 * 
	 * @return 0��ʾ��ӡ�ɹ���-1��ʾ��ӡʧ��,-2��ʾ��������-3��ʾȱֽ��-4��ʾ���ȣ� -5 ͼƬ����bmpͼƬ
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public boolean printImage(String resFile, int nAlign, int nStep)
			throws Exception {
		//File f = new File(resFile);
		//InputStream is = new FileInputStream(f);
		 InputStream is = getClass().getResourceAsStream( resFile );
		if (is == null)
			throw new IOException(errResFile);
		int nRet = printImageFromIs(is, nAlign, nStep);
		if (nRet == 0) {
			return true;
		} else if (nRet == -2) {
			throw new UtilException(errParameter);
		} else if (nRet == -3) {
			throw new UtilException(errOutofPaper);
		} else if (nRet == -4) {
			throw new UtilException(errOverheated);
		} else if (nRet == -5) {
			throw new UtilException(errImagine);
		} else
			throw new UtilException(errProtocol);
	}

	public int printImage(byte[] byData, int mImageWidth, int mImageHeight,
			int nAlign, int nStep) {
		int n = 0, nBitCount = 0, nCount = 0;
		int mOneLinePixHoldByes = 0;
		int size = 0, nTemp = 0, i = 0, j = 0;
		int ret = -10;
		byte[] printByte;
		byte[] tmpByte;
		int line = 0;
		int maxline = 1;
		int linesize = 0;
		try {
			int mImageBitCount = 1;
			// ���ͼƬ��С����Ϣ
			mOneLinePixHoldByes = (((mImageWidth + 7) / 8 + 3) / 4) * 4;// ��Ϊ���ı���,ÿ���ֽ���
			System.out.println( "mOneLinePixHoldByes="+mOneLinePixHoldByes);
			
			
			size = mImageHeight * mOneLinePixHoldByes;
			System.out.println( "size="+size);
			System.out.println( "byData.length="+byData.length);
			printByte = new byte[size];
			tmpByte = new byte[size];

			nTemp += (mImageHeight - 1) * mOneLinePixHoldByes;
			for (i = 0; i < mImageHeight; i++) {
				//System.out.println("i="+i);
				//System.out.println("mOneLinePixHoldByes * i="+mOneLinePixHoldByes * i);
				System.arraycopy(byData, mOneLinePixHoldByes * i, tmpByte,
						nTemp, mOneLinePixHoldByes);
				nBitCount = mOneLinePixHoldByes * 8 - mImageWidth;
				// Log.i("nBitCount", String.valueOf(nBitCount));
				nCount = 0;
				while (nBitCount > 0) {
					int k;
					// Log.i("nBitCount", String.valueOf(nBitCount));
					if (nBitCount < 8) {
						for (k = 0; k < nBitCount; k++) {
							tmpByte[nTemp + mOneLinePixHoldByes - nCount - 1] = (byte) (tmpByte[nTemp
									+ mOneLinePixHoldByes - nCount - 1] | (mImageBitCount << k));
						}
						nBitCount = nBitCount - k;
					} else {
						nCount++;
						for (k = 0; k < 8; k++) {
							tmpByte[nTemp + mOneLinePixHoldByes - nCount] = (byte) (tmpByte[nTemp
									+ mOneLinePixHoldByes - nCount] | (mImageBitCount << k));
						}
						nBitCount = nBitCount - 8;
					}
				}

				for (j = 0; j < mOneLinePixHoldByes; j++) {
					tmpByte[nTemp + j] = (byte) ~tmpByte[nTemp + j];
				}
				nTemp = nTemp - mOneLinePixHoldByes;
			}

			maxline = 256 / mOneLinePixHoldByes;// ���ȱ�����ָ���ƥ��
			Log.i("maxline", String.valueOf(maxline));
			while (line < mImageHeight) {
				maxline = line + maxline > mImageHeight ? mImageHeight - line
						: maxline;
				linesize = mOneLinePixHoldByes * maxline;
				for (j = 0; j < linesize; j++) {
					printByte[j] = tmpByte[n + j];
				}
				n += linesize;
				line += maxline;
				// ��������
				if (line == mImageHeight) {
					ret = PrintImage(nAlign, mOneLinePixHoldByes * 8,
							printByte, linesize, nStep);
				} else {
					ret = PrintImage(nAlign, mOneLinePixHoldByes * 8,
							printByte, linesize, 0);
				}

				if (ret != 0) {
					return ret;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/*
	 * !
	 * 
	 * @brief �����м��
	 * 
	 * @param [in] nLinesPan - �м��
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public void setLinespan(int nLinesPan) throws Exception {
		int nRet = SetLinespan(nLinesPan);
		if (nRet == 0) {
			return;
		} else if (nRet == -2) {
			throw new UtilException(errParameter);
		} else
			throw new UtilException(errProtocol);
	}

	/*
	 * !
	 * 
	 * @brief �������ұ߾�
	 * 
	 * @param [in] nLifePan - ��߾�
	 * 
	 * @param [in] nRightPan - �ұ߾�
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public void setMargin(int nLifePan, int nRightPan) throws Exception {
		int nRet = SetMargin(nLifePan, nRightPan);
		if (nRet == 0) {
			return;
		} else if (nRet == -2) {
			throw new UtilException(errParameter);
		} else
			throw new UtilException(errProtocol);
	}

	/*
	 * ! \brief ���ô�ӡ��ģʽ \param [in] bDoubleHeight - true�����ñ��ߣ�false��ȡ������ \param
	 * [in] bDoubleWidth - true�����ñ���false��ȡ������ \param [in] bUnderLine -
	 * true�������»��ߣ�false��ȡ���»��� \retval false ����ʧ�� \retval true ���óɹ� \retval �׳��쳣
	 */
	public void setMode(boolean bDoubleHeight, boolean bDoubleWidth,
			boolean bUnderLine) throws Exception {
		int nRet = SetMode(bDoubleHeight, bDoubleWidth, bUnderLine);
		if (nRet == 0) {
			return;
		} else {
			throw new UtilException(errProtocol);
		}
	}

	/*
	 * !
	 * 
	 * @brief ���ô�ӡ��
	 * 
	 * @retval false ����ʧ��
	 * 
	 * @retval true ���óɹ�
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public boolean resetPrint() throws Exception {
		int nRet = InitPrinter();
		return GetIntReturnToBoolean(nRet);
	}

	/*
	 * !
	 * 
	 * @brief ����
	 * 
	 * @param [in] path - �����ļ�·��
	 * 
	 * @retval false ����ʧ��
	 * 
	 * @retval true ���óɹ�
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public boolean update(String path) throws Exception {
		int nRet = Update(path);
		if (nRet == 0) {
			return true;
		} else if (nRet == -2) {
			throw new UtilException(errFileNotMatch);
		} else if (nRet == -3) {
			throw new UtilException(errVersionLow);
		} else
			throw new UtilException(errProtocol);
	}

	/*
	 * !
	 * 
	 * @brief ������������
	 * 
	 * @param [in] data - ��������
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public void setProductionDate(String data) throws Exception {
		int nRet = SetDate(data.getBytes("GBK"));
		Log.i("ProductionDate", String.valueOf(nRet));
		if (nRet == 0 || nRet == -2) {
			return;
		}
		/*
		 * else if( nRet == -2 ) { throw new
		 * UtilException(errSetProductionDate); }
		 */
		else
			throw new UtilException(errProtocol);
	}

	/*
	 * !
	 * 
	 * @brief ��ȡ������ӡ�����Ȱٷֱ�
	 * 
	 * @return ���Ȱٷֱȣ���50��ʾ������50%
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public int getUpdateProgress() throws Exception {
		try {
			int nRet = GetUpdateProgress();
			if (nRet >= 0) {
				return nRet;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new UtilException(errProtocol);
	}

	/*
	 * !
	 * 
	 * @brief ��ȡ����������汾
	 * 
	 * @param[in] szPath - �����ļ�·��
	 * 
	 * @return �������汾�ַ���
	 * 
	 * @throws Exception ʧ���׳��쳣
	 */
	public String getUpdateVersion(String szPath) throws Exception {
		try {
			String info = GetUpdateVersion(szPath);
			if (info != null) {
				JSONObject o = new JSONObject(info);
				return o.getString("version");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new UtilException(errProtocol);
	}
}
