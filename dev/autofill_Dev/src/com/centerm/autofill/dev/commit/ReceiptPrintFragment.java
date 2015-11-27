package com.centerm.autofill.dev.commit;

import com.centerm.autofill.dev.R;
import com.centerm.autofill.dev.printer.receipt.Formater;
import com.centerm.common.HalComService;
import com.centerm.util.financial.PeriDeviceService;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

//外置票据打印
public class ReceiptPrintFragment extends Fragment {
	private Formater.PrintPages pages;			//需要打印的页
	private View viewFront;						//正面打印
	private View viewTail;						//反面打印
	private View viewOK;						//确定面板
	private boolean canExit = false;			//是否能退出
	private boolean frontPrinted = false;		//正面已被打印
	private boolean tailPrinted = false;		//反面已被打印
	private volatile boolean isPrinting = false;		//正在打印

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate( R.layout.fragment_receiptprint, container, false );
		viewFront = view.findViewById( R.id.BLOCK_PRINTFONT );
		viewTail = view.findViewById( R.id.BLOCK_PRINTTAIL );
		viewOK = view.findViewById( R.id.BLOCK_PRINTOK );

		viewOK.setAlpha( (float)0.50 );//提示当前不可用

		//处理按下事件
		viewFront.setOnTouchListener( onTouchListener );		
		viewOK.setOnTouchListener( onTouchListener );

		//如果有正反面，就显示反面打印提示
		if( pages.getPageCount() == 2){
			viewTail.setVisibility( View.VISIBLE );
			viewTail.setOnTouchListener( onTouchListener );
		}else{
			tailPrinted = true;//无需打印反面
		}
		return view;
	}

	public void setPrintPages( Formater.PrintPages pages ){
		this.pages = pages;
	}

	//响应按下时整个区块变灰
	private OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View view, MotionEvent event ) {
			//确定按钮只有当canExit为true时可以变化
			if( view.getId() == R.id.BLOCK_PRINTOK && !canExit ){
				return true;
			}
			//打印时视图不可点按
			if( isPrinting ){
				return true;
			}
			switch( event.getAction() ){
			case MotionEvent.ACTION_DOWN:
				view.setAlpha( (float)0.7 );
				break;
			case MotionEvent.ACTION_UP:				
				view.setAlpha( (float)1.0 );
				onViewClick( view );
				break;
			}
			return true;
		}
	};

	//按下事件
	private void onViewClick( View view ) {
		int id = view.getId();
		switch( id ){
		case R.id.BLOCK_PRINTFONT://打印正面
			printReceipt( pages.getPage( 0 ) );
			frontPrinted = true;
			getView().findViewById( R.id.IMG_PRINT_ENDOK ).setVisibility( View.VISIBLE );
			break;
		case R.id.BLOCK_PRINTTAIL://打印反面
			printReceipt( pages.getPage( 1 ) );
			tailPrinted = true;
			getView().findViewById( R.id.IMG_PRINTTAIL_ENDOK ).setVisibility( View.VISIBLE );
			break;
		case R.id.BLOCK_PRINTOK://正确退出
			if( canExit ){
				CommitActivity activity = (CommitActivity)getActivity();
				activity.nextStep();
			}
			break;
		}

		//都打印过了，就可以响应退出按钮
		if( frontPrinted && tailPrinted && !canExit ){
			canExit = true;
			viewOK.setAlpha( (float)1.0 );
		}
	}

	//执行打印
	private void printReceipt( final byte[] data){
		Thread printThread = new Thread(){
			@Override
			public void run() {
				isPrinting = true;
				try {
					//					HalComService comService = new HalComService();
					//					int fd = comService.openCom( 1 );
					//					comService.writeCom( fd, data, data.length );
					//					comService.closeCom( fd );
					//调用立式打印单据
					PeriDeviceService.getInstance().PrintInfo(5, 9600, 20, data.length, data);
					Thread.sleep(1000);
					PeriDeviceService.getInstance().PopPaper(5, 9600, 20);

				} catch (Exception e) {
					e.printStackTrace();
				}
				isPrinting = false;
			}
		};
		printThread.start();
	}

}
