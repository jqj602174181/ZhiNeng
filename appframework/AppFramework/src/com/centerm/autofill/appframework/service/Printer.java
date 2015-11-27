package com.centerm.autofill.appframework.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.centerm.autofill.appframework.utils.printer.PrinterService;


public class Printer {

	private static Printer instance;
	private PrinterService service = PrinterService.getService();
	
	
	private Printer(){
	}
	
	public static Printer getInstance() {
		if( instance == null ) {
			instance = new Printer();
		}
		return instance;
	}
	
	public void Print(String buss, String preFormNo) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final String title = " \n     中  国  邮  政\n";//前面加一个\n防止第一行缩行
		final String info = "               您办理的业务是：\n"
				+ "               " + buss + "\n"
				+ "               预填单号:"+preFormNo + "\n"
				+ "           时间:" + format.format(new Date())+"\n";
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					//byte[] byData = QRUtil.CreateQR(info, 200, 200);//当前不打印二维码
					service.setMode(true, true, false);
					service.printString(title, 5);
					service.setMode(false, false, false);
					
					service.printString(info, 10);
					try {
						//service.printImage( byData, 200, 200, 1, 10);
						service.printString(" \n \n \n \n \n");//打印机有bug: 纯回车，无法走纸，因此采用空格+回车
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
}
