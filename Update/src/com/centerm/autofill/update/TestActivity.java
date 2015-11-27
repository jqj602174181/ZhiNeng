package com.centerm.autofill.update;

import com.centerm.autofill.update.newupdate.NewUpdateActivity;
import com.centerm.autofill.update.updateapp.SocketCorrespondence;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

//com.centerm.autofill.update.TestActivity
public class TestActivity extends Activity implements View.OnClickListener{

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		View button = findViewById(R.id.show);
		button.setOnClickListener(this);
		button = findViewById(R.id.app);
		button.setOnClickListener(this);
		button = findViewById(R.id.fire);
		button.setOnClickListener(this);
		button = findViewById(R.id.prineter);
		button.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.show:
			startFirmwaresUpdateActivity(TestActivity.this,SocketCorrespondence.UPDATE_ALL);
			break;
		case R.id.app:
			startFirmwaresUpdateActivity(  TestActivity.this,SocketCorrespondence.UPDATE_APP );
//			Intent intent = new Intent(UpdateReceiver.updateAppAction);
//			sendBroadcast(intent);//发送升级app的广播
			break;
		case R.id.prineter:
			startFirmwaresUpdateActivity(TestActivity.this,SocketCorrespondence.UPDATE_PRINTER);
			break;
		case R.id.fire:
			startFirmwaresUpdateActivity(TestActivity.this,SocketCorrespondence.UPDATE_FINANCIAL);
			break;
		default:
			break;
		}
	}
	
	public static void startFirmwaresUpdateActivity(Context context,String data)
	{
		Intent intent = new Intent();
		intent.putExtra("json", data);
		intent.setClass(context, NewUpdateActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	public static void startFirmwaresUpdateActivity(Context context,int style)
	{
		Intent intent = new Intent();
		intent.putExtra("style", style);
		intent.setClass(context, NewUpdateActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
