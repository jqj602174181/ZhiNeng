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
		final String title = " \n     ��  ��  ��  ��\n";//ǰ���һ��\n��ֹ��һ������
		final String info = "               �������ҵ���ǣ�\n"
				+ "               " + buss + "\n"
				+ "               Ԥ���:"+preFormNo + "\n"
				+ "           ʱ��:" + format.format(new Date())+"\n";
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					//byte[] byData = QRUtil.CreateQR(info, 200, 200);//��ǰ����ӡ��ά��
					service.setMode(true, true, false);
					service.printString(title, 5);
					service.setMode(false, false, false);
					
					service.printString(info, 10);
					try {
						//service.printImage( byData, 200, 200, 1, 10);
						service.printString(" \n \n \n \n \n");//��ӡ����bug: ���س����޷���ֽ����˲��ÿո�+�س�
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
