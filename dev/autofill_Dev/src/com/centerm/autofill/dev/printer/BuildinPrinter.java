package com.centerm.autofill.dev.printer;

import com.centerm.util.printer.PrinterService;


//���õĴ�ӡ��
public class BuildinPrinter {
	
	//��ӡ����
	public interface IPrintComplete{
		public void onPrintComplete( boolean isSuccess );
	}

	//ʵ�ֳɵ���
	private static BuildinPrinter instance;
	private PrinterService service = PrinterService.getService();	
	private BuildinPrinter(){
	}
	
	public static BuildinPrinter getInstance() {
		if( instance == null ) {
			instance = new BuildinPrinter();
		}
		return instance;
	}
	
	//������ӡ����
	private IPrintComplete comleteListener;
	public void setPrintCompleteListener( IPrintComplete listener ){
		comleteListener = listener;
	}
	private void onPrintComplete( boolean isSuccess ){
		if( comleteListener != null )
			comleteListener.onPrintComplete( isSuccess );
	}
	
	//�����Ƿ�ȱֽ
	public boolean hasPaper(){
		boolean is = false;
		try {
			 is = service.hasPaper();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return is;
	}
	/**
	 * ��ӡƾ���ַ���
	 * @param tranName	��������
	 * @param formNo	Ԥ���
	 * @param createdTime ����ʱ�� 
	 * @param strLogo �ͻ�����
	 */
	public void print( String tranName, String formNo, String createdTime, String strLogo){
		//���ɱ��������
		final String title = " \n     " + strLogo +"\n";//ǰ���һ��\n��ֹ��һ������
		final String tranNLable = "                �������ҵ���ǣ�\n";
		String space = "";
		for(int n = (24 - tranName.length()*2) / 2; n>0; n--){		// û������ʱ��һ�д�ӡ48��Ӣ�Ļ�24������
			space += " ";
		}
		final String tranN = space + tranName + "\n";
		final String formN = "    Ԥ���: "+formNo + "\n";
		final String time = "            ʱ��:" + createdTime +"\n";
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					//TODO:����ע�͵�
					//String content = title + info + " \n \n \n \n \n";
					//service.printString( content, 5);
					service.setMode(true, true, false);
					service.printString( title, 5 );
					service.setMode(false, false, false);
					service.printString( tranNLable );
					service.setMode(true, true, false);
					service.printString( tranN );
					service.printString( formN );
					service.setMode(false, false, false);
					service.printString( time, 10 );
					service.printString(" \n \n \n \n \n");//��ӡ����bug: ���س����޷���ֽ����˲��ÿո�+�س�
					
					Thread.sleep( 1000 );//�ȴ���ӡ��������ʾ��ӡ����					
					onPrintComplete( true );
				} catch (Exception e) {
					e.printStackTrace();
					onPrintComplete(false);//�������쳣ҲҪ�������
				}
			}
		}).start();
	}	
	
//	/**
//	 * ��ӡƾ���ַ���
//	 * @param tranName	��������
//	 * @param formNo	Ԥ���
//	 * @param createdTime ����ʱ�� 
//	 * @param strLogo �ͻ�����
//	 */
//	public void print( String tranName, String formNo, String createdTime, String strLogo){
//		//���ɱ��������
//		final String title = " \n     " + strLogo +"\n";//ǰ���һ��\n��ֹ��һ������
//		final String info = "               �������ҵ���ǣ�\n"
//				+ "               " + tranName + "\n"
//				+ "               Ԥ���: "+formNo + "\n"
//				+ "           ʱ��:" + createdTime +"\n";
//		
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					//TODO:����ע�͵�
//					//String content = title + info + " \n \n \n \n \n";
//					//service.printString( content, 5);
//					service.setMode(true, true, false);
//					service.printString( title, 5);
//					service.setMode(false, false, false);
//					
//					service.printString( info, 10);
//					service.printString(" \n \n \n \n \n");//��ӡ����bug: ���س����޷���ֽ����˲��ÿո�+�س�
//					
//					Thread.sleep( 1000 );//�ȴ���ӡ��������ʾ��ӡ����					
//					onPrintComplete( true );
//				} catch (Exception e) {
//					e.printStackTrace();
//					onPrintComplete(false);//�������쳣ҲҪ�������
//				}
//			}
//		}).start();
//	}	
}
