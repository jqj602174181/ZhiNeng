package com.centerm.autofill.appframework.utils.printer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import com.centerm.autofill.appframework.exception.UtilException;

import android.util.Log;

/*!
 * @brief 打印机服务类（单一实例）
 * @par 说明：
 * 		该类提供对打印机的基本操作：打印图片、打印文字、获取设备版本信息、设置\获取生产日期、设置\获取设备编号等。
 */
public class PrinterService {
	static PrinterService service = null; // 打印服务对象

	private PrinterService() {

	}

	/*
	 * !
	 * 
	 * @brief 打印机服务单一实例
	 * 
	 * @return 返回打印机单一实例
	 */
	public static PrinterService getService() {
		if (service == null)
			service = new PrinterService();
		return service;
	}

	static {
		// 加载动态库
		System.loadLibrary("printerservice");
	}

	/********************* 动态库中方法声明 *************************/
	/*
	 * !
	 * 
	 * @brief 获取设备信息
	 * 
	 * @return 设备信息
	 */
	private native String GetDevinfo();

	/*
	 * !
	 * 
	 * @brief 设置设备编号
	 * 
	 * @param [in] num - 设备编号
	 * 
	 * @retval 0 成功
	 * 
	 * @retval -1 失败
	 */
	private native int SetDevNo(String num);

	/*
	 * !
	 * 
	 * @brief 获取打印状态
	 * 
	 * @return 打印机状态
	 */
	private native int GetStatus();

	/*
	 * !
	 * 
	 * @brief 打印字符串（中英文）
	 * 
	 * @param [in] dataArray - 打印数据
	 * 
	 * @param [in] size -打印数据大小
	 * 
	 * @param [in] step - 步进数
	 * 
	 * @retval 0 成功
	 * 
	 * @retval -1 失败
	 */
	private native int PrintString(byte[] dataArray, int size, int step);

	/*
	 * !
	 * 
	 * @brief 打印图片
	 * 
	 * @param [in] align - 对齐方式
	 * 
	 * @param [in] imagewidth - 图片宽度
	 * 
	 * @param [in] dataArray - 打印数据（点阵）
	 * 
	 * @param [in] size - 打印数据大小
	 * 
	 * @param [in] step - 步进数
	 * 
	 * @return 0表示打印成功，-1表示打印失败,-2表示参数错误，-3表示缺纸，-4表示过热
	 */
	private native int PrintImage(int align, int imagewidth, byte[] dataArray,
			int size, int step);

	/*
	 * !
	 * 
	 * @brief 设置行间距
	 * 
	 * @param [in] linespan - 行间距
	 * 
	 * @retval 0 成功
	 * 
	 * @retval -1 失败
	 */
	private native int SetLinespan(int linespan);

	/*
	 * !
	 * 
	 * @brief 设置左右边距
	 * 
	 * @param [in] Ltn - 左边距
	 * 
	 * @param [in] Rtn - 右边距
	 * 
	 * @retval 0 成功
	 * 
	 * @retval -1 失败
	 */
	private native int SetMargin(int Ltn, int Rtn);

	/*
	 * ! \brief 设置打印机模式 \param [in] bDoubleHeight - true：启用倍高，false：取消倍高 \param
	 * [in] bDoubleWidth - true：启用倍宽，false：取消倍宽 \param [in] bUnderLine -
	 * true：启用下划线，false：取消下划线 \retval 0 成功 \retval -1 失败
	 */
	private native int SetMode(boolean bDoubleHeight, boolean bDoubleWidth,
			boolean bUnderLine);

	/*
	 * !
	 * 
	 * @brief 初始化打印机
	 * 
	 * @retval 0 成功
	 * 
	 * @retval -1 失败
	 */
	private native int InitPrinter();

	/*
	 * !
	 * 
	 * @brief 升级
	 * 
	 * @param [in] path - 升级文件路径
	 * 
	 * @retval 0 成功
	 * 
	 * @retval -1 失败
	 * 
	 * @retval -2 表示升级包不符合当前设备
	 * 
	 * @retval -3 版本过低
	 */
	private native int Update(String path);

	/*
	 * !
	 * 
	 * @brief 设置生产日期
	 * 
	 * @param [in] date - 生产日期
	 * 
	 * @return 0表示设置成功，-1表示无法重复设置生产日期
	 */
	private native int SetDate(byte[] date);

	/*
	 * !
	 * 
	 * @brief 获取升级打印机进度百分比
	 * 
	 * @return 进度百分比，如50表示升级了50%
	 */
	private native int GetUpdateProgress();

	/*
	 * !
	 * 
	 * @brief 获取升级包软件版本
	 * 
	 * @param[in] szPath - 升级文件路径
	 * 
	 * @return 升级包版本字符串
	 */
	private native String GetUpdateVersion(String szPath);

