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

//����Ʊ�ݴ�ӡ
public class ReceiptPrintFragment extends Fragment {
	private Formater.PrintPages pages;			//��Ҫ��ӡ��ҳ
	private View viewFront;						//�����ӡ
	private View viewTail;						//�����ӡ
	private View viewOK;						//ȷ�����
	private boolean canExit = false;			//�Ƿ����˳�
	private boolean frontPrinted = false;		//�����ѱ���ӡ
	private boolean tailPrinted = false;		//�����ѱ���ӡ
	private volatile boolean isPrinting = false;		//���ڴ�ӡ

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate( R.layout.fragment_receiptprint, container, false );
		viewFront = view.findViewById( R.id.BLOCK_PRINTFONT );
		viewTail = view.findViewById( R.id.BLOCK_PRINTTAIL );
		viewOK = view.findViewById( R.id.BLOCK_PRINTOK );

		viewOK.setAlpha( (float)0.50 );//��ʾ��ǰ������

		//�������¼�
		viewFront.setOnTouchListener( onTouchListener );		
		viewOK.setOnTouchListener( onTouchListener );

		//����������棬����ʾ�����ӡ��ʾ
		if( pages.getPageCount() == 2){
			viewTail.setVisibility( View.VISIBLE );
			viewTail.setOnTouchListener( onTouchListener );
		}else{
			tailPrinted = true;//�����ӡ����
		}
		return view;
	}

	public void setPrintPages( Formater.PrintPages pages ){
		this.pages = pages;
	}

	//��Ӧ����ʱ����������
	private OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View view, MotionEvent event ) {
			//ȷ����ťֻ�е�canExitΪtrueʱ���Ա仯
			if( view.getId() == R.id.BLOCK_PRINTOK && !canExit ){
				return true;
			}
			//��ӡʱ��ͼ���ɵ㰴
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

	//�����¼�
	private void onViewClick( View view ) {
		int id = view.getId();
		switch( id ){
		case R.id.BLOCK_PRINTFONT://��ӡ����
			printReceipt( pages.getPage( 0 ) );
			frontPrinted = true;
			getView().findViewById( R.id.IMG_PRINT_ENDOK ).setVisibility( View.VISIBLE );
			break;
		case R.id.BLOCK_PRINTTAIL://��ӡ����
			printReceipt( pages.getPage( 1 ) );
			tailPrinted = true;
			getView().findViewById( R.id.IMG_PRINTTAIL_ENDOK ).setVisibility( View.VISIBLE );
			break;
		case R.id.BLOCK_PRINTOK://��ȷ�˳�
			if( canExit ){
				CommitActivity activity = (CommitActivity)getActivity();
				activity.nextStep();
			}
			break;
		}

		//����ӡ���ˣ��Ϳ�����Ӧ�˳���ť
		if( frontPrinted && tailPrinted && !canExit ){
			canExit = true;
			viewOK.setAlpha( (float)1.0 );
		}
	}

	//ִ�д�ӡ
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
					//������ʽ��ӡ����
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
