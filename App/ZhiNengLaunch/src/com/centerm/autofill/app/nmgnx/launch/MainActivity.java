package com.centerm.autofill.app.nmgnx.launch;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.centerm.autofill.app.nmgnx.launch.R;
import com.centerm.autofill.launch.AppListActivity;
import com.centerm.autofill.launch.TypeListAcitivity;
//com.example.appnmgnxlaunch.MainActivity
public class MainActivity extends TypeListAcitivity {

	static {
		AppListActivity.APPTYPE_PERSONAL = "com.centerm.autofill.app.personal.nmgnx";
		AppListActivity.APPTYPE_PUBLICS = "com.centerm.autofill.app.publics.nmgnx";
		AppListActivity.APPTYPE_FINANCIAL= "com.centerm.autofill.app.financial.nmgnx";
		NextCls	= NmgnxAppListActivity.class;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ImageView titleImageView 				= (ImageView)findViewById(R.id.IMG_LOGO);
		RelativeLayout headLayout 				=	(RelativeLayout)findViewById(R.id.BLOCK_HEADER);
		FrameLayout autoFillLaunchLayout		=	(FrameLayout)findViewById(R.id.autoFillLaunchLayout);
		ImageView		mgPersion				=	(ImageView)findViewById(R.id.AutoFillLaunchPersional_ImageView);
		ImageView		mgFinancial				=	(ImageView)findViewById(R.id.AutoFillLaunchFinancial_ImageView);
		ImageView		mgPublic				=	(ImageView)findViewById(R.id.AutoFillLaunchPublic_ImageView);
		
		mgFinancial.setImageResource(R.drawable.financial);
		mgPersion.setImageResource(R.drawable.persional);
		mgPublic.setImageResource(R.drawable.publics);
//		titleImageView.setImageResource(R.drawable.title_logo);
		headLayout.setBackgroundResource(R.drawable.banner);
		autoFillLaunchLayout.setBackgroundResource(R.drawable.background);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		
		//不允许通过退格键退出
		int code = event.getKeyCode();
		if( code >= KeyEvent.KEYCODE_BACK )
			return true;
		return super.dispatchKeyEvent(event);
	}
	
	

}