	/********************* 本地私有函数和变量 *************************/
	private static final String errProtocol = "与设备通信失败，或通信协议错误，或未知错误";
	private static final String errParameter = "参数错误";
	private static final String errSetProductionDate = "无法重复设置生产日期";
	private static final String errOutofPaper = "缺纸";
	private static final String errOverheated = "过热";
	private static final String errFileNotMatch = "升级包不符合当前设备";
	private static final String errVersionLow = "版本过低";
	private static final String errImagine = "文件格式不正确";
	private static final String errResFile = "文件不存在";
	private static final String CharMatrix1 = "16*16";
	private static final String CharMatrix2 = "24*24";
	private static final String CharMatrix3 = "32*32";

	// public static String
	/*
	 * !
	 * 
	 * @brief 获取设备信息
	 * 
	 * @return 成功返回设备信息,失败将抛出异常
	 * 
	 * @throws Exception 失败抛出异常
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
	 * @brief jni函数返回值转换
	 * 
	 * @return 成功返回相应boolean,失败将抛出异常
	 * 
	 * @throws Exception 失败抛出异常
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
	 * @brief 打印图片
	 * 
	 * @param [in] is - 图片信息数据流
	 * 
	 * @param [in] nAlign - 对其方式
	 * 
	 * @param [in] nStep - 步进
	 * 
	 * @return 0表示打印成功，-1表示打印失败,-2表示参数错误，-3表示缺纸，-4表示过热， -5 图片不是bmp图片
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
				// 宽度
				mImageWidth = (imgData[18] & 0xff)
						| ((imgData[19] & 0xff) << 8)
						| ((imgData[20] & 0xff) << 16)
						| ((imgData[21] & 0xff) << 24);
				// Log.i("width", String.valueOf(mImageWidth));
				
				System.out.println( "mImageWidth="+mImageWidth);

				// 高度
				mImageHeight = (imgData[22] & 0xff)
						| ((imgData[23] & 0xff) << 8)
						| ((imgData[24] & 0xff) << 16)
						| ((imgData[25] & 0xff) << 24);
				// Log.i("height", String.valueOf(mImageHeight));
				
				System.out.println( "mImageHeight="+mImageHeight);

				// 偏移量
				mImageOffset = (imgData[10] & 0xff)
						| ((imgData[11] & 0xff) << 8)
						| ((imgData[12] & 0xff) << 16)
						| ((imgData[13] & 0xff) << 24);
				
				System.out.println("mImageOffset="+mImageOffset);
				// Log.i("mImageOffset", String.valueOf(mImageOffset));

				// 获得图片大小等信息
				mOneLinePixHoldByes = (((mImageWidth + 7) / 8 + 3) / 4) * 4;// 化为４的倍数
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

				maxline = 256 / mOneLinePixHoldByes;// 长度必须与指令集相匹配
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
					// 发送数据
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

	/*********************** JAR包对外接口 *************************/

	/*
	 * !
	 * 
	 * @brief 获取产品名称
	 * 
	 * @return 成功返回产品名称,失败返回null
	 * 
	 * @throws Exception 失败抛出异常
	 */
	public String getDevName() throws Exception {
		return getAllDevInfo("name");
	}

	/*
	 * !
	 * 
	 * @brief 获取设备编号
	 * 
	 * @return 设备编号
	 * 
	 * @throws Exception 失败抛出异常
	 */
	public String getDevNum() throws Exception {
		return getAllDevInfo("num");
	}

	/*
	 * !
	 * 
	 * @brief 获取设备软件版本
	 * 
	 * @return 设备软件版本
	 * 
	 * @throws Exception 失败抛出异常
	 */
	public String getDevVersion() throws Exception {
		return getAllDevInfo("version");
	}

	/*
	 * !
	 * 
	 * @brief 获取设备是否支持在线升级
	 * 
	 * @retval false 不支持在线升级
	 * 
	 * @retval true 支持在线升级
	 * 
	 * @throws Exception 失败抛出异常
	 */
	public boolean canUpdate() throws Exception {
		int nRet = Integer.parseInt(getAllDevInfo("updateflag"));
		return GetIntReturnToBoolean(nRet);
	}

	/*
	 * !
	 * 
	 * @brief 获取设备当前模式
	 * 
	 * @retval 0 用户模式
	 * 
	 * @retval 1 升级模式
	 * 
	 * @throws Exception 失败抛出异常
	 */
	public int getDevMode() throws Exception {
		return Integer.parseInt(getAllDevInfo("mode"));
	}

	/*
	 * !
	 * 
	 * @brief 获取纸张宽度
	 * 
	 * @retval 成功返回纸张宽度
	 * 
	 * @throws Exception 失败抛出异常
	 */
	public int getPaperWidth() throws Exception {
		int nRet = -1;
		nRet = Integer.parseInt(getAllDevInfo("paperwidth"));
		return nRet;
	}

