package com.centerm.autofill.appframework.base;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.centerm.autofill.appframework.R;
import com.centerm.autofill.appframework.base.BaseScrollFragment.OnFinishListener;
import com.centerm.autofill.appframework.common.PreviewDialog;
import com.centerm.autofill.appframework.utils.JsonUtil;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BaseScrollActivity extends Activity implements OnFinishListener{

	//常量
	private final int REQUESTCODE_COMMIT = 999;		//提交数据至服务器的请求码

	public FragmentItem[] fragmentItems;// fragment对象列表
	protected int[] mBlock;// 控件列表 最多10个

	protected TextView titleLabel; // 用于显示标题文本框
	protected RelativeLayout topLayout;// 标题工具栏的layout
	protected ImageButton ibRet; // 用于返回的ImageButton
	protected Button complete; //完成按钮
	private TextView tvCountDown; // 倒计时

	protected int topBackgroudResourceId; // 标题的背景图ID

	protected String programName="";//应用程序的名称
	protected String trancode = null;
	protected String title = "";

	protected int ibRetId;

	private String magCardInfo; //IC或磁卡号

	protected void onCreate(Bundle savedInstanceState, int resPreviewId) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scroll);

		titleLabel = (TextView) findViewById(R.id.LABEL_Title);
		topLayout = (RelativeLayout) findViewById(R.id.HEADER);
		ibRet = (ImageButton) findViewById(R.id.BTN_RetBtn);

		mBlock = new int[] {R.id.BLOCK1, R.id.BLOCK2, R.id.BLOCK3, R.id.BLOCK4, R.id.BLOCK5, R.id.BLOCK6};

		tvCountDown = (TextView) findViewById(R.id.LABEL_COUNTDOWN);
		tvCountDown.setText( "" + TIMER_COUNT );

		// 有些业务没有凭单 ，如二代证联网核查
		if (resPreviewId > 0) {
			PreviewDialog dlg = new PreviewDialog();
			dlg.setImageId(resPreviewId);
			dlg.show(getFragmentManager(), "preview");
		} 

		initResource();
		titleLabel.setText(title);
		ibRet.setImageResource(ibRetId);
		topLayout.setBackgroundResource(topBackgroudResourceId);
		programName  = getApplicationName(this);

		initUI();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if( requestCode == REQUESTCODE_COMMIT){
			if(resultCode==-1){
				String error = data.getStringExtra("error");
				showTip(error);
			}else if(resultCode==0){
				showTip(getString(R.string.trancodeFinish));

				finish();
			}
			return;
		}
		//		int index = getmIndex();
		//		fragmentItems[index].fragment.onActivityResult(requestCode, resultCode, data);
	}

	protected boolean isCanComplete(){
		for (int i = 0; i < fragmentItems.length; i++) {
			BaseScrollFragment fm = (BaseScrollFragment)fragmentItems[i].fragment;
			if(!fm.validateForm()){
				return false;
			}
		}
		return true;
	}

	private void initUI() {
		complete = (Button) findViewById(R.id.BTN_COMPLETE);
		complete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//提交数据到服务端
				if(isCanComplete()){ //判断是否可提交
					complete();
				}
			}
		});
	}

	// 子类一般需要进行重载
	protected void initResource() {
		title = "立式填单机";
		ibRetId = R.drawable.retbtn;
		topBackgroudResourceId = R.drawable.topbar_bak;
	}

	public void onRetClick(View view) {
		this.finish();
	}

	/***********************代理人与交易人必须不同********************************/
	String uniqueMark = "";//唯一的标签
	public void setUniqueMark( String mark ){
		this.uniqueMark = mark;
	}
	public String getUniqueMark(){
		return uniqueMark;
	}

	protected void setTranCode( String code ){
		this.trancode = code;
	}


	public void complete() {
		TreeMap<String, String> map = new TreeMap<String, String>();
		for (int i = 0; i < fragmentItems.length; i++) {
			BaseScrollFragment fm = (BaseScrollFragment)fragmentItems[i].fragment;
			Map<String, String> fmmap = fm.getRecordMap();
			if (fmmap != null)
				map.putAll(fmmap);
		}

		startCommitActivity(map);
	}

	/******************* Fragment **************************/
	public class FragmentItem {  //扩展

		public Fragment fragment; // fragment
		public String title;
		public boolean visible; // 是否为可见

		public FragmentItem( Fragment fragment, String title, boolean visible) {
			this.fragment = fragment;
			this.title = title;
			this.visible = visible;
		}
	}

	// 是否是可见的fragment
	public boolean isVisibledFragment(Fragment fragment) {
		int i = getTabItembyFragment(fragment);
		if (i >= 0)
			return fragmentItems[i].visible;
		else
			return false;
	}

	// 根据fragment找到对象
	private int getTabItembyFragment(Fragment fragment) {
		for (int i = 0; i < fragmentItems.length; i++) {
			if (fragmentItems[i].fragment.equals(fragment)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 加载fragment
	 */
	protected void loadFragment() {
		for (int i = 0; i < fragmentItems.length && i < mBlock.length; i++) {
			FragmentItem temp = fragmentItems[i];
			int id = mBlock[i];
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(id, temp.fragment);
			ft.commit();
		}

		//把不需要的fragmentlayout隐藏
		int count = mBlock.length - fragmentItems.length;
		if (count > 0) {
			for (int i = fragmentItems.length; i < mBlock.length; i++) {
				findViewById(mBlock[i]).setVisibility(View.GONE);
			}
		}

		updateUI();
	}

	private void updateUI() {
		for (int i = 0; i < fragmentItems.length && i < mBlock.length; i++) {
			FragmentItem temp = fragmentItems[i];
			if(temp.visible){ //显示
				findViewById(mBlock[i]).setVisibility(View.VISIBLE);
			} else {
				findViewById(mBlock[i]).setVisibility(View.GONE);
			}
		}
	}

	// 显示或隐藏fragment
	public void enableFragment(String title, boolean isenable) {
		for (int i = 0; i < fragmentItems.length; i++) {
			if (fragmentItems[i].title.equals(title)) {
				fragmentItems[i].visible = isenable;
				updateUI();
				break;
			}
		}
	}

	private void startCommitActivity(TreeMap<String, String> map)
	{
		//测试打印
		final String name = "com.centerm.autofill.dev.commit.CommitActivity";
		final String pkg = "com.centerm.autofill.dev";			
		try {
			//启动应用程序
			if( trancode == null ){
				trancode = map.get("trancode");
				if(trancode == null){
					trancode = "";
				}
			}			

			ComponentName appComponent = new ComponentName(pkg, name);
			Intent intent = new Intent();				
			intent.setComponent(appComponent);

			intent.putExtra( "trandata", JsonUtil.getSortedJsonString(map) );
			intent.putExtra( "trancode", trancode);
			intent.putExtra( "formName", programName );
			startActivityForResult( intent, REQUESTCODE_COMMIT );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 显示错误提示信息
	public void showTip(String info) {
		Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onStart() {
		if( canResetTimer )
			mHandler.sendEmptyMessageDelayed( TIMER_DOWN, 1000 );
		else 
			cdTimer.start();
		super.onStart();
	}

	@Override
	protected void onStop() {
		if( canResetTimer )
			mHandler.removeMessages( TIMER_DOWN );
		else 
			cdTimer.cancel();

		super.onStop();
	}

	/*********************** fragment之间共享的信息 *****************************************/
	HashMap<String, Object> sharedResources;

	public void putsharedResource(String resid, Object res) {
		if (sharedResources == null)
			sharedResources = new HashMap<String, Object>();
		sharedResources.put(resid, res);
	}

	public Object getsharedResource(String resid) {
		if (sharedResources != null)
			return sharedResources.get(resid);
		return null;
	}

	/************************************ 倒计时 *********************************/
	private boolean canResetTimer = false; 
	protected void enableResetTimerbyTouch( boolean enable ){
		canResetTimer = enable;
	}
	// 倒计时，共180秒，每秒钟一次
	private CountDownTimer cdTimer = new CountDownTimer(TIMER_COUNT * 1000, 1000) {

		public void onTick(long millisUntilFinished) {
			long g = millisUntilFinished / 1000;
			tvCountDown.setText(String.valueOf(g));
		}

		public void onFinish() {
			finish();
		}
	};

	// 倒计时handler
	final static int TIMER_DOWN = 1;	//计时 
	final static int TIMER_COUNT = 300;	//60秒一次的倒计时
	int time_elapsed = TIMER_COUNT;		//剩余多少秒
	Handler mHandler  = new Handler() {
		public void handleMessage( android.os.Message msg) {
			switch( msg.what ){
			case TIMER_DOWN:
				tvCountDown.setText( String.valueOf(time_elapsed));
				time_elapsed--;
				//超时
				if( time_elapsed < 0 ){
					finish();
				}else{
					sendEmptyMessageDelayed( TIMER_DOWN, 1000 );
				}
				break;
			}
		};
	};	

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		if( action == MotionEvent.ACTION_DOWN
				|| action == MotionEvent.ACTION_POINTER_DOWN ){//每次有操作时，都重置倒计时
			time_elapsed = TIMER_COUNT;
		}
		return super.dispatchTouchEvent(ev);
	}

	//	@Override
	//	public boolean dispatchKeyEvent(KeyEvent event) {
	//		//解决实体按键按下时，number类型的edittext控件会产生两个字符的问题
	//		int action = event.getAction();
	//		int code = event.getKeyCode();
	//		if( action == KeyEvent.ACTION_DOWN && code >= KeyEvent.KEYCODE_0 
	//				&& code <= KeyEvent.KEYCODE_9)
	//			return true;
	//
	//		return super.dispatchKeyEvent(event);
	//	}

	public static  String getApplicationName(Context context) { 
		PackageManager packageManager = null; 
		ApplicationInfo applicationInfo = null; 
		try { 
			packageManager = context.getApplicationContext().getPackageManager(); 
			applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0); 
		} catch (PackageManager.NameNotFoundException e) { 
			applicationInfo = null; 
		} 
		String applicationName =  
				(String) packageManager.getApplicationLabel(applicationInfo); 
		return applicationName; 
	}

	@Override
	public void onFinishListener(Fragment fragment) {
		Fragment mFragment = fragmentItems[0].fragment;
		if(fragment.equals(mFragment)){ //假如是第一个fragment,就finish
			finish();
		}
	}

}
