package com.centerm.autofill.dev.card;

import com.centerm.autofill.dev.R;
import com.centerm.autofill.dev.magcard.ReadMagCardActivity;
import com.centerm.autofill.dev.test.MainActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
//com.centerm.autofill.dev.card.CardSelectActivity
import android.widget.TextView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
public class CardSelectActivity extends Activity implements View.OnClickListener{
	private TextView selectCard;
	private TextView selectICCard;
	protected void  onCreate(Bundle savedInstanceState)
	{
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_card);
		selectCard = (TextView)findViewById(R.id.LABEL_ReadCard);
		selectICCard = (TextView)findViewById(R.id.LABEL_ReadICCard);
		selectCard.setOnClickListener(this);
		selectICCard.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		View selectView = findViewById( R.id.BLOCK_SELECT );
		int id = v.getId();
		switch (id) {
		case R.id.LABEL_ReadCard:
			final String name = "com.centerm.autofill.dev.magcard.ReadMagCardActivity";
			final String pkg = "com.centerm.autofill.dev";			
			try {
				// 启动重启的应用程序
				ComponentName appComponent = new ComponentName(pkg, name);
				Intent intent = new Intent();				
				intent.setComponent(appComponent);
				intent.putExtra( "title", "请刷卡" );

				Intent intent2 = new Intent();
				intent2.setClass(CardSelectActivity.this, ReadMagCardActivity.class);
				startActivityForResult(intent2, MainActivity.REQ_READMSGCARD);
				
				selectView.setVisibility( View.INVISIBLE );//隐藏显示，避免读卡时还能看到底层选择卡界面
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.LABEL_ReadICCard:
			final String name1 = "com.centerm.autofill.dev.iccard.ReadICCardActivity";
			final String pkg1 = "com.centerm.autofill.dev";			
			try {
				// 启动重启的应用程序
				ComponentName appComponent = new ComponentName(pkg1, name1);
				Intent intent = new Intent();				
				intent.setComponent(appComponent);
				intent.putExtra( "title", "请插IC卡" );
				startActivityForResult(intent,MainActivity.REQ_READICCARD);
				selectView.setVisibility( View.INVISIBLE );
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		setResult(resultCode, data);
		finish();
	}
	
	
}