	/*
	 * !
	 * 
	 * @brief 获取有效打印宽度
	 * 
	 * @retval 成功返回有效打印宽度
	 * 
	 * @throws Exception 失败抛出异常
	 */
	public int getPrintWidth() throws Exception {
		int nRet = -1;
		nRet = Integer.parseInt(getAllDevInfo("printwidth"));
		return nRet;
	}

	/*
	 * !
	 * 
	 * @brief 获取生产日期
	 * 
	 * @retval 成功返回生产日期
	 * 
	 * @throws Exception 失败抛出异常
	 */
	public String getProductionDate() throws Exception {
		return getAllDevInfo("date");
	}

	/*
	 * !
	 * 
	 * @brief 获取点阵有效字符集
	 * 
	 * @retval 成功返回点阵有效字符集
	 * 
	 * @throws Exception 失败抛出异常
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
	 * @brief 设置设备编号
	 * 
	 * @param [in] strNum - 设备编号
	 * 
	 * @throws Exception 失败抛出异常
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
	 * @brief 获取打印状态
	 * 
	 * @return 打印机状态,具体值参照升腾打印机模块指令集说明V1.0中2.4章节
	 * 
	 * @throws Exception 失败抛出异常
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
	 * @brief 获取打印状态
	 * 
	 * @retval false 无纸
	 * 
	 * @retval true 有纸
	 * 
	 * @throws Exception 失败抛出异常
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
	 * @brief 打印字符串（中英文）
	 * 
	 * @param [in] strPrintData - 打印数据
	 * 
	 * @param [in] step - 步进数
	 * 
	 * @retval true 打印成功
	 * 
	 * @retval false 失败
	 * 
	 * @throws Exception 失败抛出异常
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
	 * @brief 打印字符串（中英文）
	 * 
	 * @param [in] strPrintData - 打印数据
	 * 
	 * @retval true 打印成功
	 * 
	 * @retval false 失败
	 * 
	 * @throws Exception 失败抛出异常
	 */
	public boolean printString(String strPrintData) throws Exception {
		return printString(strPrintData, 0);
	}

	/*
	 * !
	 * 
	 * @brief 打印图片
	 * 
	 * @param [in] resFile - 打印图片的路径
	 * 
	 * @param [in] nAlign - 对齐方式
	 * 
	 * @param [in] nStep - 步进数
	 * 
	 * @return 0表示打印成功，-1表示打印失败,-2表示参数错误，-3表示缺纸，-4表示过热， -5 图片不是bmp图片
	 * 
	 * @throws Exception 失败抛出异常
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
			// 获得图片大小等信息
			mOneLinePixHoldByes = (((mImageWidth + 7) / 8 + 3) / 4) * 4;// 化为４的倍数,每行字节数
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

			maxline = 256 / mOneLinePixHoldByes;// 长度必须与指令集相匹配
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
				// 发送数据
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
	 * @brief 设置行间距
	 * 
	 * @param [in] nLinesPan - 行间距
	 * 
	 * @throws Exception 失败抛出异常
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
	 * @brief 设置左右边距
	 * 
	 * @param [in] nLifePan - 左边距
	 * 
	 * @param [in] nRightPan - 右边距
	 * 
	 * @throws Exception 失败抛出异常
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
	 * ! \brief 设置打印机模式 \param [in] bDoubleHeight - true：启用倍高，false：取消倍高 \param
	 * [in] bDoubleWidth - true：启用倍宽，false：取消倍宽 \param [in] bUnderLine -
	 * true：启用下划线，false：取消下划线 \retval false 设置失败 \retval true 设置成功 \retval 抛出异常
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
	 * @brief 重置打印机
	 * 
	 * @retval false 设置失败
	 * 
	 * @retval true 设置成功
	 * 
	 * @throws Exception 失败抛出异常
	 */
	public boolean resetPrint() throws Exception {
		int nRet = InitPrinter();
		return GetIntReturnToBoolean(nRet);
	}

	/*
	 * !
	 * 
	 * @brief 升级
	 * 
	 * @param [in] path - 升级文件路径
	 * 
	 * @retval false 设置失败
	 * 
	 * @retval true 设置成功
	 * 
	 * @throws Exception 失败抛出异常
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
	 * @brief 设置生产日期
	 * 
	 * @param [in] data - 生产日期
	 * 
	 * @throws Exception 失败抛出异常
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
	 * @brief 获取升级打印机进度百分比
	 * 
	 * @return 进度百分比，如50表示升级了50%
	 * 
	 * @throws Exception 失败抛出异常
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
	 * @brief 获取升级包软件版本
	 * 
	 * @param[in] szPath - 升级文件路径
	 * 
	 * @return 升级包版本字符串
	 * 
	 * @throws Exception 失败抛出异常
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
