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
 * ������������
 *
 */
public class UpdateActivity extends Activity {
	
	private SystemUpdate update;//��������
	private Handler handler = new Handler();//����UI�߳�ͬ��
	private ImageView imgLoading;			//���ڼ�����ʾ�ؼ�
	private TextView tvQuery;				//��ѯ��ʾ�ؼ�
	private TextView tvResult;				//��ѯ����ؼ�
	private String localUpdateFile;			//���������ļ�
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView( R.layout.activity_systemupdate);
		super.onCreate(savedInstanceState);
		
		imgLoading = (ImageView)findViewById( R.id.IMG_LOADING );
		tvQuery = (TextView)findViewById( R.id.TIP_CHECKVERSION );
		tvResult = (TextView)findViewById( R.id.TIP_RESULT );
		
		if( UpdateProgressDialog.isHasRunning() )//����Ѿ������У��Ͳ�Ҫ��������
			finish();
		
		AutofillSettings settings = new AutofillSettings( this );
		update = new SystemUpdate( settings.getServerIp());
		
		//����Դ���������.xdat�ļ�����ϵͳ����ʱ
		Intent intent = getIntent();
		String action = intent.getAction();
		if( action != null && Intent.ACTION_SEND.equals( action )){
			Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
			localUpdateFile = uri.getPath();//����ļ�·����
			tryLocalUpdate();
			tvQuery.setText( "����У��ϵͳ���°���׼����������ģʽ..." );
		}
//		else{//Զ�̲�ѯ����		
//			//��������
//			ConfigUtil conf = ConfigUtil.getInstance( this );
//			update = new SystemUpdate(conf.getServerIP());
//			startQuery();//������ѯ
//		}
	}
	
	//��ѯ�汾��Ϣ
	private void startQuery(){
		Thread thread = new Thread( new Runnable(){
			public void run(){
				update.query();
				handler.post( updateQueryResUI );
			}
		});
		thread.start();
	}
	
	//���²�ѯ���UI
	private Runnable updateQueryResUI = new Runnable(){
		public void run(){
			imgLoading.setVisibility( View.GONE );
			tvQuery.setVisibility( View.GONE );
				
			
			//��ѯ����Ҫ���£��򵯳�������������
			VersionInfo info = update.getUpdateInfo();
			if( info.alive && info.isValid() ){
				String tip = "���ڸ��°汾:" + info.version + "\n����˵����\n" + info.detail.replace(';', '\n');
				tvResult.setText( tip );
				tvResult.setVisibility(View.VISIBLE);
				
				//���������Ի���
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
	
	//���Դӱ����ļ���������
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
