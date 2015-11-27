package com.centerm.autofill.dev.card;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.centerm.autofill.dev.R;
import com.centerm.autofill.dev.test.MainActivity;
//选择读ic卡还是刷磁条卡的dialog
public class CardSelectDialog extends Dialog implements View.OnClickListener{
	private TextView selectCard;
	private TextView selectICCard;
	
	
	private Activity activity;
	public CardSelectDialog(Activity activity)
	{
		super(activity,R.style.previewStyle);

		this.activity  = activity;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		View view = getLayoutInflater().inflate(R.layout.activity_select_card, null);
		selectCard = (TextView)view.findViewById(R.id.LABEL_ReadCard);
		selectICCard = (TextView)view.findViewById(R.id.LABEL_ReadICCard);
		selectCard.setOnClickListener(this);
		selectICCard.setOnClickListener(this);
		setContentView(view);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
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
				
				//finish();REQ_SELECT_CARD
				activity.startActivityForResult(intent, MainActivity.REQ_READMSGCARD );
				this.dismiss();
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
				activity.startActivityForResult(intent,MainActivity.REQ_READICCARD);
			//	activity.startActivity(intent);
			    this.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
	
}
