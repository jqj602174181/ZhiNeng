package com.centerm.autofill.setting.manage;

import java.io.File;

import com.centerm.autofill.setting.AutofillSettings;
import com.centerm.autofill.setting.R;
import com.centerm.autofill.setting.SystemUpdate;
import com.centerm.autofill.setting.SystemUpdate.VersionInfo;
//import com.centerm.utils.ConfigUtil;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RecoverySystem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 单独升级界面
 *
 */
public class UpdateActivity extends Activity {
	
	private SystemUpdate update;//升级操作
	private Handler handler = new Handler();//用于UI线程同步
	private ImageView imgLoading;			//正在加载提示控件
	private TextView tvQuery;				//查询提示控件
	private TextView tvResult;				//查询结果控件
	private String localUpdateFile;			//本地升级文件
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView( R.layout.activity_systemupdate);
		super.onCreate(savedInstanceState);
		
		imgLoading = (ImageView)findViewById( R.id.IMG_LOADING );
		tvQuery = (TextView)findViewById( R.id.TIP_CHECKVERSION );
		tvResult = (TextView)findViewById( R.id.TIP_RESULT );
		
		if( UpdateProgressDialog.isHasRunning() )//如果已经在运行，就不要再运行了
			finish();
		
		AutofillSettings settings = new AutofillSettings( this );
		update = new SystemUpdate( settings.getServerIp());
		
		//在资源管理器点击.xdat文件进行系统升级时
		Intent intent = getIntent();
		String action = intent.getAction();
		if( action != null && Intent.ACTION_SEND.equals( action )){
			Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
			localUpdateFile = uri.getPath();//获得文件路径名
			tryLocalUpdate();
			tvQuery.setText( "正在校验系统更新包，准备进入升级模式..." );
		}
//		else{//远程查询升级		
//			//创建配置
//			ConfigUtil conf = ConfigUtil.getInstance( this );
//			update = new SystemUpdate(conf.getServerIP());
//			startQuery();//继续查询
//		}
	}
	
	//查询版本信息
	private void startQuery(){
		Thread thread = new Thread( new Runnable(){
			public void run(){
				update.query();
				handler.post( updateQueryResUI );
			}
		});
		thread.start();
	}
	
	//更新查询结果UI
	private Runnable updateQueryResUI = new Runnable(){
		public void run(){
			imgLoading.setVisibility( View.GONE );
			tvQuery.setVisibility( View.GONE );
				
			
			//查询到需要更新，则弹出下载升级窗口
			VersionInfo info = update.getUpdateInfo();
			if( info.alive && info.isValid() ){
				String tip = "正在更新版本:" + info.version + "\n更新说明：\n" + info.detail.replace(';', '\n');
				tvResult.setText( tip );
				tvResult.setVisibility(View.VISIBLE);
				
				//开启升级对话框
				UpdateProgressDialog dlg = new UpdateProgressDialog( UpdateActivity.this, update );
				dlg.setOnDismissListener( new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface arg0) {
						finish();
					}
				});
				dlg.show();
			}else{
				finish();
			}
		}
			
	};
	
	//尝试从本地文件进行升级
	private void tryLocalUpdate(){
		Thread thread = new Thread( new Runnable(){
			public void run(){
				SystemUpdate.updateFromFile( UpdateActivity.this, localUpdateFile );
				handler.post( updateQueryResUI );
			}
		});
		thread.start();
	}	
	
}
