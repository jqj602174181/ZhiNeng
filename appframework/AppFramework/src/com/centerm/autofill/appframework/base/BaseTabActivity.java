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
 * 各个业务应该继承的父类，它提供了： (1)凭单预览功能
 * 
 */
public class BaseTabActivity extends Activity implements PreviewListener,
OnPageSwitchListener {
	//常量
	private final int REQUESTCODE_COMMIT = 999;		//提交数据至服务器的请求码

	// 控件列表
	final static int MAX_TABCOUNT = 10; // 最多多少个tab
	protected TextView[] tvTabs;// 标签控件列表
	protected TabItem[] tabs;// 标签列表对象
	private TextView tvCountDown; // 倒计时

	protected RelativeLayout titleLayout;// 标题工具栏的layout
	protected ImageView titleImageView;// 用于显示标题logo的ImageView
	protected ImageButton ibRet;// 用于返回的ImageButton

	// 不同控件的资源ID
	protected int activedColor; // tab选中时字体的颜色
	protected int normalColor; // tab未选中时字体的颜色
	protected int drawableId_tabLeftActived; // 最左方、激活状态下tab的背景图
	protected int drawableId_tabLeftNormal; // 最左方、非激活状态下的tab的背景图
	protected int drawableId_tabRightActived; // 最右方、激活状态下的tab的背景图
	protected int drawableId_tabRightNormal; // 最右方、非激活状态下的tab的背景图
	protected int drawableId_tabCenterActived; // 中间、激活状态下的tab的背景图
	protected int drawableId_tabCenterNormal; // 中间、非激活状态下的tab的背景图
	protected int titleBackgroudResourceId; // 标题控制栏的背景图ID
	protected int titleLogoId; // 标题控制栏的logoID
	protected int ibRetId;

	protected String serial = "dev_1";
	protected String business = "1001";
	protected String outlets = "1002";

	protected String programName="";//应用程序的名称

	protected String trancode = null;


	/**
	 * 初始化界面元素
	 * 
	 * @param savedInstanceState
	 * @param title
	 *            标题
	 * @param resPreviewId
	 *            预览图片的ID
	 */
	protected void onCreate(Bundle savedInstanceState, int resPreviewId) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_basetab);

		titleImageView = (ImageView) findViewById(R.id.AppFrameworkTitle_ImageView);
		titleLayout = (RelativeLayout) findViewById(R.id.TopView);
		ibRet = (ImageButton) findViewById(R.id.BTN_RetBtn);

		// 获得控件
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

		// 有些业务没有凭单 ，如二代证联网核查
		if (resPreviewId > 0) {
			PreviewDialog dlg = new PreviewDialog();
			dlg.setImageId(resPreviewId);
			dlg.show(getFragmentManager(), "preview");
		}

		// 加载界面的资源皮肤配置
		initTabResource();
		titleLayout.setBackgroundResource(titleBackgroudResourceId);
		titleImageView.setImageResource(titleLogoId);
		ibRet.setImageResource(ibRetId);
		programName  = getApplicationName(this);

	}

	// 初始化一些Tab的背景与字体颜色，可以根据不同情况进行重写
	// 子类一般需要进行重载
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

	/******************* Tab标签相关 **************************/
	// tab的控件的配置
	public class TabItem {
		public String title;// 标题
		public boolean visible;// 是否为可见
		public boolean actived;// 是否处于激活状态
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
	 * 初始化tab标签属性
	 */
	protected void initTabsAttr() {
		TextView view;
		// this.tabs = tabs;

		// 逐一设置属性
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

		// 把不需要的tab隐藏
		int count = tvTabs.length - tabs.length;
		if (count > 0) {
			for (int i = tabs.length; i < tvTabs.length; i++) {
				tvTabs[i].setVisibility(View.GONE);
			}
		}

		updateTabUI();// 更新显示
	}

	// 计算显示的tab个数
	private int getTabShownCount() {
		// 逐一设置属性
		int showcnt = 0;// 显示的tab个数
		for (int i = 0; i < tabs.length && i < tvTabs.length; i++) {
			TabItem conf = tabs[i];
			if (conf.visible) {
				showcnt += 1;
			}
		}
		return showcnt;
	}

	// 返回被激活的页的索引
	private int getActivedTabIndex() {
		for (int i = 0; i < tabs.length; i++) {
			TabItem conf = tabs[i];
			if (conf.visible && conf.actived) {
				return i;
			}
		}
		return 0;// 默认返回第0个
	}

	// 是否有前一页
	private boolean hasPreTab(int index) {
		if (index >= 0 && index < tabs.length) {
			for (int i = 0; i < index; i++) {
				if (tabs[i].visible)
					return true;
			}
		}
		return false;
	}

	// 是否有下一页
	private boolean hasNextTab(int index) {
		if (index >= 0 && index < tabs.length) {
			for (int i = index + 1; i < tabs.length; i++) {
				if (tabs[i].visible)
					return true;
			}
		}
		return false;
	}

	// 根据fragment找到TabItem对象
	private int getTabItembyFragment(Fragment fragment) {
		for (int i = 0; i < tabs.length; i++) {
			if (tabs[i].fragment.equals(fragment)) {
				return i;
			}
		}
		return -1;
	}

	// 查询是否有前一页
	public boolean hasPreTab(Fragment fragment) {
		int i = getTabItembyFragment(fragment);
		if (i >= 0)
			return hasPreTab(i);
		else
			return false;
	}

	// 查询是否有后一页
	public boolean hasNextTab(Fragment fragment) {
		int i = getTabItembyFragment(fragment);
		if (i >= 0)
			return hasNextTab(i);
		else
			return false;
	}

	// 是否是可见的tab
	public boolean isVisibledTab(Fragment fragment) {
		int i = getTabItembyFragment(fragment);
		if (i >= 0)
			return tabs[i].visible;
		else
			return false;
	}

	// 更新tabUI
	private void updateTabUI() {
		TextView[] tvs = new TextView[MAX_TABCOUNT]; // 存放tab标题控件集合
		TabItem[] tabItems = new TabItem[MAX_TABCOUNT]; // 存放TabItem集合
		int showcnt = getTabShownCount();// 获得显示的tab个数
		int i, j;

		// 遍历得到可视的视图
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

		// 遍历得到不可视的视图
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

		// 先隐藏所有，再根据个数逐一显示.
		// tv1总是要显示的
		for (i = 1; i < tvs.length; i++) {
			if (tvs[i] != null)
				tvs[i].setVisibility(View.GONE);
		}

		// 根据显示的tab个数设置背景和文本颜色
		TextView tab;
		if (showcnt == 1) {// 只有一个tab时特殊处理
			tab = tvs[0];
			tab.setBackgroundResource(R.drawable.title);
			tab.setTextColor(activedColor);
			tab.setVisibility(View.VISIBLE);
		} else {// 含有多个
			for (i = 0; i < showcnt; i++) {
				tab = tvs[i];
				boolean actived = tabItems[i].actived;

				// 设置为显示，根据选中状态设置文本颜色
				tab.setVisibility(View.VISIBLE);
				if (actived) {
					tab.setTextColor(activedColor);
				} else {
					tab.setTextColor(normalColor);
				}

				if (i == 0) {// 最左边的tab
					if (actived)
						tab.setBackgroundResource(drawableId_tabLeftActived);
					else
						tab.setBackgroundResource(drawableId_tabLeftNormal);
				} else if (i == showcnt - 1) {// 最右边的tab
					if (actived)
						tab.setBackgroundResource(drawableId_tabRightActived);
					else
						tab.setBackgroundResource(drawableId_tabRightNormal);
				} else {// 中间
					if (actived)
						tab.setBackgroundResource(drawableId_tabCenterActived);
					else
						tab.setBackgroundResource(drawableId_tabCenterNormal);
				}
			}// end of for
		}// end of else
	}

	// 切到第n页
	private void switchToTab(int from, int to) {
		if (tabs != null && tabs.length > to) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.TABCONTENT, tabs[to].fragment);
			ft.commit();

			// 切换状态，更新状态显示
			tabs[from].actived = false;
			tabs[to].actived = true;
			updateTabUI();

		}
	}

	// 显示或隐藏对应的页
	public void enableTab(String title, boolean isenable) {
		for (int i = 0; i < tabs.length; i++) {
			if (tabs[i].title.equals(title)) {
				tabs[i].visible = isenable;
				updateTabUI();// 更新顶部的tab标签
				updateActivedPageButtonStatus();// 更新导航按钮状态
				break;
			}
		}
	}

	private void updateActivedPageButtonStatus() {
		// 决定哪些按钮需要显示或隐藏
		Button btnPre = (Button) findViewById(R.id.BTN_PRE);
		Button btnNext = (Button) findViewById(R.id.BTN_NEXT);
		Button btnComplete = (Button) findViewById(R.id.BTN_COMPLETE);
		int n = getActivedTabIndex();

		// 更新上一页
		if (btnPre != null) {
			if (hasPreTab(n)) {
				btnPre.setVisibility(View.VISIBLE);
			} else {
				btnPre.setVisibility(View.GONE);
			}
		}

		// 更新下一页和完成按钮
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

	/************ 页切换listener ***************/
	@Override
	public void onNextStep() {
		int n = getActivedTabIndex();// 激活的页
		if (tabs.length > n + 1) {// 存在下一页
			for (int i = n + 1; i < tabs.length; i++) {
				TabItem conf = tabs[i];
				if (conf.visible) {// 进行页的切换
					switchToTab(n, i);
					break;
				}
			}
		}
	}

	@Override
	public void onPreviewStep() {
		int n = getActivedTabIndex();// 激活的页
		if (n - 1 >= 0) {// 正常切页
			for (int i = n - 1; i >= 0; i--) {
				TabItem conf = tabs[i];
				if (conf.visible) {// 进行页的切换
					switchToTab(n, i);
					break;
				}
			}
		} else {// 第一页往前切，表示要退出
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

	// 显示激活的页,一般在无preview页面时调用
	public void showActivedTab() {
		switchToTab(0, getActivedTabIndex());
	}

	/***********************代理人与交易人必须不同********************************/
	String uniqueMark = "";//唯一的标签
	public void setUniqueMark( String mark ){
		this.uniqueMark = mark;
	}
	public String getUniqueMark(){
		return uniqueMark;
	}

	// 设置打印标题
	private String printtitle = "";

	protected void setPrintTitle(String title) {
		this.printtitle = title;
	}

	// 显示错误提示信息
	public void showTip(String info) {
		Toast.makeText(this, info, Toast.LENGTH_LONG).show();
	}

	/******************* 预览示例图片相关 **************************/
	// 当预览结束时显示界面
	@Override
	public void onPreviewComplete() {
		// 显示默认的fragment
		switchToTab(0, getActivedTabIndex());
	}

	/*********************** 代理人与交易人必须不同 ********************************/
	// TODO:可以删除这个功能，改用共享信息的方法
	// String uniqueMark = "";//唯一的标签
	// public void setUniqueMark( String mark ){
	// this.uniqueMark = mark;
	// }
	// public String getUniqueMark(){
	// return uniqueMark;
	// }

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

	/*********** 回退操作 *******************/
	@Override
	public void onBackPressed() {
		// 回到前一页，最后一次退出
		onPreviewStep();
	}

	// 当左上角退出按钮按下时
	public void onRetClick(View view) {
		onPreviewStep();//只返回上一页，不要退出
	}

	/****************** 倒计时 ***************/
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


	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		//解决实体按键按下时，number类型的edittext控件会产生两个字符的问题
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

	//设置交易码。当前应用传输的是46xxx#yy格式，即交易码#标记，服务端当前不认此种格式
	protected void setTranCode( String code ){
		this.trancode = code;
	}
	/*
	 * 启动打印机相关的Activity,传输相应的数据
	 */
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
