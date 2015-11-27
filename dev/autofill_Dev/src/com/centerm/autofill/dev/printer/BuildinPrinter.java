package com.centerm.autofill.dev.printer;

import com.centerm.util.printer.PrinterService;


//内置的打印机
public class BuildinPrinter {
	
	//打印结束
	public interface IPrintComplete{
		public void onPrintComplete( boolean isSuccess );
	}

	//实现成单例
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
	
	//监听打印结束
	private IPrintComplete comleteListener;
	public void setPrintCompleteListener( IPrintComplete listener ){
		comleteListener = listener;
	}
	private void onPrintComplete( boolean isSuccess ){
		if( comleteListener != null )
			comleteListener.onPrintComplete( isSuccess );
	}
	
	//返回是否缺纸
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
	 * 打印凭条字符串
	 * @param tranName	交易名称
	 * @param formNo	预填单号
	 * @param createdTime 创建时间 
	 * @param strLogo 客户名称
	 */
	public void print( String tranName, String formNo, String createdTime, String strLogo){
		//生成标题和内容
		final String title = " \n     " + strLogo +"\n";//前面加一个\n防止第一行缩行
		final String tranNLable = "                您办理的业务是：\n";
		String space = "";
		for(int n = (24 - tranName.length()*2) / 2; n>0; n--){		// 没倍宽倍高时，一行打印48个英文或24个中文
			space += " ";
		}
		final String tranN = space + tranName + "\n";
		final String formN = "    预填单号: "+formNo + "\n";
		final String time = "            时间:" + createdTime +"\n";
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					//TODO:测试注释掉
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
					service.printString(" \n \n \n \n \n");//打印机有bug: 纯回车，无法走纸，因此采用空格+回车
					
					Thread.sleep( 1000 );//等待打印结束，提示打印结束					
					onPrintComplete( true );
				} catch (Exception e) {
					e.printStackTrace();
					onPrintComplete(false);//发生了异常也要提醒完成
				}
			}
		}).start();
	}	
	
//	/**
//	 * 打印凭条字符串
//	 * @param tranName	交易名称
//	 * @param formNo	预填单号
//	 * @param createdTime 创建时间 
//	 * @param strLogo 客户名称
//	 */
//	public void print( String tranName, String formNo, String createdTime, String strLogo){
//		//生成标题和内容
//		final String title = " \n     " + strLogo +"\n";//前面加一个\n防止第一行缩行
//		final String info = "               您办理的业务是：\n"
//				+ "               " + tranName + "\n"
//				+ "               预填单号: "+formNo + "\n"
//				+ "           时间:" + createdTime +"\n";
//		
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					//TODO:测试注释掉
//					//String content = title + info + " \n \n \n \n \n";
//					//service.printString( content, 5);
//					service.setMode(true, true, false);
//					service.printString( title, 5);
//					service.setMode(false, false, false);
//					
//					service.printString( info, 10);
//					service.printString(" \n \n \n \n \n");//打印机有bug: 纯回车，无法走纸，因此采用空格+回车
//					
//					Thread.sleep( 1000 );//等待打印结束，提示打印结束					
//					onPrintComplete( true );
//				} catch (Exception e) {
//					e.printStackTrace();
//					onPrintComplete(false);//发生了异常也要提醒完成
//				}
//			}
//		}).start();
//	}	
}
