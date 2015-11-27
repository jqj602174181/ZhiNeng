package com.centerm.autofill.setting;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.centerm.autofill.setting.manage.MenuFragment;
//com.centerm.autofill.setting.SystemSettingActivity
public class SystemSettingActivity extends Activity implements MenuFragment.OnMenuSelectedListener, UpdateFragment.OnProcessEvent{
	
	private Fragment[] fragments;//��ҳ��
	private ImageButton btnBack;	//����
	private TextView tvCountDown;	//����ʱ	
	private long secondCountDown = 120;	//����ʱʣ������

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView( R.layout.activity_systemsetting);
		UpdateFragment.isStartQuery= true;
		super.onCreate(savedInstanceState);		
		
		//�����˵����Ӧ��fragment
		fragments = new Fragment[]{
			new NetworkFragment(),
//			new IdcardFragment(), 
			new UpdateFragment(),
			};
				
		//�����˵�
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add( R.id.MENU,  new MenuFragment());
		ft.add( R.id.CONTENT_CONTAINTER, fragments[0]);
		ft.commit();
		
		//ע�᷵�ذ�ť����Ӧ
		btnBack = (ImageButton)findViewById( R.id.BTN_BACK );
		btnBack.setOnClickListener( new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				//setResult(Activity.RESULT_OK);
				
				
				finish();				
			}
		});
		
		//����ʱ��ʱ��
		tvCountDown = (TextView)findViewById( R.id.LABEL_COUNTDOWN );		
	}
	
	//�˵�ѡ��ʱ������fragment�л�
	public void onMenuItemSelected( int position ){
		//ShareprefrencesUitls shareprefrencesUitls = new ShareprefrencesUitls(this,AutoFillSettingGlobalData.shareprefrencesPckage,AutoFillSettingGlobalData.shareperfrencesName);
		//shareprefrencesUitls.saveShareDataInt("key", 10);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace( R.id.CONTENT_CONTAINTER, fragments[position] );
		ft.commit();
	}
	
	@Override
	protected void onResume() {
		cdTimer.start();
		super.onResume();
	}

	@Override
	protected void onPause() {
		cdTimer.cancel();
		super.onPause();
	}

	//����ʱ����120�룬ÿ����һ��
	private CountDownTimer cdTimer = new CountDownTimerCanPause(secondCountDown);
	
	//������ʱ����ʱ����
	class CountDownTimerCanPause extends CountDownTimer{
		
		public CountDownTimerCanPause(long second) {
			super(second*1000, 1000);
		}

		public void onTick( long millisUntilFinished ) {
	   	 	long g = millisUntilFinished/1000;
	   	 	secondCountDown = g;
	   	 	tvCountDown.setText( String.valueOf( g ));
	    }
	
	    public void onFinish() {
	    	setResult(10);
	    	finish();
	    }
	}

	//����������ʱ����ʱ��ʱ
	@Override
	public void onStartProcessEvent() {
		cdTimer.cancel();		
	}

	@Override
	public void onEndProcessEvent() {
		cdTimer = new CountDownTimerCanPause(secondCountDown);//��ʱ��ʱ������
		cdTimer.start();		
	}

	@Override
	public void finish() {
		super.finish();
		//setResult(UtilGlobalData.iSettingFinishRequestCode);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.dispatchTouchEvent(ev);
		
	}

	
	
}
