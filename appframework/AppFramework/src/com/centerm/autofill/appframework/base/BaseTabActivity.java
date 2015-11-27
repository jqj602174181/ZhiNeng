package com.centerm.autofill.appframework.base;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.autofill.appframework.R;
import com.centerm.autofill.appframework.base.BaseTabFragment.OnPageSwitchListener;
import com.centerm.autofill.appframework.common.PreviewDialog;
import com.centerm.autofill.appframework.listener.PreviewListener;
import com.centerm.autofill.appframework.utils.JsonUtil;

/**
 * ����ҵ��Ӧ�ü̳еĸ��࣬���ṩ�ˣ� (1)ƾ��Ԥ������
 * 
 */
public class BaseTabActivity extends Activity implements PreviewListener,
OnPageSwitchListener {
	//����
	private final int REQUESTCODE_COMMIT = 999;		//�ύ��������������������

	// �ؼ��б�
	final static int MAX_TABCOUNT = 10; // �����ٸ�tab
	protected TextView[] tvTabs;// ��ǩ�ؼ��б�
	protected TabItem[] tabs;// ��ǩ�б����
	private TextView tvCountDown; // ����ʱ

	protected RelativeLayout titleLayout;// ���⹤������layout
	protected ImageView titleImageView;// ������ʾ����logo��ImageView
	protected ImageButton ibRet;// ���ڷ��ص�ImageButton

	// ��ͬ�ؼ�����ԴID
	protected int activedColor; // tabѡ��ʱ�������ɫ
	protected int normalColor; // tabδѡ��ʱ�������ɫ
	protected int drawableId_tabLeftActived; // ���󷽡�����״̬��tab�ı���ͼ
	protected int drawableId_tabLeftNormal; // ���󷽡��Ǽ���״̬�µ�tab�ı���ͼ
	protected int drawableId_tabRightActived; // ���ҷ�������״̬�µ�tab�ı���ͼ
	protected int drawableId_tabRightNormal; // ���ҷ����Ǽ���״̬�µ�tab�ı���ͼ
	protected int drawableId_tabCenterActived; // �м䡢����״̬�µ�tab�ı���ͼ
	protected int drawableId_tabCenterNormal; // �м䡢�Ǽ���״̬�µ�tab�ı���ͼ
	protected int titleBackgroudResourceId; // ����������ı���ͼID
	protected int titleLogoId; // �����������logoID
	protected int ibRetId;

	protected String serial = "dev_1";
	protected String business = "1001";
	protected String outlets = "1002";

	protected String programName="";//Ӧ�ó��������

	protected String trancode = null;


	/**
	 * ��ʼ������Ԫ��
	 * 
	 * @param savedInstanceState
	 * @param title
	 *            ����
	 * @param resPreviewId
	 *            Ԥ��ͼƬ��ID
	 */
	protected void onCreate(Bundle savedInstanceState, int resPreviewId) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// ȫ��
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_basetab);

		titleImageView = (ImageView) findViewById(R.id.AppFrameworkTitle_ImageView);
		titleLayout = (RelativeLayout) findViewById(R.id.TopView);
		ibRet = (ImageButton) findViewById(R.id.BTN_RetBtn);

		// ��ÿؼ�
		TextView tvTab1 = (TextView) findViewById(R.id.tab1);
		TextView tvTab2 = (TextView) findViewById(R.id.tab2);
		TextView tvTab3 = (TextView) findViewById(R.id.tab3);
		TextView tvTab4 = (TextView) findViewById(R.id.tab4);
		TextView tvTab5 = (TextView) findViewById(R.id.tab5);
		TextView tvTab6 = (TextView) findViewById(R.id.tab6);
		TextView tvTab7 = (TextView) findViewById(R.id.tab7);
		TextView tvTab8 = (TextView) findViewById(R.id.tab8);
		TextView tvTab9 = (TextView) findViewById(R.id.tab9);
		TextView tvTab10 = (TextView) findViewById(R.id.tab10);

		tvTabs = new TextView[] { tvTab1, tvTab2, tvTab3, tvTab4, tvTab5,
				tvTab6, tvTab7, tvTab8, tvTab9, tvTab10 };
		tvCountDown = (TextView) findViewById(R.id.LABEL_COUNTDOWN);
		tvCountDown.setText( "" + TIMER_COUNT );

		// ��Щҵ��û��ƾ�� �������֤�����˲�
		if (resPreviewId > 0) {
			PreviewDialog dlg = new PreviewDialog();
			dlg.setImageId(resPreviewId);
			dlg.show(getFragmentManager(), "preview");
		}

		// ���ؽ������ԴƤ������
		initTabResource();
		titleLayout.setBackgroundResource(titleBackgroudResourceId);
		titleImageView.setImageResource(titleLogoId);
		ibRet.setImageResource(ibRetId);
		programName  = getApplicationName(this);

	}

	// ��ʼ��һЩTab�ı�����������ɫ�����Ը��ݲ�ͬ���������д
	// ����һ����Ҫ��������
	protected void initTabResource() {
		Resources resources = getResources();
		activedColor = resources.getColor(R.color.framework_text_green);
		normalColor = resources.getColor(R.color.framework_lightwhite);
		drawableId_tabLeftActived = R.drawable.tab1;
		drawableId_tabLeftNormal = R.drawable.tab11;
		drawableId_tabRightNormal = R.drawable.tab2;
		drawableId_tabRightActived = R.drawable.tab21;
		drawableId_tabCenterActived = R.drawable.tab3;
		drawableId_tabCenterNormal = R.drawable.tab33;
		titleBackgroudResourceId = R.drawable.framework_title_bk;
		titleLogoId = R.drawable.yclogo;
		ibRetId = R.drawable.retbtn;
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

	/******************* Tab��ǩ��� **************************/
	// tab�Ŀؼ�������
	public class TabItem {
		public String title;// ����
		public boolean visible;// �Ƿ�Ϊ�ɼ�
		public boolean actived;// �Ƿ��ڼ���״̬
		public Fragment fragment;// fragment

		public TabItem(String title, boolean visible, boolean actived,
				Fragment fragment) {
			this.title = title;
			this.visible = visible;
			this.actived = actived;
			this.fragment = fragment;
		}
	}

	/**
	 * ��ʼ��tab��ǩ����
	 */
	protected void initTabsAttr() {
		TextView view;
		// this.tabs = tabs;

		// ��һ��������
		for (int i = 0; i < tabs.length && i < tvTabs.length; i++) {
			TabItem conf = tabs[i];
			view = tvTabs[i];
			view.setText(conf.title);
			if (conf.visible) {
				view.setVisibility(View.VISIBLE);
			} else {
				view.setVisibility(View.GONE);
			}
		}

		// �Ѳ���Ҫ��tab����
		int count = tvTabs.length - tabs.length;
		if (count > 0) {
			for (int i = tabs.length; i < tvTabs.length; i++) {
				tvTabs[i].setVisibility(View.GONE);
			}
		}

		updateTabUI();// ������ʾ
	}

	// ������ʾ��tab����
	private int getTabShownCount() {
		// ��һ��������
		int showcnt = 0;// ��ʾ��tab����
		for (int i = 0; i < tabs.length && i < tvTabs.length; i++) {
			TabItem conf = tabs[i];
			if (conf.visible) {
				showcnt += 1;
			}
		}
		return showcnt;
	}

	// ���ر������ҳ������
	private int getActivedTabIndex() {
		for (int i = 0; i < tabs.length; i++) {
			TabItem conf = tabs[i];
			if (conf.visible && conf.actived) {
				return i;
			}
		}
		return 0;// Ĭ�Ϸ��ص�0��
	}

	// �Ƿ���ǰһҳ
	private boolean hasPreTab(int index) {
		if (index >= 0 && index < tabs.length) {
			for (int i = 0; i < index; i++) {
				if (tabs[i].visible)
					return true;
			}
		}
		return false;
	}

	// �Ƿ�����һҳ
	private boolean hasNextTab(int index) {
		if (index >= 0 && index < tabs.length) {
			for (int i = index + 1; i < tabs.length; i++) {
				if (tabs[i].visible)
					return true;
			}
		}
		return false;
	}

	// ����fragment�ҵ�TabItem����
	private int getTabItembyFragment(Fragment fragment) {
		for (int i = 0; i < tabs.length; i++) {
			if (tabs[i].fragment.equals(fragment)) {
				return i;
			}
		}
		return -1;
	}

	// ��ѯ�Ƿ���ǰһҳ
	public boolean hasPreTab(Fragment fragment) {
		int i = getTabItembyFragment(fragment);
		if (i >= 0)
			return hasPreTab(i);
		else
			return false;
	}

	// ��ѯ�Ƿ��к�һҳ
	public boolean hasNextTab(Fragment fragment) {
		int i = getTabItembyFragment(fragment);
		if (i >= 0)
			return hasNextTab(i);
		else
			return false;
	}

	// �Ƿ��ǿɼ���tab
	public boolean isVisibledTab(Fragment fragment) {
		int i = getTabItembyFragment(fragment);
		if (i >= 0)
			return tabs[i].visible;
		else
			return false;
	}

	// ����tabUI
	private void updateTabUI() {
		TextView[] tvs = new TextView[MAX_TABCOUNT]; // ���tab����ؼ�����
		TabItem[] tabItems = new TabItem[MAX_TABCOUNT]; // ���TabItem����
		int showcnt = getTabShownCount();// �����ʾ��tab����
		int i, j;

		// �����õ����ӵ���ͼ
		for (i = 0; i < tabs.length; i++) {
			if (tabs[i].visible) {
				for (j = 0; j < tvs.length; j++) {
					if (tvs[j] == null) {
						tvs[j] = tvTabs[i];
						break;
					}
				}
				for (j = 0; j < tabItems.length; j++) {
					if (tabItems[j] == null) {
						tabItems[j] = tabs[i];
						break;
					}
				}
			}
		}

		// �����õ������ӵ���ͼ
		for (i = 0; i < tabs.length; i++) {
			if (!tabs[i].visible) {
				for (j = 0; j < tvs.length; j++) {
					if (tvs[j] == null) {
						tvs[j] = tvTabs[i];
						break;
					}
				}
			}
		}

		// ���������У��ٸ��ݸ�����һ��ʾ.
		// tv1����Ҫ��ʾ��
		for (i = 1; i < tvs.length; i++) {
			if (tvs[i] != null)
				tvs[i].setVisibility(View.GONE);
		}

		// ������ʾ��tab�������ñ������ı���ɫ
		TextView tab;
		if (showcnt == 1) {// ֻ��һ��tabʱ���⴦��
			tab = tvs[0];
			tab.setBackgroundResource(R.drawable.title);
			tab.setTextColor(activedColor);
			tab.setVisibility(View.VISIBLE);
		} else {// ���ж��
			for (i = 0; i < showcnt; i++) {
				tab = tvs[i];
				boolean actived = tabItems[i].actived;

				// ����Ϊ��ʾ������ѡ��״̬�����ı���ɫ
				tab.setVisibility(View.VISIBLE);
				if (actived) {
					tab.setTextColor(activedColor);
				} else {
					tab.setTextColor(normalColor);
				}

				if (i == 0) {// ����ߵ�tab
					if (actived)
						tab.setBackgroundResource(drawableId_tabLeftActived);
					else
						tab.setBackgroundResource(drawableId_tabLeftNormal);
				} else if (i == showcnt - 1) {// ���ұߵ�tab
					if (actived)
						tab.setBackgroundResource(drawableId_tabRightActived);
					else
						tab.setBackgroundResource(drawableId_tabRightNormal);
				} else {// �м�
					if (actived)
						tab.setBackgroundResource(drawableId_tabCenterActived);
					else
						tab.setBackgroundResource(drawableId_tabCenterNormal);
				}
			}// end of for
		}// end of else
	}

	// �е���nҳ
	private void switchToTab(int from, int to) {
		if (tabs != null && tabs.length > to) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.TABCONTENT, tabs[to].fragment);
			ft.commit();

			// �л�״̬������״̬��ʾ
			tabs[from].actived = false;
			tabs[to].actived = true;
			updateTabUI();

		}
	}

	// ��ʾ�����ض�Ӧ��ҳ
	public void enableTab(String title, boolean isenable) {
		for (int i = 0; i < tabs.length; i++) {
			if (tabs[i].title.equals(title)) {
				tabs[i].visible = isenable;
				updateTabUI();// ���¶�����tab��ǩ
				updateActivedPageButtonStatus();// ���µ�����ť״̬
				break;
			}
		}
	}

	private void updateActivedPageButtonStatus() {
		// ������Щ��ť��Ҫ��ʾ������
		Button btnPre = (Button) findViewById(R.id.BTN_PRE);
		Button btnNext = (Button) findViewById(R.id.BTN_NEXT);
		Button btnComplete = (Button) findViewById(R.id.BTN_COMPLETE);
		int n = getActivedTabIndex();

		// ������һҳ
		if (btnPre != null) {
			if (hasPreTab(n)) {
				btnPre.setVisibility(View.VISIBLE);
			} else {
				btnPre.setVisibility(View.GONE);
			}
		}

		// ������һҳ����ɰ�ť
		if (btnNext != null && btnComplete != null) {
			if (hasNextTab(n)) {
				btnNext.setVisibility(View.VISIBLE);
				btnComplete.setVisibility(View.GONE);
			} else {
				btnNext.setVisibility(View.GONE);
				btnComplete.setVisibility(View.VISIBLE);
			}
		}
	}

	/************ ҳ�л�listener ***************/
	@Override
	public void onNextStep() {
		int n = getActivedTabIndex();// �����ҳ
		if (tabs.length > n + 1) {// ������һҳ
			for (int i = n + 1; i < tabs.length; i++) {
				TabItem conf = tabs[i];
				if (conf.visible) {// ����ҳ���л�
					switchToTab(n, i);
					break;
				}
			}
		}
	}

	@Override
	public void onPreviewStep() {
		int n = getActivedTabIndex();// �����ҳ
		if (n - 1 >= 0) {// ������ҳ
			for (int i = n - 1; i >= 0; i--) {
				TabItem conf = tabs[i];
				if (conf.visible) {// ����ҳ���л�
					switchToTab(n, i);
					break;
				}
			}
		} else {// ��һҳ��ǰ�У���ʾҪ�˳�
			finish();
		}
	}

	@Override
	public void onComplete() {

		TreeMap<String, String> map = new TreeMap<String, String>();
		for (int i = 0; i < tabs.length; i++) {
			BaseTabFragment fm = (BaseTabFragment) tabs[i].fragment;
			Map<String, String> fmmap = fm.getRecordMap();
			if (fmmap != null)
				map.putAll(fmmap);
		}


		startCommitActivity(map);
	}

	// ��ʾ�����ҳ,һ������previewҳ��ʱ����
	public void showActivedTab() {
		switchToTab(0, getActivedTabIndex());
	}

	/***********************�������뽻���˱��벻ͬ********************************/
	String uniqueMark = "";//Ψһ�ı�ǩ
	public void setUniqueMark( String mark ){
		this.uniqueMark = mark;
	}
	public String getUniqueMark(){
		return uniqueMark;
	}

	// ���ô�ӡ����
	private String printtitle = "";

	protected void setPrintTitle(String title) {
		this.printtitle = title;
	}

	// ��ʾ������ʾ��Ϣ
	public void showTip(String info) {
		Toast.makeText(this, info, Toast.LENGTH_LONG).show();
	}

	/******************* Ԥ��ʾ��ͼƬ��� **************************/
	// ��Ԥ������ʱ��ʾ����
	@Override
	public void onPreviewComplete() {
		// ��ʾĬ�ϵ�fragment
		switchToTab(0, getActivedTabIndex());
	}

	/*********************** �������뽻���˱��벻ͬ ********************************/
	// TODO:����ɾ��������ܣ����ù�����Ϣ�ķ���
	// String uniqueMark = "";//Ψһ�ı�ǩ
	// public void setUniqueMark( String mark ){
	// this.uniqueMark = mark;
	// }
	// public String getUniqueMark(){
	// return uniqueMark;
	// }

	/*********************** fragment֮�乲�����Ϣ *****************************************/
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

	/*********** ���˲��� *******************/
	@Override
	public void onBackPressed() {
		// �ص�ǰһҳ�����һ���˳�
		onPreviewStep();
	}

	// �����Ͻ��˳���ť����ʱ
	public void onRetClick(View view) {
		onPreviewStep();//ֻ������һҳ����Ҫ�˳�
	}

	/****************** ����ʱ ***************/
	private boolean canResetTimer = false; 
	protected void enableResetTimerbyTouch( boolean enable ){
		canResetTimer = enable;
	}
	// ����ʱ����180�룬ÿ����һ��
	private CountDownTimer cdTimer = new CountDownTimer(TIMER_COUNT * 1000, 1000) {

		public void onTick(long millisUntilFinished) {
			long g = millisUntilFinished / 1000;
			tvCountDown.setText(String.valueOf(g));
		}

		public void onFinish() {
			finish();
		}
	};

	// ����ʱhandler
	final static int TIMER_DOWN = 1;	//��ʱ 
	final static int TIMER_COUNT = 300;	//60��һ�εĵ���ʱ
	int time_elapsed = TIMER_COUNT;		//ʣ�������
	Handler mHandler  = new Handler() {
		public void handleMessage( android.os.Message msg) {
			switch( msg.what ){
			case TIMER_DOWN:
				tvCountDown.setText( String.valueOf(time_elapsed));
				time_elapsed--;

				//��ʱ
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
				|| action == MotionEvent.ACTION_POINTER_DOWN ){//ÿ���в���ʱ�������õ���ʱ
			time_elapsed = TIMER_COUNT;
		}
		return super.dispatchTouchEvent(ev);
	}


	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		//���ʵ�尴������ʱ��number���͵�edittext�ؼ�����������ַ�������
		int action = event.getAction();
		int code = event.getKeyCode();
		if( action == KeyEvent.ACTION_DOWN && code >= KeyEvent.KEYCODE_0 
				&& code <= KeyEvent.KEYCODE_9)
			return true;

		return super.dispatchKeyEvent(event);
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
		int index = getActivedTabIndex();
		tabs[index].fragment.onActivityResult(requestCode, resultCode, data);
	}

	//���ý����롣��ǰӦ�ô������46xxx#yy��ʽ����������#��ǣ�����˵�ǰ���ϴ��ָ�ʽ
	protected void setTranCode( String code ){
		this.trancode = code;
	}
	/*
	 * ������ӡ����ص�Activity,������Ӧ������
	 */
	private void startCommitActivity(TreeMap<String, String> map)
	{
		//���Դ�ӡ
		final String name = "com.centerm.autofill.dev.commit.CommitActivity";
		final String pkg = "com.centerm.autofill.dev";			
		try {
			//����Ӧ�ó���
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

}
